<%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<%@ taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@ include file="/libs/fd/af/components/guidesglobal.jsp" %>
<%@ page import="com.adobe.aemds.guide.taglibs.GuideELUtils,
                com.adobe.aemds.guide.utils.GuideUtils,
                com.adobe.aemds.guide.utils.GuideConstants,
                com.day.cq.wcm.api.Page,
                org.apache.sling.api.resource.Resource,
                com.day.cq.wcm.api.Template" %>
<cq:defineObjects/>
<%@ page session="false" %>
<%
    String guideContainerPath = GuideELUtils.getGuideContainerPath(slingRequest, resource);
    Resource guideContainerResource = resourceResolver.resolve(guideContainerPath);
    String lang = GuideELUtils.getLocale(slingRequest, guideContainerResource);
    // in case resource is within template node :
    Resource templateResource = resource.getParent().getParent();
    Template templateNode = templateResource.adaptTo(Template.class);
    boolean isTemplate = templateNode!=null ? templateNode.hasStructureSupport() : false;
    boolean isWebDocument = !isTemplate && guideContainerResource.isResourceType(GuideConstants.RT_WEB_DOCUMENT_CONTAINER);
    String documentName = "";
    if(isWebDocument){
        int rhsIndex = guideContainerPath.lastIndexOf("/channels/");
        if(rhsIndex>-1){
            String prefixPath = guideContainerPath.substring(0, rhsIndex);
            Page pageResource = resourceResolver.resolve(prefixPath).adaptTo(Page.class);
            if(pageResource!=null){
                documentName = pageResource.getTitle();
            }
        }
    }
%>
<!DOCTYPE html>
<cq:include script="html-tag.html"/>
<title><%= (isWebDocument?(documentName + " - "):"") + (currentPage.getTitle() == null ?
        xssAPI.encodeForHTML(currentPage.getName()) : xssAPI.encodeForHTML(currentPage.getTitle())) %></title>
<cq:include script="fallbackLibrary.jsp"/>
<%-- Adobe Target relevant clientlibs: --%>
<c:if test="<%=GuideUtils.isAdobeTargetConfigured(resource)%>">
    <sling:include path="contexthub" resourceType="granite/contexthub/components/contexthub"/>
    <%-- @TODO Remove cq.shared as this is a heavy library. Tracked via CQ-4219972 --%>
    <ui:includeClientLib categories="cq.shared"/>
    <cq:include script="/libs/cq/cloudserviceconfigs/components/servicelibs/servicelibs.jsp"/>
</c:if>
<cq:include script="afpagecontent.html"/>
</html>