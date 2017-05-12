package com.cp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis处理公共类
 * 
 * @author Administrator
 * @create 2015-9-29 下午4:57:58
 */
public class RedisCodeUtil
{
	/**
	 * redis序列化
	 */
	private static RedisSerializer<String> strSeria = new StringRedisSerializer();

	/**
	 * byte[]转换成string
	 */
	public static String toString(byte[] bytes){
		return strSeria.deserialize(bytes);
	}
	
	/**
	 * string转换成byte[]
	 */
	public static byte[] toByte(String key){
		return strSeria.serialize(key);
	}
	
	/**
	 * objectmap转换为bytemap
	 */
	public static Map<byte[], byte[]> toByteMap(Map<String, Object> objectMap)
	{
		Map<byte[], byte[]> bmap = new HashMap<byte[], byte[]>();
		Set<String> keyset = objectMap.keySet();
		for (String key : keyset)
		{
			bmap.put(strSeria.serialize(key), strSeria.serialize(objectMap.get(key).toString()));
		}
		return bmap;
	}

	/**
	 * bytemap转换为stringmap
	 */
	public static Map<String, String> toStringMap(Map<byte[], byte[]> bmap)
	{
		Map<String, String> StringMap = new HashMap<String, String>();
		Set<byte[]> keyset = bmap.keySet();
		for (byte[] key : keyset)
		{
			StringMap.put(strSeria.deserialize(key), strSeria.deserialize(bmap.get(key)));
		}
		return StringMap;
	}
}
