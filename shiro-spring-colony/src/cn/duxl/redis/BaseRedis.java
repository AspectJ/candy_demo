package cn.duxl.redis;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;


/**
 * redis 数据处理
 * @author Administrator
 * @create 2015-11-16 上午11:37:57
 */
@Component("baseRedis")
public class BaseRedis
{
	
	private static final Logger logger = LoggerFactory.getLogger(BaseRedis.class);
	
	/**
	 * Redis数据操作对象
	 */
	@Autowired
	protected JedisConnectionFactory jedisConnectionFactory;

	@Autowired
	protected RedisSerializer<String> stringSerializer;

	public RedisSerializer<String> getStringSerializer()
	{
		return stringSerializer;
	}

	public void setStringSerializer(RedisSerializer<String> stringSerializer)
	{
		this.stringSerializer = stringSerializer;
	}
	
	
	/**
	 * byte[]转换成string
	 */
	public String toString(byte[] bytes)
	{
		return stringSerializer.deserialize(bytes);
	}

	
	/**
	 * string转换成byte[]
	 */
	public byte[] toByte(String key)
	{
		return stringSerializer.serialize(key);
	}
	
	/**
	 * objectmap转换为bytemap
	 */
	public Map<byte[], byte[]> toByteMap(Map<String, Object> objectMap)
	{
		Map<byte[], byte[]> bmap = new HashMap<byte[], byte[]>();
		Set<String> keyset = objectMap.keySet();
		for (String key : keyset)
		{
			bmap.put(stringSerializer.serialize(key), stringSerializer.serialize(objectMap.get(key).toString()));
		}
		return bmap;
	}

	
	/**
	 * bytemap转换为stringmap
	 */
	public Map<String, String> toStringMap(Map<byte[], byte[]> bmap)
	{
		Map<String, String> StringMap = new HashMap<String, String>();
		Set<byte[]> keyset = bmap.keySet();
		for (byte[] key : keyset)
		{
			StringMap.put(stringSerializer.deserialize(key), stringSerializer.deserialize(bmap.get(key)));
		}
		return StringMap;
	}
	
	
	/**
	 * byte list转换为string list
	 */
	public List<String> toStringList(List<byte[]> blist)
	{
		List<String> strlist = new LinkedList<String>();
		for (byte[] bs : blist)
		{
			strlist.add(stringSerializer.deserialize(bs));	
		}
		return strlist;
	}
	
	
	/**
	 * set装byte数组
	 * @param strSet
	 * @return
	 */
	public  byte[][] toByteSet(Set<String> strSet)
	{
		byte[][] bset = new byte[strSet.size()][];
		int i=0;
		for (String str : strSet)
		{
			bset[i] = toByte(str);
			i++;
		}
		return bset;
	}
	
	
	/**
	 * set byte[]转list string
	 * @return
	 */
	public List<String> setToStrList(Set<byte[]> set){
		List<String> list = new LinkedList<String>();
		for (byte[] bytes : set)
		{
			list.add(toString(bytes));
		}
		return list;
	}
	
	/**
	 * =========================================================================================
	 * redis数据操作
	 * =========================================================================================
	 */
	/**
	 * 查询Map的字段信息
	 * 
	 * @param database
	 * @param key
	 * @param fieldsByte
	 * @return
	 */
	public String queryMapField(int database, String key, String field)
	{
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);
			byte[] value = conn.hGet(toByte(key), toByte(field));
			conn.close();

			if (value != null)
			{
				return toString(value);
			} else
			{
				return null;
			}
		} catch (Exception e)
		{
			logger.error("查询Map的字段信息");
			return null;
		}
	}

	
	/**
	 * 查询Map的字段信息
	 * 
	 * @param database
	 * @param key
	 * @param fieldsByte
	 * @return
	 */
	public List<String> queryMapFields(int database, String key, byte[]... fieldsByte)
	{
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);

			byte[] keyByte = toByte(key);

			List<String> fieldValuelist = toStringList(conn.hMGet(keyByte, fieldsByte));
			conn.close();
			return fieldValuelist;
		} catch (Exception e)
		{
			logger.error("查询Map的字段信息");
		}
		return null;
	}
	
	/**
	 * redis的keys操作
	 * @param database
	 * @param key
	 * @return
	 */
	public Set<byte[]> keys(int database, String key) {
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);

			byte[] id = toByte(key);
			Set<byte[]> set = conn.keys(id);
			conn.close();
			
			return set;
		} catch (Exception e)
		{
			logger.error("redis的keys操作");
		}
		return null;
	}

	
	/**
	 * 修改map字段
	 * 
	 * @param database
	 * @param key
	 * @param field
	 * @param value
	 */
	protected void updateMapField(int database, String key, String field, String value)
	{
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);
			conn.hSet(toByte(key), toByte(field), toByte(value));
			conn.close();
		} catch (Exception e)
		{
			logger.error("Redis-修改map字段");
		}
	}
	
	
	/**
	 * redis删除key操作
	 * @param database
	 * @param key
	 */
	public void removeKey(int database, String key)
	{
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);
			conn.del(toByte(key));
			conn.close();
		} catch (Exception e)
		{
			logger.error("Redis-移除key");
		}
	}
	
	/**
	 * redis删除key操作
	 * @param database
	 * @param key
	 */
	public void removeByteKey(int database, byte[] key)
	{
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);
			conn.del(key);
			conn.close();
		} catch (Exception e)
		{
			logger.error("Redis-移除key");
		}
	}
	
	
	/**
	 * 字符串设置
	 * @param key
	 * @param value
	 */
	public void setStrValue(int database, String key, String value, long expire){
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);
			conn.set(toByte(key), toByte(value));
			if(expire > 0){
				conn.expire(toByte(key), expire);
			}
			conn.close();
		} catch (Exception e)
		{
			logger.error("Redis-移除key");
			
		}
	}
	
	
	/**
	 * byte设置
	 * @param key
	 * @param value
	 */
	public void setByteValue(int database, byte[] key, byte[] value, long expire){
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);
			conn.set(key, value);
			if(expire > 0){
				conn.expire(key, expire);
			}
			conn.close();
		} catch (Exception e)
		{
			logger.error("Redis-移除key");
		}
	}
	
	/**
	 * 获取字符串
	 * @param key
	 * @param value
	 */
	public String getStrValue(int database, String key){
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);
			String value = toString(conn.get(toByte(key)));
			conn.close();
			return value;
		} catch (Exception e)
		{
			logger.error("Redis-移除key");
		}
		return null;
	}
	
	
	/**
	 * 获取字符串
	 * @param key
	 * @param value
	 */
	public String getByteValue(int database, byte[] key){
		try
		{
			RedisConnection conn = jedisConnectionFactory.getConnection();
			conn.select(database);
			String value = toString(conn.get(key));
			conn.close();
			return value;
		} catch (Exception e)
		{
			logger.error("Redis-移除key");
		}
		return null;
	}
}
