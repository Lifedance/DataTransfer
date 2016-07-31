package com.xtonic.config;

public class SrcData {
	private String querySql;
	private String srcDataSourceRef;
	private String keyColumn;
	private Boolean isMainSrc;

	public String getQuerySql() {
		return querySql;
	}
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
	public String getSrcDataSourceRef() {
		return srcDataSourceRef;
	}
	public void setSrcDataSourceRef(String srcDataSourceRef) {
		this.srcDataSourceRef = srcDataSourceRef;
	}
	public String getKeyColumn() {
		return keyColumn;
	}
	public void setKeyColumn(String keyColumn) {
		this.keyColumn = keyColumn;
	}
	public Boolean getIsMainSrc() {
		return isMainSrc;
	}
	public void setIsMainSrc(Boolean isMainSrc) {
		this.isMainSrc = isMainSrc;
	}
	@Override
	public String toString() {
		return "SrcData [querySql=" + querySql + ", srcDataSourceRef=" + srcDataSourceRef + ", keyColumn=" + keyColumn
				+ ", isMainSrc=" + isMainSrc + "]";
	}
	
}
