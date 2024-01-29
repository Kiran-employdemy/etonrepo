<%--
  ADOBE CONFIDENTIAL

  Copyright 2015 Adobe Systems Incorporated
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
--%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page import="org.apache.sling.api.resource.Resource,
				  javax.jcr.Node,
				  com.day.cq.dam.api.Asset"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0"%><%
%><%@include file="/libs/dam/gui/coral/components/admin/contentrenderer/base/base.jsp"%><%
%><%@include file="/libs/dam/gui/coral/components/admin/contentrenderer/base/assetBase.jsp"%><%--###
ASSET Properties
=========

###--%><%
    long publishDateInMillis = request.getAttribute(PUBLISH_DATE_IN_MILLIS) != null ? (long) request.getAttribute(PUBLISH_DATE_IN_MILLIS) : -1;
    String publishedDate = request.getAttribute(PUBLISHED_DATE) != null ? (String) request.getAttribute(PUBLISHED_DATE) : null;
    boolean isDeactivated = request.getAttribute(IS_DEACTIVATED) != null ? (boolean) request.getAttribute(IS_DEACTIVATED) : false;
    String publishedBy = request.getAttribute(PUBLISHED_BY) != null ? (String) request.getAttribute(PUBLISHED_BY) : "";
    long assetLastModification = request.getAttribute(ASSET_LAST_MODIFICATION) != null ? (long) request.getAttribute(ASSET_LAST_MODIFICATION) : 0;
    String lastModified = request.getAttribute(LAST_MODIFIED) != null ? (String) request.getAttribute(LAST_MODIFIED) : "";
    int commentsCount = request.getAttribute(COMMENTS_COUNT) != null ? (int) request.getAttribute(COMMENTS_COUNT) : 0;
    String status = request.getAttribute(STATUS) != null ? (String) request.getAttribute(STATUS) : "";
    boolean isAssetExpired = request.getAttribute(IS_ASSETEXPIRED) != null ? (boolean) request.getAttribute(IS_ASSETEXPIRED) : false;
    boolean isSubAssetExpired = request.getAttribute(IS_SUBASSET_EXPIRED) != null ? (boolean) request.getAttribute(IS_SUBASSET_EXPIRED) : false;
    String size = request.getAttribute(SIZE) != null ? (String) request.getAttribute(SIZE) : "0.0 B";
    String resolution = request.getAttribute(RESOLUTION) != null ? (String) request.getAttribute(RESOLUTION) : "";
    double averageRating = request.getAttribute(AVERAGE_RATING) != null ? (double) request.getAttribute(AVERAGE_RATING) : 0.0;
    long width = request.getAttribute(WIDTH) != null ? (long) request.getAttribute(WIDTH) : 0;
    long height = request.getAttribute(HEIGHT) != null ? (long) request.getAttribute(HEIGHT) : 0;
    long bytes = request.getAttribute(BYTES) != null ? (long) request.getAttribute(BYTES) : 0;
    JSONObject viewSettings = (JSONObject) request.getAttribute(VIEW_SETTINGS);
    String dateFormat = viewSettings.getString(VIEW_SETTINGS_PN_DATE_FORMAT);

