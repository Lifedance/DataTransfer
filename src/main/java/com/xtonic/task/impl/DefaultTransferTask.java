package com.xtonic.task.impl;

import java.util.List;
import java.util.Map;

import com.xtonic.config.SrcData;
import com.xtonic.task.AbstractTransferTask;

@SuppressWarnings("rawtypes")
public class DefaultTransferTask extends AbstractTransferTask {

	
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getDataFromSrc(List srcDatas, int pageNo, int pageSize) {
		return doGetDataFromSrc((SrcData)srcDatas.get(0), pageNo, pageSize);
	}
}
