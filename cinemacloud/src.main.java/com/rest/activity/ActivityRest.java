package com.rest.activity;

import com.cp.bean.ResMessage;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.filter.ReVerifyFilter;
import com.cp.util.CodeUtil;
import com.cp.util.DateFormatUtil;
import com.mongo.MyMongo;
import com.redis.UserRedisImpl;
import com.rest.activity.dao.ActivityDaoImpl;
import com.rest.log.annotation.SystemServiceLog;
import com.rest.user.UserRest;
import com.rest.user.dao.UserDaoImpl;
import com.rest.user.role.dao.RoleDaoImpl;
import com.wx.WXTools;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by john on 2017/3/14.
 */
@Service
@Path("/rest/activity")
public class ActivityRest extends BaseServlet {

    @Autowired
    @Qualifier("activityDao")
    private ActivityDaoImpl activityDao;

    @Autowired
    @Qualifier("userDao")
    private UserDaoImpl userDao;

    @Autowired
    @Qualifier("roleDao")
    private RoleDaoImpl roleDao;

    @Autowired
    private UserRedisImpl userRedis;

    @Autowired
    private WXTools wxTools;
    @Autowired
    private UserRest userRest;


    /**
     * 添加活动通知
     * @param request
     * @param response
     * @return
     * @throws CustomException
     * @throws UnsupportedEncodingException
     */
    //url = http://localhost:8080/cinemacloud/rest/activity/addActivity?title=999&content=666&type=1&theaterids=1,2,3&roleids=70,71,73
    @SystemServiceLog("添加活动")
    @RequiresPermissions("activity:create")
    @GET
    @POST
    @Path("/addActivity")
    public String addActivity(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, UnsupportedEncodingException {

        long stime = System.currentTimeMillis();
        JSONObject resultJson = new JSONObject();
        // -----------------------------------------------------------
        // --------------------

        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String type = request.getParameter("type");
        String starttime = request.getParameter("starttime"); //YYYY-MM-dd hh:mm:ss
        String endtime = request.getParameter("endtime");     //YYYY-MM-dd hh:mm:ss

        String theaterids = request.getParameter("theaterids");
        String roleids = request.getParameter("roleids");
        Integer userid = (Integer) ReVerifyFilter.getUserid(request, response);

        if(CodeUtil.checkParam(String.valueOf(userid), title, content, type, theaterids, roleids)) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }

        Map<String, Object> paramsMap = new HashMap<>();
        title=URLDecoder.decode(title, "UTF-8");
        content=URLDecoder.decode(content, "UTF-8");
        paramsMap.put("title", title);
        paramsMap.put("content", content);
        paramsMap.put("type", Integer.parseInt(type));
        paramsMap.put("operatorid", userid);
        paramsMap.put("theaterids", theaterids.split(","));
        paramsMap.put("roleids", roleids + ",");

        if(!CodeUtil.checkParam(starttime, endtime)) {
            paramsMap.put("starttime", starttime);
            paramsMap.put("endtime", endtime);
        }

        //检验活动名称重复性
        Integer repeatCount = activityDao.checkRepeat(paramsMap);
        if(repeatCount != 0) {
            MyMongo.mRequestFail(request, ResMessage.Activity_Title_Exists.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Activity_Title_Exists.code));
        }

        //添加活动通知
        activityDao.addActivity(paramsMap);

        //添加关联影院(rel_activity_theater)
        activityDao.addActivityTheater(paramsMap);


        Integer activityid=(Integer)paramsMap.get("activityid");
        List<Map<String,Object>> resultlist= activityDao.getActivityDetailByactivityid(activityid);

        String access_token=userRedis.getWXToken();
        for(Map<String,Object> info : resultlist){
            wxTools.sendMsg(info.get("userid").toString(), access_token, title, content, "http://vip.hn.yidepiao.net:7070/cinemacloud/rest/activity/getuserinfo", info.get("id").toString());
        }

        //============================ 日志记录 BEGIN ===================================
        //查询当前登录用户（操作员）的用户名
        String operatorName = ReVerifyFilter.getUsername(request, response);
        String message = "操作员["+ operatorName +"]成功添加活动通知["+ paramsMap.get("title") +"]";

        resultJson.put("returnValue", message);
        //============================  日志记录 END  ===================================


