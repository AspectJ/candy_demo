package com.rest.zone;

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

import com.cp.bean.ResMessage;
import com.cp.filter.BaseServlet;
import com.cp.util.CodeUtil;
import com.mongo.MyMongo;
import com.rest.zone.dao.ZoneDaoImpl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
@Path("/rest/zone")
public class ZoneRest extends BaseServlet{

	@Autowired
	@Qualifier("zoneDao")
	private ZoneDaoImpl zoneDao;
	
	/**
	 * 查询所有省份
	 * @param request
	 * @param response
	 * @return
	 */
	@GET
	@POST
	@Path("/ajaxProvince")
	public String ajaxProvince(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			List<Map<String, Object>> proList = zoneDao.findProvince();
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			for (int i = 0; i < proList.size(); i++) {
				Map<String, Object> resultMap = proList.get(i);
				jsonObject.put("province", resultMap.get("province"));
				jsonObject.put("provinceID", resultMap.get("provinceID"));
				jsonArray.add(jsonObject);
			}
			resultJson.put("data", jsonArray);
			
		}catch(Exception e) {
			MyMongo.mErrorLog("查询省份操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询省份操作操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 根据省份查询市级单位
	 * @param request
	 * @param response
	 * @return
	 */
	@GET
	@POST
	@Path("/ajaxCity")
	public String ajaxCity(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		String provinceID = request.getParameter("provinceID");
		if(CodeUtil.checkParam(provinceID)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}
		try {
			List<Map<String, Object>> cityList = zoneDao.findCity(Integer.parseInt(provinceID));
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			for (int i = 0; i < cityList.size(); i++) {
				Map<String, Object> resultMap = cityList.get(i);
				jsonObject.put("cityID", resultMap.get("cityID"));
				jsonObject.put("city", resultMap.get("city"));
				jsonArray.add(jsonObject);
			}
			resultJson.put("data", jsonArray);
			
		}catch(Exception e) {
			MyMongo.mErrorLog("根据省份查询市级操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据省份查询市级操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 根据市级查询县级单位
	 * @param request
	 * @param response
	 * @return
	 */
	@GET
	@POST
	@Path("/ajaxArea")
	public String ajaxArea(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		String cityID = request.getParameter("cityID");
		if(CodeUtil.checkParam(cityID)) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
		}
		try {
			List<Map<String, Object>> areaList = zoneDao.findArea(Integer.parseInt(cityID));
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = new JSONObject();
			for (int i = 0; i < areaList.size(); i++) {
				Map<String, Object> resultMap = areaList.get(i);
				jsonObject.put("areaID", resultMap.get("areaID"));
				jsonObject.put("area", resultMap.get("area"));
				jsonArray.add(jsonObject);
			}
			resultJson.put("data", jsonArray);
			
		}catch(Exception e) {
			MyMongo.mErrorLog("根据省份查询市级操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("根据省份查询市级操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
}
