<%@include file="/libs/fd/af/components/guidesglobal.jsp"%>
<div class="<%= GuideConstants.GUIDE_FIELD_WIDGET%> dropDownList dynamic-dropdown"
     style="${guide:encodeForHtmlAttr(guideField.styles,xssAPI)}">
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
