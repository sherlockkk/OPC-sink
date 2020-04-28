package com.sherlockkk.sink.service.impl;

import com.google.common.collect.Lists;
import com.sherlockkk.sink.service.OpcUaOperationService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.serialization.SerializationContext;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.*;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class OpcUaOperationServiceImpl implements OpcUaOperationService {
	@Autowired
	private UaClient uaClient;

	@Override
	public void browseNodes() {
		long startTime = System.currentTimeMillis();
		AtomicInteger count = new AtomicInteger(0);
		browseNode(Identifiers.RootFolder, count);
		// browseNode2(Identifiers.RootFolder, count);
		long endTime = System.currentTimeMillis();
		log.info("检索共耗时：{}ms,总计{}个节点", endTime - startTime, count.get());
	}

	private void browseNode2(NodeId nodeId, AtomicInteger count) {
		BrowseDescription browseDescription = new BrowseDescription(
				nodeId,
				BrowseDirection.Forward,
				Identifiers.References,
				true,
				Unsigned.uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue()),
				Unsigned.uint(BrowseResultMask.All.getValue()));

		try {
			// new NodeId(0,35)
			// uaClient.getAddressSpace().browse()

			BrowseResult browseResult = uaClient.browse(browseDescription).get();
			List<ReferenceDescription> references = Arrays.asList(browseResult.getReferences());
			for (ReferenceDescription reference : references) {
				QualifiedName qualifiedName = reference.getBrowseName();
				NodeId curNodeId = reference.getReferenceTypeId();
				log.info("Node BrowseName:{}===NodeId:ns={};s={}===Node Type:{}",
						qualifiedName.getName(),
						curNodeId.getNamespaceIndex(),
						curNodeId.getIdentifier(),
						curNodeId.getType());
				count.incrementAndGet();
				browseNode2(curNodeId, count);
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	private void browseNode(NodeId nodeId, AtomicInteger count) {
		List<Node> nodes = null;
		try {
			nodes = uaClient.getAddressSpace().browse(nodeId).get();
			for (Node node : nodes) {
				NodeId curNodeId = node.getNodeId().get();
				QualifiedName qualifiedName = node.getBrowseName().get();
				int nodeClass = node.getNodeClass().get().getValue();
				if (nodeClass == NodeClass.Variable.getValue() && !Unsigned.ushort(0).equals(curNodeId.getNamespaceIndex())) {
					log.info("Node BrowseName:{}===NodeId:ns={};s={}===Node Type:{}",
							qualifiedName.getName(),
							curNodeId.getNamespaceIndex(),
							curNodeId.getIdentifier(),
							curNodeId.getType());
					count.incrementAndGet();
				}
				browseNode(curNodeId, count);
			}
		} catch (InterruptedException | ExecutionException e) {
			//TODO 权限不足导致的检索不了也会报错，报错信息太多，暂时先屏蔽掉
			// e.printStackTrace();
		}

	}

	@Override
	public void readValue() {
		NodeId nodeId = new NodeId(2, "通道 1.设备 1.TAG2");
		VariableNode variableNode = uaClient.getAddressSpace().createVariableNode(nodeId);
		DataValue dataValue = null;
		try {
			dataValue = variableNode.readValue().get();
			Float value = (Float) dataValue.getValue().getValue();
			log.info("通道 1.设备 1.TAG2当前的值为：{}", value);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void subscription(int namespace, String identifier) throws ExecutionException, InterruptedException {
		UaSubscription uaSubscription = uaClient.getSubscriptionManager().createSubscription(1000.0).get();

		NodeId nodeId = new NodeId(namespace, identifier);
		ReadValueId readValueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);

		MonitoringParameters monitoringParameters = new MonitoringParameters(uaSubscription.nextClientHandle(), 1000.0, null, Unsigned.uint(10), true);

		MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(readValueId, MonitoringMode.Reporting, monitoringParameters);

		List<UaMonitoredItem> uaMonitoredItems = uaSubscription.createMonitoredItems(TimestampsToReturn.Both,
				Lists.newArrayList(request),
				((uaMonitoredItem, integer) -> uaMonitoredItem.setValueConsumer(this::onSubscriptionValue))).get();

		for (UaMonitoredItem uaMonitoredItem : uaMonitoredItems) {
			if (uaMonitoredItem.getStatusCode().isGood()) {
				log.info("item created for nodeId={}", uaMonitoredItem.getReadValueId().getNodeId());
			} else {
				log.info("failed to create item for nodeId={} (status={})", uaMonitoredItem.getReadValueId().getNodeId(), uaMonitoredItem.getStatusCode());
			}
		}
	}

	@Override
	public void listSubscriptionId() {
		uaClient.getSubscriptionManager().getSubscriptions().forEach(uaSubscription -> {
			uaSubscription.getMonitoredItems().forEach(uaMonitoredItem -> {
				String identifier = (String) uaMonitoredItem.getReadValueId().getNodeId().getIdentifier();
				int subscriptionId = uaSubscription.getSubscriptionId().intValue();
				log.info("已订阅节点：{}，其订阅id为：{}", identifier, subscriptionId);
			});
		});
	}

	@Override
	public void unSubscription(int subscriptionId) throws ExecutionException, InterruptedException {
		uaClient.getSubscriptionManager().deleteSubscription(Unsigned.uint(subscriptionId)).get();
	}

	private void onSubscriptionValue(SerializationContext serializationContext, UaMonitoredItem uaMonitoredItem, DataValue dataValue) {
		log.info(
				"接收到订阅数据: item={}, value={}",
				uaMonitoredItem.getReadValueId().getNodeId(), dataValue.getValue());
	}

}
