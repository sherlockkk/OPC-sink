package com.sherlockkk.sink.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttProperties {

	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * MQTT  TCP URL,多个URL用逗号分隔
	 */
	private String endpointUrl;
	/**
	 * 连接超时时长,单位为秒，默认为30
	 */
	private int connectionTimeout;
	/**
	 * 会话心跳时间，单位为秒，默认为60
	 */
	private int keepAliveInterval;
}


