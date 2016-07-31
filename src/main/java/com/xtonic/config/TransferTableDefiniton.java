package com.xtonic.config;

import java.util.List;

public class TransferTableDefiniton {
	private String beanClass;
	private String table;
	private String targetDataSourceRef;
	private String id;
	private String taskImplClass;
	private List<SrcData> srcs;
	private List<TableChanges> changes;
	public String getBeanClass() {
		return beanClass;
	}
	public void setBeanClass(String beanClass) {
		this.beanClass = beanClass;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getTargetDataSourceRef() {
		return targetDataSourceRef;
	}
	public void setTargetDataSourceRef(String targetDataSourceRef) {
		this.targetDataSourceRef = targetDataSourceRef;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<SrcData> getSrcs() {
		return srcs;
	}
	public void setSrcs(List<SrcData> srcs) {
		this.srcs = srcs;
	}
	public List<TableChanges> getChanges() {
		return changes;
	}
	public void setChanges(List<TableChanges> changes) {
		this.changes = changes;
	}
	public String getTaskImplClass() {
		return taskImplClass;
	}
	public void setTaskImplClass(String taskImplClass) {
		this.taskImplClass = taskImplClass;
	}
	@Override
	public String toString() {
		return "TransferTableDefiniton [beanClass=" + beanClass + ", table=" + table + ", targetDataSourceRef="
				+ targetDataSourceRef + ", id=" + id + ", srcs=" + srcs + ", changes=" + changes + "]";
	}
	
}
