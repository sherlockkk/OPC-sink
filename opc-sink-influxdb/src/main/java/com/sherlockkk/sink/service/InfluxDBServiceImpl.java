package com.sherlockkk.sink.service;

import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.influxdb.DefaultInfluxDBTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class InfluxDBServiceImpl implements InfluxDBService {
	@Autowired
	private DefaultInfluxDBTemplate influxDBTemplate;


	@Override
	public void createDatabase(String database) {

	}

	@Override
	public void write() {
		Point point = Point.measurement("test-measure")
				.addField("tag1", 8.98)
				.addField("tag2", "哈哈")
				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
				.tag("tenant", "default")
				.build();
		influxDBTemplate.write(point);
	}

	@Override
	public void query() {
		Query query = new Query("SELECT * FROM \"test-measure\" WHERE time > now() - 5m", "test");
		QueryResult queryResult = influxDBTemplate.query(query);
		log.info("查询结果：{}", queryResult);
		List<QueryResult.Result> results = queryResult.getResults();
		for (QueryResult.Result result : results) {
			for (QueryResult.Series series : result.getSeries()) {
				System.out.println(series.getName());
			}
		}
	}
}
