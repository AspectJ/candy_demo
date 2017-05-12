package com.cp.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.session.UnknownSessionException;

import com.cp.bean.ResMessage;

import net.sf.json.JSONObject;

/**
 * 全局异常处理器
 * @author john
 *
 */
@Provider
public class RestEasyException implements ExceptionMapper<Exception> {

	@Context HttpServletRequest request;
	
	@Context HttpServletResponse response;

	@Override
	@Produces("application/json;charset=UTF-8")
	public Response toResponse(Exception e) {
		
		JSONObject resultJson=new JSONObject();
		
		CustomException customException = null;
		if (e instanceof CustomException) {
			customException = (CustomException) e;
			String message=customException.getMessage();
			try {
				Integer result=Integer.valueOf(message);
				resultJson.put("result",result);
				resultJson.put("msg", "请重新登录");
				return Response.ok(resultJson.toString()).build();
			} catch (Exception e2) {
				resultJson.put("result", 9999);
				resultJson.put("msg", message);
				return Response.ok(resultJson.toString()).build();
			}
			
		}
		if(e instanceof AuthorizationException){
			resultJson.put("result", ResMessage.Lack_Privilege);
			resultJson.put("msg", ResMessage.getMessage(ResMessage.Lack_Privilege.code));
			return Response.ok(resultJson.toString()).build();
		}
		if(e instanceof UnknownSessionException){
			resultJson.put("result",1109);
			resultJson.put("msg", "请重新登录");
			return Response.ok(resultJson.toString()).build();
		}
		resultJson.put("result",ResMessage.Server_Abnormal.code );
		resultJson.put("msg", ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		return Response.ok(resultJson.toString()).build();
	}
	
	

}
