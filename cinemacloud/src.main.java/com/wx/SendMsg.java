package com.wx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mongo.MyMongo;

import net.sf.json.JSONObject;

public class SendMsg {
	
	/**
	 * 推送微信消息
	 * @param userids 用户组。格式："userid|userid|userid"
	 * @param access_token 
	 * @param title 推送标题，如回盘通知，物料分发通知
	 * @param description 发送内容，换用用\n
	 * @param redirectUrl 跳转页面连接。
	 */
	public String sendMsg(String userids,String access_token,String title,String description,String redirectUrl){
		HttpURLConnection connection = null;
		StringBuffer resultStr=new StringBuffer();
		try{
			
		URL url=new URL("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+access_token);
			
		JSONObject contentjson=new JSONObject();
		contentjson.put("title", title);
		contentjson.put("description",description);
		contentjson.put("url",redirectUrl);
		
		List<JSONObject> content=new ArrayList<JSONObject>();
		content.add(contentjson);
		JSONObject articlesjson=new JSONObject();
		articlesjson.put("articles", content.toString());
		
		JSONObject paramjson = new JSONObject();
		paramjson.put("touser", userids);
		paramjson.put("msgtype", "news");
		paramjson.put("agentid", 0);
		paramjson.put("news", articlesjson);
		
		connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestProperty("Accept-Charset", "utf-8");
		connection.setRequestProperty("contentType", "utf-8");
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.connect();

		OutputStream out = connection.getOutputStream();
		out.write(paramjson.toString().getBytes());
		out.flush();
		out.close();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		String str = reader.readLine();
		while (str != null)
		{
			resultStr.append(str);
			str = reader.readLine();
		}
		
		MyMongo.mLog("INFO", "微信,2-发送"+title+"消息", title);
		} catch (MalformedURLException e)
		{
			MyMongo.mErrorLog("微信,2-发送"+title+"消息", e);
		} catch (IOException e)
		{
			MyMongo.mErrorLog("微信,2-发送"+title+"消息", e);
		} catch (Exception e)
		{
			MyMongo.mErrorLog("微信,2-发送"+title+"消息", e);
		}
		return resultStr.toString();
		
		
	}
	
	public static void main(String[] args) {
		/*SendMsg s=new SendMsg();
		String access_token="wsEDwbMgUbBDQyf-fI1xNWQpqfj5cM24FworokLimlyYJrhGVoSD7wqdZDaxiMRA";
		s.sendMsg("296328516", access_token, "回盘通知", "fdsfdsfdsf\nfdsfdsfa", "www.baidu.com");
		*/
		
		String userid="fdsafdsa,fdsfds";
		userid=userid.replace(",", "|");
		System.out.println(userid);
	
	}

}
