<%@ page import="com.adobe.aemds.guide.utils.GuideConstants,com.eaton.platform.core.util.CommonUtil" %>
<%@include file="/libs/fd/af/components/guidesglobal.jsp" %>

<%
String authoredLinkProp = resource.getValueMap().get("buttonLink", "");
String linkUrl = "";
if (! authoredLinkProp.equals("")) {
    linkUrl = CommonUtil.dotHtmlLink(authoredLinkProp, slingRequest.getResourceResolver()) + ".html";
}
pageContext.setAttribute("href", linkUrl);
%>

<%GuideNode guideField =(GuideNode) request.getAttribute("guideField");%>
<div class="<%= GuideConstants.GUIDE_FIELD_WIDGET%> <%= GuideConstants.GUIDE_FIELD_BUTTON_WIDGET%>" style="${guide:encodeForHtmlAttr(guideField.styles,xssAPI)}">
    <a class="${guideField.type} ${ guideField.type == 'buttonLink' ? 'button-default button-medium Button' : '' }"
       href="${ guideField.type == 'link' || guideField.type == 'buttonLink' ? href : 'javascript:void(0);' }"
       type="${guideField.type == 'submit' ? 'submit' : 'button'}"
       id="${guideid}${'_widget'}"
       name="${guide:encodeForHtmlAttr(guideField.name,xssAPI)}"
       style="${guide:encodeForHtmlAttr(guideField.widgetInlineStyles,xssAPI)}">
        <span class="iconButton-icon"></span>
        <span class="iconButton-label" data-guide-button-label="true" style="${guide:encodeForHtmlAttr(guideField.captionInlineStyles,xssAPI)}">${guide:encodeForHtml(guideField.title,xssAPI)}</span>
    </a>
</div>
