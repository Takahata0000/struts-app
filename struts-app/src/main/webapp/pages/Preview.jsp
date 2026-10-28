<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>ファイルプレビュー</title>
  <link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/example.css'/>">
</head>
<body>
  <header class="app-header">
    <h1>商品管理</h1>
    <nav class="app-nav">
      <html:link action="/Welcome">商品登録</html:link>
      <html:link action="/itemList">商品一覧</html:link>
    </nav>
  </header>

  <main class="container">

    <logic:messagesPresent>
      <div class="error-area"><html:errors/></div>
    </logic:messagesPresent>

    <div class="page-head">
      <div>
        <h2>ファイルプレビュー</h2>
        <p class="count">
          <bean:write name="item" property="itemName"/> /
          <bean:write name="item" property="fileName"/>
          <span class="badge"><bean:write name="item" property="fileType"/></span>
        </p>
      </div>
      <html:link action="/itemList" styleClass="btn btn-secondary btn-lg">一覧へ戻る</html:link>
    </div>

    <%-- PDF: iframe に /itemFile.do を読み込ませる --%>
    <logic:equal name="item" property="fileType" value="pdf">
      <iframe class="preview-frame" title="PDFプレビュー"
              src="<html:rewrite action='/itemFile' paramId='itemId' paramName='item' paramProperty='itemId'/>"></iframe>
    </logic:equal>

    <%-- CSV: 先頭の数十行を表で表示する(値は bean:write で自動的にHTMLエスケープされる) --%>
    <logic:equal name="item" property="fileType" value="csv">
      <p class="preview-meta">
        1行目を見出しとして表示しています(文字コード: <bean:write name="csvCharset"/>)。
        <logic:equal name="csvTruncated" value="true">
          先頭の<bean:write name="csvMaxRows"/>行までを表示しています。全体はダウンロードして確認してください。
        </logic:equal>
      </p>
      <div class="table-wrap">
        <table class="list-table">
          <thead>
            <tr>
              <logic:iterate id="h" name="csvHeader">
                <th><bean:write name="h"/></th>
              </logic:iterate>
            </tr>
          </thead>
          <tbody>
            <logic:iterate id="row" name="csvRows">
              <tr>
                <logic:iterate id="cell" name="row">
                  <td><bean:write name="cell"/></td>
                </logic:iterate>
              </tr>
            </logic:iterate>
          </tbody>
        </table>
        <logic:empty name="csvRows">
          <p class="empty">データ行がありません。</p>
        </logic:empty>
      </div>
    </logic:equal>

    <div class="preview-actions">
      <a class="btn btn-primary btn-lg"
         href="<html:rewrite action='/itemFile' paramId='itemId' paramName='item' paramProperty='itemId'/>&amp;download=1">ダウンロード</a>
    </div>

  </main>
</body>
</html:html>
