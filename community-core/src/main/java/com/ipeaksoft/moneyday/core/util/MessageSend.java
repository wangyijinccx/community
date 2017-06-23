package com.ipeaksoft.moneyday.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javapns.devices.Device;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;

public class MessageSend {
	// ios
	public static final String Path = "";
	public static final String Key = "";
	// jpush-android
	private final static String appKey = "7d431e42dfa6a6d693ac2d04";
	private final static String masterSecret = "5e987ac6d2e04d95a9d8f0d1";

	public static void iosSend(String deviceToken, String alert, Integer badge) {
		// String deviceToken =
		// "d4b3c5f3d497554f56f6f9791872666ae06e3b4e7abad6f4792dcd030007db91";
		// String alert = "给你发信息了";// push的内容
		// int badge = 3;// 图标小红圈的数值
		String sound = "default";// 铃音

		List<String> tokens = new ArrayList<String>();
		tokens.add(deviceToken);
		String certificatePath = Path; // D:/ZshPush.p12
		String certificatePassword = Key; // "123456";// 此处注意导出的证书密码不能为空因为空密码会报错
		boolean sendCount = true;
		try {
			PushNotificationPayload payLoad = new PushNotificationPayload();
			payLoad.addAlert(alert); // 消息内容
			payLoad.addBadge(badge); // iphone应用图标上小红圈上的数值

			if (!StringUtils.isBlank(sound)) {
				payLoad.addSound(sound);// 铃音
			}
			PushNotificationManager pushManager = new PushNotificationManager();
			// true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
			pushManager
					.initializeConnection(new AppleNotificationServerBasicImpl(
							certificatePath, certificatePassword, false));
			List<PushedNotification> notifications = new ArrayList<PushedNotification>();
			// 发送push消息
			if (sendCount) {
				Device device = new BasicDevice();
				device.setToken(tokens.get(0));
				PushedNotification notification = pushManager.sendNotification(
						device, payLoad, true);
				notifications.add(notification);
			} else {
				List<Device> device = new ArrayList<Device>();
				for (String token : tokens) {
					device.add(new BasicDevice(token));
				}
				notifications = pushManager.sendNotifications(payLoad, device);
			}
			pushManager.stopConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 问题1：alias or registration_id
	// 问题2：alert 通知的内容
	public static void AndroidSend(String registration_id,String content) {
		String data = appKey + ":" + masterSecret;
		String basic = Base64.encodeBase64String(data.getBytes());
		String url = "https://api.jpush.cn/v3/push";
		// platform
		JSONObject param = new JSONObject();
		param.put("platform", "android");
		// audience
		JSONArray audience_reg_array = new JSONArray();
		audience_reg_array.add(registration_id);
		JSONObject audience_alias = new JSONObject();
		audience_alias.put("registration_id", audience_reg_array);
		param.put("audience", audience_alias);
		// notification
		JSONObject ntf_info = new JSONObject();
		ntf_info.put("alert", content);
		JSONObject ntf_android = new JSONObject();
		ntf_android.put("android", ntf_info);
		param.put("notification", ntf_android);
		HttpRequest.post(url, param.toString(), basic);
	}

	public static void main(String args[]) {
		MessageSend.AndroidSend("alist","content1231415");
	}
}