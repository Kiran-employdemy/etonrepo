<%@include file="/libs/fd/af/components/guidesglobal.jsp" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.eaton.platform.core.models.ProductSelectorSubmitActionModel" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0"
%>
<cq:defineObjects/>
<sling:defineObjects/>
<%
 	ProductSelectorSubmitActionModel submitModel = slingRequest.adaptTo(ProductSelectorSubmitActionModel.class);
	if (submitModel != null) {
		submitModel.doSubmitAction(request, slingRequest);
	}
%>
