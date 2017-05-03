<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
</head>
<body>
	<form action="getUrl" method="post">
		<input type="text" name="devID" value="douya520"> <input
			type="text" name="contentID" value="douya_201404171638290437_JPG">
		<input type="text" name="urlType" value="0"> <input
			type="submit" value="获取上传URL">
	</form>
	<%=request.getAttribute("uploadUrl")%>

	<form action="UploadImageTest" method="post"
		enctype="multipart/form-data">
		<input type="text" name="devID" value="210"> 
		<input type="text" name="contentID" value="210_201406190911170081_txt">
		<table>
			<tr>
				<td><input type="file" name="uploadImage" /></td>
			</tr>
		</table>
		<input type="submit" value="上 传">
	</form>

	<form action="getUrl" method="post">
		<input type="text" name="devID" value="douya520"> <input
			type="text" name="contentID" value="douya_201404171638290437_JPG">
		<input type="text" name="urlType" value="1"> <input
			type="submit" value="获取下载URL">
	</form>
	<%=request.getAttribute("downloadUrl")%>

	<form action="downloadImage" method="post">
		<input type="text" name="devID" value="douya520"> <input
			type="text" name="contentID" value="douya_201404171638290437_JPG">
		<input type="text" name="stamptime" value="201404171638290437">
		<input type="text" name="life" value="1800"> <input
			type="text" name="cry" value="f73a6ea4d7a021e43849ad3660d2a239">
		<input type="submit" value="下 载">
	</form>
	<label><%=request.getAttribute("message")%></label>

	<form action="deteleImage" method="post">
		<input type="text" name="devID" value="douya520"> <input
			type="text" name="contentID" value="douya_201404171638290437_JPG">
		<input type="submit" value="删除图片">
	</form>
	<label><%=request.getAttribute("deleteMessage")%></label>
</body>
</html>
