package com.rest.user;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cp.bean.ResMessage;
import com.cp.bean.Role;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.cp.util.DateFormatUtil;
import com.mongo.MyMongo;
import com.rest.enterprize.MemberRest;
import com.rest.log.annotation.SystemServiceLog;
import com.rest.theater.dao.TheaterDaoImpl;
import com.rest.user.dao.UserDaoImpl;
import com.rest.user.role.dao.RoleDaoImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Path("/rest/user")
public class UserRest extends BaseServlet {

	@Autowired
	@Qualifier("userDao")
	private UserDaoImpl userDao;
	
	@Autowired
	@Qualifier("theaterDao")
	private TheaterDaoImpl theaterDao;
	
	@Autowired
	@Qualifier("roleDao")
	private RoleDaoImpl roleDao;
	
	
	private final static Logger logger = LoggerFactory.getLogger(UserRest.class);

	/**
	 * 
	 * @param request
	 * @throws IOException
	 * @throws ServletException
	 */
	// http://localhost:8080/cinemacloud/rest/user/getAllUser
	@GET
	@POST
	@Path("/getAllUser")
	public String getAllUser(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			
			String page = request.getParameter("page");
			String pagesize = request.getParameter("pagesize");
			if(CodeUtil.checkParam(page, pagesize)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
			paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
			
			List<Map<String, Object>> resultList = userDao.getAllUser(paramsMap);
			
			resultJson.put("data", resultList);
			
		}catch(Exception e) {
			MyMongo.mErrorLog("查询所有用户列表操作失败", request, e);
			return this.returnError(resultJson, ResMessage.User_Select_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询所有用户列表操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

	/**
	 * 根据用户关注企业号状态查询用户列表(同时可以对用户名进行模糊查询)(查询的是已审核的用户列表)(audit_flag=1)
	 * @param request
	 * @return
	 */
	//url = http://localhost:8080/cinemacloud/rest/user/getAuditedUserListByStatus?status=4
	@GET
	@POST
	@RequiresPermissions("user:view")
	@Path("/getAuditedUserListByStatus")
	public String getAuditedUserListByStatus(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			int userid = (int) ReVerifyFilter.getUserid(request, response);	//当前登录用户的userid
			String status = request.getParameter("status");
			String criteria = request.getParameter("criteria");
			String page = request.getParameter("page");
			String pagesize = request.getParameter("pagesize");
			
			if(CodeUtil.checkParam(String.valueOf(userid), status)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
			
			
			//根据关注状态获取企业号成员列表
			String result = MemberRest.getDepartmentMemberDetails(Integer.parseInt(status));
			
			//获取返回结果中的userlist字段
			JSONObject jsonOBJ = JSONObject.fromObject(result);
			Object userListOBJ = jsonOBJ.get("userlist");
			
			//将userlist字段转为JSONArray
			List<String> ids = new ArrayList<String>();
			JSONArray jsonArray = JSONArray.fromObject(userListOBJ); 
			
			//判断jsonArray（userList）是否为空
			if(jsonArray != null && jsonArray.size() > 0) {
				for (Object object : jsonArray) {
	 				JSONObject userObj = JSONObject.fromObject(object);
	 				ids.add((String) userObj.get("userid"));
				}
	 			
	 			Map<String, Object> paramsMap = new HashMap<String, Object>();
				/*
				 * roletype=0 系统用户能看到所有用户（系统，院线和影院）
				 * roletype=1 院线用户能看到的用户（院线和影院）
				 * roletype=2 影院用户能看到所属影院的用户（影院）
				 */
	 			paramsMap.put("roletype", (int) userMap.get("roletype"));
	 			if((int) userMap.get("roletype") == 2) {
	 				paramsMap.put("theaterid", (int) userMap.get("theaterid"));
	 			}
	 			
	 			paramsMap.put("ids", ids);
	 			//同时可以根据名称进行模糊查询
	 			if(!CodeUtil.checkParam(criteria)) {
	 				paramsMap.put("criteria", criteria);
	 			}
	 			if(!CodeUtil.checkParam(page, pagesize)) {
	 				paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
	 				paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
	 			}
	 			
	 			//根据企业号中成员查询已审核的成员列表
	 			List<Map<String, Object>> userList = userDao.getAuditedUserListByStatus(paramsMap);
	 			//查询数量，用于分页
	 			Integer userCount = userDao.getAuditedUserListByStatusCount(paramsMap);
	 			
	 			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
	 			
	 			
	 			for (Map<String, Object> resultMap : userList) {
					if(resultMap != null && resultMap.size() > 0) {
						//更改时间类型为字符串
						if(resultMap.containsKey("createtime")) {
							String createtime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("createtime"));
							resultMap.put("createtime", createtime_str);
						}
						if(resultMap.containsKey("modifytime")) {
							String modifytime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("modifytime"));
							resultMap.put("modifytime", modifytime_str);
						}
						if(resultMap.containsKey("lastlogintime")) {
							String lastlogintime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("lastlogintime"));
							resultMap.put("lastlogintime", lastlogintime_str);
						}
						
						//系统用户
						if((int) resultMap.get("roletype") == 0) {
							
						} 
						//院线用户
						else if((int) resultMap.get("roletype") == 1) {
							//根据id查询院线详细信息
							Map<String, Object> theaterChainMap = theaterDao.findTheaterChainById((Integer) resultMap.get("theaterchainid"));
							if(theaterChainMap != null && theaterChainMap.size() > 0) {
								resultMap.put("theaterchainname", theaterChainMap.get("theaterchainname"));
							}
						}
						//影院用户
						else if((int) resultMap.get("roletype") == 2) {
							//根据id查询影院详细信息
							Map<String, Object> theaterMap = theaterDao.findTheaterById((Integer) resultMap.get("theaterid"));
							if(theaterMap != null && theaterMap.size() > 0) {
								resultMap.put("theatername", theaterMap.get("theatername"));
							}
						}
						
						resultList.add(resultMap);
					}
				}
	 			resultJson.put("data", resultList);
	 			resultJson.put("total", userCount);
			}
			
		}catch(Exception e) {
			MyMongo.mErrorLog("根据用户关注企业号状态查询用户列表操作失败", request, e);
			return this.returnError(resultJson, ResMessage.User_Select_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据用户关注企业号状态查询用户列表操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 查询未审核用户列表
	 * @param request
	 * @param response
	 * @return
	 */
	// url = http://localhost:8080/cinemacloud/rest/user/getUnAuditedUserList
	@GET
	@POST
	@RequiresPermissions("user:view")
	@Path("/getUnAuditedUserList")
	public String getUnAuditedUserList(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			int userid = (int) ReVerifyFilter.getUserid(request, response);	//当前登录用户的userid
			String criteria = request.getParameter("criteria");
			String page = request.getParameter("page");
			String pagesize = request.getParameter("pagesize");
			
			if(CodeUtil.checkParam(String.valueOf(userid))) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			//影院用户只能查看自己影院的
			paramsMap.put("roletype", (int) userMap.get("roletype"));
			if((int) userMap.get("roletype") == 2) {
				paramsMap.put("theaterid", userMap.get("theaterid"));
			}
			
			//分页
 			if(!CodeUtil.checkParam(page, pagesize)) {
 				paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
 				paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
 			}
 			
 			//模糊查询
 			if(!CodeUtil.checkParam(criteria)) {
 				paramsMap.put("criteria", criteria);
 			}
			
			List<Map<String, Object>> userList = userDao.getUnAuditedUserList(paramsMap);
			
			Integer userCount = userDao.getUnAuditedUserListCount(paramsMap);
 			
 			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
 			
 			
 			for (Map<String, Object> resultMap : userList) {
				if(resultMap != null && resultMap.size() > 0) {
					//更改时间类型为字符串
					if(resultMap.containsKey("createtime")) {
						String createtime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("createtime"));
						resultMap.put("createtime", createtime_str);
					}
					if(resultMap.containsKey("modifytime")) {
						String modifytime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("modifytime"));
						resultMap.put("modifytime", modifytime_str);
					}
					if(resultMap.containsKey("lastlogintime")) {
						String lastlogintime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("lastlogintime"));
						resultMap.put("lastlogintime", lastlogintime_str);
					}
					
					//系统用户
					if((int) resultMap.get("roletype") == 0) {
						
					} 
					//院线用户
					else if((int) resultMap.get("roletype") == 1) {
						//根据id查询院线详细信息
						Map<String, Object> theaterChainMap = theaterDao.findTheaterChainById((Integer) resultMap.get("theaterchainid"));
						if(theaterChainMap != null && theaterChainMap.size() > 0) {
							resultMap.put("theaterchainname", theaterChainMap.get("theaterchainname"));
						}
					}
					//影院用户
					else if((int) resultMap.get("roletype") == 2) {
						//根据id查询影院详细信息
						Map<String, Object> theaterMap = theaterDao.findTheaterById((Integer) resultMap.get("theaterid"));
						if(theaterMap != null && theaterMap.size() > 0) {
							resultMap.put("theatername", theaterMap.get("theatername"));
						}
					}
					
					resultList.add(resultMap);
				}
			}
 			resultJson.put("data", resultList);
 			resultJson.put("total", userCount);
			
			
		}catch(Exception e) {
			MyMongo.mErrorLog("根据用户关注企业号状态查询用户列表操作失败", request, e);
			return this.returnError(resultJson, ResMessage.User_Select_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据用户关注企业号状态查询用户列表操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}



	/**
	 * 条件查询用户列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	// url = http://localhost:8080/cinemacloud/rest/user/findUserByCriteria
	@GET
	@POST
	@Path("/findUserByCriteria")
	public String findUserByCriteria(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		int userid = (int) ReVerifyFilter.getUserid(request, response);	//当前登录用户的userid
		String criteria = request.getParameter("criteria");		//用户名模糊查询
		String sel_roletype = request.getParameter("sel_roletype");		//根据角色类型查询	0-系统用户 1-院线用户 2-影院用户
		String mobile = request.getParameter("mobile");			//要查询的手机号码
		String audit_flag = request.getParameter("audit_flag");	//审核状态			0-未审核   1-已审核

		String status = request.getParameter("status");			//启用状态			0-禁用	  1-启用
		String isConcern = request.getParameter("isConcern");	//是否关注企业号		1-获取已关注成员列表  4-获取未关注成员列表
		String page = request.getParameter("page");
		String pagesize = request.getParameter("pagesize");


		Map<String, Object> paramsMap = new HashMap<String, Object>();
		if(!CodeUtil.checkParam(criteria)) {
			paramsMap.put("criteria", URLDecoder.decode(criteria, "UTF-8")) ;
		}
		if(!CodeUtil.checkParam(sel_roletype)) {
			paramsMap.put("sel_roletype", Integer.parseInt(sel_roletype)) ;
		}
		if(!CodeUtil.checkParam(mobile)) {
			paramsMap.put("mobile", mobile) ;
		}
		if(!CodeUtil.checkParam(audit_flag)) {
			paramsMap.put("audit_flag", audit_flag) ;
		}
		if(!CodeUtil.checkParam(status)) {
			paramsMap.put("status", status) ;
		}
		if(!CodeUtil.checkParam(isConcern)) {
			paramsMap.put("isConcern", isConcern) ;
		}

		//	如果查询影院用户，则可以根据影院查询
		if(!CodeUtil.checkParam(sel_roletype) && Integer.parseInt(sel_roletype) == 2) {
			String sel_theaterid = request.getParameter("sel_theaterid");		//影院id
			paramsMap.put("sel_theaterid", sel_theaterid);
		}
		//	分页参数
		if(!CodeUtil.checkParam(page, pagesize)) {
			paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
			paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
		}


		/**============================= 1 start ==================================*/
		//查询当前用户的角色类型及其所属影院(院线)
		Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);


