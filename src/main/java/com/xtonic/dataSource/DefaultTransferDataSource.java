package com.xtonic.dataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.xtonic.config.DataSourceDeifiniton;

public class DefaultTransferDataSource extends AbstractTransferDataSource {

	public DefaultTransferDataSource(DataSourceDeifiniton dataSourceDefinition) {
		super(dataSourceDefinition);
	}

	public Connection getConnection() {
		try {
			return DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			throw new RuntimeException("获取SQL链接是失败，"
					+ "URL:" + url + "; username: " + username + ";password: " + password,e);
		}
	}
}
