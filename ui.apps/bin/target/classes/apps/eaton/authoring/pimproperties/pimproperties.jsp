<%@page session="false" import="com.adobe.granite.ui.components.AttrBuilder,
                                    com.adobe.granite.ui.components.ComponentHelper.Options,
                                    com.adobe.granite.ui.components.Config,
                                    com.adobe.granite.ui.components.Tag,
                                    com.adobe.granite.ui.components.ValueMapResourceWrapper,
                                    com.day.cq.wcm.api.Page,
                                    com.day.cq.wcm.api.Template,
                                    org.apache.sling.api.resource.Resource,
                                    org.apache.sling.api.resource.ValueMap" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    Page targetPage = null;
   String pimPagePath = null;
   String propertiesSrc="";
    // get page object from suffix
    String pagePath = slingRequest.getRequestPathInfo().getSuffix();
    if (pagePath != null) {
        Resource pageResource = slingRequest.getResourceResolver().resolve(pagePath);


        if (pageResource != null) {
            targetPage = pageResource.adaptTo(Page.class);
            Resource pageContentResource = pageResource.getChild("jcr:content");
            if (pageContentResource != null) {
                ValueMap properties =pageContentResource.adaptTo(ValueMap.class);
                pimPagePath = properties.get("pimPagePath", String.class);
            }
        }
    }

    if (targetPage == null) return;

    if (pimPagePath != null && pimPagePath != "") {
        propertiesSrc = request.getContextPath() + "/mnt/overlay/commerce/gui/content/products/properties.html?item=" + xssAPI.encodeForHTMLAttr(pimPagePath) +"&context=pimpage";
    }

    Config cfg = cmp.getConfig();

    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    Resource wrapper = new ValueMapResourceWrapper(resource, cfg.get("wrappedResourceType", "granite/ui/components/coral/foundation/button"));

    ValueMap vm = wrapper.adaptTo(ValueMap.class);
    vm.put("text", cfg.get("text", ""));

    attrs.addClass("pim-properties-activator");
    attrs.addOther("path", propertiesSrc);

    cmp.include(wrapper, new Options().tag(tag));
%>