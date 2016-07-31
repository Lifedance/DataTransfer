package com.xtonic.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.context.TransferContext;
import com.xtonic.task.TransferTask;
import com.xtonic.task.TransferTaskConfig;

public class Runcontainer {
	private static final Log LOGGER = LogFactory.getLog(Runcontainer.class);

	private ExecutorService transferWorker = Executors.newFixedThreadPool(10);
	private List<Future<String>> taskResults = new ArrayList<Future<String>>();
	@SuppressWarnings("rawtypes")
	private EorrorDataLogConsumer errorLogDealer;
	private Future<String> consumerTask;
	private TransferContext context;
	private int taskCount = 0;

	@SuppressWarnings("unchecked")
	public Runcontainer(TransferContext context) {
		LOGGER.info("迁移任务容器启动................");
		this.context = context;
		errorLogDealer = new DefaultErrorLogComsumer(context);
		this.consumerTask = transferWorker.submit(errorLogDealer);
		LOGGER.info("完成容器注册任务上下文及错误记录处理器；错误记录处理器为：" + EorrorDataLogConsumer.class.getName());
	}

	public void excute() {
		long st = System.currentTimeMillis();
		Map<String, TransferTask<?>> tasks = context.getTransferTasks();
		for (String key : tasks.keySet()) {
			LOGGER.info("提交执行任务,任务ID：" + ((TransferTaskConfig) context.getTransferTask(key)).getId());
			taskCount++;
			try {
				Future<String> result = transferWorker.submit(tasks.get(key));
				taskResults.add(result);
			} catch (Exception e) {
				LOGGER.error("提交执行任务失败,任务ID：" + ((TransferTaskConfig) context.getTransferTask(key)).getId(), e);
			}
		}
		checkExcuteResult();
		System.out.println("所有的任务的完成时间：" + (System.currentTimeMillis() - st) );
	}

	private void checkExcuteResult() {
		int finishedCount = 0;
		while (true) {
			for (int i = 0; i < taskResults.size(); i ++) {
				try {
					String resultStr = taskResults.get(i).get(5, TimeUnit.SECONDS);
					taskResults.remove(i);
					i --;
					finishedCount ++;
					if (resultStr != null) {
						LOGGER.info(resultStr);
					}
				} catch (Exception e) {
					if(e instanceof TimeoutException){
						//System.out.println("超时！！！！！！");
						continue;
					}
					e.printStackTrace();
				}
			}
			if(finishedCount == taskCount){
				break;
			}
		}
		LOGGER.info("所有的任务均都执行完毕，关闭迁移任务容器....................");
		errorLogDealer.stopConsumer(consumerTask);
		transferWorker.shutdown();
	}

	public TransferContext getContext() {
		return context;
	}
}
