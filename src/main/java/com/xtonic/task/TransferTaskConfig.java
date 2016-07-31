package com.xtonic.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.SrcData;
import com.xtonic.config.TableChanges;
import com.xtonic.config.TransferTableDefiniton;
import com.xtonic.context.TransferContext;
import com.xtonic.dataSource.TransferDataSource;
import com.xtonic.exception.GetTotalCountException;

public abstract class TransferTaskConfig<T> implements TransferTask<T> {
	private static final Log LOGGER = LogFactory.getLog(TransferTaskConfig.class);
	
	protected String id;
	@SuppressWarnings("rawtypes")
	protected Class beanClass;
	protected TransferContext config;
	protected String targetTableName;
	protected String targetTableDataSourceRef;
	protected List<String> fieldNames = new ArrayList<String>();
	protected Map<String,TableChanges> changes;
	protected List<SrcData> srcs;
	protected Map<String,String> columnToFieldName;
	protected int totalDataCount =0;
	protected int totalPage = 0;
	protected int pageSize = 0;
	protected String insertSQL = null;
	
	
	public Map<String, String> getColumnToFieldName() {
		return columnToFieldName;
	}
	public void setColumnToFieldName(Map<String, String> columnToFieldName) {
		this.columnToFieldName = columnToFieldName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@SuppressWarnings("rawtypes")
	public Class getBeanClass() {
		return beanClass;
	}
	@SuppressWarnings("rawtypes")
	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}
	public TransferContext getConfig() {
		return config;
	}
	public void setConfig(TransferContext config) {
		this.config = config;
	}
	public String getTargetTableName() {
		return targetTableName;
	}
	public void setTargetTableName(String targetTableName) {
		this.targetTableName = targetTableName;
	}
	public String getTargetTableDataSourceRef() {
		return targetTableDataSourceRef;
	}
	public void setTargetTableDataSourceRef(String targetTableDataSourceRef) {
		this.targetTableDataSourceRef = targetTableDataSourceRef;
	}
	public Map<String, TableChanges> getChanges() {
		return changes;
	}
	public void setChanges(Map<String, TableChanges> changes) {
		this.changes = changes;
	}
	public List<String> getFieldNames() {
		return fieldNames;
	}
	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}
	public List<SrcData> getSrcs() {
		return srcs;
	}
	public void setSrcs(List<SrcData> srcs) {
		this.srcs = srcs;
	}
	public int getTotalDataCount() {
		return totalDataCount;
	}
	public void setTotalDataCount(int totalDataCount) {
		this.totalDataCount = totalDataCount;
	}
	
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getInsertSQL() {
		return insertSQL;
	}
	public void setInsertSQL(String insertSQL) {
		this.insertSQL = insertSQL;
	}
	/**
	 * 获取待复制数据的总量
	 * 
	 * @param list
	 * @return
	 * @throws GetTotalCountException
	 */
	protected int getCount(List<SrcData> list) throws GetTotalCountException {
		String queryCountSql = null;
		int count = 0;
		for (SrcData src : list) {
			int tmp = count;
			queryCountSql = "SELECT COUNT(1) FROM ( " + src.getQuerySql() + " )  $1";
			count = doQueryCount(queryCountSql, config.getDataSources(src.getSrcDataSourceRef()));
			//如果该数据源是主数据源，则总数量以主数据源为准；否则就取数据量最大的数据源的数据作为总数据量；
			if(src.getIsMainSrc()){
				return count;
			}
			count = tmp > count ? tmp : count;
		}
		return count;
	}

	protected int doQueryCount(String queryCountSql, TransferDataSource transferDataSource)
			throws GetTotalCountException {
		Connection conn = transferDataSource.getConnection();
		Statement stat = null;
		ResultSet result = null;
		try {
			stat = conn.createStatement();
			result = stat.executeQuery(queryCountSql);
			result.next();
			int tmpCount =  result.getInt(1);
			LOGGER.info("执行SQL语句："+ queryCountSql +"获取记录条数："+ tmpCount+"条记录");
			return tmpCount;
		} catch (Exception e) {
			throw new GetTotalCountException("取總數據量失敗,SQL:" + queryCountSql, e);
		} finally {
			DbUtils.closeQuietly(conn, stat, result);
		}
	}
	/**
	 * 获取插入数据的SQL语句
	 * @param fieldNames2 
	 * 
	 * @return
	 */
	protected String getInserSQL(String tableName, List<String> fieldNames) {
		String columNamesStr = "";
		String valuesStr = "";
		for (String fieldName : fieldNames) {
			columNamesStr = columNamesStr + fieldName + ",";
			valuesStr = valuesStr + "? ,";
		}
		columNamesStr = columNamesStr.substring(0, columNamesStr.length() - 1);
		valuesStr = valuesStr.substring(0, valuesStr.length() - 1);

		if (columNamesStr.length() == 0) {
			throw new RuntimeException("初始化Context出错：表名：" + tableName + " 对应的实体类没有定义任何属性");
		}

		String insertSQL = "INSERT INTO " + tableName + " ( " + columNamesStr + " ) VALUES ( " + valuesStr + " )";

		return insertSQL;
	}
}
