package com.sherlockkk.sink.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.mqtt.inbound")
public class MqttInboundProperties {
	/**
	 * 订阅消息的客户端 ID
	 */
	private String clientId;
	/**
	 * 消息的服务质量 Quality of Service
	 * 0：最多传输一次，不保证消息到达，可能会有消息丢失，性能最好
	 * 1：至少传输一次，保证消息到达，可能会有消息重复，性能居中
	 * 2：仅传输一次，保证消息到达，且仅传输一次，性能最差
	 */
	private int qos;
	/**
	 * 订阅的 Topic，可以有多个
	 */
	private String[] topic;
}
