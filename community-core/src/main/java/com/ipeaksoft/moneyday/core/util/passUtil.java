package com.ipeaksoft.moneyday.core.util;

public class passUtil {
	public static String getPassWord(String passWord) {
		return passWord.substring(passWord.length() - 6, passWord.length());
	}
}
