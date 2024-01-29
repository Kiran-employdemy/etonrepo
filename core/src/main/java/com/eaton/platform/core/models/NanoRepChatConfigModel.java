package com.eaton.platform.core.models;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.NanoRepChatConfigUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NanoRepChatConfigModel {
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(NanoRepChatConfigModel.class);

	private boolean showComponent;

	private NanoRepChatConfigBean chatConfigBean;

	private NanoRepChatBean chatBean;

	@OSGiService
	private AdminService adminService;
	
	@OSGiService
	private ProductFamilyDetails productFamilyDetails;

	@OSGiService
	private ProductFamilyPIMDetails productFamilyPIMDetails;
	
    @OSGiService
    private ConfigurationManagerFactory configManagerFctry;
       
	@Inject
	protected EndecaConfig endecaConfigService;

	@Inject
	protected EndecaService endecaService;
	
	@Inject
	private Page currentPage;	

	@Inject @Source("sling-object")
	private SlingHttpServletRequest slingRequest;
	
	@Inject @Source("sling-object") 
	private ResourceResolver resourceResolver; 

	@Inject
	protected ProductFamilyDetailService productFamilyDetailService;
	
	@Inject
	protected EndecaRequestService endecaRequestService;
	
	protected String pageType;
	
    @PostConstruct
    public void init() {
		LOG.debug("NanoRepChatConfigModel :: setComponentValues() :: Start");

		Resource currentPageRes = resourceResolver.resolve(currentPage.getPath());
		
		ValueMap properties = currentPage.getProperties();
		if (properties != null && properties.get(CommonConstants.PAGE_TYPE) != null) {
			pageType = properties.get(CommonConstants.PAGE_TYPE).toString();
		}

		if (null != adminService) {
			try(ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
							
				Configuration configObj = CommonUtil.getCloudConfigObj(configManagerFctry, adminReadResourceResolver,
						currentPageRes, CommonConstants.NR_CLOUD_CONFIG_NODE_NAME);

				  if (null != configObj && null != adminReadResourceResolver) {
					  Resource chatConfigRes = adminReadResourceResolver.resolve(configObj.getPath()
								.concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_STR));
					
					    // get configBean using ChatConfigUtil method for getting the configurations
					     chatConfigBean = NanoRepChatConfigUtil.populateChatList(chatConfigRes);
					     
					// Check for the Page type and accordingly fetch the tags and match them with that of the configuration
					if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
						LOG.info("in NanoRepChatConfigModel activate() method ::: Page Type is Product Family Page");
						productFamilyDetails = productFamilyDetailService.getProductFamilyDetailsBean(currentPage);
						if ((productFamilyDetails != null) &&
							((productFamilyDetails.getPimTags() != null) && (!(productFamilyDetails.getPimTags().isEmpty())))) {
								String[] pimTags = formatPIMTags(productFamilyDetails.getPimTags(), adminReadResourceResolver);
								chatBean = NanoRepChatConfigUtil.compareTags(chatConfigBean, pimTags);
						 }
					} else if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {
						LOG.info("in NanoRepChatConfigModel activate() method ::: Page Type is SKU Page");
						final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
						final EndecaServiceRequestBean endecaRequestBean = endecaRequestService.getEndecaRequestBean(currentPage, selectors, StringUtils.EMPTY);
						final SKUDetailsResponseBean skuDetails = endecaService.getSKUDetails(endecaRequestBean);
						final SKUDetailsBean skuDetailsBean = skuDetails.getSkuResponse().getSkuDetails();
						productFamilyPIMDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);
						if ((productFamilyPIMDetails != null)&& 
								((productFamilyPIMDetails.getPimTags() != null) && (!(productFamilyPIMDetails.getPimTags().isEmpty())))){
								String[] pimTags = formatPIMTags(productFamilyPIMDetails.getPimTags(), adminReadResourceResolver);
								chatBean = NanoRepChatConfigUtil.compareTags(chatConfigBean, pimTags);					
							}				
					} else {
						LOG.info("in NanoRepChatConfigModel activate() method ::: Page Type is Other Pages");
						
							currentPage = CommonUtil.getOriginalPage(currentPage, adminReadResourceResolver);
							String[] pageAemTags = (String[]) currentPage.getProperties().get(TagConstants.PN_TAGS);
							chatBean = NanoRepChatConfigUtil.compareTags(chatConfigBean, pageAemTags);
					}
				}
				// if the chatbean data is present then the configuration is matched with the chatConfig object
			    if (chatBean != null) { 
					showComponent = true;
					LOG.info("in NanoRepChatConfigModel activate() method Matched :::");
				} else {
					showComponent = false;
					LOG.info("in NanoRepChatConfigModel activate() method NOT Matched :::");
				}

			} catch (Exception exception) {
				LOG.error("Exception occured while getting the reader service", exception);
			}
		}
		LOG.debug("NanoRepChatConfigModel :: setComponentValues() :: Exit");
	}
	
	public NanoRepChatConfigBean getChatConfigBean() {
		return chatConfigBean;
	}
	
	private static String[] formatPIMTags(List<String> tagList, ResourceResolver resourceResolver){
		String[] pimTags = new String[tagList.size()];
		int index = 0;
   
		final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		if(tagManager!= null){
		for (String tagPath : tagList) {
			if (StringUtils.isNotBlank(tagPath)) {
				
				Tag tag = tagManager.resolve(tagPath);
				if(tag != null) {
					pimTags[index] = tag.getTagID();
					index++;
				}
			}
		  }
		}
		return pimTags;
	}
	
	public NanoRepChatBean getChatBean() {
		return chatBean;
	}

	public boolean getShowComponent() {
		return showComponent;
	}
}
