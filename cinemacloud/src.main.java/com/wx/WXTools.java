package com.wx;

import com.cp.exception.CustomException;
import com.mongo.MyMongo;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("wxTools")
public class WXTools {
	

	//企业号唯一标识
	public static final String CORPID="wxd9d43b512d86cf6d";
	//管理组凭证密码
	public static final String SECRET="p0QtbfvUbfliGA-gl6czwATUJxuCI-0VZnzT3SvW-0jfJ-oRRNloKrpK7x4NeN-2";

	/*//企业号唯一标识
	public static final String CORPID="wx14d59bae85c815fc";
	//管理组凭证密码
	public static final String SECRET="tLw1sLQdddjoGj5EOM64d2OT5MQxkND8aVYWzbnydLopoaGRoXws23srSOKbebDl";*/

	//获取token的url
	public static final String GET_TOKEN_URL="https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="+CORPID+"&corpsecret="+SECRET;


		
	/**
	 * 推送微信消息
	 * @param userids 用户组。格式："userid|userid|userid"
	 * @param access_token 
	 * @param title 推送标题，如回盘通知，物料分发通知
	 * @param description 发送内容，换用用\n
	 * @param redirectUrl 跳转页面连接。
	 */
	public  String sendMsg(String userids,String access_token,String title,String description,String redirectUrl,String state) throws CustomException {
		HttpURLConnection connection = null;
		StringBuffer resultStr=new StringBuffer();
		try{
			
		String url="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+access_token;
		
		//redirect_uri:授权后重定向的回调链接地址，请使用urlencode对链接进行处理
		//response_type		返回类型，此时固定为：code
		//scope		应用授权作用域，此时固定为：snsapi_base
		//state	 可以不带此参数	重定向后会带上state参数，企业可以填写a-zA-Z0-9的参数值，长度不可超过128个字节 
		//#wechat_redirect		微信终端使用此参数判断是否需要带上身份信息
		redirectUrl="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+CORPID
				+"&redirect_uri="+redirectUrl
				+"&response_type=code&scope=snsapi_base&state="+state+"#wechat_redirect";
			
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
		
		connection =getConnection(url, "POST");

		PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "utf-8"));
		out.write(paramjson.toString());
		out.flush();
		out.close();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		String str = reader.readLine();
		while (str != null)
		{
			resultStr.append(str);
			str = reader.readLine();
		}
		JSONObject resultjson=JSONObject.fromObject(resultStr.toString());
		Object errcode=resultjson.get("errcode");
		Object errmsg=resultjson.get("errmsg");
		if(errcode.equals(0) && "ok".equals(errmsg)){
			MyMongo.mLog("INFO", "微信,2-发送"+title+"消息成功", title);
		}else{
			MyMongo.mLog("INFO", "微信,2-发送"+title+"消息失败", title);
			throw new CustomException("发送微信通知失败");
		}
		
		} catch (MalformedURLException e)
		{
			MyMongo.mErrorLog("微信,2-发送"+title+"消息", e);
			throw new CustomException("发送微信通知失败");
		} catch (IOException e)
		{
			MyMongo.mErrorLog("微信,2-发送"+title+"消息", e);
			throw new CustomException("发送微信通知失败");
		} catch (Exception e)
		{
			MyMongo.mErrorLog("微信,2-发送"+title+"消息", e);
			throw new CustomException("发送微信通知失败");
		}
		return resultStr.toString();
		
	}
	
	/**
	 * 获取微信部门成员
	 * @param access_token 微信授权码
	 * @param department_id	部门id
	 * @param status	0获取全部成员，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表。status可叠加，未填写则默认为4
	 * @param fetch_child 1/0：是否递归获取子部门下面的成员
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  Map<Object, Object> getUser(String access_token,int department_id,int status,int fetch_child){
		
		HttpURLConnection connection=null;
		Map<Object,Object> usermap=new HashMap<Object,Object>();
		StringBuffer resultStr=new StringBuffer();
		String url="https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token="+access_token
					+"&department_id="+department_id
					+"&fetch_child="+fetch_child
					+"&status="+status;
		try {
			connection=getConnection(url, "GET");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			String str = reader.readLine();
			while (str != null)
			{
				resultStr.append(str);
				str = reader.readLine();
			}
			JSONObject resultjson=JSONObject.fromObject(resultStr.toString());
			List<JSONObject> userlist=(List<JSONObject>) resultjson.get("userlist");
			if(userlist!=null && userlist.size()>0){
				for(JSONObject user : userlist){
					usermap.put(user.get("userid"), user.get("name"));
				}
			}
			
		} catch (IOException e) {
			MyMongo.mErrorLog("微信，获取微信成员", e);
		}
		return usermap;
	}
	
	/**
	 * 根据code获取成员信息
	 * @param access_token
	 * @param code
	 * @return
	 * @throws IOException
	 */
	public  String getUserIdByCode(String access_token,String code) throws IOException{
		String userid;
		String url="https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token="+access_token+"&code="+code;
		HttpURLConnection connection=getConnection(url, "GET");
		StringBuffer resultStr=new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		String str = reader.readLine();
		while (str != null)
		{
			resultStr.append(str);
			str = reader.readLine();
		}
		JSONObject resultjson=JSONObject.fromObject(resultStr.toString());
		if(resultjson.containsKey("UserId")){
			userid=(String) resultjson.get("UserId");
			return userid;
		}else{
			return null;
		}
		
	}
	
	
	/**
	 * 创建连接
	 * @param url 请求地址
	 * @param method 请求方式：GET,POST
	 * @return
	 * @throws IOException
	 */
	private   HttpURLConnection getConnection(String url,String method) throws IOException{
		
		HttpURLConnection connection=null;
		URL u=new URL(url);
		connection=(HttpURLConnection) u.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		/*connection.setRequestProperty("Accept-Charset", "utf-8");
		connection.setRequestProperty("contentType", "utf-8");*/
		connection.setRequestMethod(method);
		connection.setRequestProperty("Accept-Charset", "utf-8");
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		connection.connect();
		return connection;
	}
	
	public String getAccess_token(){
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
		
		return access_token;
	}

}
