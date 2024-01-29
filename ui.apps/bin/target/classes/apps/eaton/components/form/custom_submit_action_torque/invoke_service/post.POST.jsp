<%@include file="/libs/fd/af/components/guidesglobal.jsp" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.foundation.forms.FormsHelper,
             org.apache.sling.api.resource.ResourceUtil,
             org.apache.sling.api.resource.ValueMap,
			 java.util.Map,
			 java.util.List,
			 java.util.ArrayList,
			 java.util.HashMap,
			 java.util.Collections,
			 com.eaton.platform.core.util.CommonUtil,
			 org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.adobe.aemds.guide.utils.GuideSubmitUtils" %>
<%@taglib prefix="sling"
                uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@taglib prefix="cq"
                uri="http://www.day.com/taglibs/cq/1.0"
%>
<cq:defineObjects/>
<sling:defineObjects/>
<%

String CONST_COMMA = ",";
List<String> facetsValues = new ArrayList<>();

String year = request.getParameter("Year");
if(null != year) {
    facetsValues.add(year);
}
String make = request.getParameter("Make");
if(null != make) {
    facetsValues.add(make);
}
String model = request.getParameter("Model");
if(null != model) {
    facetsValues.add(model);
}

String difftype = request.getParameter("Differential_Type");
if(null != difftype) {
    facetsValues.add(difftype);
    
}
String gearRatio = request.getParameter("Gear_Ratio");
if(null != gearRatio) {
    facetsValues.add(gearRatio);
}

String configuredRedirectURL = CommonUtil.dotHtmlLink(StringUtils.remove(request.getParameter(":redirect"), ".html"), resourceResolver);

String facetURL = StringUtils.EMPTY;

for(String facet : facetsValues) {
    facetURL = facetURL + "$" + facet;
}

String finalRedirectURL = configuredRedirectURL + ".facets" + facetURL + ".html";
Map<String, String> redirectParameters = GuideSubmitUtils.getRedirectParameters(slingRequest);

if(redirectParameters==null) {
    redirectParameters = new HashMap<String, String>();
}

redirectParameters.remove("status");
redirectParameters.remove("owner");

//Temporary fix to blank out values
redirectParameters.put("status", "");
redirectParameters.put("owner", "");

GuideSubmitUtils.setRedirectParameters(slingRequest,redirectParameters);
GuideSubmitUtils.setRedirectUrl(slingRequest, finalRedirectURL);

%>
