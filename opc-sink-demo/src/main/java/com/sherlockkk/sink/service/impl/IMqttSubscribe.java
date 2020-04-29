package com.sherlockkk.sink.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

@Component
public class IMqttSubscribe {

	public static final String MQTT_RECEIVED_TOPIC = "mqtt_receivedTopic";

	@Autowired
	private KafkaProducer kafkaProducer;

	@Bean
	@ServiceActivator(inputChannel = "mqttInboundChannel")
	public MessageHandler mqttInbound() {
		return message -> {
			String topic = (String) message.getHeaders().get(MQTT_RECEIVED_TOPIC);
			assert topic != null;
			switch (topic) {
				case "sink-demo":
					String payload = (String) message.getPayload();
					//将订阅的数据发送到 Kafka
					kafkaProducer.sendMessage(payload);
					break;
				default:
					break;
			}
		};
	}
}
