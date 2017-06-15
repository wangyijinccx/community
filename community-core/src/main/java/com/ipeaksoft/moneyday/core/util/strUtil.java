package com.ipeaksoft.moneyday.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class strUtil {
	public static String getPassWord(String passWord) {
		return passWord.substring(passWord.length() - 6, passWord.length());
	}

	public static String getAgentName(String agentname) {
		return agentname.replace("xg_", "");
	}

	public static Map<String, String> getMap(Map<String, String[]> maps) {
		Map<String, String> map = new HashMap<String, String>();
		for (Entry<String, String[]> entry : maps.entrySet()) {
			map.put(entry.getKey(), entry.getValue()[0]);
		}
		return map;
	}
	
	public static String map2JsonString(Map<String, String[]> maps) {
		String string= "{";
		for (Entry<String, String[]> entry : maps.entrySet()) {
			string += "\""+entry.getKey()+"\":\""+entry.getValue()[0]+"\",";
		}
		string = string.substring(0, string.length()-1);
		string+="}";
		return string;
	}
}
