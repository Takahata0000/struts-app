<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>商品編集</title>
  <link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/example.css'/>">
</head>
<body>
  <header class="app-header">
    <h1>商品管理</h1>
    <nav class="app-nav">
      <html:link page="/pages/Input.jsp">商品登録</html:link>
      <html:link action="/itemList">商品一覧</html:link>
    </nav>
  </header>

  <main class="container">
    <div class="page-head">
      <h2>商品編集</h2>
    </div>

    <logic:messagesPresent>
      <div class="error-area"><html:errors/></div>
    </logic:messagesPresent>

    <html:form action="/itemUpdate" styleClass="form-card">
      <html:hidden property="itemId"/>

      <div class="form-row">
        <span class="muted">商品ID: <bean:write name="itemEditForm" property="itemId"/></span>
      </div>
      <div class="form-row">
        <label for="itemName">商品名</label>
        <html:text property="itemName" styleId="itemName"/>
      </div>
      <div class="form-row">
        <label for="itemPrice">価格</label>
        <html:text property="itemPrice" styleId="itemPrice"/>
      </div>
      <div class="form-row">
        <label for="itemCategory">種類</label>
        <html:text property="itemCategory" styleId="itemCategory"/>
      </div>

      <div class="form-actions">
        <html:submit value="更新" styleClass="btn btn-submit btn-lg"/>
        <html:link action="/itemList" styleClass="btn btn-secondary btn-lg">キャンセル</html:link>
      </div>
    </html:form>
  </main>
</body>
</html:html>