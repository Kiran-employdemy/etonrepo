package com.eaton.platform.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;

/**
 * This class is used to hide secure tab at page properties.
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HideTabModel {

    private static final Logger LOG = LoggerFactory.getLogger(HideTabModel.class);

    private final static String SECURE_LINK_COMPONENT_NAME = "securelinkcta";

    @Self
    private SlingHttpServletRequest slingRequest;

    @Inject @Via("resource")
    private Resource resource;

    @ValueMapValue
    private String allowedTemplates[];

    @PostConstruct
    protected void init() {
        boolean display = false;
        LOG.debug("HideTabModel :: init() :: Start");
        if(resource != null && ArrayUtils.isNotEmpty(allowedTemplates)) {
            String currentPagePath = slingRequest.getParameter(CommonConstants.ITEM_PARAM);
            Resource currentPageResource = slingRequest.getResourceResolver().resolve(currentPagePath).getChild(JcrConstants.JCR_CONTENT);
            if(currentPageResource != null) {
                ValueMap resourceValueMap = currentPageResource.getValueMap();
                String currentPageTemplate = resourceValueMap.get(CommonConstants.TEMPLATE_PROP_KEY, StringUtils.EMPTY);
                for (int i = 0; i < allowedTemplates.length; i++) {
                    if (currentPageTemplate.equals(allowedTemplates[i])) {
                        display = true;
                        break;
                    }
                }
            } else if (slingRequest.getPathInfo().contains(SECURE_LINK_COMPONENT_NAME)) {
                display = true;
            }else {
                display = false;
            }
        }
        slingRequest.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(display));
        LOG.debug("HideTabModel :: init() :: End");
    }

}
