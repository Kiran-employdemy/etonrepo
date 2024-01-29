package com.eaton.platform.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class SocialShareConfigModel.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SocialShareConfigModel {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SocialShareConfigModel.class);

	/** The pub ID. */
	private String pubID;

	/** The allowed template. */
	private Boolean allowedTemplate = false;

	@Inject
	private Page currentPage;

	@OSGiService
	private AdminService adminService;

	@OSGiService
	private ConfigurationManagerFactory configManagerFctry;

	/**
	 * Init() method.
	 */
	@PostConstruct
	public void init() {
		if (null != adminService) {
			LOGGER.debug("SocialShareConfigModel :: init() :: Start");
			try (ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
				if(null != currentPage) {
				Resource currentPageRes = adminReadResourceResolver.resolve(currentPage.getPath());
				// socialshare
				Configuration configObj = CommonUtil.getCloudConfigObj(configManagerFctry, adminReadResourceResolver,
						currentPageRes, CommonConstants.SOCIAL_SHARE_CLOUD_CONFIG);
				Resource res = null;
				String[] allowedTemplates = CommonConstants.EMPTY_ARRAY;
				// if cloud config object is not null, get the details
				if (null != configObj) {
					Resource socialshareconfigResource = adminReadResourceResolver.resolve(configObj.getPath()
					.concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_STR));
					res = socialshareconfigResource.getParent();
					pubID = CommonUtil.getStringProperty(socialshareconfigResource.getValueMap(),
							CommonConstants.SOCIAL_SHARE_PUB_ID);
					if (res != null) {
						Page socialShareConfigPage = res.adaptTo(Page.class);
						if (socialShareConfigPage != null) {
							allowedTemplates = socialShareConfigPage.getProperties()
									.get(CommonConstants.ALLOWED_TEMPLATE_PROPERTY, String[].class);
						}
					}
				}
				// get current page type to check if social share is applicable
				String currentPageType = null;
				res = currentPageRes.getChild(CommonConstants.JCR_CONTENT_STR);
				if (res != null) {
					currentPageType = CommonUtil.getStringProperty(res.getValueMap(), CommonConstants.PAGE_TYPE);
				}
				// social share is allowed on Article Page, Product Family Page
				// and SKU page: Needs to be updated in Release 2 to allow on
				// Product Family & SKU pages

				if (allowedTemplates != null && allowedTemplates.length > 0) {
					for (String configuredPageTypeItem : allowedTemplates) {
						if (null != currentPageType
								&& StringUtils.equalsIgnoreCase(configuredPageTypeItem, currentPageType)) {
							allowedTemplate = true;
						}
					}
				}

			}
		} catch (Exception exception) {
				LOGGER.error("Exception occured while getting the reader service", exception);
			}
			LOGGER.debug("SocialShareConfigModel :: init() :: Exit");
		}
	}

	/**
	 * Gets the pub ID.
	 *
	 * @return the pubID
	 */
	public String getPubID() {
		return pubID;
	}

	/**
	 * Gets the allowed template.
	 *
	 * @return the allowedTemplate
	 */
	public Boolean getAllowedTemplate() {
		return allowedTemplate;
	}

}
