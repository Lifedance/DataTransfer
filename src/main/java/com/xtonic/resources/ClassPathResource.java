package com.xtonic.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ClassPathResource implements Resource {
	
	private final String path;
	
	private final File file;
	
	public ClassPathResource(String path){
		this.path = path;
		this.file = new File(path);
	}
	
	public File getFile() {
		return this.file;
	}

	public boolean isExist() {
		return file.exists();
	}
	
	public InputStream getInputStream() throws IOException {

		return  this.getClass().getClassLoader().getResourceAsStream(path);
	}
}
