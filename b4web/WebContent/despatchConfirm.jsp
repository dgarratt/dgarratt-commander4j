<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="expires" content="0">
<META HTTP-Equiv="scanner" Content="enabled">
<META HTTP-Equiv="scanner" Content="autoenter">
<META HTTP-Equiv="acceleratekey" content="all">
<title>Confirm Despatch</title>
<link href="commander.css" rel="stylesheet" type="text/css">
</head>

<jsp:useBean id="Lang" class="com.commander4j.bean.JLanguage" scope="page">
	<jsp:setProperty name="Lang" property="hostID" value="<%=session.getAttribute(\"selectedHost\")%>" />
	<jsp:setProperty name="Lang" property="sessionID" value="<%=session.getId()%>" />
	<jsp:setProperty name="Lang" property="languageID" value="<%=session.getAttribute(\"language\")%>" />
</jsp:useBean>

<body>
	<form id="despatchConfirm" name="despatchConfirm" action="Process" method="post">
		<h2>
			<%=Lang.getText("dlg_Despatch_Confirm")%>
			<%
				out.println(session.getAttribute("despatchNo"));
			%>
		</h2>
		<br> <INPUT TYPE=CHECKBOX NAME="printSTNonConfirm">&nbsp;<%=Lang.getText("web_Print_STN")%>
		<br>
		<br>
		<%
			out.println(session.getAttribute("_ErrorMessage"));
		%>
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td width="230" height="20" align="center"><input type="submit" tabindex='1' name="buttonYes" value="<%=Lang.getText("web_Yes")%>" id="buttonYes" onclick="document.despatchConfirm.button.value='Yes';" />&nbsp; <input type="submit"
					tabindex='2' name="buttonNo" value="<%=Lang.getText("web_No")%>" id="buttonNo" onclick="document.despatchConfirm.button.value='No';" /></td>
			</tr>
		</table>
		<input type="hidden" name="formName" value="despatchConfirm.jsp" /> <input type="hidden" id="button" name="button" value="Yes" />
	</form>
</body>
</html>