        // -------------------------------------------------------------------------------
        long etime = System.currentTimeMillis();
        MyMongo.mRequestLog("添加活动通知成功",  etime - stime, request, resultJson);
        return this.response(resultJson, request);
    }



    /**
     * 删除活动
     * @param request
     * @param response
     * @return
     * @throws CustomException
     * @throws UnsupportedEncodingException
     */
    //url = http://localhost:8080/cinemacloud/rest/activity/deleteActivity?activityid=1
    @SystemServiceLog("删除活动")
    @RequiresPermissions("activity:delete")
    @GET
    @POST
    @Path("/deleteActivity")
    public String deleteActivity(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, UnsupportedEncodingException {

        long stime = System.currentTimeMillis();
        JSONObject resultJson = new JSONObject();
        // -----------------------------------------------------------
        // --------------------

        String activityid = request.getParameter("activityid");

        if(CodeUtil.checkParam(activityid)) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }

        //删除影院已上传文件(如果影院上传了回执文件)
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("activityid", activityid);
        Map<String, Object> activityMap = activityDao.findActivityById(paramsMap);
        if(activityMap.containsKey("doc_url")) {
            File file = new File(request.getSession().getServletContext()
                                    .getRealPath(activityMap.get("doc_url").toString()));
            if(file.exists()) {
                file.delete();
            }
        }

        //删除影院回复信息(rel_activity_theater)
        activityDao.deleteActivityTheater(activityid);

        //删除活动（t_activity）
        activityDao.deleteActivity(activityid);


        //============================ 日志记录 BEGIN ===================================
        //查询当前登录用户（操作员）的用户名
        String operatorName = ReVerifyFilter.getUsername(request, response);
        String message = "操作员["+ operatorName +"]成功删除活动通知["+ activityMap.get("title") +"]";

        resultJson.put("returnValue", message);
        //============================  日志记录 END  ===================================

        // -------------------------------------------------------------------------------
        long etime = System.currentTimeMillis();
        MyMongo.mRequestLog("删除活动通知成功",  etime - stime, request, resultJson);
        return this.response(resultJson, request);
    }


    /**
     * 系统或者院线用户查询活动通知批次
     * @param request
     * @param response
     * @return
     * @throws CustomException
     * @throws UnsupportedEncodingException
     */
    // url = http://localhost:8080/cinemacloud/rest/activity/findActivityBatch
    @RequiresPermissions("activity:view")
    @GET
    @POST
    @Path("/findActivityBatch")
    public String findActivityBatch(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, UnsupportedEncodingException {

        long stime = System.currentTimeMillis();
        JSONObject resultJson = new JSONObject();
        // -----------------------------------------------------------

        String page = request.getParameter("page");
        String pagesize = request.getParameter("pagesize");
        String criteria = request.getParameter("criteria"); //模糊查询 title --条件查询
        Integer userid = (Integer) ReVerifyFilter.getUserid(request, response);
        if(CodeUtil.checkParam(String.valueOf(userid))) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }

        Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);

        //院线用户不能查询该接口
        if((int) userMap.get("roletype") == 2) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Privilege.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Privilege.code));
        }

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        if(!CodeUtil.checkParam(page, pagesize)) {
            paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
            paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
        }
        if(!CodeUtil.checkParam(criteria)) {
            paramsMap.put("criteria", URLDecoder.decode(criteria, "UTF-8"));
        }
        //分页查询活动批次及其总数
        List<Map<String, Object>> activityList = activityDao.findActivityBatch(paramsMap);
        Integer activityCount = activityDao.findActivityBatchCount(paramsMap);

        if(activityList != null && activityList.size() > 0) {
            //===============================================================
            List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();
            //查询活动批次的第一条详细信息
            Integer activityid = (Integer) activityList.get(0).get("activityid");
            paramsMap.put("activityid", activityid);
            Map<String, Object> activityMap = activityDao.findActivityById(paramsMap);
            //查询第一条批次的前十条rel_activity_theater信息
            Integer firstBatchCount = activityDao.getFirstBatchDetailCount(activityid);
            List<Map<String, Object>> firstBatchList = activityDao.getFirstBatchDetailPreTen(activityid);

            //因为一条活动关联的影院角色都是一样的，所以我们取该批次第一条的角色信息来共用
            if(firstBatchList != null && firstBatchList.size() > 0) {
                if(firstBatchList.get(0).containsKey("roleids")) {
                    roleList.clear();
                    //获取角色ids
                    String roleids = (String) firstBatchList.get(0).get("roleids");

                    String[] roleidArr = roleids.split(",");

                    for (String roleid: roleidArr) {
                        //查询角色信息
                        Map<String, Object> roleMap = roleDao.findRoleInfoById(Integer.parseInt(roleid));
                        roleList.add(roleMap);
                    }
                }

                for (Map<String, Object> ratMap: firstBatchList) {
                    if(ratMap.containsKey("roleids")) {
                        ratMap.put("roleList", roleList);
                    }
                }
            }
            //===============================================================

            resultJson.put("data", activityList);
            resultJson.put("total", activityCount);

            if(activityMap != null && activityMap.size() > 0) {
                if(firstBatchList != null && firstBatchList.size() > 0) {
                    activityMap.put("batchDetailList", firstBatchList);
                    activityMap.put("batchDetailCount", firstBatchCount);
                }
                resultJson.put("firstBatch", activityMap);

            }
        }

        // -------------------------------------------------------------------------------
        long etime = System.currentTimeMillis();
        MyMongo.mRequestLog("系统/院线用户查询活动通知批次操作成功",  etime - stime, request, resultJson);
        return this.response(resultJson, request);
    }


    /**
     * 系统用户/院线用户查询某一个批次的详细详细信息
     * @param request
     * @param response
     * @return
     * @throws CustomException
     * @throws UnsupportedEncodingException
     */
    // url = http://localhost:8080/cinemacloud/rest/activity/findActivityBatchDetailByActi_id?activityid=1
    @RequiresPermissions("activity:view")
    @GET
    @POST
    @Path("/findActivityBatchDetailByActi_id")
    public String findActivityBatchDetailByActi_id(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, UnsupportedEncodingException {

        long stime = System.currentTimeMillis();
        JSONObject resultJson = new JSONObject();
        // -----------------------------------------------------------

        String activityid = request.getParameter("activityid");
        String theatername = request.getParameter("theatername");     //影院名称   --条件模糊查询
        String theaternum = request.getParameter("theaternum");      //影院编码   --条件查询
        String status = request.getParameter("status");              //回复状态  --条件查询

        String page = request.getParameter("page");
        String pagesize = request.getParameter("pagesize");
        Integer userid = (Integer) ReVerifyFilter.getUserid(request, response);

        if(CodeUtil.checkParam(String.valueOf(userid), activityid)) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }

        Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);

        //院线用户不能查询该接口
        if((int) userMap.get("roletype") == 2) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Privilege.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Privilege.code));
        }

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("activityid", activityid);
        if(!CodeUtil.checkParam(page, pagesize)) {
            paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
            paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
        }

        //影院名称   --模糊查询
        if(!CodeUtil.checkParam(theatername)) {
            paramsMap.put("theatername", URLDecoder.decode(theatername, "UTF-8"));
        }
        //影院名称   --模糊查询
        if(!CodeUtil.checkParam(theaternum)) {
            paramsMap.put("theaternum", theaternum);
        }
        //模板类型 --条件查询
        if(!CodeUtil.checkParam(status)) {
            //  如果该条活动通知type = 0[status 0-初始状态 1-已阅]
            //  如果该条活动通知type = 1[status -1-不参加 0-初始状态 1-已参加]
            if(!CodeUtil.checkParam(status)) {
                paramsMap.put("status", status);
            }
        }

        List<Map<String, Object>> roleList = new ArrayList<Map<String, Object>>();


        //系统用户/院线用户查询某一个批次的详细详细信息
        Map<String, Object> activityMap = activityDao.findActivityById(paramsMap);

        //系统用户/院线用户查询该条活动关联的影院，角色信息等
        List<Map<String, Object>> batchDetailList = activityDao.findRel_Activity_theater(paramsMap);
        //查询count
        Integer count = activityDao.findRel_Activity_theaterCount(paramsMap);

        //因为一条活动关联的影院角色都是一样的，所以我们取该批次第一条的角色信息来共用
        if(batchDetailList != null && batchDetailList.size() > 0) {
            if(batchDetailList.get(0).containsKey("roleids")) {
                roleList.clear();
                //获取角色ids
                String roleids = (String) batchDetailList.get(0).get("roleids");

                String[] roleidArr = roleids.split(",");

                for (String roleid: roleidArr) {
                    //查询角色信息
                    Map<String, Object> roleMap = roleDao.findRoleInfoById(Integer.parseInt(roleid));
                    roleList.add(roleMap);
                }
            }

            for (Map<String, Object> ratMap: batchDetailList) {
                if(ratMap.containsKey("roleids")) {
                    ratMap.put("roleList", roleList);
                }
            }
        }

        if(activityMap != null && !activityMap.isEmpty()) {
            if(batchDetailList != null && batchDetailList.size() > 0) {
                activityMap.put("batchDetailList", batchDetailList);
                activityMap.put("batchDetailCount", count);
            }
            resultJson.put("data", activityMap);

        }

        // -------------------------------------------------------------------------------
        long etime = System.currentTimeMillis();
        MyMongo.mRequestLog("系统用户/院线用户查询某一个批次的详细详细信息操作成功",  etime - stime, request, resultJson);
        return this.response(resultJson, request);
    }


    /**
     * 影院用户查询自己所属影院及其角色所能看到的通知信息
     * @param request
     * @param response
     * @return
     * @throws CustomException
     * @throws UnsupportedEncodingException
     */
    // url = http://localhost:8080/cinemacloud/rest/activity/findAvailableActivityList
    @RequiresPermissions("activity:view")
    @GET
    @POST
    @Path("/findAvailableActivityList")
    public String findAvailableActivityList(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, UnsupportedEncodingException {

        long stime = System.currentTimeMillis();
        JSONObject resultJson = new JSONObject();
        // -----------------------------------------------------------

        String criteria = request.getParameter("criteria"); //活动标题  --条件查询
        String type = request.getParameter("type");         //模板类型  --条件查询

        String page = request.getParameter("page");
        String pagesize = request.getParameter("pagesize");
        Integer userid = (Integer) ReVerifyFilter.getUserid(request, response);

        if(CodeUtil.checkParam(String.valueOf(userid))) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }

        Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
        if((int) userMap.get("roletype") != 2) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Privilege.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Privilege.code));
        }

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("theaterid", userMap.get("theaterid"));
        paramsMap.put("roleid", userMap.get("roleid"));
        if(!CodeUtil.checkParam(page, pagesize)) {
            paramsMap.put("offsetNum", (Integer.parseInt(page) - 1) * Integer.parseInt(pagesize));		//从第多少条开始查询
            paramsMap.put("limitSize", Integer.parseInt(pagesize));	//每页查询数据条数：例如10条
        }

        if(!CodeUtil.checkParam(criteria)) {  //条件查询
            paramsMap.put("criteria", URLDecoder.decode(criteria, "UTF-8"));
        }

        //模板类型 --条件查询
        if(!CodeUtil.checkParam(type)) {
            paramsMap.put("type", type);

            //  type=0[status 0-初始状态 1-已阅]
            //  type=1[status -1-不参加 0-初始状态 1-已参加]
            String status = request.getParameter("status");
            if(!CodeUtil.checkParam(status)) {
                paramsMap.put("status", status);
            }
        }


        //影院用户只能查看自己影院并且自己所属角色能够有权限查看的活动通知
        Integer count = activityDao.findAvaiableActivityListCount(paramsMap);
        List<Map<String, Object>> activityList = activityDao.findAvaiableActivityList(paramsMap);

        List<Map<String, Object>> roleList = null;

        for (Map<String, Object>  actiMap : activityList) {
            if(actiMap.containsKey("roleids")) {
                roleList = new ArrayList<Map<String, Object>>();

                String[] roleidArr = actiMap.get("roleids").toString().split(",");
                for (String roleid : roleidArr) {
                    //查询角色信息
                    Map<String, Object> roleMap = roleDao.findRoleInfoById(Integer.parseInt(roleid));
                    roleList.add(roleMap);
                }
                actiMap.put("roleList", roleList);
            }
        }

        if(activityList != null && activityList.size() > 0) {
            resultJson.put("data", activityList);
            resultJson.put("total", count);
        }


        // -------------------------------------------------------------------------------
        long etime = System.currentTimeMillis();
        MyMongo.mRequestLog("影院用户查询活动通知操作成功",  etime - stime, request, resultJson);
        return this.response(resultJson, request);
    }


    /**
     * 影院用户对活动通知确认操作
     * @param request
     * @param response
     * @return
     * @throws CustomException
     * @throws UnsupportedEncodingException
     */
    // url = http://localhost:8080/cinemacloud/rest/activity/confirmActivity
    @SystemServiceLog("确认活动通知")
    @RequiresPermissions("activity:view")   //对于影院用户，若有"活动查看"权限则有"活动确认"权限
    @GET
    @POST
    @Path("/confirmActivity")
    public String confirmActivity(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, UnsupportedEncodingException {

        long stime = System.currentTimeMillis();
        JSONObject resultJson = new JSONObject();
        // -----------------------------------------------------------

        String activityid = request.getParameter("activityid"); //活动id
        String type = request.getParameter("type");             //是否需要回执(上传文件)   0-不需要  1-需要
        Integer userid = (Integer) ReVerifyFilter.getUserid(request, response);

        if(CodeUtil.checkParam(String.valueOf(userid), activityid, type)) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }

        Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);
        //系统用户和院线用户不能执行该操作
        if((int) userMap.get("roletype") != 2) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Privilege.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Privilege.code));
        }

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("activityid", activityid);
        paramsMap.put("userid", userid);
        paramsMap.put("theaterid", userMap.get("theaterid"));
        paramsMap.put("type", Integer.parseInt(type));

        //回执单模板
        if(Integer.parseInt(type) == 1) {
            String status = request.getParameter("status");
            if(CodeUtil.checkParam(status)) {
                MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
                throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
            }
            paramsMap.put("status", status);
            if(Integer.parseInt(status) == 1) { //表示确认参加
                String doc_url = request.getParameter("doc_url");
                paramsMap.put("doc_url", doc_url);
            }
        }
        activityDao.confirmActivity(paramsMap);


        //============================ 日志记录 BEGIN ===================================
        //查询活动title，用户日志记录
        Map<String, Object> actiMap = activityDao.findActivityById(paramsMap);
        String message = null;
        //查询当前登录用户（操作员）的用户名
        String operatorName = ReVerifyFilter.getUsername(request, response);

        resultJson.put("returnValue", message);

        if(Integer.parseInt(type) == 0) {
            message = "影院用户["+ operatorName +"]已确认（查看）活动通知["+ actiMap.get("title") +"]";
        } else {
            message = "影院用户["+ operatorName +"]确认"+ (Integer.parseInt((String) paramsMap.get("status")) == 1 ? "参加" : "不参加") +"活动["+ actiMap.get("title") +"]";
        }

        resultJson.put("returnValue", message);
        //============================  日志记录 END  ===================================


        // -------------------------------------------------------------------------------
        long etime = System.currentTimeMillis();
        MyMongo.mRequestLog("影院用户对活动通知确认操作成功",  etime - stime, request, resultJson);
        return this.response(resultJson, request);
    }

    @RequiresPermissions("activity:send")
    @GET
    @POST
    @Path("/sendActivityNotice")
    @Produces("text/html;charset=UTF-8")
    @SystemServiceLog("发送活动通知消息")
    public String sendActivityNotice(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

        JSONObject resultJson = new JSONObject();
        // 获取请求参数
        String id = request.getParameter("id");
        String username=ReVerifyFilter.getUsername(request, response);


        if(CodeUtil.checkParam(id)){
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }

        Map<String,Object> resultmap=activityDao.getActivityDetail(Integer.valueOf(id));
        if(resultmap==null || resultmap.isEmpty()){
            throw new CustomException("没有当前活动或当前活动关联影院用户没有关注公共号");
        }
        String[] theaterid=resultmap.get("theaterid").toString().split(",");
        String[] roleids=resultmap.get("roleids").toString().split(",");
        Map<String,Object> map=new HashMap<>();
        map.put("theaterids", theaterid);
        map.put("roleids", roleids);
        List<Map<String,Object>> useridList=userDao.getUserId(map);
        if(useridList==null || useridList.isEmpty()){
            throw new CustomException("没有找到当前影院的用户");
        }
        StringBuffer userids=new StringBuffer();
        for(Map<String,Object> usermap:useridList){
            userids.append(usermap.get("userid"));
            userids.append("|");
        }
        String title=resultmap.get("title").toString();
        String access_token=userRedis.getWXToken();
        wxTools.sendMsg(userids.toString(), access_token,"活动通知", title, "http://vip.hn.yidepiao.net:7070/cinemacloud/rest/activity/getuserinfo", id);


        resultJson.put("returnValue", username+"发送活动通知");
        return this.response(resultJson, request);
    }

    @GET
    @POST
    @Path("/getuserinfo")
    @Produces("text/html;charset=UTF-8")
    public void getInfo(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException, ServletException {
        String code = request.getParameter("code");
        String id=request.getParameter("state");
        String access_token = userRedis.getWXToken();

        try {
            String userid = wxTools.getUserIdByCode(access_token, code);
            if(userid==null || "".equals(userid)){
                throw new CustomException("没有找到当前用户");
            }
            userRest.loginByUserid(userid);
            response.sendRedirect("/cinemacloud/weNotice?id="+id);

        } catch (IOException e) {
            MyMongo.mErrorLog("微信跳转页面失败", request, e);
            throw new CustomException("操作失败，请联系管理员");

        }
    }

    @GET
    @POST
    @Path("/getActivityDetail")
    @Produces("text/html;charset=UTF-8")
    @SystemServiceLog("获取活动通知详情")
    public String getActivityDetail(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

        JSONObject resultJson = new JSONObject();
        // 获取请求参数
        String id = request.getParameter("id");


        if(CodeUtil.checkParam(id)){
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }

        Map<String,Object> resultmap=activityDao.getActivityDetail(Integer.valueOf(id));
        if(resultmap==null || resultmap.isEmpty()){
            throw new CustomException("没有当前活动或当前活动关联影院用户没有关注公共号");
        }

        if(resultmap.get("starttime") != null && !resultmap.get("starttime").equals("")){
            resultmap.put("starttime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date)resultmap.get("starttime")));
        }else{
            resultmap.put("starttime", "");
        }
        if(resultmap.get("endtime") != null && !resultmap.get("endtime").equals("")){
            resultmap.put("endtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date)resultmap.get("endtime")));
        }else{
            resultmap.put("endtime", "");
        }

        Object doc_url=resultmap.get("doc_url");
        if(doc_url!=null && !"".equals(doc_url)){
            String requesturl=request.getRequestURL().toString();
            int end=requesturl.indexOf("rest")+4;
            String projectUrl=requesturl.substring(0,end);
            String downloadUrl=projectUrl+"/file/downloadFile?doc_url="+doc_url.toString();
            resultmap.put("downloadUrl",downloadUrl);
        }
        resultJson.put("data",resultmap);

        // resultJson.put("returnValue", username+"发送回盘通知");
        return this.response(resultJson, request);
    }


    /**
     * 用户登录后查看最新活动通知列表
     * @param request
     * @param response
     * @return
     * @throws CustomException
     */
    // url = http://localhost:8080/cinemacloud/rest/activity/getLatestActivityList
    @RequiresPermissions("activity:view")
    @GET
    @POST
    @Path("/getLatestActivityList")
    public String getLatestActivityList(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

        long stime = System.currentTimeMillis();
        JSONObject resultJson = new JSONObject();
        // -----------------------------------------------------------

        Integer userid = (Integer) ReVerifyFilter.getUserid(request, response);

        if(CodeUtil.checkParam(String.valueOf(userid))) {
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }
        Map<String, Object> userMap = userDao.findRoleAndTheaterById(userid);

        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("userid", userid);


        List<Map<String, Object>> latestList = null;
        Integer count = null;

        //  影院用户登录之后查看未处理的活动通知
        if((int) userMap.get("roletype") == 2) {
            paramsMap.put("theaterid", userMap.get("theaterid"));
            paramsMap.put("roleid", userMap.get("roleid"));

            latestList = activityDao.getAvailableLatestActivity(paramsMap);
            count = activityDao.getAvailableLatestActivityCount(paramsMap);
        }
        //  院线用户/系统用户 查看最新活动列表(非自己添加的)
        else {
            latestList = activityDao.getAllLatestActivity(paramsMap);
            count = activityDao.getAllLatestActivityCount(paramsMap);
        }

        if(latestList != null && latestList.size() > 0) {
            resultJson.put("data", latestList);
            resultJson.put("total", count);
        }

        // -------------------------------------------------------------------------------
        long etime = System.currentTimeMillis();
        MyMongo.mRequestLog("查看最新活动通知列表成功",  etime - stime, request, resultJson);
        return this.response(resultJson, request);
    }



    @Test
    public void fun() {
        System.out.println("123");
        String pass = BCrypt.hashpw("123456", BCrypt.gensalt(12));
        System.out.println(pass);
    }
}
