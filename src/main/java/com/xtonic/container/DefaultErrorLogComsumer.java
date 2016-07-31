package com.xtonic.container;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xtonic.config.ErrorMsg;
import com.xtonic.context.TransferContext;

public class DefaultErrorLogComsumer implements EorrorDataLogConsumer<Object> {
	private static final Log LOGER = LogFactory.getLog(DefaultErrorLogComsumer.class);
	private String failRecordFilePath;
	private Map<String,File>  files= new HashMap<String, File>();
	private Map<String,BufferedWriter> fileStreams = new HashMap<String, BufferedWriter>();
	private boolean allTransferTasksDone = false; 
	
	
	public DefaultErrorLogComsumer(TransferContext context) {
		String filePath = (String) context.getProperty("fialRecordFilePath");
		if(filePath == null){
			filePath = this.getClass().getResource(".").getFile().toString();
		}
		this.failRecordFilePath = filePath;
	}

	public Object call() throws Exception {
		while(true){
			messageConsumer();
			if(allTransferTasksDone && messageQueue.isEmpty()){
				break;
			}
		}
		LOGER.info("错误数据记录器停止工作");
		return "错误数据记录器停止工作";
	}

	public void messageConsumer() {
		ErrorMsg  errorMsg  = null;
		try {
			errorMsg = messageQueue.take();  //如果messageQueue 为空，在这里阻塞等待；
		} catch (InterruptedException e) {
			if(messageQueue.isEmpty() && allTransferTasksDone){
				return;
			}
			LOGER.error("从队列中获取错误信息失败",e);
			return;
		}	
		File file = getFile(errorMsg.getTableName());
		System.out.println(" .............." + file.getAbsolutePath());
 		BufferedWriter writer = getFileString(errorMsg.getTableName(),file);
		
		try {
			writer.write(errorMsg.getErrorMsg()+ "\r\n");
			writer.flush();
		} catch (IOException e) {
			LOGER.error("将错误信息："+errorMsg.getErrorMsg() + "写入"+file.getName()+"失败",e);
		}
	}

	private BufferedWriter getFileString(String tableName, File file) {
		BufferedWriter writer = fileStreams.get(tableName);
		if(writer == null){
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			} catch (FileNotFoundException e) {
				LOGER.error("创建工作流失败：");
			}
		}
		fileStreams.put(tableName, writer);
		return writer;
	}

	private File getFile(String tableName) {
		File file = files.get(tableName);
		if(file != null){
			return file;
		}
		file = new File(failRecordFilePath+File.pathSeparator+tableName + ".txt");
		files.put(tableName, file);
		return file;
	}

	public void stopConsumer() {
		allTransferTasksDone = true;
		System.out.println(Thread.currentThread().getName());
		Thread.currentThread().interrupted();
	}

	public void stopConsumer(Future<String> consumerTask) {
		allTransferTasksDone = true;
		System.out.println(Thread.currentThread().getName());
		consumerTask.cancel(true);
	}
}
