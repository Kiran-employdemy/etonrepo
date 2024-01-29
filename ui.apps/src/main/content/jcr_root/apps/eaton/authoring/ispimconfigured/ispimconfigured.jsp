<%@page session="false" import="com.adobe.granite.ui.components.Config,
                                    com.day.cq.wcm.api.Page,
                                    org.apache.sling.api.resource.Resource,
                                    com.adobe.granite.ui.components.rendercondition.RenderCondition,
                                    com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    // a condition to determine if the pimPagePath configured at the given page

    boolean isPimConfigured= false;
    Page targetPage = null;

    Config cfg = cmp.getConfig();
    String path = cmp.getExpressionHelper().getString(cfg.get("path", ""));;

    if (path != null) {
        Resource pageResource = slingRequest.getResourceResolver().resolve(path);
        if (pageResource != null) {
            Resource pageContentResource = pageResource.getChild("jcr:content");
            if (pageContentResource != null) {
                ValueMap properties =pageContentResource.adaptTo(ValueMap.class);
               String  pimPagePath = properties.get("pimPagePath", String.class);
                if (pimPagePath != null && pimPagePath != "") {
                    isPimConfigured = true;
                }
            }
        }
    }
    request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(isPimConfigured));
%>