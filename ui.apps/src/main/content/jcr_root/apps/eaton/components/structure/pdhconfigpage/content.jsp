<%@page import="com.eaton.platform.core.models.PdhConfigModel" %>
<%@include file="/libs/foundation/global.jsp" %>
<c:set var ="pdhConfigModel" value="<%= resource.adaptTo(PdhConfigModel.class)%>" />

<h2 style="margin:0;padding-left:0;">PDH Configuation Details</h2>
<br>
<br>

<strong>Combo Attributes</strong>
<table>
  <thead>
    <tr>
      <th>Combo Field Name</th>
      <th>Field 1 Name</th>
      <th>Field 1 I18n Key</th>
      <th>Field 2 Name</th>
      <th>Field 2 I18n Key</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${pdhConfigModel.comboAttributes}" var="comboAttribute">
      <tr>
        <td>${comboAttribute.comboFieldName}</td>
        <td>${comboAttribute.field1Name}</td>
        <td>${comboAttribute.field1I18nKey}</td>
        <td>${comboAttribute.field2Name}</td>
        <td>${comboAttribute.field2I18nKey}</td>
      </tr>
    </c:forEach>
  </tbody>
</table>

<br>
<br>

<button onclick="dialog.show()">Edit PDH Configuration</button>
