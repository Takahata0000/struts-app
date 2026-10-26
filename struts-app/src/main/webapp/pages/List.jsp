<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>商品一覧</title>
  <%-- html:rewrite はコンテキストパスを自動で付ける(EL設定に依存しない) --%>
  <link rel="stylesheet" type="text/css" href="<html:rewrite page='/css/example.css'/>">
</head>
<body>
  <header class="app-header">
    <h1>商品管理</h1>
    <nav class="app-nav">
      <%-- 登録画面のパスは実際のものに合わせて変更する --%>
      <html:link page="/pages/Welcome.jsp">商品登録</html:link>
      <html:link action="/itemList" styleClass="current">商品一覧</html:link>
    </nav>
  </header>

  <main class="container">

    <logic:messagesPresent>
      <div class="error-area"><html:errors/></div>
    </logic:messagesPresent>

    <div class="page-head">
      <div>
        <h2>商品一覧</h2>
        <bean:size id="itemCount" name="items" scope="request"/>
        <p class="count">全<bean:write name="itemCount"/>件</p>
      </div>
      <html:link page="/pages/Welcome.jsp" styleClass="btn btn-primary btn-lg">新規登録</html:link>
    </div>

    <%-- 検索ボックス(ステップ3で検索が動くようになる。それまでは押しても全件表示のまま) --%>
    <form class="search-box" role="search" method="get" action="<html:rewrite action='/itemList'/>">
      <label for="keyword">商品名で検索</label>
      <input id="keyword" type="text" name="keyword" placeholder="例:ほうじ茶"
             value="<bean:write name='keyword' scope='request' ignore='true'/>">
      <button type="submit" class="btn btn-submit btn-lg">検索</button>
      <html:link action="/itemList" styleClass="btn btn-secondary btn-lg">クリア</html:link>
    </form>

    <div class="table-wrap">
      <table class="list-table">
        <thead>
          <tr>
            <th>商品名</th>
            <th class="text-right">価格</th>
            <th>種類</th>
            <th>作成日</th>
            <th>更新日</th>
            <%-- ステップ4で「操作」列を追加する --%>
          </tr>
        </thead>
        <tbody>
          <logic:iterate id="item" name="items" scope="request">
            <tr>
              <td><bean:write name="item" property="itemName"/></td>
              <td class="text-right">&yen;<bean:write name="item" property="itemPrice" format="#,##0"/></td>
              <td><bean:write name="item" property="itemCategory" ignore="true"/></td>
              <td><bean:write name="item" property="createdAt" format="yyyy/MM/dd HH:mm"/></td>
              <td>
                <logic:present name="item" property="updatedAt">
                  <bean:write name="item" property="updatedAt" format="yyyy/MM/dd HH:mm"/>
                </logic:present>
                <logic:notPresent name="item" property="updatedAt">
                  <span class="muted">-</span>
                </logic:notPresent>
              </td>
              <%-- ステップ4・5で「操作」列のセルを追加する --%>
            </tr>
          </logic:iterate>
        </tbody>
      </table>
      <logic:empty name="items" scope="request">
        <p class="empty">該当する商品はありません。</p>
      </logic:empty>
    </div>

  </main>
</body>
</html:html>
