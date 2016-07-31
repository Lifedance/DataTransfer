package com.xtonic.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Resource {
	
	File getFile();

	boolean isExist();
	
	InputStream getInputStream() throws IOException;
	
}
