package com.sherlockkk.sink.controller;

import com.sherlockkk.sink.service.IMqttPublishGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/mqtt")
public class MqttController {
	@Autowired
	private IMqttPublishGateway mqttPublishGateway;

	@GetMapping("/send")
	public void sendMqtt(@RequestParam Map<String, Object> map) {
		String message = (String) map.get("message");
		mqttPublishGateway.sendToMqtt(message);
	}
}
