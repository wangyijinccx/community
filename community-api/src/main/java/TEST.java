import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.util.HttpRequest;


public class TEST {
	
	public static final String PLAT_SECURE_KEY = "5e511d59019de14691b8f0f360bf6841";
	public static final String PLAT_ID ="1869527";

	public static void main(String[] args) throws IOException {
		userReg();
	}
	
	
	public static void userReg(){
		String url="http://localhost:8080/community-api/user/reg";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("plat_id", PLAT_ID);
		p.put("timestamp", "149751");
		p.put("sign_type", "MD5");
		p.put("sign", "356b52b5b1a50be9e34a7dbbf09a0a8a");
		p.put("app_id", 1001);
		p.put("username", "ztwireless");
		p.put("agentname", "xg_13552886455");
		p.put("ip", "106.37.252.136");
		p.put("time", "149751");
		p.put("device_id", "macsgi1235");
		p.put("from", 4);
		p.put("userua", "1231212");
		p.put("password", "12314");
		String result = HttpRequest.postForm(url, p);
		System.out.println(result);
	}
	
	
	
	public static void serverUpdate(){
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("update_time", 149751);
		jo.put("server_name", "huangshiserver1");
		jo.put("server_id", "10011");
		String url="http://localhost:8080/community-api/server/update";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("plat_id", PLAT_ID);
		p.put("timestamp", "149751");
		p.put("sign_type", "MD5");
		p.put("sign", "b267ca557e8b46b0a3e1d0a3d40463e2");
		p.put("app_id", 1001);
		p.put("gamename", "皇室战争");
		p.put("serinfo", jo.toString());
		String result = HttpRequest.postForm(url, p);
		System.out.println(result);
	}
	
	
	
	public static void serverAdd(){
		String url="http://localhost:8080/community-api/server/add";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("plat_id", PLAT_ID);
		p.put("timestamp", "149751");
		p.put("sign_type", "MD5");
		p.put("sign", "82b429ff6a6f98d7611be440e4e102a4");
		p.put("app_id", 1001);
		p.put("gamename", "皇室战争");
	    p.put("server_id", 10011);
		p.put("server_code", "huangshiserver");
		p.put("server_name", "天下一通");
		p.put("server_desc", "天下一通");
		p.put("creat_time", 149751);
		p.put("start_time", 149751);
		String result = HttpRequest.postForm(url, p);
		System.out.println(result);
	}
	
	
	public static void gameAdd(){
		String url="http://localhost:8080/community-api/game/add";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("plat_id", PLAT_ID);
		p.put("timestamp", "1497515216512");
		p.put("sign_type", "MD5");
		p.put("sign", "962c30ef8d87ad5916005ac4eb54c817");
		p.put("app_id", 1001);
		p.put("gamename", "皇室战争");
	    p.put("classify", 4);
		p.put("gameflag", "huangshizhanzheng");
		p.put("creat_time", "1497515216512");
		p.put("status", "2");
		p.put("pinyin", "huangshizhanzheng");
		p.put("initial", "h");
		p.put("version", 1);
		String result = HttpRequest.postForm(url, p);
		System.out.println(result);
	}
	
	
	public static void gameUpdate(){
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("update_time", "1497515216512");
		jo.put("status", "3");
		jo.put("pinyin", "huangshizhanzheng1");
		String url="http://localhost:8080/community-api/game/update";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("plat_id", PLAT_ID);
		p.put("timestamp", "1497515216512");
		p.put("sign_type", "MD5");
		p.put("sign", "68fac253cecb1229bf4481c6b7ebd8bf");
		p.put("app_id", 1001);
		p.put("upinfo", jo.toString());
		String result = HttpRequest.postForm(url, p);
		System.out.println(result);
	}
	
	
	public static void gameDelete(){
		String url="http://localhost:8080/community-api/game/delete";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("plat_id", PLAT_ID);
		p.put("timestamp", "1497515216512");
		p.put("sign_type", "MD5");
		p.put("sign", "9900e4768e916339e18f6ad87759d656");
		p.put("app_id", 1001);
		p.put("delete_time", "1497515216512");
		String result = HttpRequest.postForm(url, p);
		System.out.println(result);
	}
	
	public static void gameRestore(){
		String url="http://localhost:8080/community-api/game/restore";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("plat_id", PLAT_ID);
		p.put("timestamp", "1497515216512");
		p.put("sign_type", "MD5");
		p.put("sign", "1818a53f8a546b13a53f1da851c40a41");
		p.put("app_id", 1001);
		p.put("restore_time", "1497515216512");
		String result = HttpRequest.postForm(url, p);
		System.out.println(result);
	}
}
