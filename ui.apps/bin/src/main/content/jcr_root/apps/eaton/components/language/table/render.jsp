<%--
    Language Table - render.jsp
    ---
    Renders the table of available translations.
    ---
    By: Soo Woo (soo@freedomdam.com)
--%><%
%><%@ include file="/libs/granite/ui/global.jsp" %><%
%><%@ page session="false"
           import="javax.jcr.Node,
                  javax.jcr.Property,
                  javax.jcr.PropertyIterator,
                  org.apache.sling.api.resource.ValueMap,
                  java.util.HashSet,
                  java.util.Iterator" %>
<%
    String suffix = slingRequest.getRequestPathInfo().getSuffix();
    if (suffix == null) {
        response.getWriter().write("There is no content to display. Please go back and use the language table link.");
        return;
    }

    Resource res = slingRequest.getResourceResolver().getResource(suffix + "/jcr:content/metadata");
    if (res == null) {
        response.getWriter().write("There is no content to display. Please go back and use the language table link.");
        return;
    }

    Node node = res.adaptTo(Node.class);
    ValueMap metadataMap = res.adaptTo(ValueMap.class);
    PropertyIterator nodePropertiesIterator = node.getProperties();
    HashSet<String> langCtyCode = new HashSet<>();
    while (nodePropertiesIterator.hasNext()) {
        Property prop = nodePropertiesIterator.nextProperty();
        if (prop.getName().startsWith("dc:title-")) {
            langCtyCode.add(prop.getName().replaceAll("dc:title-", ""));
        }
        if (prop.getName().startsWith("dc:description-")) {
            langCtyCode.add(prop.getName().replaceAll("dc:description-", ""));
        }
    }

    Iterator langCtyCodeIterator = langCtyCode.iterator();
%>

<ui:includeClientLib categories="eaton.multilingual.table" />

<table id="multilingual-table" is="coral-table">
    <thead is="coral-table-head">
    <tr is="coral-table-row">
        <th is="coral-table-headercell">Language</th>
        <th is="coral-table-headercell">Title</th>
        <th is="coral-table-headercell">Description</th>
    </tr>
    </thead>
    <tbody is="coral-table-body" divider="cell">
    <% if (langCtyCode.size() < 1) { %>
        <tr is="coral-table-row">
            <td is="coral-table-cell" style="text-align:center" colspan="4">
                <p>There is no content to display.</p>
            </td>
        </tr>
    <% } else { %>
        <% while (langCtyCodeIterator.hasNext()) { %>
            <% String prop = (String)langCtyCodeIterator.next(); %>
            <tr is="coral-table-row">
                <td is="coral-table-cell" class="language"><%= xssAPI.encodeForHTML(prop) %></td>
                <td is="coral-table-cell"><%= xssAPI.encodeForHTML(metadataMap.get("dc:title-" + prop, "")) %></td>
                <td is="coral-table-cell"><%= xssAPI.encodeForHTML(metadataMap.get("dc:description-" + prop, "")) %></td>
            </tr>
        <% } %>
    <% } %>
    </tbody>
</table>