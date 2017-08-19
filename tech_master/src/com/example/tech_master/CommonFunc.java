package com.example.tech_master;

public class CommonFunc {
	public static int getLineNumber(Exception e){
		StackTraceElement[] trace =e.getStackTrace();
		if(trace==null||trace.length==0) return -1; //
		return trace[0].getLineNumber();
		}
	public static String getFileName(Exception e){
		StackTraceElement[] trace =e.getStackTrace();
		if(trace==null||trace.length==0) return null; //
		return trace[0].getFileName();
		}

}
