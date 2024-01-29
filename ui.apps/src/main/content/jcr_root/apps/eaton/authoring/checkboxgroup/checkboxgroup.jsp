<%@ include file="/libs/granite/ui/global.jsp" %><%
%><%@ page session="false"
           import="java.util.Iterator,
    			  org.apache.commons.lang3.StringUtils,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag,
				  org.apache.sling.api.resource.ValueMap,
				  org.apache.sling.api.wrappers.ValueMapDecorator,
				  com.adobe.granite.ui.components.ds.ValueMapResource,
				  java.util.HashMap,
				  org.apache.sling.api.resource.ResourceMetadata,
				  org.apache.commons.logging.Log,
				  org.apache.commons.logging.LogFactory" %>
<% Log logger = LogFactory.getLog( this.getClass(  ) ); %>
<%
    Iterator<Resource> itemIterator = cmp.getItemDataSource().iterator();
    logger.info("in checkboxgroup jsp with datasource datasource = " + cmp.getItemDataSource().toString());
    if (itemIterator != null && itemIterator.hasNext()) {
        while(itemIterator.hasNext()) {
            ValueMap valueMap = new ValueMapDecorator(new HashMap<String, Object>());
            valueMap.putAll(resource.getValueMap());
            valueMap.putAll(itemIterator.next().getValueMap());
            Resource checkboxResource = new ValueMapResource(resourceResolver, resource.getPath(), "/libs/granite/ui/components/coral/foundation/form/checkbox", valueMap);
%>
<sling:include resource="<%= checkboxResource %>" />
<%
        }
    }
%>