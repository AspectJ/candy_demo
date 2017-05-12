package com.rest.log;

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
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.cp.util.DateFormatUtil;
import com.mongo.MyMongo;
import com.rest.log.dao.LogDaoImpl;
import com.rest.user.dao.UserDaoImpl;

import net.sf.json.JSONObject;

@Service
@Path("/rest/log")
public class LogRest extends BaseServlet {
	
	@Autowired
	@Qualifier("logDao")
	private LogDaoImpl logDao;
	
	@Autowired
	@Qualifier("userDao")
	private UserDaoImpl userDao;
	
	/**
	 *  查询用户能够看到的全部日志
	 *  	(影院用户只能 查看自己影院的全部日志)
	 *  		院线用户看不到系统用户的操作日志
	 * @param request
	 * @return
	 */
	//url = http://localhost:8080/cinemacloud/rest/log/getAllLogList?page=1&pagesize=10
	@GET
	@POST
	@RequiresPermissions("log:view")
	@Path("/getAllLogList")
	public String getAllLogList(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			
			String page = request.getParameter("page");
			String pagesize = request.getParameter("pagesize");
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			
			if(!CodeUtil.checkParam(page, pagesize)) {
				paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
				paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
			}
			
			
			//获取当前用户的userid
			int userid = (int) ReVerifyFilter.getUserid(request, response);
			if(CodeUtil.checkParam(String.valueOf(userid))) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
			
			//影院角色只能查看自己所属影院的操作日志
			paramsMap.put("roletype", (int) userMap.get("roletype"));
			if((int) userMap.get("roletype") == 2) {
				paramsMap.put("theaterid", (int) userMap.get("theaterid"));
			}
			
			paramsMap.put("userid", userid);
			
			List<Map<String, Object>> resultList = logDao.getLogList(paramsMap);
			if (resultList != null && resultList.size() > 0) {
				for (Map<String, Object> logMap : resultList) {
					if(logMap.containsKey("CREATE_TIME")) {
						String createtime_str =DateFormatUtil.obj_to_yyyy_MM_dd_HH_mm_ss_str(logMap.get("CREATE_TIME"));
						logMap.put("CREATE_TIME", createtime_str);
					}
				}
				
				//查询日志count
				Integer count = logDao.getLogListCount(paramsMap);
				
				resultJson.put("data", resultList);
				resultJson.put("total", count);
			}
			
			//说明点击了最新日志,则需要更改当前用户最后一次查看日志的时间为当前时间
			userDao.updateLastSeeLogTime(userid);
			
		}catch(Exception e) {
			MyMongo.mErrorLog("查询日志列表操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询日志列表操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 查询最新日志的count
	 * @param request
	 * @return
	 */
	//http://localhost:8080/cinemacloud/rest/log/getNewLogCount
	@GET
	@POST
	@RequiresPermissions("log:view")
	@Path("/getNewLogCount")
	public String getNewLogCount(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			
			//获取当前用户的userid
			int userid = (int) ReVerifyFilter.getUserid(request, response);
			if(CodeUtil.checkParam(String.valueOf(userid))) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//查询当前用户的角色类型及其所属影院(院线)
			Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
			
			//影院角色只能查看自己所属影院的操作日志
			if((int) userMap.get("roletype") == 2) {
				paramsMap.put("theaterid", (int) userMap.get("theaterid"));
			}
			
			paramsMap.put("userid", userid);
			paramsMap.put("isNew", 1);	//查看最新日志
			
			//查询日志count
			Integer count = logDao.getLogListCount(paramsMap);
			if(count != null && count != 0) {
				resultJson.put("total", count);
			}
			

			
		}catch(Exception e) {
			MyMongo.mErrorLog("查询最新日志条数失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询最新日志条数成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	

}
