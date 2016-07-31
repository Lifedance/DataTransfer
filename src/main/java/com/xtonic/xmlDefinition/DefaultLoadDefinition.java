package com.xtonic.xmlDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.Config;
import com.xtonic.config.DataSourceDeifiniton;
import com.xtonic.config.SrcData;
import com.xtonic.config.TableChanges;
import com.xtonic.config.TransferTableDefiniton;
import com.xtonic.config.TypeHandlerDefinition;
import com.xtonic.context.RegisterFactory;
import com.xtonic.context.TransferContext;
import com.xtonic.resources.Resource;
import com.xtonic.utils.Xml2JsonUtil;
import com.xtonic.validator.DefinitonValidator;
import com.xtonic.validator.impl.DefaultDefinitionValidator;

public class DefaultLoadDefinition implements LoadDefinition {
	private static final Log LOGGER  = LogFactory.getLog(DefaultLoadDefinition.class);
	private static final String IS_LIST = "is_List";
	private static final String IS_MAP = "is_MAP";
	private static final String DATASOURCES_KEY = "dataSources";
	private static final String TABLES_KEY = "tables";
	private static final String PROPERTIES_KEY = "properties";
	private static final String TYPEHANDLERS_KEY = "typehandlers";
	
	
	private Map<String,Object> xmlMap; 
	private List<TransferTableDefiniton> tableDefinitonList = new ArrayList<TransferTableDefiniton>();
	private List<DataSourceDeifiniton> dataSourceDefinitionList = new ArrayList<DataSourceDeifiniton>();
	private List<TypeHandlerDefinition> typeHandlerDeinfitionList = new ArrayList<TypeHandlerDefinition>();
	
	private Config config;
	
	private DefinitonValidator validator;
	private RegisterFactory registerFactory;
	
	public DefaultLoadDefinition(RegisterFactory registerFactory) {
		LOGGER.info("初始化XML任务定义文件加载器完成；" + DefaultLoadDefinition.class.getName() );
		this.registerFactory = registerFactory;
		this.validator  = new DefaultDefinitionValidator((TransferContext) registerFactory);
	}
	public void loadXMLDefiniton(Resource resource) throws IOException{
		xmlMap = Xml2JsonUtil.json2XML(resource.getInputStream());
		LOGGER.debug("将任务定义XML文件转换为JSON："+ xmlMap.toString());
		doLoadXMLDefiniton();
		LOGGER.info("开始解析各类型的定义bean并注册到上下文中"  );
		doRegistConfig();
		//先进行数据源的注册，再进行迁移任务的注册；
		doRegistDataSource();
		doRegistTransferTable();
		doRegistTypeHanlder();
		LOGGER.info("完成解析各类型的定义bean并注册到上下文中"  );
		
	}

	private void doRegistTypeHanlder() {
		for(TypeHandlerDefinition definition : typeHandlerDeinfitionList){
			String errorMsg = null;
			if((errorMsg = validator.typeHandlerDeifnitonValidator(definition)) == null){
				this.registerFactory.registerTypeHandler(definition);
			}else{
				LOGGER.error("ID：" + definition.getHandlerid() + "的类型转换器定义有误，注册失败;错误信息为："+ errorMsg);
				throw new RuntimeException("ID：" + definition.getHandlerid() + "的类型转换器定义有误，注册失败;错误信息为："+ errorMsg);
			}
		}
	}
	private void doRegistConfig() {
		registerFactory.registConfig(config);
	}

	private void doRegistTransferTable() {
		for(TransferTableDefiniton definition : tableDefinitonList){
			String errorMsg = null;
			if((errorMsg = validator.tableDefinitionValidator(definition)) == null){
				this.registerFactory.registTableDefinition(definition);
			}else{
				LOGGER.error("注册任务失败,错误信息：" + errorMsg);
				throw new RuntimeException("注册任务失败,错误信息："+ errorMsg);
			}
		}
	}
	private void doRegistDataSource() {
		for(DataSourceDeifiniton dataSourceDefiniton : dataSourceDefinitionList){
			String errorMsg = null;
			if((errorMsg =validator.dataSourceDefinitionValidator(dataSourceDefiniton)) == null){
				this.registerFactory.registDataSources(dataSourceDefiniton);
			}else{
				LOGGER.error( errorMsg);
				throw new RuntimeException("ID：" + dataSourceDefiniton.getId() + "的数据源定义有误，注册失败;错误信息为："+errorMsg);
			}
		}
	}
	private void doLoadXMLDefiniton() {
		LOGGER.info("开始将XML文件的定义内容转化对应的bean；");
		buildCommonConfig();
		LOGGER.info("开始将XML文件的定义内容转化对应的bean ---  config解析完毕，内容为："+config.toString());
		buildDataSourceDefinitionList();
		LOGGER.info("开始将XML文件的定义内容转化对应的bean ---  DataSources解析完毕，内容为："+dataSourceDefinitionList.toString());
		buildTableDefinitionList();
		LOGGER.info("开始将XML文件的定义内容转化对应的bean ---  tables解析完毕，内容为："+tableDefinitonList.toString());
		buildTypeChangeHandler();
		LOGGER.info("开始将XML文件的定义内容转化对应的bean ---  changeHandlers解析完毕，内容为："+typeHandlerDeinfitionList.toString());
	}
	
