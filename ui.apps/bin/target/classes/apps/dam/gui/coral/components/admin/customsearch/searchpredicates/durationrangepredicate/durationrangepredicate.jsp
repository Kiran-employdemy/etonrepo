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
%><%@page session="false" contentType="text/html; charset=utf-8"%><%
%><%@page import="java.util.Iterator,
                  javax.jcr.Node,
                  javax.jcr.NodeIterator,                  
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.request.RequestParameter,
                  org.apache.sling.api.resource.ResourceResolver,
                  com.adobe.granite.ui.components.Config,
                  org.apache.sling.commons.json.JSONObject,
                  com.adobe.granite.xss.XSSAPI,
                  com.day.cq.i18n.I18n,
                  javax.jcr.Node,
                  java.util.List,
                  java.util.ArrayList,
                  org.apache.jackrabbit.util.Text"%><%

%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0"%><%
%><%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0"%><%

%><cq:defineObjects /><%
    I18n i18n = new I18n(request);
    Config cfg = new Config(resource);
    String title = cfg.get("text", i18n.get("Range"));
    String mappedProperty = cfg.get("name", String.class);
    String listOrder = cfg.get("listOrder", "undefined");
    String globalMinOffset = cfg.get("globalMinOffset", String.class);
    String globalMaxOffset = cfg.get("globalMaxOffset", String.class);
    String step = cfg.get("step", String.class);
    Boolean bounded = (globalMinOffset != null && globalMaxOffset != null) ? true : false;
    Boolean stepped = step != null ? true : false;
	boolean foldableOpen = cfg.get("open", true);
	String selected = foldableOpen?"selected":"";

    String predicateName = listOrder + "_rangeproperty";
	String predicateNameProperty = predicateName + ".property";
	String lowerBound = predicateName + ".lowerBound";
    String lowerOperation = predicateName + ".lowerOperation";
	String upperBound = predicateName + ".upperBound";
	String upperOperation = predicateName + ".upperOperation";
	String breadcrumbs = predicateName + ".breadcrumbs";

%><ui:includeClientLib categories="dam.admin.searchpanel.durationrangepredicate" /><%
%><coral-accordion variant="large">
      <coral-accordion-item "<%=selected%>"
          data-property-to-map="<%= mappedProperty %>"
          data-metaType="checkboxgroup"
          data-type="range"
          data-list-order = "<%= listOrder %>">
        <coral-accordion-item-label><%= xssAPI.encodeForHTML(i18n.getVar(title)) %></coral-accordion-item-label>
        <coral-accordion-item-content class="durationrangepredicate">
            <div class="noslider coral-Form-fieldwrapper">
                <div class="min-duration-range-val">
                    <input is="coral-textfield" type="decimal" class="coral-Textfield coral-InputGroup-input min-mm-range-val" placeholder="<%= i18n.get("mm") %>" title="Minimum number of minutes"><input is="coral-textfield" type="decimal" class="coral-Textfield coral-InputGroup-input min-ss-range-val" placeholder="<%= i18n.get("ss") %>" title="Minimum number of seconds">
                </div>
                <coral-icon icon="switch" size="XS"></coral-icon>
                <div class="max-duration-range-val">
                    <input is="coral-textfield" type="decimal" class="coral-Textfield coral-InputGroup-input max-mm-range-val" placeholder="<%= i18n.get("mm") %>" title="Maximum number of minutes"><input is="coral-textfield" type="decimal" class="coral-Textfield coral-InputGroup-input max-ss-range-val" placeholder="<%= i18n.get("ss") %>" title="Maximum number of seconds">
                </div>
            </div>
            <input type="hidden" name="<%= xssAPI.encodeForHTMLAttr(predicateNameProperty) %>"
                                            value="<%= xssAPI.encodeForHTMLAttr(mappedProperty) %>">
            <input type="hidden" name="<%=lowerBound%>" value = "" class="search-predicate-durationrangepredicate search-predicate-durationrangepredicate-min" disabled>
            <input type="hidden" name="<%=lowerOperation%>" value = ">=" class="search-predicate-durationrangepredicate search-predicate-durationrangepredicate-min-operation" disabled>
            <input type="hidden" name="<%=upperBound%>" value = "" class="search-predicate-durationrangepredicate search-predicate-durationrangepredicate-max" disabled>
            <input type="hidden" name="<%=upperOperation%>" value = "<=" class="search-predicate-durationrangepredicate search-predicate-durationrangepredicate-max-operation" disabled>
        </coral-accordion-item-content>
      </coral-accordion-item>
</coral-accordion>
