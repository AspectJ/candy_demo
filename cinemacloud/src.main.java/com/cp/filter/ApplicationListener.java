package com.cp.filter;

import java.lang.reflect.Method;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Application Lifecycle Listener implementation class Listener
 *
 */
@WebListener
public class ApplicationListener implements ServletContextListener {
	@SuppressWarnings("rawtypes")
	private Class protocolConfig;
	private Method method;
	
    public ApplicationListener() {}

    @SuppressWarnings("unchecked")
	public void contextDestroyed(ServletContextEvent sce)  {
    	
		try {
			protocolConfig = Class.forName("com.alibaba.dubbo.config.ProtocolConfig");
			method = protocolConfig.getMethod("destroyAll");
			method.invoke(protocolConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void contextInitialized(ServletContextEvent sce)  {}
	
}
