package com.rest.content.program;

import com.cp.bean.Archive;
import com.cp.bean.Program;
import com.cp.bean.ResMessage;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.mongo.MyMongo;
import com.rest.content.program.dao.ProgramDaoImpl;
import com.rest.log.annotation.SystemServiceLog;
import com.rest.theater.dao.TheaterDaoImpl;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jboss.resteasy.annotations.cache.NoCache;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Path("/rest/program")
public class ProgramRest extends BaseServlet {
	
	@Autowired
	@Qualifier("programDao")
	private ProgramDaoImpl programDao;
	
	@Autowired
	@Qualifier("theaterDao")
	private TheaterDaoImpl theaterDao;
	
	
	/**
	 * 添加information栏目信息
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws CustomException 
	 */
	//url = http://localhost:8080/cinemacloud/rest/program/addProgram?program_name=123&archiveArr=...
	@GET
	@POST
	@NoCache
	@SystemServiceLog("添加栏目")
	@RequiresPermissions("program:create")
	@Path("/addProgram")
	public String addProgram(@Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException, CustomException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String program_name = request.getParameter("program_name"); //栏目名称
		String archiveArr = request.getParameter("archiveArr");		//归档属性（JSON数组）
		
		if(CodeUtil.checkParam(program_name, archiveArr)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("program_name", URLDecoder.decode(program_name, "UTF-8"));
		
		//验证栏目名重复性
		List<Map<String, Object>> repeatList = programDao.checkRepeat(paramsMap);
		if(repeatList != null && repeatList.size() > 0) {
			MyMongo.mRequestFail(request, ResMessage.Program_Name_Exists.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Program_Name_Exists.code));
		}

		//添加栏目信息
		programDao.addProgram(paramsMap);  //该方法执行结束后，program_id已经被put到了paramsMap中
		
		//归档信息
		JSONArray jsonArray = JSONArray.fromObject(URLDecoder.decode(archiveArr, "UTF-8"));
		if(jsonArray.size() > 0) {	//说明归档不是空数组[]
			paramsMap.put("jsonArray", jsonArray);
			
			//添加栏目归档属性信息
			programDao.addArchive(paramsMap);
		}
		
		//============================  日志记录 BEGIN  ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		resultJson.put("returnValue", "操作员[" + operatorName + "]成功添加栏目["+ paramsMap.get("program_name") +"]");
		
		//============================   日志记录 END   ===================================
			
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("添加栏目操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 修改栏目信息
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws CustomException 
	 */
	//url = http://localhost:8080/cinemacloud/rest/program/updateProgram?program_id=123&program_name=123&archiveArr=...
	@GET
	@POST
	@SystemServiceLog("修改栏目信息")
	@RequiresPermissions("program:update")
	@Path("/updateProgram")
	public String updateProgram(@Context HttpServletRequest request, @Context HttpServletResponse response) throws UnsupportedEncodingException, CustomException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String program_id = request.getParameter("program_id");		//栏目ID
		String program_name = request.getParameter("program_name"); //栏目名称
//		String archiveArr = request.getParameter("archiveArr");				//归档属性（JSON数组）   ----栏目的归档属性不能修改

		
		if(CodeUtil.checkParam(program_id, program_name)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("program_id", program_id);
		paramsMap.put("program_name", URLDecoder.decode(program_name, "UTF-8"));
		
		
		//更改栏目的栏目信息(名称)
		programDao.updateProgram(paramsMap);
		
//		//更新栏目的归档属性(先删除其归档属性，再添加)
//		programDao.deleteArchive(Integer.parseInt(program_id));
//
//		//归档信息
//		JSONArray jsonArray = JSONArray.fromObject(URLDecoder.decode(archiveArr, "UTF-8"));
//		if(jsonArray.size() > 0) { //说明归档不是空数组[]
//			paramsMap.put("jsonArray", jsonArray);
//			//添加归档属性
//			programDao.addArchive(paramsMap);
//		}
		
		//============================  日志记录 BEGIN  ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		resultJson.put("returnValue", "操作员[" + operatorName + "]成功修改栏目["+ paramsMap.get("program_name") +"]信息");
		
		//============================   日志记录 END   ===================================
			
			
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("修改栏目操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	/**
	 * 删除空栏目信息及其归档信息
	 * 		若 该栏目下有information，不允许删除(只能删除空栏目)
	 * @param request
	 * @return
	 * @throws CustomException 
	 */
	//url = http://localhost:8080/cinemacloud/rest/program/deleteProgram?program_id=123
	@GET
	@POST
	@SystemServiceLog("删除栏目")
	@RequiresPermissions("program:delete")
	@Path("/deleteProgram")
	public String deleteProgram(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String program_id = request.getParameter("program_id");
		
		if(CodeUtil.checkParam(program_id)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		//根据栏目查询information，判断是否为空
		List<Map<String, Object>> resultList = programDao.findInfoByProgram(Integer.parseInt(program_id));
		if(resultList != null && resultList.size() > 0) {
			MyMongo.mRequestFail(request, ResMessage.Program_Not_null.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Program_Not_null.code));
		}
		
		//根据栏目id查询栏目名称
		String program_name = programDao.findProgramNameById(program_id);
		
		//删除栏目  同时删除栏目的归档属性
		programDao.deleteProgram(Integer.parseInt(program_id));
		programDao.deleteArchive(Integer.parseInt(program_id));
			
		//============================  日志记录 BEGIN  ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		resultJson.put("returnValue", "操作员[" + operatorName + "]成功删除栏目["+ program_name +"]");
		
		//============================   日志记录 END   ===================================
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("删除栏目操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 更改栏目的可用状态
	 * @param request
	 * @return
	 * @throws CustomException 
	 */
	// url = http://localhost:8080/cinemacloud/rest/program/auditProgramStatus?program_id=105?status=要改变为的状态
	@GET
	@POST
	@SystemServiceLog("栏目审核")
	@RequiresPermissions("program:audit")
	@Path("/auditProgramStatus")
	public String auditProgramStatus(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		String program_id = request.getParameter("program_id");
		String status = request.getParameter("status");
		
		if(CodeUtil.checkParam(program_id, status)) {
			MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
			throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
		}
		
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("program_id", program_id);
		paramsMap.put("status", status);
		
		programDao.changeProgramStatus(paramsMap);
		
		//============================ 日志记录 BEGIN ===================================
		
		String operatorName = ReVerifyFilter.getUsername(request, response);
		//查询被审核的栏目名称
		String program_name = programDao.findProgramNameById(program_id);
		String message = Integer.parseInt(status) == 0 ? "禁用" : "可用";
		
		resultJson.put("returnValue", "操作员["+ operatorName +"]成功更改栏目["+ program_name +"]状态为" + message);
		
		//============================  日志记录 END  ===================================
			
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("更改栏目状态操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	
	
	/**
	 * 查询栏目列表及其归档属性(根据权限查询)
	 * @param request
	 * @return
	 */
	//url = http://localhost:8080/cinemacloud/rest/program/findProgramList
	@GET
	@POST
	@RequiresPermissions("program:view")
	@Path("/findProgramList")
	public String findProgramList(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		
		try {
			int userid = (int) ReVerifyFilter.getUserid(request, response);
			String page = request.getParameter("page");
			String pagesize = request.getParameter("pagesize");
			
			if(CodeUtil.checkParam(String.valueOf(userid))) {
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			//根据userid查询该用户所属影院信息
			Map<String, Object> theaterMap = theaterDao.findTheaterByUserid(String.valueOf(userid));
			
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			//分页参数
			if(!CodeUtil.checkParam(page, pagesize)) {
				paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
				paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
			}
			
			List<Program> programList = null;
			Integer count = null;
			
			//表示是某个影院用户
			if(theaterMap.containsKey("theaterid")) {
				
				paramsMap.put("theaterid", theaterMap.get("theaterid"));
				//根据影院归档查询其可看的目录(program)及其归档属性
				programList = programDao.getProgramListByArchive(paramsMap);
				count = programDao.getProgramListByArchiveCount(paramsMap);
			} else {
				programList = programDao.getAllProgram(paramsMap);
				count = programDao.getAllProgramCount(paramsMap);
			}
			
			for (Program program : programList) {
				//根据栏目查询归档
				List<Archive> arlist = programDao.findProgramArchive(program.getProgram_id());
				program.setArlist(arlist);
			}

			
			if(programList != null && programList.size() > 0) {
				resultJson.put("data", programList);
				resultJson.put("total", count);
			}
			
		} catch(Exception e) {
			MyMongo.mErrorLog("查询栏目列表操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查询栏目列表操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
	
	/**
	 * 查找当前栏目的归档信息
	 * @param request
	 * @return
	 */
	//url = http://localhost:8080/cinemacloud/rest/program/findProgramArchive?program_id=71
	@GET
	@POST
	@Path("/findProgramArchive")
	public String findProgramArchive(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		long stime = System.currentTimeMillis();
		JSONObject resultJson = new JSONObject();
		// -------------------------------------------------------------------------------
		try{
			
			String program_id = request.getParameter("program_id"); 	//program_id主键
			
			if(CodeUtil.checkParam(program_id))
			{
				MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
				return this.returnError(resultJson, ResMessage.Lack_Param.code, request);
			}
			
			List<Archive> resultList = programDao.findProgramArchive(Integer.parseInt(program_id));
			
			if(resultList != null && resultList.size() > 0) {
				resultJson.put("data", resultList);
			}
			
		}catch(Exception e) {
			MyMongo.mErrorLog("查找当前栏目的归档信息操作失败", request, e);
			return this.returnError(resultJson, ResMessage.Select_Info_Fail.code, request);
		}
		
		// -------------------------------------------------------------------------------
		long etime = System.currentTimeMillis();
		MyMongo.mRequestLog("查找当前栏目的归档信息操作成功",  etime - stime, request, resultJson);
		return this.response(resultJson, request);
	}
	
}
