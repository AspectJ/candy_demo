package com.rest.bill;

import com.cp.bean.BillBean;
import com.cp.bean.ResMessage;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.cp.util.DateFormatUtil;
import com.cp.util.FileUtil;
import com.mongo.MyMongo;
import com.redis.UserRedisImpl;
import com.rest.bill.dao.BillDaoImpl;
import com.rest.log.annotation.SystemServiceLog;
import com.rest.theater.dao.TheaterDaoImpl;
import com.rest.user.dao.UserDaoImpl;
import com.wx.WXTools;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Path("/rest/bill")
@Service
public class BillRest extends BaseServlet {

	@Autowired
	private BillDaoImpl billDao;
	@Autowired
	private UserRedisImpl userRedis;
	@Autowired
	private WXTools wxtools;
	@Autowired
	private UserDaoImpl userDao;

	/**
	 * 新增账单批次
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@Path("/createBillconf")
	@POST
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("billconf:create")
	@SystemServiceLog("新增账单批次")
	@Transactional
	public String createBillconf(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		Integer operatorid = (Integer)ReVerifyFilter.getUserid(request, response);
		String username=(String)ReVerifyFilter.getUsername(request, response);
		String billconfname = request.getParameter("billconfname");
		String theaterids=request.getParameter("theaterids");
		String roleids=request.getParameter("roleids");

		if (CodeUtil.checkParam(billconfname,theaterids)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		String[] theateridArr=theaterids.split(",");
		List<String> idlist=Arrays.asList(theateridArr);
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("billconfname", billconfname);
		paramsMap.put("createtime", new Date());
		paramsMap.put("operatorid", operatorid);
		try {
			Map<String, Object> resultMap = billDao.saveBillconf(paramsMap);
			// 插入失败
			if (resultMap == null) {
				MyMongo.mRequestFail(request, ResMessage.Add_Info_Fail.code);
				return this.returnError(resultJson, ResMessage.Add_Info_Fail.code, request);
			}
			Date createtime=new Date();
			List<BillBean> billList=new ArrayList<BillBean>();
			for(String id :idlist){
				BillBean bean=new BillBean();
				bean.setBillconfid(Integer.valueOf(resultMap.get("billconfid").toString()));
				bean.setTheaterid(Integer.valueOf(id));
				bean.setCreatetime(createtime);
				bean.setOperatorid(operatorid);
				bean.setStatus(0);
				billList.add(bean);
			}
			billDao.saveBillList(billList);
		Map<String,Object> map=new HashMap<>();
		map.put("theaterids", theaterids.split(","));
		map.put("roleids", roleids.split(","));
		List<Map<String,Object>> useridList=userDao.getUserId(map);
		StringBuffer userids=new StringBuffer();
		for(Map<String,Object> usermap:useridList){
			userids.append(usermap.get("userid"));
			userids.append("|");
		}
		String access_token=userRedis.getWXToken();
		wxtools.sendMsg(userids.toString(), access_token, billconfname, "请在PC端上传上传账单文件", "", "");
		} catch (Exception e) {
			MyMongo.mErrorLog("新增账单批次", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("新增账单批次成功", etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"新增账单批次成功");
		return this.response(resultJson, request);
	}

	/**
	 * 更新账单批次
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@Path("/updateBillconf")
	@POST
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("billconf:update")
	@SystemServiceLog("更新账单批次")
	public String updateBillconf(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		String operatorid = ReVerifyFilter.getUserid(request, response).toString();
		String username=(String)ReVerifyFilter.getUsername(request, response);
		String billconfid = request.getParameter("billconfid");
		String billconfname = request.getParameter("billconfname");

		try {
			if (CodeUtil.checkParam(billconfid) || CodeUtil.checkParam(billconfname)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("billconfid", billconfid);
			paramsMap.put("billconfname", billconfname);
			paramsMap.put("modifytime", new Date());
			paramsMap.put("operatorid", operatorid);
			Map<String, Object> map = billDao.updateBillconf(paramsMap);
			// 更新失败
			if (map == null) {
				MyMongo.mRequestFail(request, ResMessage.Update_Info_Fail.code);
				return this.returnError(resultJson, ResMessage.Update_Info_Fail.code, request);
			}
			List<Map<String, Object>> resultList = billDao.getBillconfList(paramsMap);
			if (resultList != null && resultList.size() > 0) {
				Map<String, Object> resultMap = resultList.get(0);
				if (resultMap.get("createtime") != null && !resultMap.get("createtime").equals("")) {
					resultMap.put("createtime",
							DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) resultMap.get("createtime")));
				} else {
					resultMap.put("createtime", "无");
				}
				if (resultMap.get("modifytime") != null && !resultMap.get("modifytime").equals("")) {
					resultMap.put("modifytime",
							DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) resultMap.get("modifytime")));
				} else {
					resultMap.put("modifytime", "无");
				}
				JSONObject data = new JSONObject();
				data.putAll(resultMap);
				resultJson.put("data", data);
			}

		} catch (Exception e) {
			MyMongo.mErrorLog("更新账单批次", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更新账单批次成功", etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"更新账单批次成功");
		return this.response(resultJson, request);
	}

	/**
	 * 删除账单批次
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@Path("/deleteBillconf")
	@GET
	@POST
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("billconf:delete")
	@SystemServiceLog("删除账单批次")
	public String removeBillconf(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		//String operatorid = (String)ReVerifyFilter.getUserid(request);
		String username=(String)ReVerifyFilter.getUsername(request, response);
		String billconfid = request.getParameter("billconfid");

		try {
			if (CodeUtil.checkParam(billconfid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("billconfid", billconfid);
			Integer num = billDao.removeBillconf(paramsMap);
			// 删除失败
			if (num == null) {
				MyMongo.mRequestFail(request, ResMessage.Delete_Info_Fail.code);
				return this.returnError(resultJson, ResMessage.Delete_Info_Fail.code, request);
			}
			JSONObject data = new JSONObject();
			data.putAll(paramsMap);
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("删除账单批次", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除账单批次成功", etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"删除账单批次");
		return this.response(resultJson, request);
	}

	/**
	 * 查询账单批次
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@Path("/getBillconfList")
	@GET
	@POST
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("billconf:view")
	public String getBillconfList(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		//String operatorid = (String)ReVerifyFilter.getUserid(request);
		String billconfid = request.getParameter("billconfid");
		String billconfname = request.getParameter("billconfname");
		String page = request.getParameter("page");
		String pagesize = request.getParameter("pagesize");

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
			if (!CodeUtil.checkParam(billconfname))
				paramsMap.put("billconfname", "%" + billconfname + "%");
			if (!CodeUtil.checkParam(billconfid))
				paramsMap.put("billconfid", billconfid);
			int total = billDao.getBillconfTotal(paramsMap);
			if (total > 0) {
				List<Map<String, Object>> resultList = billDao.getBillconfList(paramsMap);
				for (Map<String, Object> map : resultList) {
					map.put("createtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("createtime")));
					if (map.get("modifytime") != null && !map.get("modifytime").equals("")) {
						map.put("modifytime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("modifytime")));
					} else {
						map.put("modifytime", "无");
					}
				}
				paramsMap.clear();
				List<Map<String, Object>> billList = null;
				Object billconfidFirst = null;
				int firsttotal=0;
				if (resultList != null && resultList.size() > 0) {
					billconfidFirst = resultList.get(0).get("billconfid");
					
					paramsMap.put("billconfid", billconfidFirst);
					paramsMap.put("start", 0);
					paramsMap.put("pagesize", 10);
					firsttotal=billDao.getBillTotal(paramsMap);
					if(firsttotal>0){
						billList = billDao.getBillList(paramsMap);
						if (billList != null && billList.size() > 0) {
							for (Map<String, Object> map : billList) {
								if (map.get("createtime") != null && !map.get("createtime").equals("")) {
									map.put("createtime",
											DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("createtime")));
								} else {
									map.put("createtime", "无");
								}
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
				first.put("billconfid", billconfidFirst);
				first.put("list", billList);
				first.put("total", firsttotal);
				resultJson.put("first", first);
				resultJson.put("data", resultList);
			}
			resultJson.put("total", total);
			resultJson.put("current", page);
		} catch (Exception e) {
			MyMongo.mErrorLog("查询账单批次", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询账单批次成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

	/**
	 * 新增账单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@Path("/createBill")
	@POST
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("bill:create")
	@SystemServiceLog("新增账单")
	public String createBill(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		String operatorid = ReVerifyFilter.getUserid(request, response).toString();
		String billconfid = request.getParameter("billconfid");
		String theaterid = request.getParameter("theaterid");
		String amount = request.getParameter("amount");
		String status = request.getParameter("status");
		String sendtime = request.getParameter("sendtime");

		try {
			if (CodeUtil.checkParam(billconfid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("billconfid", billconfid);
			paramsMap.put("amount", amount);
			paramsMap.put("status", status);
			if (!CodeUtil.checkParam(sendtime))
				paramsMap.put("sendtime", sendtime);
			paramsMap.put("theaterid", theaterid);
			paramsMap.put("createtime", new Date());
			paramsMap.put("operatorid", operatorid);
			Map<String, Object> resultMap = billDao.saveBill(paramsMap);
			// 插入失败
			if (resultMap == null) {
				MyMongo.mRequestFail(request, ResMessage.Add_Info_Fail.code);
				return this.returnError(resultJson, ResMessage.Add_Info_Fail.code, request);
			}
			if (resultMap.get("createtime") != null && !resultMap.get("createtime").equals("")) {
				resultMap.put("createtime",
						DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) resultMap.get("createtime")));
			} else {
				resultMap.put("createtime", "无");
			}
			if (resultMap.get("modifytime") != null && !resultMap.get("modifytime").equals("")) {
				resultMap.put("modifytime",
						DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) resultMap.get("modifytime")));
			} else {
				resultMap.put("modifytime", "无");
			}
			if (resultMap.get("sendtime") != null && !resultMap.get("sendtime").equals("")) {
				resultMap.put("sendtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) resultMap.get("sendtime")));
			} else {
				resultMap.put("sendtime", "无");
			}
			JSONObject data = new JSONObject();
			data.putAll(resultMap);
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("新增账单", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("新增账单成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

	/**
	 * 更新账单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/updateBill")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("bill:update")
	public String updateBill(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		String operatorid = ReVerifyFilter.getUserid(request, response).toString();
		String billid = request.getParameter("billid");
		String billconfid = request.getParameter("billconfid");
		String status = request.getParameter("status");

		try {
			if (CodeUtil.checkParam(billid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("billid", billid);
			if(!CodeUtil.checkParam(billconfid)){
				paramsMap.put("billconfid", billconfid);	
			}
			if(!CodeUtil.checkParam(status)){
				paramsMap.put("status", status);
			}
			paramsMap.put("modifytime", new Date());
			Map<String, Object> map = billDao.updateBill(paramsMap);
			// 更新失败
			if (map == null) {
				MyMongo.mRequestFail(request, ResMessage.Update_Info_Fail.code);
				return this.returnError(resultJson, ResMessage.Update_Info_Fail.code, request);
			}
		} catch (Exception e) {
			MyMongo.mErrorLog("更新账单", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更新账单成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);

	}

	/**
	 * 删除账单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/removeBill")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("bill:delete")
	public String removeBill(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();

		// 获取请求参数
		//String operatorid =(String)ReVerifyFilter.getUserid(request);
		String billid = request.getParameter("billid");

		try {
			if (CodeUtil.checkParam(billid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("billid", billid);
			Integer num = billDao.removeBill(paramsMap);
			// 删除失败
			if (num == null) {
				MyMongo.mRequestFail(request, ResMessage.Delete_Info_Fail.code);
				return this.returnError(resultJson, ResMessage.Delete_Info_Fail.code, request);
			}
			JSONObject data = new JSONObject();
			data.putAll(paramsMap);
			resultJson.put("data", data);
		} catch (Exception e) {
			MyMongo.mErrorLog("删除账单", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除账单成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}

	/**
	 * 查询账单列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/getBillList")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("billconf:view")
	public String getBillList(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		
		Integer userid=(Integer)ReVerifyFilter.getUserid(request, response);
		Map<String,Object>userInfo=userDao.findRoleAndTheaterById(userid);
		String roleType=userInfo.get("roletype").toString();

		// 获取请求参数
		//String operatorid = ReVerifyFilter.getUserid(request).toString();
		String billconfid = request.getParameter("billconfid");
		String theaternum = request.getParameter("theaternum");
		String theatername = request.getParameter("theatername");
		String s_time = request.getParameter("s_time");
		String e_time = request.getParameter("e_time");
		String page = request.getParameter("page");
		String pagesize = request.getParameter("pagesize");
		String status=request.getParameter("status");

		try {
			/*if (CodeUtil.checkParam(billconfid)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}*/
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			// 默认为第一页，每页20
			if (CodeUtil.checkParam(page, pagesize)) {
				paramsMap.put("start", 0);
				paramsMap.put("pagesize", 20);
			} else {
				paramsMap.put("start", Integer.parseInt(pagesize) * (Integer.parseInt(page) - 1));
				paramsMap.put("pagesize", Integer.parseInt(pagesize));
			}
			if (!CodeUtil.checkParam(theatername))
				paramsMap.put("theatername", "%" + theatername + "%");
			if (!CodeUtil.checkParam(theaternum))
				paramsMap.put("theaternum", "%" + theaternum + "%");
			if (!CodeUtil.checkParam(s_time)) {
				paramsMap.put("s_time",s_time + " 00:00:00");
			}
			if (!CodeUtil.checkParam(e_time)) {
				paramsMap.put("e_time", e_time + " 23:59:59");
			}
			if("2".equals(roleType)){
				paramsMap.put("theaterid", userInfo.get("theaterid"));
			}
			if(!CodeUtil.checkParam(status)){
				paramsMap.put("status",status);
			}
			paramsMap.put("billconfid", billconfid);
			int total = billDao.getBillTotal(paramsMap);
			if (total > 0) {
				List<Map<String, Object>> resultList = billDao.getBillList(paramsMap);
				if (resultList != null && resultList.size() > 0) {
					for (Map<String, Object> map : resultList) {
						if (map.get("createtime") != null && !map.get("createtime").equals("")) {
							map.put("createtime",
									DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("createtime")));
						} else {
							map.put("createtime", "无");
						}
						if (map.get("modifytime") != null && !map.get("modifytime").equals("")) {
							map.put("modifytime",
									DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("modifytime")));
						} else {
							map.put("modifytime", "无");
						}
						if (map.get("sendtime") != null && !map.get("sendtime").equals("")) {
							map.put("sendtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date) map.get("sendtime")));
						} else {
							map.put("sendtime", "无");
						}
					}
				}
				resultJson.put("data", resultList);
			}
			resultJson.put("total", total);
			resultJson.put("current", page);
		} catch (Exception e) {
			MyMongo.mErrorLog("查询账单批次", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询账单批次成功", etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	

	@POST
	@Path("/uploadBill")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/html;charset=UTF-8")
	@SystemServiceLog("上传账单")
	@RequiresPermissions("bill:upload")
	@Transactional
	public String uploadBill(@Context HttpServletRequest request,@Context HttpServletResponse response) throws IOException, CustomException {
		
		JSONObject resultJson = new JSONObject();
		String billid=request.getParameter("billid");
		String username = (String)ReVerifyFilter.getUsername(request,response);

		if (CodeUtil.checkParam(billid)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		try {
			File file=FileUtil.uploadFile(request, billid);
			String filename="/rest/bill/downloadBill?filename="+file.getName();
			
			JSONObject data=new JSONObject();
			data.put("filename", filename);
			data.put("billid", billid);
			resultJson.put("data", data);
			
			Map<String,Object> paramsMap=new HashMap<>();
			paramsMap.put("filename",filename);
			paramsMap.put("status", 1);
			paramsMap.put("billid", billid);
			billDao.updateBill(paramsMap);
		} catch (Exception e) {
			throw new CustomException("上传文件失败");
		}
		resultJson.put("returnValue", username+"上传账单");
		return this.response(resultJson, request);
	}

	@GET
	@POST
	@Path("/downloadBill")
	@Produces("text/html;charset=UTF-8")
	//@RequiresPermissions("bill:download")
	public String downloadBill(@Context HttpServletRequest request,@Context HttpServletResponse response) throws CustomException{
		
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		
		String filename=request.getParameter("filename");
		if (CodeUtil.checkParam(filename)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}

		String path="E:/upload/"+filename;
		int index=filename.indexOf("-");
		filename=filename.substring(index+1);
		
		try {
			FileUtil.downloadFile(request, response, filename, path);
		} catch (CustomException e) {
			throw e;
		}
		
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("下载文件成功", etime - stime, request, resultJson);
		
		return null;
	}
	
	@POST
	@Path("/cancelUpload")
	@Produces("text/html;charset=UTF-8")
	@SystemServiceLog("取消上传")
	public String cancelUpload(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		String username=(String)ReVerifyFilter.getUsername(request, response);
		try {
			String ids = request.getParameter("ids");
			if (CodeUtil.checkParam(ids)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			List<String> idList = Arrays.asList(ids.split(","));
			billDao.removeBillList(idList);
		} catch (Exception e) {
			MyMongo.mErrorLog("批量删除账单错误", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		resultJson.put("returnValue", username+"取消上传");
		return this.response(resultJson, request);
	}
	
	@GET
	@POST
	@Path("/sendBillMsg")
	@Produces("text/html;charset=UTF-8")
	@SystemServiceLog("发送微信回盘消息")
	public String sendFfersMsg(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		String access_token = userRedis.getWXToken();
		// 获取请求参数
		String billids = request.getParameter("billids");
		String username=(String)ReVerifyFilter.getUsername(request, response);
		if(CodeUtil.checkParam(billids)){
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		//Arrays.asList的返回值是只读，不能进行增删改(add/remove/set)
		List<String> idsarr = Arrays.asList(billids.split(","));
		List<String> ids = new ArrayList<String>(idsarr);

		//获取所有物料分发通知信息
		List<Map<String, Object>> resultlist = billDao.queryBillListByIds(ids);
		if(resultlist!=null && resultlist.size()>0){
			//获取企业号号关注成员
			Map<Object, Object> usermap = wxtools.getUser(access_token, 1, 1, 1);
			Map<String,Object> paramsMap=new HashMap<String,Object>();
			List<Map<String,Object>> paramsList=new ArrayList<Map<String,Object>>();
			//未关注成员列表
			List<String> useridlist = new ArrayList<String>();
			//未关联影院列表
			List<String> theaterlist=new ArrayList<>();
			String title=(String)resultlist.get(0).get("billconfname");
			for (Map<String, Object> map : resultlist) {
				String billid = map.get("billid").toString();
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
					ids.remove(billid);
					continue;
				}
				wxtools.sendMsg(userid.toString(), access_token, title,"请在PC端上传上传账单文件", "http://vip.hn.yidepiao.net:7070/cinemacloud/rest/materiel/confirmReceipt",billid);
				paramsMap.put("billid", map.get("billid"));
				paramsMap.put("modifytime", new Date());
				paramsList.add(paramsMap);
			}
			if (ids != null && ids.size() > 0){
				List<Map<String, Object>> data=billDao.queryBillListByIds(ids);
				resultJson.put("data", data);
			}
			resultJson.put("theater", theaterlist);
			resultJson.put("userid", useridlist);
		}
		resultJson.put("returnValue", username+"发送回盘通知");
		return this.response(resultJson, request);
	}
	

}
