package com.xtonic.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.xtonic.config.TableChanges;
import com.xtonic.entity.User;

public class ClassUtils {
	public static String getBeanMethodName(String prefix,String fieldName,Class clazz) throws NoSuchFieldException, SecurityException{
		Field field = clazz.getDeclaredField(fieldName);
		if(field.getType() == boolean.class &&fieldName.toUpperCase().startsWith("IS")){
			String tmpStr = fieldName.substring(2);
			return fieldName;
		}
		
		if(Character.isUpperCase(fieldName.charAt(0))){
			return prefix + fieldName;
		}
		
		if(Character.isUpperCase(fieldName.charAt(1))){
			return prefix + fieldName;
		}
		
		return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		
	}
	
	public static void setBeanValue(Class clazz, Object obj, Object value,String propertyName,TableChanges change) throws Exception{
			if(change != null){
				value = dealChange(value,Class.forName(change.getSrcType()) , Class.forName(change.getTargetType()));
			}
			String methodName;
			try {
				Field field = clazz.getDeclaredField(propertyName);
				methodName = getBeanMethodName("set", propertyName, clazz);
				Method method = clazz.getMethod(methodName, field.getType());
				method.setAccessible(true);
				method.invoke(obj, value);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("表实体类"+clazz.getName()+"注入属性值失败",e);
			} 
	}
	
	private static <V, T> V dealChange(Object value,Class<T> srcTypeClazz , Class<V> targetTypeClazz){
		if(srcTypeClazz == String.class  && targetTypeClazz == Integer.class){
			return (V) Integer.valueOf((String)value);
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		User tmp = new User();
		
		setBeanValue(User.class,tmp,"1","username",null);
		
		System.out.println(tmp.toString());
		
		
		
	}
	
	
	
}
