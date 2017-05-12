package com.cp.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 发送http请求
 */
public class HttpRequestTools
{
	/**
	 * 模拟HTTP实现POST请求
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static String getHttpRequest(String url, String param, String contentType) throws MalformedURLException, IOException{
		String result = "";
		
		HttpURLConnection connection = null;
		connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true); 
		connection.setRequestProperty("Accept-Charset", "utf-8");
		connection.setRequestMethod("POST"); 
		connection.setUseCaches(false); 
		
		// application/json;charset=UTF-8	application/x-www-form-urlencoded
		if(contentType != null){
			connection.setRequestProperty("contentType", contentType);
		}
		
		connection.connect(); // 建立连接
		
		if(param != null){
			OutputStream out = connection.getOutputStream(); // 创建输入输出流
			out.write(param.getBytes("UTF-8")); // 将参数输出到连接
			out.flush(); // 输出完成后刷新并关闭流
			out.close(); // 重要且易忽略步骤 (关闭流,切记!)
		}

		InputStream fin = connection.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(fin, "utf-8"));
		String str = reader.readLine();
		while (str != null)
		{
			result += str;
			str = reader.readLine();
		}
		return result;
	}
	
	
	/**
	 * 模拟HTTP实现GET请求
	 * @param urlStr
	 * @return
	 */
	public static String httpGet(String urlStr){
		String result="";
		HttpURLConnection connection=null;
		try {
			URL url=new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			// 设置是否从httpUrlConnection读入，默认情况下是true;
			connection.setDoInput(true);
			connection.setRequestProperty("Accept-Charset", "utf-8");
			connection.setRequestProperty("contentType", "utf-8");
			connection.setUseCaches(false);
			connection.connect();
			
			InputStream fin = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(fin, "utf-8"));
			String str = reader.readLine();
			while (str != null)
			{
				result += str;
				str = reader.readLine();
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	/**
	 * http post请求上传文件
	 * @param fileName
	 */
	@SuppressWarnings("finally")
	public static String httpUploadFile(String urlStr, String fileName) {  
		String result = "";
        try {  
  
            // 换行符  
            final String newLine = "\r\n";  
            final String boundaryPrefix = "--";  
            // 定义数据分隔线  
            String BOUNDARY = "========7d4a6d158c9";  
            // 服务器的域名  
            URL url = new URL(urlStr);  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            // 设置为POST情  
            conn.setRequestMethod("POST");  
            // 发送POST请求必须设置如下两行  
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);  
            // 设置请求头参数  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("Charsert", "UTF-8");  
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  
  
            OutputStream out = new DataOutputStream(conn.getOutputStream());  
  
            // 上传文件  
            File file = new File(fileName);  
            StringBuilder sb = new StringBuilder();  
            sb.append(boundaryPrefix);  
            sb.append(BOUNDARY);  
            sb.append(newLine);  
            // 文件参数,photo参数名可以随意修改  
            sb.append("Content-Disposition: form-data;name=\"photo\";filename=\"" + fileName  
                    + "\"" + newLine);  
            sb.append("Content-Type:application/octet-stream");  
            // 参数头设置完以后需要两个换行，然后才是参数内容  
            sb.append(newLine);  
            sb.append(newLine);  
  
            // 将参数头的数据写入到输出流中  
            out.write(sb.toString().getBytes());  
  
            // 数据输入流,用于读取文件数据  
            DataInputStream in = new DataInputStream(new FileInputStream(  
                    file));  
            byte[] bufferOut = new byte[1024];  
            int bytes = 0;  
            // 每次读1KB数据,并且将文件数据写入到输出流中  
            while ((bytes = in.read(bufferOut)) != -1) {  
                out.write(bufferOut, 0, bytes);  
            }  
            // 最后添加换行  
            out.write(newLine.getBytes());  
            in.close();  
  
            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。  
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine)  
                    .getBytes();  
            // 写上结尾标识  
            out.write(end_data);  
            out.flush();  
            out.close();  
  
            // 定义BufferedReader输入流来读取URL的响应  
            BufferedReader reader = new BufferedReader(new InputStreamReader(  
                    conn.getInputStream()));  
            String line = null;
            while ((line = reader.readLine()) != null) {  
                result += line;
            }  
  
        } catch (Exception e) {  
            System.out.println("发送POST请求出现异常！" + e);  
            e.printStackTrace();  
        } finally {
			return result;
		}
    }  
}