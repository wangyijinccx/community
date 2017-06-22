import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.util.HttpRequest;
import com.ipeaksoft.moneyday.core.util.RSAutil;
import com.ipeaksoft.moneyday.core.util.strUtil;


public class TEST {
	
	public static final String PLAT_SECURE_KEY = "5e511d59019de14691b8f0f360bf6841";
	public static final String PLAT_ID ="1869527";

	public static void main(String[] args) throws IOException {
		String a ="bBlrW2iAKZ6UZBccRnBkiaMrqumO3Ybbj%2BE6Bj78DGSotwPhkLssoFGWbkuqAhCnE3Ew7rvbbekS%2BDKKnAeWoJ7UAmfr4FiVhbf5zkeuZ6NEOPMPDOP4lKKvcKiXW6GgSw0LILzlbEzH7Dme6Q0J5B9DtOZu6HEjoL8a28CpAsY%3D";
		String b= "bBlrW2iAKZ6UZBccRnBkiaMrqumO3Ybbj%2BE6Bj78DGSotwPhkLssoFGWbkuqAhCnE3Ew7rvbbekS%2BDKKnAeWoJ7UAmfr4FiVhbf5zkeuZ6NEOPMPDOP4lKKvcKiXW6GgSw0LILzlbEzH7Dme6Q0J5B9DtOZu6HEjoL8a28CpAsY%3D";
		System.out.println(a.equals(b));
		reg("12347856879");
	}
	
	
	public static void reg(String phoneNumber){
		String xgName = "calvin";
		String pass = "362623";
		String encStr ="";
		try {
			String content = xgName+pass;
			System.out.println("签名前="+content);
			String signstr = RSAutil.sign(content);
			encStr = URLEncoder.encode(signstr);
			System.out.println("签名后="+encStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String xgurl ="http://101.201.253.175/index.php/Register/pack";
		Map<String, Object> postParamsXg = new HashMap<String, Object>();
		postParamsXg.put("agent", xgName);
		postParamsXg.put("appid", pass);
		postParamsXg.put("sign", encStr);
		String callback = HttpRequest.postForm(xgurl, postParamsXg);
		System.out.println(callback);
		//验证？？
	}
	
	public static void userPay(){
		String url="http://localhost:8080/community-api/user/pay";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("plat_id", PLAT_ID);
		p.put("timestamp", "149751");
		p.put("sign_type", "MD5");
		p.put("sign", "24a4ed9e99ce33ce2f2648b5787944e3");
		p.put("app_id", 1001);
		p.put("uersname", "ztwireless");
		p.put("agentname", "xg_13552886455");
		p.put("ip", "106.37.252.136");
		p.put("time", "149751");
		p.put("device_id", "macsgi12352");
		p.put("from", 4);
		p.put("userua", "1231212");
		
		
		
		p.put("order_id", 123456);
		p.put("payway", "payway");
		p.put("real_amount", 100);
		p.put("amount", 110);
		p.put("gm_cnt", 500);
		p.put("status", 2);
		p.put("rebate_cnt", 0);
		
		
		p.put("role_id", "r10012");
		p.put("role_level", 100);
		p.put("role_name", "二当家");
		p.put("server_id", "10011");
		p.put("server_name", "天下一通");
		
		
		String result = HttpRequest.postForm(url, p);
		System.out.println(result);
	}
	
	public static void userUproleinfo(){
		String url="http://localhost:8080/community-api/user/uproleinfo";
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("plat_id", PLAT_ID);
		p.put("timestamp", "149751");
		p.put("sign_type", "MD5");
		p.put("sign", "063e859aaaa561403c5b16403dae388b");
		p.put("app_id", 1001);
		p.put("username", "ztwireless");
		p.put("agentname", "xg_13552886455");
		p.put("ip", "106.37.252.136");
		p.put("time", "149751");
		p.put("device_id", "macsgi12351");
		p.put("from", 4);
		p.put("userua", "1231212");
		
		
		
		p.put("role_id", "r10012");
		p.put("role_level", 100);
		p.put("role_name", "二当家");
		p.put("server_id", "10011");
		p.put("server_name", "天下一通");
		
		
		String result = HttpRequest.postForm(url, p);
		System.out.println(result);
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
