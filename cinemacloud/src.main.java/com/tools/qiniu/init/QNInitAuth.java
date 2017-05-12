package com.tools.qiniu.init;

import com.qiniu.util.Auth;

public class QNInitAuth
{
	
	public static Auth auth;
	
	/**
	 * 获取七牛的token
	 * (简单上传，使用默认策略，只需要设置上传的空间名就可以了)
	 * 
	 * @param bucketname 要上传的空间
	 * @return
	 */
	  public static String getUpToken(String bucketname){
	      return auth.uploadToken(bucketname);
	  }
	  
	  
	  /**
	   * 获取七牛的token
	   * (覆盖上传)
	   * 
	   * @param bucketname 要上传的空间
	   * @param key
	   * @return
	   */
	  public static String getUpToken(String bucketname,String key){
		  return auth.uploadToken(bucketname,key);
	  }
	
	  
	/**
	 * 初始化
	 * @param accessKey
	 * @param secretKey
	 */
	public static void initAuth(String accessKey, String secretKey){
		auth = Auth.create(accessKey, secretKey);
	}
}
