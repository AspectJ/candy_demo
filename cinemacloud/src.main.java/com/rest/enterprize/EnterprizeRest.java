package com.rest.enterprize;

import com.cp.util.HttpRequestTools;
import com.qq.weixin.mp.aes.AesException;
import com.qq.weixin.mp.aes.WXBizMsgCrypt;
import net.sf.json.JSONObject;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 企业号管理
 * @author john
 *
 */
@Service
@Path("/rest/enterprize")
public class EnterprizeRest {
	
	
	
	/**
	 * 解析企业号配置文件(enterprize.properties),获取corpid,secret等
	 */
	public static Map<String, Object> parse_en_conf() {
		ClassLoader classLoader = EnterprizeRest.class.getClassLoader();
		InputStream input = classLoader.getResourceAsStream("enterprize/enterprize.properties");
		
		Properties properties = new Properties();
		try {
			properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, Object> enMap = new HashMap<String, Object>();
		enMap.put("corpid", properties.get("en.corpid"));
		enMap.put("secret", properties.get("en.group1.secret"));
		
		return enMap;
	}
	
	
	/**
	 * 获取access_token
	 * @throws IOException
	 */
	public static String getAccess_token() throws IOException {
		
		Map<String, Object> resultMap = EnterprizeRest.parse_en_conf();
		String corpid = (String) resultMap.get("corpid");
		String secret = (String) resultMap.get("secret");
		
		String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
		url = String.format(url, corpid, secret);
		
		String result = HttpRequestTools.httpGet(url);
		JSONObject jsonObject = JSONObject.fromObject(result);
		return (String) jsonObject.get("access_token");
	}
	
	
	@GET
	@POST
	@Path("/sendMessage")
	public void sendMessage() throws MalformedURLException, IOException {
		String access_token = EnterprizeRest.getAccess_token();
		//发送消息url
		String urlStr = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + access_token;
		
		String url_Code = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STAT1E#wechat_redirect";
		Map<String, Object> resultMap = EnterprizeRest.parse_en_conf();
		String corpid = (String) resultMap.get("corpid");
		
		url_Code = String.format(url_Code, corpid, "http://duxl.ngrok.cc/cinemacloud/rest/enterprize/getUserCode");
		String param = "{'touser': 'dxl542298831',"
				+ "'msgtype': 'text'," 
				+ "'agentid': 1," 
				+ "'text': {"
				+ "'content': 'Holiday Request For Pony("+ url_Code +")'" + "},"
				+ "}";
		JSONObject jsonObject = JSONObject.fromObject(param);
		String contentType = "application/json;charset=UTF-8";
		String result = HttpRequestTools.getHttpRequest(urlStr, jsonObject.toString(), contentType);
		System.out.println(result);
	}
	
	/**
	 * OAuth 2.0获取用户code
	 * @throws IOException 
	 */
	@GET
	@POST
	@Path("/getUserCode")
	public void getUserCode(@Context HttpServletRequest request) throws IOException {
		String code = request.getParameter("code");
		
		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=%s&code=%s";
		url = String.format(url, EnterprizeRest.getAccess_token(), code);
		String result = HttpRequestTools.httpGet(url);
		System.out.println(result);
	}
	
	
	
	/**
	 * 将应用设置为回调模式
	 * @param request
	 * @param response
	 * @return
	 * @throws AesException
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	@GET
	@POST
	@Path("/callback")
	public String callback(@Context HttpServletRequest request, @Context HttpServletResponse response) throws AesException, IOException {
		
		//配置回调url
		String sToken = "rlfEodtdbsmLrXWz3oJ9llbThfLKig8";
		//String sCorpID = "wxd9d43b512d86cf6d";
		String sCorpID="wx14d59bae85c815fc";
		String sEncodingAESKey = "9rc1CaoxqV8dlh7FfeIvE52pk7g1nWDtUwOoe2N1lbX";

		WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
		
		@SuppressWarnings("unused")
		String queryString = request.getQueryString();
		// 解析出url上的参数值如下：
		String sVerifyMsgSig = request.getParameter("msg_signature").toString();
		
		String sVerifyTimeStamp = request.getParameter("timestamp").toString();
		
		String sVerifyNonce = request.getParameter("nonce").toString();
		
		
		//==============================接收消息及事件时的加解密处理 START================================
/*		String encryptMsg = IOUtils.toString(request.getInputStream());
		try {
			String sMsg = wxcpt.DecryptMsg(sVerifyMsgSig, sVerifyTimeStamp, sVerifyNonce, encryptMsg);
			System.out.println("after decrypt msg: \n" + sMsg);
			// TODO: 解析出明文xml标签的内容进行处理
			// For example:
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader(sMsg);
			InputSource is = new InputSource(sr);
			Document document = db.parse(is);

			Element root = document.getDocumentElement();
			NodeList nodelist1 = root.getElementsByTagName("Content");
			String Content = nodelist1.item(0).getTextContent();
			System.out.println("Content：" + Content);
			
		} catch (Exception e) {
			// TODO
			// 解密失败，失败原因请查看异常
			e.printStackTrace();
		}*/
		//==============================接收消息及事件时的加解密处理 END================================

			
		//================================配置回调模式 START========================================
		String sVerifyEchoStr = request.getParameter("echostr");
		String sEchoStr = ""; //需要返回的明文
		if(sVerifyEchoStr != null) {
			try {
				sEchoStr = wxcpt.VerifyURL(sVerifyMsgSig, sVerifyTimeStamp,
						sVerifyNonce, sVerifyEchoStr);
				System.out.println("verifyurl echostr: " + sEchoStr);
				// 验证URL成功，将sEchoStr返回
				//HttpUtils.SetResponse(sEchoStr);
			} catch (Exception e) {
				//验证URL失败，错误原因请查看异常
				e.printStackTrace();
			}
		}
		
		return sEchoStr;
		//================================配置回调模式 END========================================

	}
}
