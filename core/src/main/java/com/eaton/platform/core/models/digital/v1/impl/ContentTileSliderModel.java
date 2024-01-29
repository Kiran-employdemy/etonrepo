package com.eaton.platform.core.models.digital.v1.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.PIMResourceSlingModel;
import com.eaton.platform.core.models.digital.v1.ContentService;
import com.eaton.platform.core.models.digital.v1.ContentServiceBean;
import com.eaton.platform.core.models.digital.v1.ContentTileButtonMultiField;
import com.eaton.platform.core.models.digital.v1.ManualListModel;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.util.CommonUtil;

@Model(adaptables = Resource.class, 
defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
resourceType="core/content-tile-slider/v1/content-tile-slider")

public class ContentTileSliderModel {
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ContentTileSliderModel.class);

	private static final String MAX_LIMIT_DISPLAY = "maxLimitToDisplay";
	
	@Inject 
	private String  titleText;

	@Inject 
	private String  subtitle;

	@Inject  @Default(values = "false")
	private String ctaApplyNoFollowTag;
	
	@Inject
	private String ctaColor;

	@Inject @Source("sling-object")
	private ResourceResolver resourceResolver;

	@Inject
    private String listType;
	   
    @Inject
    private String[] tags;

	@Inject
	private String parentPage;
	   
    @Inject
	private Resource manualLinkList;
	   
	@Inject
	private String alignmentDesktop;

	@Inject
	private String alignmentMobile;
	    
	@Inject
	private String mobileTrans;
	
	@Inject
	 private String desktopTrans;

	@Self
	protected Resource resource;
	 
	private int maxLimitToDisplay;

	 private List<ContentServiceBean> childPageAndTagsLinksList;
	 protected String tag;
	 
	 /** The manual links list. */
	 private List<ManualListModel> manualLinksList;

	@PostConstruct
	protected void init() {
		ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
		if(null != contentPolicyManager){
			 // first we check the component policy
			 ContentPolicy componentPolicy = contentPolicyManager.getPolicy(resource);
			 if(componentPolicy!=null) {
			 maxLimitToDisplay = componentPolicy.getProperties().get(MAX_LIMIT_DISPLAY,CommonConstants.FIFTY);
			 }
		}
		//then we check if there is a override max limit
		if(resource.getValueMap().containsKey(MAX_LIMIT_DISPLAY)){
			this.maxLimitToDisplay = resource.getValueMap().get(MAX_LIMIT_DISPLAY,CommonConstants.FIFTY);
		}

		if ("true".equalsIgnoreCase(ctaApplyNoFollowTag)) {
			tag = "nofollow";
		} else {
			tag = "follow";
		}
		try {
             if (StringUtils.isNotBlank(listType)) {
			    switch (listType) {
			    	case CommonConstants.TAGS:
				    setTagsModel();
				    break;

			    	case CommonConstants.MANUAL_LIST:
				    setManualLinkModel();
				    break;
				    
			    	default:
				    break;
			    }
             } else {
            	 LOG.debug("ContentTileSliderFrame ::init():: listType is blank");
             }
		}catch(Exception e) {
			LOG.debug("ContentTileSliderFrame Error: {}" ,e.getMessage(),e);
		}
		LOG.debug("ContentTileSliderFrame :: init() :: Exited");
	}

	private void setManualLinkModel(){
		LOG.debug("ContentTileSliderFrame :: setManualLinkModel() :: Started");
		manualLinksList = new ArrayList<>();
		if (null != manualLinkList) {
			populateManualLinkModel(manualLinksList, manualLinkList);
		}
		LOG.debug("ContentTileSliderFrame :: setManualLinkModel() :: Exited");
	}
	
	public List<ManualListModel> populateManualLinkModel(List<ManualListModel> manualLinksList, Resource resource) {
		LOG.debug("ContentTileSliderFrame :: populateManualLinkModel() :: Started");
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				ManualListModel manualLink = linkResources.next().adaptTo(ManualListModel.class);
				if (null != manualLink){
					manualLink.setDesktopTransformedUrl(getDesktopTrans());
					manualLink.setMobileTransformedUrl(getMobileTrans());
					manualLinksList.add(manualLink);
				}
			}
		}
		LOG.debug("ContentTileSliderFrame :: populateManualLinkModel() :: Exited");
		return manualLinksList;
	}
	
	private void setTagsModel(){
		LOG.debug("ContentTileSliderFrame :: setTagsModel() :: Started");
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != tags) {
			populateTagsModel(childPageAndTagsLinksList, tags, resourceResolver,parentPage);
		}
		LOG.debug("ContentTileSliderFrame :: setTagsModel() :: Exited");
	}
	 
	public List<ContentServiceBean> populateTagsModel(List<ContentServiceBean> childPageAndTagsLinksList,
			   String[] tags, ResourceResolver resourceResolver, String parentPage) {
		LOG.debug("ContentTileSliderFrame :: populateTagsModel() :: Started");
		Page page = null != resourceResolver.adaptTo(PageManager.class) ? 
				resourceResolver.adaptTo(PageManager.class).getPage(parentPage): null;
		Iterator<Page> pageList =  null != page ? page.listChildren(): null;
		if(page != null && pageList != null){
			if(page.getAbsoluteParent(5) != null) {
				setPageListForTagsModel(pageList,tags);
			}else {
					while (pageList.hasNext()) {
						Page levelOnePage = pageList.next();
						Iterator<Page> levelOnePageList =  null != levelOnePage ? levelOnePage.listChildren(): null;
						setPageListForTagsModel(levelOnePageList,tags);
					}	
			}
		}
		LOG.debug("ContentTileSliderFrame :: populateTagsModel() :: Exited");
		return childPageAndTagsLinksList;
	}
	
	private void setPageListForTagsModel(Iterator<Page> pageList,String[] tags) {
		LOG.debug("ContentTileSliderFrame :: setPageListForTagsModel() :: Started");
		if (pageList != null) {
			while (pageList.hasNext()) {
				Resource pageContentRes = pageList.next().getContentResource();
				if (null != pageContentRes && CommonUtil.startsWithAnySiteContentRootPath(pageContentRes.getPath())) {
					setTagFields(pageContentRes, tags);
				}
			}
		}
		LOG.debug("ContentTileSliderFrame :: setPageListForTagsModel() :: Exited");
	}
	
	private void setTagFields(Resource tagResource, String[] tags){
		LOG.debug("ContentTileSliderFrame :: setTagFields() :: Started");
        int productTimeToLive=0;
		ValueMap pageProperties = tagResource.getValueMap();
		String linkPath = tagResource.getPath();
		if(StringUtils.equals(StringUtils.EMPTY, CommonUtil.getStringProperty(pageProperties, NameConstants.PN_HIDE_IN_NAV)) &&
				StringUtils.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE, CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_TYPE))
		        && CommonUtil.checkProductLifeSpanIsWithinThePeriod(tagResource,productTimeToLive)) {
			String pimPagePath = CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_PIM_PATH);
			if(StringUtils.isNotBlank(pimPagePath)) {
				final Resource pimPagePathResource = resourceResolver.getResource(pimPagePath);
				if(null != pimPagePathResource) {
					setFields(childPageAndTagsLinksList, pimPagePathResource,linkPath, tags);
				}
			}
		}
		LOG.debug("ContentTileSliderFrame :: setTagFields() :: Exited");
	}

	private void setFields(List<ContentServiceBean> relatedProductsPDHBeanList, final Resource pimPagePathResource, String linkPath, String[] tags) {
		LOG.debug("ContentTileSliderFrame :: setFields() :: Started");
		final PIMResourceSlingModel pimResourceSlingModel = pimPagePathResource.adaptTo(PIMResourceSlingModel.class);
		String[] pimTags =  null != pimResourceSlingModel ? pimResourceSlingModel.getTags(): ArrayUtils.EMPTY_STRING_ARRAY;
		boolean contains = false;
		for (String ptag : tags) {
			if(null != pimTags) {
				contains = Arrays.stream(pimTags).anyMatch(ptag::equals);
				if (contains) {
					break;
				}
			}
		}
        if(contains && null != pimResourceSlingModel) {
			setPIMTagFields(relatedProductsPDHBeanList,pimPagePathResource,linkPath,pimResourceSlingModel);
		}
        LOG.debug("ContentTileSliderFrame :: setFields() :: Exited");
	}

	private void setPIMTagFields(List<ContentServiceBean> relatedProductsPDHBeanList, Resource pimPagePathResource,
				String linkPath,PIMResourceSlingModel pimResourceSlingModel) {
		final ValueMap pimPageProperties = pimPagePathResource.getValueMap();
		final ContentServiceBean contentTileSliderBean = new ContentServiceBean();
		
		String imagePath = StringUtils.isNotBlank(pimResourceSlingModel.getPrimaryImage()) ?
			      pimResourceSlingModel.getPrimaryImage() : pimResourceSlingModel.getPdhPrimaryImg();
	    contentTileSliderBean.setLinkImage(imagePath);
		contentTileSliderBean.setLinkImageAltText(CommonUtil.getAssetAltText(resourceResolver, imagePath));
		
		contentTileSliderBean.setLinkTitle(StringUtils.isNotBlank(pimResourceSlingModel.getProductName()) ? 
				pimResourceSlingModel.getProductName() : pimResourceSlingModel.getPdhProdName());
		
		if (null != pimResourceSlingModel.getPrimarySubCategory()) {
			contentTileSliderBean.setLinkEyebrow(StringUtils.isNoneEmpty(
					CommonUtil.getLinkTitle(null, pimResourceSlingModel.getPrimarySubCategory(), resourceResolver)) ?
					CommonUtil.getLinkTitle(null, pimResourceSlingModel.getPrimarySubCategory(), resourceResolver) : StringUtils.EMPTY);
		}

		contentTileSliderBean.setDescription(StringUtils.isNoneEmpty(CommonUtil.getStringProperty(pimPageProperties,CommonConstants.PIM_MARKETING_DESCRIPTION)) 
				?CommonUtil.getStringProperty(pimPageProperties,CommonConstants.PIM_MARKETING_DESCRIPTION) :
					CommonUtil.getStringProperty(pimPageProperties,CommonConstants.PIM_PDH_DESCRIPTION));
		
		linkPath = StringUtils.removeEnd(linkPath, CommonConstants.SLASH_STRING.concat(JcrConstants.JCR_CONTENT));
		contentTileSliderBean.setLinkPath(CommonUtil.dotHtmlLink(linkPath, resourceResolver));
		
		Page page = null != resourceResolver.adaptTo(PageManager.class) ? resourceResolver.adaptTo(PageManager.class).getPage(linkPath): null;		
		String butNowCtaLink=pimResourceSlingModel.getPrimaryCTAURL(page);
		ContentTileButtonMultiField ctaButton = new ContentTileButtonMultiField();
		if(butNowCtaLink!=null){
			ctaButton.setCtaLinkPath(CommonUtil.dotHtmlLink(butNowCtaLink, resourceResolver));
		}
		String buyNowCtaLabel=pimResourceSlingModel.getPrimaryCTALabel(page);
		ctaButton.setCtaLinkTitle(buyNowCtaLabel);
		contentTileSliderBean.addToCtaButtons(ctaButton);
		
		contentTileSliderBean.setDesktopTransformedUrl(getDesktopTrans());
		contentTileSliderBean.setMobileTransformedUrl(getMobileTrans());
	
		relatedProductsPDHBeanList.add(contentTileSliderBean);
		
	}

	public String getListType() {
		if(null == listType) {
			listType = StringUtils.EMPTY;
		}
		return listType;
	}
	
	public List<ContentService> getListTypeArray() {
		LOG.debug("ContentTileSliderFrame :: getListTypeArray() :: Started");
		List<ContentService> viewList = new ArrayList<>();
		//since there is only 2 condtion we can use if/else instead of if/elseif
		boolean isTag = StringUtils.equals(CommonConstants.TAGS, getListType());
		if(isTag) {
			if(maxLimitToDisplay > childPageAndTagsLinksList.size()) {
				maxLimitToDisplay = childPageAndTagsLinksList.size();
			}
			List<ContentServiceBean> prodBeanList = childPageAndTagsLinksList.subList(0, maxLimitToDisplay);
			for (ContentServiceBean prodBean : prodBeanList) {
				viewList.add(prodBean);				
			}
			if(viewList.isEmpty()){ 
				populateTagsfromPage(viewList);
			}
		}else {
			for (ManualListModel mlModel : manualLinksList) {
				viewList.add(mlModel);
			}			
		}
		LOG.debug("ContentTileSliderFrame :: getListTypeArray() :: Exited");
		return viewList;
	}
	
	private void populateTagsfromPage(List<ContentService> viewList) {
		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		ValueMap pageProperties;
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page currentPage = null != pageManager ? pageManager.getContainingPage(resource) : null;
		if(null != tagManager && null !=currentPage && null != ( pageProperties = currentPage.getProperties()) ){
			String[] pageTags= pageProperties.get(CommonConstants.CQ_TAGS,new String[0]);
			RangeIterator<Resource> tagsIt = tagManager.find(CommonUtil.getHomePagePath(currentPage), pageTags, Boolean.FALSE);
			if(null != tagsIt) {
				while (tagsIt.hasNext()) {
					Resource tagResource = tagsIt.next();
					if(null != tagResource && CommonUtil.startsWithAnySiteContentRootPath(tagResource.getPath())){
						ValueMap resourceProperties = tagResource.getValueMap();
						setFields(viewList, tagResource, resourceProperties);
					}
				}
			}
		}
	}

	private void setFields(List<ContentService> viewList, Resource tagResource, ValueMap pageProperties) {
		final ContentServiceBean contentTileSliderBean = new ContentServiceBean();		
		String imagePath = (!CommonUtil.getStringProperty(pageProperties, CommonConstants.TEASER_IMAGE_PATH).equals(StringUtils.EMPTY)) ? CommonUtil.getStringProperty(pageProperties, CommonConstants.TEASER_IMAGE_PATH) : "";
		String teaserTitle = CommonUtil.getStringProperty(pageProperties, CommonConstants.TEASER_TITLE);
		String linkPath=StringUtils.removeEnd(tagResource.getPath(), CommonConstants.SLASH_STRING.concat(JcrConstants.JCR_CONTENT));
		contentTileSliderBean.setLinkPath(CommonUtil.dotHtmlLink(linkPath, resourceResolver));
		contentTileSliderBean.setLinkImage(imagePath);
		contentTileSliderBean.setLinkImageAltText(CommonUtil.getAssetAltText(resourceResolver, imagePath));
		contentTileSliderBean.setEyebrowTitleLink(StringUtils.isNotBlank(teaserTitle) ? teaserTitle : CommonUtil.getLinkTitle(null, StringUtils.removeEnd(tagResource.getPath(), JcrConstants.JCR_CONTENT), resourceResolver));
		contentTileSliderBean.setDesktopTransformedUrl(getDesktopTrans());
		contentTileSliderBean.setMobileTransformedUrl(getMobileTrans());
		contentTileSliderBean.setDescription(CommonUtil.getStringProperty(pageProperties,"teaser-description"));
		viewList.add(contentTileSliderBean);
	}
	 
	/**
	 * Gets the checks if is eligible for carousel.
	 *
	 * @return the checks if is eligible for carousel
	 */
	public boolean getIsEligibleForCarousel() {
		boolean isCarousel = false;
		if(getListTypeArray().size() > CommonConstants.THREE) {
			isCarousel = true;
		}
		return isCarousel;
	}
	
	public String getTitleText() {
		return titleText;
	}
	
	public String getSubtitle() {
		return subtitle;
	}

	public String getCTAColor(){
		return ctaColor;
	}
	
	public String getApplyNoFollowTag() {
		ctaApplyNoFollowTag = tag;
		return ctaApplyNoFollowTag;
	}

    public String getAlignmentDesktop() {
        return alignmentDesktop;
    }

    public String getAlignmentMobile() {
        return alignmentMobile;
    }
    
    public String getMobileTrans() {
        if (null == mobileTrans) {
            mobileTrans = StringUtils.EMPTY;
        }
        return mobileTrans;
    }

    public String getDesktopTrans() {
        if (null == desktopTrans) {
            desktopTrans = StringUtils.EMPTY;
        }
        return desktopTrans;
    }
}

