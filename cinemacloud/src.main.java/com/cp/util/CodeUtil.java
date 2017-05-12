package com.cp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 代码处理工具类
 * @author stone 2015-4-23下午6:16:54
 */
public class CodeUtil
{
	private static final Logger logger = LoggerFactory.getLogger(CodeUtil.class);

	/**
	 * 将Unicode编码转换为汉字
	 * 
	 * @param utfString
	 * @return
	 */
	public static String convert(String utfString)
	{
		StringBuilder sb = new StringBuilder();
		int i = -1;
		int pos = 0;

		while ((i = utfString.indexOf("\\u", pos)) != -1)
		{
			sb.append(utfString.substring(pos, i));
			if (i + 5 < utfString.length())
			{
				pos = i + 6;
				sb.append((char) Integer.parseInt(utfString.substring(i + 2, i + 6), 16));
			}
		}
		if (sb.length() == 0)
		{
			return utfString;
		}

		return sb.toString();
	}
	
	/**
	 * 判断是不是数字
	 * @param 传入的字符串
	 * @return 
	 */
	public static boolean isNumeric(String str){
		if(str!=null){
			str = str.trim();
		  for (int i = str.length();--i>=0;){   
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }		
		  return true;
		}else{
			return false;
		}
		 }

	/**
	 * 订单号 (ydporder:ordernumber -- 给用户显示的订单号) 由接入商自定义，一天内不能重复，并且不能大于13位的长度。可由数字、字母组合而成。
	 * @param isOrder 退款传false
	 * @return
	 */
	public static String getOrderNo(String pk_id, boolean isOrder)
	{
		StringBuffer sb_random = new StringBuffer();
		long max = (long) Math.pow(10, pk_id.length());
		sb_random.append(randomNum(max, max * 10));

		int index = sb_random.length();

		if (isOrder)
		{
			sb_random.append("0");
		} else
		{
			sb_random.append("1");
		}
		
		for (int i = 0; i < pk_id.length(); i++)
		{
			sb_random.insert(index, pk_id.substring(i, i + 1));
			index--;
		}
		
		return sb_random.toString();
	}
	
	
	/**
	 * 将十六进制数字字符串转为BYTE
	 * 
	 * @param str
	 *            长度为2的十六进制数字字符串 如"FF"、"12"
	 * @return
	 */
	public static byte str2bytes(String str) {
		return (byte) Integer.parseInt(str, 16);
	}
	
	/**
	 * 获得要解析的原始数据
	 */
	public static byte[] getContent(String strData) {
		byte[] content = {};
		if (strData != null) {
			content = new byte[strData.length() / 2];
			for (int i = 0; i < (strData.length() / 2); i++) {
				content[i] = CodeUtil.str2bytes(strData.substring(2 * i, 2 * i + 2));
			}
		}
		return content;
	}
	
	public static void main(String[] args)
	{
		byte[] a = CodeUtil.getContent("AA55");
		System.out.println(a.length);
	}

	/**
	 * 用户id 随机字符串
	 * 
	 * @return
	 */
	public static String getRandomUUID(int length)
	{
		String serialNum = UUID.randomUUID().toString().replace("-", "");
		if (length < 32)
		{
			serialNum = serialNum.substring(0, length);
		}
		return serialNum;
	}

	/**
	 * 随机数字
	 * @param max 最大值
	 * @param min 最小值
	 * @return 返回值介于 min - max 之间
	 */
	public static String randomNum(long min, long max)
	{
		long randNum = (long) (Math.random() * max);
		while (randNum < min)
		{
			randNum = (long) (Math.random() * max);
		}
		return String.valueOf(randNum);
	}

	/**
	 * 验证时间戳是否超时
	 * 
	 * @param now
	 */
	public static boolean verityTimeStamp(String now)
	{
		try
		{
			if (System.currentTimeMillis() - Long.parseLong(now) > 1200000)
			{
				return false;
			}
		} catch (Exception e)
		{
			logger.error("", e);
		}

		return true;
	}

	/**
	 * 字节数组转换成十六进制的字符串
	 */
	public static String getString(int content)
	{
		// 测试:原始数据输出
		String hexString = "0123456789ABCDEF";
		StringBuilder sb = new StringBuilder(2);
		sb.append(hexString.charAt((content & 0xf0) >> 4));
		sb.append(hexString.charAt((content & 0x0f) >> 0));
		return sb.toString();
	}

	/**
	 * 检验参数是否为空
	 * 
	 * @param param
	 * @return
	 */
	public static boolean checkParam(String... params)
	{
		boolean lackParam = false;
		for (String param : params)
		{
			if (param == null || param.trim().isEmpty() ||"".equals(param) || "null".equals(param) || "undefined".equals(param))
			{
				lackParam = true;
			}
		}
		return lackParam;
	}

