package com.ipeaksoft.moneyday.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

	private static Logger logger = LoggerFactory.getLogger(HttpRequest.class);

	// 发送POST请求，参数为form表单形式
	public static String postForm(String url, Map<String, Object> params) {
		String result = null;
		StringBuilder buffer = new StringBuilder();
		try {
			HttpClient client = new HttpClient();
			PostMethod method = new PostMethod(url);
			for (Entry<String, Object> entry : params.entrySet()) {
				method.addParameter(entry.getKey(), entry.getValue().toString());
				buffer.append(entry.getKey() + "="
						+ entry.getValue().toString() + "&");
			}
			HttpMethodParams param = method.getParams();
			param.setContentCharset("UTF-8");
			client.executeMethod(method);
			// 打印返回的信息
			result = method.getResponseBodyAsString();
			// 释放连接
			method.releaseConnection();
		} catch (IllegalArgumentException e) {
		} catch (HttpException e) {
		} catch (IOException e) {
		}

		if (buffer.toString().length() <= 1000) {
			logger.info("post url:{}, param:{}, result:{}", url,
					buffer.toString(), result);
		}
		return result;

	};

	public static String sendHttpRequest(String sUrl, String method,
			String encoding) {
		HttpURLConnection con = null;
		ByteArrayOutputStream baos = null;
		InputStream is = null;
		String sResult = "";
		try {
			URL url = new URL(sUrl);

			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(method);
			con.setReadTimeout(1000 * 60 * 5);

			baos = new ByteArrayOutputStream();
			is = con.getInputStream();
			int ch;
			byte[] tmp = new byte[1024];
			while ((ch = is.read(tmp)) != -1) {
				baos.write(tmp, 0, ch);
			}

			if (encoding != null)
				sResult = new String(baos.toByteArray(), encoding);
			else
				sResult = new String(baos.toByteArray());
		} catch (Exception e) {
			logger.error("ERROR: ", e);
			sResult = "{\"code\":500,\"msg\":\"" + e.getMessage() + "\"}";
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (con != null)
				con.disconnect();
		}

		if (sResult.length() <= 1000) {
			logger.info("get to url:{}, result:{}", sUrl, sResult);
		}
		return sResult;
	}

	public static String sendHttpRequest(String sUrl, String method,
			String encoding, Map<String, String> header) {
		HttpURLConnection con = null;
		ByteArrayOutputStream baos = null;
		InputStream is = null;
		String sResult = "";
		try {
			URL url = new URL(sUrl);

			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(method);
			con.setReadTimeout(1000 * 60 * 5);

			if (header != null) {
				Iterator<String> iter = header.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();
					con.setRequestProperty(key, header.get(key));
				}
			}

			baos = new ByteArrayOutputStream();
			is = con.getInputStream();
			int ch;
			byte[] tmp = new byte[1024];
			while ((ch = is.read(tmp)) != -1) {
				baos.write(tmp, 0, ch);
			}

			if (encoding != null)
				sResult = new String(baos.toByteArray(), encoding);
			else
				sResult = new String(baos.toByteArray());

			if (sResult.length() <= 1000) {
				logger.debug("request to url:{}, result:{}", sUrl, sResult);
			}
		} catch (SocketException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (con != null)
				con.disconnect();
		}
		return sResult;
	}

	public static void post(String url, String json, String basic) {
		long time = System.currentTimeMillis();
		final String CONTENT_TYPE_TEXT_JSON = "text/json";
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String result = null;
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
			httpPost.setHeader("Authorization", "Basic basic");
			StringEntity se = new StringEntity(json);
			se.setContentType(CONTENT_TYPE_TEXT_JSON);
			httpPost.setEntity(se);
			response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, "UTF-8");

			}
		} catch (Exception e) {
			logger.error("ERROR:{}", url, e);
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
			}
		}
		logger.info("POST to:{}, content:{}, result:{}, time:{},code:{}", url,
				json, result, (System.currentTimeMillis() - time), response
						.getStatusLine().getStatusCode());
	}
}
