package com.rest.archive;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.mongo.MyMongo;
import com.rest.archive.dao.ArchiveDaoImpl;

import net.sf.json.JSONObject;

@Service
@Path("/rest/archive")
public class ArchiveRest extends BaseServlet {

	@Autowired
	@Qualifier("archiveDao")
	private ArchiveDaoImpl archiveDao;
	
	
	/**
	 * 查询归档列表
	 * @param request
	 * @return
	 */
	//http://localhost:8080/cinemacloud/rest/archive/findAllArchive
	@GET
	@POST
	@Path("/findAllArchive")
	public String findAllArchive(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			List<Map<String, Object>> resultList = archiveDao.findAllArchive();
			
			for (Map<String, Object> resultMap : resultList) {
				Object content_obj = resultMap.get("content");
				//移除content
				resultMap.remove("content");
				//将content转为Map
				JSONObject contentOBJ = JSONObject.fromObject(content_obj);
				
				List<Map<String, Object>> contList = new ArrayList<Map<String,Object>>();
				for(Object key : contentOBJ.keySet()) {
					Map<String, Object> contMap = new HashMap<String, Object>();
					
					String value = (String) contentOBJ.get(key);
					
					contMap.put((String) key, value);
					contList.add(contMap);
				}
				resultMap.put("contList", contList);
			}
			
			resultJson.put("data", resultList);
			
		}catch(Exception e) {
			MyMongo.mErrorLog("查询全部归档列表操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}

		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询全部归档列表操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
		
	}
}
