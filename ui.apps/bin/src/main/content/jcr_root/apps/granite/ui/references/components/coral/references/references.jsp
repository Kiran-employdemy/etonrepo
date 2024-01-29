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

--%><%@page session="false" import="com.adobe.granite.ui.components.AttrBuilder,
                                    com.adobe.granite.ui.components.Tag,
                                    com.adobe.granite.ui.components.Config" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    Config cfg = cmp.getConfig();

    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    attrs.addClass("references foundation-layout-panel");
    attrs.addOther("component-path", resource.getPath());
    attrs.addOther("paths", "");
    attrs.addOthers(cfg.getProperties(), "class", "component-path", "paths");

    String title = cfg.get("jcr:title", String.class);

%><div <%= attrs.build() %>>

    <div class="granite-references-item granite-references-item--header granite-references-item--back">
        <coral-icon icon="chevronLeft" size="XXS"></coral-icon>
        <%= outVar(xssAPI, i18n, title) %>
    </div>

    <div class="message"></div>
    <div class="refSpinner">
        <div>
            <coral-wait centered size="L"></coral-wait>
        </div>
    </div>
    <div class="detail">
        <div class="detail-header granite-references-item granite-references-item--header"></div>
        <div class="detail-list"></div>
        <div class="detail-toolbars"></div>
    </div>
    <ul class="reference-list">
        <ui:includeClientLib categories="granite.ui.coral.references" />
        <sling:include path="<%= resource.getPath() %>" resourceType="granite/ui/components/coral/foundation/contsys" />
    </ul>
</div>
