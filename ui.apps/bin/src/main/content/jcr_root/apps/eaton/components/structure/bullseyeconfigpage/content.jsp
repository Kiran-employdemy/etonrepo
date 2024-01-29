<%@page import="com.eaton.platform.integration.bullseye.models.BullseyeConfigModel" %>
<%@include file="/libs/foundation/global.jsp" %>
<c:set var ="bullseyeConfigModel" value="<%= resource.adaptTo(BullseyeConfigModel.class)%>" />

<h2 style="margin:0;padding-left:0;">Bullseye Configuation Details</h2>

<br>
<br>

<table>
  <tbody>
    <tr><td>Client ID:</td>             <td>${bullseyeConfigModel.clientId}</td></tr>
    <tr><td>API Key:</td>               <td>${bullseyeConfigModel.apiKey}</td></tr>
    <tr><td>Mapping Vendor:</td>        <td>${bullseyeConfigModel.mappingVendor}</td></tr>
    <tr><td>Mapping API Key:</td>        <td>${bullseyeConfigModel.mappingApiKey}</td></tr>
    <tr><td>Page Size:</td>             <td>${bullseyeConfigModel.pageSize}</td></tr>
    <tr><td>Default Radius:</td>        <td>${bullseyeConfigModel.defaultRadius}</td></tr>
    <tr><td>Default Distance Unit:</td> <td>${bullseyeConfigModel.defaultDistanceUnit}</td></tr>
    <tr><td>Preapplied Filters:</td>    <td>${bullseyeConfigModel.printPrefilters}</td></tr>
  </tbody>
</table>

<br>
<br>

<button onclick="dialog.show()">Edit Bullseye Configuration</button>
