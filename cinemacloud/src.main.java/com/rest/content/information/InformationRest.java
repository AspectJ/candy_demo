package com.rest.content.information;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cp.bean.ArchiveInfoCustom;
import com.cp.bean.Information;
import com.cp.bean.ResMessage;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.mongo.MyMongo;
import com.rest.content.information.dao.InformationDaoImpl;
import com.rest.theater.dao.TheaterDaoImpl;
import com.tools.qiniu.init.upload.QNUpload;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Path("/rest/information")
public class InformationRest extends BaseServlet {
	
	@Autowired
	@Qualifier("informationDao")
	private InformationDaoImpl informationDao;
	
	@Autowired
	@Qualifier("theaterDao")
	private TheaterDaoImpl theaterDao;
	
	
	/**
	 * 添加information
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@GET
	@POST
	@RequiresPermissions("program:create")
	@Path("/addInformation")
	public String addInformation(@Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		
		String program_id = request.getParameter("program_id"); //所属栏目id
		String title = request.getParameter("title");			//标题
		String content = request.getParameter("content");		//内容
		
		String use_status = request.getParameter("use_status"); //状态
		String doc_name = request.getParameter("doc_name"); 	//文档在七牛保存名称			--->可能为空
		/*
			String image_name = request.getParameter("image_name"); 	//主题图片在七牛保存名称	--->可能为空
		*/		
		String start_time = request.getParameter("start_time"); //活动开始时间				--->可能为空
		String end_time = request.getParameter("end_time");		//活动截止时间				--->可能为空
		String author = request.getParameter("author"); 		//作者						--->可能为空
		String operatorid = request.getParameter("operatorid"); //操作员id
		
		//归档属性（JSON数组）
		String archiveArr = request.getParameter("archiveArr");
		
