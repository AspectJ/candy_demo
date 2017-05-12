package com.rest.template;

import com.cp.bean.ResMessage;
import com.cp.exception.CustomException;
import com.cp.filter.BaseServlet;
import com.cp.util.CodeUtil;
import com.cp.util.DateFormatUtil;
import com.mongo.MyMongo;
import com.rest.template.dao.TemplateDaoImpl;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Path("/rest/template")
public class TemplateRest extends BaseServlet{

    @Autowired
    private TemplateDaoImpl templateDao;

    @POST
    @Produces("text/html;charset:UTF-8")
    @Path("/createTemplate")
    public String createTemplate(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

        long stime=System.currentTimeMillis();
        JSONObject resultJson=new JSONObject();

        String name=request.getParameter("name");
        String diskcode=request.getParameter("diskcode");
        String theaternum=request.getParameter("theaternum");
        String theatername=request.getParameter("theatername");
        String theaterchain=request.getParameter("theaterchain");
        String moviename=request.getParameter("moviename");
        String customer_phone=request.getParameter("customer_phone");
        String customer=request.getParameter("customer");
        String start_row=request.getParameter("start_row");
        String type=request.getParameter("type");

        if(CodeUtil.checkParam(name,start_row,theatername,type)){
            MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
            throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
        }
        if(CodeUtil.checkParam(diskcode)){
            diskcode="-1";
        }
        if(CodeUtil.checkParam(theaternum)){
            theaternum="-1";
        }
        if(CodeUtil.checkParam(theatername)){
            theatername="-1";
        }
        if(CodeUtil.checkParam(theaterchain)){
            theaterchain="-1";
        }
        if(CodeUtil.checkParam(moviename)){
            moviename="-1";
        }
        if(CodeUtil.checkParam(customer_phone)){
            customer_phone="-1";
        }
        if(CodeUtil.checkParam(customer)){
            customer="-1";
        }

        try {
            Map<String,Object> paramsmap=new HashMap<>();
            paramsmap.put("name",name);
            paramsmap.put("diskcode",diskcode);
            paramsmap.put("theaternum",theaternum);
            paramsmap.put("theatername",theatername);
            paramsmap.put("theaterchain",theaterchain);
            paramsmap.put("moviename",moviename);
            paramsmap.put("customer_phone",customer_phone);
            paramsmap.put("customer",customer);
            paramsmap.put("createtime",new Date());
            paramsmap.put("start_row",start_row);
            paramsmap.put("type",type);

            templateDao.saveTemplate(paramsmap);
        } catch (Exception e) {
            MyMongo.mErrorLog("新增模板失败", request,e);
            throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
        }

        long etime = System.currentTimeMillis();
        MyMongo.mRequestLog("新增模板成功",  etime - stime, request, resultJson);

        //resultJson.put("returnValue", username+"新增回盘批次成功");
        return this.response(resultJson, request);

    }
    @POST
    @GET
    @Produces("text/html;charset:UTF-8")
    @Path("/getTemplateList")
    public String getTemplate(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

        long stime=System.currentTimeMillis();
        JSONObject resultJson=new JSONObject();

        String name=request.getParameter("name");
        String page=request.getParameter("page");
        String pagesize=request.getParameter("pagesize");
        String type=request.getParameter("type");

        Map<String,Object> paramsMap = new HashMap<String,Object>();
        //默认为第一页，每页10
        if (CodeUtil.checkParam(page, pagesize))
        {
            paramsMap.put("start",  0);
            paramsMap.put("pagesize",  10);
        }else{
            paramsMap.put("start",  Integer.parseInt(pagesize) * (Integer.parseInt(page) - 1));
            paramsMap.put("pagesize",  Integer.parseInt(pagesize));
        }
        if(!CodeUtil.checkParam(name)){
            paramsMap.put("name",name);
        }
        if(!CodeUtil.checkParam(type)){
            paramsMap.put("type",type);
        }

        try {
            int total=templateDao.getTemplateTotal(paramsMap);
            if(total>0){
                List<Map<String,Object>> resultlist=templateDao.getTemplate(paramsMap);
                for(Map<String,Object> map : resultlist){
                    if (map.get("createtime") != null && !"".equals(map.get("createtime"))) {
                        map.put("createtime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date)map.get("createtime")));
                    }else{
                        map.put("createtime","无");
                    }
                    if(map.get("modifytime") != null && !"".equals(map.get("modifytime"))){
                        map.put("modifytime", DateFormatUtil.to_yyyy_MM_dd_HH_mm_ss_str((Date)map.get("modifytime")));
                    }else{
                        map.put("modifytime", "无");
                    }
                }
                resultJson.put("data", resultlist);
            }
            resultJson.put("total", total);
            resultJson.put("current", page);
        }catch (Exception e) {
            MyMongo.mErrorLog("查询模板列表失败", request,e);
            throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
        }
        long etime=System.currentTimeMillis();
        MyMongo.mRequestLog("查询模板列表成功",  etime - stime, request, resultJson);
        return this.response(resultJson, request);

    }

    @POST
    @Path("/deleteTemplate")
    @Produces("text/html;charset=UTF-8")
    public String deleteTemplate(@Context HttpServletRequest request, @Context HttpServletResponse response) throws CustomException {

        JSONObject resultJson = new JSONObject();
        long stime = System.currentTimeMillis();

        //获取请求参数
        String templateid=request.getParameter("templateid");
        try{
            if (CodeUtil.checkParam(templateid)){
                MyMongo.mRequestFail(request, ResMessage.Lack_Param.code);
                throw new CustomException(ResMessage.getMessage(ResMessage.Lack_Param.code));
            }

           templateDao.removeTemplate(Integer.valueOf(templateid));
        }catch (Exception e) {
            MyMongo.mErrorLog("删除模板失败", request,e);
            throw new CustomException(ResMessage.getMessage(ResMessage.Server_Abnormal.code));
        }
        long etime = System.currentTimeMillis();
        MyMongo.mRequestLog("删除模板成功",  etime - stime, request, resultJson);
        return this.response(resultJson, request);
    }

}
