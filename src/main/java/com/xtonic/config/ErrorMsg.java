package com.xtonic.config;

public class ErrorMsg {
	private String tableName;
	private String errorMsg;
	
	public ErrorMsg(String tableName,String errorMsg) {
		this.tableName = tableName;
		this.errorMsg = errorMsg;
	}
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	@Override
	public String toString() {
		return "ErrorMsg [tableName=" + tableName + ", errorMsg=" + errorMsg + "]";
	}
}
