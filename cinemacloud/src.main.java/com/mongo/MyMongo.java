package com.mongo;

import com.cp.bean.ResMessage;
import com.cp.util.CodeUtil;
import com.ydp.server.DataMessage;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


/**
 * @ClassName: MyMongo
 * @Description: mongodb数据记录
 */
@Service
public class MyMongo
{
	private static final Logger logger = LoggerFactory.getLogger(MyMongo.class);


	/**
	 * *****************************************************************************************
	 * ***********************************写日志的方法******************************************
	 * *****************************************************************************************
	 */

	/**
	 * mongodb数据库插入记录
	 */
	public static void mLog(String level, Object record, Object content)
	{
		JSONObject json = new JSONObject();
		json.put("logger", record);
		json.put("level", level);
		json.put("message", String.valueOf(content));
		writeLog(json);
		logger.info("{}：{}", record, content);
	}

	/**
	 * mongodb数据库插入记录
	 */
	public static void mMongInfoLog(Object record, Object content)
	{
		JSONObject json = new JSONObject();
		json.put("logger", record);
		json.put("level", "INFO");
		json.put("message", String.valueOf(content));
		writeLog(json);
	}

	/**
	 * 系统商请求日志
	 */
	public static void sysBusiReqLog(String interName, long utime, Object content)
	{
		JSONObject json = new JSONObject();
		json.put("logger", interName + "<" + utime + ">");
		json.put("level", "INFO");
		json.put("message", content.toString());
		writeLog(json);
		logger.info("【{}】<{}>-{}", new Object[] { interName, utime, content });
	}




	/**
	 * 错误日志记录
	 */
	public static void mRequestFail(HttpServletRequest request, int eCode)
	{
		JSONObject json = new JSONObject();

		String requestPath = request.getRequestURI();
		String url = requestPath.substring(requestPath.indexOf("rest") + 5);

		json.put("logger", url);
		json.put("level", "INFO");

		StringBuffer content = new StringBuffer();

		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(request.getParameterMap());
		map.remove("jsonpCallback");
		map.remove("now");
		map.remove("sign");
		map.remove("_");

		if (map.size() > 0)
		{
			content.append("【参数】");
			Set<String> pNames = map.keySet();
			for (String pName : pNames)
			{
				content.append(pName).append(":").append(((String[]) map.get(pName))[0]).append(" ");
			}
		}

		content.append("【返回】").append(eCode).append(":").append(ResMessage.getMessage(eCode));

		json.put("message", content.toString());
		writeLog(json);
		logger.info("【{}】{}", new Object[] { url, content });
	}


	/**
	 * 请求记录日志
	 * @param methodName
	 * @param utime
	 * @param request
     * @param reJson
     */
	public static void mRequestLog(String methodName, long utime, HttpServletRequest request, JSONObject reJson)
	{
		JSONObject json = new JSONObject();
		json.put("logger", methodName + "<" + utime + ">");
		json.put("level", "INFO");

		StringBuffer content = new StringBuffer();

		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(request.getParameterMap());
		map.remove("jsonpCallback");
		map.remove("now");
		map.remove("sign");
		map.remove("_");

		if (map.size() > 0)
		{
			content.append("【参数】");
			Set<String> pNames = map.keySet();
			for (String pName : pNames)
			{
				content.append(pName).append(":").append(((String[]) map.get(pName))[0]).append(" ");
			}
		}
		if (!reJson.isEmpty())
		{
			content.append("【返回】").append(reJson);
		} else
		{
			content.append("【数据为空】");
		}
		json.put("message", content.toString());
		writeLog(json);
		logger.info("【{}-<{}>】{}", new Object[] { methodName, utime, content });
	}

	/**
	 * @Title: mRequestLog
	 * @Description: 请求记录日志
	 * @param @param methodName
	 * @param @param utime
	 * @param @param dataMessage
	 * @return void
	 * @throws
	 */
	public static void mRequestLog(String methodName, long utime, DataMessage dataMessage)
	{
		JSONObject json = new JSONObject();
		json.put("logger", methodName + "<" + utime + ">");
		json.put("level", "INFO");
		StringBuffer content = new StringBuffer();
		if (dataMessage != null && dataMessage.isSuccess())
		{
			content.append("【返回】:").append(dataMessage.getData());
		}
		json.put("message", content.toString());
		writeLog(json);
		logger.info("【{}-<{}>】-{}", new Object[] { methodName, utime, content });
	}

