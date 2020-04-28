package com.sherlockkk.sink.controller;

import com.sherlockkk.sink.service.OpcUaOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/ua-client")
public class TestController {
	@Autowired
	private OpcUaOperationService opcUaOperationService;

	@RequestMapping("/browse")
	public void browseNodes() throws ExecutionException, InterruptedException {
		opcUaOperationService.browseNodes();
	}

	@RequestMapping("/read")
	public void readNode() {
		opcUaOperationService.readValue();
	}

	@RequestMapping("/subscribe")
	public void subscribeNode(@RequestParam int namespace, @RequestParam String identifier) {
		try {
			opcUaOperationService.subscription(namespace, identifier);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/listSubscriptionId")
	public void listSubscriptionId() {
		opcUaOperationService.listSubscriptionId();
	}

	@RequestMapping("/unSubscription")
	public void unSubscription(@RequestParam int subscriptionId) {
		try {
			opcUaOperationService.unSubscription(subscriptionId);
		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
