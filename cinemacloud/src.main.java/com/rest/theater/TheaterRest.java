package com.rest.theater;

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

import com.cp.bean.ResMessage;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.cp.util.DateFormatUtil;
import com.mongo.MyMongo;
import com.rest.log.annotation.SystemServiceLog;
import com.rest.theater.dao.TheaterDaoImpl;
import com.rest.user.dao.UserDaoImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Path("/rest/theater")
public class TheaterRest extends BaseServlet {

	@Autowired
	@Qualifier("theaterDao")
	private TheaterDaoImpl theaterDao;
	
	@Autowired
	@Qualifier("userDao")
	private UserDaoImpl userDao;
	
	
	/**
	 * 查询当前用户能够查看的院线列表
	 * @param request
	 * @return
	 */
	//url=http://localhost:8080/cinemacloud/rest/theater/findTheaterChainList
	@GET
	@POST
	@Path("/findTheaterChainList")
	public String findTheaterChainList(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			
			int userid = (int) ReVerifyFilter.getUserid(request, response);
			
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userRoleAndTheaterMap = userDao.findRoleAndTheaterById(userid);
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("roletype", (int) userRoleAndTheaterMap.get("roletype"));
			//系统角色
			if((int) userRoleAndTheaterMap.get("roletype") == 0) {
				//可以查询所有院线
			}
			//院线角色
			else if((int) userRoleAndTheaterMap.get("roletype") == 1 && userRoleAndTheaterMap.containsKey("theaterchainid")) {
				//只能查询该用户所属院线
				paramsMap.put("theaterchainid", (int) userRoleAndTheaterMap.get("theaterchainid"));
			}
			//影院角色 ---->不能查询院线
			else if((int) userRoleAndTheaterMap.get("roletype") == 2) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Privilege.code);
				return this.returnError(resultJson, ResMessage.Lack_Privilege.code, request);
			}
			
			
			List<Map<String, Object>> resultList = theaterDao.findTheaterChainList(paramsMap);
			
			//将时间类型转换为字符串
			if(resultList != null && resultList.size() > 0) {
				for (Map<String, Object> resultMap : resultList) {
					Object createtime = resultMap.get("createtime");
					Object updatetime = resultMap.get("updatetime");
					if(createtime != null) {
						String createtime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(createtime);
						resultMap.put("createtime", createtime_str);
					}
					if(updatetime != null) {
						String updatetime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(updatetime);
						resultMap.put("updatetime", updatetime_str);
					}
				}
			}
			
			resultJson.put("data", resultList);
			
		}catch(Exception e) {
			MyMongo.mErrorLog("查询院线列表操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询院线列表操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
		
	}
	
	
	/**
	 * 添加影院信息
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws CustomException 
	 */
	//url = http://localhost:8080/cinemacloud/rest/theater/addTheater
	@GET
	@POST
	@SystemServiceLog("添加影院")
	@RequiresPermissions("theater:create")
	@Path("/addTheater")
	public String addTheater(@Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException, CustomException {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String theaternum = request.getParameter("theaternum");				//影院编码
		String theatername = request.getParameter("theatername"); 			//影院名称
		String theaterphone = request.getParameter("theaterphone");			//联系电话
		
		String theateraddress = request.getParameter("theateraddress");		//详细地址
		String description = request.getParameter("description");			//影院描述(可选)
		String archiveArr = request.getParameter("archiveArr");				//归档属性（JSON数组）
//		String archiveArr = "[{archiveid:%201,%20archive_value:%201,archive_content:%20%27hehe%27}]";		//归档属性（JSON数组）
		
		/*		archive_Array = [
									{
										archiveid: 1, 
										archive_value: 1,
										archive_content: 'A类城市'
									},
									{
										archiveid: 101, 
										archive_value: 2,
										archive_content: '合作影院'
									}
								];
								
					archiveArr = encodeuricomponent(JSON.stringify(archive_Array));
		*/
		
		if(CodeUtil.checkParam(theaternum, theatername, theaterphone, theateraddress, archiveArr)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("theaternum", theaternum);
		paramsMap.put("theatername", URLDecoder.decode(theatername, "UTF-8"));
		paramsMap.put("theaterphone", theaterphone);
		
		paramsMap.put("theateraddress", URLDecoder.decode(theateraddress, "UTF-8"));
		
		if(!CodeUtil.checkParam(description)) {		//影院描述(可选)
			paramsMap.put("description", URLDecoder.decode(description, "UTF-8"));
		}

		//验证影院的重复性(影院名称和影院编码唯一)
		List<Map<String, Object>> repeatList = theaterDao.checkTheaterRepeat(paramsMap);
		if(repeatList != null && repeatList.size() > 0) {
			MyMongo.mRequestFail(request, ResMessage.Theater_NameORNum_Exist.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Theater_NameORNum_Exist.code));
		}
		
		//添加影院信息
		theaterDao.addTheater(paramsMap);
		
		//归档信息
		JSONArray jsonArray = JSONArray.fromObject(URLDecoder.decode(archiveArr, "UTF-8"));
		if(jsonArray.size() > 0) {	//说明归档数组不是空数组[]
			paramsMap.put("jsonArray", jsonArray);
			
			//添加影院的归档属性
			theaterDao.addArchive(paramsMap);
		}
		
		//============================  日志记录 BEGIN  ===================================
		String operatorName = ReVerifyFilter.getUsername(request, response);
		resultJson.put("returnValue", "操作员[" + operatorName + "]成功添加影院["+ paramsMap.get("theatername") +"]");
		//============================   日志记录 END   ===================================

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("添加影院信息操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
		
	}
	
	
	//url = http://localhost:8080/cinemacloud/rest/theater/updateTheater
	/**
	 * 更改影院信息
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws CustomException 
	 */
	@GET
	@POST
	@SystemServiceLog("更改影院信息")
	@RequiresPermissions("theater:update")
	@Path("/updateTheater")
	public String updateTheater(@Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException, CustomException {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String theaterid = request.getParameter("theaterid");		//影院ID
		String theaternum = request.getParameter("theaternum");		//影院编码
		String theatername = request.getParameter("theatername"); 	//影院名称
		
		
		String theaterphone = request.getParameter("theaterphone");	//联系电话
		String theateraddress = request.getParameter("theateraddress");	//详细地址
		String description = request.getParameter("description");	//影院描述 (可选)
		
		String archiveArr = request.getParameter("archiveArr");		//归档属性（JSON数组）
		/*
		 * 	archiveArr = [
						{
							archiveid: 1, 
							archive_value: 1,
							archive_content: 'A类城市'
						},
						{
							archiveid: 101, 
							archive_value: 2,
							archive_content: '合作影院'
						}
						];
		 */
		
		
		if(CodeUtil.checkParam(theaterid, theaternum, theatername, theaterphone, theateraddress, archiveArr)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("theaterid", theaterid);
		paramsMap.put("theaternum", theaternum);
		paramsMap.put("theatername", URLDecoder.decode(theatername, "UTF-8"));
		
		paramsMap.put("theaterphone", theaterphone);
		paramsMap.put("theateraddress", URLDecoder.decode(theateraddress, "UTF-8"));
		
		if(!CodeUtil.checkParam(description)) {			//影院描述 (可选)
			paramsMap.put("description", description);
		}
		
		//验证影院的重复性(影院名称和影院编码唯一)
		List<Map<String, Object>> repeatList = theaterDao.checkTheaterRepeat(paramsMap);
		if(repeatList != null && repeatList.size() > 0) {
			MyMongo.mRequestFail(request, ResMessage.Theater_NameORNum_Exist.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Theater_NameORNum_Exist.code));
		}
		
		//更新影院信息
		theaterDao.updateTheater(paramsMap);
		
		//更新影院的归档属性(先删除，再添加)
		theaterDao.deleteArchive(paramsMap);
		//归档属性(List<Map<String, Object>>)
		JSONArray jsonArray = JSONArray.fromObject(URLDecoder.decode(archiveArr, "UTF-8"));
		if(jsonArray.size() > 0) {
			paramsMap.put("jsonArray", jsonArray);
			
			//添加归档属性
			theaterDao.addArchive(paramsMap);
		}
		
		//============================  日志记录 BEGIN  ===================================
		String operatorName = ReVerifyFilter.getUsername(request, response);
		resultJson.put("returnValue", "操作员[" + operatorName + "]成功更改影院["+ paramsMap.get("theatername") +"]信息");
		//============================   日志记录 END   ===================================
			
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更改影院信息操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 删除影院信息及归档属性
	 * 		删除影院信息同时，必须保证该影院下角色和用户为空
	 * 			如果影院关联的角色和用户不为空，不能删除。
	 * @param request
	 * @return
	 * @throws CustomException 
	 */
	//http://localhost:8080/cinemacloud/rest/theater/deleteTheater?theaterid=111
	@GET
	@POST
	@SystemServiceLog("删除影院")
	@RequiresPermissions("theater:delete")
	@Path("/deleteTheater")
	public String deleteTheater(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String theaterid = request.getParameter("theaterid");		//影院ID
		
		if(CodeUtil.checkParam(theaterid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("theaterid", theaterid);
		

		//查询影院关联的用户是否为空,若为空则可以删除该影院
		List<Map<String, Object>> userList = userDao.findUserByTheaterid(paramsMap);
		if(userList != null && userList.size() > 0) {
			MyMongo.mRequestFail(request, ResMessage.User_of_Theater_NOTNULL.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.User_of_Theater_NOTNULL.code));
		}

		//查询该影院名称，用于日志记录
		Map<String, Object> theaterMap = theaterDao.findTheaterById(Integer.parseInt(theaterid));
		
		//删除影院信息
		theaterDao.deleteTheater(paramsMap);
		//删除影院的归档属性
		theaterDao.deleteArchive(paramsMap);
		
		
		//============================  日志记录 BEGIN  ===================================
		String operatorName = ReVerifyFilter.getUsername(request, response);
		resultJson.put("returnValue", "操作员[" + operatorName + "]成功删除影院["+ theaterMap.get("theatername") +"]信息");
		//============================   日志记录 END   ===================================

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除影院信息操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	/**
	 * 查询当前用户能够有权限查看的影院列表详细信息(及其归档属性信息)
	 * 
	 * @param request
	 * @return
	 * @throws CustomException 
	 */
	//url=http://localhost:8080/cinemacloud/rest/theater/findAllTheater?page=1&pagesize=10&criteria=111
	@GET
	@POST
	@RequiresPermissions("theater:view")
	@Path("findAllTheater")
	public String findAllTheater(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		Integer userid = (Integer) ReVerifyFilter.getUserid(request, response);
		String page = request.getParameter("page");
		String pagesize = request.getParameter("pagesize");
		String criteria = request.getParameter("criteria");	//可选字段 根据影院名进行模糊查询
		
		//userid不能为空
		if(CodeUtil.checkParam(String.valueOf(userid))) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}
		
		try {
			
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			
			//影院用户只能查看自己影院
			if((int) userMap.get("roletype") == 2) {
				paramsMap.put("theaterid", (int) userMap.get("theaterid"));
			}
			
 			if(!CodeUtil.checkParam(page, pagesize)) {
 				paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
 				paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
 			}
 			if(!CodeUtil.checkParam(criteria)) {
 				paramsMap.put("criteria", URLDecoder.decode(criteria, "UTF-8"));
 			}
			
 			Integer total = theaterDao.findAllTheaterCount(paramsMap);
			List<Map<String, Object>> resultList = theaterDao.findAllTheater(paramsMap);
			if(resultList != null && resultList.size() > 0) {
				for (Map<String, Object> resultMap : resultList) {
					if(resultMap.containsKey("createtime")) {
						String createtime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("createtime"));
						resultMap.put("createtime", createtime_str);
					}
					if(resultMap.containsKey("modifytime")) {
						String modifytime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("modifytime"));
						resultMap.put("modifytime", modifytime_str);
					}
					
					//查询该影院的归档属性信息
					List<Map<String, Object>> archiveList = theaterDao.findArchiveListByTheaterid((Integer) resultMap.get("theaterid"));
					if(archiveList != null && archiveList.size() > 0) {
						resultMap.put("archiveList", archiveList);
					}
				}
				
				resultJson.put("data", resultList);
				resultJson.put("total", total);
			}
			
		}catch(Exception e) {
			MyMongo.mErrorLog("获取影院详细信息操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("获取影院详细信息操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 查找所有影院，用于注册（仅仅用于注册，不用于添加）
	 * 
	 * @param request
	 * @return
	 */
	//url=http://localhost:8080/cinemacloud/rest/theater/findTheaterListForRegist
	@GET
	@POST
	@Path("findTheaterListForRegist")
	public String findTheaterListForRegist(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			
			String criteria = request.getParameter("criteria");	//可选字段 根据影院名进行模糊查询
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			if(!CodeUtil.checkParam(criteria)) {
				paramsMap.put("criteria", URLDecoder.decode(criteria, "UTF-8"));
			}
			
			List<Map<String, Object>> resultList = theaterDao.findTheaterListForRegist(paramsMap);
			if(resultList != null && resultList.size() > 0) {
				resultJson.put("data", resultList);
			}
		}catch(Exception e) {
			MyMongo.mErrorLog("查找所有影院，用于注册操作失败", request, e);
			return this.returnError(resultJson, ResMessage.User_Add_Fail.code, request);
		}

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查找所有影院，用于注册操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 查询能够查看的院线的简易信息(theaterid， theatername)
	 * @param request
	 * @param response
	 * @return
	 */
	//url=http://localhost:8080/cinemacloud/rest/theater/findTheaterSimpleInfoList
	@GET
	@POST
	@Path("findTheaterSimpleInfoList")
	public String findTheaterSimpleInfoList(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			
			int userid = (int) ReVerifyFilter.getUserid(request, response);
			String criteria = request.getParameter("criteria");	//可选字段 根据影院名进行模糊查询
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			if(!CodeUtil.checkParam(criteria)) {
				paramsMap.put("criteria", URLDecoder.decode(criteria, "UTF-8"));
			}
			
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
			
			//影院用户只能查看自己影院
			if((int) userMap.get("roletype") == 2) {
				paramsMap.put("theaterid", userMap.get("theaterid"));
			}
			
			List<Map<String, Object>> resultList = theaterDao.findTheaterSimpleInfoList(paramsMap);
			if(resultList != null && resultList.size() > 0) {
				resultJson.put("data", resultList);
			}
			
		}catch(Exception e) {
			MyMongo.mErrorLog("查询影院简易信息失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查找影院简易信息成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	/**
	 * 根据影院编码查询影院详细信息
	 * @param request
	 * @return
	 */
	@GET
	@POST
	@Path("findTheaterByNum")
	public String findTheaterByNum(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			String theaternum = request.getParameter("theaternum");
			
			if(CodeUtil.checkParam(theaternum)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			Map<String, Object> resultMap = theaterDao.findTheaterByNum(theaternum);
			
			if(resultMap != null && resultMap.size() > 0) {
				String createtime = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("createtime"));
				String modifytime = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("modifytime"));
				resultMap.put("createtime", createtime);	//将时间转换为String
				resultMap.put("modifytime", modifytime);	//将时间转换为String
			}
			resultJson.put("resultMap", resultMap);
			
			
		}catch(Exception e) {
			MyMongo.mErrorLog("根据影院编码查询影院详细信息操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据影院编码查询影院详细信息操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 查询当前用户能够有权限查看的影院ID，名称及其城市类别
	 * 
	 * @param request
	 * @return
	 * @throws CustomException 
	 */
	// url = http://localhost:8080/cinemacloud/rest/theater/findTheaterByCityLevel
	@GET
	@POST
//	@RequiresPermissions("theater:view")
	@Path("findTheaterByCityLevel")
	public String findTheaterByCityLevel(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		Integer userid = (Integer) ReVerifyFilter.getUserid(request, response);
		
		//userid不能为空
		if(CodeUtil.checkParam(String.valueOf(userid))) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}
		
		try {
			
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			
			//影院用户只能查看自己影院
			if((int) userMap.get("roletype") == 2) {
				paramsMap.put("theaterid", (int) userMap.get("theaterid"));
			}
			
			
			List<Map<String, Object>> resultList = theaterDao.findTheaterByCityLevel(paramsMap);

			if(resultList != null && resultList.size() > 0) {
				resultJson.put("data", resultList);
			}
			
		}catch(Exception e) {
			MyMongo.mErrorLog("查看影院及其城市类别操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查看影院及其城市类别操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
}
