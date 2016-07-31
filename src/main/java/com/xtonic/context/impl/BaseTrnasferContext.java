package com.xtonic.context.impl;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.Config;
import com.xtonic.context.AbstractRegistFactory;
import com.xtonic.dataSource.TransferDataSource;
import com.xtonic.resources.Resource;
import com.xtonic.task.TransferTask;
import com.xtonic.type.TypeChangeHander;

public  class BaseTrnasferContext extends AbstractRegistFactory {
	private static final Log LOGGER = LogFactory.getLog(BaseTrnasferContext.class);
	public BaseTrnasferContext(){};
	
	public BaseTrnasferContext(Resource resource) throws IOException{
		LOGGER.info("开始加载解析XML文件:" + resource.getFile().getName());
		this.loadDefinition.loadXMLDefiniton(resource);
	}
	
	public Object getProperty(String Key) {
		return config.getParameter(Key);
	}

	public TransferTask<?> getTransferTask(String id) {
		return transferTasks.get(id);
	}

	public TransferDataSource getDataSources(String id) {
		return dataSources.get(id);
	}

	public Map<String, TransferTask<?>> getTransferTasks() {
		return transferTasks;
	}

	public void registConfig(Config config) {
		this.config = config;
	}

	public TypeChangeHander<?> getTypeHandler(String id) {
		return typeHandlers.get(id);
	}

	public void setParameter(String key, Object value) {
		config.setParameter(key, value);
	}
}