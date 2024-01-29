package com.eaton.platform.integration.eloqua.condition.impl;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.integration.akamai.exception.AkamaiNetStorageException;
import com.eaton.platform.integration.akamai.services.AkamaiNetStorageService;
import com.eaton.platform.integration.eloqua.condition.Redirectable;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

public class SoftwareDeliveryRedirectable implements Redirectable {

    private static final Logger LOG = LoggerFactory.getLogger(SoftwareDeliveryRedirectable.class);


    private SlingHttpServletRequest request;
    private AkamaiNetStorageService netStorageService;
    public static final String SOFTWARE_DELIVERY_TEMPLATE ="/conf/eaton/settings/wcm/templates/software-delivery";
    public static final String DOWNLOAD_PATH_VAR = "softwarePath";
    private String downloadPath;


    public SoftwareDeliveryRedirectable(SlingHttpServletRequest request, AkamaiNetStorageService netStorageService){
        this.request = request;
        this.netStorageService = netStorageService;
        this.downloadPath = request.getParameter(DOWNLOAD_PATH_VAR);
    }

    @Override
    public boolean shouldRedirect() {
        boolean shouldRedirect = false;
        Resource currentResource = request.getResource();
        if(!currentResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)){
            ResourceResolver resourceResolver = currentResource.getResourceResolver();
            LOG.debug("Software Delivery Redirectable :: Current Resource Path :: {}",currentResource.getPath());
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page currentPage = pageManager.getContainingPage(currentResource);
            if(null != currentPage) {
                String template = currentPage.getProperties().get(NameConstants.NN_TEMPLATE, StringUtils.EMPTY);
                LOG.debug("Software Delivery Redirectable :: template path :: {}", template);
                shouldRedirect = StringUtils.isNotBlank(template) && template.equals(SOFTWARE_DELIVERY_TEMPLATE) && StringUtils.isNotBlank(downloadPath);
            } else {
                LOG.error("SoftwareDeliveryRedirectable :: shouldRedirect :: Unable to get resource from current request");
            }
        } else {
            LOG.error("SoftwareDeliveryRedirectable :: shouldRedirect :: Unable to get resource from current request");
        }
        LOG.debug("Software Delivery Redirectable :: should redirect :: {}",shouldRedirect);
        return shouldRedirect;
    }

    @Override
    public String redirectUrl() {
        if(shouldRedirect()){
            try {
                return netStorageService.getDownloadUrl(downloadPath);
            } catch (AkamaiNetStorageException e) {
                LOG.error("AkamaiNetStorageException :: {}",e.getMessage(),e);
            } catch (URISyntaxException e) {
                LOG.error("URISyntaxException :: {}",e.getMessage(),e);
            }
        }
        return StringUtils.EMPTY;
    }
}
