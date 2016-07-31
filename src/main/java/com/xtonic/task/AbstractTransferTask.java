package com.xtonic.task;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.ErrorMsg;
import com.xtonic.config.SrcData;
import com.xtonic.config.TableChanges;
import com.xtonic.container.ErrorMsgProvider;
import com.xtonic.dataSource.TransferDataSource;
import com.xtonic.exception.GetTotalCountException;
import com.xtonic.type.TypeChangeHander;
import com.xtonic.utils.ClassUtils;

public abstract class AbstractTransferTask<T> extends AbstractInsertDataTask<T> {
	private static final Log LOGGER = LogFactory.getLog(AbstractTransferTask.class);
	
	
	public String call() throws Exception {
		Long startTime = System.currentTimeMillis();
		LOGGER.info("开始执行ID为"+getId()+"的迁移任务");
		int pageNo = 0;
		int count = 0;
		TransferDataSource insertDataSource = config.getDataSources(targetTableDataSourceRef);
		Connection insertConn = insertDataSource.getConnection();
		insertConn.setAutoCommit(true);
		while (true) {
			List<Map<String, Object>> resultData = getDataFromSrc(srcs, pageNo, pageSize);
			
			List dataList = insertBeanGenerator(resultData, beanClass,pageNo);
			
			for (int i = 0; i < dataList.size(); i++) {
				//LOGGER.debug("ID：" + id+"任务，执行插入SQL，插入数据"+dataList.get(i).toString());
				if (!insertData(insertConn, dataList.get(i), insertSQL, beanClass)) {
					String errorMsg = "任务ID：" + id + "的数据：" + dataList.get(i).toString() + "插入到表：" + targetTableName + "表中失败！";
					LOGGER.error(errorMsg);
					ErrorMsgProvider.putErrorMsgData(new ErrorMsg(targetTableName, errorMsg));
				}
			}
			// 退出条件
			pageNo = pageNo + 1;
			count = count + resultData.size();
			if (pageNo == totalPage || count == totalDataCount) {
				break;
			}
		}
		DbUtils.close(insertConn);
		//LOGGER.info("完成" + targetTableName + "表的数据迁移！");
		System.out.println(targetTableName +"任务完成时间："+(System.currentTimeMillis() - startTime) + "毫秒");
		return "完成" + targetTableName + "表的数据迁移！";
	}

	private List<T> insertBeanGenerator(List<Map<String, Object>> resultData, Class<T> beanClass,int pageNo) {
		List<T> dataList = new ArrayList<T>();
		for (int i = 0; i < resultData.size(); i++) {
			Map<String, Object> beanValue = resultData.get(i);
			try {
				T bean = beanClass.newInstance();
				TableChanges change = null;
				String filedName = null;
				for (String key : beanValue.keySet()) {
					filedName = columnToFieldName.get(key.toUpperCase());
					if (changes.containsKey(filedName)) {
						change = changes.get(filedName);
					}
					setBeanValue(beanClass, bean, beanValue.get(key), filedName, change);
					change = null;
				}
				dataList.add(bean);
			} catch (Exception e) {
				String errorMsg = "任务ID：" + id + "的数据：" + beanValue.toString() + "生成实体类失败：" + beanClass.getName();
				LOGGER.error(errorMsg,e);
			}
		}
		if(dataList.size() != resultData.size()){
			int tmpCount = resultData.size() - dataList.size();
			LOGGER.info("Id:"+getId()+"的任务在获取第"+pageNo+"页数据的后，有"+tmpCount + "条记录转化为实体类失败，具体请查看错误信息");
		}
		return dataList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setBeanValue(Class clazz, Object obj, Object value, String propertyName, TableChanges change)
			throws Exception {
		if (change != null) {
			TypeChangeHander hander = config.getTypeHandler(change.getHandlerRef());
			value = hander.changeType(value, Class.forName(change.getSrcType()), Class.forName(change.getTargetType()));
		}
		String methodName;
		try {
			Field field = clazz.getDeclaredField(propertyName);
			methodName = ClassUtils.getBeanMethodName("set", propertyName, clazz);
			Method method = clazz.getMethod(methodName, field.getType());
			method.setAccessible(true);
			method.invoke(obj, value);
		} catch (Exception e) {
			throw new Exception("表实体类" + clazz.getName() + "注入属性值失败", e);
		}
	}

	protected List<Map<String, Object>> doGetDataFromSrc(SrcData srcData, int pageNo, int pageSize) {
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
		QueryRunner runner = new QueryRunner();
		String querySQL = srcData.getQuerySql();
		String dbType = config.getDataSources(srcData.getSrcDataSourceRef()).getDBType();
		querySQL = getQuerySQL(dbType, querySQL, pageNo, pageSize);
		LOGGER.info("ID："+id+"的任务执行查询SQL："+querySQL);
		Connection srcConn = config.getDataSources(srcData.getSrcDataSourceRef()).getConnection();
		try {
			resultMap = runner.query(srcConn, querySQL, new MapListHandler());
		} catch (SQLException e) {
			String errorMsg = "任务ID：" + id + "的数据：" + querySQL + "获取源数据失败：分页信息为：pageNo:"+ pageNo+"pageSize:"+pageSize;
			LOGGER.error(errorMsg,e);
			ErrorMsgProvider.putErrorMsgData(new ErrorMsg(targetTableName, errorMsg));
			return resultMap;
		}
		return resultMap;
	}
}
