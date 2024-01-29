<%@page import="com.eaton.platform.core.models.EndecaConfigModel" %>
<%@include file="/libs/foundation/global.jsp" %>
<c:set var ="endecaConfigModel" value="<%= resource.adaptTo(EndecaConfigModel.class)%>" />

<h2 style="margin:0;padding-left:0;">Endeca Configuation Details</h2>
<br>
<br>

<strong>Sortable Attributes</strong>
<c:forEach items="${endecaConfigModel.sortableAttributes}" var="sortableAttribute">
  <div style="margin-left: 10px;">${sortableAttribute}</div>
</c:forEach>
<br>
<br>

<strong>Attribute Mappings</strong>
<table>
  <thead>
    <tr>
      <th>AEM Attribute Name</th>
      <th>Endeca Request Attribute Name</th>
      <th>Endeca Response Attribute Name</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${endecaConfigModel.attributeMappings}" var="attributeMapping">
      <tr>
        <td>${attributeMapping.aemAttributeName}</td>
        <td>${attributeMapping.endecaRequestAttributeName}</td>
        <td>${attributeMapping.endecaResponseAttributeName}</td>
      </tr>
    </c:forEach>
  </tbody>
</table>

<br>
<br>

<strong>Additional Taxonomy Attributes</strong>
<table>
  <thead>
    <tr>
      <th>Taxonomy Attribute Name</th>
      <th>Taxonomy Attribute I18 Key</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${endecaConfigModel.additionalTaxonomyAttributes}" var="additionalTaxonomyAttribute">
      <tr>
        <td>${additionalTaxonomyAttribute.taxonomyAttributeName}</td>
        <td>${additionalTaxonomyAttribute.taxonomyAttributeI18nKey}</td>
      </tr>
    </c:forEach>
  </tbody>
</table>

<br>
<br>

<button onclick="dialog.show()">Edit Endeca Configuration</button>
