<%--
  ADOBE CONFIDENTIAL

  Copyright 2017 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="org.apache.commons.collections.Transformer,
                  org.apache.commons.collections.iterators.TransformIterator,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
				  com.day.cq.wcm.api.PageManager,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.HashMap,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  org.apache.sling.api.resource.ResourceMetadata,
                  java.util.List,
                  java.util.ArrayList,
                  java.util.Arrays,
				  com.eaton.platform.integration.ordercenter.services.OrderCenterStoreIdService,
				  com.google.gson.JsonObject,
				  com.google.gson.JsonElement,
				  com.google.gson.Gson,
				  java.lang.reflect.Type,
				  com.google.gson.reflect.TypeToken,
				  org.apache.commons.lang.StringUtils" %><%

    final ResourceResolver resolver = resourceResolver;
    ValueMap props = resource.getValueMap();
    String optionsStr = props.get("options", "");
    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    String callingPage = request.getRequestURL().toString();
    OrderCenterStoreIdService ocs = sling.getService(OrderCenterStoreIdService.class);

    if (ocs.isEnabled() && callingPage.contains(ocs.getOrderCenterTagPath())){

        JsonElement jsonobj = ocs.getStoreIds();
        if(jsonobj != null && jsonobj.isJsonArray()){
            Type listType = new TypeToken<List<String>>() {}.getType();

            List<String> options = new Gson().fromJson(jsonobj, listType);

            @SuppressWarnings("unchecked")
            DataSource ds = new SimpleDataSource(new TransformIterator(options.iterator(), new Transformer() {
                public Object transform(Object input) {
                    try {
                        String text = ((String) input).trim();
                        ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
                        vm.put("value", text);
                        vm.put("text", text);

                        return new ValueMapResource(
                                resolver, new ResourceMetadata(), "nt:unstructured", vm);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }));

            request.setAttribute(DataSource.class.getName(), ds);
        }


    } else {
        String[] optionsArray = optionsStr.split(",");
        List<String> options = new ArrayList<String>(4);
        options.addAll(Arrays.asList(optionsArray));
    
        @SuppressWarnings("unchecked")
        DataSource ds = new SimpleDataSource(new TransformIterator(options.iterator(), new Transformer() {
            public Object transform(Object input) {
                try {
                    String text = ((String) input).trim();
                    ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
    
                    vm.put("value", text);
                    vm.put("text", text);
    
                    return new ValueMapResource(
                            resolver, new ResourceMetadata(), "nt:unstructured", vm);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }));

        request.setAttribute(DataSource.class.getName(), ds);
	}

%>