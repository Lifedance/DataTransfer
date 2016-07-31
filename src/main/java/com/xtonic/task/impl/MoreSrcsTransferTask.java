package com.xtonic.task.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.ErrorMsg;
import com.xtonic.config.SrcData;
import com.xtonic.container.ErrorMsgProvider;
import com.xtonic.dataSource.TransferDataSource;
import com.xtonic.exception.GetTotalCountException;
import com.xtonic.task.AbstractTransferTask;

@SuppressWarnings("rawtypes")
public class MoreSrcsTransferTask extends AbstractTransferTask {
	private static final Log LOGGER = LogFactory.getLog(MoreSrcsTransferTask.class);

	private SrcData mainSrcData;
	private int selectDataCount = 0;
	private Map<String,Connection> conns = new HashMap<String, Connection>();

	@SuppressWarnings("unchecked")
	public List getDataFromSrc(List srcDatas, int pageNo, int pageSize) {
		long st = System.currentTimeMillis();
		List<Map<String, Object>> resultMap = new ArrayList<Map<String, Object>>();
		SrcData mainSrcData = selectMainSrc(srcDatas, totalDataCount);
		resultMap = doGetDataFromSrc(mainSrcData, pageNo, pageSize);
		srcDatas.remove(mainSrcData);
		for (int i = 0; i < resultMap.size(); i++) {
			Map<String, Object> record = resultMap.get(i);
			SrcData subSrcData = null;
			String subQuerySql = "";
			List<Map<String, Object>> subResultMap = new ArrayList<Map<String, Object>>();
			for (int j = 0; j < srcDatas.size(); j++) {
				subSrcData = (SrcData) srcDatas.get(j);
				subQuerySql = subSrcData.getQuerySql();
				subQuerySql = createdSubQuerySql(record, subQuerySql, mainSrcData.getKeyColumn(),
						subSrcData.getKeyColumn());
				QueryRunner runner = new QueryRunner();
				Connection srcConn = getConns(subSrcData.getSrcDataSourceRef()); 
						//
				try {
					subResultMap = runner.query(srcConn, subQuerySql, new MapListHandler());
				} catch (SQLException e) {
					//resultMap.remove(i);
					String errorMsg = "任务ID：" + id + "的数据：" + subQuerySql + "获取源数据失败";
					LOGGER.error(errorMsg, e);
					ErrorMsgProvider.putErrorMsgData(new ErrorMsg(targetTableName, errorMsg));
				}
				if (subResultMap.size() > 1) {
					LOGGER.error("关键字配置错误，一条主记录，对应俩条记录: 查询SQL为：" + subQuerySql);
					//resultMap.remove(i);
					continue;
				}
				record.putAll(subResultMap.get(0));
			
			}
		}
		System.out.println(getId() + "获取数据耗时：" +(System.currentTimeMillis() - st));
		selectDataCount  = selectDataCount + resultMap.size();
		closeConnection(selectDataCount);
		return resultMap;
	}

	private void closeConnection(int selectDataCount2) {
		if(selectDataCount == totalDataCount){
			for(String key : conns.keySet()){
				DbUtils.closeQuietly(conns.get(key));
			}
		}
	}

	private Connection getConns(String srcDataSourceRef) {
		Connection conn = conns.get(srcDataSourceRef);
		if(conn == null){
			conn = config.getDataSources(srcDataSourceRef).getConnection();
		}
		conns.put(srcDataSourceRef, conn);
		return conn;
	}
	
	private SrcData selectMainSrc(List srcDatas, int totalCount) {
		if (mainSrcData != null) {
			return mainSrcData;
		}
		try {
			for (int i = 0; i < srcDatas.size(); i++) {
				SrcData srcData = (SrcData) srcDatas.get(i);
				//配置文件中指定的主语句；
				if(srcData.getIsMainSrc()){
					this.mainSrcData = srcData;
					return srcData;
				}
				//如果没有配置，则获取每一个数据源的总数据量，当与总数据量一直时候，就当选为主数据源；
				String queryCountSql = "SELECT COUNT(1) FROM ( " + srcData.getQuerySql() + " )  $1";
				TransferDataSource dataSource = config.getDataSources(srcData.getSrcDataSourceRef());
				int count = doQueryCount(queryCountSql, dataSource);
				if (count == totalCount) {
					this.mainSrcData = srcData;
					return srcData;
				}
			}
		} catch (GetTotalCountException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	private String createdSubQuerySql(Map<String, Object> record, String subQuerySql, String mainKeyColumn,
			String subKeyColumn) {
		String[] mainKeyColumns = mainKeyColumn.split(",");
		String[] subKeyColumns = subKeyColumn.split(",");
		String where = " where 1 =1 ";
		for (int i = 0; i < mainKeyColumns.length; i++) {
			where = where + "And " + subKeyColumns[i] + " = " + record.get(mainKeyColumns[i].toUpperCase());
		}
		return (subQuerySql + where).toUpperCase();
	}

}
