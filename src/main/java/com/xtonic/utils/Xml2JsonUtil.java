package com.xtonic.utils;

import java.io.InputStream;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public class Xml2JsonUtil {
    public static String xml2JSON(String xml){
        return new XMLSerializer().read(xml).toString();
    }
     
    @SuppressWarnings("unchecked")
	public static Map<String,Object> xml2Map(String xml){
    	JSONObject json = (JSONObject) new XMLSerializer().read(xml);
    	return (Map<String,Object>) json;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> json2XML(InputStream inputStream) {
    	JSONObject json = (JSONObject) new XMLSerializer().readFromStream(inputStream);
		return (Map<String,Object>) json;
	}
    public static String json2XML(String json){
        JSONObject jobj = JSONObject.fromObject(json);
        String xml =  new XMLSerializer().write(jobj);
        return xml;
    }

	// 测试
	public static void main(String[] args) {
		
		String xml = "<MapSet>" + "<MapGroup id='Sheboygan'>" + "<Map>" + "<Type>MapGuideddddddd</Type>"
				+ "<SingleTile>true</SingleTile>" + "<Extension>" + "<ResourceId>ddd</ResourceId>"
				+ "</Extension>" + "</Map>" + "<Map>" + "<Type>ccc</Type>" + "<SingleTile>ggg</SingleTile>"
				+ "<Extension>" + "<ResourceId>aaa</ResourceId>" + "</Extension>" + "</Map>" + "<Extension />"
				+ "</MapGroup>" + "<ddd>" + "33333333" + "</ddd>" + "<ddd>" + "444" + "</ddd>" + "</MapSet>";
		
		System.out.println(xml);
		
		System.out.println(xml2JSON(xml));
		Map<String,Object> tmpMap = xml2Map(xml);
		System.out.println(tmpMap.toString());
		
//		System.out.println(Xml2JsonUtil
//				.xml2JSON("<MapSet>" + "<MapGroup id='Sheboygan'>" + "<Map>" + "<Type>MapGuideddddddd</Type>"
//						+ "<SingleTile>true</SingleTile>" + "<Extension>" + "<ResourceId>ddd</ResourceId>"
//						+ "</Extension>" + "</Map>" + "<Map>" + "<Type>ccc</Type>" + "<SingleTile>ggg</SingleTile>"
//						+ "<Extension>" + "<ResourceId>aaa</ResourceId>" + "</Extension>" + "</Map>" + "<Extension />"
//						+ "</MapGroup>" + "<ddd>" + "33333333" + "</ddd>" + "<ddd>" + "444" + "</ddd>" + "</MapSet>"));
	}

	
}
