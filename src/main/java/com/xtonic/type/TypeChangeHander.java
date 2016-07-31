package com.xtonic.type;

public interface TypeChangeHander<T> {
	T  changeType(Object value, Class SrcTypeClazz, Class<T> targetTypeClazz);
}
