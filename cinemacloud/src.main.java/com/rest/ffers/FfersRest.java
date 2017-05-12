package com.rest.ffers;

import com.cp.bean.FfersBean;
import com.cp.bean.ResMessage;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.cp.util.DateFormatUtil;
import com.cp.util.FileUtil;
import com.cp.util.ReadExcelUtil;
import com.mongo.MyMongo;
import com.redis.UserRedisImpl;
import com.rest.ffers.dao.FfersDaoImpl;
import com.rest.log.annotation.SystemServiceLog;
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

@Path("/rest/ffers")
@Service
public class FfersRest extends BaseServlet {
	
	
	@Autowired
	private FfersDaoImpl ffersDao;
	@Autowired
	private UserRedisImpl userRedis;
	@Autowired
	private WXTools wxtools;
	@Autowired
	private TheaterDaoImpl theaterDao;
	@Autowired
	private UserDaoImpl userDao;
	@Autowired
	private TemplateDaoImpl templateDao;
	@Autowired
	private WXTools wxTools;
	@Autowired
	private UserRest userRest;

	/**
	 * 新增回盘批次
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@POST
	@Path("/createFfersconf")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("ffersconf:create")
	@SystemServiceLog("新增回盘批次")
	public String createFfersconf(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		
		//获取请求参数
		String operatorid =ReVerifyFilter.getUserid(request, response).toString();
		String username=(String)ReVerifyFilter.getUsername(request, response);
		String ffersconfname=request.getParameter("ffersconfname"); //批次名称
		try{
			if (CodeUtil.checkParam(ffersconfname)){
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String,Object> paramsMap=new HashMap<String,Object>();
			paramsMap.put("ffersconfname", ffersconfname);
			paramsMap.put("createtime", new Date());
			paramsMap.put("operatorid",operatorid);
			Map<String, Object> resultMap=ffersDao.insertFfersBatch(paramsMap);
			JSONObject data = new JSONObject();
			data.put("ffersconfid", resultMap.get("ffersconfid"));
			resultJson.put("data", data);
		}catch (Exception e) {
			MyMongo.mErrorLog("新增回盘批次", request,e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("新增回盘批次成功",  etime - stime, request, resultJson);
		
		resultJson.put("returnValue", username+"新增回盘批次成功");
		return this.response(resultJson, request);
	}
	
	/**
	 * 更新回盘批次信息
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@POST
	@Path("/updateFfersconf")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("ffersconf:update")
	@SystemServiceLog("更新回盘批次")
	public String updateFfersBatch(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		
		//获取请求参数
		String operatorid =ReVerifyFilter.getUserid(request, response).toString();
		String username=(String)ReVerifyFilter.getUsername(request, response);
		String ffersconfname=request.getParameter("ffersconfname"); //批次名称
		String ffersconfid=request.getParameter("ffersconfid");
		try{
			if (CodeUtil.checkParam(ffersconfname) ){
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String,Object> paramsMap=new HashMap<String,Object>();
			paramsMap.put("ffersconfid", ffersconfid);
			paramsMap.put("ffersconfname", ffersconfname);
			paramsMap.put("modifytime", new Date());
			paramsMap.put("operatorid",operatorid);
			ffersDao.updateFfersBatch(paramsMap);
			List<Map<String, Object>> resultList = ffersDao.queryFfersBatchList(paramsMap);
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
		}catch (Exception e) {
			MyMongo.mErrorLog("更新回盘批次", request,e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更新回盘批次成功",  etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"更新回盘批次");
		return this.response(resultJson, request);
	}
	
	/**
	 * 删除回盘批次信息
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@POST
	@Path("/deleteFfersconf")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("ffersconf:delete")
	public String deleteFfersBatch(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		
		//获取请求参数
		//String operatorid =ReVerifyFilter.getUserid(request, response).toString();
		String username=(String)ReVerifyFilter.getUsername(request, response);
		String ffersconfid=request.getParameter("ffersconfid");
		try{
			if (CodeUtil.checkParam(ffersconfid)){
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String,Object> paramsMap=new HashMap<String,Object>();
			paramsMap.put("ffersconfid", ffersconfid);
			//先删除t_ffers(回盘通知表)下该批次的所有通知
			ffersDao.deleteFfers(paramsMap);
			//删除t_ffers_conf(回盘批次表)下该批次
			ffersDao.deleteFfersBatch(paramsMap);
		}catch (Exception e) {
			MyMongo.mErrorLog("删除回盘批次", request,e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除回盘批次成功",  etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"删除回盘批次");
		return this.response(resultJson, request);
	}
	
	/**
	 * 查询回盘批次列表
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@GET
	@POST
	@Path("/getFfersconfList")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("ffersconf:view")
	public String getFfersBatchList(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		
		//获取请求参数
		String page = request.getParameter("page");
		String pagesize = request.getParameter("pagesize");
		String ffersconfname=request.getParameter("ffersconfname");
		
		try{
			Map<String,Object> paramsMap = new HashMap<String,Object>();
			if(!CodeUtil.checkParam(ffersconfname)) paramsMap.put("ffersconfname", "%"+ffersconfname+"%");
			if(!CodeUtil.checkParam(page,pagesize)){
				paramsMap.put("start",  Integer.parseInt(pagesize) * (Integer.parseInt(page) - 1));
				paramsMap.put("pagesize",  Integer.parseInt(pagesize));
				}
			
			int total=ffersDao.queryFfersBatchCount(paramsMap);
			if(total>0){
				List<Map<String,Object>> ffersconfList=ffersDao.queryFfersBatchList(paramsMap);
				for(Map<String,Object> map : ffersconfList){
					map.put("createtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date)map.get("createtime")));
					if(map.get("modifytime") != null && !map.get("modifytime").equals("")){
						map.put("modifytime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date)map.get("modifytime")));
					}else{
						map.put("modifytime", "无");
					}
				}
				//获取第一个回盘批次的回盘通知列表
				paramsMap.clear();
				List<Map<String,Object>> ffersList=null;
				Object ffersconfidFirst = null;
				int firsttotal=0;
				if(ffersconfList!=null && ffersconfList.size()>0){
					ffersconfidFirst=ffersconfList.get(0).get("ffersconfid");
					paramsMap.put("ffersconfid", ffersconfidFirst);
					paramsMap.put("start", 0);
					paramsMap.put("pagesize", 10);
					firsttotal=ffersDao.queryFfersListCount(paramsMap);
					if(firsttotal>0){
						ffersList=ffersDao.queryFfersList(paramsMap);
						if(ffersList!=null&& ffersList.size()>0){
							for (Map<String, Object> map : ffersList) {
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
				JSONObject first=new JSONObject();
				first.put("ffersconfid", ffersconfidFirst);
				first.put("list", ffersList);
				first.put("total",firsttotal);
				resultJson.put("first", first);
				resultJson.put("data", ffersconfList);
			}
			resultJson.put("total", total);
			resultJson.put("current", page);
		}catch (Exception e) {
			MyMongo.mErrorLog("查询回盘批次列表", request,e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime=System.currentTimeMillis();
		MyMongo.mRequestLog("查询回盘批次列表",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 新增回盘通知
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	/*@GET
	@POST
	@Path("/createFfers")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("ffers:create")
	@SystemServiceLog("新增回盘通知")
	public String createFfers(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		
		//获取请求参数
		String operatorid =ReVerifyFilter.getUserid(request, response).toString();
		String username=(String)ReVerifyFilter.getUsername(request, response);
		String ffersconfid=request.getParameter("ffersconfid"); //批次id
		String theaterid=request.getParameter("theaterid");			  //影院id
		String content=request.getParameter("content");			      //内容
		String status=request.getParameter("status");				  //发盘，回盘状态 0-未发盘，1-已发盘，待验收，2-已验收，待回盘，3-待验收，已回盘，4-已完成
		String logistics=request.getParameter("logistics");
		String waybill=request.getParameter("waybill");
		try{
			if (CodeUtil.checkParam(ffersconfid,theaterid)){
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String,Object> paramsMap=new HashMap<String,Object>();
			paramsMap.put("ffersconfid", ffersconfid);
			paramsMap.put("theaterid", theaterid);
			paramsMap.put("content", content);
			if(!CodeUtil.checkParam(status)){
				paramsMap.put("statu", status);
			}
			paramsMap.put("createtime", new Date());
			paramsMap.put("operatorid",operatorid);
			Map<String, Object> resultMap=ffersDao.insertFfers(paramsMap);
			JSONObject data = new JSONObject();
			data.put("ffersconfid", resultMap.get("ffersconfid"));
			resultJson.put("data", data);
		}catch (Exception e) {
			MyMongo.mErrorLog("新增回盘通知", request,e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("新增回盘通知成功",  etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"新增回盘通知");
		return this.response(resultJson, request);
	}*/
	
