package com.sherlockkk.sink.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class KafkaProducer {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(String message) {
		ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send("sink-demo", message);
		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
			@Override
			public void onFailure(Throwable throwable) {
				log.info("Send message fail:{}", throwable.getMessage());
			}

			@Override
			public void onSuccess(SendResult<String, String> sendResult) {
				log.info("Send message success:{}", sendResult.toString());
			}
		});
	}
}
