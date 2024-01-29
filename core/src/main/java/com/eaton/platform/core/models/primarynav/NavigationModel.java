package com.eaton.platform.core.models.primarynav;

import com.day.cq.wcm.api.Page;

import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.ResourceResolver;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

/**
 * Navigation Model class
 * Model for Primary Navigation component
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NavigationModel {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(NavigationModel.class);

    /** The Constant PRIMARY_NAV_SELECTOR. */
    private static final String PRIMARY_NAV_SELECTOR = "primary-nav";

    /** The link list model. */
    private PrimaryNavigationModel primaryNavModel;

    /** The view content list model. */
    private PrimaryNavContentList viewContentListModel;

    @Inject
    private Page currentPage;

    @Self
    private SlingHttpServletRequest slingRequest;

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource resource;

    /**
     * Init method, previously called setComponentValues()
     * Takes brunt of initial config from old PrimaryNavigationHelper.java
     */
    @PostConstruct
    public void init() {
        LOGGER.debug("NavigationModel  :: init() :: Start");
        // local variables
        String selector = null;
        Resource linkListRes = null;
        Page homePage = null;

        //null pointer check.
        if(null!=currentPage){
            homePage = CommonUtil.getHomePage(currentPage);
        }

        // get selector from header & footer passed while including linklist
        // component
        selector = slingRequest.getRequestPathInfo().getSelectorString();
        // if selector is available, component is statically included in header
        // or footer of home page template,
        // resources are not present under page resources but inherited from
        // home page
        if (null != selector && null != homePage && StringUtils.equals(PRIMARY_NAV_SELECTOR, selector)) {
            linkListRes = resourceResolver.getResource(homePage.getPath().concat("/jcr:content/root/header/primary-nav"));

        } else {
            linkListRes = resource;
        }

        // Primary Navigation view. SonarQube Null Pointer Issue fix
        if(null!=linkListRes){
            primaryNavModel = linkListRes.adaptTo(PrimaryNavigationModel.class);
            viewContentListModel = linkListRes.adaptTo(PrimaryNavContentList.class);
        }
        LOGGER.debug("NavigationModel :: init() :: End");
    }

    /**
     * Gets the link list bean.
     *
     * @return linkListBean
     */
    public PrimaryNavigationModel getLinkListBean() {
        return primaryNavModel;
    }

    /**
     * Gets the view content list model.
     *
     * @return the view content list model
     */
    public PrimaryNavContentList getViewContentListModel() {
        return viewContentListModel;
    }

    /**
     * Gets the primary nav bg image
     *
     * @return the primaryNavBGImage
     */
    public String getPrimaryNavBGImage() {
        String bGImage = null;
        Page homePage = null;
        Resource headerRes = null;

        if(null != currentPage) {
            homePage = CommonUtil.getHomePage(currentPage);
            if(homePage!= null) {
                headerRes = resourceResolver.getResource(homePage.getPath().concat("/jcr:content/root/header"));
            }
        }

        if(headerRes!= null){
            ValueMap properties =	headerRes.getValueMap();
            bGImage =  CommonUtil.getStringProperty(properties, "primaryNavBGImage");
        }
        return bGImage;
    }

    /**
     * Gets the primary nav bg image alternative text
     * 
     * @return the primaryNavBGImgAltText
     */
    public String getPrimaryNavBGImgAltText() {
        String bgImagePath = getPrimaryNavBGImage();
        String bgImageAltText = null;
        if(bgImagePath != null) {
            bgImageAltText = CommonUtil.getAssetAltText(resourceResolver, bgImagePath);
        }
        return bgImageAltText;
    }

}
