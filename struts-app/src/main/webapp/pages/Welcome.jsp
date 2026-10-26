<%--
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with
    this work for additional information regarding copyright ownership.
    The ASF licenses this file to You under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with
    the License.  You may obtain a copy of the License at
   
         http://www.apache.org/licenses/LICENSE-2.0
   
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:html>
<head>
<title><bean:message key="welcome.title"/></title>
<html:base/>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/example.css" />
</head>
<body bgcolor="white">

<logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
  <font color="red">
    ERROR:  Application resources not loaded -- check servlet container
    logs for error messages.
  </font>
</logic:notPresent>

<h3><bean:message key="welcome.heading"/></h3>
<p><bean:message key="welcome.message"/></p>
<hr noshade="noshade" /> 
<div class="error-area">
  <html:errors/>
</div>  
  <html:form  action="/hello">
  	<p>* 商品名:<br/><html:text property="name" size="40" maxlength="50"/></p>
	<p>* 価格:<br/><html:text property="price" size="40" maxlength="50"/></p>
	
	<p>種類:<br/> 
	  <html:select property="category">
    	<html:option value="tea">茶系飲料</html:option>
	    <html:option value="soda">炭酸飲料</html:option>
    	<html:option value="fruit">果実飲料</html:option>
    	<html:option value="other">その他</html:option>
	  </html:select>
    </p>
	
    <html:submit value="送信"/>
  </html:form>
  
</body>
</html:html>