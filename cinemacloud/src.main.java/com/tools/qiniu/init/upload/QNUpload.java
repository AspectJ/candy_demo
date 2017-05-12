package com.tools.qiniu.init.upload;

import java.io.File;
import java.io.IOException;

import com.cp.exception.CustomException;
import com.mongo.MyMongo;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

/**
 * 七牛上传文件
 */
public class QNUpload
{
	/**
	 * 简单上传
	 * @param token 七牛令牌
	 * @param filePath 上传文件的路径
	 * @param key 上传到七牛后保存的文件名
	 * @throws IOException
	 */
	public static void upload(String token, String filePath, String key) throws IOException
	{
		try
		{
			//构造一个带指定Zone对象的配置类
			Configuration cfg = new Configuration(Zone.zone0());
			
			// 创建上传对象
			UploadManager uploadManager = new UploadManager(cfg);

			// 调用put方法上传
			Response res = uploadManager.put(filePath, key, token);
			// 打印返回的信息
			MyMongo.mLog("INFO", "七牛-简单上传", res.bodyString());
			
			File file = new File(filePath);  
		    if (file.isFile() && file.exists()) {  
		        file.delete();  
		    }  
		} catch (QiniuException e)
		{
			Response r = e.response;
			// 请求失败时打印的异常的信息
			MyMongo.mLog("WARN", "七牛-简单上传", r.toString());
			try
			{
				// 响应的文本信息
				MyMongo.mLog("WARN", "七牛-简单上传", r.bodyString());
			} catch (QiniuException e1)
			{
				MyMongo.mErrorLog("七牛-简单上传", e1);
			}
		}
	}
	
	
	/**
	 * 七牛下载
	 * @param access_key
	 * @param secret_key
	 * @param URL 保存的文件名称
	 * 			//构造私有空间需要生成的下载的链接
		 		//String URL = "http://of84a9dga.bkt.clouddn.com/a.jpg";
	 * @return downloadRUL 文件的下载链接
	 */
	public static String download(String access_key, String secret_key, String fileURL) {
		 //获取token
		 Auth auth = Auth.create(access_key, secret_key);
		 
	     //调用privateDownloadUrl方法生成下载链接,第二个参数可以设置Token的过期时间（3600秒 一小时）
	     String downloadRUL = auth.privateDownloadUrl(fileURL, 3600);
	     System.out.println(downloadRUL);
	     return downloadRUL;
	}
	
	
	public static void deleteQNFile(String accessKey, String secretKey, String bucket, String key) throws CustomException {
		//构造一个带指定Zone对象的配置类
		Configuration cfg = new Configuration(Zone.zone0());

		Auth auth = Auth.create(accessKey, secretKey);
		BucketManager bucketManager = new BucketManager(auth, cfg);
		try {
		    bucketManager.delete(bucket, key);
		} catch (QiniuException ex) {
		    //如果遇到异常，说明删除失败
		    System.err.println(ex.code());
		    System.err.println(ex.response.toString());
//		    throw new CustomException("删除七牛文件失败");
		}
	}
}
