<%@include file="/libs/foundation/global.jsp"%>
<%@ page import="com.day.cq.wcm.api.WCMMode,
   com.eaton.platform.core.util.CommonUtil" %>
<%@page session="false" %>


<%
String headerPath=null;
String homePagePath = CommonUtil.getHomePagePath(currentPage);

Resource res = resourceResolver.getResource(homePagePath+"/jcr:content/root/header");
if(null!=res && res.getValueMap().get("reference")!=null){
headerPath= res.getValueMap().get("reference")+"/jcr:content/headerReference";
}

WCMMode wcmMode = WCMMode.fromRequest(request);
if(wcmMode==WCMMode.EDIT){ %>
 <div class="alert alert-warning">Header</div>
<%
}if(null!=headerPath){
 %>
<header>
<sling:include path="<%= headerPath %>" resourceType="/apps/eaton/components/structure/headerReference" />
</header>

<%}%>