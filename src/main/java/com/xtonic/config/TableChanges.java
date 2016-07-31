package com.xtonic.config;

public class TableChanges {
	private String columnName;
	private String targetType;
	private String srcType;
	private String handlerRef;
	
	public String getHandlerRef() {
		return handlerRef;
	}
	public void setHandlerRef(String handlerRef) {
		this.handlerRef = handlerRef;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	
	public String getSrcType() {
		return srcType;
	}
	public void setSrcType(String srcType) {
		this.srcType = srcType;
	}
	
	@Override
	public String toString() {
		return "TableChanges [columnName=" + columnName + ", targetType=" + targetType + ", srcType=" + srcType
				+ ", handlerRef=" + handlerRef + "]";
	}
}
