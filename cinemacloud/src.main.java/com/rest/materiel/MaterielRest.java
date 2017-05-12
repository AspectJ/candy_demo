package com.rest.materiel;

import com.cp.bean.ResMessage;
import com.cp.bean.SuppliesBean;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.cp.util.DateFormatUtil;
import com.cp.util.FileUtil;
import com.cp.util.ReadExcelUtil;
import com.mongo.MyMongo;
import com.redis.UserRedisImpl;
import com.rest.log.annotation.SystemServiceLog;
import com.rest.materiel.dao.MaterielDaoImpl;
import com.rest.template.dao.TemplateDaoImpl;
import com.rest.theater.dao.TheaterDaoImpl;
import com.rest.user.UserRest;
import com.rest.user.dao.UserDaoImpl;
import com.wx.WXTools;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.cp.filter.ReVerifyFilter.getUsername;

@Path("/rest/materiel")
@Service
public class MaterielRest extends BaseServlet {

	@Autowired
	private MaterielDaoImpl materielDao;

	@Autowired
	private UserRedisImpl userRedis;
	@Autowired
	private WXTools wxTools;
	@Autowired
	private TheaterDaoImpl theaterDao;
	@Autowired
	private UserDaoImpl userDao;
	@Autowired
	private TemplateDaoImpl templateDao;
	@Autowired
	private UserRest userRest;