	@SuppressWarnings("unchecked")
	private void buildTypeChangeHandler() {
		Map<String,Object> parameterMap = null;
		List tableDefinitions  = (List) xmlMap.get(TYPEHANDLERS_KEY);
		for(int i = 0; i < tableDefinitions.size(); i++){
			parameterMap = (Map<String, Object>) tableDefinitions.get(i);
			TypeHandlerDefinition typeHnadlerDefinition = new TypeHandlerDefinition();
			typeHnadlerDefinition.setHandlerClass((String)parameterMap.get("@handlerClass"));
			typeHnadlerDefinition.setHandlerid((String)parameterMap.get("@handlerid"));
			typeHandlerDeinfitionList.add(typeHnadlerDefinition);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildCommonConfig() {
		Map<String,String> propertiesMap = new HashMap<String, String>();
		List propertyList =  (List) xmlMap.get(PROPERTIES_KEY);
		Map<String,Object> parameterMap = null;
	
		for(int i = 0 ; i < propertyList.size(); i ++){
			parameterMap = (Map<String, Object>) propertyList.get(i);
			if(parameterMap == null){
				continue;
			}
			String key = (String) parameterMap.get("@name");
			String value = (String) parameterMap.get("@value");
			propertiesMap.put(key, value);
		}
		this.config = Config.initConfig(propertiesMap);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildTableDefinitionList() {
		Map<String,Object> parameterMap = null;
		List tableDefinitions  = (List) xmlMap.get(TABLES_KEY);
		for(int i = 0; i < tableDefinitions.size(); i++){
			parameterMap = (Map<String, Object>) tableDefinitions.get(i);
			addTableDefinitions(parameterMap);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addTableDefinitions(Map<String, Object> parameterMap) {
		TransferTableDefiniton tableDefinition = new TransferTableDefiniton();
		tableDefinition.setBeanClass((String)parameterMap.get("@beanClass"));
		tableDefinition.setId((String)parameterMap.get("@id"));
		tableDefinition.setTargetDataSourceRef((String)parameterMap.get("@targetDataSourceRef"));
		tableDefinition.setTable((String)parameterMap.get("@table"));
		tableDefinition.setTaskImplClass((String)parameterMap.get("@taskImplClass"));
		List<SrcData> srcList = new ArrayList<SrcData>();
		Map<String,Object> tmpMap = null;
		List tmpList = (List) parameterMap.get("srcs");
		for(int i = 0; i < tmpList.size(); i++){
			tmpMap = (Map<String, Object>) tmpList.get(i);
			SrcData  srcData = new SrcData();
			srcData.setQuerySql((String)tmpMap.get("@querySql"));
			srcData.setSrcDataSourceRef((String)tmpMap.get("@srcDataSourceRef"));
			srcData.setKeyColumn((String)tmpMap.get("@keyColumn"));
			if("true".equalsIgnoreCase((String)tmpMap.get("@isMainSrc"))){
				srcData.setIsMainSrc(true);
			}else{
				srcData.setIsMainSrc(false);
			}
			srcList.add(srcData);
		}
		tableDefinition.setSrcs(srcList);
		
		List<TableChanges> changesList = new ArrayList<TableChanges>();
		tmpList = (List) parameterMap.get("changes");
		if(tmpList != null){
			for(int i = 0; i < tmpList.size(); i++){
				tmpMap = (Map<String, Object>) tmpList.get(i);
				TableChanges changes = new TableChanges();
				changes.setColumnName((String)tmpMap.get("@columnName"));
				changes.setSrcType((String)tmpMap.get("@srcType"));
				changes.setTargetType((String)tmpMap.get("@targetType"));
				changes.setHandlerRef((String)tmpMap.get("@handlerRef"));
				changesList.add(changes);
			}
		}
		tableDefinition.setChanges(changesList);
		
		tableDefinitonList.add(tableDefinition);
		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildDataSourceDefinitionList() {
		String collectionType = checkCollectionType(xmlMap.get(DATASOURCES_KEY)); 
		Map<String,Object> parameterMap = null;
		if(collectionType.equals(IS_LIST)){
			List dataSources = (List) xmlMap.get(DATASOURCES_KEY);
			for(int i = 0 ; i < dataSources.size();i++){
				parameterMap = (Map<String, Object>) dataSources.get(i);
				addDataSource(parameterMap);
			}
			return;
		}
		parameterMap = (Map<String, Object>) xmlMap.get(DATASOURCES_KEY);
		addDataSource(parameterMap);
		
		
	}
	private void addDataSource(Map<String, Object> parameterMap) {
		DataSourceDeifiniton dataSource = new DataSourceDeifiniton();
		dataSource.setDriverClass((String)parameterMap.get("driverClass"));
		dataSource.setPassword((String)parameterMap.get("password"));
		dataSource.setUrl((String)parameterMap.get("url"));
		dataSource.setUsername((String)parameterMap.get("username"));
		dataSource.setId((String)parameterMap.get("@id"));
		dataSource.setDbType((String)parameterMap.get("@dbType"));
		dataSourceDefinitionList.add(dataSource);
	}
	private String checkCollectionType(Object object) {
		if(Collection.class.isAssignableFrom(object.getClass())){
			return IS_LIST;
		}
		return IS_MAP;
	}
}
