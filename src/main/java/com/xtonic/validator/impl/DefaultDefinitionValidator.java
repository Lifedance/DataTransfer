package com.xtonic.validator.impl;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.DataSourceDeifiniton;
import com.xtonic.config.SrcData;
import com.xtonic.config.TransferTableDefiniton;
import com.xtonic.config.TypeHandlerDefinition;
import com.xtonic.context.TransferContext;
import com.xtonic.type.TypeChangeHander;
import com.xtonic.validator.DefinitonValidator;

public class DefaultDefinitionValidator implements DefinitonValidator {
	private static final Log LOGGER = LogFactory.getLog(DefaultDefinitionValidator.class);
	private static final String QUERY_SQL_FLAG = "SELECT";
	
	private TransferContext context;
	
	public DefaultDefinitionValidator(TransferContext context ){
		this.context  = context;
	}
	
	public String tableDefinitionValidator(TransferTableDefiniton definition) {
		LOGGER.info("开始对ID为："+ definition.getId() +"的任务进行校验！");
		String errorMsg = null;
		Class tableEntityClazz = null;
		try {
			tableEntityClazz = Class.forName(definition.getBeanClass());
		} catch (ClassNotFoundException e) {
			errorMsg = "任务ID：" + definition.getId() + "的实体类未找到"+ definition.getBeanClass();
			return errorMsg;
		}
		List<SrcData> srcList = definition.getSrcs();
		HashSet<String> tmpDataSrcList = new HashSet<String>();
		String querySql = null;
		HashSet<String> columnsFromSrcSQL = new HashSet<String>();
		for (SrcData src : srcList) {
			if(context.getDataSources(src.getSrcDataSourceRef())== null){
				errorMsg = "任务ID：" + definition.getId() + "的数据源："+ src.getSrcDataSourceRef() +"未被定义";
				return errorMsg;
			}
			if (!tmpDataSrcList.add(src.getSrcDataSourceRef())) {
				errorMsg = "数据源：" + src.getSrcDataSourceRef() + " 重复，有多个数据源来自同一个库"
						+ "请将他们归并未下面格式的SQL： SELECT A.COLUMN1,A.COLUMN2,B.COLUMN1 "
						+ "FROM TABLE1 A LEFT JOIN TABLE2 B ON (A.KEY = B.KEY)";
				return errorMsg;
			}
			// 1：判断主键ID，是否设置；
			if (src.getKeyColumn() == null || src.getKeyColumn().trim().length() == 0) {
				errorMsg = "数据源：" + src.getQuerySql() + " 的数据源未设置keyColumn的值";
				return errorMsg;
			}
			querySql = src.getQuerySql();
			querySql = querySql.trim().toUpperCase();
			if (querySql == null || !querySql.startsWith(QUERY_SQL_FLAG)) {
				errorMsg = "table_id：" + definition.getId() + " 的数据源的sql不是查询SQL";
				return errorMsg;
			}
			String columnStr = querySql.substring(querySql.indexOf(QUERY_SQL_FLAG) + QUERY_SQL_FLAG.length(),
					querySql.indexOf("FROM"));
			String[] columns = columnStr.split(",");
			String tmpStr = null;
			for (int i = 0; i < columns.length; i++) {
				tmpStr = columns[i].trim();
				tmpStr = tmpStr.substring(tmpStr.indexOf(".") + 1);
				if ((tmpStr.indexOf("AS")) != -1) {
					tmpStr = tmpStr.substring(tmpStr.indexOf("AS") + "AS".length()).trim();
				}
				if (columnsFromSrcSQL.contains(tmpStr) && (tmpStr.indexOf(src.getKeyColumn().toUpperCase()) == -1)) {
					errorMsg = "table_id：" + definition.getId() + " 的数据源的有重复的字段";
					return errorMsg;
				}
				columnsFromSrcSQL.add(tmpStr);
			}

//			config.setParameter(definition.getId(), columnsFromSrcSQL.clone());
			context.setParameter(definition.getId(), columnsFromSrcSQL.clone());

			// 判断keyColumn字段是否被 包含在SELECT语句里面
			String[] keColumns = src.getKeyColumn().split(",");
			for (int j = 0; j < keColumns.length; j++) {
				if (!columnsFromSrcSQL.contains(keColumns[j].trim().toUpperCase())) {
					errorMsg = "数据源SQL语句：" + src.getQuerySql() + " 的数据源未包含keyColumn的字段";
					return errorMsg;
				}
			}

		}
		Field[] fields = tableEntityClazz.getDeclaredFields();
		for (int k = 0; k < fields.length; k++) {
			if (columnsFromSrcSQL.contains(fields[k].getName().toUpperCase())) {
				columnsFromSrcSQL.remove(fields[k].getName().toUpperCase());
			}
		}
		if (!columnsFromSrcSQL.isEmpty()) {
			errorMsg = "任务ID：" + definition.getId() + "的sql语句中的字段："+columnsFromSrcSQL.toString() + "未在实体类中定义；请检查" ;
			return errorMsg;
		}
		LOGGER.info("开始对ID为："+ definition.getId() +"的任务的校验结束！");
		return errorMsg;
	}
	
	
	public String dataSourceDefinitionValidator(DataSourceDeifiniton definition) {
		LOGGER.info("开始对ID为："+ definition.getId() +"的数据源进行校验！");
		if(context.getDataSources(definition.getId()) != null){
			return "DataSource ID：" + definition.getId() + " 的数据源 ID重复";
		}
		LOGGER.info("开始对ID为："+ definition.getId() +"的数据源校验结束！");
		return null;
	}
	
	
	public String  typeHandlerDeifnitonValidator(TypeHandlerDefinition definition) {
		LOGGER.info("开始对ID为："+ definition.getHandlerid() +"的类型转换器进行校验！");
		if (StringUtils.isEmpty(definition.getHandlerid())) {
			return "有类型转换器 的ID没有定义或为空字符串，请检查typehandlers节点的内容";
		}
		if (StringUtils.isEmpty(definition.getHandlerClass())) {
			return "有类型转换器 的实现类没有定义或为空字符串，请检查typehandlers节点的内容";
		}
		Class clazz = null;
		try {
			clazz = Class.forName(definition.getHandlerClass());
		} catch (ClassNotFoundException e) {
			return "没有找到ID：" + definition.getHandlerid() + "的类型转换器的实现类；";
		}
		
		if (!TypeChangeHander.class.isAssignableFrom(clazz)) {
			return "ID：" + definition.getHandlerid() + "的类型转换器的实现类没有显示TypeChangeHander接口";
		}
		if((context.getTypeHandler(definition.getHandlerid())!= null)){
			return "类型处理器 ID：" + definition.getHandlerid() + "重复";
		}
		LOGGER.info("开始对ID为："+ definition.getHandlerid() +"的类型转换器校验结束！");
		return null;
	}
	
	
}
