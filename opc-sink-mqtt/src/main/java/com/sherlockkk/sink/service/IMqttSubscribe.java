package com.sherlockkk.sink.service;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class IMqttSubscribe {

	public static final String MQTT_RECEIVED_TOPIC = "mqtt_receivedTopic";

	@Bean
	@ServiceActivator(inputChannel = "mqttInboundChannel")
	public MessageHandler mqttInbound() {
		return message -> {
			String topic = (String) message.getHeaders().get(MQTT_RECEIVED_TOPIC);
			String payload = (String) message.getPayload();
			System.out.println(topic + ":" + payload);
		};
	}
}
