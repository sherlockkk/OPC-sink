package com.sherlockkk.sink.config;

import com.sherlockkk.sink.service.impl.KeyStoreLoader;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.client.UaStackClient;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Configuration
public class OpcUaConfiguration {


	/*
		1、根据 OPC 地址和安全策略搜索 OPC 节点
		2、构造 OpcUaClientConfig 配置类
		3、构造 OpcUaClient 客户端对象
	 */

	/**
	 * 根据 OPC 地址和安全策略搜索 OPC 节点
	 *
	 * @return EndpointDescription
	 */
	@Bean
	public EndpointDescription endpoint() {
		EndpointDescription endpoint = null;
		try {
			List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints("opc.tcp://127.0.0.1:49320").get();
			endpoint = endpoints.stream().filter(e -> SecurityPolicy.None.getUri().equals(e.getSecurityPolicyUri()))
					.findFirst().orElseThrow(() -> new UaException(StatusCodes.Bad_ConfigurationError, "未匹配到 OPC 服务器"));
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}
		return endpoint;
	}

	@Bean
	public IdentityProvider identityProvider() {
		return new UsernameProvider("opc-sink", "1234QWERasdfzx");
	}

	@Bean
	public KeyStoreLoader loader() throws Exception {
		Path securityDir = Paths.get("D:\\opc-sink", "security");
		Files.createDirectories(securityDir);
		if (!Files.exists(securityDir)) {
			throw new Exception("unable to create security dir: " + securityDir);
		}
		log.info("security dir: {}", securityDir.toAbsolutePath());
		return new KeyStoreLoader().load(securityDir);
	}

	@Bean
	public OpcUaClientConfig config(EndpointDescription endpoint, IdentityProvider identityProvider, KeyStoreLoader loader) {
		return OpcUaClientConfig.builder()
				.setApplicationName(LocalizedText.english("OPC-SINK OPC-UA CLIENT"))
				.setApplicationUri("urn:opc-sink")
				.setCertificate(loader.getClientCertificate())
				.setKeyPair(loader.getClientKeyPair())
				.setEndpoint(endpoint)
				.setIdentityProvider(identityProvider)
				.setRequestTimeout(Unsigned.uint(5000))
				.build();
	}

	@Bean
	public UaClient uaClient(OpcUaClientConfig config) throws UaException, InterruptedException, ExecutionException, TimeoutException {
		UaStackClient uaStackClient = UaStackClient.create(config);
		return new OpcUaClient(config, uaStackClient).connect().get(30, TimeUnit.SECONDS);
	}
}
