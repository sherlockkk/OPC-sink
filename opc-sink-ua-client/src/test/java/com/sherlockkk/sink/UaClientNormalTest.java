package com.sherlockkk.sink;

import com.sherlockkk.sink.service.impl.KeyStoreLoader;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RunWith(JUnit4.class)
public class UaClientNormalTest {

	@Test
	public void findEndpoints() {
		try {
			List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints("opc.tcp://127.0.0.1:49320").get();
			endpoints.stream().filter(e -> SecurityPolicy.Aes128_Sha256_RsaOaep.getUri().equals(e.getSecurityPolicyUri()))
					.findFirst().orElseThrow(() -> new UaException(StatusCodes.Bad_ConfigurationError, "未匹配到符合安全策略的 OPC 服务器"));
			for (EndpointDescription endpoint : endpoints) {
				// SecurityPolicy.Basic256
				System.out.println(endpoint.getSecurityPolicyUri());
			}
		} catch (InterruptedException | ExecutionException | UaException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void createLoader() throws Exception {
		Path securityDir = Paths.get("D:\\opc-sink", "security");
		Files.createDirectories(securityDir);
		if (!Files.exists(securityDir)) {
			throw new Exception("unable to create security dir: " + securityDir);
		}
		System.out.println("security dir:" + securityDir.toAbsolutePath());

		KeyStoreLoader load = new KeyStoreLoader().load(securityDir);
		System.out.println(load.getClientCertificate().getVersion());
		System.out.println(load.getClientKeyPair().getPublic().getAlgorithm());
	}
}
