<%@include file="/libs/fd/af/components/guidesglobal.jsp"%>
<%
String name = properties.get("name", "");
String txnmyAttributeValue = properties.get("txnmyAttributeValue", "");
String jcrTitle = properties.get("jcr:title", "");
String tooltipTitle = properties.get("tooltipTitle", "");
String tooltipDescription = properties.get("tooltipDescription", "");
String tooltipImage = properties.get("tooltipImage", "");
String tooltipIcon = properties.get("tooltipIcon", "");
String closeIcon = properties.get("closeIcon", "");
String closeTitle = properties.get("closeTitle", "");
String tooltipId = jcrTitle.replaceAll("[+^?$#%&*()!@=';<>/* ]*", "");
%>


<div id="<%= tooltipId %>" class="modal fade tooltipmodal" role="dialog">
  <div class="modal-dialog">
      <!-- Modal content-->
      <div class="modal-content">
          <div class="tooltipmodal__body modal-body">
              <a class="tooltipmodal__close" href="#" role="button" aria-label="Close feedback form" id="close-modal" data-dismiss ="modal" tabindex="0" ><img src="<%= closeIcon %>" title="<%= closeTitle %>"/></a>
            <div class="tooltipmodal__title"> <h2><%= tooltipTitle %></h2></div>
            <div class="tooltipmodal__image">
              <img src="<%= tooltipImage %>" class="img-responsive">
            </div>
            <div class="tooltipmodal__text"><%= tooltipDescription %></div>
          </div>
      </div>
  </div>
</div>
<c:set var="hideRadioBtn" value="${properties.hideRadio}"/>
<c:set var="displayMode" value="${properties.display}"/>
<c:set var="toolTipCheck" value="${properties.tooltipRequiredCheckbox}"/>

<c:choose>
  <c:when test="${displayMode == 'radio'}">
    <div class="card__header__radio__container nopreference">
      <div class="card__header__radio__content">
        <div class="card__header__radio__btn"></div>
        <div class="card__header__radio__btn-inner"></div>
      </div>
      <div class="card__header__radio__pretext">
         <h5 class="eaton-title__headline"><%= jcrTitle %></h5>
      </div>
    </div>
  </c:when>
  <c:otherwise>
    <div class="card">
      <div class="card__header">
        <div class="card__header__radio__container">
          <div class="card__header__radio__wrapper">
            <c:if test="${hideRadioBtn != false}">
              <div class="card__header__radio__btn"></div>
              <div class="card__header__radio__btn-inner" data-parentGroupID="<%= name %>"  data-facetId="<%= txnmyAttributeValue %>"></div>
            </c:if>
          </div>
        </div>

        <div class="card__header__title__container">
          <h3 class="card__header__title"><%= jcrTitle %>
           <c:if test="${toolTipCheck == true}">
            <div class="tooltip-info" data-toggle="modal" data-target="#<%= tooltipId %>">   <img src="<%= tooltipIcon %>" id="toolTipModal"></div>
           </c:if>
          </h3>
        </div>
      </div>

      <div class="card__body">
        <cq:include path="form-parsys" resourceType="wcm/foundation/components/responsivegrid"/>
      </div>
    </div>
   
  </c:otherwise>
</c:choose>