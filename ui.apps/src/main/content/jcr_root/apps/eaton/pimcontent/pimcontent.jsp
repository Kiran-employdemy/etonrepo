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

--%><%@page session="false" import="java.util.Calendar,
                                    com.adobe.granite.references.Reference,
                                    com.adobe.granite.ui.components.AttrBuilder,
                                    org.apache.sling.api.resource.Resource,
                                    org.apache.sling.api.resource.ValueMap,
                                    com.day.cq.commons.date.RelativeTimeFormat,
                                    com.day.cq.wcm.api.Page,
                                    com.day.cq.wcm.api.PageManager,
                                    com.day.cq.i18n.I18n
                                    " %><%
%><%@include file="/libs/foundation/global.jsp"%><%
    I18n i18n = new I18n(slingRequest);

    AttrBuilder attr = new AttrBuilder(slingRequest, xssAPI);
    Reference reference = (Reference) request.getAttribute("granite.ui.references.reference");

    if (reference == null) {
        return;
    }

    Resource source = reference.getSource();

    String title = xssAPI.encodeForHTML(source.getName());
    String navPath = source.getPath();


    Resource target = reference.getTarget();



    attr.addOther("type", "pimcontent");
    attr.addOther("path", navPath);
    attr.addClass("granite-references-item");

%><section <%= attr.build() %>>
    <div class="info">
        <span class="granite-references-title">
            <%= title %>
        </span>
        <div class="granite-references-subtitle">
            <%= i18n.get("Referenced resource:") %>
            <span title="<%= xssAPI.encodeForHTMLAttr(target.getPath()) %>">
                #<%= xssAPI.encodeForHTML(target.getName()) %>
            </span>
        </div>
    </div>
    <div class="actions" style="display: block;">
        <a is="coral-anchorbutton" block variant="primary" href="<%= xssAPI.getValidHref("/libs/commerce/gui/content/products/properties.html?item=" + navPath) %>" target="_blank">
            <%= i18n.get("Navigate to target") %>
        </a>
    </div>
</section>