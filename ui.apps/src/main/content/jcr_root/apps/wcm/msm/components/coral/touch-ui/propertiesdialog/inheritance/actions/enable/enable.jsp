<%--
 * enable.jsp
 * --------------------------------------
 *
 * A component for "enable inheritance" action.
 * 
 * --------------------------------------
 * Author: Jaroslav Rassadin
--%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"%><%
%><%@page import="org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.resource.SyntheticResource,
                  org.apache.sling.api.resource.ValueMap,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ValueMapResourceWrapper,
                  com.day.cq.wcm.api.Page,
                  com.day.cq.wcm.api.PageManager,
                  com.day.cq.wcm.msm.api.LiveRelationshipManager,
                  com.day.cq.wcm.msm.api.LiveRelationship" %><%
try {
	final Config cfg = cmp.getConfig();
	final String path = cmp.getExpressionHelper().getString(cfg.get("path", cfg.get("data/path", "")));

	if ("".equals(path)) {
		return;
	}
	if (!cmp.getRenderCondition(resource).check()) {
		return;
	}
	final ResourceResolver resolver = slingRequest.getResourceResolver();
	final Resource target = resolver.getResource(path);
	
	if(target == null){
		return;
	}	
	final String newPath = path.replaceFirst("_msm_moved(_\\d)?$", "");
	
	final LiveRelationshipManager relationshipManager = sling.getService(LiveRelationshipManager.class);
	final LiveRelationship relationship = relationshipManager.getLiveRelationship(target, false);
	
	final String blueprintPath = relationship != null ? relationship.getSourcePath().replaceFirst("_msm_moved(_\\d)?$", "") : "";

	final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
	final Page deletePage = pageManager.getPage(newPath);
	
	final String deletePath = deletePage != null ? newPath : "";

	final Resource wrapper = new ValueMapResourceWrapper(resource, "granite/ui/components/coral/foundation/collection/action") {

		@Override
		public Resource getChild(final String relPath) {

			if ("granite:data".equals(relPath)) {
				final SyntheticResource sres = new SyntheticResource(this.getResourceResolver(), "synthetic", null);
				final Resource dataWrapper = new ValueMapResourceWrapper(sres, "granite/ui/components/coral/foundation");
				final ValueMap dataVm = dataWrapper.adaptTo(ValueMap.class);
				
				dataVm.put("newpath", newPath);
				dataVm.put("blueprintpath", blueprintPath);
				dataVm.put("deletepath", deletePath);
		
				return dataWrapper;
		
			} else {
				return super.getChild(relPath);
			}
		}
	};
	final ValueMap vm = wrapper.adaptTo(ValueMap.class);

	vm.put("action", cfg.get("action", ""));
	vm.put("rel", cfg.get("rel", ""));
	vm.put("text", cfg.get("text", ""));
	vm.put("granite:title", cfg.get("granite:title", ""));
	vm.put("icon", cfg.get("icon", ""));
	vm.put("variant", cfg.get("variant", ""));
	vm.put("granite:class", cfg.get("granite:class", ""));
	
%><sling:include resource="<%=wrapper%>" />
<%
	} catch (Exception ex) {

	}
%>