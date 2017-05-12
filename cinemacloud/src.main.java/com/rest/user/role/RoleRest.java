package com.rest.user.role;

import com.cp.bean.ResMessage;
import com.cp.bean.Role;
import com.cp.bean.Role_Menu;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.shiro.realms.ShiroRealm;
import com.cp.util.CodeUtil;
import com.mongo.MyMongo;
import com.rest.log.annotation.SystemServiceLog;
import com.rest.menu.dao.MenuDaoImpl;
import com.rest.theater.dao.TheaterDaoImpl;
import com.rest.user.dao.UserDaoImpl;
import com.rest.user.role.dao.RoleDaoImpl;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * 角色
 * @author john
 *
 */
@Service
@Path("/rest/role")
public class RoleRest extends BaseServlet{
	
	@Autowired
	@Qualifier("roleDao")
	private RoleDaoImpl roleDao;
	
	@Autowired
	@Qualifier("userDao")
	private UserDaoImpl userDao;
	
	@Autowired
	@Qualifier("theaterDao")
	private TheaterDaoImpl theaterDao;
	
	@Autowired
	@Qualifier("menuDao")
	private MenuDaoImpl menuDao;

	@Autowired
	private ShiroRealm shiroRealm;
	
	/**
	 * 添加角色信息
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws CustomException 
	 */
	//url = http://localhost:8080/cinemacloud/rest/role/addRole?rolename=999&roletype=1&theaterchainid=1
	@GET
	@POST
	@SystemServiceLog("角色添加")
	@RequiresPermissions("role:add")
	@Path("/addRole")
	public String addRole(@Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException, CustomException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String rolename = request.getParameter("rolename");
		String roletype = request.getParameter("roletype"); 	//角色类别  0-系统用户   1-院线用户  2-影院用户
		
		if(CodeUtil.checkParam(rolename, roletype)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("rolename", URLDecoder.decode(rolename, "UTF-8"));	//角色名称
		paramsMap.put("roletype", Integer.parseInt(roletype));	//角色类别
		
		//重复性校验(rolename校验，不能重复)
		Integer repeatCount = roleDao.checkRepeat(paramsMap);
		if(repeatCount > 0) {
			MyMongo.mRequestFail(request, ResMessage.Role_Name_Exists.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Role_Name_Exists.code));
		}
		
		
		roleDao.addRole(paramsMap);
		
		//查询新增的这条数据，并发送给前端
		Role role = roleDao.findRoleByRoleid((int) paramsMap.get("roleid"));
		resultJson.put("data", role);
		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功添加角色["+ paramsMap.get("rolename") +"]");
		
		//============================  日志记录 END  ===================================
			
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("添加角色信息成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	/**
	 * 删除角色信息
	 * 		若该角色关联的用户，不为空，则不能删除
	 * @param request
	 * @return
	 * @throws CustomException 
	 */
	//url = http://localhost:8080/cinemacloud/rest/role/deleteRole?roleid=123
	@GET
	@POST
	@SystemServiceLog("角色删除")
	@RequiresPermissions("role:delete")
	@Path("/deleteRole")
	public String deleteRole(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException{
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String roleid = request.getParameter("roleid");		//角色id
		
		if(CodeUtil.checkParam(roleid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		/**
		 * 查询该角色下用户是否为空
		 * 		若不为空，则不能删除
		 */
		List<Map<String, Object>> userList = userDao.getUserListByRoleid(roleid);
		if(userList != null && userList.size() > 0) {
			MyMongo.mRequestFail(request, ResMessage.User_of_Role_NOTNULL.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.User_of_Role_NOTNULL.code));
		}
		
		//删除角色
		roleDao.deleteRole(roleid);
			
		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		Map<String, Object> returnMap = roleDao.findRoleInfoById(Integer.parseInt(roleid));
		String rolename = null;
		if(returnMap != null && returnMap.containsKey("rolename")) {
			rolename = (String) returnMap.get("rolename");
		}
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功删除角色["+ rolename +"]");
		
		//============================  日志记录 END  ===================================
			
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除角色成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	/**
	 * 更改角色信息(角色名)
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws CustomException 
	 */
	//url = http://localhost:8080/cinemacloud/rest/role/updateRoleInfo
	@GET
	@POST
	@SystemServiceLog("更改角色信息")
	@RequiresPermissions("role:update")
	@Path("/updateRoleInfo")
	public String updateRoleInfo(@Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException, CustomException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String roleid = request.getParameter("roleid");		//角色id
		String rolename = request.getParameter("rolename");	//角色名称
		
		if(CodeUtil.checkParam(roleid, rolename)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("roleid", roleid);	//角色名称
		paramsMap.put("rolename", URLDecoder.decode(rolename, "UTF-8"));	//角色类别
		
		
		roleDao.updateRoleInfo(paramsMap);
		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功更改角色["+ rolename +"]信息（名称）");
		
		//============================  日志记录 END  ===================================
			
			
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更改角色信息成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	/**
	 * 更改角色状态
	 * @param request
	 * @return
	 * @throws CustomException 
	 */
	//http://localhost:8080/cinemacloud/rest/role/auditRoleStatus
	@GET
	@POST
	@SystemServiceLog("审核角色")
	@RequiresPermissions("role:audit")
	@Path("/auditRoleStatus")
	public String auditRoleStatus(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String roleid = request.getParameter("roleid");		//角色id
		String status = request.getParameter("status");		//角色状态
		
		if(CodeUtil.checkParam(roleid, status)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("roleid", roleid);
		paramsMap.put("status", status);
		
		roleDao.auditRoleStatus(paramsMap);
		
		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		Map<String, Object> returnMap = roleDao.findRoleInfoById(Integer.parseInt(roleid));
		String rolename = null;
		if(returnMap != null && returnMap.containsKey("rolename")) {
			rolename = (String) returnMap.get("rolename");
		}
		String message = Integer.parseInt(status) == 0 ? "禁用" : "可用";
		
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功更改角色["+ rolename +"]状态为" + message);
		
		//============================  日志记录 END  ===================================
			
			
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更改角色状态成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	//http://localhost:8080/cinemacloud/rest/role/findRolenameBySystem?roletype=2
	/**
	 * 根据角色类型查询拥有角色信息(未查询角色的权限信息)
	 * @param request
	 * @return
	 */
	@GET
	@POST
	@Path("/findRolenameBySystem")
	public String findRolenameBySystem(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			String roletype = request.getParameter("roletype");	//角色类别  0-系统用户   1-院线用户  2-影院用户
			if(CodeUtil.checkParam(roletype)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("roletype", Integer.parseInt(roletype));
			
			
			List<Map<String, Object>> resultList = roleDao.findRolenameBySystem(paramsMap);
			if(resultList != null && resultList.size() > 0) {
				resultJson.put("data", resultList);
			}
			
		} catch(Exception e) {
			MyMongo.mErrorLog("根据角色类型查询拥有角色信息操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
			
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据角色类型查询拥有角色信息操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 条件查询角色信息列表及其已有权限信息(可根据角色名模糊查询，同时可根据角色类型查询)
	 * @param request
	 * @return
	 */
	//url=http://localhost:8080/cinemacloud/rest/role/findRoleListByCriteria
	@GET
	@POST
	@RequiresPermissions("role:view")
	@Path("/findRoleListByCriteria")
	public String findRoleListByCriteria(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			
			//========================= 查询条件  BEGIN ================================
			String sel_criteria = request.getParameter("criteria");
			String sel_roletype = request.getParameter("roletype");
			String page = request.getParameter("page");				//要查询页面
			String pagesize = request.getParameter("pagesize");		//每页查询多少条数据
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			if(!CodeUtil.checkParam(sel_criteria)) {
				paramsMap.put("sel_criteria", URLDecoder.decode(sel_criteria, "UTF-8"));
			}
			if(!CodeUtil.checkParam(sel_roletype)) {
				paramsMap.put("sel_roletype", sel_roletype);
			}
			
 			if(!CodeUtil.checkParam(page, pagesize)) {
 				paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
 				paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
 			}
			//========================= 查询条件 END ================================
			
			
			
			//========================= 当前用户角色 ================================
			//当前用户角色类型及所属院线(影院信息)
			int userid = (int) ReVerifyFilter.getUserid(request, response);
			
			//userid不能为空
			if(CodeUtil.checkParam(String.valueOf(userid))) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
			
			paramsMap.put("user_roletype", (int) userMap.get("roletype"));
			
			List<Role> resultList = roleDao.findRoleListByCriteria(paramsMap);
			if(resultList != null && resultList.size() > 0) {
				for (Role role : resultList) {
					
					//查询权限列表并set
					List<Role_Menu> menuList = menuDao.findMenuListByRoleid(role.getRoleid());
					role.setMenuList(menuList);
				}

				Integer count = roleDao.findRoleListByCriteriaCount(paramsMap);
				
				resultJson.put("data", resultList);
				resultJson.put("total", count);
			}
			
		} catch(Exception e) {
			MyMongo.mErrorLog("根据条件查询角色信息列表失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
			
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据条件查询角色信息列表成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 为角色授权
	 * @param request
	 * @return
	 * @throws CustomException 
	 */
	//http://localhost:8080/cinemacloud/rest/role/authorize?roleid=1&menuids=1_2_3_6
	@GET
	@POST
	@SystemServiceLog("角色授权")
	@RequiresPermissions("role:authorize")
	@Path("/authorize")
	public String authorize(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String roleid = request.getParameter("roleid");
		String menuids = request.getParameter("menuids");
		int userid = (int) ReVerifyFilter.getUserid(request, response);
		
		if(CodeUtil.checkParam(roleid, menuids, String.valueOf(userid))) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		//查询当前用户的角色类型及其所属影院(院线)
		Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
		//不能更改自己所属角色的权限
		if((int) userMap.get("roleid") == Integer.parseInt(roleid)) {
			MyMongo.mRequestFail(request, ResMessage.Not_Change_OwnRole_Menu.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Not_Change_OwnRole_Menu.code));
		}
		
		String[] menuidArr = menuids.split("_");
		List<String> menuIdlist = new ArrayList<String>();
		Collections.addAll(menuIdlist, menuidArr);		//将数组中的数据拷贝到list中
		
		//授权前先删除该角色所有权限，再重新授权
		menuDao.deleteMenuByRoleid(roleid);
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("roleid", roleid);
		paramsMap.put("menuIdlist", menuIdlist);
		
		//授权
		roleDao.addPermission(paramsMap);


		/**
		 * 	授权之后，清除该角色下可登陆用户的缓存
		 */
		//查询被授权角色下的可能登陆用户
		List<Map<String, Object>> mabeyLoginUserList = userDao.findMabeyLoginUserList(roleid);
		PrincipalCollection principals = null;
		for (Map<String, Object> loginUserMap: mabeyLoginUserList) {
		    principals = new SimplePrincipalCollection(loginUserMap.get("username"), shiroRealm.getName());
			shiroRealm.clearCachedAuthorizationInfo(principals);
		}

		//==============================================返回数据 BEGIN ===========================================================
		//查询授权之后的该条角色最新数据
		Role role = roleDao.findRoleByRoleid(Integer.valueOf(roleid));
		
		//==============================================返回数据  END ===========================================================
		
		resultJson.put("data", role);
		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		Map<String, Object> returnMap = roleDao.findRoleInfoById(Integer.parseInt(roleid));
		String rolename = null;
		if(returnMap != null && returnMap.containsKey("rolename")) {
			rolename = (String) returnMap.get("rolename");
		}
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功为角色["+ rolename +"]授权/更改权限");
		
		//============================  日志记录 END  ===================================
		
			
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("为角色授权操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

}
