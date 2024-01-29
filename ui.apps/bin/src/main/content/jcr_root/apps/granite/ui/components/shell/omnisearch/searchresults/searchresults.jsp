<%--
  ADOBE CONFIDENTIAL
  ___________________

  Copyright 2015 Adobe
  All Rights Reserved.

  NOTICE: All information contained herein is, and remains
  the property of Adobe and its suppliers, if any. The intellectual
  and technical concepts contained herein are proprietary to Adobe
  and its suppliers and are protected by all applicable intellectual
  property laws, including trade secret and copyright laws.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe.
--%><%
%><%@ include file="/libs/granite/ui/global.jsp" %><%
%><%@ page import="java.io.UnsupportedEncodingException,
                  java.net.URLDecoder,
                  java.net.URLEncoder,
                  java.util.ArrayList,
                  java.util.Iterator,
                  java.util.List,
                  java.util.Map,
                  java.util.Map.Entry,
                  org.apache.commons.lang3.StringUtils,
                  org.apache.jackrabbit.util.Text,
                  org.apache.sling.api.SlingHttpServletRequest,
                  org.apache.sling.resourcemerger.api.ResourceMergerService,
                  com.adobe.granite.omnisearch.api.core.OmniSearchService,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ExpressionHelper,
                  com.adobe.granite.ui.components.Tag" %><%

final Config cfg = cmp.getConfig();
final ExpressionHelper ex = cmp.getExpressionHelper();
final OmniSearchService searchService = sling.getService(OmniSearchService.class);
final ResourceMergerService resourceMerger = sling.getService(ResourceMergerService.class);

final String targetCollection = cfg.get("targetCollection", String.class);
final String modeGroup = cfg.get("modeGroup", String.class);

// in case the location is set, we need to render the facets accordingly
final String location = request.getParameter("location");
final List<String> clientLibs = new ArrayList<String>();

Resource searchPredicates = null;

// by default we assume multiresults until a valid location has been confirmed
Resource searchResults = resource.getChild("multiresults");

if (!StringUtils.isBlank(location)) {
    searchPredicates = searchService.getModuleConfiguration(resourceResolver, location);
    if (location.equals("product")) {
        searchPredicates = resourceResolver.getResource("/apps/granite/omnisearch/content/metadata/product");
    }else {
        searchPredicates = searchService.getModuleConfiguration(resourceResolver, location);
    }

    if (searchPredicates != null) {
        if (resourceMerger != null) {
            Resource tempSearchPredicates = resourceMerger.getMergedResource(searchPredicates);
            if (tempSearchPredicates != null) {
                searchPredicates = tempSearchPredicates;
            }
        }

        searchResults = resource.getChild("singleresults");

        ValueMap vm = searchPredicates.getValueMap();

        for (String s : vm.get("clientlibs", new String[0])) {
            clientLibs.add(s);
        }
    }
} else {
    Map<String, Resource> modules = searchService.getModules(resourceResolver);

    for (Entry<String, Resource> entry : modules.entrySet()) {
        Resource res = entry.getValue();

        if (resourceMerger != null) {
            Resource tempRes = resourceMerger.getMergedResource(res);
            if (tempRes != null) {
                res = tempRes;
            }
        }

        ValueMap vm = res.getValueMap();

        for (String s : vm.get("clientlibs", new String[0])) {
            clientLibs.add(s);
        }
    }
}

// items based on the search results mode
final Resource rails = searchResults.getChild("rails");

final List<Resource> viewCache = new ArrayList<Resource>();

final String consoleId = "shell.omnisearch.results.layoutId";

final String targetViewName = getTargetViewName(slingRequest, consoleId);
Resource currentView = null;

int i = 0;
for (Iterator<Resource> it = searchResults.getChild("views").listChildren(); it.hasNext();) {
    Resource item = it.next();

    if (i == 0 || item.getName().equals(targetViewName)) {
        currentView = item;
    }

    viewCache.add(item);
    i++;
}

Resource searchRailRes = null;

if (searchPredicates != null) {
    ValueMap properties = searchPredicates.getValueMap();
    searchRailRes = resourceResolver.getResource(properties.get("searchRailPath", String.class));

    AttrBuilder selectionAttrs = new AttrBuilder(request, xssAPI);
    selectionAttrs.add("id", "granite-shell-search-result-selectionbar");
    selectionAttrs.addClass("granite-collection-selectionbar");
    selectionAttrs.addClass("foundation-mode-switcher");
    selectionAttrs.add("data-foundation-mode-switcher-group", modeGroup);
    selectionAttrs.addClass("foundation-collection-actionbar");
    selectionAttrs.add("data-foundation-collection-actionbar-target", targetCollection);

    %><div <%= selectionAttrs %>>
        <div class="foundation-mode-switcher-item" data-foundation-mode-switcher-item-mode="selection">
            <coral-actionbar class="betty-ActionBar betty-ActionBar--large">
                <coral-actionbar-primary><%
                    String actionsPath = properties.get("actionsPath", "actions/selection");
    				log.error("actionspath"+actionsPath);
                    Resource selection = searchPredicates.getChild(actionsPath);
                    if (selection != null) {
                        for (Iterator<Resource> it = selection.listChildren(); it.hasNext();) {
                            Resource item = it.next();

                            if (!cmp.getRenderCondition(item, true).check()) {
                                continue;
                            }
                            %><coral-actionbar-item><%
                                AttrBuilder selectionItemAttrs = new AttrBuilder(request, xssAPI);
                                selectionItemAttrs.addClass("betty-ActionBar-item");
                                cmp.include(item, new Tag(selectionItemAttrs));
                            %></coral-actionbar-item><%
                        }
                    }
                %></coral-actionbar-primary>
                <coral-actionbar-secondary>
                    <coral-actionbar-item><%
                        AttrBuilder deselectAttrs = new AttrBuilder(request, xssAPI);
                        deselectAttrs.add("is", "coral-button");
                        deselectAttrs.add("variant", "quiet");
                        deselectAttrs.add("type", "button");
                        deselectAttrs.add("icon", "close");
                        deselectAttrs.add("iconposition", "right");
                        deselectAttrs.add("iconsize", "S");
                        deselectAttrs.addClass("betty-ActionBar-item");
                        deselectAttrs.addClass("granite-collection-deselect");
                        deselectAttrs.add("data-granite-collection-deselect-target", targetCollection);
                        deselectAttrs.add("data-foundation-command", "escape");

                        AttrBuilder counterAttrs = new AttrBuilder(request, xssAPI);
                        counterAttrs.addClass("foundation-admin-selectionstatus");
                        counterAttrs.add("data-foundation-admin-selectionstatus-template", i18n.get("{0} selected", null, "{{count}}"));
                        counterAttrs.add("data-foundation-admin-selectionstatus-target", targetCollection);

                        %><button <%= deselectAttrs %>><span <%= counterAttrs %>></span></button>
                    </coral-actionbar-item>
                </coral-actionbar-secondary>
            </coral-actionbar>
        </div>
    </div><%
}

