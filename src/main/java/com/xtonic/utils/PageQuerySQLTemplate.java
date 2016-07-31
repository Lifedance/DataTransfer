package com.xtonic.utils;

public class PageQuerySQLTemplate {
	
	public static String creatPageableQuerSql(String dataBaseType,String querySql){
		if("mysql".equalsIgnoreCase(dataBaseType)){
			return creatMySqlQuerySql(querySql);
		}
		if("sqlserver".equalsIgnoreCase(dataBaseType)){
			return querySql;
		}
		if("oracle".equalsIgnoreCase(dataBaseType)){
			return querySql;
		}
		return querySql;
	};
	
	
	public static String creatMySqlQuerySql(String querySql){
		String tmpQuerySql = querySql.toUpperCase();
		StringBuilder sql = new StringBuilder();
//		sql.append(tmpQuerySql.substring(0, tmpQuerySql.indexOf("FROM")))
		sql.append("SELECT *  ")
		     .append(" FROM (")
		     .append(tmpQuerySql)
		     .append(")$1")
		     .append(" limit {0},{1}");
		return sql.toString();
	}
	
	
	
	
	
	
}
