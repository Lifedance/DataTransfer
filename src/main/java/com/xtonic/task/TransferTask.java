package com.xtonic.task;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.xtonic.config.SrcData;

public interface TransferTask<T> extends Callable<String> {
	/**
	 * 从源表中获取要被插入的对象；
	 * @return
	 */
	 List<Map<String, Object>>  getDataFromSrc(List<SrcData> srcDatas, int pageNo, int pageSize);
	 /**
	  * 将一条数据插入到数据库里面；
	  * @param t
	  * @return
	  */
	 boolean insertData(Connection conn,Object obj,String insertSql,Class clazz);
}
