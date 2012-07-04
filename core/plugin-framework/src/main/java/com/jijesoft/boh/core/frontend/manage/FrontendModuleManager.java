package com.jijesoft.boh.core.frontend.manage;

import java.util.HashMap;
import java.util.Map;

public class FrontendModuleManager {
	
	private static FrontendModuleManager frontendModuleManager;
	
	Map<String,FrontendModule> frontendModules=new HashMap<String,FrontendModule>();
	
	private FrontendModuleManager(){
		
	}
	
	public static FrontendModuleManager getFrontendModuleManagerInstance(){
		if(frontendModuleManager==null){
			frontendModuleManager=new FrontendModuleManager();
		}
		return frontendModuleManager;
	}
	
	public void put(FrontendModule frontendModule){
		frontendModules.put(frontendModule.getKey(), frontendModule);
	}
	
	public FrontendModule getFrontendModuleById(String id){
		return frontendModules.get(id);
	}
	
	public void remove(String id){
		if(frontendModules.containsKey(id)){
			frontendModules.remove(frontendModules);
		}
	}
	

}
