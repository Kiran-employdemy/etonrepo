<%@page import="com.eaton.platform.integration.priceSpider.models.PriceSpiderConfigModel" %>
<%@include file="/libs/foundation/global.jsp" %>
<c:set var ="priceSpiderConfigModel" value="<%= resource.adaptTo(PriceSpiderConfigModel.class)%>" />

<h2 style="margin:0;padding-left:0;">Price Spider Configuation Details</h2>

<br>
<br>

<table>
  <tbody>
    <tr><td>Price Spider JSON Location:</td>             <td>${priceSpiderConfigModel.jsonLocation}</td></tr>
    <tr><td>Universal Script Path:</td>             <td>${priceSpiderConfigModel.universalScriptPath}</td></tr>
    <tr><td>Product Script Path:</td>             <td>${priceSpiderConfigModel.productScriptPath}</td></tr>
  </tbody>
</table>

<br>
<br>

<button onclick="dialog.show()">Edit Price Spider Configuration</button>
