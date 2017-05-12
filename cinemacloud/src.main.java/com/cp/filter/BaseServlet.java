package com.cp.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cp.bean.ResMessage;
import com.cp.util.CodeUtil;

import net.sf.json.JSONObject;

public class BaseServlet
{
	/**
	 * 数据返回
	 */
	protected String response(JSONObject resultJson, HttpServletRequest request)
	{
		String resultStr = null;
		String jsonpCallback = request.getParameter("jsonpCallback");

		resultJson.put("result", ResMessage.Success.code);
		resultJson.put("msg", ResMessage.getMessage(ResMessage.Success.code));

		if (jsonpCallback != null && !"".equals(jsonpCallback))
		{
			resultStr = jsonpCallback + "(" + resultJson + ")";
		} else
		{
			resultStr = resultJson.toString();
		}
		return resultStr;
	}

	/**
	 * 过滤器返回错误
	 * @param code
	 * @param request
	 * @param response 
	 * @throws IOException 
	 */
	protected void filterError(int typeCode, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		JSONObject resultJson = new JSONObject();
		resultJson.put("result", typeCode);
		resultJson.put("msg", ResMessage.getMessage(typeCode));
		
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter pw = response.getWriter(); 
		String jsonpCallback = request.getParameter("jsonpCallback");
		if (jsonpCallback != null && !"".equals(jsonpCallback))
		{
			pw.write( jsonpCallback + "(" + resultJson + ")" );
		} else
		{
			pw.write( resultJson.toString() );
		}
		pw.close();
	}

	/**
	 * 异常返回
	 */
	protected String returnError(JSONObject resultJson, int typeCode, HttpServletRequest request)
	{
		String resultStr = null;

		resultJson.put("result", typeCode);
		resultJson.put("msg", ResMessage.getMessage(typeCode));

		String jsonpCallback = request.getParameter("jsonpCallback");
		if (jsonpCallback != null && !"".equals(jsonpCallback))
		{
			resultStr = jsonpCallback + "(" + resultJson + ")";
		} else
		{
			resultStr = resultJson.toString();
		}
		return resultStr;
	}


	/**
	 * 获取数据
	 * @param request
	 * @param nParam 参数名称
	 * @param vDefault 默认值
	 * @return
	 */
	public static String getParam(HttpServletRequest request, String nParam, String vDefault)
	{
		String value = request.getParameter(nParam);
		if (CodeUtil.checkParam(value))
		{
			value = vDefault;
		}
		return value;
	}
	
	/**
	 * 权限验证
	 * @Title: privilegeCheck 
	 * @Description: TODO
	 * @param @return
	 * @return boolean
	 * @throws
	 */
//	public boolean privilegeCheck(HttpServletRequest request, String requesturl, UserRedisImpl userRedis){
//		boolean flag = false;
//		int userid = Integer.parseInt(String.valueOf(request.getAttribute("userid")));
//		flag = userRedis.privilegeCheck(userid, requesturl);
//		return flag;
//	}
}
