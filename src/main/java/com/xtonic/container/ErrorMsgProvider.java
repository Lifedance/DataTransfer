package com.xtonic.container;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.ErrorMsg;

public class ErrorMsgProvider {
	private static final Log LOGGER = LogFactory.getLog(ErrorMsgProvider.class);
	public static void putErrorMsgData(ErrorMsg errorMsg){
		try {
			EorrorDataLogConsumer.messageQueue.put(errorMsg);
		} catch (Exception e) {
			LOGGER.error("错误信息入队列失败； 错误信息为："+ errorMsg.toString(),e);
		}
	}
}
