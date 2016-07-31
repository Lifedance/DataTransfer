package com.xtonic.dataSource;

import org.apache.commons.dbutils.DbUtils;

import com.xtonic.config.DataSourceDeifiniton;

public abstract class AbstractTransferDataSource implements TransferDataSource{
	protected String url;
	protected String password;
	protected String username;
	protected String driverClass;
	protected String dbType;
	
	public AbstractTransferDataSource(DataSourceDeifiniton dataSourceDefinition){
		this.url = dataSourceDefinition.getUrl();
		this.password = dataSourceDefinition.getPassword();
		this.username = dataSourceDefinition.getUsername();
		this.driverClass = dataSourceDefinition.getDriverClass();
		this.dbType = dataSourceDefinition.getDbType();
		boolean flag = DbUtils.loadDriver(this.driverClass);
		if(!flag){
			throw new RuntimeException("加载数据库驱动出错：driverClass = "+this.driverClass);
		}
	}

	public String getDBType() {
		return this.dbType;
	}
	
	
	
	
}
