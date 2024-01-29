<%@page session="false" %>
<%@page contentType="text/html" pageEncoding="utf-8"
        import="java.util.ArrayList,
    			com.eaton.platform.core.util.EloquaConfigUtil,
				com.eaton.platform.core.models.EloquaCloudConfigModel,
				java.util.List" %>

<%@include file="/libs/foundation/global.jsp" %>

<%
EloquaCloudConfigModel eloquaCloudConfigModel = EloquaConfigUtil.getEloquaConfigDetails(resource);

%>
<div>

    <ul style="float: left; margin: 0px;">
        <h2 style="margin:0;padding-left:0;">Eloqua Account Details</h2>
            <p></p>

        	<c:set var="eloquaServerUrl" value="<%=eloquaCloudConfigModel.getEloquaserverurl()%>" />
        	<li><div class="li-bullet"><strong>Eloqua Server URL : </strong>${eloquaServerUrl}</div></li>

        	<c:set var="eloquaSubmitUrl" value="<%=eloquaCloudConfigModel.getEloquaSubmitUrl()%>" />
        	<li><div class="li-bullet"><strong>Eloqua Submit URL : </strong>${eloquaSubmitUrl}</div></li>

        	<c:set var="eloquaCompanyName" value="<%=eloquaCloudConfigModel.getEloquaCompanyName()%>" />
        	<li><div class="li-bullet"><strong>Eloqua Company Name : </strong>${eloquaCompanyName}</div></li>

        	<c:set var="eloquaEndpointUrl" value="<%=eloquaCloudConfigModel.getEloquaEndpointUrl()%>" />
        	<li><div class="li-bullet"><strong>Eloqua EndPoint URL : </strong>${eloquaEndpointUrl}</div></li>

        	<c:set var="eloquaUsername" value="<%=eloquaCloudConfigModel.getEloquaUsername()%>" />
        	<li><div class="li-bullet"><strong>Eloqua Username : </strong>${eloquaUsername}</div></li>

        	<c:set var="eloquaSiteId" value="<%=eloquaCloudConfigModel.getEloquaSiteId()%>" />
        	<li><div class="li-bullet"><strong>Eloqua Site ID : </strong>${eloquaSiteId}</div></li>

        	<c:set var="lookupIdVisitor" value="<%=eloquaCloudConfigModel.getLookupIdVisitor()%>" />
        	<li><div class="li-bullet"><strong>Lookup ID Visitor : </strong>${lookupIdVisitor}</div></li>

        	<c:set var="lookupIdPrimary" value="<%=eloquaCloudConfigModel.getLookupIdPrimary()%>" />
        	<li><div class="li-bullet"><strong>Lookup ID Primary : </strong>${lookupIdPrimary}</div></li>

        	<c:set var="visitorUniqueField" value="<%=eloquaCloudConfigModel.getVisitorUniqueField()%>" />
        	<li><div class="li-bullet"><strong>Visitor Unique Field : </strong>${visitorUniqueField}</div></li>

        	<c:set var="primaryUniqueField" value="<%=eloquaCloudConfigModel.getPrimaryUniqueField()%>" />
        	<li><div class="li-bullet"><strong>Primary Unique Field : </strong>${primaryUniqueField}</div></li>

        	<c:set var="cookieTrackingUrl" value="<%=eloquaCloudConfigModel.getCookieTrackingUrl()%>" />
        	<li><div class="li-bullet"><strong>Cookie Tracking URL : </strong>${cookieTrackingUrl}</div></li>


        <br/>
        <br/>


        <br/>
        <br/>
           <button onclick="dialog.show()">Edit Eloqua Configuration</button>

    </ul>
</div>
