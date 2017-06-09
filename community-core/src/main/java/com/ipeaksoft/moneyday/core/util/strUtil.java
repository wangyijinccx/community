package com.ipeaksoft.moneyday.core.util;

public class strUtil {
	public static String getPassWord(String passWord) {
		return passWord.substring(passWord.length() - 6, passWord.length());
	}
	public static String getAgentName(String agentname){
		return agentname.replace("xg_", "");
	}
}
