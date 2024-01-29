package com.eaton.platform.core.models;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.BrightcoveUtil;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL) 
public class BrightcoveModel {
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(BrightcoveModel.class);
	
	/** variable errorMsg. */
	private String errorMsg;
	
	/** variable playerUniqueId. */
	private String playerUniqueId;
	
	/** The bc comp model. */
	private BCComponentModel bcCompModel;
	
	/** contentRepository. */	
	private String contentRepository;
	
	@Inject
	protected AdminService adminService;
	
	@Inject
	protected ConfigurationManagerFactory configurationManagerFactory;
	
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;
	
	@Inject
	@ScriptVariable
	private Page currentPage;
	
	@Inject
	protected Resource res;
	
	
	@PostConstruct
	protected void init() {
		
		LOG.debug("BrightcoveModel :: init() :: Start");
		// get admin service
		Resource currentPageRes = resourceResolver.resolve(currentPage.getPath());
		if(null != adminService) {
			try (ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
				// get brightcove configuration page jcr resource
				Resource brightcoveConfigRes = BrightcoveUtil.getBCConfigResource(configurationManagerFactory,
						adminReadResourceResolver, currentPageRes);
				// get error message configured in brightcove config page
				errorMsg = BrightcoveUtil.getBCErrorMessage(brightcoveConfigRes);
				if (LOG.isInfoEnabled()) {
					LOG.info("in BrightcoveHelper activate() method errorMsg :::{}", errorMsg);
				}
				// unique identifier for Brightcove component, required to get a handle of the player
				playerUniqueId = UUID.randomUUID().toString().replace(CommonConstants.HYPHEN, StringUtils.EMPTY);

				//get account Number configured in brightcove config page
				contentRepository = BrightcoveUtil.getBCContentRepository(brightcoveConfigRes);

				bcCompModel = res.adaptTo(BCComponentModel.class);

			} catch (Exception exception) {
				LOG.error("Exception occured while getting the reader service", exception);
			}
		}
		
		LOG.debug("BrightcoveModel :: init() :: Exit");
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Gets the error msg.
	 *
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * Gets the player unique id.
	 *
	 * @return the playerUniqueId
	 */
	public String getPlayerUniqueId() {
		return playerUniqueId;
	}

	/**
	 * Gets the bc comp model.
	 *
	 * @return the bcCompModel
	 */
	public BCComponentModel getBcCompModel() {
		return bcCompModel;
	}
	
	/**
	 * Gets the content repository.
	 *
	 * @return the contentRepository
	 */
	public String getContentRepository() {
		return contentRepository;
	}

}
