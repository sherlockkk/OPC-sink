package com.sherlockkk.sink.controller;

import com.sherlockkk.sink.service.InfluxDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	@Autowired
	private InfluxDBService influxDBService;

	@RequestMapping("/write")
	public void write() {
		influxDBService.write();
	}

	@RequestMapping("/read")
	public void read() {
		influxDBService.query();
	}
}
