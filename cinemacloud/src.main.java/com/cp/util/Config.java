package com.cp.util;

import com.mongo.KafkaTool;
import com.mongo.MyMongo;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Config
{
	private static Logger logger = LoggerFactory.getLogger(Config.class);

//	public static String SERVICEURL = "http://cp.yidepiao.net:8080/cinemapay/";
	public static String SERVICEURL = "http://localhost:8000";
	
	public static String FILE_PATH = "D:\\soft_devolep\\apache-tomcat\\Tomcat-8080-cinemacloud\\webapps\\upload";
	
	public static String QN_ACCESS;
	public static String QN_SECRET;
	public static String QN_BUCKET;
	public static String QN_PREFIX;
	
	public static String DES_KEY = "46778292";
	
	/**
	 * 解析配置文件
	 */
	public static void parseConfig(String configFile) {

		try
		{
			SAXReader read = new SAXReader();
			Document document = read.read(configFile);
			Node rootNode = document.selectSingleNode("config");


			// *********************** kafka消息队列配置 **********************
			Node kafkaNode = rootNode.selectSingleNode("kafka");
			KafkaTool.TOPIC = kafkaNode.valueOf("@topic");
			KafkaTool.ZOOKEEPER_CONNECT = kafkaNode.valueOf("@zookeeper");
			KafkaTool.BROKER_LIST = kafkaNode.valueOf("@broker");
			
			
			FILE_PATH = rootNode.selectSingleNode("path").valueOf("@file");
	        File fileDir = new File(FILE_PATH);
	        if (!fileDir.exists())
			{
	        	fileDir.mkdirs();
			}
			logger.info("[config][解析配置文件][FILE_PATH:{}]", FILE_PATH);
			
			SERVICEURL = rootNode.selectSingleNode("service").valueOf("@url");
			logger.info("[config][解析配置文件][SERVICEURL:{}]", SERVICEURL);
			
			/**
			 * 加载七牛配置
			 */
			QN_ACCESS = rootNode.selectSingleNode("QN").valueOf("@access");
			QN_SECRET = rootNode.selectSingleNode("QN").valueOf("@secret");
			QN_BUCKET = rootNode.selectSingleNode("QN").valueOf("@bucket");
			QN_PREFIX = rootNode.selectSingleNode("QN").valueOf("@prefix");
			MyMongo.mLog("INFO", "加载七牛配置", QN_ACCESS);
			MyMongo.mLog("INFO", "加载七牛配置", QN_SECRET);
			MyMongo.mLog("INFO", "加载七牛配置", QN_BUCKET);
			MyMongo.mLog("INFO", "加载七牛配置", QN_PREFIX);
			
			MyMongo.mLog("INFO", "加载配置文件", "加载完成");
		} catch (DocumentException e)
		{
			logger.error("[config][解析配置文件失败][eMess:{}]", e.getMessage(), e);
		}
	}
}
