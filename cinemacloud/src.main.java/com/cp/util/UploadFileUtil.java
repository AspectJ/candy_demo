package com.cp.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.mongo.MyMongo;

public class UploadFileUtil {
	
	public static File getFile(HttpServletRequest request){
		
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
                    String path="E:/springUpload"+file.getOriginalFilename();
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

}
