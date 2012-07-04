package com.jijesoft.boh.startup;

import java.util.HashMap;
import java.util.Map;

public class Env {
	
	private static Env env;
	
	Map<String,String> envdata=new HashMap<String,String>();
	
	private Env(){
		
	}
	
	public static Env getEnvInstance(){
		if(env==null){
			env=new Env();
		}
		return env;
	}
	
	public void addConfigFilePath(String filePath){
		envdata.put("configfilepath", filePath);
	}
	
	public String getConfigFilePath(){
		return envdata.get("configfilepath");
	}
	
	public void clearEnvData(){
		envdata.clear();
	}

}
