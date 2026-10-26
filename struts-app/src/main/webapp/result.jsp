<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html>
<body>
<a href="http://localhost:8080/struts-blank-1.3.10/Welcome.do"/>登録画面</a>
  <p>商品を登録しました。</p>
  <p>商品名：<bean:write name="helloForm" property="name"/></p>
  <p>価格：<bean:write name="helloForm" property="price"/></p>
  <p>種類：<bean:write name="helloForm" property="category"/></p>
  
<%
out.println(new java.util.Date());

%>
</body>
</html>