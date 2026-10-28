<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<html:html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商品一覧</title>
<%-- html:rewrite はコンテキストパスを自動で付ける(EL設定に依存しない) --%>
<link rel="stylesheet" type="text/css"
	href="<html:rewrite page='/css/example.css'/>">
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
			<div class="error-area">
				<html:errors />
			</div>
		</logic:messagesPresent>

		<div class="page-head">
			<div>
				<h2>商品一覧</h2>
				<bean:size id="itemCount" name="items" scope="request" />
				<p class="count">
					全
					<bean:write name="itemCount" />
					件
				</p>
			</div>
			<html:link page="/pages/Welcome.jsp"
				styleClass="btn btn-primary btn-lg">新規登録</html:link>
		</div>

		<%-- 検索ボックス(ステップ3で検索が動くようになる。それまでは押しても全件表示のまま) --%>
		<form class="search-box" role="search" method="get"
			action="<html:rewrite action='/itemList'/>">
			<label for="keyword">商品名で検索</label> <input id="keyword" type="text"
				name="keyword" placeholder="例:ほうじ茶"
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
						<th>添付ファイル</th>
						<th>作成日</th>
						<th>更新日</th>
						<th class="text-center">操作</th>
					</tr>
				</thead>
				<tbody>
					<logic:iterate id="item" name="items" scope="request">
						<tr>
							<%-- 商品名 --%>
							<td><bean:write name="item" property="itemName" /></td>
							<%-- 価格 --%>
							<td class="text-right">&yen;<bean:write name="item"
									property="itemPrice" format="#,##0" /></td>
							<%-- 種類 --%>
							<td><bean:write name="item" property="itemCategory"
									ignore="true" /></td>
							<%--　ファイル --%>
							<td class="file-cell"><logic:present name="item"
									property="fileName">
									<div>
									<span class="file-name"><bean:write name="item"
											property="fileName" /></span>
									<span class="badge"><bean:write name="item"
											property="fileType" /></span>
									</div>
									<html:link action="/itemPreview" paramId="itemId" paramName="item"
                             paramProperty="itemId" styleClass="btn btn-secondary">プレビュー</html:link>
								</logic:present> <logic:notPresent name="item" property="fileName">
									<span class="muted">-</span>
								</logic:notPresent></td>
							<%-- 作成日 --%>
							<td><bean:write name="item" property="createdAt"
									format="yyyy/MM/dd HH:mm" /></td>
							<td><logic:present name="item" property="updatedAt">
									<bean:write name="item" property="updatedAt"
										format="yyyy/MM/dd HH:mm" />
								</logic:present> <logic:notPresent name="item" property="updatedAt">
									<span class="muted">-</span>
								</logic:notPresent></td>
							<td>
								<div class="actions">
									<html:link action="/itemEdit" paramId="itemId" paramName="item"
										paramProperty="itemId" styleClass="btn btn-edit">編集</html:link>
									<form class="inline-form" method="post"
										action="<html:rewrite action='/itemDelete'/>"
										onsubmit="return confirm('この商品を削除します。よろしいですか?');">
										<input type="hidden" name="itemId"
											value="<bean:write name='item' property='itemId'/>">
										<button type="submit" class="btn btn-delete"
											aria-label="<bean:write name='item' property='itemName'/>を削除">削除</button>
									</form>
								</div>
							</td>
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
