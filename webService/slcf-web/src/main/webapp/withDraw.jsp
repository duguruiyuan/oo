<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<title>提现回调页面模拟</title>
</head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${ctx }/static/script/jquery-1.7.2.min.js"></script>
<body>
	<div>提现回调页面模拟</div>
	<form action="/withdrawCash/withdrawJsp" method="post">
	交易编号<input type="text" name="tradeNo" id="tradeNo"><br>
	成功失败标示<input type="text" name="tradeCode" id="tradeCode" value="000000"><br><br><br>
	<button type="submit">回调模拟</button>
	<li>成功标示:000000</li>
	<li>失败标示:111111</li>
	</form>
</body>
</html>