<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
    <h1>success.jsp</h1>
    
   	Welcome:	<shiro:guest></shiro:guest>
   	
	<shiro:hasRole name="admin">
   		<br/><br/>
   		<a href="success.jsp">Success page</a>	
   	</shiro:hasRole>
   	
   	<shiro:hasRole name="admin">
   		<br/><br/>
   		<a href="admin.jsp">Admin page</a>	
   	</shiro:hasRole>
   	
   	
   	<shiro:hasRole name="user">
   		<br/><br/>
   		<a href="user.jsp">User page</a>
   	</shiro:hasRole>
   	
   	<br/><br/>
   	<a href="logout">logout</a>
   	
   	<br/><br/>
   	<a href="user/testShiroAnnotation">testShiroAnnotation</a>
   	
    
  </body>
</html>
