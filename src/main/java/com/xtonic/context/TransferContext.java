package com.xtonic.context;

import java.util.Map;

import com.xtonic.dataSource.TransferDataSource;
import com.xtonic.task.TransferTask;
import com.xtonic.type.TypeChangeHander;

public interface TransferContext   {
	
	
	
	
	/**
	 * 获取系统的一些通用配置参数；
	 * @return
	 */
	Object getProperty(String Key);
	/**
	 * 获取迁移任务
	 * @param id
	 */
	TransferTask<?> getTransferTask(String id);
	
	TransferDataSource getDataSources(String id);
	
	TypeChangeHander<?> getTypeHandler(String id);
	
	Map<String,TransferTask<?>> getTransferTasks();
	
	void setParameter(String key,Object value);

}
