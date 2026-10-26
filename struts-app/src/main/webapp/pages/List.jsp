<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<html:html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>商品一覧</title>
</head>
<body>
  <h1>商品一覧(ステップ1:装飾なし)</h1>

  <html:errors/>

  <table border="1">
    <tr>
      <th>商品名</th><th>価格</th><th>種類</th><th>作成日</th><th>更新日</th>
    </tr>
    <logic:iterate id="item" name="items" scope="request">
      <tr>
        <td><bean:write name="item" property="itemName"/></td>
        <td><bean:write name="item" property="itemPrice"/></td>
        <td><bean:write name="item" property="itemCategory" ignore="true"/></td>
        <td><bean:write name="item" property="createdAt" format="yyyy/MM/dd HH:mm"/></td>
        <td><bean:write name="item" property="updatedAt" format="yyyy/MM/dd HH:mm" ignore="true"/></td>
      </tr>
    </logic:iterate>
  </table>
</body>

</html:html>