	/**
	 * 新增物料批次
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/createMaterielBatch")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("suppliesconf:create")
	@SystemServiceLog("新增物料批次")
	public String createMaterielBatch(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		String operatorid = ReVerifyFilter.getUserid(request, response).toString();
		String username=(String) getUsername(request, response);
		String suppliesconfname = request.getParameter("suppliesconfname"); // 批次名称
		String distributor = request.getParameter("distributor"); // 分发者
		try {
			if (CodeUtil.checkParam(suppliesconfname) || CodeUtil.checkParam(distributor)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("suppliesconfname", suppliesconfname);
			paramsMap.put("distributor", distributor);
			paramsMap.put("createtime", new Date());
			paramsMap.put("operatorid", operatorid);
			Map<String, Object> resultMap = materielDao.insertMaterielBatch(paramsMap);
			resultMap.put("createtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) resultMap.get("createtime")));
			JSONObject data = new JSONObject();
			data.putAll(resultMap);
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("新增物料批次", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("新增物料批次成功", etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"新增物料批次");
		return this.response(resultJson, request);
	}

	/**
	 * 更新物料批次信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/updateMaterielBatch")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("suppliesconf:update")
	@SystemServiceLog(" 更新物料批次信息")
	public String updateMaterielBatch(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		String operatorid = ReVerifyFilter.getUserid(request, response).toString();
		String username = (String) getUsername(request, response);;
		String suppliesconfname = request.getParameter("suppliesconfname"); // 批次名称
		String distributor = request.getParameter("distributor"); // 分发者
		String suppliesconfid = request.getParameter("suppliesconfid");
		try {
			if (CodeUtil.checkParam(suppliesconfname) || CodeUtil.checkParam(distributor)
					|| CodeUtil.checkParam(suppliesconfid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("suppliesconfid", suppliesconfid);
			paramsMap.put("suppliesconfname", suppliesconfname);
			paramsMap.put("distributor", distributor);
			paramsMap.put("modifytime", new Date());
			paramsMap.put("operatorid", operatorid);
			materielDao.updateMaterielBatch(paramsMap);
			List<Map<String, Object>> resultList = materielDao.queryMaterielBatchList(paramsMap);
			Map<String, Object> resultMap = resultList.get(0);
			resultMap.put("createtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) resultMap.get("createtime")));
			if (resultMap.get("modifytime") != null && !resultMap.get("modifytime").equals("")) {
				resultMap.put("modifytime",
						DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) resultMap.get("modifytime")));
			} else {
				resultMap.put("modifytime", "无");
			}
			JSONObject data = new JSONObject();
			data.putAll(resultMap);
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("更新物料批次", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更新物料批次成功", etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"更新物料批次信息");
		return this.response(resultJson, request);
	}

	/**
	 * 删除物料批次信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/deleteMaterielBatch")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("suppliesconf:delete")
	@SystemServiceLog("删除物料批次信息")
	public String deleteMaterielBatch(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		// String operatorid =(String) request.getAttribute("userid");
		String username=(String) getUsername(request, response);
		String suppliesconfid = request.getParameter("suppliesconfid");
		try {
			if (CodeUtil.checkParam(suppliesconfid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("suppliesconfid", suppliesconfid);
			// 先删除t_supplies(物料分发通知表)下该批次的所有通知
			materielDao.deleteMateriel(paramsMap);
			// 删除t_supplies_conf(物料批次表)下该批次
			materielDao.deleteMaterielBatch(paramsMap);
			JSONObject data = new JSONObject();
			data.putAll(paramsMap);
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("删除物料批次", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除物料批次成功", etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"删除物料批次");
		return this.response(resultJson, request);
	}

	/**
	 * 查询物料批次列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@GET
	@POST
	@Path("/getMaterielBatchList")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("suppliesconf:view")
	public String getMaterielBatchList(@Context HttpServletRequest request, @Context HttpServletResponse response) {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		String page = request.getParameter("page");
		String pagesize = request.getParameter("pagesize");
		String suppliesconfname = request.getParameter("suppliesconfname");
		String suppliesconfid = request.getParameter("suppliesconfid");

		try {
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			// 默认为第一页，每页20
			if (CodeUtil.checkParam(page, pagesize)) {
				paramsMap.put("start", 0);
				paramsMap.put("pagesize", 10);
			} else {
				paramsMap.put("start", Integer.parseInt(pagesize) * (Integer.parseInt(page) - 1));
				paramsMap.put("pagesize", Integer.parseInt(pagesize));
			}

			if (!CodeUtil.checkParam(suppliesconfname))
				paramsMap.put("suppliesconfname", "%" + suppliesconfname + "%");
			if (!CodeUtil.checkParam(suppliesconfid))
				paramsMap.put("suppliesconfid", suppliesconfid);

			int total = materielDao.queryMaterielBatchCount(paramsMap);
			if (total > 0) {
				List<Map<String, Object>> materielBatchList = materielDao.queryMaterielBatchList(paramsMap);
				for (Map<String, Object> map : materielBatchList) {
					map.put("createtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("createtime")));
					if (map.get("modifytime") != null && !map.get("modifytime").equals("")) {
						map.put("modifytime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("modifytime")));
					} else {
						map.put("modifytime", "无");
					}
				}
				// 获取第一物料批次的物料分发通知列表
				paramsMap.clear();
				List<Map<String, Object>> materielList = null;
				Object suppliesconfidFirst = null;
				
				int firsttotal=0;
				if (materielBatchList != null && materielBatchList.size() > 0) {
					suppliesconfidFirst = materielBatchList.get(0).get("suppliesconfid");
					paramsMap.put("suppliesconfid", suppliesconfidFirst);
					paramsMap.put("start", 0);
					paramsMap.put("pagesize", 10);
					firsttotal=materielDao.queryMaterielListCount(paramsMap);
					if(firsttotal>0){
						materielList = materielDao.queryMaterielList(paramsMap);
						if (materielList != null && materielList.size() > 0) {
							for (Map<String, Object> map : materielList) {
								map.put("createtime",
										DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("createtime")));
								if (map.get("modifytime") != null && !map.get("modifytime").equals("")) {
									map.put("modifytime",
											DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("modifytime")));
								} else {
									map.put("modifytime", "无");
								}
								if (map.get("sendtime") != null && !map.get("sendtime").equals("")) {
									map.put("sendtime",
											DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("sendtime")));
								} else {
									map.put("sendtime", "无");
								}
							}
						}
					}
					
				}
				JSONObject first = new JSONObject();
				first.put("suppliesconfid", suppliesconfidFirst);
				first.put("list", materielList);
				first.put("total",firsttotal);
				resultJson.put("first", first);
				resultJson.put("data", materielBatchList);
			}
			resultJson.put("total", total);
			resultJson.put("current", page);
		} catch (Exception e) {
			MyMongo.mErrorLog("查询物料批次列表", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询物料批次列表", etime - stime, request, resultJson);

		return this.response(resultJson, request);
	}

	/**
	 * 新增物料分发通知
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/createMateriel")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("supplies:create")
	@SystemServiceLog("新增物料分发通知")
	public String createMateriel(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		String operatorid = ReVerifyFilter.getUserid(request, response).toString();
		String username=(String) getUsername(request, response);
		String suppliesconfid = request.getParameter("suppliesconfid"); // 批次id
		String theaterid = request.getParameter("theaterid"); // 影院id
		String content = request.getParameter("content"); // 内容
		String status = request.getParameter("status"); // '状态
														// 0-未发送，1-已发送，2-已收到',
		String waybill = request.getParameter("waybill"); // 快递单号
		String logistics = request.getParameter("logistics");
		try {
			if (CodeUtil.checkParam(suppliesconfid) || CodeUtil.checkParam(theaterid) || CodeUtil.checkParam(waybill)
					|| CodeUtil.checkParam(status)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("suppliesconfid", suppliesconfid);
			paramsMap.put("theaterid", theaterid);
			paramsMap.put("content", content);
			paramsMap.put("status", status);
			paramsMap.put("waybill", waybill);
			paramsMap.put("logistics", logistics);
			paramsMap.put("createtime", new Date());
			paramsMap.put("operatorid", operatorid);
			Map<String, Object> resultMap = materielDao.insertMateriel(paramsMap);
			JSONObject data = new JSONObject();
			data.put("suppliesid", resultMap.get("suppliesid"));
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("新增物料分发通知", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("新增物料分发通知成功", etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"新增物料分发通成功");
		return this.response(resultJson, request);
	}

	/**
	 * 更新回盘通知信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/updateMateriel")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("supplies:update")
	@SystemServiceLog("更新物料通知信息")
	public String updateMateriel(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		String operatorid = ReVerifyFilter.getUserid(request, response).toString();
		String username=(String) getUsername(request, response);
		String suppliesid = request.getParameter("suppliesid");
		String suppliesconfid = request.getParameter("suppliesconfid"); // 批次id
		//String theaterid = request.getParameter("theaterid"); // 影院id
		//String content = request.getParameter("content"); // 内容
		String status = request.getParameter("status"); // 状态 0-未发送，1-已发送，2-已收到
		//String waybill = request.getParameter("waybill"); // 快递单号
		try {
			if (CodeUtil.checkParam(suppliesid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("suppliesid", suppliesid);
			if(!CodeUtil.checkParam(suppliesconfid)){
				paramsMap.put("suppliesconfid", suppliesconfid);
			}
			
			if(!CodeUtil.checkParam(status)){
				paramsMap.put("status",status);
				}
			paramsMap.put("modifytime", new Date());
			paramsMap.put("operatorid", operatorid);
			Map<String,Object> resultmap=materielDao.updateMateriel(paramsMap);
			JSONObject data = new JSONObject();
			data.putAll(resultmap);
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("更新物料分发通知", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更新物料分发通知成功", etime - stime, request, resultJson);
		resultJson.put("returnValue",username+"更新物料分发通知成功");
		return this.response(resultJson, request);
	}

	/**
	 * 删除物料分发通知信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@POST
	@Path("/deleteMateriel")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("supplies:delete")
	@SystemServiceLog("删除物料分发通知信息")
	public String deleteMateriel(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		// String operatorid =(String) request.getAttribute("userid");
		String username=(String) getUsername(request, response);
		String suppliesid = request.getParameter("suppliesid");
		try {
			if (CodeUtil.checkParam(suppliesid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("suppliesid", suppliesid);
			// 先删除t_ffers(回盘通知表)下该批次的所有通知
			materielDao.deleteMateriel(paramsMap);
			JSONObject data = new JSONObject();
			data.putAll(paramsMap);
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("删除物料分发通知", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除物料分发通知成功", etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"删除物料分发通知成功");
		return this.response(resultJson, request);
	}

	/**
	 * 查询物料分发通知列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@POST
	@GET
	@Path("/getMaterielList")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("supplies:view")
	public String getMaterielList(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		Integer userid=(Integer)ReVerifyFilter.getUserid(request, response);
		Map<String,Object>userInfo=userDao.findRoleAndTheaterById(userid);
		String roleType=userInfo.get("roletype").toString();

		// 获取请求参数
		String page = request.getParameter("page");
		String pagesize = request.getParameter("pagesize");
		String suppliesconfid = request.getParameter("suppliesconfid");
		String theatername = request.getParameter("theatername"); // 影院名称
		String theaternum = request.getParameter("theaternum"); // 影院编码
		String suppliesconfname = request.getParameter("suppliesconfname"); // 物料名称
		String suppliesid=request.getParameter("suppliesid");
		String waybill=request.getParameter("waybill");
		String s_time = request.getParameter("s_time");
		String e_time = request.getParameter("e_time");
		String status=request.getParameter("status");


		try {

			Map<String, Object> paramsMap = new HashMap<String, Object>();
			// 默认为第一页，每页10
			if (CodeUtil.checkParam(page, pagesize)) {
				paramsMap.put("start", 0);
				paramsMap.put("pagesize", 10);
			} else {
				paramsMap.put("start", Integer.parseInt(pagesize) * (Integer.parseInt(page) - 1));
				paramsMap.put("pagesize", Integer.parseInt(pagesize));
			}
			paramsMap.put("suppliesconfid", suppliesconfid);
			if (!CodeUtil.checkParam(theatername))
				paramsMap.put("theatername", "%" + theatername + "%");
			if (!CodeUtil.checkParam(theaternum))
				paramsMap.put("theaternum", "%" + theaternum + "%");
			if (!CodeUtil.checkParam(suppliesconfname))
				paramsMap.put("suppliesconfname", "%" + suppliesconfname + "%");
			if (!CodeUtil.checkParam(waybill)) {
				paramsMap.put("waybill", "%" + waybill + "%");
			}
			if (!CodeUtil.checkParam(suppliesid)) {
				paramsMap.put("suppliesid", suppliesid);
			}
			if (!CodeUtil.checkParam(s_time)) {
				paramsMap.put("s_time", s_time + " 00:00:00");
			}
			if (!CodeUtil.checkParam(e_time)) {
				paramsMap.put("e_time", (e_time + " 23:59:59"));
			}
			if (!CodeUtil.checkParam(status)) {
				paramsMap.put("status",status);
			}
			
			//如果是影院用户，则只返回该影院的通知
			if("2".equals(roleType)){
				paramsMap.put("theaterid", userInfo.get("theaterid"));
			}
			int total = materielDao.queryMaterielListCount(paramsMap);
			if (total > 0) {
				List<Map<String, Object>> materielList = materielDao.queryMaterielList(paramsMap);
				for (Map<String, Object> map : materielList) {
					if (map.get("createtime") != null && !map.get("createtime").equals("")) {
						map.put("createtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("createtime")));
					} else {
						map.put("createtime", "无");
					}
					if (map.get("modifytime") != null && !map.get("modifytime").equals("")) {
						map.put("modifytime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("modifytime")));
					} else {
						map.put("modifytime", "无");
					}
					if (map.get("sendtime") != null && !map.get("sendtime").equals("")) {
						map.put("sendtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("sendtime")));
					} else {
						map.put("sendtime", "无");
					}
				}
				resultJson.put("data", materielList);
			}
			resultJson.put("total", total);
			resultJson.put("current", page);
		} catch (Exception e) {
			MyMongo.mErrorLog("查询物料批次列表", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询物料批次列表", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

	@GET
	@POST
	@Path("/sendMaterielMsg")
	@Produces("text/html;charset=UTF-8")
	@SystemServiceLog("发送微信物料通知消息")
	public String sendMaterielMsg(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		String access_token = userRedis.getWXToken();
		// 获取请求参数
		String suppliesids = request.getParameter("suppliesids");
		String username=(String) getUsername(request, response);
		
		if(CodeUtil.checkParam(suppliesids)){
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			
		}
		
		//Arrays.asList的返回值是只读，不能进行增删改(add/remove/set)
		List<String> idsarr = Arrays.asList(suppliesids.split(","));
		List<String> ids = new ArrayList<String>(idsarr);

		//获取所有物料分发通知信息
		List<Map<String, Object>> resultlist = materielDao.queryMaterielListByIds(ids);
		if(resultlist!=null && resultlist.size()>0){
			//获取企业号号关注成员
			Map<Object, Object> usermap = wxTools.getUser(access_token, 1, 1, 1);
			Map<String,Object> paramsMap=new HashMap<String,Object>();
			List<Map<String,Object>> parmasList=new LinkedList<Map<String,Object>>();
			//未关注成员列表
			List<String> useridlist = new ArrayList<String>();
			String title=(String)resultlist.get(0).get("suppliesconfname");
			for (Map<String, Object> map : resultlist) {
				String description = (String) map.get("content");
				//String waybill=(String) map.get("waybill");
				String suppliesid = map.get("suppliesid").toString();
				// 判断该成员是否关注企业号，未关注则不发送
				try {
					String userid=map.get("userid").toString();
					boolean boo = usermap.containsKey(userid);
					if (!boo) {
						useridlist.add(userid);
						ids.remove(suppliesid);
						continue;
					}
					wxTools.sendMsg(userid, access_token, title,description, "http://vip.hn.yidepiao.net:7070/cinemacloud/rest/materiel/getuserinfo",suppliesids);
				} catch (Exception e) {
					throw new CustomException("影院关联手机号未注册");
				}
				
				paramsMap.put("suppliesid", map.get("suppliesid"));
				paramsMap.put("modifytime", new Date());
				parmasList.add(paramsMap);
				materielDao.updateMaterielByIds(parmasList);
			}
			if (ids != null && ids.size() > 0){
				List<Map<String, Object>> data=materielDao.queryMaterielListByIds(ids);
				resultJson.put("data", data);
			}
			resultJson.put("userid", useridlist);
		}
		resultJson.put("returnValue", username+"发送微信物料分发通知消息成功");
		return this.response(resultJson, request);
	}

	@GET
	@POST
	@Path("/getuserinfo")
	@Produces("text/html;charset=UTF-8")
	public void getuserinfo(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, ServletException {
		String code = request.getParameter("code");
		String suppliesid=request.getParameter("state");
		String access_token = userRedis.getWXToken();
		try {
			String userid = wxTools.getUserIdByCode(access_token, code);
			if(userid==null && "".equals(userid)){
				throw new CustomException("没有找到当前用户");
			}
			userRest.loginByUserid(userid);
			response.sendRedirect("/cinemacloud/weAllot?suppliesid="+suppliesid);

		} catch (IOException e) {
			MyMongo.mErrorLog("微信跳转页面失败", request, e);
			throw new CustomException("操作失败，请联系管管理员");
			
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws CustomException 
	 */
	@POST
	@Path("/uploadMateriel")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/html;charset=UTF-8")
	@SystemServiceLog("上传物料分发通知")
	@RequiresPermissions("supplies:upload")
	public String uploadMateriel(@Context HttpServletRequest request,@Context HttpServletResponse response) throws  IOException, CustomException {
		
		JSONObject resultJson = new JSONObject();
		String access_token = userRedis.getWXToken();

		String suppliesconfid = request.getParameter("suppliesconfid");
		String operatorid = ReVerifyFilter.getUserid(request, response).toString();
		String username= ReVerifyFilter.getUsername(request,response);
		String templateid=request.getParameter("templateid");

		if (CodeUtil.checkParam(suppliesconfid,templateid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
			File newFile = FileUtil.uploadFile(request,suppliesconfid);
			List<List<Object>> readlist = ReadExcelUtil.readExcel(newFile);
			newFile.delete();
			if (readlist!= null && readlist.isEmpty()) {
				return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
			}

			Map<String,Object> resultmap=templateDao.getTemplateOne(Integer.valueOf(templateid));
			if(resultmap==null || resultmap.isEmpty()){
				throw new CustomException("没有获取到模板");
			}

			int start_row=(int)resultmap.get("start_row");
			int theaternum_row=(int)resultmap.get("theaternum");
			int theatername_row=(int)resultmap.get("theatername");
			int customer_phone_row=(int)resultmap.get("customer_phone");
			int customer_row=(int)resultmap.get("customer");
			int post_row=(int)resultmap.get("post");
			int card_row=(int)resultmap.get("card");
			int exhibition_row=(int)resultmap.get("exhibition");
			int diskcardcount_row=(int)resultmap.get("diskcardcount");
			int recipient_address_row=(int)resultmap.get("recipient_address");

			int count = 0;
			Date createtime = new Date();
			List<String> theaterList=new ArrayList<>();
			List<SuppliesBean> suppliesList = new ArrayList<>();
			for (List<Object> list : readlist) {
				//从数据起始行读取
				if (count++ < start_row){
					continue;
				}
				String theatername=(String)list.get(theatername_row);
				if("".equals(theatername)){
					continue;
				}
				//物料根据影院名称查找影院id，发盘根据影院编码查找影院id
				String theaterid=null;
				if(theaternum_row!=-1){
					String theaternum = (String) list.get(theaternum_row);
					if(theaternum==null || "".equals(theaternum)){
						continue;
					}
					Map<String, Object> resultMap = theaterDao.findTheaterByNum(theaternum);
					if (resultMap == null) {
						theaterList.add(theatername);
						continue;
					}
					theaterid=resultMap.get("theaterid").toString();
				}else{
					Map<String, Object> resultMap = theaterDao.findTheaterByName(theatername);
					if (resultMap == null) {
						theaterList.add(theatername);
						continue;
					}
					theaterid=resultMap.get("theaterid").toString();
				}
				SuppliesBean bean = new SuppliesBean();
				bean.setTheaterid(theaterid);
				StringBuffer content=new StringBuffer();
				if(post_row!=-1){
					String poster="".equals(list.get(post_row)) || list.get(post_row)==null?"0":list.get(post_row).toString();
					content.append("海报:");
					content.append(poster);
					content.append(",");
				}
				if(card_row!=-1){
					String card="".equals(list.get(card_row)) || list.get(card_row)==null?"0":list.get(card_row).toString();
					content.append("立牌:");
					content.append(card);
					content.append(",");
				}
				if(exhibition_row!=-1){
					String exhibition="".equals(list.get(exhibition_row)) || list.get(exhibition_row)==null?"0":list.get(exhibition_row).toString();
					content.append("展架:");
					content.append(exhibition);
				}
				if(customer_phone_row!=-1){
					bean.setCustomer_phone((String)list.get(customer_phone_row));
				}
				if(customer_row!=-1){
					bean.setCustomer((String)list.get(customer_row));
				}
				if(diskcardcount_row!=-1){
					String diskcardcount="".equals(list.get(diskcardcount_row)) || list.get(diskcardcount_row)==null?"0":list.get(diskcardcount_row).toString();
					content.append("硬盘数量：");
					content.append(diskcardcount);
				}
				if(recipient_address_row!=-1){
					bean.setRecipient_address((String)list.get(recipient_address_row));
				}
				bean.setContent(content.toString());
				bean.setSuppliesconfid(suppliesconfid);
				bean.setCreatetime(createtime);
				bean.setOperatorid(operatorid);
				//上传之后默认为已发送
				bean.setStatus("1");
				suppliesList.add(bean);
			}
			if(suppliesList==null || suppliesList.isEmpty()){
				throw new CustomException("上传失败");
			}
			try {
			List<Integer> idlist = new ArrayList<>();
			List<SuppliesBean> resultlist = materielDao.insertBatch(suppliesList);
			if (resultlist.size() > 0) {
				for (SuppliesBean bean : resultlist) {
					idlist.add(bean.getSuppliesid());
				}
			}
				//给影院管理员和影院运营发送微信消息
				if(idlist!=null && !idlist.isEmpty()){
					List<Map<String,Object>> materiellist=materielDao.getUserid(idlist);
					if(materiellist!=null && !materiellist.isEmpty()){
						//获取企业号号关注成员
						Map<Object, Object> usermap = wxTools.getUser(access_token, 1, 1, 1);
						/*Map<String,Object> paramsMap=new HashMap<String,Object>();
						List<Map<String,Object>> paramsList=new ArrayList<Map<String,Object>>();*/
						//未关注成员列表
						List<String> useridlist = new ArrayList<String>();
						//未关联影院列表
						List<String> theaterlist=new ArrayList<>();
						String title=(String)materiellist.get(0).get("suppliesconfname");
						for (Map<String, Object> map : materiellist) {
							String description = (String) map.get("content");
							String suppliesid = map.get("suppliesid").toString();
							// 判断该成员是否关注企业号，未关注则不发送
							Object userid=map.get("userid");
							if(userid==null){
								String theatername=(String)map.get("theatername");
								theaterlist.add(theatername);
								continue;
							}
							boolean boo = usermap.containsKey(userid.toString());
							if (!boo) {
								useridlist.add(userid.toString());
								idlist.remove(suppliesid);
								continue;
							}
							wxTools.sendMsg(userid.toString(), access_token, title,description, "http://vip.hn.yidepiao.net:7070/cinemacloud/rest/materiel/getuserinfo",suppliesid);
						/*	paramsMap.put("ffersid", ffersid);
							paramsMap.put("modifytime", new Date());
							paramsList.add(paramsMap);*/
						}
					}

				}
			JSONObject data = new JSONObject();
			data.put("ids", idlist);
			data.put("num", resultlist.size());
			data.put("theatername",theaterList);
			resultJson.put("data", data);

		} catch (Exception e) {
			throw new CustomException("上传文件失败");
		}
		resultJson.put("returnValue", username+"上传回盘通知");
		return this.response(resultJson,request);

	}

	@GET
	@POST
	@Path("/cancelUpload")
	@Produces("text/html;charset=UTF-8")
	@SystemServiceLog("取消上传物料分发通知")
	public String cancelUpload(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		String username=(String) getUsername(request, response);
		try {
			String ids = request.getParameter("ids");
			if (CodeUtil.checkParam(ids)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			List<String> idlist = Arrays.asList(ids.split(","));
			materielDao.batchDeleteMateriel(idlist);
		} catch (Exception e) {
			MyMongo.mErrorLog("批量删除物料分发通知错误", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
			
		}
		resultJson.put("returnValue", username+"取消上传物料分发通知成功");
		return this.response(resultJson, request);
	}
	
	/**
	 * 更新回盘通知信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/updateMaterielFromWX")
	@Produces("text/html;charset=UTF-8")
	public String updateMaterielFromWX(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		String suppliesid = request.getParameter("suppliesid");
		String status = request.getParameter("status"); // 状态 0-未发送，1-已发送，2-已收到
		try {
			if (CodeUtil.checkParam(suppliesid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("suppliesid", suppliesid);
			if(!CodeUtil.checkParam(status)){
				paramsMap.put("status",status);
				};
			paramsMap.put("modifytime", new Date());
			Map<String,Object> resultmap=materielDao.updateMateriel(paramsMap);
			JSONObject data = new JSONObject();
			data.putAll(resultmap);
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("更新物料分发通知", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更新物料分发通知成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	/**
	 * 查询物料分发通知列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/getMaterielListFromWX")
	@Produces("text/html;charset=UTF-8")
	public String getMaterielListFromWX(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		// 获取请求参数
		String suppliesid=request.getParameter("suppliesid");
		if (CodeUtil.checkParam(suppliesid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}

		try {

			Map<String, Object> paramsMap = new HashMap<String, Object>();
			// 默认为第一页，每页10
			if (!CodeUtil.checkParam(suppliesid)) {
				paramsMap.put("suppliesid", suppliesid);
			}
				List<Map<String, Object>> materielList = materielDao.queryMaterielList(paramsMap);
				resultJson.put("data", materielList);
		} catch (Exception e) {
			MyMongo.mErrorLog("查询物料批次列表", request, e);
			return this.returnError(resultJson, ResMessage.Server_Abnormal.code, request);
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询物料批次列表", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

	@GET
	@POST
	@Path("/index")
	@Produces("text/html;charset=UTF-8")
	public void index(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, ServletException, IOException {
		userRest.loginByUserid("166");

		response.sendRedirect("/cinemacloud/rest/materiel/getMaterielBatchList");


	}

	

}
