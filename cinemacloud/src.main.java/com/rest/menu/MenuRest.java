package com.rest.menu;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cp.bean.Menu;
import com.cp.bean.ResMessage;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.mongo.MyMongo;
import com.rest.menu.dao.MenuDaoImpl;
import com.rest.user.dao.UserDaoImpl;

import net.sf.json.JSONObject;

@Service
@Path("/rest/menu")
public class MenuRest extends BaseServlet {
	
	@Autowired
	@Qualifier("menuDao")
	private MenuDaoImpl menuDao;

	@Autowired
	@Qualifier("userDao")
	private UserDaoImpl userDao;
	
	/**
	 * 根据角色ID删除该角色所有权限
	 * @param request
	 * @return
	 */
	@GET
	@POST
	@Path("/deleteMenuByRoleid")
	public String deleteMenuByRoleid(@Context HttpServletRequest request, @Context HttpServletResponse response){
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		
		try {
			String roleid = request.getParameter("roleid");

			if (CodeUtil.checkParam(roleid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			menuDao.deleteMenuByRoleid(roleid);

		} catch (Exception e) {
			MyMongo.mErrorLog("根据角色ID删除该角色所有权限操作失败", request, e);
			return this.returnError(resultJson, ResMessage.User_Select_Fail.code, request);
		}
		
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据角色ID删除该角色所有权限操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 查询出系统所有权限
	 * @param request
	 * @return
	 */
	//url = http://localhost:8080/cinemacloud/rest/menu/findAllMenu
	@GET
	@POST
	@Path("/findAllMenu")
	public String findAllMenu(@Context HttpServletRequest request, @Context HttpServletResponse response){
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		try {
			
			List<Menu> menuList = menuDao.findAllMenu();
			
			if(menuList != null && menuList.size() > 0) {
				resultJson.put("data", menuList);
			}

		} catch (Exception e) {
			MyMongo.mErrorLog("查询系统全部权限操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询系统全部权限操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 查询当前用户所属角色的权限列表
	 * @param request
	 * @return
	 */
	//url = http://localhost:8080/cinemacloud/rest/menu/findAvailableMenuList
	@GET
	@POST
	@Path("/findAvailableMenuList")
	public String findAvailableMenuList(@Context HttpServletRequest request, @Context HttpServletResponse response){
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------

		try {
			
			Integer userid = (Integer) ReVerifyFilter.getUserid(request, response);
			if (CodeUtil.checkParam(String.valueOf(userid))) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
			
			List<Menu> menuList = menuDao.findAvailableMenuList((int) userMap.get("roleid"));
			
			if(menuList != null && menuList.size() > 0) {
				resultJson.put("data", menuList);
			}

		} catch (Exception e) {
			MyMongo.mErrorLog("查询当前用户角色的权限列表操作失败", request, e);
			return this.returnError(resultJson, ResMessage.User_Select_Fail.code, request);
		}
		
		//-------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询当前用户角色的权限列表操作成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	
}
