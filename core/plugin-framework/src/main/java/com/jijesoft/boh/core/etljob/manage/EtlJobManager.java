package com.jijesoft.boh.core.etljob.manage;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class EtlJobManager {
	
	private static EtlJobManager etlJobManager;
	
	Map<String,EtlJob> etlJobs=new HashMap<String,EtlJob>();
	
	private EtlJobManager(){
		
	}
	
	public static EtlJobManager getEtlJobInstance(){
		if(null==etlJobManager){
			etlJobManager=new EtlJobManager();
		}
		return etlJobManager;
	}
	
	public void put(EtlJob etlJob){
		etlJobs.put(etlJob.getKey(), etlJob);	
	}
	
	public EtlJob getEtlJobById(String id){
		return etlJobs.get(id);
	}
	
	public Collection<EtlJob> getAllEtlJobs(){
		return etlJobs.values();
	}
	
	public void remove(String etlJobId) {
		if(etlJobs.containsKey(etlJobId)){
			etlJobs.remove(etlJobId);
		}
	}
}
