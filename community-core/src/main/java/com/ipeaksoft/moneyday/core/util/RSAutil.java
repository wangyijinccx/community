package com.ipeaksoft.moneyday.core.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

public class RSAutil {

	public static final String publicKey = "/mnt/key/rsa_public_key_xiguamei_union.pem";
	public static final String privateKey = "/mnt/key/rsa_private_key_xiguamei_union.pkcs8.pem";
    
	/**
	 * 从文件中加载私钥
	 * 
	 * @param keyFileName
	 *            私钥文件名
	 * @return 是否成功
	 * @throws Exception
	 */
	public static String loadPrivateKeyByFile() throws Exception {
		try {
			BufferedReader br = new BufferedReader(new FileReader(privateKey));
			String readLine = null;
			StringBuilder sb = new StringBuilder();
			while ((readLine = br.readLine()) != null) {
				// sb.append(readLine);

				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			throw new Exception("私钥数据读取错误");
		} catch (NullPointerException e) {
			throw new Exception("私钥输入流为空");
		}
	}

	/**
	 * RSA签名
	 * 
	 * @param content
	 *            待签名数据
	 * @param privateKey
	 *            商户私钥
	 * @return 签名值
	 */
	public static String sign(String content) {
		try {
			String privateKey = RSAutil.loadPrivateKeyByFile();
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decodeBase64(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			Signature signature = Signature.getInstance("MD5withRSA");
			signature.initSign(priKey);
			signature.update(content.getBytes());
			byte[] signed = signature.sign();
			return Base64.encodeBase64String(signed);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		// byte[] decBytes = decrypt();
		// String dec = new String(decBytes);

		// System.out.println("用私钥加密后的结果是:" + dec);
		String content = "calvin123456";
		// byte[] encBytes = RSAutil.encrypt(message.getBytes());
		// byte[] cc = Base64.encodeBase64(encBytes);
		// System.out.println(URLEncoder.encode(new String(cc)));
		String signstr = RSAutil.sign(content);
		System.out.println("签名原串：" + content);
		System.out.println("签名串：" + signstr);
		//byte[] by = Base64.encodeBase64(signstr.getBytes());
		System.out.println("sign=" + URLEncoder.encode(signstr));

	}
}