package com.xtonic.config;

public class TypeHandlerDefinition {
	private String handlerid;
	private String handlerClass;
	public String getHandlerid() {
		return handlerid;
	}
	public void setHandlerid(String handlerid) {
		this.handlerid = handlerid;
	}
	public String getHandlerClass() {
		return handlerClass;
	}
	public void setHandlerClass(String handlerClass) {
		this.handlerClass = handlerClass;
	}
	@Override
	public String toString() {
		return "TypeHandlerDefinition [handlerid=" + handlerid + ", handlerClass=" + handlerClass + "]";
	}
}
