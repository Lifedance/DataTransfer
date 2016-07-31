package com.xtonic.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.DataSourceDeifiniton;
import com.xtonic.config.TransferTableDefiniton;
import com.xtonic.config.TypeHandlerDefinition;
import com.xtonic.dataSource.DefaultTransferDataSource;
import com.xtonic.task.TransferTaskFactory;
import com.xtonic.type.TypeChangeHander;
import com.xtonic.xmlDefinition.DefaultLoadDefinition;
import com.xtonic.xmlDefinition.LoadDefinition;

public abstract class AbstractRegistFactory extends AbstarctTransferContext implements RegisterFactory {
	private static final Log LOGGER = LogFactory.getLog(AbstractRegistFactory.class);

	protected LoadDefinition loadDefinition = new DefaultLoadDefinition(this);

	public void registerTypeHandler(TypeHandlerDefinition definition) {
		Class clazz = null;
		try {
			clazz = Class.forName(definition.getHandlerClass());
		} catch (ClassNotFoundException e) {
			LOGGER.error("没有找到ID：" + definition.getHandlerid() + "的类型转换器的实现类；", e);
			throw new RuntimeException("没有找到ID：" + definition.getHandlerid() + "的类型转换器的实现类；", e);
		}
		TypeChangeHander<?> hander = null;
		try {
			hander = (TypeChangeHander<?>) clazz.newInstance();
		} catch (Exception e) {
			LOGGER.error("ID：" + definition.getHandlerid() + "的类型转换器的实现类实例化失败", e);
			throw new RuntimeException("ID：" + definition.getHandlerid() + "的类型转换器的实现类实例化失败", e);
		}
		typeHandlers.put(definition.getHandlerid(), hander);
		LOGGER.info("类型处理器 ID：" + definition.getHandlerid() + "完成在上下文中的注册");
	}

	public void registTableDefinition(TransferTableDefiniton tableDefinition) {
		String task_key = tableDefinition.getBeanClass() + "_" + tableDefinition.getId();
		transferTasks.put(task_key, TransferTaskFactory.creatTask(this, tableDefinition));
		LOGGER.info("任务 ID：" + tableDefinition.getId() + "的任务初始化完成并在上下文中的注册");
	}
	
	public void registDataSources(DataSourceDeifiniton datasourceDefinitions) {
		dataSources.put(datasourceDefinitions.getId(), new DefaultTransferDataSource(datasourceDefinitions));
		LOGGER.info("完成DataSource ID：" + datasourceDefinitions.getId() + " 的数据源 ID在上下文的注册");
	}

}
