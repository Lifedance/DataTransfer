package com.xtonic.context.impl;

import java.io.IOException;

import com.xtonic.resources.ClassPathResource;
import com.xtonic.resources.Resource;

public   class TrnasferAppContext extends BaseTrnasferContext{

	public TrnasferAppContext(String path) throws IOException {
		this(new ClassPathResource(path));
	}
	
	public TrnasferAppContext(Resource resource) throws IOException{
		super(resource);
	}
}