		if(CodeUtil.checkParam(program_id, title, content, use_status, operatorid, archiveArr))
		{
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("program_id", program_id);
		paramsMap.put("title", URLDecoder.decode(title, "UTF-8"));
		paramsMap.put("content", URLDecoder.decode(content, "utf-8"));
		
		paramsMap.put("use_status", use_status);
		paramsMap.put("operatorid", operatorid);
		//上传文档
		if(!CodeUtil.checkParam(doc_name)) {
			paramsMap.put("doc_name", URLDecoder.decode(doc_name, "UTF-8"));
		}
		/*		
			//上传图片
			if(!CodeUtil.checkParam(image_name)) {
				paramsMap.put("image_name", URLDecoder.decode(image_name, "UTF-8"));
			}
		*/
		//活动开始结束时间不为空
		if(!CodeUtil.checkParam(start_time, end_time)) {
			paramsMap.put("start_time", start_time);
			paramsMap.put("end_time", end_time);
		}
		//文章作者不为空
		if(!CodeUtil.checkParam(author)) {
			paramsMap.put("author", URLDecoder.decode(author, "UTF-8"));
		}
		
		//验证information重复性（根据title）
		List<Map<String, Object>> repeatList = informationDao.checkRepeat(paramsMap);
		if(repeatList != null && repeatList.size() > 0) {
			MyMongo.mRequestFail(request, ResMessage.Information_Name_Exists.code);
			return this.returnError(resultJson, ResMessage.Information_Name_Exists.code, request);
		}
		
		//添加information
		informationDao.addInformation(paramsMap);	//该方法执行结束后，info_id已经被put到了paramsMap中
		
		//为information添加归档属性
		JSONArray jsonArray = JSONArray.fromObject(URLDecoder.decode(archiveArr, "UTF-8"));
		if(jsonArray.size() > 0) {	//说明归档不是空数组[]
			paramsMap.put("jsonArray", jsonArray);
			
			//添加information归档属性信息
			informationDao.addArchive(paramsMap);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("添加Information操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 更改information
	 * @param request
	 * @return
	 * @throws CustomException 
	 * @throws IOException 
	 */
	// http://localhost:8080/cinemacloud/rest/updateInformation
	@GET
	@POST
	@RequiresPermissions("program:update")
	@Path("/updateInformation")
	public String updateInformation(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, IOException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
			
		String info_id = request.getParameter("info_id"); 		//information主键
		
		String program_id = request.getParameter("program_id"); //栏目名称
		String title = request.getParameter("title");			//标题
		String content = request.getParameter("content");		//内容
		
		String use_status = request.getParameter("use_status"); //可用状态
		String doc_name = request.getParameter("doc_name"); 	//文档保存相对路径		--->可能为空
/*		
		String image_name = request.getParameter("image_name"); //主题图片ID				--->可能为空
*/		
		String start_time = request.getParameter("start_time"); //活动开始时间			--->可能为空
		String end_time = request.getParameter("end_time");		//活动截止时间			--->可能为空
		String author = request.getParameter("author"); 		//作者					--->可能为空
		String operatorid = request.getParameter("operatorid"); //操作员id
		
		//归档属性（JSON数组）
		String archiveArr = request.getParameter("archiveArr");
		
		if(CodeUtil.checkParam(info_id, program_id, title, content, use_status, operatorid, archiveArr)) 
		{
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("info_id", info_id);
		paramsMap.put("program_id", program_id);
		paramsMap.put("title", URLDecoder.decode(title, "UTF-8"));
		
		paramsMap.put("content", URLDecoder.decode(content, "UTF-8"));
		paramsMap.put("use_status", use_status);
		paramsMap.put("operatorid", operatorid);
		
		/**
		 * 如果上传文档或者图片文档，则查询更新前的图片或者文档是否为空，
		 * 		如果不为空， 则删除七牛中保存的图片或者文档
		 */
		//该文章的详细信息
		Map<String, Object> infoMap = informationDao.getInfoByInfoId(info_id);
		//解析七牛
		Map<String, String> QNMap = CodeUtil.parseQNConfig();
		
		
		//上传文档
		if(!CodeUtil.checkParam(doc_name)) {
			paramsMap.put("doc_name", URLDecoder.decode(doc_name, "UTF-8"));
			
			try{
				//如果该次文档名和数据库保存的文档名称不一致，删除七牛中上一次传的文档
				if(infoMap.containsKey("doc_name") && !infoMap.get("doc_name").equals(paramsMap.get("doc_name"))) {
					String key = (String) infoMap.get("doc_name");
					QNUpload.deleteQNFile(QNMap.get("AccessKey"), QNMap.get("SecretKey"), QNMap.get("bucketname1"), key);
				}
			} catch(Exception e) {
				MyMongo.mErrorLog("删除七牛文件失败", request, e);
				return this.returnError(resultJson, ResMessage.Delete_QNFile_Fail.code, request);
			}
		} else {	//删除文档
			try {
				if(infoMap.containsKey("doc_name")) {
					String key = (String) infoMap.get("doc_name");
					QNUpload.deleteQNFile(QNMap.get("AccessKey"), QNMap.get("SecretKey"), QNMap.get("bucketname1"), key);
				}
			} catch(Exception e) {
				MyMongo.mErrorLog("删除七牛文件失败", request, e);
				return this.returnError(resultJson, ResMessage.Delete_QNFile_Fail.code, request);
			}
		}
		
		/*
		//上传图片
		if(!CodeUtil.checkParam(image_name)) {
			paramsMap.put("image_name", URLDecoder.decode(image_name, "UTF-8"));
			
			try {
				//如果该次图片名和数据库保存的图片名称不一致，删除七牛中上一次传的图片
				if(infoMap.containsKey("image_name") && !infoMap.get("image_name").equals(paramsMap.get("image_name"))) {
					String key = (String) infoMap.get("image_name");
					QNUpload.deleteQNFile(QNMap.get("AccessKey"), QNMap.get("SecretKey"), QNMap.get("bucketname1"), key);
				}
			} catch(Exception e) {
				MyMongo.mErrorLog("删除七牛文件失败", request, e);
				return this.returnError(resultJson, ResMessage.Delete_QNFile_Fail.code, request);
			}
		} else { //删除图片
			
			try {
				if(infoMap.containsKey("image_name")) {
					String key = (String) infoMap.get("image_name");
					QNUpload.deleteQNFile(QNMap.get("AccessKey"), QNMap.get("SecretKey"), QNMap.get("bucketname1"), key);
				}
			} catch(Exception e) {
				MyMongo.mErrorLog("删除七牛文件失败", request, e);
				return this.returnError(resultJson, ResMessage.Delete_QNFile_Fail.code, request);
			}
		}
		*/
		//活动开始结束时间不为空
		if(!CodeUtil.checkParam(start_time, end_time)) {
			paramsMap.put("start_time", start_time);
			paramsMap.put("end_time", end_time);
		}
		//文章作者不为空
		if(!CodeUtil.checkParam(author)) {
			paramsMap.put("author", URLDecoder.decode(author, "UTF-8"));
		}
		
		//更改information
		informationDao.updateInformation(paramsMap);
		
		//为information更改归档属性（先删除再添加）
		informationDao.deleteArchive(info_id);
		
		JSONArray jsonArray = JSONArray.fromObject(URLDecoder.decode(archiveArr, "UTF-8"));
		if(jsonArray.size() > 0) {	//说明归档不是空数组[]
			paramsMap.put("jsonArray", jsonArray);
			
			//添加归档属性信息
			informationDao.addArchive(paramsMap);
		}
			
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更改Information操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 删除information
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	@GET
	@POST
	@RequiresPermissions("program:delete")
	@Path("/deleteInformation")
	public String deleteInformation(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
			
		String info_id = request.getParameter("info_id"); 	//information主键
		
		if(CodeUtil.checkParam(info_id)) 
		{
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}
		
		//删除七牛中文件(图片和文档)
		Map<String, Object> infoMap = informationDao.getInfoByInfoId(info_id);
		/*if(infoMap.containsKey("doc_name") || infoMap.containsKey("image_name")) {*/
		if(infoMap.containsKey("doc_name")) {
			//解析七牛
			Map<String, String> QNMap = CodeUtil.parseQNConfig();
			
			if(infoMap.containsKey("doc_name")) {
				String key = (String) infoMap.get("doc_name");
				try {
					QNUpload.deleteQNFile(QNMap.get("AccessKey"), QNMap.get("SecretKey"), QNMap.get("bucketname1"), key);
				} catch (CustomException e) {
					MyMongo.mErrorLog("删除七牛文件失败", request, e);
					return this.returnError(resultJson, ResMessage.Delete_QNFile_Fail.code, request);
				}
			}
			/*			
			if(infoMap.containsKey("image_name")) {
				String key = (String) infoMap.get("image_name");
				try {
					QNUpload.deleteQNFile(QNMap.get("AccessKey"), QNMap.get("SecretKey"), QNMap.get("bucketname1"), key);
				} catch (CustomException e) {
					MyMongo.mErrorLog("删除七牛文件失败", request, e);
					return this.returnError(resultJson, ResMessage.Delete_QNFile_Fail.code, request);
				}
			}*/
		}

		//删除information
		informationDao.deleteInformation(info_id);
		
		
		//删除information的归档属性
		informationDao.deleteArchive(info_id);
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除Information操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 更改information可用状态(0禁用， 1可用)--audit_flag
	 * @param request
	 * @return
	 */
	//url = http://localhost:8080/cinemacloud/rest/information/auditInformationStatus
	@GET
	@POST
	@RequiresPermissions("program:audit")
	@Path("/auditInformationStatus")
	public String auditInformationStatus(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
			
		String info_id = request.getParameter("info_id"); 	//information主键
		String audit_flag = request.getParameter("audit_flag");		//infoamation要更改为的状态(0禁用， 1可用)
		
		if(CodeUtil.checkParam(info_id, audit_flag)) 
		{
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("info_id", info_id);
		paramsMap.put("audit_flag", audit_flag);
		
		//更改information可用状态(0禁用， 1可用)
		informationDao.auditInformationStatus(paramsMap);
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更改information可用状态成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 查找当前information的归档信息
	 * @param request
	 * @return
	 */
	// url = http://localhost:8080/cinemacloud/rest/information/findInformationArchive?info_id=149
	@GET
	@POST
	@Path("/findInformationArchive")
	public String findInformationArchive(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
			
		try {
			
			String info_id = request.getParameter("info_id"); 	//information主键
			
			if(CodeUtil.checkParam(info_id)) 
			{
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//查找当前information的归档信息
			List<ArchiveInfoCustom> infoArchiveList = informationDao.findInformationArchive(info_id);
			
			if(infoArchiveList != null && infoArchiveList.size() >0) {
				resultJson.put("data", infoArchiveList);
			}
			
		} catch(Exception e) {
			MyMongo.mErrorLog("查找当前information的归档信息失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查找当前information的归档信息成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	
	/**
	 * 查找某一栏目的information的详细信息及其归档信息列表（根据栏目id）
	 * @param request
	 * @return
	 */
	// url = http://localhost:8080/cinemacloud/rest/information/findInformationDetailListByProId?program_id=1
	@GET
	@POST
	@RequiresPermissions("program:view")
	@Path("/findInformationDetailListByProId")
	public String findInformationDetailListByProId(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
			
		
		try {
			
			Integer userid = (int) ReVerifyFilter.getUserid(request, response);
			String program_id = request.getParameter("program_id");
			String criteria = request.getParameter("criteria");
			String page = request.getParameter("page");
			String pagesize = request.getParameter("pagesize");
			
			if(CodeUtil.checkParam(program_id, String.valueOf(userid))) 
			{
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//根据userid查询该用户所属影院信息
			Map<String, Object> theaterMap = theaterDao.findTheaterByUserid(String.valueOf(userid));
			
			List<Information> infoList = null;
			Integer count = null;
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("program_id", program_id);
			//模糊查询
			if(!CodeUtil.checkParam(criteria)) {
				paramsMap.put("criteria", URLDecoder.decode(criteria, "UTF-8"));
			}
			
			//分页参数
			if(!CodeUtil.checkParam(page, pagesize)) {
				paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
				paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
			}
			
			//表示是某个影院用户
			if(theaterMap.containsKey("theaterid")) {
				paramsMap.put("theaterid", (int) theaterMap.get("theaterid"));
				infoList = informationDao.getInfoListByArchiveAndProId(paramsMap);
				count = informationDao.getInfoListByArchiveAndProIdCount(paramsMap);
			} else { //表示是院线用户或者系统用户
				infoList = informationDao.getAllInfoByProId(paramsMap);
				count = informationDao.getAllInfoByProIdCount(paramsMap);
			}
			
			//七牛参数
			Map<String, String> QNMap = CodeUtil.parseQNConfig();
			String access_key = QNMap.get("AccessKey");
			String secret_key = QNMap.get("SecretKey");
			String fileURL = QNMap.get("fileURL");
			
			//根据栏目id查找归档属性并封装
			for (Information information : infoList) {
				
				//================ 获取文档在七牛的保存路径  BEGIN ================
				if(!CodeUtil.checkParam(information.getDoc_name())) {
					//获取保存空间url地址
					String wholeURL = fileURL + "/" + information.getDoc_name();
					String downURL = QNUpload.download(access_key, secret_key, wholeURL);
					information.setDoc_url(downURL);
				}
				//================  获取文档在七牛的保存路径  END  ================
				
				//归档信息
				List<ArchiveInfoCustom> ariclist =  informationDao.findInformationArchive(information.getInfo_id().toString());
				information.setAriclist(ariclist);
			}
			
			if(infoList != null && infoList.size() > 0) {
				resultJson.put("data", infoList);
			}
			if(count != null && count != 0) {
				resultJson.put("total", count);
			}
			
		} catch(Exception e) {
			MyMongo.mErrorLog("查找某一栏目information的详细信息列表失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查找某一栏目information的详细信息列表成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	/**
	 * 查找可看的(根据权限)info详细信息及其归档信息列表（查找所有栏目的，不根据program_id查询）
	 * @param request
	 * @return
	 */
	// url = http://localhost:8080/cinemacloud/rest/information/findAvaiableInfoList
	@GET
	@POST
	@RequiresPermissions("program:view")
	@Path("/findAvaiableInfoList")
	public String findAvaiableInfoList(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			
			Integer userid = (int) ReVerifyFilter.getUserid(request, response);
			String criteria = request.getParameter("criteria");
			String page = request.getParameter("page");
			String pagesize = request.getParameter("pagesize");
			
			if(CodeUtil.checkParam(String.valueOf(userid))) 
			{
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//根据userid查询该用户所属影院信息
			Map<String, Object> theaterMap = theaterDao.findTheaterByUserid(String.valueOf(userid));
			
			List<Information> infoList = null;
			Integer count = null;
			
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			if(!CodeUtil.checkParam(criteria)) {
				paramsMap.put("criteria", URLDecoder.decode(criteria, "UTF-8"));
			}
			
			if(!CodeUtil.checkParam(page, pagesize)) {
				paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
				paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
			}
			
			//表示是某个影院用户
			//如果登陆用户是影院用户(roletype==2) ,则根据归档查看所有可看的info列表(查询所有栏目的info)
			if(theaterMap.containsKey("theaterid")) {
				paramsMap.put("theaterid", theaterMap.get("theaterid"));
				
				infoList = informationDao.findAvaiableInfoList(paramsMap);
				count = informationDao.findAvaiableInfoListCount(paramsMap);
				
			} else { 
				//表示是院线用户或者系统用户
				//如果登陆用户是系统用户或者院线用户，则可以查看所有栏目的所有info
				infoList = informationDao.getAllInfoList(paramsMap);
				count = informationDao.getAllInfoListCount(paramsMap);
			}
			
			//获取七牛参数
			Map<String, String> QNMap = CodeUtil.parseQNConfig();
			String access_key = QNMap.get("AccessKey");
			String secret_key = QNMap.get("SecretKey");
			String fileURL = QNMap.get("fileURL");
			
			//根据栏目id查找归档属性并封装
			for (Information information : infoList) {
				
				//================ 获取文档在七牛的保存路径  BEGIN ================
				if(!CodeUtil.checkParam(information.getDoc_name())) {
					//获取保存空间url地址
					String wholeURL = fileURL + "/" + information.getDoc_name();
					String downURL = QNUpload.download(access_key, secret_key, wholeURL);
					information.setDoc_url(downURL);
				}
				//================  获取文档在七牛的保存路径  END  ================
				
				List<ArchiveInfoCustom> ariclist =  informationDao.findInformationArchive(information.getInfo_id().toString());
				information.setAriclist(ariclist);
			}
			
			if(infoList != null && infoList.size() > 0) {
				resultJson.put("data", infoList);
			}
			if(count != null && count != 0) {
				resultJson.put("total", count);
			}
			
		} catch(Exception e) {
			MyMongo.mErrorLog("查找所有栏目的info的详细信息列表失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查找所有栏目的info的详细信息列表成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	
	/**
	 * 查找用户能够看到的最新的文章列表(自该用户上次登录之后)
	 * @param request
	 * @param response
	 * @return
	 */
	// url = http://localhost:8080/cinemacloud/rest/information/findLatestInfoList
	@GET
	@POST
	@RequiresPermissions("program:view")
	@Path("/findLatestInfoList")
	public String findLatestInfoList(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			
			Integer userid = (int) ReVerifyFilter.getUserid(request, response);
			String page = request.getParameter("page");
			String pagesize = request.getParameter("pagesize");
			
			if(CodeUtil.checkParam(String.valueOf(userid))) 
			{
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//根据userid查询该用户所属影院信息
			Map<String, Object> theaterMap = theaterDao.findTheaterByUserid(String.valueOf(userid));
			
			List<Information> infoList = null;
			Integer count = null;
			
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("userid", theaterMap.get("userid"));

			if(!CodeUtil.checkParam(page, pagesize)) {
				paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
				paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
			}
			
			//表示是某个影院用户
			//如果登陆用户是影院用户(roletype==2) ,则根据归档查看最新的info列表(查询所有栏目的最新info)
			if(theaterMap.containsKey("theaterid")) {
				paramsMap.put("theaterid", theaterMap.get("theaterid"));
				
				infoList = informationDao.findLatestAvaiableInfoList(paramsMap);
				count = informationDao.findLatestAvaiableInfoListCount(paramsMap);
				
			} else { 
				//表示是院线用户或者系统用户
				//如果登陆用户是系统用户或者院线用户，则可以查看所有栏目的最新info列表（自上次登录之后的info(不是自己添加的)）
				infoList = informationDao.getLatestAllInfoList(paramsMap);
				count = informationDao.getLatestAllInfoListCount(paramsMap);
			}
			
			if(infoList != null && infoList.size() > 0) {
				resultJson.put("data", infoList);
				resultJson.put("total", count);
			}
			
		} catch(Exception e) {
			MyMongo.mErrorLog("查找最新的info列表失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查找最新的info列表成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
}
