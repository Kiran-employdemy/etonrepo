<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%>
<%@page session="false"%><%
%><%@page import="javax.jcr.Node,
        com.day.cq.i18n.I18n,
		org.apache.sling.api.resource.Resource,
		org.apache.sling.api.resource.ResourceResolver,
        org.apache.sling.api.resource.ResourceUtil"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0"%><%
%><%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0"%><%
%><cq:defineObjects /><%
    I18n i18n = new I18n(slingRequest);
    String contentPath = resource.getPath() + "/jcr:content/metadata/";
    Resource res = resourceResolver.getResource(contentPath);
    String status = ResourceUtil.getValueMap(res).get("dam:status", "");
    // Rights Restricted Flag EQS-53
    String rightsRestricted = ResourceUtil.getValueMap(res).get("xmp:eaton-rights-restricted", "");
    boolean rightsAreRestricted = rightsRestricted.equalsIgnoreCase("yes");
    if (status.equalsIgnoreCase("approved")) {%>
<coral-icon icon="thumbUp" size="XS" style = "margin-left: 10px; color: #B3DA8A;"></coral-icon>
<coral-tooltip class="status-approved-tooltip" placement="right" target="_prev" interaction="on" position="relative">
    <%= xssAPI.encodeForHTMLAttr(i18n.get("Approved")) %>
</coral-tooltip>
<%} else if(status.equalsIgnoreCase("rejected")) { %>
<coral-icon icon="thumbDown" size="XS" style = "margin-left: 10px; color: #FA7D73;"></coral-icon>
<coral-tooltip class="status-rejected-tooltip" placement="right" target="_prev" interaction="on" position="relative">
    <%= xssAPI.encodeForHTMLAttr(i18n.get("Rejected")) %>
</coral-tooltip>
<%} else if(status.equalsIgnoreCase("pending")) { %>
<coral-icon icon="pending" size="XS" style = "margin-left: 10px; color: #F5B041;"></coral-icon>
<coral-tooltip class="status-pending-tooltip" placement="right" target="_prev" interaction="on" position="relative">
    <%= xssAPI.encodeForHTMLAttr(i18n.get("Pending")) %>
</coral-tooltip>
<%} else if(status.equalsIgnoreCase("expired")) { %>
<coral-icon icon="alert" size="XS" style = "margin-left: 10px; color: #17202A;"></coral-icon>
<coral-tooltip class="status-expired-tooltip" placement="right" target="_prev" interaction="on" position="relative">
    <%= xssAPI.encodeForHTMLAttr(i18n.get("Expired")) %>
</coral-tooltip>
<%} else if(status.equalsIgnoreCase("retouched")) { %>
<coral-icon icon="wand" size="XS" style = "margin-left: 10px; color: #2980B9;"></coral-icon>
<coral-tooltip class="status-retouched-tooltip" placement="right" target="_prev" interaction="on" position="relative">
    <%= xssAPI.encodeForHTMLAttr(i18n.get("Retouched")) %>
</coral-tooltip>
<!-- BEGIN Rights Restricted Flag for EQS-53 -->
<%} if(rightsAreRestricted) { %>
<coral-icon icon="flag" size="XS" style = "margin-left: 10px; color: #FA7D73;"></coral-icon>
<coral-tooltip class="rights-are-restricted-tooltip" placement="right" target="_prev" interaction="on" position="relative">
    <%= xssAPI.encodeForHTMLAttr(i18n.get("Rights Restricted")) %>
</coral-tooltip>
<!-- END Rights Restricted Flag -->
<%}%>