if (!clientLibs.isEmpty()) {
    %><ui:includeClientLib categories="<%= StringUtils.join(clientLibs, ",") %>"/><%
}

if (searchRailRes != null || rails != null || viewCache.size() > 1) {
    %><betty-titlebar id="granite-omnisearch-result-actionbar">
        <betty-titlebar-title>
            <span class="granite-title" role="heading" aria-level="2"><%= xssAPI.encodeForHTML(i18n.get("Search Results")) %></span>
        </betty-titlebar-title>
        <betty-titlebar-primary><%

            if (searchRailRes != null) {
                %><coral-cyclebutton id="granite-omnisearch-result-rail-toggle" class="granite-toggleable-control" icon="railLeft" displaymode="icontext">
                    <coral-cyclebutton-item
                        displaymode="icon"
                        data-granite-toggleable-control-target="#granite-omnisearch-result-rail"
                        data-granite-toggleable-control-action="hide"><%= xssAPI.encodeForHTML(i18n.get("Content Only")) %></coral-cyclebutton-item>
                    <coral-cyclebutton-item
                        data-granite-toggleable-control-target=".granite-omnisearch-predicates"
                        data-granite-toggleable-control-action="show"><%= xssAPI.encodeForHTML(i18n.get("Filters")) %></coral-cyclebutton-item>
                </coral-cyclebutton><%
            }
        %></betty-titlebar-primary>
        <betty-titlebar-secondary><%
            AttrBuilder selectAllAttrs = new AttrBuilder(request, xssAPI);
            selectAllAttrs.addClass("foundation-collection-selectall");
            selectAllAttrs.addClass("coral-Button--graniteActionBar");
            selectAllAttrs.add("data-foundation-collection-selectall-target", targetCollection);
            selectAllAttrs.add("data-foundation-command", "ctrl+a");
            selectAllAttrs.add("trackingfeature", "aem:search");
            selectAllAttrs.add("trackingelement", "selectall");
            %><button is="coral-button" type="button" variant="quiet" icon="selectAll" <%= selectAllAttrs %>><%= xssAPI.encodeForHTML(i18n.get("Select All")) %></button>
            <granite-pagingstatus class="granite-collection-pagingstatus" data-granite-collection-pagingstatus-target="#granite-omnisearch-result"></granite-pagingstatus><%
            if (viewCache.size() > 1) {
                AttrBuilder switcherAttrs = new AttrBuilder(request, xssAPI);
                switcherAttrs.addClass("granite-collection-switcher");
                switcherAttrs.add("data-granite-collection-switcher-target", targetCollection);

                %><coral-cyclebutton <%= switcherAttrs %>><%
                    for (Resource item : viewCache) {
                        Config itemCfg = new Config(item);

                        String src = ex.getString(itemCfg.get("src", String.class));

                        AttrBuilder itemAttrs = new AttrBuilder(request, xssAPI);
                        itemAttrs.add("data-granite-collection-switcher-src", handleURITemplate(src, request));
                        itemAttrs.add("icon", itemCfg.get("icon", String.class));
                        itemAttrs.addSelected(item.getName().equals(currentView.getName()));

                        %><coral-cyclebutton-item <%= itemAttrs %>><%= outVar(xssAPI, i18n, itemCfg.get("jcr:title", String.class)) %></coral-cyclebutton-item><%
                    }
                %></coral-cyclebutton><%
            }
        %></betty-titlebar-secondary>
    </betty-titlebar><%
}

%><div id="granite-omnisearch-result-content" class="foundation-layout-panel-content"><%
    if (currentView != null) {
        %><sling:include resource="<%= currentView %>" /><%
    }
%></div><%!

private String handleURITemplate(String src, HttpServletRequest request) {
    if (src != null && src.startsWith("/")) {
        return request.getContextPath() + src;
    }
    return src;
}

private String getTargetViewName(SlingHttpServletRequest request, String consoleId) {
    try {
        consoleId = URLEncoder.encode(consoleId, "utf-8");
        Cookie cookie = request.getCookie(consoleId);

        if (cookie == null) {
            return null;
        }

        return URLDecoder.decode(cookie.getValue(), "utf-8");
    } catch (UnsupportedEncodingException impossible) {
        throw new RuntimeException(impossible);
    }
}
%>

