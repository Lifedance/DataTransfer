package com.xtonic.task;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.TableChanges;
import com.xtonic.config.TransferTableDefiniton;
import com.xtonic.context.TransferContext;
import com.xtonic.exception.GetTotalCountException;
import com.xtonic.task.impl.DefaultTransferTask;

public class TransferTaskFactory {
	private static final Log LOGGER = LogFactory.getLog(TransferTaskFactory.class);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static TransferTask<?>  creatTask(TransferContext config,TransferTableDefiniton tableDefinition){
		LOGGER.info("开始初始化任务ID为：" +tableDefinition.getId()+"的任务" );
		TransferTaskConfig task;
		String taskImplClassName = tableDefinition.getTaskImplClass();
		if(taskImplClassName == null){
			task = new DefaultTransferTask();
		}else{
			 try {
				  Class clazz = Class.forName(taskImplClassName);
				  task = (TransferTaskConfig) clazz.newInstance();
			} catch (Exception e) {
				LOGGER.error("任务ID："+tableDefinition.getId() + "指定的自定义数据迁移任务实现类不存在！",e);
				throw new RuntimeException("任务ID："+tableDefinition.getId() + "指定的自定义数据迁移任务实现类不存在！",e);
			} 
		}
		Class tableBeanClazz = null;
		try {
			tableBeanClazz = Class.forName(tableDefinition.getBeanClass());
		} catch (ClassNotFoundException e) {
			LOGGER.error("初始化Context出错：表名：" + tableDefinition.getTable() + " 对应的实体类没有定义", e);
			throw new RuntimeException("初始化Context出错：表名：" + tableDefinition.getTable() + " 对应的实体类没有定义", e);
		}
		
		List<String> fieldNames = getFieldNameList(tableBeanClazz);
		Map<String, TableChanges> changes = getChangesMap(tableDefinition);
		Map<String, String> columnToFieldName = getColumnFieldMap(config, tableDefinition, fieldNames);
		
		task.setColumnToFieldName(columnToFieldName);
		task.setBeanClass(tableBeanClazz);
		task.setId(tableDefinition.getId());  
		task.setTargetTableName(tableDefinition.getTable());
		task.setTargetTableDataSourceRef(tableDefinition.getTargetDataSourceRef());
		task.setChanges(changes);
		task.setFieldNames(fieldNames);
		task.setConfig(config);
		task.setSrcs(tableDefinition.getSrcs());
		try {
			task.setTotalDataCount(task.getCount(tableDefinition.getSrcs()));
		} catch (GetTotalCountException e) {
			LOGGER.error("ID:"+tableDefinition.getId()+"的任务初始化失败，获取数据总量失败",e);
		}
		if(task.getTotalDataCount() == 0){
			LOGGER.info("ID:"+tableDefinition.getId()+"的任务的待迁移的数据量为0，请确认是否正确；因为数据源的数据为0，所以不生成迁移任务！");
			return null;
		}
		
		Integer pageSize = Integer.valueOf(config.getProperty("pageSize")==null ? "0" : (String)config.getProperty("pageSize"));
		//如果没有配置单页容量，则默认为6000；
		if(pageSize == 0){
			task.setPageSize(6000);
		}
		task.setPageSize(pageSize);
		task.setTotalPage(getTotalPage(task.getTotalDataCount(),task.getPageSize()));
		LOGGER.debug("ID："+tableDefinition.getId() + "的任务的数据按照pageSize："+task.getPageSize() +"分为"+task.getTotalPage()+"页");
	
		task.setInsertSQL(task.getInserSQL(tableDefinition.getTable(),fieldNames));
		LOGGER.info("ID：" + tableDefinition.getId()+"任务，执行插入SQL为："+task.getInsertSQL());
		
		
		
		return task;
	}

	private static List<String> getFieldNameList(Class tableBeanClazz) {
		List<String> fieldNames = new ArrayList<String>();
		Field[] fields = tableBeanClazz.getDeclaredFields(); 
		for(Field field : fields){
			fieldNames.add(field.getName());
		}
		return fieldNames;
	}
	/**
	 * 返回一个类型转换器的MAP；
	 * 
	 * KEY： 需要进行类型转换的属性名； VALUE： 类型转换配置信息
	 * 
	 */
	private static Map<String, TableChanges> getChangesMap(TransferTableDefiniton tableDefinition) {
		Map<String,TableChanges> changes = new Hashtable<String, TableChanges>();
		for(TableChanges change : tableDefinition.getChanges()){
			changes.put(change.getColumnName(), change);
		}
		LOGGER.debug(tableDefinition.getId()+"任务的类型转化器："+ changes.toString());
		return changes;
	}
	
    /**
     * 返回一个 SQL语句中的字段与实体类的属性的对应的关系的MAP
     * 
     * KEY： sql语句中的字段名， VALUE： 实体类中的属性名
     */
	private static Map<String, String> getColumnFieldMap(TransferContext config, TransferTableDefiniton tableDefinition,
			List<String> fieldNames) {
		Map<String,String> columnToFieldName = new HashMap<String, String>();
		HashSet<String> columnFromSql = (HashSet<String>)config.getProperty(tableDefinition.getId());
		Iterator<String> it = columnFromSql.iterator();
		String columnName = null;
		while(it.hasNext()){
			columnName = it.next();
			for(String fieldName : fieldNames){
				if(fieldName.equalsIgnoreCase(columnName)){
					columnToFieldName.put(columnName, fieldName);
					break;
				}
			}
		}
		LOGGER.debug(tableDefinition.getId()+"任务的 SQL语句中的字段与实体类的属性的对应的关系："+ columnToFieldName.toString());
		return columnToFieldName;
	}
	
	private static int getTotalPage(int total,int pageSize) {
		//Integer pageSize = Integer.valueOf((String) config.getProperty("pageSize"));
		if (total % pageSize == 0) {
			return total / pageSize;
		} else {
			return total / pageSize + 1;
		}
	}
	
	
	
}	
