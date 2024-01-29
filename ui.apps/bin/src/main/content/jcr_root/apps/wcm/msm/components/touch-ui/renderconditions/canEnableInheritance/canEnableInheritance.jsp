<%--
 * canEnableInheritance.jsp
 * --------------------------------------
 *
 * A condition to determine if the page at the given path is a conflicting page and inheritance with a blueprint can be established.
 * 
 * --------------------------------------
 * Author: Jaroslav Rassadin
--%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.rendercondition.RenderCondition,
                  com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition,
                  com.eaton.platform.core.services.msm.RolloutConflictInheritanceService" %><%

	final Config cfg = cmp.getConfig();
	String path = cmp.getExpressionHelper().getString(cfg.get("path", ""));
	path = path.replace(".html", "");
	boolean condition = false;

	if (!"".equals(path)) {
		final RolloutConflictInheritanceService inheritanceService = sling.getService(RolloutConflictInheritanceService.class);
		condition = inheritanceService.canEnableInheritanceForPage(path);
	}
	request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(condition));
%>
