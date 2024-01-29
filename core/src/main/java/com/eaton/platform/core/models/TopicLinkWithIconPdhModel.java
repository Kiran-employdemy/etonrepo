package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.List;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ProductFamilyDetails;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.core.bean.TopicLinkWithIconBean;
import com.eaton.platform.core.models.TopicLinkWithIconPdhModel;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.SlingHttpServletRequest;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class TopicLinkWithIconPdhModel {
	
	
	/**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TopicLinkWithIconPdhModel.class);
    
    
    @Inject @Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	@Inject
	protected Page currentPage;
	
	
	@Inject
	protected ProductFamilyDetailService productFamilyDetailService;

	@Inject
	protected EndecaRequestService endecaRequestService;

	@Inject
	protected EndecaService endecaService;
    
	private String title;
	private String icon;
	private String link;
	private String description;
	private String newWindow;
	private List<TopicLinkWithIconPdhModel> topicLinkWithIconPdhModelList;
	private String pageType;
	
	/**
     * Inits the.
     */
    @PostConstruct
    protected void init() {
    	
    	    	
    	LOG.debug("TopicLinkWithIconPdhModel :: init() :: Started");
    	
    	
		
    	if (currentPage.getProperties().get(CommonConstants.PAGE_TYPE) != null) {
			pageType = currentPage.getProperties().get(CommonConstants.PAGE_TYPE).toString();
		}
		
		if (pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE)){
			ProductFamilyDetails pfDetails = productFamilyDetailService.getProductFamilyDetailsBean(currentPage);
			
			if (pfDetails != null) {
				
				if (pfDetails.getTopicLinkWithIconList() != null) {
					setTopicLinkWithIconPdhModelList(pfDetails.getTopicLinkWithIconList());
				}
			} else {
				LOG.debug("Product Family object is null");
			}
		} else if(pageType != null && pageType.equals(CommonConstants.PAGE_TYPE_PRODUCT_SKU_PAGE)){
            final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
            final EndecaServiceRequestBean endecaRequestBean = endecaRequestService
                .getEndecaRequestBean(currentPage, selectors, StringUtils.EMPTY);
            final SKUDetailsResponseBean skuDetails = endecaService.getSKUDetails(endecaRequestBean);
            final SKUDetailsBean skuDetailsBean = skuDetails.getSkuResponse().getSkuDetails();
            final ProductFamilyPIMDetails pfPimDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);
			if(pfPimDetails != null && null != pfPimDetails.getTopicLinkWithIconList()) {
				setTopicLinkWithIconPdhModelList(pfPimDetails.getTopicLinkWithIconList());
				
			} else {
				LOG.debug("TopicLinkWithIconList object is null");
			}            
		}
		LOG.debug(" TopicLinkWithIconPdhModel :: int() :: Exit");
	}
    
    
    private void setTopicLinkWithIconPdhModelList(List<TopicLinkWithIconBean> topicLinkWithIconList){
    	
    	LOG.debug("TopicLinkWithIconPdhModel :: setTopicLinkWithIconPdhModelList() :: Started");
    	
    	topicLinkWithIconPdhModelList = new ArrayList<TopicLinkWithIconPdhModel>();
    	for (TopicLinkWithIconBean item : topicLinkWithIconList) {
			TopicLinkWithIconPdhModel topicLinkWithIconPdhModel = new TopicLinkWithIconPdhModel();
			topicLinkWithIconPdhModel.setTitle(item.getTitle());
			topicLinkWithIconPdhModel.setDescription((item.getDescription()));
			topicLinkWithIconPdhModel.setIcon(item.getIcon());
			topicLinkWithIconPdhModel.setNewWindow(item.getNewWindow() != null ? CommonConstants.TARGET_BLANK : CommonConstants.TARGET_SELF);

			if (CommonUtil.getIsExternal(item.getLink())) {
				topicLinkWithIconPdhModel.setLink(item.getLink());
			} else {
				topicLinkWithIconPdhModel.setLink(CommonUtil.dotHtmlLink(item.getLink()));
			}

			topicLinkWithIconPdhModelList.add(topicLinkWithIconPdhModel);
		}
    	LOG.debug("TopicLinkWithIconPdhModel :: setTopicLinkWithIconPdhModelList() :: End");
    }
    /**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the newWindow
	 */
	public String getNewWindow() {
		return newWindow;
	}
	/**
	 * @param newWindow the newWindow to set
	 */
	public void setNewWindow(String newWindow) {
		this.newWindow = newWindow;
	}
	
	
    public List<TopicLinkWithIconPdhModel> getTopicLinkWithIconPdhModelList() {
		return topicLinkWithIconPdhModelList;
	}
}
