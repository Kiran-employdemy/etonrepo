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

--%><%@page session="false" import="com.adobe.granite.references.Reference,
                                    com.adobe.granite.references.ReferenceAggregator,
									com.eaton.platform.core.services.ReferenceService,
									org.apache.sling.api.resource.ResourceResolver,
									com.day.cq.search.result.Hit,
                                    com.adobe.granite.references.ReferenceList,
                                    com.adobe.granite.ui.components.Config,
                                    org.apache.sling.api.resource.Resource,
                                    java.util.HashMap,
                                    java.util.Iterator,
									java.util.List,
                                    java.util.Map,
                                    java.util.Set"%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    // if nothing is selected, then item is null
    if (slingRequest.getRequestParameter("item") == null) {
        log.error("Reference list resource must be provided via [item] request parameter");
        return;
    }
	ReferenceService referenceService = sling.getService(ReferenceService.class);
    ReferenceAggregator referenceAggregator = sling.getService(ReferenceAggregator.class);
    if (referenceAggregator == null) {
        log.warn("Unable to get references aggregator service");
        return;
    }

    String path = slingRequest.getRequestParameter("item").getString("UTF-8");
    if (path == null) {
        log.warn("Reference path hasn't been provided");
        return;
    }

    // get references groups
    Config cfg = new Config(resource);
    Resource itemsRes = cfg.getChild("items");
    if (itemsRes == null) {
        log.error("Missing child node [items] at [{}]", resource.getPath());
        return;
    }
    Config itemsCfg = new Config(itemsRes);
    Iterator<Resource> listIt = itemsCfg.getItems("list/items");
    Map<String, String> providers = new HashMap<String, String>();
    while (listIt.hasNext()) {
        // get references providers for each groups
        Resource groupRes = listIt.next();
        Config groupItemsCfg = new Config(groupRes);
        Iterator<Resource> groupIt = groupItemsCfg.getItems();
        while (groupIt.hasNext()) {
            Config refTypeCfg = new Config(groupIt.next());
            String type = refTypeCfg.get("type", String.class);
            if (type != null) {
                providers.put(type, refTypeCfg.get("itemResourceType", "granite/ui/references/components/coral/type"));
            }
        }
    }

//Query
final ReferenceList referenceList = referenceService.getReferenceListForPageAndAssets(path,resourceResolver);
    for (final Reference reference : referenceList) {
        try {
            String resourceType = providers.get(reference.getType());
            if (resourceType != null && reference.getTarget() != null) {
                request.setAttribute("granite.ui.references.reference", reference);
%><sling:include path="<%= reference.getTarget().getPath() %>" resourceType="<%= resourceType %>" /><%
                request.removeAttribute("granite.ui.references.reference");
            }
        } catch (Exception e) {
            log.warn("Unable to render reference: " + reference.toString(), e);
        }
    }
%>
