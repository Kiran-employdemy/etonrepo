<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false"
        import="com.adobe.granite.ui.components.Config,
          com.adobe.granite.ui.components.rendercondition.RenderCondition,
          com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition,
          com.eaton.platform.core.util.RenderConditionLanguagePageUtil,
          org.apache.sling.api.resource.Resource,
          com.adobe.granite.xss.XSSAPI,
          com.day.cq.i18n.I18n"%>
<sling:defineObjects/>
<%
boolean langPage = RenderConditionLanguagePageUtil.isHREFLangVisible(slingRequest, request);
request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(langPage));
%>