package com.sherlockk.sink.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaProducerTest {

	@Autowired
	private KafkaProducer kafkaProducer;

	@Test
	public void sendMessage() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			String message = "第" + (i + 1) + "条测试消息";
			kafkaProducer.sendMessage(message);
		}

		TimeUnit.SECONDS.sleep(5);
	}

}
