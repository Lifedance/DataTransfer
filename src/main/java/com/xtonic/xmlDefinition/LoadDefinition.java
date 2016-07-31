package com.xtonic.xmlDefinition;

import java.io.IOException;
import java.util.List;

import com.xtonic.config.Config;
import com.xtonic.config.DataSourceDeifiniton;
import com.xtonic.config.TransferTableDefiniton;
import com.xtonic.resources.Resource;

public interface LoadDefinition {

	void loadXMLDefiniton(Resource resource) throws IOException;
}
