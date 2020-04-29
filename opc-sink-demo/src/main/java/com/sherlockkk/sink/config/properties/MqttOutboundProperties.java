package com.sherlockkk.sink.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.mqtt.outbound")
public class MqttOutboundProperties {
	/**
	 * 订阅消息的客户端 ID
	 */
	private String clientId;
	/**
	 * 是否开启异步发送
	 */
	private boolean async;
	/**
	 * 默认的 Topic
	 */
	private String defaultTopic;
	/**
	 * 默认的 Qos
	 */
	private int defaultQos;
}
