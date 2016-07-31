package com.xtonic.dataSource;

import java.sql.Connection;

public interface TransferDataSource {
	Connection getConnection();
	
	String getDBType();
}
