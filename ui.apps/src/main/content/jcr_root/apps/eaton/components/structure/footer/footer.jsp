<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.WCMMode,
    com.eaton.platform.core.util.CommonUtil" %>
<%@page session="false" %>


<%
String homePagePath = CommonUtil.getHomePagePath(currentPage);
Resource res = resourceResolver.getResource(homePagePath+"/jcr:content/root/footer");
String footerPath=null;
if(null!=res && res.getValueMap().get("reference")!=null){
footerPath= res.getValueMap().get("reference")+"/jcr:content/footerReference";
}

WCMMode wcmMode = WCMMode.fromRequest(request);
if(wcmMode==WCMMode.EDIT){ %>
 <div class="alert alert-warning">Footer</div>
<%
}if(null!=footerPath){
 %>

<sling:include path="<%= footerPath %>" resourceType="/apps/eaton/components/structure/footerReference" />

<%}%>