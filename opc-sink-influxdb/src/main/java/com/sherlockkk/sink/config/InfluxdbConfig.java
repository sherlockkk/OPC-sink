// package com.sherlockkk.sink.config;
//
// import lombok.extern.slf4j.Slf4j;
// import org.influxdb.BatchOptions;
// import org.influxdb.InfluxDB;
// import org.influxdb.InfluxDBFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.influx.InfluxDbProperties;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
//
// @Slf4j
// @Configuration
// public class InfluxdbConfig {
// 	@Autowired
// 	private InfluxDbProperties influxDbProperties;
//
// 	@Bean
// 	public InfluxDB influxdb() {
// 		InfluxDB influxdb = InfluxDBFactory.connect(influxDbProperties.getUrl(), influxDbProperties.getUser(), influxDbProperties.getPassword());
// 		BatchOptions batchOptions = BatchOptions.DEFAULTS
// 				.bufferLimit(5000)
// 				.actions(100)
// 				.flushDuration(1000)
// 				.jitterDuration(20)
// 				.exceptionHandler((batch, exception) -> log.error("InfluxDB 插入失败:{}", exception.getMessage()));
// 		// 开启批量插入
// 		influxdb.enableBatch(batchOptions);
// 		return influxdb;
// 	}
// }
