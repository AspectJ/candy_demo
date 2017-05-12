package com.rest.log.aop;

import java.lang.reflect.Method;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.cp.bean.LogInfo;
import com.cp.filter.ReVerifyFilter;
import com.mongo.MyMongo;
import com.rest.log.annotation.SystemServiceLog;
import com.rest.log.dao.LogDaoImpl;

import net.sf.json.JSONObject;

/**
 * AOP Aspect 切点类
 * @author john
 *
 */
@Aspect
@Component
public class SystemLogAspect {
	
	@Autowired
	@Qualifier("logDao")
	private LogDaoImpl logDao;
	
	private Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);
	
	//Service层切点
	@Pointcut("@annotation(com.rest.log.annotation.SystemServiceLog)")
	private void servicePointCut() {
		
	};
	
	@AfterReturning(value="servicePointCut()", returning="returnValue")
	public void doAfter(JoinPoint joinPoint, Object returnValue) {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		//-------------------------------------------------------------------------------
		
		//获取注入点方法中的参数(HttpSaasRequest request, HttpSaasResponse response)
		//获取request
        HttpServletRequest request = (HttpServletRequest) joinPoint.getArgs()[0];
        //获取response
        HttpServletResponse response = (HttpServletResponse) joinPoint.getArgs()[1];
        

        JSONObject jsonObject = JSONObject.fromObject(returnValue);
        String description = null;
        
        if(jsonObject.size() > 0 && jsonObject.get("returnValue") != null) {
        	description = (String) jsonObject.get("returnValue");
        }
	        
        try {
	        
	        String uri = request.getRequestURI();
	        if(uri.lastIndexOf(";") != -1) { 	//截取
	        	uri = uri.substring(0, uri.lastIndexOf(";"));
	        }
	        //获取客户端IP
	        String ip = request.getRemoteAddr();
	        
	        //从请求中获取参数列表
//	        Enumeration<String> parameterNames = request.getParameterNames();
//	        
//	        Map<String,String> requestParams = new HashMap<>();
//	        while(parameterNames.hasMoreElements()) {
//	        	String parameterName = parameterNames.nextElement();
//	        	String value = request.getParameter(parameterName);
//	        	requestParams.put(parameterName, value);
//	        }
	        
	        Integer userid = null;
	        try {
	        	userid = (int) ReVerifyFilter.getUserid(request, response);
	        } catch(NullPointerException e) {
//	        	e.printStackTrace();
	        }

	        
	        LogInfo logInfo = new LogInfo();
	        
	        logInfo.setUSERID(userid);
	        logInfo.setMETHOD_NAME(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
	        logInfo.setMETHOD_DESCRIPTION(getServiceMethodDescription(joinPoint) + ":{" + (description == null ? "" : description) + "}");
	        
	        logInfo.setREQUEST_IP(ip);
	        logInfo.setREQUEST_URI(uri);
	        logInfo.setCREATE_TIME(new Date());
	        
	        logDao.addLogInfo(logInfo);
		} catch(Exception e) {
			logger.error("写入日志出现异常");
			MyMongo.mErrorLog("写入日志失败", request, e);
			return;
		}
        
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("写入日志成功", etime - stime, request, resultJson);
        
	}
	
	
	
	@SuppressWarnings("rawtypes")
	public static String getServiceMethodDescription(JoinPoint joinPoint) {
		Class targetClass = joinPoint.getTarget().getClass();				//目标对象class
		String methodName = joinPoint.getSignature().getName();				//目标方法
		
		Object[] arguments = joinPoint.getArgs();
		Method[] methods = targetClass.getMethods();
		String description = null;
		for (Method method : methods) {
			if(methodName.equals(method.getName())) {
				Class[] clazz = method.getParameterTypes();
				if(clazz.length == arguments.length) {
					System.out.println(method.getAnnotation(SystemServiceLog.class));
					description = method.getAnnotation(SystemServiceLog.class).value();
				}
			}
		}
		return description;
		
	}
	
}
