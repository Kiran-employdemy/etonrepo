<%@include file="/libs/granite/ui/global.jsp"%>

<%@ page import="com.adobe.granite.ui.components.ds.DataSource" %>
<%@ page import="com.adobe.granite.ui.components.ds.ValueMapResource" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.apache.sling.api.wrappers.ValueMapDecorator" %>
<%@ page import="com.adobe.granite.ui.components.ds.SimpleDataSource" %>
<%@ page import="org.apache.commons.collections.iterators.TransformIterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="org.apache.commons.collections.Transformer" %>
<%@ page import="org.apache.sling.api.resource.*" %>

<ui:includeClientLib categories="eaton.showhide.dialog" />
<%
    final Map<String, String> options = new LinkedHashMap<String, String>();

    options.put("expire", "Expire");
    options.put("renew", "Renew");

    final ResourceResolver resolver = resourceResolver;

    DataSource ds = new SimpleDataSource(new TransformIterator(options.keySet().iterator(), new Transformer() {
        public Object transform(Object o) {
            String option = (String) o;
            ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());

            vm.put("value", option);
            vm.put("text", options.get(option));

            return new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm);
        }
    }));

    request.setAttribute(DataSource.class.getName(), ds);
%>