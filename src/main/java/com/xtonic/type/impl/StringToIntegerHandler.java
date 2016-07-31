package com.xtonic.type.impl;

import com.xtonic.type.TypeChangeHander;

public class StringToIntegerHandler implements TypeChangeHander<Integer> {

	public Integer changeType(Object value, Class SrcTypeClazz, Class<Integer> targetTypeClazz) {
		if(SrcTypeClazz != String.class){
			return null;
		}
		return   Integer.valueOf((String)value);
	}

	

	
	
}
