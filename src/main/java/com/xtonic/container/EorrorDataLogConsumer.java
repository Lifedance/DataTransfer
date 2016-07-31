package com.xtonic.container;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.xtonic.config.ErrorMsg;

public interface EorrorDataLogConsumer<T> extends Callable<T> {
	public static final BlockingQueue<ErrorMsg> messageQueue = new LinkedBlockingQueue<ErrorMsg>();
	public static boolean stopConsumerFlag = false;

	void stopConsumer(Future<String> consumerTask);
}
