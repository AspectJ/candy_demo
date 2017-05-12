package com.cp.util;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;


import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

public class DesUtil
{

	public static void main(String[] args) throws Exception
	{
		//String data = "9630852074101230";
		//cardnumber1:f682444395c742c1;cardcipher1:990285a299394bdf;desCardCipher1:lq1XveDRopTMUfhav0Q+Ge/m0R3ccyoM
		//String data = "lq1XveDRopTMUfhav0Q+Ge/m0R3ccyoM";
//		String key = "46778292";
//		System.err.println(desEncrypt(data, key));//加密
//		System.err.println(desDncrypt(desEncrypt(data, key), key));//解密
//		System.err.println(desDncrypt(data, key));//解密
//		
//		String daxie = "f682444395c742c1";
//		String mima = "990285a299394bdf";
//		System.err.println(daxie.toUpperCase());
//		System.err.println(mima.toUpperCase());
//		System.err.println(desEncrypt(mima.toUpperCase(), key));

	}
	
	/** 
     * 解密c#的des加密串 
     * @param message 
     * @param key 
     * @return 
     * @throws Exception 
     */ 
    public static String desDncrypt(String message, String key) throws Exception { 
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding"); 
         
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8")); 
         
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec); 
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8")); 
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv); 
        byte[] retByte = cipher.doFinal(new BASE64Decoder().decodeBuffer(message));  
        return new String(retByte); 
    } 
    
    /**
     * 加密c#的des加密串 
     * @param message
     * @param key
     * @return
     * @throws Exception
     */
    public static String desEncrypt(String message, String key) throws Exception { 
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding"); 
 
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8")); 
 
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec); 
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8")); 
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv); 
        
        return new BASE64Encoder().encode(cipher.doFinal(message.getBytes("UTF-8")));
    } 
}