		//影院用户只能查看自己影院的
		paramsMap.put("roletype", (int) userMap.get("roletype"));
		if((int) userMap.get("roletype") == 2) {
			paramsMap.put("theaterid", userMap.get("theaterid"));
		}
		/**============================= 1 end  ===================================*/


		/**============================= 2 start  =================================*/
		JSONArray jsonArray = null;
		if(!CodeUtil.checkParam(isConcern) && (Integer.parseInt(isConcern) == 1 || Integer.parseInt(isConcern) == 4)) {
			//根据关注状态获取企业号成员列表
			String result = MemberRest.getDepartmentMemberDetails(Integer.parseInt(isConcern));

			//获取返回结果中的userlist字段
			JSONObject jsonOBJ = JSONObject.fromObject(result);
			Object userListOBJ = jsonOBJ.get("userlist");

			//将userlist字段转为JSONArray
			List<String> ids = new ArrayList<String>();
			jsonArray = JSONArray.fromObject(userListOBJ);

			//判断jsonArray（userList）是否为空
			if(jsonArray != null && jsonArray.size() > 0) {
				for (Object object : jsonArray) {
					JSONObject userObj = JSONObject.fromObject(object);
					ids.add((String) userObj.get("userid"));
				}
				paramsMap.put("ids", ids);
			}
		}
		/**============================= 2 end  ==================================*/


