package com.eaton.platform.core.models;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.ChatConfigUtil;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.models.annotations.Source;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ChatConfigModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ChatConfigModel.class);

	private boolean showComponent;

	private ChatConfigBean chatConfigBean;

	private ChatBean chatBean;
	
	/** The current page. */
	@Inject
	protected Page currentPage;
	
	@Inject
	protected ProductFamilyDetailService productFamilyDetailService;
	
	@OSGiService
	private ProductFamilyPIMDetails productFamilyPIMDetails;
	
	/** The sling request. */
	@Inject @Source("sling-object")
	protected SlingHttpServletRequest slingRequest;
	
	@Inject @Source("sling-object") 
	private ResourceResolver resourceResolver;
	
	@OSGiService
	private AdminService adminService;
	
	@Inject
	protected EndecaRequestService endecaRequestService;
	
	@Inject
	protected EndecaService endecaService;
	
	@OSGiService
	private ConfigurationManagerFactory configManagerFctry;
	
	@OSGiService
	private ProductFamilyDetails productFamilyDetails;
    
    @PostConstruct
	protected void init() {
		LOG.debug("ChatConfigModel :: init() :: Start");
		ValueMap properties = currentPage.getProperties();
		// get the current page type (product-family, SKU or other page)
		String pageType = null != properties.get(CommonConstants.PAGE_TYPE)
				? properties.get(CommonConstants.PAGE_TYPE).toString() : null;

		if(null != adminService) {
			try (ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
				Resource currentPageRes = adminReadResourceResolver.resolve(currentPage.getPath());
				
					Configuration configObj = CommonUtil.getCloudConfigObj(configManagerFctry, adminReadResourceResolver,
							currentPageRes, CommonConstants.C_CLOUD_CONFIG_NODE_NAME);
					if (null != configObj) {
						// get configObj for the chatConfig page in the Eaton Cloud Config
						Resource chatConfigRes = adminReadResourceResolver.resolve(configObj.getPath()
								.concat(CommonConstants.SLASH_STRING).concat(CommonConstants.JCR_CONTENT_STR));

						// get configBean using ChatConfigUtil method for getting the configurations
						chatConfigBean = ChatConfigUtil.populateChatList(chatConfigRes);
					}
				
				// Check for the Page type and accordingly fetch the tags and match
				// them with that of the configuration
				if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)) {
					productFamilyDetails = productFamilyDetailService
							.getProductFamilyDetailsBean(currentPage);
					if ((productFamilyDetails != null) && ((productFamilyDetails.getPimTags() != null) && (!(productFamilyDetails.getPimTags().isEmpty())))) {
							String[] pimTags = formatPIMTags(productFamilyDetails.getPimTags(), adminReadResourceResolver);
							chatBean = ChatConfigUtil.compareTags(chatConfigBean, pimTags);
						}
				} else if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)) {
					final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
					final String inventoryId = endecaRequestService.getInventoryId(currentPage, adminReadResourceResolver);
					final EndecaServiceRequestBean endecaRequestBean = endecaRequestService
							.getEndecaRequestBean(currentPage, selectors, inventoryId);
					final SKUDetailsResponseBean skuDetails = endecaService.getSKUDetails(endecaRequestBean);
					final SKUDetailsBean skuDetailsBean = skuDetails.getSkuResponse().getSkuDetails();						productFamilyPIMDetails = productFamilyDetailService
								.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);
						if ((productFamilyPIMDetails != null)&& 
								((productFamilyPIMDetails.getPimTags() != null) && (!(productFamilyPIMDetails.getPimTags().isEmpty())))){
								String[] pimTags = formatPIMTags(productFamilyPIMDetails.getPimTags(), adminReadResourceResolver);
								chatBean = ChatConfigUtil.compareTags(chatConfigBean, pimTags);
							}
					} else {
							currentPage = CommonUtil.getOriginalPage(currentPage, adminReadResourceResolver);
							String[] pageAemTags = (String[]) currentPage.getProperties().get(TagConstants.PN_TAGS);
							chatBean = ChatConfigUtil.compareTags(chatConfigBean, pageAemTags);	
					}
				// if the chatbean data is present then the configuration is matched
				// with the chatConfig object
				if (chatBean != null) {
					showComponent = true;
					LOG.info("in ChatConfigModel activate() method matched :::");
				} else {
					showComponent = false;
					LOG.info("in ChatConfigModel activate() method NOTmatched :::");
				}

			} catch (Exception exception) {
				LOG.error("Exception occured while getting the reader service", exception);
			}
		}

		LOG.debug("ChatConfigModel :: setComponentValues() :: Exit");

	}
	
	/**
	 * This method convert PIM tag to below format-
	 * * /content/cq:tags/eaton/chat/electrical to
	 * eaton:chat/electrical
	 * *
	 * @return the String[]
	 */

	private static String[] formatPIMTags(List<String> tagList, ResourceResolver resourceResolver) {
		String[] pimTags = new String[tagList.size()];
		int index = 0;
		final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		if(null != tagManager){
		 for (String value : tagList) {
		     if(value != null) {
				 Tag tag = tagManager.resolve(value);
				 if(tag != null) {
					 pimTags[index] = tag.getTagID();
					 index++;
				 }
             }
		}
	}
		return pimTags;
	}

	/**
	 * Gets the ChatConfigBean repository.
	 *
	 * @return the ChatConfigBean
	 */
	public ChatConfigBean getChatConfigBean() {
		return   chatConfigBean;
	}

	/**
	 * Gets the ChatBean Matched with Tags.
	 *
	 * @return the ChatBean
	 */
	public ChatBean getChatBean() {
		return chatBean;
	}

	/**
	 * Gets the Boolean for showing the component.
	 *
	 * @return the showComponent
	 */
	public boolean getShowComponent() {
		return showComponent;
	}

}
