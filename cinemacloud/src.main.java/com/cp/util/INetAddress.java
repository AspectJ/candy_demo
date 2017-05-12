package com.cp.util;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 查找服务器
 */
public class INetAddress
{
	private static Logger logger = LoggerFactory.getLogger(INetAddress.class);

	public static InetAddress getInetAddress(){  
        try{  
            return InetAddress.getLocalHost();  
        }catch(Exception e){  
            logger.info("unknown host!");  
        }  
        return null;  
  
    }  
  
    public static String getHostIp(InetAddress netAddress){  
        if(null == netAddress){  
            return null;  
        }  
        String ip = netAddress.getHostAddress(); //get the ip address  
        return ip;  
    }  
  
    public static String getHostName(InetAddress netAddress){  
        if(null == netAddress){  
            return null;  
        }  
        String name = netAddress.getHostName(); //get the host address  
        return name;  
    }  
    
    public static String getNet(){
    	InetAddress netAddress = getInetAddress();  
		String net = " #IP:"+getHostIp(netAddress)+",Name:"+getHostName(netAddress);
		//String net = " #HostName:"+getHostName(netAddress);
		return net;
    }
    
    
    
    public String getIpAddr(HttpServletRequest request) {      

        String ip = request.getHeader("x-forwarded-for");      

            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     

                ip = request.getHeader("Proxy-Client-IP");      

            }     

            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     

                ip = request.getHeader("WL-Proxy-Client-IP");     

            }     

            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {     

                ip = request.getRemoteAddr();      

            }   

       return ip;     

    }  
    
    
}
