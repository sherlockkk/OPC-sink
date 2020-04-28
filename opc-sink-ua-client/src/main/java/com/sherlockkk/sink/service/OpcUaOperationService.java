package com.sherlockkk.sink.service;

import java.util.concurrent.ExecutionException;

public interface OpcUaOperationService {
	void browseNodes();

	void readValue();

	void subscription(int namespace, String identifier) throws ExecutionException, InterruptedException;

	void listSubscriptionId();

	void unSubscription(int subscriptionId) throws ExecutionException, InterruptedException;
}
