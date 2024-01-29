<%@page session="false"
        import="com.adobe.acs.commons.errorpagehandler.ErrorPageHandlerService,
    			com.adobe.acs.commons.wcm.vanity.VanityURLService,
        		com.eaton.platform.core.services.vanity.*"%><%
%><%@include file="/libs/foundation/global.jsp" %><%
    ErrorPageHandlerService errorPageHandlerService = sling.getService(ErrorPageHandlerService.class);
    if (errorPageHandlerService != null && errorPageHandlerService.isEnabled()) {
    	if (errorPageHandlerService.isVanityDispatchCheckEnabled()){
            final EatonVanityConfigService eatonVanityConfigService = sling.getService(com.eaton.platform.core.services.vanity.EatonVanityConfigService.class);
            if(eatonVanityConfigService != null && eatonVanityConfigService.getVanityConfig().isEnableCustomVanityFlow()){
                final EatonVanityURLService eatonVanityURLService = sling.getService(com.eaton.platform.core.services.vanity.EatonVanityURLService.class);
                if (eatonVanityURLService != null && eatonVanityURLService.dispatchMethod(slingRequest, slingResponse)){
                    return;
                }
            }else{
                final VanityURLService vanityURLService = sling.getService(VanityURLService.class);
                if (vanityURLService != null && vanityURLService.dispatch(slingRequest, slingResponse)){
                    return;
                }
            }
    	}

    	// Check for and handle 404 Requests properly according on Author/Publish
        if (errorPageHandlerService.doHandle404(slingRequest, slingResponse)) {
        	final String path = errorPageHandlerService.findErrorPage(slingRequest, resource);
			if (path != null) {
				slingResponse.setStatus(404);
                errorPageHandlerService.resetRequestAndResponse(slingRequest, slingResponse, 404);
				errorPageHandlerService.includeUsingGET(slingRequest, slingResponse, path);
				return;
            }
    	}
    }
%><%@include file="/libs/sling/servlet/errorhandler/default.jsp" %>
