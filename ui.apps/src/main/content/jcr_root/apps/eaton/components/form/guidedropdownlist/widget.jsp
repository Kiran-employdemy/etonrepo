<%------------------------------------------------------------------------
 ~
 ~ ADOBE CONFIDENTIAL
 ~ __________________
 ~
 ~  Copyright 2014 Adobe Systems Incorporated
 ~  All Rights Reserved.
 ~
 ~ NOTICE:  All information contained herein is, and remains
 ~ the property of Adobe Systems Incorporated and its suppliers,
 ~ if any.  The intellectual and technical concepts contained
 ~ herein are proprietary to Adobe Systems Incorporated and its
 ~ suppliers and may be covered by U.S. and Foreign Patents,
 ~ patents in process, and are protected by trade secret or copyright law.
 ~ Dissemination of this information or reproduction of this material
 ~ is strictly forbidden unless prior written permission is obtained
 ~ from Adobe Systems Incorporated.
 --------------------------------------------------------------------------%>
 
<%--
  DropDown List Component
--%>
<%@include file="/libs/fd/af/components/guidesglobal.jsp"%>
<%@ page import="org.apache.sling.api.resource.Resource, org.apache.commons.lang.StringUtils, org.apache.sling.api.resource.ValueMap" %>
    <%-- todo: In case of repeatable panels, please change this logic at view layer --%>

    <%
    
		Resource formJcrResource = resourceResolver.getResource(resource.getPath());
		ValueMap currentPageProperties = null;
		if(null != formJcrResource) {
			currentPageProperties = formJcrResource.getValueMap();
		}

		String infoType = StringUtils.EMPTY;
		if (null != currentPageProperties && null != "infoType") {
			if (currentPageProperties.containsKey("infoType")) {
				infoType = currentPageProperties.get("infoType").toString();
			}
		}

	%>
    <div  class="<%= GuideConstants.GUIDE_FIELD_WIDGET%> dropDownList" style="${guide:encodeForHtmlAttr(guideField.styles,xssAPI)}" id="${guide:encodeForHtmlAttr(guideField.name,xssAPI)}" data-info-type="<%= infoType %>">

    <select id="${guideid}${"_widget"}" name="${guide:encodeForHtmlAttr(guideField.name,xssAPI)}" ${guideField.isMultiSelect ? "multiple=\"multiple\"" : ""} style="${guide:encodeForHtmlAttr(guideField.widgetInlineStyles,xssAPI)}">
        <c:if test="${guideField.placeholderText != null && fn:length(guideField.placeholderText) > 0}">
            <option value="" disabled selected>${guide:encodeForHtmlAttr(guideField.placeholderText,xssAPI)} </option>
        </c:if>
        <c:forEach items="${guideField.options}" var="option" varStatus="loopCounter">
            <c:set var="key" value="${guide:encodeForHtmlAttr(option.key,xssAPI)}" />
            <c:choose>
                <%-- Adds a optgroup if key contains the prefix for optgroup--%>
                <c:when test="${guide:isKeyAnOptGroup(key)}" >
                    <%-- Optgroup closing tag won't occur for first optgroup, hence the check--%>
                    <c:if test="${!guideField.isFirstOptGroup}">
                        </optgroup>
                    </c:if>
                    <optgroup label="${guide:encodeForHtmlAttr(option.value,xssAPI)}">
                </c:when>
                <%-- Adds a option otherwise.--%>
                <c:otherwise>
                    <option value="${key}"> ${guide:encodeForHtml(option.value,xssAPI)} </option>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <%-- Optgroup closing tag for last optgroup.
        This check is needed so that optgroup closing tag does not occur
        for drop down list without any optgroup and comprised purely of options.--%>
        <c:if test="${!guideField.isFirstOptGroup}">
            </optgroup>
        </c:if>
    </select>
    <%-- End of Widget Div --%>
</div>
