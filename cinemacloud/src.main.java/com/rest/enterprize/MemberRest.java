package com.rest.enterprize;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.Path;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.cp.util.HttpRequestTools;
import com.rest.user.dao.UserDaoImpl;

import net.sf.json.JSONObject;

/**
 * 企业号成员管理
 * @author john
 *
 */
@Service
@Path("/rest/member")
public class MemberRest {
	
	private static final Logger logger = LoggerFactory.getLogger(MemberRest.class);
	
	
	/**
	 * 创建成员
	 * @throws IOException 
	 */
	public static Integer createMember(Map<String, Object> userMap) throws IOException {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=%s";
		String access_token = EnterprizeRest.getAccess_token();
		url = String.format(url, access_token);
		String params = 	"{'userid': '"+ userMap.get("userid").toString() +"', "
							+ "'name': '"+ userMap.get("realname").toString() +"', "
							+ "'department': [1], "    //跟部门id为1
//							+ "'position': '产品经理', "
							+ "'mobile': '"+ userMap.get("mobile").toString() +"', "
//							+ "'gender': '1', "
//							+ "'email': 'zhangsan@gzdev.com', "
//							+ "'weixinid': 'zhangsan4dev', "
//							+ "'avatar_mediaid': '2-G6nrLmr5EC3MNb_-zL1dDdzkd0p7cNliYu9V5w7o8K0',"
//							+ "'extattr': {'attrs':[{'name':'爱好','value':'旅游'},{'name':'卡号','value':'1234567234'}]}"
							+ "}";
		
		String result = HttpRequestTools.getHttpRequest(url, 
											JSONObject.fromObject(params).toString(),
										"application/json;charset=utf-8");
		int errcode = JSONObject.fromObject(result).getInt("errcode");
		return errcode;
	}
	
	/**
	 * 获取成员信息(主要是查询关注状态status)
	 * @throws IOException 
	 */
	public static Integer getMemberInfo(Integer userid) throws IOException {
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";
		url = String.format(url, EnterprizeRest.getAccess_token(), userid);
		
		//http Get请求
		String result = HttpRequestTools.httpGet(url);
		//查询成员关注状态
		Integer status = JSONObject.fromObject(result).getInt("status");
		return status;
	}
	
	/**
	 * 删除成员
	 * @throws IOException 
	 */
	public static Integer deleteMember(String userid) throws IOException {
		
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=%s&userid=%s";
		url = String.format(url, EnterprizeRest.getAccess_token(), userid);
		
		String result = HttpRequestTools.httpGet(url);
		JSONObject jsonObject = JSONObject.fromObject(result);
		
		int errcode = jsonObject.getInt("errcode");
		return errcode;
	}
	
	/**
	 * 递归获取部门成员详情(主要是获取已关注成员和未关注成员列表)
	 * @param status
	 * 		//	0获取全部成员，1获取已关注成员列表，2获取禁用成员列表，4获取未关注成员列表。status可叠加,未填写则默认为4
	 * @return
	 * @throws IOException 
	 */
	public static String getDepartmentMemberDetails(int status) throws IOException {
		
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/list?access_token=%s&department_id=%s&fetch_child=%s&status=%s";
		int department_id = 1; 	// 获取的部门成员ID；跟部门ID为1
		int fetch_child = 1;	// 1/0：是否递归获取子部门下面的成员
		url = String.format(url, EnterprizeRest.getAccess_token(), department_id, fetch_child, status);
		String result = HttpRequestTools.httpGet(url);
		return result;
	}
	
	
	
	
	
	
	
	
	
}
