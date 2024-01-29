<%@page session="false" %>
<%@page contentType="text/html" pageEncoding="utf-8"
        import="java.util.ArrayList"%>
    			<%@page
        import="com.eaton.platform.core.util.SiteConfigUtil"%>
				<%@page
        import="com.eaton.platform.core.models.SiteConfigModel"%>
				<%@page
        import="com.eaton.platform.core.models.GlobalAttributeModel"%>
				<%@page
        import="java.util.List"%>

<%@include file="/libs/foundation/global.jsp" %>
<cq:includeClientLib categories="eaton.multifield"/>
<%
SiteConfigModel siteConfigModel = SiteConfigUtil.getSiteConfigDetails(resource);



%>
<div>

    <ul style="float: left; margin: 0px;">
        <h2 style="margin:0;padding-left:0;">Site Configuration Details</h2>
            <p></p>
		<c:set var="disableDatasheet" value="<%=siteConfigModel.getDisableDatasheet()%>" />
            <li><div class="li-bullet"><strong>Disable Excel Datasheet : </strong>${disableDatasheet}</div></li>
 		<c:set var="overridePDHData" value="<%=siteConfigModel.getOverridePDHData()%>" />
        	<li><div class="li-bullet"><strong>Override PDH Data : </strong>${overridePDHData}</div></li>
       	<c:set var="unitedStatesDateFormat" value="<%=siteConfigModel.getUnitedStatesDateFormat()%>" />
        	<li><div class="li-bullet"><strong>United States Date Format : </strong>${unitedStatesDateFormat}</div></li>
 		<c:set var="pageSize" value="<%=siteConfigModel.getPageSize()%>" />
        	<li><div class="li-bullet"><strong>Page Size : </strong>${pageSize}</div></li>

        <c:set var="facetCount" value="<%=siteConfigModel.getFacetCount()%>" />
        	<li><div class="li-bullet"><strong>Facet Count : </strong>${facetCount}</div></li>

		 <c:set var="noOfDays" value="<%=siteConfigModel.getNoOfDays()%>" />
        	<li><div class="li-bullet"><strong>Number Of Days : </strong>${noOfDays}</div></li>

        <c:set var="expandedFacetCount" value="<%=siteConfigModel.getExpandedFacetCount()%>" />
        	<li><div class="li-bullet"><strong>Expanded Facet Count : </strong>${expandedFacetCount}</div></li>

        <c:set var="facetValueCount" value="<%=siteConfigModel.getFacetValueCount()%>" />
        	<li><div class="li-bullet"><strong>Facet Value Count : </strong>${facetValueCount}</div></li>

        <c:set var="defaultSortOrder" value="<%=siteConfigModel.getDefaultSortOrder()%>" />
        	<li><div class="li-bullet"><strong>Default Sort Order : </strong>${defaultSortOrder}</div></li>

        <c:set var="dirType" value="<%=siteConfigModel.getDirType()%>" />
                	<li><div class="li-bullet"><strong>Site Language Direction : </strong>${dirType}</div></li>

		<c:set var="noOfGlobalAttributes" value="<%=siteConfigModel.getGlobalAttributeListSize()%>" />
        <li></li>


         <strong>Global Attributes : </strong>

         <c:set var="attributegroupname" value="<%=siteConfigModel.getGlobalAttributeGroupName()%>" />
        	<div class="li-bullet"><strong> Attribute Group Name: </strong>${attributegroupname}</div>

        <c:if test="${noOfGlobalAttributes gt 0}">
            <c:forEach var="j" begin="0" end="${noOfGlobalAttributes-1}" step="1" varStatus="status1">
                  <c:set var="globalAttributeIndex" value="${j}" scope="request" />

                <c:set var="attributevalue" value="<%=siteConfigModel.getGlobalAttributesList().get((Integer)request.getAttribute("globalAttributeIndex")).getAttributevalue()%>" />


                <div class="li-bullet"><strong>Attribute value : </strong>${attributevalue}</div>

		</c:forEach>
       	 </c:if>


 		<c:set var="countUpSell" value="<%=siteConfigModel.getCountUpSell()%>" />
        	<li><div class="li-bullet"><strong>Max count for Up-Sell: </strong>${countUpSell}</div></li>

         <c:set var="	countImages" value="<%=siteConfigModel.getCountImages()%>" />
        	<li><div class="li-bullet"><strong>Max count for Images in Gallery: </strong>${countImages}</div></li>

         <c:set var="countCorouselItem" value="<%=siteConfigModel.getCountCorouselItem()%>" />
        	<li><div class="li-bullet"><strong>Carousel Maximum Number of Items : </strong>${countCorouselItem}</div></li>

         <c:set var="skuFallBackImage" value="<%=siteConfigModel.getSkuFallBackImage()%>" />
        	<li><div class="li-bullet"><strong>SKU Fallback Image : </strong>${	skuFallBackImage}</div></li>

         <c:set var="defaultLinkCTA" value="<%=siteConfigModel.getDefaultLinkCTA()%>" />
        	<li><div class="li-bullet"><strong>Default link for Primary CTA Button and How to Buy Option for Orphan SKUs : </strong>${defaultLinkCTA}</div></li>

        <c:set var="defaultLinkHTB" value="<%=siteConfigModel.getDefaultLinkHTB()%>" />
        	<li><div class="li-bullet"><strong>Default link for How to Buy Option for Orphan SKUs : </strong>${defaultLinkHTB}</div></li>

        <c:set var="pfOverviewDesc" value="<%=siteConfigModel.getPfOverviewDesc()%>" />
        <li><div class="li-bullet"><strong>Product Family Pages SEO Meta Description: </strong>${pfOverviewDesc}</div></li>

              <c:set var="pfOverviewTitle" value="<%=siteConfigModel.getPfOverviewTitle()%>" />
        	<li><div class="li-bullet"><strong>Product Family SEO Meta Overview Title: </strong>${pfOverviewTitle}</div></li>

         <c:set var="pfModelsTitle" value="<%=siteConfigModel.getPfModelsTitle()%>" />
        	<li><div class="li-bullet"><strong>Product Family SEO Meta Models Title: </strong>${pfModelsTitle}</div></li>

         <c:set var="pfResourcesTitle" value="<%=siteConfigModel.getPfResourcesTitle()%>" />
        	<li><div class="li-bullet"><strong>Product Family SEO Meta Resources Title: </strong>${pfResourcesTitle}</div></li>



        <c:set var="skuOverviewDesc" value="<%=siteConfigModel.getSkuOverviewDesc()%>" />
        <li><div class="li-bullet"><strong>SKU Pages SEO Meta Description: </strong>${skuOverviewDesc}</div></li>

 <c:set var="skuOverviewTitle" value="<%=siteConfigModel.getSkuOverviewTitle()%>" />
        	<li><div class="li-bullet"><strong>SKU Page SEO Meta Overview Title: </strong>${skuOverviewTitle}</div></li>

         <c:set var="skuSpecificationsTitle" value="<%=siteConfigModel.getSkuSpecificationsTitle()%>" />
        	<li><div class="li-bullet"><strong>SKU Page SEO Meta Specifications Title: </strong>${skuSpecificationsTitle}</div></li>

         <c:set var="skuResourcesTitle" value="<%=siteConfigModel.getSkuResourcesTitle()%>" />
        	<li><div class="li-bullet"><strong>SKU Page SEO Meta Resources Title: </strong>${skuResourcesTitle}</div></li>

         <strong>Support Info Country List : </strong>

		<c:set var="noOfSupportInfoCountries" value="<%=siteConfigModel.getSupprotInfoCountryListSize()%>" />

 			<c:if test="${noOfSupportInfoCountries gt 0}">
 		           <c:forEach var="j" begin="0" end="${noOfSupportInfoCountries-1}" step="1" varStatus="status1">

				 <c:set var="supportInfoCountriesIndex" value="${j}" scope="request" />
                 <c:set var="countryname" value="<%=siteConfigModel.getSupportInfoContriesList().get((Integer)request.getAttribute("supportInfoCountriesIndex")).getCountryName()%>" />
                <c:set var="countryvalue" value="<%=siteConfigModel.getSupportInfoContriesList().get((Integer)request.getAttribute("supportInfoCountriesIndex")).getCountryValue()%>" />

                 <div class="li-bullet"><strong>Country Name : </strong>${countryname}</div>
                <div class="li-bullet"><strong>Country Code : </strong>${countryvalue}</div>

		</c:forEach>
       	 </c:if>

 <li></li>

         <strong>Resource List Configuration for SKU Page : </strong>
         <c:set var="noOfDocumentGroupForSKU" value="<%=siteConfigModel.getResourceCategoryListSize()%>" />
         <div class="li-bullet"><strong>SKU number : </strong>${noOfDocumentGroupForSKU}</div>
         	<c:if test="${noOfDocumentGroupForSKU gt 0}">
 		           <c:forEach var="j" begin="0" end="${noOfDocumentGroupForSKU-1}" step="1" varStatus="status1">

				 <c:set var="documentGroupForSKUIndex" value="${j}" scope="request" />
                <c:set var="groupname" value="<%=siteConfigModel.getResourceCategoryList().get((Integer)request.getAttribute("documentGroupForSKUIndex")).getResourceCategoryID()%>" />
                 <div class="li-bullet"><strong>Group Name : </strong>${groupname}</div>
               <c:set var="resourceValue" value="<%=siteConfigModel.getResourceCategoryList().get((Integer)request.getAttribute("documentGroupForSKUIndex")).getPDHGroupIDs()%>" />
               <div class="li-bullet"><strong>Resource Name : </strong>${resourceValue}</div>
		</c:forEach>
       	 </c:if>

 <li></li>
           <c:set var="searchresultURL" value="<%=siteConfigModel.getSearchResultsURL()%>" />
        	<div class="li-bullet"><strong>Search ResultPage URL : </strong>${searchresultURL}</div>
        	<c:set var="crossRefSearchResultsURL" value="<%=siteConfigModel.getCrossRefSearchResultsURL()%>" />
        	<div class="li-bullet"><strong>Cross Reference Search Results Page URL : </strong>${crossRefSearchResultsURL}</div>
           <c:set var="skuPageURL" value="<%=siteConfigModel.getSkuPageURL()%>" />
        	<div class="li-bullet"><strong>SKU Page URL : </strong>${skuPageURL}</div>
