package com.eaton.platform.integration.eloqua.util;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.models.ResourceDecorator;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.eloqua.constant.EloquaConstants;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


/**
 * Utility Class for Eloqua Service
 *
 */
public class EloquaUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(EloquaUtil.class);

	private EloquaUtil() {
	    throw new IllegalStateException("Utility class");
	  }

	public static boolean hasEloquaForm(String pagePath, AdminService adminService){

		LOGGER.debug("EloquaUtil : hasEloquaForm : entry");
		
		boolean existsOnPage = false;
		if (null != adminService) {
			
			try (ResourceResolver adminResourceResolver = adminService.getReadService()) {

				PageManager pageManager = adminResourceResolver.adaptTo(PageManager.class);
				if (null != pageManager) {
					if (pagePath != null) {
						if(pagePath.contains(CommonConstants.HTML_EXTN)){
							pagePath = pagePath.replace(CommonConstants.HTML_EXTN, StringUtils.EMPTY).trim();
						}
					}
					Page page = pageManager.getPage(pagePath);
					if ((null != page) && (page.getContentResource() != null)) {
						final ResourceDecorator resourceDecorator = page.getContentResource().adaptTo(ResourceDecorator.class);
						if (null != resourceDecorator) {
							final Optional<Resource> componentResource = resourceDecorator.findByResourceType(EloquaConstants.ELOQUA_FORM_RESOURCE_TYPE);
							if (componentResource.isPresent()) {
								existsOnPage = true;
							}
						}
					}
				}
				
			}
			
		}
		
		LOGGER.debug("EloquaUtil : hasEloquaForm : exit");
		return existsOnPage;
	}

	public static String appendPrimaryProductTaxonomy(String pagePath, String primaryProductTaxonomy) {

		LOGGER.debug("EloquaUtil : appendPrimaryProductTaxonomy : entry");
		
		String pagePathWithPrimaryProductTaxonomy = null;

		try {

			String tagSuffix = StringUtils.join(EndecaConstants.ETN_PRODUCT_TAG, CommonConstants.SLASH_STRING);

			if ((primaryProductTaxonomy != null) && (primaryProductTaxonomy.contains(tagSuffix))) {
				primaryProductTaxonomy = primaryProductTaxonomy.replace(tagSuffix, StringUtils.EMPTY);
			} else {
				throw new EatonApplicationException("Primary Product Taxonomy tag does not reside under {} path.", tagSuffix);
			}

			String encodedPrimaryProductTaxonomy = CommonUtil.encode(primaryProductTaxonomy, CommonConstants.UTF_8);

			pagePathWithPrimaryProductTaxonomy = StringUtils.join(
					pagePath,
					CommonConstants.QUESTION_MARK_CHAR,
					CommonConstants.PRIMARY_PRODUCT_TAXONOMY,
					CommonConstants.EQUALS_SYMBOL,
					encodedPrimaryProductTaxonomy);

		} catch (EatonApplicationException e) {
			LOGGER.error("EatonApplicationException: ", e);
		}

		LOGGER.debug("EloquaUtil : appendPrimaryProductTaxonomy : exit");
		return pagePathWithPrimaryProductTaxonomy != null ? pagePathWithPrimaryProductTaxonomy : pagePath;
	}

}
