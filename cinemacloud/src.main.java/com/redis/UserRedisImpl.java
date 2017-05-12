package com.redis;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.stereotype.Service;

import com.cp.filter.BaseRedis;
import com.cp.util.RedisCodeUtil;

@Service("userRedis")
public class UserRedisImpl extends BaseRedis{

	/**
	 * 微信授权码
	 * 
	 * @param string
	 * @param access_token
	 */
	public void setWXToken(String tokenname, String access_token)
	{
		RedisConnection conn = jedisConnectionFactory.getConnection();
		conn.select(0);
		byte[] key = RedisCodeUtil.toByte("cinemacloud:weix:" + tokenname);
		conn.set(key, RedisCodeUtil.toByte(access_token));
		conn.close();
	}

	/**
	 * 微信授权码
	 * 
	 * @param string
	 * @param api_component_token
	 */
	public String getWXToken()
	{
		RedisConnection conn = jedisConnectionFactory.getConnection();
		conn.select(0);
		byte[] key = RedisCodeUtil.toByte("cinemacloud:weix:access_token");
		String token = RedisCodeUtil.toString(conn.get(key));
		conn.close();
		return token;
	}

}