		List<Map<String, Object>> userList = userDao.findUserByCriteria(paramsMap);
		Integer userCount = userDao.findUserByCriteriaCount(paramsMap);

		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> resultMap : userList) {
			if(resultMap != null && resultMap.size() > 0) {
				//更改时间类型为字符串
				if(resultMap.containsKey("createtime")) {
					String createtime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("createtime"));
					resultMap.put("createtime", createtime_str);
				}
				if(resultMap.containsKey("modifytime")) {
					String modifytime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("modifytime"));
					resultMap.put("modifytime", modifytime_str);
				}
				if(resultMap.containsKey("lastlogintime")) {
					String lastlogintime_str = DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(resultMap.get("lastlogintime"));
					resultMap.put("lastlogintime", lastlogintime_str);
				}

				//系统用户
				if((int) resultMap.get("roletype") == 0) {

				}
				//院线用户
				else if((int) resultMap.get("roletype") == 1) {
					//根据id查询院线详细信息
					Map<String, Object> theaterChainMap = theaterDao.findTheaterChainById((Integer) resultMap.get("theaterchainid"));
					if(theaterChainMap != null && theaterChainMap.size() > 0) {
						resultMap.put("theaterchainname", theaterChainMap.get("theaterchainname"));
					}
				}
				//影院用户
				else if((int) resultMap.get("roletype") == 2) {
					//根据id查询影院详细信息
					Map<String, Object> theaterMap = theaterDao.findTheaterById((Integer) resultMap.get("theaterid"));
					if(theaterMap != null && theaterMap.size() > 0) {
						resultMap.put("theatername", theaterMap.get("theatername"));
					}
				}

				resultList.add(resultMap);
			}
		}

		if(userList != null && userList.size() > 0) {
			if((!CodeUtil.checkParam(isConcern) && jsonArray != null && jsonArray.size() > 0)
					|| CodeUtil.checkParam(isConcern)) {
				resultJson.put("data", resultList);
				resultJson.put("total", userCount);
			}
		}



		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据用户关注企业号状态查询用户列表操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 根据userid查询用户详细信息
	 * 
	 * @param request
	 */
	// url = http://localhost:8080/cinemacloud/rest/user/findUserById?userid=116
	@GET
	@POST
	@Path("/findUserById")
	public String findUserById(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		try {
			String userid = request.getParameter("userid");

			if (CodeUtil.checkParam(userid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			Map<String, Object> userMap = userDao.findUserById(userid);

			resultJson.put("data", userMap);

		} catch (Exception e) {
			MyMongo.mErrorLog("根据userid查询用户详细信息操作失败", request, e);
			return this.returnError(resultJson, ResMessage.User_Select_Fail.code, request);
		}

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据userid查询用户详细信息操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

	/**
	 * 用户注册
	 * 		
	 * @param request
	 * @return
	 * @throws CustomException 
	 * @throws IOException 
	 */
	//url = http://192.168.1.135:8080/cinemacloud/rest/user/regist
	@GET
	@POST
	@SystemServiceLog("用户注册")
	@Path("/regist")
	public String regist(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, IOException {

		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
			
		String username = request.getParameter("username");		//用户名
		String password = request.getParameter("password");		//密码
		String realname = request.getParameter("realname");		//真实姓名
		String mobile = request.getParameter("mobile");			//联系方式
		String email = request.getParameter("email");			//邮箱
		/*
		 * 	String roleid = request.getParameter("roleid");			//角色id
		 */		
		String operatorid = request.getParameter("operatorid");	//操作员id
		String theaterid = request.getParameter("theaterid");	//影院id
		String theaterchainid = request.getParameter("theaterchainid");	//院线id PS：影院id和院线id二传其一即可
		
		if(CodeUtil.checkParam(username, password, realname, mobile, email)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("username", username);
		paramsMap.put("password", CodeUtil.MD5_Encrypt(username, password));
		paramsMap.put("realname", URLDecoder.decode(realname, "UTF-8"));
		paramsMap.put("mobile", mobile);
		paramsMap.put("email", email);
		
		//查询影院的角色"未审核用户"
		Map<String, Object> unAuditRole = roleDao.getUnAuditRoleInfo();
		Integer roleid = null;
		if(unAuditRole != null && unAuditRole.size() > 0) {
			roleid = (int) unAuditRole.get("roleid");
		}
		/*如果影院角色没有"未审核用户"，则创建该角色
				rolename = "未审核用户"	roletype = 2	status = 1
		 */
		else {
			Map<String, Object> unAuditParamsMap = new HashMap<String, Object>();
			unAuditParamsMap.put("rolename", "未审核用户");
			unAuditParamsMap.put("roletype", 2);
			unAuditParamsMap.put("status", 1);
			//添加影院角色 "未审核用户"
			roleDao.addUnAuditRole(unAuditParamsMap);
			
			roleid = (Integer) unAuditParamsMap.get("roleid");
		}
		paramsMap.put("roleid", roleid);  //影院角色"未审核用户"
		if(!CodeUtil.checkParam(operatorid)) {
			paramsMap.put("operatorid", Integer.parseInt(operatorid));	//操作员id 即当前登录用户id
		}
		if(!CodeUtil.checkParam(theaterid)) {
			paramsMap.put("theaterid", theaterid);
		}
		if(!CodeUtil.checkParam(theaterchainid)) {
			paramsMap.put("theaterchainid", theaterchainid);
		}


		// 判断用户名和手机号码是否已被注册(验证重复性)
		List<Map<String, Object>> repeatList = userDao.checkRepeat(paramsMap);
		if (repeatList != null && repeatList.size() > 0) {
			for (Map<String, Object> repeatMap : repeatList) {
				if (username.equals(repeatMap.get("username").toString())) { // 用户名已被注册
					MyMongo.mRequestFail(request, ResMessage.Username_is_regist.code);
					throw new CustomException(ResMessage.getMessage(ResMessage.Username_is_regist.code));
				} else if (mobile.equals(repeatMap.get("mobile"))) { // 手机号码已被注册
					MyMongo.mRequestFail(request, ResMessage.Mobile_Is_Regist.code);
					throw new CustomException(ResMessage.getMessage(ResMessage.Mobile_Is_Regist.code));
				}
			}
		}
		userDao.regist(paramsMap);

		
		//============================ 日志记录 BEGIN ===================================
		String message = null;
		if(!CodeUtil.checkParam(operatorid)) {
			//查询当前登录用户（操作员）的用户名
			String operatorName = ReVerifyFilter.getUsername(request, response);
			message = "操作员["+ operatorName +"]成功添加用户["+ username +"]";
		} else {
			message = "用户["+ username +"]成功注册";
		}
		
		resultJson.put("returnValue", message);
		//============================  日志记录 END  ===================================
			

			
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("用户注册/管理员添加用户操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);

	}
	
	
	/**
	 * 添加用户
	 * 		
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException
	 * @throws IOException
	 */
	@GET
	@POST
	@SystemServiceLog("添加用户")
	@RequiresPermissions("user:create")
	@Path("/addUser")
	public String addUser(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, IOException {

		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
			
		String username = request.getParameter("username");		//用户名
		String password = request.getParameter("password");		//密码
		String realname = request.getParameter("realname");		//真实姓名
		String mobile = request.getParameter("mobile");			//联系方式
		String email = request.getParameter("email");			//邮箱
		String roleid = request.getParameter("roleid");			//角色id
		String operatorid = request.getParameter("operatorid");	//操作员id
		String theaterid = request.getParameter("theaterid");	//影院id
		String theaterchainid = request.getParameter("theaterchainid");	//院线id PS：影院id和院线id二传其一即可
		
		if(CodeUtil.checkParam(username, password, realname, mobile, email, roleid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("username", username);
		paramsMap.put("password", CodeUtil.MD5_Encrypt(username, password));
		paramsMap.put("realname", URLDecoder.decode(realname, "UTF-8"));
		paramsMap.put("mobile", mobile);
		paramsMap.put("email", email);
		paramsMap.put("roleid", Integer.parseInt(roleid));
		if(!CodeUtil.checkParam(operatorid)) {
			paramsMap.put("operatorid", Integer.parseInt(operatorid));	//操作员id 即当前登录用户id
		}
		if(!CodeUtil.checkParam(theaterid)) {
			paramsMap.put("theaterid", theaterid);
		}
		if(!CodeUtil.checkParam(theaterchainid)) {
			paramsMap.put("theaterchainid", theaterchainid);
		}


		// 判断用户名和手机号码是否已被注册(验证重复性)
		List<Map<String, Object>> repeatList = userDao.checkRepeat(paramsMap);
		if (repeatList != null && repeatList.size() > 0) {
			for (Map<String, Object> repeatMap : repeatList) {
				if (username.equals(repeatMap.get("username").toString())) { // 用户名已被注册
					MyMongo.mRequestFail(request, ResMessage.Username_is_regist.code);
					throw new CustomException(ResMessage.getMessage(ResMessage.Username_is_regist.code));
				} else if (mobile.equals(repeatMap.get("mobile"))) { // 手机号码已被注册
					MyMongo.mRequestFail(request, ResMessage.Mobile_Is_Regist.code);
					throw new CustomException(ResMessage.getMessage(ResMessage.Mobile_Is_Regist.code));
				}
			}
		}
		userDao.regist(paramsMap);

		//============================ 日志记录 BEGIN ===================================
		String message = null;
		if(!CodeUtil.checkParam(operatorid)) {
			//查询当前登录用户（操作员）的用户名
			String operatorName = ReVerifyFilter.getUsername(request, response);
			message = "操作员["+ operatorName +"]成功添加用户["+ username +"]";
		} else {
			message = "用户["+ username +"]成功注册";
		}
		
		resultJson.put("returnValue", message);
		//============================  日志记录 END  ===================================
			

			
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("用户注册/管理员添加用户操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);

	}

	/**
	 * 审核用户 
	 * 		若用户审核为"审核"，
	 * 			则判断是否已为其在企业号中创建成员 若未创建，则为其创建成员 若用户从"未审核"审核为"审核"，则...
	 * 
	 * @throws IOException
	 * @throws CustomException
	 */
	//http://localhost:8080/cinemacloud/rest/user/auditUser?userid=123?audit_flag=0
	@GET
	@POST
	@SystemServiceLog("用户审核")
	@RequiresPermissions("user:audit")
	@Path("/auditUser")
	public String auditUser(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, IOException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		String userid = request.getParameter("userid");
		
		if(CodeUtil.checkParam(userid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}

		Map<String, Object> userMap = userDao.findUserById(userid);
		Integer audit_flag = (Integer) userMap.get("audit_flag"); //原有状态
		//status要更新成的审核状态
		audit_flag = (audit_flag == 0) ? 1 : 0;
		
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("userid", userid);
		paramsMap.put("audit_flag", audit_flag);
		
		//如果更改为已审核，则需要为其赋角色
		if(audit_flag == 1) {
			String roleid = request.getParameter("roleid");
			
			if(CodeUtil.checkParam(roleid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			
			paramsMap.put("roleid", roleid);
		}


		userDao.auditUser(paramsMap); //更改用户状态
		
		if(audit_flag == 1) { //若更改状态为1，说明则改为启用状态，判断是否已创建成员
			Boolean isCreate = (Boolean) userMap.get("isCreate");
			if(!isCreate) {  //如果否，则为企业号创建该成员
				int errcode = MemberRest.createMember(userMap);
				if(errcode != 0) {
					logger.error("【{}-<{}>】{}", 
							new Object[] { "createMember", System.currentTimeMillis(), "创建成员操作失败！" });
					throw new CustomException("创建成员操作失败！/审核用户操作失败！");
				}else {
					//若创建用户成功则更改用户表isCreate字段为true/1
					userDao.updateIsCreate(Integer.parseInt(userid));
					logger.info("【{}-<{}>】{}", 
							new Object[] { "createMember", System.currentTimeMillis(), "创建成员操作成功！" });
				}
			}
		}

		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		//查询被审核用户的用户名
		String dummy_username = null; 
		Map<String, Object> returnMap = userDao.findUserById(userid);
		if(returnMap != null && returnMap.containsKey("username")) {
			dummy_username = (String) returnMap.get("username");
		}
		
		String message = (audit_flag == 0 ? "未审核" : "审核");
		
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功更改用户["+ dummy_username +"]审核状态为" + message);
		
		//============================  日志记录 END  ===================================
		
		
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("审核用户操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 用户启用/禁用
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException
	 * @throws IOException
	 */
	//http://localhost:8080/cinemacloud/rest/user/changeUserStatus?userid=123
	@GET
	@POST
	@SystemServiceLog("用户启用/禁用")
	@RequiresPermissions("user:audit")
	@Path("/changeUserStatus")
	public String changeUserStatus(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, IOException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		String userid = request.getParameter("userid");
		
		if(CodeUtil.checkParam(userid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}

		Map<String, Object> userMap = userDao.findUserById(userid);
		Integer status = (Integer) userMap.get("status"); //原有状态
		//status要更新成的审核状态
		status = (status == 0) ? 1 : 0;

		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("userid", userid);
		paramsMap.put("status", status);

		userDao.changeUserStatus(paramsMap); //更改用户状态
		

		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		//查询被审核用户的用户名
		String dummy_username = null; 
		Map<String, Object> returnMap = userDao.findUserById(userid);
		if(returnMap != null && returnMap.containsKey("username")) {
			dummy_username = (String) returnMap.get("username");
		}
		
		String message = (status == 0 ? "禁用" : "启用");
		
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功更改用户["+ dummy_username +"]启用/禁用状态为" + message);
		
		//============================  日志记录 END  ===================================
		
		
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("审核用户操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	/**
	 * 更改用户信息
	 * @param request
	 * @return
	 * @throws CustomException 
	 */
	//url = http://localhost:8080/cinemacloud/rest/user/updateUserInfo?userid=123&email=123@qq.com&roleid=123
	@GET
	@POST
	@SystemServiceLog("更改用户信息")
	@RequiresPermissions("user:update")
	@Path("/updateUserInfo")
	public String updateUserInfo(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException{
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		
		String userid = request.getParameter("userid");
		String email = request.getParameter("email");
		String roleid = request.getParameter("roleid");

		if (CodeUtil.checkParam(userid, email, roleid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("userid", userid);
		paramsMap.put("email", email);
		paramsMap.put("roleid", roleid);
		
		userDao.updateUserInfo(paramsMap);
		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		String dummy_name = null;
		Map<String, Object> returnMap = userDao.findUserById(userid);
		if(returnMap != null && returnMap.containsKey("username")) {
			dummy_name = (String) returnMap.get("username");
		}
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功更改用户["+ dummy_name +"]信息");
		
		//============================  日志记录 END  ===================================
		

		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更改用户信息操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 用户更改密码
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	//url = http://localhost:8080/cinemacloud/rest/user/updatePassword?userid=123&password=123
	@GET
	@POST
	@SystemServiceLog("更改密码")
	@Path("/updatePassword")
	public String updatePassword(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException{
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		
		String userid = request.getParameter("userid");
		String oldPassword = request.getParameter("oldPassword");
		String newPassword = request.getParameter("newPassword");

		if (CodeUtil.checkParam(userid, oldPassword, newPassword)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		//获取用户名
		String username = null;
		Map<String, Object> userMap = userDao.findUserById(userid);
		if(userMap != null && userMap.containsKey("username")) {
			username = (String) userMap.get("username");
		}
		//数据库中保存的密码
		String hashPass = (String) userMap.get("password");
		if(!CodeUtil.MD5_Encrypt(username, oldPassword).equals(hashPass)) {	//必须确认原密码正确
			MyMongo.mRequestFail(request, ResMessage.OldPass_Error.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.OldPass_Error.code));
		}
		if(CodeUtil.MD5_Encrypt(username, newPassword).equals(hashPass)) {	//新密码和原密码不能相同
			MyMongo.mRequestFail(request, ResMessage.Two_Pass_Same.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Two_Pass_Same.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("userid", userid);
		paramsMap.put("password", CodeUtil.MD5_Encrypt(username, newPassword));

		userDao.updatePassword(paramsMap);
		
		//============================ 日志记录 BEGIN ===================================
		
		resultJson.put("returnValue", "用户["+ username +"]成功更改密码");
		
		//============================  日志记录 END  ===================================
		

		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更改用户信息操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	

	/**
	 * 删除用户 
	 * 		同时删除企业号中该用户的成员信息
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws CustomException
	 */
	//url = http://localhost:8080/cinemacloud/rest/user/deleteUser?userid=140
	@GET
	@POST
	@SystemServiceLog("删除用户")
	@RequiresPermissions("user:delete")
	@Path("/deleteUser")
	public String deleteUser(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, CustomException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		String userid = request.getParameter("userid");

		if (CodeUtil.checkParam(userid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}

		// 查询该用户的isCreate字段，判断是否已拥有企业号中成员身份
		Map<String, Object> userMap = userDao.findUserById(userid);
		Boolean bool = (Boolean) userMap.get("isCreate");

		// 删除用户表中该用户信息
		userDao.deleteUser(Integer.parseInt(userid));

		if (bool) {
			// 删除企业号中该用户的成员信息
			int errcode = MemberRest.deleteMember(userid);
			if (errcode != 0) {
				logger.error("【{}-<{}>】{}", new Object[] { "deleteMember", System.currentTimeMillis(), "企业号中删除成员失败！" });
				throw new CustomException("企业号中删除成员失败！");
			}
			
		}
		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request,response);
		//查询被审核用户的用户名
		String dummy_username = null; 
		Map<String, Object> returnMap = userDao.findUserById(userid);
		if(returnMap != null && returnMap.containsKey("username")) {
			dummy_username = (String) returnMap.get("username");
		}
		
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功删除用户["+ dummy_username +"]");
		
		//============================  日志记录 END  ===================================
		
		
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		String message = bool == true ? "删除用户且删除该用户企业号中成员身份操作成功" : "删除用户操作成功";
		MyMongo.mRequestLog(message, etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

	/**
	 * 登陆
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 * @throws CustomException
	 */
	//http://localhost:8080/cinemacloud/rest/user/login?username=777&password=777
	@GET
	@POST
	@SystemServiceLog("用户登录")
	@Path("/login")
	public String login(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, IOException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		Map<String, Object> userMap = null;
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		if (CodeUtil.checkParam(username, password)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		if (password.length() > 12) {	//密码长度不能大于12位
			MyMongo.mRequestFail(request, ResMessage.Userlogin_Pass_Fail.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Userlogin_Pass_Fail.code));
		}
		username=URLDecoder.decode(username, "UTF-8");

		// 获取权限认证主体Subject
		Subject currentUser = SecurityUtils.getSubject();
		// 若已经登陆过，则踢出，重新登录
		if (currentUser.isAuthenticated()){
			currentUser.logout();
		}
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		token.setRememberMe(true);

		try {
			currentUser.login(token);
			// 查询该用户信息
			userMap = userDao.login(username);
			Session session = currentUser.getSession(false);
			session.setAttribute("userid", userMap.get("userid"));
			session.setAttribute("username", username);
			
			//更改该用户的最后一次登录时间
			userDao.updateLastLoginTime((int) userMap.get("userid"));
			
			// 是否已经扫描关注了企业号
			Integer concern_status = MemberRest.getMemberInfo((Integer) userMap.get("userid"));
			userMap.put("concern_status", concern_status);
			
			//查询当前用户的角色信息及其权限
			Role role = roleDao.findRoleByRoleid((int) userMap.get("roleid"));
			userMap.put("role", role);
			
			resultJson.put("data", userMap);
			
		} catch (UnknownAccountException uae) { // 用户不存在
			MyMongo.mRequestFail(request, ResMessage.Userlogin_Name_Fail.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Userlogin_Name_Fail.code));
		} catch (IncorrectCredentialsException ice) { // 密码错误
			MyMongo.mRequestFail(request, ResMessage.Userlogin_Pass_Fail.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Userlogin_Pass_Fail.code));
		} catch (LockedAccountException lae) { // 用户被锁定
			MyMongo.mRequestFail(request, ResMessage.Userlogin_Status_Fail.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Userlogin_Status_Fail.code));
		} catch(DisabledAccountException dae) {//用户所属角色状态不可用
			MyMongo.mRequestFail(request, ResMessage.Role_Status_Fail.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Role_Status_Fail.code));
		} catch (AuthenticationException ae) {
			MyMongo.mRequestFail(request, ResMessage.User_Login_fail.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.User_Login_fail.code));
		}

		
		//============================  日志记录 BEGIN  ===================================
		
		resultJson.put("returnValue", "用户[" + userMap.get("username") + "]成功登入系统");
		//============================   日志记录 END   ===================================

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("用户登录成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

	public void loginByUserid(String userid){
		Map<String,Object> usermap=userDao.findUserById(userid);
		String username=(String)usermap.get("username");
		String password=(String)usermap.get("password");

		// 获取权限认证主体Subject
		Subject currentUser = SecurityUtils.getSubject();
		// 若已经登陆过，则踢出，重新登录
		if (currentUser.isAuthenticated()){
			currentUser.logout();
		}
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		token.setRememberMe(true);
		currentUser.login(token);

		Map<String,Object> userMap = userDao.login(username);
		Session session = currentUser.getSession(false);
		session.setAttribute("userid", userMap.get("userid"));
		session.setAttribute("username", username);

	}
	
	
}
