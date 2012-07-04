package com.jijesoft.boh.core.threadlocal;

public class RunData {
	
	private String tntpid;
	private String siteeid;
	
	private static ThreadLocal<RunData> context = new ThreadLocal<RunData>() {   
        protected synchronized RunData initialValue() {   
           return new RunData();   
        }   
    };   
   
	public static String getTntpid() {
		return context.get().tntpid;
	}
	public static void setTntpid(String tntpid) {
		context.get().tntpid = tntpid;
	}
	public static String getSiteeid() {
		return context.get().siteeid;
	}
	public static void setSiteeid(String siteeid) {
		context.get().siteeid = siteeid;
	}
   
}
