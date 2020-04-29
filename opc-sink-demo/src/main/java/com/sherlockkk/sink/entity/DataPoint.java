package com.sherlockkk.sink.entity;

import lombok.Data;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Data
@Measurement(name = "sink-demo", database = "test")
public class DataPoint {
	@Column(name = "browseName")
	private String browseName;
	@Column(name = "namespaceIndex")
	private Integer namespaceIndex;
	@Column(name = "identifier")
	private Object identifier;
	@Column(name = "dataType")
	private String dataType;
	@Column(name = "value")
	private Object value;
	@Column(name = "sourceTime")
	private long sourceTime;
	@Column(name = "serverTime")
	private long serverTime;
}
