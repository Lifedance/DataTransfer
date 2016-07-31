package com.xtonic.task;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;

import com.xtonic.utils.ClassUtils;
import com.xtonic.utils.PageQuerySQLTemplate;

public abstract class AbstractInsertDataTask<T> extends TransferTaskConfig<T>  {
	private int count =0;
	private PreparedStatement pst = null;
	public boolean insertData(Connection conn, Object obj, String insertSql, Class clazz) {
		
		
		List values = getValues(obj, beanClass);
		
		//如果有需要对即将插入的数据进行特殊处理的；在自己的实现类里面重写这个方法就可以；
		customProcessValues(values,fieldNames);
		
		try {
			conn.setAutoCommit(false);
			if(pst == null){
				pst = conn.prepareStatement(insertSql);
			}
			for (int i = 0; i < values.size(); i++) {
				pst.setObject(i + 1, values.get(i));
			}
			count++;
			pst.addBatch();
			//pst.executeUpdate();
			if(count!=0 && count % 2000 ==0 ){
				System.out.println("id:"+getId()+"的任务已经迁移了" +count +"条");
				pst.executeBatch();
				pst.clearBatch();
				conn.commit();
			}
			
			if(count == totalDataCount){
				System.out.println("id:"+getId()+"的任务已经迁移了" +count +"条");
				pst.executeBatch();
				pst.clearBatch();
				conn.commit();
			}
			
			//DbUtils.close(pst);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 重写这个方法，进行一些特殊的处理； 
	 * @param values
	 * @param fieldNames 
	 */
	public void customProcessValues(List values, List<String> fieldNames) {
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List getValues(Object obj, Class clazz) {
		List list = new ArrayList();
		Method method = null;
		String methodName = "";
		for (String name : fieldNames) {
			try {
				methodName = ClassUtils.getBeanMethodName("get", name, clazz);
				method = clazz.getMethod(methodName, null);
				list.add(method.invoke(obj, null));
			} catch (Exception e) {
				// TODO: 不能抛运行时异常
				throw new RuntimeException("初始化Context出错：实体类" + clazz.getName() + " 的属性" + name + "没有对应的get方法", e);
			}
		}
		return list;
	}
	
	
	
	protected String getQuerySQL(String dbType,String querySql,int pageNo, int pageSize){
		String tmp = PageQuerySQLTemplate.creatPageableQuerSql(dbType, querySql);
		
		return MessageFormat.format(tmp, String.valueOf(pageNo*pageSize),String.valueOf(pageSize));
	}
}
