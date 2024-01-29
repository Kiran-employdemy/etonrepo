package com.eaton.platform.core.vgselector.helpers;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.models.vgSelector.ConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.core.vgselector.utils.VGSelectorUtil;

/**
 * <html> Description: This class is used in "/apps/eaton/components/form/custom_submit_action_clutch/invoke_service/post.POST.jsp" file
 * to get the cloud configurations</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2018
 */
public class PostHelper {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PostHelper.class);
	
	/**
	 * Gets the cloud config.
	 *
	 * @param currentRes the current res
	 * @param sling the sling
	 * @return the cloud config
	 */
	public ConfigModel getCloudConfig(Resource currentRes, SlingScriptHelper sling){
		LOGGER.debug("PostHelper :: getCloudConfig() :: Start");
		
		ConfigModel cloudConfig = null;
		
		AdminService adminService = sling.getService(AdminService.class);
		ConfigurationManagerFactory configManagerFctry = sling.getService(ConfigurationManagerFactory.class);

		if (null != adminService && null != configManagerFctry) {
			try (ResourceResolver adminResourceResolver = adminService.getReadService())
			{
				String resourcePath = StringUtils.substringBefore(currentRes.getPath(), VGCommonConstants.JCR_CONTENT);
				Resource currentPageRes = adminResourceResolver.resolve(resourcePath);

				// get the configuration set in the Cloud Config Tab of the form page
				cloudConfig = VGSelectorUtil.populateSiteConfiguration(configManagerFctry, adminResourceResolver, currentPageRes);
			}
		}
		LOGGER.debug("PostHelper :: getCloudConfig() :: End");
		return cloudConfig;
	}
}
