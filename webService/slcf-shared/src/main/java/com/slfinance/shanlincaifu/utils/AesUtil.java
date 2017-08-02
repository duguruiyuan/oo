package com.slfinance.shanlincaifu.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author zhangt
 * @version $Revision:1.0.0, $Date: 2015年10月22日下午4:54:36 $ 
 */
public class AesUtil {

	/**
	 * AES-128-CBC 加密
	 * 
	 * @author zhangt
	 * @date   2015年10月22日下午3:58:36
	 * @param key
	 * @param iv
	 * @param content
	 * @return
	 */
	public static byte[] cbc128Encrypt(String key, String iv, String content) {

		byte[] encrypted = null;
		try {
			IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"),"AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
			encrypted = cipher.doFinal(content.getBytes());			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return encrypted;
	}
	
	/**
	 * 将二进制加密串转为16进制加密串，由吉融通提供
	 * @author zhangt
	 * @date   2015年10月22日下午4:14:55
	 * @param bytes
	 * @return
	 */
	public static String encodeBytes(byte[] bytes) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
			strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
		}
		return strBuf.toString();
	}
	
}
