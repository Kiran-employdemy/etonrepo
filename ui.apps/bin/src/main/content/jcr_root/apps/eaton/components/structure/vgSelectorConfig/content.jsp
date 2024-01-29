<%@page session="false" %>
<%@page contentType="text/html" pageEncoding="utf-8"
        import="java.util.ArrayList,
    			com.eaton.platform.core.vgselector.utils.VGSiteConfigUtil,
				com.eaton.platform.core.models.vgSelector.ConfigModel,
				java.util.List" %>

<%@include file="/libs/foundation/global.jsp" %>
<cq:includeClientLib categories="eaton.multifield"/>
<%
	ConfigModel formModelConfig = VGSiteConfigUtil.getVGSelectorConfigDetails(resource);
%>

<div>

    <button onclick="dialog.show()">Edit VG Selector Configuration</button>

	<c:set var="selectorToolType" value="<%=formModelConfig.getSelectorToolType()%>" />
    <li><div class="li-bullet"><strong>Selector Tool Type : </strong>${selectorToolType}</div></li>

    <c:set var="noOfReturnFacet" value="<%=formModelConfig.getReturnFacetsListSize()%>" />
    <li> 
    	 <c:if test="${noOfReturnFacet gt 0}">
            <c:forEach var="j" begin="0" end="${noOfReturnFacet-1}" step="1" varStatus="status1">
                  <c:set var="noOfReturnFacetIndex" value="${j}" scope="request" />

         <c:set var="returnFacetvalue" value="<%=formModelConfig.getReturnFacetsList().get((Integer)request.getAttribute("noOfReturnFacetIndex")).getReturnFacets()%>" />
                 <div class="li-bullet"><strong>Endeca Questions ID For Clutch : </strong>${returnFacetvalue}</div>
			</c:forEach>
       	 </c:if>
	</li> 

    <c:set var="noOfAdditionalFacet" value="<%=formModelConfig.getAdditional_facetsListSize()%>" />
    <li> 
    	 <c:if test="${noOfAdditionalFacet gt 0}">
            <c:forEach var="j" begin="0" end="${noOfAdditionalFacet-1}" step="1" varStatus="status1">
                  <c:set var="additionalFacetIndex" value="${j}" scope="request" />

         <c:set var="additionalFacetvalue" value="<%=formModelConfig.getAdditional_facetsList().get((Integer)request.getAttribute("additionalFacetIndex")).getFacets()%>" />
                 <div class="li-bullet"><strong>Additional Facets : </strong>${additionalFacetvalue}</div>
			</c:forEach>
       	 </c:if>
	</li>

    <c:set var="noOfSKUcardAttributes" value="<%=formModelConfig.getSkuCardAttributeListSize()%>" />
	<li> 
  		  <c:if test="${noOfSKUcardAttributes gt 0}">
            <c:forEach var="j" begin="0" end="${noOfSKUcardAttributes-1}" step="1" varStatus="status1">
                  <c:set var="skuCardAttributeIndex" value="${j}" scope="request" />

         <c:set var="attributevalue" value="<%=formModelConfig.getSkucardattributesList().get((Integer)request.getAttribute("skuCardAttributeIndex")).getSKUattribute()%>" />
               <div class="li-bullet"><strong>SKU Card Attributes : </strong>${attributevalue}</div>
			</c:forEach>
       	 </c:if>
	</li> 

<%-- This code can be used if the torque submit buttons - axle and vehicle displays separate result 
     <c:set var="noOfReturnFacetVehicle" value="<%=formModelConfig.getReturnFacetsVehicleListSize()%>" />
     <li> 
    	 <c:if test="${noOfReturnFacetVehicle gt 0}">
            <c:forEach var="j" begin="0" end="${noOfReturnFacetVehicle-1}" step="1" varStatus="status1">
                  <c:set var="noOfReturnFacetVehicleIndex" value="${j}" scope="request" />

         <c:set var="returnFacetVehicleValue" value="<%=formModelConfig.getReturnFacetsVehicleList().get((Integer)request.getAttribute("noOfReturnFacetVehicleIndex")).getReturnFacetsVehicle()%>" />
                 <div class="li-bullet"><strong>Endeca Questions ID For Vehicle : </strong>${returnFacetVehicleValue}</div>
			</c:forEach>
       	 </c:if>
	</li> 

     <c:set var="noOfReturnFacetAxle" value="<%=formModelConfig.getReturnFacetsAxleListSize()%>" />
     <li> 
    	 <c:if test="${noOfReturnFacetAxle gt 0}">
            <c:forEach var="j" begin="0" end="${noOfReturnFacetAxle-1}" step="1" varStatus="status1">
                  <c:set var="noOfReturnFacetAxleIndex" value="${j}" scope="request" />

         <c:set var="returnFacetvalueAxle" value="<%=formModelConfig.getReturnFacetsAxleList().get((Integer)request.getAttribute("noOfReturnFacetAxleIndex")).getReturnFacetsAxle()%>" />
                 <div class="li-bullet"><strong>Endeca Questions ID Axle: </strong>${returnFacetvalueAxle}</div>
			</c:forEach>
       	 </c:if>
	</li> 
--%>

    <c:set var="fallbackImage" value="<%=formModelConfig.getFallbackImage()%>" />
    <li><div class="li-bullet"><strong>Fallback Image : </strong>${fallbackImage}</div></li>


    <c:set var="longDescriptionCheckbox" value="<%=formModelConfig.getLongDescriptionCheckbox()%>" />
        	<li><div class="li-bullet"><strong>Display Long Description : </strong>${longDescriptionCheckbox}</div></li>

   		 <c:set var="facetValueCount" value="<%=formModelConfig.getFacetValueCount()%>" />
        	<li><div class="li-bullet"><strong>Facet Value Count : </strong>${facetValueCount}</div></li>

		 <c:set var="facetCount" value="<%=formModelConfig.getFacetCount()%>" />
        	<li><div class="li-bullet"><strong>Facet Count : </strong>${facetCount}</div></li>

		 <c:set var="expandedFacetCount" value="<%=formModelConfig.getExpandedFacetCount()%>" />
        	<li><div class="li-bullet"><strong>Expanded Facet Count : </strong>${expandedFacetCount}</div></li>

		 <c:set var="pageSize" value="<%=formModelConfig.getPageSize()%>" />
        	<li><div class="li-bullet"><strong>Number Of Records to Return : </strong>${pageSize}</div></li>

		 <c:set var="good" value="<%=formModelConfig.getGood()%>" />
        	<li><div class="li-bullet"><strong>Portfolio Rating for Good id : </strong>${good}</div></li>

		 <c:set var="better" value="<%=formModelConfig.getBetter()%>" />
        	<li><div class="li-bullet"><strong>Portfolio Rating for Better id : </strong>${better}</div></li>

		 <c:set var="best" value="<%=formModelConfig.getBest()%>" />
        	<li><div class="li-bullet"><strong>Portfolio Rating for Best id : </strong>${best}</div></li>

</div>