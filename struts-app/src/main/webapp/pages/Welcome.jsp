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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title><bean:message key="welcome.title"/></title>
  <link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/example.css'/>">
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

    <logic:notPresent name="org.apache.struts.action.MESSAGE" scope="application">
      <div class="error-area">
        ERROR: Application resources not loaded -- check servlet container
        logs for error messages.
      </div>
    </logic:notPresent>

    <logic:messagesPresent>
      <div class="error-area"><html:errors/></div>
    </logic:messagesPresent>

    <div class="page-head">
      <div>
        <h2><bean:message key="welcome.heading"/></h2>
        <p class="count"><bean:message key="welcome.message"/></p>
      </div>
    </div>

    <p class="form-note"><span class="required">*</span>は必須項目です。</p>

    <html:form action="/hello" styleClass="form-card">
      <div class="form-row">
        <label for="name"><span class="required">*</span>商品名</label>
        <html:text property="name" styleId="name" maxlength="50"/>
      </div>

      <div class="form-row">
        <label for="price"><span class="required">*</span>価格</label>
        <html:text property="price" styleId="price" maxlength="50"/>
      </div>

      <div class="form-row">
        <label for="category">種類</label>
        <html:select property="category" styleId="category">
          <html:option value="tea">茶系飲料</html:option>
          <html:option value="soda">炭酸飲料</html:option>
          <html:option value="fruit">果実飲料</html:option>
          <html:option value="other">その他</html:option>
        </html:select>
      </div>

      <div class="form-actions">
        <html:submit value="送信" styleClass="btn btn-submit btn-lg"/>
      </div>
    </html:form>

  </main>
</body>
</html:html>
