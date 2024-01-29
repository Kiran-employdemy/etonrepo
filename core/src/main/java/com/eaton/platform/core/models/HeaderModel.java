package com.eaton.platform.core.models;

import javax.inject.Inject;
import javax.inject.Named;
import javax.annotation.PostConstruct;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeaderModel.class);
	private static final String HOMEPAGE_HEADER_PATH = "/jcr:content/root/header";
	private static final String PROPERTY_ENABLE_COOKIE = "enableCookieAcceptance";
	private static final String PROPERTY_DISABLED_FULL_PAGE_DRAWER = "disableFullPageDrawer";
	private static final String MICRO_SITE_HEADER_OPTIONS = "microSiteHeaderOptions";
	private static final String SEARCH = "search";
	private static final String HEADER_REFERENCE_PATH ="/jcr:content/headerReference";
	private static final String REFERENCE ="reference";

	@Inject
	private Page resourcePage;

	@Inject
	private Page currentPage;

	@Inject @Source("sling-object")
	private ResourceResolver resourceResolver;

	@Inject @Named("header-logo-secondary")
	private LogoModel headerLogoSecondary;

	@Inject
	private SlingHttpServletRequest slingRequest;

	private String microSiteHeaderOption;

	private Resource headerResource;

	private String homePagePath;

	private String editMode;

	private boolean isHomePage;

	@PostConstruct
	protected void init() {
		LOGGER.debug("HeaderModel: init: Entry");
		homePagePath = CommonUtil.getHomePagePath(resourcePage);

		if( null != currentPage ){
			isHomePage = CommonUtil.isHomePagePath(currentPage);
		} else {
			isHomePage = false;
		}

		headerResource = resourceResolver.getResource(homePagePath + HOMEPAGE_HEADER_PATH);
		if(headerResource != null) {
			microSiteHeaderOption = headerResource.getValueMap().get(MICRO_SITE_HEADER_OPTIONS, SEARCH);
		} else {
			microSiteHeaderOption = StringUtils.EMPTY;
		}

		WCMMode currentMode = WCMMode.fromRequest(slingRequest);

		if(isHomePage && (WCMMode.EDIT == currentMode)){
			editMode = CommonConstants.EDIT;
		} else {
			editMode = CommonConstants.DISABLED;
		}

		LOGGER.debug("HeaderModel: init: Exit");

	}

	public String getEnableCookieAcceptance() {
		String enableCookieAcceptance = StringUtils.EMPTY;
		if(null != headerResource && null != headerResource.getValueMap().get(PROPERTY_ENABLE_COOKIE)){
			enableCookieAcceptance = headerResource.getValueMap().get(PROPERTY_ENABLE_COOKIE).toString();
		}
		return enableCookieAcceptance;
	}

	public Boolean isSecondLogoAuthored() {
		return headerLogoSecondary != null &&
				headerLogoSecondary.getLogo() != null &&
				! headerLogoSecondary.getLogo().isEmpty();
	}

	public Boolean isDisableFullPageDrawer() {
		return headerResource != null && headerResource.getValueMap().get(PROPERTY_DISABLED_FULL_PAGE_DRAWER, false);
	}

	public String getMicroSiteHeaderOption() {
		return microSiteHeaderOption;
	}

	public Boolean isMicroSitePage() {
		String homePageTemplatePath = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(homePagePath)) {
			final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			final Page homePage;
			if (pageManager != null) {
				homePage = pageManager.getPage(homePagePath);
			} else {
				homePage = null;
			}

			if (homePage != null && null != homePage.getTemplate()) {
				homePageTemplatePath = homePage.getTemplate().getPath();
			}
		}
		return CommonUtil.isTemplatePathMatchesWithAnyMicroSiteTemplates(homePageTemplatePath, CommonConstants.microSiteHomePageTemplatePathConfig);
	}

	public String getMegaHeaderReferencePath() {
		String headerReference = StringUtils.EMPTY;
		if (null != headerResource) {
			headerReference = headerResource.getValueMap().get(REFERENCE, StringUtils.EMPTY);
			if (StringUtils.isNotBlank(headerReference)) {
				headerReference += HEADER_REFERENCE_PATH;
			}
		}
		return headerReference;
	}

	public String getEditMode() { return editMode;}

	public boolean isHomePage() {return isHomePage;}
}
