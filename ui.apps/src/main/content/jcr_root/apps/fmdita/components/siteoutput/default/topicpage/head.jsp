<%--
********************************************************************
*
*
*
***********************************************************************
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

<%@page session="false"%>

<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/libs/fmdita/components/siteoutput/default/uuidmeta/uuidmeta.jsp"%>
<%
    String uuidMetaString=getUuidMetaString(properties);
%>    
<%
request.setAttribute("markup", "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
%>
<%
request.setAttribute("uuidMetaString",uuidMetaString);
%>
<cq:include path="headnode" resourceType="fmdita/components/dita/head"/>
