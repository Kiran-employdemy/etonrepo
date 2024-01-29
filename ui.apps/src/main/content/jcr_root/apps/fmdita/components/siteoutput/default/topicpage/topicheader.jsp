<%--
  ADOBE CONFIDENTIAL
  __________________
  Copyright 2016 Adobe Systems Incorporated
  All Rights Reserved.
  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
  ____________________
--%>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/libs/fmdita/components/common/init.jsp" %>

<div class="topicheader">
  <div class="breadcrumb"></div>
  <div class="mobile-open-toc-button"></div>
  <%
    String pdfPath = properties.get("pdfPath", null);
    if(pdfPath != null && resourceResolver.getResource(pdfPath) != null) 
    {
  %>
    <div class="pdf-link">
      <a href="<%= pdfPath %>"><%= resBundle.getString("Download PDF") %></a>
    </div>
    <div class="pdf-icon"></div>
  <%
    }
  %>
</div>