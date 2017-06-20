package com.ipeaksoft.moneyday.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.*;

import org.apache.commons.codec.binary.Base64;

public class RSAutil {

	public static final String publicKey = "C:/Users/sjk/Desktop/bat2/rsa_public_key_xiguamei_union.pem";
	public static final String privateKey = "C:/Users/sjk/Desktop/bat2/rsa_private_key_xiguamei_union.pkcs8.pem";

	/**
	 * 公钥加密（每次都读文件吧，以后优化）
	 * @param srcBytes
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] srcBytes) throws Exception {
		File file = new File(publicKey);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer("");
		String tempString = null;
		// 一次读入一行，直到读入null为文件结束
		while ((tempString = reader.readLine()) != null) {

			if (tempString.charAt(0) == '-') {
				continue;
			} else {
				sb.append(tempString);
				sb.append('\r');
			}
		}
		reader.close();

		// BASE64Decoder base64Decoder = new BASE64Decoder();
		// byte[] buffer = base64Decoder.decodeBuffer(sb.toString());
		byte[] buffer = Base64.decodeBase64(sb.toString().getBytes("UTF-8"));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
		RSAPublicKey pbk = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		if (pbk != null) {
			try {
				// Cipher负责完成加密或解密工作，基于RSA
				Cipher cipher = Cipher.getInstance("RSA");

				// 根据公钥，对Cipher对象进行初始化
				cipher.init(Cipher.ENCRYPT_MODE, pbk);

				// 加密，结果保存进resultBytes，并返回
				byte[] resultBytes = cipher.doFinal(srcBytes);

				return resultBytes;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 私钥解密
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt() throws Exception {
		String message = "calvin123456";
		byte[] encBytes = RSAutil.encrypt(message.getBytes());
		File file = new File(privateKey);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuffer sb = new StringBuffer("");
		String tempString = null;
		// 一次读入一行，直到读入null为文件结束
		while ((tempString = reader.readLine()) != null) {

			if (tempString.charAt(0) == '-') {
				continue;
			} else {
				sb.append(tempString);
				sb.append('\r');
			}
		}
		reader.close();

		// BASE64Decoder base64Decoder= new BASE64Decoder();
		// byte[] buffer= base64Decoder.decodeBuffer(sb.toString());
		byte[] buffer = Base64.decodeBase64(sb.toString().getBytes("UTF-8"));
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		RSAPrivateKey prk = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

		if (prk != null) {
			try {
				Cipher cipher = Cipher.getInstance("RSA");

				// 根据私钥对Cipher对象进行初始化
				cipher.init(Cipher.DECRYPT_MODE, prk);

				// 解密并将结果保存进resultBytes
				byte[] decBytes = cipher.doFinal(encBytes);
				return decBytes;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		//byte[] decBytes = decrypt();
		//String dec = new String(decBytes);

		//System.out.println("用私钥加密后的结果是:" + dec);
		String message = "calvin123456";
		byte[] encBytes = RSAutil.encrypt(message.getBytes());
		byte[] cc = Base64.encodeBase64(encBytes);
		System.out.println(URLEncoder.encode(new String(cc)));
	}
}