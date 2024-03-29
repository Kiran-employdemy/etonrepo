<%@page session="false" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils,
					com.day.cq.commons.Doctype" %><%
%><%@include file="/libs/foundation/global.jsp" %><%
    String xs = Doctype.isXHTML(request) ? "/" : "";
	String title = currentPage.getTitle() == null ? currentPage.getName() : currentPage.getTitle();
%><head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"<%=xs%>>
    <meta name="keywords" content="<%= WCMUtils.getKeywords(currentPage) %>"<%=xs%>>
    <% currentDesign.writeCssIncludes(pageContext); %>
    <title><%= xssAPI.encodeForHTML(title) %></title>
    <cq:include script="init.jsp"/>
	<cq:includeClientLib categories="cq.tagging"/>
	<cq:includeClientLib categories="eaton.multifield"/>
    <cq:includeClientLib categories="apps.eaton-custommultifield"/>
    <script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
</head>