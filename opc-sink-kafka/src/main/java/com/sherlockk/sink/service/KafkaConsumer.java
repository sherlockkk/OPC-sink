package com.sherlockk.sink.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

	@KafkaListener(topics = "kafka-test-topic", groupId = "test-group")
	public void consumer(String message) {
		log.info("consumer message:{}", message);
	}
}
