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
				java.util.Calendar,
				org.apache.commons.lang3.StringUtils,
				java.lang.Integer,
				com.eaton.platform.core.vgselector.helpers.PostHelper,
			    com.eaton.platform.core.util.CommonUtil,
				com.eaton.platform.core.models.vgSelector.ConfigModel" %>
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

String hydraulicLinkage = request.getParameter("Hydraulic_Linkage");
if(null != hydraulicLinkage) {
    facetsValues.add(hydraulicLinkage);

}
String clutchSize = request.getParameter("Clutch_Size");
if(null != clutchSize) {
    facetsValues.add(clutchSize);
}
String damperSpringCount = request.getParameter("Damper_Spring_Count");
if(null != damperSpringCount) {
    facetsValues.add(damperSpringCount);
}
String torqueCapacity = request.getParameter("Torque_Capacity");
if(null != torqueCapacity) {
    facetsValues.add(torqueCapacity);

}
String portfolioRating = request.getParameter("Portfolio_Rating");
if(null != portfolioRating) {


    PostHelper postHelper = new PostHelper();
    ConfigModel siteResourceSlingModel = postHelper.getCloudConfig(resource, sling);
    
    int selectedYear = Integer.parseInt(portfolioRating);
    Calendar now = Calendar.getInstance();   // Gets the current date and time
    int currentYear = now.get(Calendar.YEAR);       // The current year

    if(currentYear - selectedYear < 3) {
			facetsValues.add(siteResourceSlingModel.getBest());
		} else if(currentYear - selectedYear >= 3 && currentYear - selectedYear < 10) {
			facetsValues.add(siteResourceSlingModel.getBetter());
			facetsValues.add(siteResourceSlingModel.getBest());
		} else if(currentYear - selectedYear >= 10) {
			facetsValues.add(siteResourceSlingModel.getGood());
			facetsValues.add(siteResourceSlingModel.getBest());
			facetsValues.add(siteResourceSlingModel.getBetter());
		}

}
String inputShaftType = request.getParameter("Input_Shaft_Type");
if(null != inputShaftType) {
    facetsValues.add(inputShaftType);
}
String preDamper = request.getParameter("Pre_Damper");
if(null != preDamper) {
    facetsValues.add(preDamper);
}
String torsionalSpringRate = request.getParameter("Torsional_Spring_Rate");
if(null != torsionalSpringRate) {
    facetsValues.add(torsionalSpringRate);
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