	/**
	 * @Title: mRequestLog
	 * @Description: 请求记录日志
	 * @param @param methodName
	 * @param @param utime
	 * @param @param param
	 * @param @param dataMessage
	 * @return void
	 * @throws
	 */
	public static void mRequestLog(String methodName, long utime, Object[] param, DataMessage dataMessage)
	{
		JSONObject json = new JSONObject();
		json.put("logger", methodName + "<" + utime + ">");
		json.put("level", "INFO");

		StringBuffer content = new StringBuffer();
		if (param != null && param.length > 0)
		{
			content.append("【参数】:").append(Arrays.toString(param)).append(",");
		}
		if (dataMessage != null && dataMessage.isSuccess())
		{
			content.append("【返回】:").append(dataMessage.getData());
		}
		json.put("message", content.toString());
		writeLog(json);
		logger.info("【{}-<{}>】-{}", new Object[] { methodName, utime, content });
	}

	/**
	 * @Title: mRequestLog
	 * @Description: 请求记录日志
	 * @param @param methodName
	 * @param @param utime
	 * @param @param paramMap
	 * @param @param dataMessage
	 * @return void
	 * @throws
	 */
	public static void mRequestLog(String methodName, long utime, Map<String, Object> paramMap, DataMessage dataMessage)
	{
		JSONObject json = new JSONObject();
		json.put("logger", methodName + "<" + utime + ">");
		json.put("level", "INFO");

		StringBuffer content = new StringBuffer();
		if (paramMap != null)
		{
			content.append("【参数】:").append(paramMap).append(",");
		}
		if (dataMessage != null && dataMessage.isSuccess())
		{
			content.append("【返回】:").append(dataMessage.getData());
		}
		json.put("message", content.toString());
		writeLog(json);
		logger.info("【{}-<{}>】-{}", new Object[] { methodName, utime, content });
	}

	/**
	 * 错误日志记录
	 */
	public static void mErrorLog(String methodName, Exception e)
	{
		JSONObject json = new JSONObject();
		json.put("logger", methodName);
		json.put("level", "ERROR");
		json.put("message", CodeUtil.getErrorMessage(e));
		writeLog(json);
		logger.error("【{}】", methodName, e);
	}

	/**
	 * 错误日志记录
	 */
	public static void mErrorLog(String methodName, String paramContent, Exception e)
	{
		JSONObject json = new JSONObject();
		json.put("logger", methodName);
		json.put("level", "ERROR");
		json.put("message", paramContent + ", " + CodeUtil.getErrorMessage(e));
		writeLog(json);
		logger.error("【{}】", methodName + "-" + paramContent, e);
	}

	public static void mErrorLog(String methodName, Object[] param, Exception e)
	{
		StringBuffer content = new StringBuffer();
		if (param != null && param.length > 0)
		{
			content.append("【参数】:").append(Arrays.toString(param)).append(",");
		}

		JSONObject json = new JSONObject();
		json.put("logger", methodName);
		json.put("level", "ERROR");
		json.put("message", content + CodeUtil.getErrorMessage(e));
		writeLog(json);
		logger.error("【{}】", methodName, e);
	}

	/**
	 * 错误日志记录
	 */
	public static void mErrorLog(String methodName, HttpServletRequest request, Exception e)
	{
		StringBuffer content = new StringBuffer();
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(request.getParameterMap());
		map.remove("jsonpCallback");
		map.remove("now");
		map.remove("sign");
		map.remove("_");
		if (map.size() > 0)
		{
			content.append("【参数】");
			Set<String> pNames = map.keySet();
			for (String pName : pNames)
			{
				content.append(pName).append(":").append(((String[]) map.get(pName))[0]).append(" ");
			}
		}
		JSONObject json = new JSONObject();
		json.put("logger", methodName);
		json.put("level", "ERROR");
		json.put("message", content + ", " + CodeUtil.getErrorMessage(e));
		writeLog(json);
		logger.error("【{}】", methodName, e);
	}

	/**
	 * 数据库操作记录
	 * @param name
	 * @param utime
	 * @param param
     * @param size
     */
	public static void mDaoLog(String name, long utime, Object[] param, int size)
	{
		if (utime > 500)
		{
			JSONObject json = new JSONObject();
			json.put("logger", "DAO-" + name);
			json.put("level", "WARN");

			if (param != null)
			{
				json.put("message", "执行<" + utime + ">ms, 查询参数" + Arrays.toString(param) + ",数据记录:" + size);
				logger.info("【DAO-{}】【查询时间<{}>ms】【查询参数】:{},数据记录:{}】",
						new Object[] { name, utime, Arrays.toString(param), size });
			} else
			{
				json.put("message", "执行<" + utime + ">ms, 数据记录:" + size);
				logger.info("【DAO-{}】【查询时间<{}>ms】【数据记录:{}】", new Object[] { name, utime, size });
			}

			writeLog(json);
		}
	}

	private static void writeLog(JSONObject json)
	{
		try
		{
			json.put("processname", "DPC");
			json.put("@version", 1);
			json.put("@timestamp", new Date());
			json.put("thread", Thread.currentThread().getName());
			json.put("hostname", InetAddress.getLocalHost().getHostAddress());

			KafkaTool.send(String.valueOf(json));
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
}
