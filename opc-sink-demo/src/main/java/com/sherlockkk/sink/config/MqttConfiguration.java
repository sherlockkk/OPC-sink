package com.sherlockkk.sink.config;

import com.sherlockkk.sink.config.properties.MqttInboundProperties;
import com.sherlockkk.sink.config.properties.MqttOutboundProperties;
import com.sherlockkk.sink.config.properties.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableConfigurationProperties({MqttProperties.class, MqttInboundProperties.class, MqttOutboundProperties.class})
public class MqttConfiguration {
	public static final String WILL_TOPIC = "willTopic";
	public static final byte[] WILL_DATA = "offline".getBytes();

	/**
	 * MQTT 客户端
	 *
	 * @return {@link MqttPahoClientFactory}
	 */
	@Bean
	public MqttPahoClientFactory mqttClientFactory(MqttProperties properties) {
		// MQTT 连接器选项设置
		MqttConnectOptions options = new MqttConnectOptions();
		//设置是否清空 session
		//true 表示每次连接到服务器都以新的身份连接
		//false 表示服务器会保留客户端的连接记录
		options.setCleanSession(true);
		options.setUserName(properties.getUsername());
		options.setPassword(properties.getPassword().toCharArray());
		options.setServerURIs(properties.getEndpointUrl().split("[,]"));
		options.setConnectionTimeout(properties.getConnectionTimeout());
		//开启自动重连
		options.setAutomaticReconnect(true);
		//自动重连间隔时间,单位毫秒
		options.setMaxReconnectDelay(5000);
		options.setKeepAliveInterval(properties.getKeepAliveInterval());
		//设置“遗嘱”消息的话题，若客户端和服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
		options.setWill(WILL_TOPIC, WILL_DATA, 2, false);

		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		factory.setConnectionOptions(options);
		return factory;
	}

	/**
	 * 发送通道
	 *
	 * @return
	 */
	@Bean
	public MessageChannel mqttOutboundChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "mqttOutboundChannel")
	public MessageHandler mqttOutbound(MqttPahoClientFactory mqttClientFactory, MqttOutboundProperties properties) {
		MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(properties.getClientId(), mqttClientFactory);
		messageHandler.setAsync(properties.isAsync());
		messageHandler.setDefaultQos(properties.getDefaultQos());
		messageHandler.setDefaultTopic(properties.getDefaultTopic());
		return messageHandler;
	}


	/**
	 * 接收通道
	 *
	 * @return
	 */
	@Bean
	public MessageChannel mqttInboundChannel() {
		return new DirectChannel();
	}

	/**
	 * 配置Client， 订阅Topic
	 *
	 * @return
	 */
	@Bean
	public MessageProducer inbound(MqttPahoClientFactory mqttClientFactory, MqttInboundProperties properties) {
		// "opc-sink"
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(properties.getClientId(), mqttClientFactory, properties.getTopic());
		// adapter.setErrorChannel(new DirectChannel());
		// adapter.connectionLost(new Throwable("连接失败"));
		// adapter.setCompletionTimeout(30);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(properties.getQos());
		adapter.setOutputChannel(mqttInboundChannel());
		return adapter;
	}

}
