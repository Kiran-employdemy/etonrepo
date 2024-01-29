<%--
********************************************************************
*
* ADOBE CONFIDENTIAL
*
* ___________________
*
* Copyright 2016 Adobe Systems Incorporated
* All Rights Reserved.
*
* NOTICE:  All information contained herein is, and remains
* the property of Adobe Systems Incorporated and its suppliers,
*if any.The intellectual and technical concepts contained
* herein are proprietary to Adobe Systems Incorporated and its
* suppliers and may be covered by U.S.and Foreign Patents,
*patents in process, and are protected by trade secret or copyright law.
* Dissemination of this information or reproduction of this material
* is strictly forbidden unless prior written permission is obtained
* from Adobe Systems Incorporated.
*********************************************************************


  Default head script.

  Draws the HTML head with some default content:
  - includes the WCML init script
  - includes the head libs script
  - includes the favicons
  - sets the HTML title
  - sets some meta data

  ==============================================================================
--%>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/libs/fmdita/components/siteoutput/default/uuidmeta/uuidmeta.jsp"%> 
<%
    String favIcon = currentDesign.getPath() + "/favicon.ico";
    if (resourceResolver.getResource(favIcon) == null) {
        favIcon = null;
    }
    String uuidMetaString=getUuidMetaString(properties);
%>

<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="keywords" content="<%= xssAPI.encodeForHTMLAttr(WCMUtils.getKeywords(currentPage, false)) %>">
    <meta name="description" content="<%= xssAPI.encodeForHTMLAttr(properties.get("jcr:description", "")) %>">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<% if (uuidMetaString != null) { %>
	<meta name="uuid" content="<%= xssAPI.encodeForHTMLAttr(uuidMetaString) %>">
	 <% } %>
    <cq:include script="headlibs.jsp"/>
    <cq:include script="/libs/wcm/core/components/init/init.jsp"/> 
    
    <% if (favIcon != null) { %>
    <link rel="icon" type="image/vnd.microsoft.icon" href="<%= xssAPI.getValidHref(favIcon) %>">
    <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="<%= xssAPI.getValidHref(favIcon) %>">
    <% } %>
    <title><%= currentPage.getTitle() == null ? xssAPI.encodeForHTML(currentPage.getName()) : xssAPI.encodeForHTML(currentPage.getTitle()) %></title>
	 <cq:include script="/apps/eaton/components/structure/eaton-edit-template-page/datalayer.html"/>
	  <cq:include script="/apps/eaton/components/structure/securedatalayer/securedatalayer.html"/>
	<cq:include script="/apps/eaton/components/content/ccms-metatags/ccms-metatags.html"/>
</head>