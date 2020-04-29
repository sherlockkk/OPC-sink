package com.sherlockkk.sink.service;

public interface InfluxDBService {
	/**
	 * 创建数据库
	 *
	 * @param database 数据库名
	 */
	void createDatabase(String database);

	void write();

	void write(String data);

	void query();

}
