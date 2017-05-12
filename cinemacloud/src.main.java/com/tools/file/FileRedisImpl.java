package com.tools.file;

import org.springframework.stereotype.Service;

import com.cp.filter.BaseRedis;

@Service("fileRedis")
public class FileRedisImpl extends BaseRedis
{
	
	/**
	 * 设置七牛token
	 * @param token
	 */
	public void setQNToken(String token){
		setStrValue(3, "cinamepay:qiniu:token", token, 0);
	}
	
	
	/**
	 * 设置七牛token
	 * @param token
	 */
	public String getQNToken(){
		return getStrValue(3, "cinamepay:qiniu:token");
	}
}
