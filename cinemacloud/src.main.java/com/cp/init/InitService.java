package com.cp.init;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.xml.DOMConfigurator;

import com.cp.util.Config;

/**
 * 程序启动初始化加载
 * 
 * @author stone 2015-5-29下午4:15:38
 */
public class InitService extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException
	{
		String prefix = getServletContext().getRealPath("/");
		//String log4j = getInitParameter("log4j");
		// 启动日志记录
		//DOMConfigurator.configureAndWatch(prefix + log4j, 60000);

		// 读取配置文件
		String config = getInitParameter("config");
		Config.parseConfig(prefix + config);
		
		// 微信获取tocken线程
//		new AccessTokenThread().start();
	}
}
