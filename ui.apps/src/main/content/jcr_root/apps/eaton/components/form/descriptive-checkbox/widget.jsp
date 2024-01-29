<%@include file="/libs/fd/af/components/guidesglobal.jsp" %>
<div class="<%=GuideConstants.GUIDE_FIELD_CHECKBOXGROUP_ITEMS%>" style="${guide:encodeForHtmlAttr(guideField.styles,xssAPI)};${guide:encodeForHtmlAttr(guideField.widgetInlineStyles,xssAPI)}">
    <c:forEach items="${guideField.options}" var="option" varStatus="loopCounter">
        <div class="<%=GuideConstants.GUIDE_FIELD_CHECKBOX_ITEM%> <%=GuideConstants.AF_CHECKBOX_ITEM%>
            ${guideField.name} ${guideField.alignment} ${guide:encodeForHtmlAttr(guideField.cssClassName,xssAPI )}">
            <div class="<%= GuideConstants.GUIDE_FIELD_WIDGET%> left XfaCheckBox" data-id="${loopCounter.count}"  >
                <input type="checkbox" id="${guideField.id}${'_'}${loopCounter.count}${"_widget"}"
                       name="${guide:encodeForHtmlAttr(guideField.name,xssAPI)}" value="${guide:encodeForHtmlAttr(option.key,xssAPI)}" ${option.key == guideField.value? "checked" : ""}  />
            </div>
            <div class="<%=GuideConstants.GUIDE_WIDGET_LABEL%> right">
                <label>${guide:encodeForHtml(option.value,xssAPI)}</label>
            </div>
            <div class="checkbox-description">
                ${properties.itemDescriptions[loopCounter.index]}
            </div>
        </div>
    </c:forEach>
</div>