<li></li>
         <strong>Site Search Facet Group : </strong>
       <c:set var="noOfFacetsGroups" value="<%=siteConfigModel.getSiteSearchFacetGroupsListSize()%>" />
       <div class="li-bullet"><strong>Facets Groups : </strong>${noOfFacetsGroups}</div>
       <c:if test="${noOfFacetsGroups gt 0}">
            <c:forEach var="j" begin="0" end="${noOfFacetsGroups-1}" step="1" varStatus="status1">
                  <c:set var="facetsGroupsIndex" value="${j}" scope="request" />

                <c:set var="facetGroup" value="<%=siteConfigModel.getSiteSearchFacetGroups().get((Integer)request.getAttribute("facetsGroupsIndex")).getSiteSearchFacetGroup()%>" />
                <c:set var="showAsGrid" value="<%=siteConfigModel.getSiteSearchFacetGroups().get((Integer)request.getAttribute("facetsGroupsIndex")).isGridFacet()%>" />
                <c:set var="facetSearchEnabled" value="<%=siteConfigModel.getSiteSearchFacetGroups().get((Integer)request.getAttribute("facetsGroupsIndex")).isFacetSearchEnabled()%>" />
                <div class="li-bullet"><strong>Facet Groups : </strong>${facetGroup}, <strong>Show As Grid: </strong>${showAsGrid} , <strong>Facet Search Enabled : </strong>${facetSearchEnabled}</div>

		</c:forEach>
       	 </c:if>

       	  <li></li>
       	            <c:set var="contentHubDefaultIconPath" value="<%=siteConfigModel.getContenthubDefaultIcon()%>" />
                 	<div class="li-bullet"><strong>Content Hub default icon path : </strong>${contentHubDefaultIconPath}</div>
         <li></li>
		 
		  <li></li>
       	            <c:set var="faviconIconPath" value="<%=siteConfigModel.getFavionIcon()%>" />
                 	<div class="li-bullet"><strong>Favicon icon path : </strong>${faviconIconPath}</div>
         <li></li>
                    <c:set var="bulkDownloadPackageSize" value="<%=siteConfigModel.getBulkDownloadPackageSize()%>" />
                    <div class="li-bullet"><strong>Bulk Download Max Allowed Size: </strong>${bulkDownloadPackageSize}</div>
                    <c:set var="bulkDownloadCacheDuration" value="<%=siteConfigModel.getBulkDownloadCacheDuration()%>" />
                    <div class="li-bullet"><strong>Bulk Download cache Duration : </strong>${bulkDownloadCacheDuration}</div>
                    <c:set var="companyName" value="<%=siteConfigModel.getCompanyName()%>" />
                    <div class="li-bullet"><strong>Company Name : </strong>${companyName}</div>
                    <c:set var="seoScriptPath" value="<%=siteConfigModel.getSeoScriptPath()%>" />
                    <div class="li-bullet"><strong>Path to SEO script resource: </strong>${seoScriptPath}</div>
         <li></li>
              
            <c:set var="noOfSiteVerificationCodes" value="<%=siteConfigModel.getSiteVerificationCodeModelListSize()%>" />
            <c:if test="${noOfSiteVerificationCodes gt 0}">
                <strong>Site Verification Code List: </strong>
                <c:forEach var="siteVerificationCodeIndexValue" begin="0" end="${noOfSiteVerificationCodes-1}" step="1" varStatus="siteVerificationCodeIndexStatus">
                
                    <c:set var="siteVerificationCodeIndex" value="${siteVerificationCodeIndexValue}" scope="request" />
                    <c:set var="name" value="<%=siteConfigModel.getSiteVerificationCodeModelList().get((Integer)request.getAttribute("siteVerificationCodeIndex")).getName()%>" />
                    <c:set var="value" value="<%=siteConfigModel.getSiteVerificationCodeModelList().get((Integer)request.getAttribute("siteVerificationCodeIndex")).getValue()%>" />
                    
                    <div class="li-bullet"><strong>Meta Tag Name: </strong>${name}</div>
                    <div class="li-bullet"><strong>Site Verification Code: </strong>${value}</div>
                
                </c:forEach>
            </c:if>

         
        <li></li>
           <c:set var="shoppingCart" value="<%=siteConfigModel.getCartUrl()%>" />
                 	<li><div class="li-bullet"><strong>Shopping Cart URL: </strong>${shoppingCart}</div></li>

        <li></li>

                        <strong>WhiteList for nsRoles : </strong>
                             <c:if test="<%=siteConfigModel.getWhiteListForNsRoles()!=null%>">
                               <c:set var="noOfRoles" value="<%=siteConfigModel.getWhiteListForNsRoles().size()%>" />

                              <c:if test="${noOfRoles gt 0}">
                                  <c:forEach var="j" begin="0" end="${noOfRoles-1}" step="1" varStatus="status1">
                                  <c:set var="roles" value="${j}" scope="request" />
                                  <c:set var="rolename" value="<%=siteConfigModel.getWhiteListForNsRoles().get((Integer)request.getAttribute("roles"))%>" />
                                   <div class="li-bullet"><strong> ${roles+1}: </strong>${rolename}</div>
                                    </c:forEach>
                              </c:if>
                                   </c:if>

        <br/>
        <br/>
           <button onclick="dialog.show()">Edit Site Configuration</button>

    </ul>
</div>
