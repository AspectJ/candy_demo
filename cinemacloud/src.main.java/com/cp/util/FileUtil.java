package com.cp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.cp.exception.CustomException;
import com.mongo.MyMongo;

public class FileUtil {
	
	public static File uploadFile(HttpServletRequest request,String token){
		
		File newFile=null;
		
		  //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver=new CommonsMultipartResolver(
                request.getSession().getServletContext());
        //检查form中是否有enctype="multipart/form-data"
        if(multipartResolver.isMultipart(request))
        {
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest=multipartResolver.resolveMultipart(request);
           //获取multiRequest 中所有的文件名
            Iterator<String> iter=multiRequest.getFileNames();
            while(iter.hasNext())
            {
                //一次遍历所有文件
                MultipartFile file=multiRequest.getFile(iter.next().toString());
                if(file!=null)
                {
                	String dirpath="E:/upload";
                	File dir=new File(dirpath);
                	if(!dir.exists()){
                		dir.mkdir();
					}
                    String path=dir.getPath()+"/"+token+"-"+file.getOriginalFilename();
                    //上传
                    newFile=new File(path);
                    try {
						file.transferTo(newFile);
					} catch (IOException e) {
						newFile.delete();
						MyMongo.mErrorLog("上传文件失败", request, e);
						return null;
					}
                }
            }
        }
		return newFile;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @throws CustomException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void downloadFile(@Context  HttpServletRequest request,@Context HttpServletResponse response,String filename,String path) throws CustomException {
		
	        //两个头 contentType 和 contentDisposition
			String contentType = request.getSession().getServletContext().getMimeType(filename);//通过文件名称获取MIME类型
			String contentDisposition = null;
			try {
				contentDisposition = "attachment;filename=" + URLEncoder.encode(filename,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		//设置头
			response.setHeader("Content-Type", contentType);
			response.setHeader("Content-Disposition", contentDisposition);
			File file = new File(path);
			FileInputStream inputStream=null;
			try {
			//通过文件路径获得File对象
			inputStream=new FileInputStream(file);
	        //得到输出流
	        OutputStream output = response.getOutputStream();
	        IOUtils.copy(inputStream, output);
	        
		} catch(Exception e) {
			MyMongo.mErrorLog("下载文件失败", request, e);
			throw new CustomException("下载文件失败");
		}finally {
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					throw new CustomException("关闭文件流失败");
				}
			}
		}
		
	}

}