%><coral-card-propertylist>
    <% if (publishDateInMillis < assetLastModification) { %>
    <coral-card-property icon="edit" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Modified")) %>">
        <foundation-time type="datetime" format="<%= xssAPI.encodeForHTMLAttr(dateFormat) %>" value="<%= xssAPI.encodeForHTMLAttr(lastModified) %>"></foundation-time>
    </coral-card-property>
    <% } if (publishedDate != null && !isDeactivated) { %>
    <coral-card-property icon="globe" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Published")) %>">
        <foundation-time type="datetime" format="<%= xssAPI.encodeForHTMLAttr(dateFormat) %>" value="<%= xssAPI.encodeForHTMLAttr(publishedDate) %>"></foundation-time>
    </coral-card-property>
    <% } else if (publishedDate != null && isDeactivated) { // expecting a previous publishing date otherwise it couldn't be deactivated
    %>
    <coral-card-property icon="unpublish" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Unpublished")) %>">
        <foundation-time type="datetime" format="<%= xssAPI.encodeForHTMLAttr(dateFormat) %>" value="<%= xssAPI.encodeForHTMLAttr(publishedDate) %>"></foundation-time>
    </coral-card-property>
    <% } if (commentsCount > 0) { %>
    <coral-card-property icon="comment" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Comments")) %>"><%= xssAPI.encodeForHTML(Integer.toString(commentsCount)) %></coral-card-property>
    <!-- BEGIN Icons and Tooltips mapped to dam:status value-->
    <!-- Approved -->
    <% } if (status.equalsIgnoreCase("approved")) { %>
    <coral-card-property class="status approved" icon="thumbUp" style="z-index: 1; position:relative;"></coral-card-property>
    <coral-tooltip class="tooltip status-approved-tooltip" placement="bottom" target="_prev" interaction="on" position="relative">
        <%= xssAPI.encodeForHTMLAttr(i18n.get("Approved")) %>
    </coral-tooltip>
    <!-- Rejected -->
    <% } else if (status.equalsIgnoreCase("rejected")) { %>
    <coral-card-property class="status rejected" icon="thumbDown" style="z-index: 1; position:relative;"></coral-card-property>
    <coral-tooltip class="tooltip status-rejected-tooltip" placement="bottom" target="_prev" interaction="on" position="relative">
        <%= xssAPI.encodeForHTMLAttr(i18n.get("Rejected")) %>
    </coral-tooltip>
    <!-- Pending -->
    <% } else if (status.equalsIgnoreCase("pending")) { %>
    <coral-card-property class="status pending" icon="pending" style="z-index: 1; position:relative;"></coral-card-property>
    <coral-tooltip class="tooltip status-pending-tooltip" placement="bottom" target="_prev" interaction="on" position="relative">
        <%= xssAPI.encodeForHTMLAttr(i18n.get("Pending")) %>
    </coral-tooltip>
    <!-- Expired -->
    <% } else if (status.equalsIgnoreCase("expired")) { %>
    <coral-card-property class="status expired" icon="alert" style="z-index: 1; position:relative;" ></coral-card-property>
    <coral-tooltip class="tooltip status-expired-tooltip" placement="bottom" target="_prev" interaction="on" position="relative">
        <%= xssAPI.encodeForHTMLAttr(i18n.get("Expired")) %>
    </coral-tooltip>
    <!-- Retouched -->
    <% } else if (status.equalsIgnoreCase("retouched")) { %>
    <coral-card-property class="status retouched" icon="wand" style="z-index: 1; position:relative;"></coral-card-property>
    <coral-tooltip class="tooltip status-retouched-tooltip" placement="bottom" target="_prev" interaction="on" position="relative">
        <%= xssAPI.encodeForHTMLAttr(i18n.get("Retouched")) %>
    </coral-tooltip>
    <!-- END Icons and Tooltips mapped to dam:status value-->
    <% } if (isAssetExpired || isSubAssetExpired) { %>
    <coral-card-property class="expirystatus" icon="flag" data-is-asset-expired="<%= isAssetExpired %>" data-is-sub-asset-expired="<%= isSubAssetExpired %>" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("Expired")) %>"></coral-card-property>
    <% } %>
    <coral-card-property data-size="<%= bytes%>" title="<%= xssAPI.encodeForHTMLAttr(i18n.get("size")) %>"><%= xssAPI.encodeForHTML(size) %></coral-card-property>
</coral-card-propertylist>
<%  if (resolution.length() > 0 || averageRating > 0) {  %>
<coral-card-propertylist>
    <%  if (resolution.length() > 0) {  %>
    <coral-card-property title="<%= xssAPI.encodeForHTMLAttr(i18n.get("resolution")) %>" data-width="<%= width%>"><%= xssAPI.encodeForHTML(resolution)%></coral-card-property>
    <% } %>
    <% if (averageRating > 0) {
        int averageCharacterstic = (int) averageRating;
        double averageMantissa = averageRating - averageCharacterstic;
        double sizeinrem = 0.75;

        double widthMantissa = averageMantissa * sizeinrem;
    %><coral-card-property class="rating expired" title = "<%=averageRating%>">
    <div class = "rating-background" style = "color: rgba(0, 0, 0, 0.4);">
        <coral-icon icon="star" size="XS"></coral-icon>
        <coral-icon icon="star" size="XS"></coral-icon>
        <coral-icon icon="star" size="XS"></coral-icon>
        <coral-icon icon="star" size="XS"></coral-icon>
        <coral-icon icon="star" size="XS"></coral-icon>
    </div>
    <div class = "rating-foreground" style = "color: #ffd700;margin-top: -17px;">
        <%
            for (int i = 0 ; i < averageCharacterstic ; i++) {
        %>
        <coral-icon icon="star" size="XS" style="overflow: hidden;"></coral-icon>
        <%
            }
            if (averageMantissa > 0) {
                String style = "overflow: hidden; width:" + widthMantissa + "rem;";
        %>
        <coral-icon icon="star" size="XS" style="<%=style%>"></coral-icon>
        <%
            }
        %>
    </div>
</coral-card-property>
    <% } %>
</coral-card-propertylist>
<% } %>