	/**
	 * 更新回盘通知信息
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@POST
	@Path("/updateFfers")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("ffers:update")
	@SystemServiceLog("更新回盘通知")
	public String updateFfers(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		
		//获取请求参数
		String operatorid =ReVerifyFilter.getUserid(request, response).toString();
		String username=(String)ReVerifyFilter.getUsername(request, response);
		String ffersid=request.getParameter("ffersid");
		String status=request.getParameter("status");				  //回盘状态 0-待回盘，1-已回盘
		String logistics=request.getParameter("logistics");
		String waybill=request.getParameter("waybill");
		try{
			if (CodeUtil.checkParam(ffersid)){
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String,Object> paramsMap=new HashMap<String,Object>();
			paramsMap.put("ffersid", ffersid);
			if(!CodeUtil.checkParam(status)){paramsMap.put("status", status);}
			if(!CodeUtil.checkParam(logistics)){paramsMap.put("logistics", logistics);}
			if(!CodeUtil.checkParam(waybill)){paramsMap.put("waybill", waybill);}
			paramsMap.put("modifytime", new Date());
			//paramsMap.put("operatorid",operatorid);
			ffersDao.updateFfers(paramsMap);
		}catch (Exception e) {
			MyMongo.mErrorLog("更新回盘通知", request,e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更新回盘通知成功",  etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"更新回盘通知");
		return this.response(resultJson, request);
	}
	
	/**
	 * 删除回盘通知信息
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@POST
	@Path("/deleteFfers")
	@Produces("text/html;charset=UTF-8")
	@RequiresPermissions("ffers:delete")
	@SystemServiceLog("删除回盘通知")
	public String deleteFfers(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		
		//获取请求参数
		//String operatorid =(String) request.getAttribute("userid");
		String username=(String)ReVerifyFilter.getUsername(request, response);
		String ffersid=request.getParameter("ffersid");
		try{
			if (CodeUtil.checkParam(ffersid)){
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			Map<String,Object> paramsMap=new HashMap<String,Object>();
			paramsMap.put("ffersconfid", ffersid);
			//先删除t_ffers(回盘通知表)下该批次的所有通知
			ffersDao.deleteFfers(paramsMap);
		}catch (Exception e) {
			MyMongo.mErrorLog("删除回盘通知", request,e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除回盘通知成功",  etime - stime, request, resultJson);
		resultJson.put("returnValue", username+"删除回盘通知");
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 查询回盘通知列表
	 * @param request
	 * @param response
	 * @return
	 * @throws CustomException 
	 */
	@POST
	@GET
	@Path("/getFfersList")
	@Produces("text/html;charset=UTF-8")
	//@RequiresPermissions("ffers:view")
	public String getFfersList(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		
		JSONObject resultJson = new JSONObject();
		long stime = System.currentTimeMillis();
		Integer userid=(Integer)ReVerifyFilter.getUserid(request,response);
		Map<String,Object>userInfo=userDao.findRoleAndTheaterById(userid);
		String roleType=userInfo.get("roletype").toString();
		//获取请求参数
		String page = request.getParameter("page");
		String pagesize = request.getParameter("pagesize");
		String ffersconfid=request.getParameter("ffersconfid");
		String theatername=request.getParameter("theatername");			//影院名称
		String theaternum=request.getParameter("theaternum");			//影院编码
		String ffersconfname=request.getParameter("ffersconfname");		//批次名称
		String moviename=request.getParameter("moviename");
		String s_time = request.getParameter("s_time");
		String e_time = request.getParameter("e_time");
		String status=request.getParameter("status");
		String ffersid=request.getParameter("ffersid");
		
		try{
			
			Map<String,Object> paramsMap = new HashMap<String,Object>();
			//默认为第一页，每页20
			if (CodeUtil.checkParam(page, pagesize))
			{
				paramsMap.put("start",  0);
				paramsMap.put("pagesize",  20);
			}else{
				paramsMap.put("start",  Integer.parseInt(pagesize) * (Integer.parseInt(page) - 1));
				paramsMap.put("pagesize",  Integer.parseInt(pagesize));
			}
			if(!CodeUtil.checkParam(ffersconfid)) paramsMap.put("ffersconfid", ffersconfid);
			if(!CodeUtil.checkParam(ffersid)) paramsMap.put("ffersid", ffersid);
			if(!CodeUtil.checkParam(theatername)) paramsMap.put("theatername", "%"+theatername+"%");
			if(!CodeUtil.checkParam(theaternum)) paramsMap.put("theaternum", "%"+theaternum+"%");
			if(!CodeUtil.checkParam(ffersconfname)) paramsMap.put("ffersconfname", "%"+ffersconfname+"%");
			if(!CodeUtil.checkParam(moviename)) paramsMap.put("moviename", "%"+moviename+"%");
			if (!CodeUtil.checkParam(s_time)){paramsMap.put("s_time", s_time + " 00:00:00");}
			if (!CodeUtil.checkParam(e_time)){paramsMap.put("e_time", e_time + " 23:59:59");}
			if (!CodeUtil.checkParam(status)){paramsMap.put("status",status);}
			if("2".equals(roleType)){
				paramsMap.put("theaterid", userInfo.get("theaterid"));
			}
			
			int total=ffersDao.queryFfersListCount(paramsMap);
			if(total>0){
				List<Map<String,Object>> ffersList=ffersDao.queryFfersList(paramsMap);
				for(Map<String,Object> map : ffersList){
					map.put("createtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date)map.get("createtime")));
					if(map.get("modifytime") != null && !map.get("modifytime").equals("")){
						map.put("modifytime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date)map.get("modifytime")));
					}else{
						map.put("modifytime", "无");
					}
					if(map.get("sendtime") != null && !map.get("sendtime").equals("")){
						map.put("sendtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date)map.get("sendtime")));
					}else{
						map.put("sendtime", "无");
					}
				}
				resultJson.put("data", ffersList);
			}
			resultJson.put("total", total);
			resultJson.put("current", page);
		}catch (Exception e) {
			MyMongo.mErrorLog("查询回盘通知列表", request,e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		long etime=System.currentTimeMillis();
		MyMongo.mRequestLog("查询回盘通知列表成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
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
	@Path("/uploadFfers")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("text/html;charset=UTF-8")
	@SystemServiceLog("上传回盘通知")
	@RequiresPermissions("ffers:upload")
	public String  uploadFfers(@Context HttpServletRequest request,@Context HttpServletResponse response) throws CustomException, IOException
    {
         	JSONObject resultJson = new JSONObject();
			String access_token = userRedis.getWXToken();
         	String username=ReVerifyFilter.getUsername(request, response);
         	String ffersconfid=request.getParameter("ffersconfid");
         	String templateid=request.getParameter("templateid");

			if(CodeUtil.checkParam(ffersconfid,templateid)){
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}

         	File file=FileUtil.uploadFile(request,ffersconfid);
         	List<List<Object>> readlist=ReadExcelUtil.readExcel(file);
         	if(readlist.size()==0){
         		throw new CustomException("表格没有数据");
         	}
			Map<String,Object> resultmap=templateDao.getTemplateOne(Integer.valueOf(templateid));

			int start_row=(int)resultmap.get("start_row");
			int theaternum_row=(int)resultmap.get("theaternum");
			int diskcode_row=(int)resultmap.get("diskcode");
			int theatername_row=(int)resultmap.get("theatername");
			int theaterchain_row=(int)resultmap.get("theaterchain");
			int moviename_row=(int)resultmap.get("moviename");
			int customer_phone_row=(int)resultmap.get("customer_phone");
			int customer_row=(int)resultmap.get("customer");

			int count=0;
			Date createtime=new Date();
			List<FfersBean> ffersList=new ArrayList<>();
			List<String> theaterList=new ArrayList<>();
			for(List<Object> list : readlist){
				if(count++<start_row  || list.size()<=0){
					continue;
				}
				String theatername=(String)list.get(theatername_row);
				if("".equals(theatername)){
					continue;
				}
	        	//通过影院名称查询影院id
	        	Map<String,Object> resultMap=theaterDao.findTheaterByName(theatername);
				if(resultMap==null){
					theaterList.add(theatername);
	        		//throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
	        		continue;
	        	}
	        	FfersBean bean=new FfersBean();
	        	bean.setCreatetime(createtime);
	        	bean.setTheaterid(resultMap.get("theaterid")+"");
	        	bean.setFfersconfid(ffersconfid);
	        	try{

	        	if(diskcode_row!=-1){
					bean.setDiskcode((String)list.get(diskcode_row));
				}
				if (moviename_row!=-1) {
					bean.setMoviename((String)list.get(moviename_row));
				}
				if(customer_phone_row!=-1){
	        		bean.setCustomer_phone((String)list.get(customer_phone_row));
				}
				if(customer_row!=-1){
					bean.setCustomer((String)list.get(customer_row));
				}
				if(theaterchain_row!=-1){
					bean.setTheaterchain((String)list.get(theaterchain_row));
				}
				if(theaternum_row!=-1){
					bean.setTheaternum((String)list.get(theaternum_row));
				}
				}catch (Exception e){
					throw new CustomException("模板和上传文件不匹配，请添加或修改模板");
				}
				bean.setStatus("0");
	        	ffersList.add(bean);
			}
			if(ffersList==null || ffersList.isEmpty()){
				throw new CustomException("上传失败");
			}
			try {
				 List<FfersBean> resultlist=ffersDao.insertBatch(ffersList);
			        List<Integer> idlist=new ArrayList<>();
			        if(resultlist != null && resultlist.size()>0){
			        	for(FfersBean bean : resultlist){
			        		idlist.add(bean.getFfersid());
			        	}
			        }

			        //给影院管理员和影院运营发送微信消息
				if(idlist!=null && !idlist.isEmpty()){
					List<Map<String,Object>> fferslist=ffersDao.getUserid(idlist);
					if(fferslist!=null && !fferslist.isEmpty()){
						//获取企业号号关注成员
						Map<Object, Object> usermap = wxtools.getUser(access_token, 1, 1, 1);
						/*Map<String,Object> paramsMap=new HashMap<String,Object>();
						List<Map<String,Object>> paramsList=new ArrayList<Map<String,Object>>();*/
						//未关注成员列表
						List<String> useridlist = new ArrayList<String>();
						//未关联影院列表
						List<String> theaterlist=new ArrayList<>();
						String title=fferslist.get(0).get("ffersconfname").toString();
						for(Map<String,Object> map : fferslist){
							String moviename=(String)map.get("moviename");
							String diskcode=(String)map.get("diskcode");
							String description = "影片名称:"+moviename+"\n硬盘编码："+diskcode;
							String ffersid = map.get("ffersid").toString();
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
								idlist.remove(ffersid);
								continue;
							}
							wxtools.sendMsg(userid.toString(), access_token, title,description, "http://vip.hn.yidepiao.net:7070/cinemacloud/rest/ffers/getuserinfo",ffersid);
						/*	paramsMap.put("ffersid", ffersid);
							paramsMap.put("modifytime", new Date());
							paramsList.add(paramsMap);*/
						}
					}

				}


			        JSONObject data=new JSONObject();
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
	
	@POST
	@Path("/cancelUpload")
	@Produces("text/html;charset=UTF-8")
	@SystemServiceLog("取消上传回盘通知")
	public String cancelUpload(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		String username=(String)ReVerifyFilter.getUsername(request, response);
		try {
			String ids = request.getParameter("ids");
			if (CodeUtil.checkParam(ids)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
			}
			List<String> idlist = Arrays.asList(ids.split(","));
			ffersDao.removeFfersList(idlist);
		} catch (Exception e) {
			MyMongo.mErrorLog("批量删除回盘通知错误", request, e);
			throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
		}
		resultJson.put("returnValue", username+"取消上传");
		return this.response(resultJson, request);
	}
	
	@POST
	@Path("/sendFfersMsg")
	@Produces("text/html;charset=UTF-8")
	@SystemServiceLog("发送微信回盘消息")
	public String sendFfersMsg(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

		JSONObject resultJson = new JSONObject();
		String access_token = userRedis.getWXToken();
		// 获取请求参数
		String ffersids = request.getParameter("ffersids");
		String username=(String)ReVerifyFilter.getUsername(request, response);
		if(CodeUtil.checkParam(ffersids)){
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		//Arrays.asList的返回值是只读，不能进行增删改(add/remove/set)
		List<String> idsarr = Arrays.asList(ffersids.split(","));
		List<String> ids = new ArrayList<String>(idsarr);

		//获取所有物料分发通知信息
		List<Map<String, Object>> resultlist = ffersDao.queryFfersListByIds(ids);
		if(resultlist!=null && resultlist.size()>0){
			//获取企业号号关注成员
			Map<Object, Object> usermap = wxtools.getUser(access_token, 1, 1, 1);
			Map<String,Object> paramsMap=new HashMap<String,Object>();
			List<Map<String,Object>> paramsList=new ArrayList<Map<String,Object>>();
			//未关注成员列表
			List<String> useridlist = new ArrayList<String>();
			//未关联影院列表
			List<String> theaterlist=new ArrayList<>();
			String title=(String)resultlist.get(0).get("ffersconfname");
			for (Map<String, Object> map : resultlist) {
				String moviename=(String)map.get("moviename");
				String diskcode=(String)map.get("diskcode");
				String description = "影片名称:"+moviename+"\n硬盘编码："+diskcode;
				String ffersid = map.get("ffersid").toString();
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
					ids.remove(ffersid);
					continue;
				}
				wxtools.sendMsg(userid.toString(), access_token, title,description, "http://vip.hn.yidepiao.net:7070/cinemacloud/rest/ffers/getuserinfo",ffersid);
				paramsMap.put("suppliesid", map.get("suppliesid"));
				paramsMap.put("modifytime", new Date());
				paramsList.add(paramsMap);
			}
			/*if(paramsList!=null && paramsList.size()>0){
				ffersDao.updateFfersByIds(paramsList);
			}*/
			if (ids != null && ids.size() > 0){
				List<Map<String, Object>> data=ffersDao.queryFfersListByIds(ids);
				resultJson.put("data", data);
			}
			resultJson.put("theater", theaterlist);
			resultJson.put("userid", useridlist);
		}
		resultJson.put("returnValue", username+"发送回盘通知");
		return this.response(resultJson, request);
	}

	@GET
	@POST
	@Path("/getuserinfo")
	@Produces("text/html;charset=UTF-8")
	public void getInfo(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, ServletException {
		String code = request.getParameter("code");
		String ffersid=request.getParameter("state");
		String access_token = userRedis.getWXToken();

		try {
			String userid = wxTools.getUserIdByCode(access_token, code);
			if(userid==null || "".equals(userid)){
				throw new CustomException("没有找到当前用户");
			}
			userRest.loginByUserid(userid);
			response.sendRedirect("/cinemacloud/weFfers?ffersid="+ffersid);

		} catch (IOException e) {
			MyMongo.mErrorLog("微信跳转页面失败", request, e);
			throw new CustomException("操作失败，请联系管管理员");

		}
	}


}
