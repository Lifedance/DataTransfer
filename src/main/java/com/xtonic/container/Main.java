package com.xtonic.container;

import java.io.IOException;

import com.xtonic.context.TransferContext;
import com.xtonic.context.impl.TrnasferAppContext;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		//初始化XML文件，生成上下文
		TransferContext context = new TrnasferAppContext("TransferDefinitions.xml");
		//将上下文丢到容器里面进行运行；
 		Runcontainer containor = new Runcontainer(context);
		containor.excute();
	}
}