	/**
	 * 获取几位小数
	 */
	public static float decimalsFloat(float original)
	{
		return original * 100 / 100;
	}

	/**
	 * 获取短信验证码
	 */
	public static String getSMSVerify()
	{
		// return "123456";
		return randomNum(100000, 1000000);
	}

	private static List<String> SimplePass = new LinkedList<String>();
	static
	{
		SimplePass.add("000000");
		SimplePass.add("111111");
		SimplePass.add("222222");
		SimplePass.add("333333");
		SimplePass.add("444444");
		SimplePass.add("555555");
		SimplePass.add("666666");
		SimplePass.add("777777");
		SimplePass.add("888888");
		SimplePass.add("999999");
		SimplePass.add("123456");
		SimplePass.add("654321");
	}

	/**
	 * 验证密码的复杂度
	 */
	public static boolean verifyPassComplex(String cardpass)
	{
		boolean flag = true;
		if (SimplePass.contains(cardpass))
		{
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 字符串长度不够前面或后面补0
	 * @param str
	 * @param strLength
	 * @return
	 */
	public static String addZeroForNum(String str, int strLength)
	{
		int strLen = str.length();
		if (str.length() == strLength)
		{
		} else if (str.length() < strLength)
		{
			StringBuffer sb = null;
			while (strLen < strLength)
			{
				sb = new StringBuffer();
				// sb.append("0").append(str);// 左(前)补0
				sb.append(str).append("0");// 右(后)补0
				str = sb.toString();
				strLen = str.length();
			}
		} else
		{
			str = str.substring(0, strLength);
		}
		return str;
	}
	
	/**
	 * 传值
	 * @param param 参数名称
	 * @param defultV 默认值
	 */
	public static void jsonPutVlue(JSONObject json, Map<String, String> map, String param, String feildName, String defultV) {
		if (checkMapValue(map, param))
		{
			json.put(feildName, map.get(param));
		} else
		{
			if (defultV != null)
			{
				json.put(feildName, defultV);
			}
		}
	}
	
	/**
	 * 传值
	 * 
	 * @param param 参数名称
	 * @param defultV 默认值
	 */
	public static void jsonPutVlue(JSONObject json, Map<String, String> map, String param, String defultV) {
		if (checkMapValue(map, param))
		{
			json.put(param, map.get(param));
		} else
		{
			if (defultV != null)
			{
				json.put(param, defultV);
			}
		}
	}
	
	/**
	 * 验证集合map是否为空
	 * @param params
	 * @return
	 */
	public static boolean checkMapValue(Map<String, String> map, String param) {
		boolean flag = true;
		if (map == null || !map.containsKey(param) || "".equals(map.get(param).toString())
				|| "null".equals(map.get(param).toString()))
		{
			flag = false;
		}
		return flag;
	}
	
	
	/**
	 * 获取异常的字符串信息
	 * @return
	 */
	public static String getErrorMessage(Exception e)
	{
		ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
		e.printStackTrace(new java.io.PrintWriter(buf, true));
		String expMessage = buf.toString();
		try
		{
			buf.close();
		} catch (IOException e1)
		{}
		return expMessage;
	}
	
	/**
	 * 用户状态转换
	 * @param status
	 * @return
	 */
	public static String convertUserStatus(String status){
		String result = "";
		if(status.equals("0")){//0、启用；1、禁用
			result = "启用";
		}else if(status.equals("1")){
			result = "禁用";
		}
		return result;
	}
	
	/**
	 * 角色类型转换
	 * @param status
	 * @return
	 */
	public static String convertRoleType(String type){
		String result = "";
		if(type.equals("1")){//1:业务角色;2:管理角色 ;3:系统内置角色
			result = "业务角色";
		}else if(type.equals("2")){
			result = "管理角色";
		}else if(type.equals("3")){
			result = "系统内置角色";
		}else if(type.equals("100")){
			result = "影投角色";
		}
		return result;
	}
	
	
	/**
	 * 角色状态转换
	 * @param status
	 * @return
	 */
	public static String convertRoleStatus(String status){
		String result = "";
		if(status.equals("0")){//0、启用；1、禁用
			result = "启用";
		}else if(status.equals("1")){
			result = "禁用";
		}
		return result;
	}
	
	/**
	 * 归属查询
	 * @param status
	 * @return
	 */
	public static String convertTheaterType(String type){
		String result = "";
		if(type.equals("1")){//系统
			result = "系统";
		}else if(type.equals("2")){//影院
			result = "影院";
		}else if(type.equals("3")){//影投
			result = "影投";
		}
		return result;
	}
	
	
	/**
	 * 日志类型转换
	 * @param status
	 * @return
	 */
	public static String convertLogType(String type){
		String result = "";
		if(type.equals("0")){//0、操作类型；1、登录类型
			result = "操作类型";
		}else if(type.equals("1")){
			result = "登录类型";
		}
		return result;
	}
	
	/**
	 * 日志状态转换
	 * @param status
	 * @return
	 */
	public static String convertLogStatus(String status){
		String result = "";
		if(status.equals("0")){//0、未读；1、已读
			result = "未读";
		}else if(status.equals("1")){
			result = "已读";
		}
		return result;
	}
	
	/**
	 * 判断是否是IE浏览器
	 * 
	 * @param userAgent
	 * @return
	 */
	public static boolean isIE(HttpServletRequest request) {
		String userAgent = request.getHeader("USER-AGENT").toLowerCase();
		boolean isIe = true;
		int index = userAgent.indexOf("msie");
		if (index == -1) {
			isIe = false;
		}
		return isIe;
	}

	/**
	 * 判断是否是Chrome浏览器
	 * 
	 * @param userAgent
	 * @return
	 */
	public static boolean isChrome(HttpServletRequest request) {
		String userAgent = request.getHeader("USER-AGENT").toLowerCase();
		boolean isChrome = true;
		int index = userAgent.indexOf("chrome");
		if (index == -1) {
			isChrome = false;
		}
		return isChrome;
	}

	/**
	 * 判断是否是Firefox浏览器
	 * 
	 * @param userAgent
	 * @return
	 */
	public static boolean isFirefox(HttpServletRequest request) {
		String userAgent = request.getHeader("USER-AGENT").toLowerCase();
		boolean isFirefox = true;
		int index = userAgent.indexOf("firefox");
		if (index == -1) {
			isFirefox = false;
		}
		return isFirefox;
	}
	
	
	/**
	 * 获取客户端类型
	 * 
	 * @param userAgent
	 * @return
	 */
	public static String getClientExplorerType(HttpServletRequest request) {
		String userAgent = request.getHeader("USER-AGENT").toLowerCase();
		String explorer = "非主流浏览器";
		if (isIE(request)) {
			int index = userAgent.indexOf("msie");
			explorer = userAgent.substring(index, index + 8);
		} else if (isChrome(request)) {
			int index = userAgent.indexOf("chrome");
			explorer = userAgent.substring(index, index + 12);
		} else if (isFirefox(request)) {
			int index = userAgent.indexOf("firefox");
			explorer = userAgent.substring(index, index + 11);
		}
		return explorer.toUpperCase();
	}
	
	/*
	 * ------------------------------------------------------------------------------
	 * -----------------------------------位置---------------------------------------
	 * ------------------------------------------------------------------------------
	 */

	/**
	 * 地球半径
	 */
	private static double EARTH_RADIUS = 6378.137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 获取两位置(经纬度)的距离
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static double getDistance(double lat1, double lng1, double lat2,double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + 
				Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10;
		return s;
	}

	
	/**
	 * 获取远程ip
	 * @param request
	 * @return
	 */
	public static String getRemortIP(HttpServletRequest request) {  
	    if (request.getHeader("x-forwarded-for") == null) {  
	        return request.getRemoteAddr();  
	    }  
	    return request.getHeader("x-forwarded-for");  
	}  
	
	
	/**
	 * MD5加盐值(username)加密，迭代次数1024次
	 * @param password
	 * @return
	 */
	public static String MD5_Encrypt(String username, String password) {
		String algorithmName = "MD5";
		
		Object source = password;
		
		Object salt = ByteSource.Util.bytes(username);
		
		int hashIterations = 1024;
		
		//不加盐
//		Object result = new SimpleHash(algorithmName, source);
		
		//加盐且循环一定次数
		Object result = new SimpleHash(algorithmName, source, salt, hashIterations);
		
		return result.toString();
	}
	
	
	
	/**
	 * 32位大写UUID
	 * 
	 * @return
	 */
	public static String UUID()
	{
		String serialNum = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
		return serialNum;
	}
	
	/**
	 * 解析七牛配置文件
	 * @return
	 * @throws IOException 
	 */
	public static Map<String, String> parseQNConfig() throws IOException {
		InputStream input = CodeUtil.class.getClassLoader().getResourceAsStream("QNiu.properties");
		
		Properties prop = new Properties();
		prop.load(input);
		
		Map<String, String> QNMap = new HashMap<String, String>();
		QNMap.put("AccessKey", prop.getProperty("AccessKey"));
		QNMap.put("SecretKey", prop.getProperty("SecretKey"));
		QNMap.put("fileURL", prop.getProperty("fileURL"));
		QNMap.put("bucketname1", prop.getProperty("bucketname1"));
		
		return QNMap;
	}
	
	
	
	
}