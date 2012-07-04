package com.jijesoft.boh.core.report.factory;

import java.util.Collection;
import java.util.Map;

public interface DataSourceFactory {

	public Collection<Object> getDataSource(Map parameters);
}
