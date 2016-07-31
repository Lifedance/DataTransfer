package com.xtonic.context;

import java.util.HashMap;
import java.util.Map;

import com.xtonic.config.Config;
import com.xtonic.dataSource.TransferDataSource;
import com.xtonic.task.TransferTask;
import com.xtonic.type.TypeChangeHander;

public abstract class AbstarctTransferContext implements TransferContext{
	protected Config config;
	protected final Map<String, TransferTask<?>> transferTasks = new HashMap<String, TransferTask<?>>();
	protected final Map<String, TransferDataSource> dataSources = new HashMap<String, TransferDataSource>();
	protected final Map<String,TypeChangeHander<?>> typeHandlers = new HashMap<String, TypeChangeHander<?>>();
}
