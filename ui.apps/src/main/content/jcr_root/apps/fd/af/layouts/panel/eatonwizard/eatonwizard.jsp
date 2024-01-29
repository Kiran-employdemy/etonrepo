<%@include file="/libs/fd/af/components/guidesglobal.jsp"%>
<%@ taglib prefix="wcmmode" uri="http://www.adobe.com/consulting/acs-aem-commons/wcmmode" %>

<wcmmode:edit>
  <div class="alert alert-warning">Eaton Wizard</div>
</wcmmode:edit>

<div id="${guidePanel.id}_layoutContainer" class="guideLayout row guideWizardLayout etn-wizard" style="position:relative;">

    <div id="#${guidePanel.id}_layoutPanelContainer" class="col-md-10 col-sm-10 PanelContainer afWizardPanel">
        <c:if test="${fn:length(guidePanel.description) > 0}">
            <div class="<%=GuideConstants.GUIDE_PANEL_DESCRIPTION%> guideWizardDescription">
                ${guide:encodeForHtml(guidePanel.description,xssAPI)}
                <cq:include script="/libs/fd/af/components/panel/longDescription.jsp"/>
            </div>
        </c:if>
        <cq:include script = "panelContainer.jsp"/>
    </div>
</div>