package com.sherlockkk.sink.service.impl;

import com.sherlockkk.sink.service.InfluxDBService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {
	@Autowired
	private InfluxDBService influxDBService;

	@KafkaListener(topics = "sink-demo", groupId = "test-group")
	public void consumer(String message) {
		log.info("consumer message:{}", message);
		//从 Kafka 消费数据，对数据做业务处理，再将处理后的数据保存到 InfluxDB
		influxDBService.write(message);
	}
}
