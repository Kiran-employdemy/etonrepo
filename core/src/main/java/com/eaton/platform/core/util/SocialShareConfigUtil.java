package com.eaton.platform.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;

/**
 * The Class SocialShareConfigUtil.
 */
public final class SocialShareConfigUtil {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SocialShareConfigUtil.class);

	/**
	 * Instantiates a new social share config util.
	 */
	private SocialShareConfigUtil() {
		LOGGER.debug("Inside SocialShareConfigUtil constructor");
	}

	/**
	 * This method populates Brightcove account details like account name,
	 * number and invokes populatePlayerList method to fetch player list.
	 *
	 * @param ssConfigRes
	 *            the ss config res
	 * @return the allowed templates
	 */
	public static List<String> getAllowedTemplates(Resource ssConfigRes) throws NullPointerException {
		LOGGER.debug("SocialShareConfigUtil :: getAllowedTemplates() :: Start");
		// local variables
		String[] allowedTemplates = null;
		List<String> allowedTempList = new ArrayList<>();

		Page ssConfigPage = null;
		// check if brightcove accounts are configured
		try {
			if (null != ssConfigRes) {
				Resource res = ssConfigRes.getParent();
				if (res != null) {
					ssConfigPage = res.adaptTo(Page.class);
				}
				if (ssConfigPage != null)
					allowedTemplates = ssConfigPage.getProperties().get(CommonConstants.ALLOWED_TEMPLATE_PROPERTY,
							String[].class);
			}
			if (allowedTemplates != null) {
				//Check for the length
				if (allowedTemplates.length > 0) {
					for (String configuredTemp : allowedTemplates) {
						if (StringUtils.equalsIgnoreCase(configuredTemp, CommonConstants.PAGE_TYPE_HOME_PAGE)) {
							allowedTempList.add(CommonConstants.HOME_PAGE_LABEL);
						} else if (StringUtils.equalsIgnoreCase(configuredTemp,
								CommonConstants.PAGE_TYPE_GENERIC_PAGE)) {
							allowedTempList.add(CommonConstants.GENERIC_PAGE_LABEL);
						} else if (StringUtils.equalsIgnoreCase(configuredTemp,
								CommonConstants.PAGE_TYPE_ARTICLE_PAGE)) {
							allowedTempList.add(CommonConstants.ARTICLE_PAGE_LABEL);
						} else if (StringUtils.equalsIgnoreCase(configuredTemp,
								CommonConstants.PAGE_TYPE_SITEMAP_PAGE)) {
							allowedTempList.add(CommonConstants.SITEMAP_PAGE_LABEL);
						}
						else if (StringUtils.equalsIgnoreCase(configuredTemp,
								CommonConstants.PAGE_TYPE_SKU_PAGE)) {
							allowedTempList.add(CommonConstants.SKU_PAGE_LABEL);
						}
						else if (StringUtils.equalsIgnoreCase(configuredTemp,
								CommonConstants.PAGE_TYPE_PRODUCTFAMILY_PAGE)) {
							allowedTempList.add(CommonConstants.PRODUCTFAMILY_PAGE_LABEL);
						}
					}
				}
			}
		} catch (NullPointerException e) {
			LOGGER.error("Exception occured while getting brightcove account details", e);
		}

		LOGGER.debug("SocialShareConfigUtil :: getAllowedTemplates() :: Exit");
		return allowedTempList;
	}

}