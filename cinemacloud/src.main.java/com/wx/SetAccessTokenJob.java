package com.wx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mongo.MyMongo;
import com.redis.UserRedisImpl;

import net.sf.json.JSONObject;

/**
 * 定时任务 获取微信企业号token，每隔1小时50分获取一次
 * 
 * @author 29632
 *
 */
public class SetAccessTokenJob extends QuartzJobBean {

	@Autowired
	private UserRedisImpl userRedis;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		HttpURLConnection connection = null;
		String access_token=null;
		URL url;
		try {
			url = new URL(WXTools.GET_TOKEN_URL);
		
		connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Accept-Charset", "utf-8");
		connection.setRequestProperty("contentType", "utf-8");
		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.connect();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		String str = reader.readLine();
		StringBuffer resultStr = new StringBuffer();
		while (str != null)
		{
			resultStr.append(str);
			str = reader.readLine();
		}

		// logger.info("通过component_verify_ticket获取api_component_token,返回数据:{}", resultStr);
		if (resultStr != null && resultStr.length() > 0)
		{
			JSONObject access_tokenJson = JSONObject.fromObject(resultStr.toString());
			access_token = access_tokenJson.getString("access_token");
			userRedis.setWXToken("access_token", access_token);
			MyMongo.mLog("INFO", "微信,2-获取access_token", access_token);
			}
		} catch (MalformedURLException e)
		{
			MyMongo.mErrorLog("微信,2-获取access_token", e);
		} catch (IOException e)
		{
			MyMongo.mErrorLog("微信,2-获取access_token", e);
		} catch (Exception e)
		{
			MyMongo.mErrorLog("微信,2-获取access_token", e);
		}
		
		
	}
}
