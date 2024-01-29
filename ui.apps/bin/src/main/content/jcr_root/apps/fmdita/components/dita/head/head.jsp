<%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================
--%><%@page import="javax.jcr.Node,
                  javax.jcr.NodeIterator,
                  com.day.cq.wcm.foundation.Paragraph,
                  com.day.cq.wcm.foundation.ParagraphSystem" %>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/libs/fmdita/components/common/init.jsp" %>

<%@ page import="com.day.cq.commons.Doctype" %>
<%
    String xs = Doctype.isXHTML(request) ? "/" : "";
    String favIcon = currentDesign.getPath() + "/favicon.ico";
    if (resourceResolver.getResource(favIcon) == null) {
        favIcon = null;
    }
%>

<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"<%=xs%>>
    <meta name="keywords" content="<%= xssAPI.encodeForHTMLAttr(WCMUtils.getKeywords(currentPage, false)) %>"<%=xs%>>
    <meta name="description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>"<%=xs%>>
    
    <%
       String markup = (String)request.getAttribute("markup");
       if (markup != null) {
           %><%= markup %><%
       }
    %>
     <%
       String uuidMetaString = (String)request.getAttribute("uuidMetaString");
    %>
	<% if (uuidMetaString != null) { %>
	<meta name="uuid" content="<%= xssAPI.encodeForHTMLAttr(uuidMetaString) %>"<%=xs%>>
	 <% } %>
    <cq:include script="/libs/foundation/components/page/headlibs.jsp"/>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/> 
    <% if (favIcon != null) { %>
    <link rel="icon" type="image/vnd.microsoft.icon" href="<%= xssAPI.getValidHref(favIcon) %>"<%=xs%>>
    <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="<%= xssAPI.getValidHref(favIcon) %>"<%=xs%>>
    <% } %>
	
    <title><%= currentPage.getTitle() == null ? xssAPI.encodeForHTML(currentPage.getName()) : xssAPI.encodeForHTML(currentPage.getTitle()) %></title>
    <%
      ParagraphSystem parSys = ParagraphSystem.create(resource, slingRequest);
      for (Paragraph par: parSys.paragraphs()){
         %><sling:include resource="<%= par %>"/><%
      }
    %>
	 <cq:include script="/apps/eaton/components/structure/eaton-edit-template-page/datalayer.html"/>
	  <cq:include script="/apps/eaton/components/structure/securedatalayer/securedatalayer.html"/>
    <cq:include script="/apps/eaton/components/content/ccms-metatags/ccms-metatags.html"/>
</head>