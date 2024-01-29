<%@page import="com.eaton.platform.core.models.ModelViewerCloudConfig" %>
<%@include file="/libs/foundation/global.jsp" %>
<c:set var ="modelViewerConfig" value="<%= resource.adaptTo(ModelViewerCloudConfig.class)%>" />

<h2 style="margin:0;padding-left:0;">Model Viewer Configuration Details</h2>
<br>
<br>

<strong>Model Viewers</strong>
<table>
  <thead>
    <tr>
      <th>Model Viewer Title</th>
      <th>Part Solutions URL</th>
      <th>Part Solutions Catalog</th>
      <th>Resource Group</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${modelViewerConfig.modelViewers}" var="modelViewer">
      <tr>
        <td>${modelViewer.title}</td>
        <td>${modelViewer.partSolutionsUrl}</td>
        <td>${modelViewer.partSolutionsCatalog}</td>
        <td>${modelViewer.resourceGroup}</td>
      </tr>
    </c:forEach>
  </tbody>
</table>

<br>
<br>

<button onclick="dialog.show()">Edit Model Viewer Configuration</button>
