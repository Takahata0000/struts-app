<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><bean:message key="welcome.title" /></title>
<link rel="stylesheet" type="text/css"
	href="<html:rewrite page='/css/example.css'/>">
</head>

<body>
	<header class="app-header">
		<h1>商品管理</h1>
		<nav class="app-nav">
			<html:link action="/Welcome" styleClass="current">商品登録</html:link>
			<html:link action="/itemList">商品一覧</html:link>
		</nav>
	</header>

	<main class="container">

		<p>商品を登録しました。</p>
		<p>
			商品名：
			<bean:write name="helloForm" property="name" />
		</p>
		<p>
			価格：
			<bean:write name="helloForm" property="price" />
		</p>
		<p>
			種類：
			<bean:write name="helloForm" property="category" />
		</p>

		<%
out.println(new java.util.Date());

%>
	</main>
</body>
</html>