


<%@page session="false" %>
<%@page contentType="text/html" pageEncoding="utf-8"
        import="com.day.cq.i18n.I18n,java.lang.StringBuilder,
    	java.util.ArrayList,
		java.util.List,
    	com.eaton.platform.core.util.SocialShareConfigUtil"
                 %>

<%@include file="/libs/foundation/global.jsp" %>

<%
String pubID=properties.get("pubID","");
List<String> allowedTempList = new ArrayList<String>();
allowedTempList = SocialShareConfigUtil.getAllowedTemplates(resource);

%>

<div>

    <strong>Publish ID :</strong> <%= pubID %> <br/>
    <strong>Allowed Templates :</strong> <br/>
    <c:set var="noOfTemplates" value="<%=allowedTempList.size()%>" />
    <c:set var="templateList" value="<%=allowedTempList%>" />

    <c:if test="${noOfTemplates gt 0}">
            <c:forEach items="${templateList}" var="item">
    			${item}<br>
			</c:forEach>
    </c:if>

<br/>

       <button onclick="dialog.show()">Edit Social Share Configuration</button>
<br/>

</div>
