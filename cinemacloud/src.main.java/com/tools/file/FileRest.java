package com.tools.file;

import com.cp.bean.ResMessage;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.util.CodeUtil;
import com.cp.util.Constant;
import com.mongo.MyMongo;
import com.tools.qiniu.init.QNInitAuth;
import com.tools.qiniu.init.upload.QNUpload;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

//文件上传下载帮助类
@Service
@Path("/rest/file")
public class FileRest extends BaseServlet {
	
	/**
	 * 上传文档或者图片   到七牛
	 * @param request
	 * @param response
	 * @throws CustomException 
	 */
	// url = http://localhost:8080/cinemacloud/rest/file/uploadFile
	@GET
	@POST
	@Path("/uploadFile")
	@Produces("text/html;charset=UTF-8")
	public void uploadFile(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException{
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		//-------------------------------------------------------------------------------
		
		// 获取真实路径，对应${项目目录}/upload/file，当然，这个目录必须存在
		String savepath = request.getSession().getServletContext().getRealPath(Constant.upload + Constant.upFile);
		
		// 通过uploads目录和文件名称来创建File对象
		File file = new File(savepath);
		if(!file.exists()) {
			file.mkdirs();
		}
		
		// 创建工厂(同时设置缓存目录，当文件内容超过10M时，不写入内存，而写入临时目录)
		DiskFileItemFactory dfif = new DiskFileItemFactory(10*1024*1024, file);
		// 使用工厂创建解析器对象
		ServletFileUpload fileUpload = new ServletFileUpload(dfif);
		// 限制文件上传大小100M
		fileUpload.setSizeMax(1024 * 1024 * 100);   
		try {
			// 使用解析器对象解析request，得到FileItem列表
			List<FileItem> list = fileUpload.parseRequest(request);
			for(FileItem fileItem : list) {
				if(fileItem.isFormField()) {
					// 获取当前表单项的字段名称
//					String fileName = fileItem.getFieldName();
					
				} else {//如果当前表单项不是普通表单项，说明就是文件字段
					String name = fileItem.getName();//获取上传文件的名称
					
					// 如果上传的文件名称为空，即没有指定上传文件
					if(name == null || name.isEmpty()) {
						continue;
					}
					
					// 如果客户端使用的是IE6，那么需要从完整路径中获取文件名称
					int lastIndex = name.lastIndexOf("\\");
					if(lastIndex != -1) {
						name = name.substring(lastIndex + 1);
					}

					// 把上传文件保存到指定位置
					// 为防止文件名重名，所以给文件名加上UUID前缀
					String saveName = CodeUtil.UUID() + "_" + name;
					fileItem.write(new File(savepath, saveName));
					
					JSONObject data = new JSONObject();
					
					//===============================上传到七牛===================================
					//初始化
					Map<String, String> QNMap = CodeUtil.parseQNConfig();
					String access_key = QNMap.get("AccessKey");
					String secret_key = QNMap.get("SecretKey");
					QNInitAuth.initAuth(access_key, secret_key);
					//七牛云简单上传
					QNUpload.upload(QNInitAuth.getUpToken(QNMap.get("bucketname1")), 
									savepath + "\\" + saveName, 	//文件真实路径(磁盘路径)
									saveName);
					//===============================    END   ==================================
					
					
					
					//================ 获取文档在七牛的保存路径  BEGIN ================
					//获取保存空间url地址
					String fileURL = QNMap.get("fileURL");
					String wholeURL = fileURL + "/" + saveName;
					String downURL = QNUpload.download(access_key, secret_key, wholeURL);
					//================  获取文档在七牛的保存路径  END  ================
					
					//返回数据到前端页面
//					data.put("savepath", savepath + saveName);	//文件保存磁盘路径
					data.put("savename", saveName);				//文件在七牛的保存名(加了UUID)
					data.put("filename", name);					//文件名称(没有UUID)
					data.put("image_src", downURL);				//文件在七牛的保存路径
					resultJson.put("data", data);
					resultJson.put("status", "success");
					
					PrintWriter writer = response.getWriter();
					writer.print(resultJson);
					writer.close();
					
				}
			}
		}catch(Exception e) {
			MyMongo.mErrorLog("上传文件失败", request, e);
			throw new CustomException("上传文件失败");
		}
		
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("上传文件成功", etime - stime, request, resultJson);
	
	}


	/**
	 * 上传文件到本地服务器
	 * @param request
	 * @param response
	 * @throws CustomException
     */
	// url = http://localhost:8080/cinemacloud/rest/file/uploadFileToLocal
	@GET
	@POST
	@Path("/uploadFileToLocal")
	@Produces("text/html;charset=UTF-8")
	public void uploadFileToLocal(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException{
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		//-------------------------------------------------------------------------------

		// 获取真实路径，对应${项目目录}/upload/file，当然，这个目录必须存在
		String savepath = request.getSession().getServletContext().getRealPath(Constant.upload + Constant.upFile);
		String activityid = request.getParameter("activityid");

		// 通过uploads目录和文件名称来创建File对象
		File file = new File(savepath);
		if(!file.exists()) {
			file.mkdirs();
		}

		// 创建工厂(同时设置缓存目录，当文件内容超过10M时，不写入内存，而写入临时目录)
		DiskFileItemFactory dfif = new DiskFileItemFactory(10*1024*1024, file);
		// 使用工厂创建解析器对象
		ServletFileUpload fileUpload = new ServletFileUpload(dfif);
		// 限制文件上传大小100M
		fileUpload.setSizeMax(1024 * 1024 * 100);
		try {
			// 使用解析器对象解析request，得到FileItem列表
			List<FileItem> list = fileUpload.parseRequest(request);
			for(FileItem fileItem : list) {
				if(fileItem.isFormField()) {
					// 获取当前表单项的字段名称
//					String fileName = fileItem.getFieldName();

				} else {//如果当前表单项不是普通表单项，说明就是文件字段
					String name = fileItem.getName();//获取上传文件的名称

					// 如果上传的文件名称为空，即没有指定上传文件
					if(name == null || name.isEmpty()) {
						continue;
					}

					// 如果客户端使用的是IE6，那么需要从完整路径中获取文件名称
					int lastIndex = name.lastIndexOf("\\");
					if(lastIndex != -1) {
						name = name.substring(lastIndex + 1);
					}

					// 把上传文件保存到指定位置
					// 为防止文件名重名，所以给文件名加上UUID前缀
					String saveName = CodeUtil.UUID() + "_" + name;
					fileItem.write(new File(savepath, saveName));

					JSONObject data = new JSONObject();

					//返回数据到前端页面
					data.put("savepath", "/" + (Constant.upload + Constant.upFile + saveName).replaceAll("\\\\", "/"));	//文件保存磁盘路径
					data.put("savename", saveName);				//文件在七牛的保存名(加了UUID)
					data.put("filename", name);					//文件名称(没有UUID)
					data.put("activityid", activityid);
					resultJson.put("data", data);
					resultJson.put("status", "success");

					PrintWriter writer = response.getWriter();
					writer.print(resultJson);
					writer.close();

				}
			}
		}catch(Exception e) {
			MyMongo.mErrorLog("上传文件到服务器失败", request, e);
			throw new CustomException("上传文件失败");
		}

		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("上传文件到服务器成功", etime - stime, request, resultJson);

	}


	/**
	 * 从本地服务器下载文件
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException
     */
	@GET
	@POST
	@Path("/downloadFile")
	@Produces("text/html;charset=UTF-8")
	// url = http://localhost:8080/cinemacloud/rest/file/downloadFile?filename=a.jpg
	public String downloadFile(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		//-------------------------------------------------------------------------------

		String doc_url = request.getParameter("doc_url");
		String filename = null;
		if(doc_url.lastIndexOf("/") != -1) {
			filename = doc_url.substring(doc_url.lastIndexOf("/") + 1);
		} else {
			MyMongo.mRequestFail(request, ResMessage.File_Not_Exists.code);
			return this.returnError(resultJson, ResMessage.File_Not_Exists.code, request);
		}
		if(CodeUtil.checkParam(filename)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}

		try {

			//GET请求转码
//			filename = new String(filename.getBytes("ISO-8859-1"), "UTF-8");

			String savepath = request.getSession().getServletContext().getRealPath(Constant.upload + Constant.upFile + filename);

			File file = new File(savepath);
			if(!file.exists()) {
				MyMongo.mRequestFail(request, ResMessage.File_Not_Exists.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.File_Not_Exists.code));
			}

			//流
			InputStream input = new FileInputStream(file);

			//头信息
			String contentType = request.getSession().getServletContext().getMimeType(filename);
			String contentDisposition = "attachment;filename=" + filenameEncoding(filename, request);

			response.setHeader("Content-Type", contentType);
			response.setHeader("Content-Disposition", contentDisposition);
			response.setHeader("Content-Length", String.valueOf(file.length()));

			OutputStream output = response.getOutputStream();
			IOUtils.copy(input, output);

		} catch(Exception e) {
			MyMongo.mErrorLog("下载本地服务器文件失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}

		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("下载本地服务器文件成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	/**
	 * 对文件名进行编码（各大主流浏览器--包括IE）
	 * @param filename
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("restriction")
	public static String filenameEncoding(String filename, HttpServletRequest request) throws IOException {
		String agent = request.getHeader("User-Agent"); //获取浏览器
		if (agent.contains("Firefox")) {
			BASE64Encoder base64Encoder = new BASE64Encoder();
			filename = "=?utf-8?B?"
					+ base64Encoder.encode(filename.getBytes("utf-8"))
					+ "?=";
		} else if(agent.contains("MSIE")) {
			filename = URLEncoder.encode(filename, "utf-8");
		} else {
			filename = URLEncoder.encode(filename, "utf-8");
		}
		return filename;
	}
	
	/**
	 * 获取文件在七牛的路径地址
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException
	 */
	// url = http://localhost:8080/cinemacloud/rest/file/getQNFileURL?filename=C81FB495337043ED9A8F6975A7ECC276_规范.png
	@GET
	@POST
	@Path("/getQNFileURL")
	@Produces("text/html;charset=UTF-8")
	public String getQNFileURL(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		//-------------------------------------------------------------------------------
		
		String filename = request.getParameter("filename");
		if(CodeUtil.checkParam(filename)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}
		
		try {
			
			//GET请求转码
//			filename = new String(filename.getBytes("ISO-8859-1"), "UTF-8");
			
			//获取保存空间url地址
			Map<String, String> QNMap = CodeUtil.parseQNConfig();
			String access_key = QNMap.get("AccessKey");
			String secret_key = QNMap.get("SecretKey");
			String fileURL = QNMap.get("fileURL");
			
			String wholeURL = fileURL + "/" + filename;
			
			String downURL = QNUpload.download(access_key, secret_key, wholeURL);
			
			resultJson.put("downURL", downURL);
			
		} catch(Exception e) {
			MyMongo.mErrorLog("获取七牛文件路径失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("获取七牛文件路径成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

}
