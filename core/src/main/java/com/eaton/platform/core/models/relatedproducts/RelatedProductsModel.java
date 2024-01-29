package com.eaton.platform.core.models.relatedproducts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.RelatedProductsBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.RelatedProductsService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in RelatedProductsHelper class. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RelatedProductsModel  {
	
	
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(RelatedProductsModel.class);

	/** The Constant SIMPLE_DATE_FORMAT. */
	private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	/** The Constant DATE_FORMAT_PUBLISH. */
    private static final  String DATE_FORMAT_PUBLISH = "MMMM d, yyyy HH:mm:ss.SSSXXX";
	
	/** The Constant ANY. */
	private static final String ANY = "any";
	
	/** The manual links list. */
	private List<ManualLinksModel> manualLinksList;
	
	/** The fixed links list. */
	private List<FixedLinksModel> fixedLinksList;
	
	/** The child page links list. */
	private List<RelatedProductsBean> childPageAndTagsLinksList;
	
	/** The products header. */
	@Inject
	private String productsHeader;
	
	/** The products description. */
	@Inject
	private String productsDescription;
	
	/** The list type. */
	@Inject
	private String listType;
	
	/** The parent page. */
	@Inject
	private String parentPage;
	
	/** The tags. */
	@Inject
	private String[] tags;
	
	/** The tags type. */
	@Inject
	private String tagsType;
	
	/** The open new window. */
	@Inject
	private String openNewWindow;
	
	/** The fixed links. */
	@Inject
	private Resource fixedLinks;
	
	/** The manual link list. */
	@Inject
	private Resource manualLinkList;
		
	/** The sort by. */
	@Inject
	private String sortBy;

	/** The maxLimitToDisplay. */
	@Inject
	private int maxLimitToDisplay;

	/** The desktop trans. */
	@Inject
	private String desktopTrans;
	
	/** The mobile trans. */
	@Inject
	private String mobileTrans;
	
    /** The resource resolver. */
	@Inject	@Source("sling-object")
	private ResourceResolver resourceResolver;


    /** The resource page. */
    @Inject
	private Page resourcePage;
	
   
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		// get resource resolver object from admin service		
		LOG.debug("RelatedProductsModel :: Init() :: Started");	
		switch(getListType()) {
			case CommonConstants.CHILD_PAGES :
				setParentModel();
				break;
			case CommonConstants.TAGS :
				setTagsModel();
				break;
			case CommonConstants.FIXED_LIST :
				setFixedLinkModel();
				break;
			case CommonConstants.MANUAL_LIST :
				setManualLinkModel();
				break;

			default :
				break;
		}

		LOG.debug("RelatedProductsModel :: Init() :: Exit");
	}
	/** Calls populateParentModel if ListType is Child Page. */
	
	private void setParentModel(){
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != parentPage) {
			populateParentModel(childPageAndTagsLinksList, parentPage, resourceResolver);
			// sort the list when author has either selected Parent Page
			getSortedList(childPageAndTagsLinksList);
		}
	}
	

	/** Calls populateTagsModel if ListType is Tags. */
	private void setTagsModel(){
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != tags) {
			populateTagsModel(childPageAndTagsLinksList, tags, resourceResolver);
			// sort the list when author has either selected Tag
			getSortedList(childPageAndTagsLinksList);
		}
	}
	

	/** Calls populateFixedLinkModel if ListType is Fixed-List. */
	private void setFixedLinkModel(){
		fixedLinksList = new ArrayList<>();
		if (null != fixedLinks) {
			populateFixedLinkModel(fixedLinksList, fixedLinks);
		}
	}
	
	/** Calls populateFixedLinkModel if ListType is Fixed-List. */
	private void setManualLinkModel(){
		manualLinksList = new ArrayList<>();
		if (null != manualLinkList) {
			populateManualLinkModel(manualLinksList, manualLinkList);
		}
	}
	
	/**
	 * Populate parent model.
	 *
	 * @param childPageAndTagsLinksList the child page and tags links list
	 * @param parentPagePath the parent page path
	 * @param resourceResolver
	 * @return the list
	 */
	public List<RelatedProductsBean> populateParentModel(List<RelatedProductsBean> childPageAndTagsLinksList,
														 String parentPagePath, ResourceResolver resourceResolver) {
		if (StringUtils.equals(CommonConstants.CHILD_PAGES, getListType())) {
		    Resource parentResource = resourceResolver.getResource(parentPagePath);
			if(null != parentResource){
				Iterator<Resource> childrenResource = parentResource.listChildren();
				while(childrenResource.hasNext()){
					Resource childResource = childrenResource.next();
					if(StringUtils.equals(CommonUtil.getStringProperty(childResource.getValueMap(), JcrConstants.JCR_PRIMARYTYPE), NameConstants.NT_PAGE)){
						Resource jcrResource = childResource.getChild(JcrConstants.JCR_CONTENT);
						if(null != jcrResource) {
							ValueMap pageProperties = jcrResource.getValueMap();
							setFields(childPageAndTagsLinksList, childResource, pageProperties, resourceResolver);
						}
					}
				}
			}
		}
		return childPageAndTagsLinksList;
	}

	/**
	 * Populate tags model.
	 *
	 * @param childPageAndTagsLinksList the child page and tags links list
	 * @param tags the tags
	 * @param resourceResolver
	 * @return the list
	 */
	public List<RelatedProductsBean> populateTagsModel(List<RelatedProductsBean> childPageAndTagsLinksList,
													   String[] tags, ResourceResolver resourceResolver) {
		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		if(null != tagManager) {
			boolean oneMatchIsEnough = StringUtils.equals(ANY, this.tagsType);
	        RangeIterator<Resource> tagsIt = tagManager.find(CommonUtil.getHomePagePath(resourcePage), tags, oneMatchIsEnough);
	        
	        if(null != tagsIt) {
	            while (tagsIt.hasNext()) { 
	                Resource tagResource = tagsIt.next(); 
	    			if(null != tagResource && CommonUtil.startsWithAnySiteContentRootPath(tagResource.getPath())){
    					ValueMap pageProperties = tagResource.getValueMap();
    					setFields(childPageAndTagsLinksList, tagResource, pageProperties, resourceResolver);
    					}
	    			}
	            }
	        }
		
		return childPageAndTagsLinksList;
	}
	
	 /**
	 * Populate fixed link model.
	 *
	 * @param fixedLinksList the fixed links list
	 * @param resource the resource
	 * @return the list
	 */
	public List<FixedLinksModel> populateFixedLinkModel(List<FixedLinksModel> fixedLinksList, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				FixedLinksModel fixedLink = linkResources.next().adaptTo(FixedLinksModel.class);
				if (null != fixedLink){
					fixedLink.setDesktopTransformedUrl(getDesktopTrans());
					fixedLink.setMobileTransformedUrl(getMobileTrans());
					fixedLinksList.add(fixedLink);
				}
			}
		}
		return fixedLinksList;
	}
	
	 /**
 	 * Populate manual link model.
 	 *
 	 * @param manualLinksList the manual links list
 	 * @param resource the resource
 	 * @return the list
 	 */
	public List<ManualLinksModel> populateManualLinkModel(List<ManualLinksModel> manualLinksList, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				ManualLinksModel manualLink = linkResources.next().adaptTo(ManualLinksModel.class);
				if (null != manualLink){
					manualLink.setDesktopTransformedUrl(getDesktopTrans());
					manualLink.setMobileTransformedUrl(getMobileTrans());
					manualLinksList.add(manualLink);
				}
			}
		}
		return manualLinksList;
	}

	/**
	 * Sets the fields.
	 *  @param array the array
	 * @param pageResource the page resource
	 * @param properties the properties
	 * @param resourceResolver
	 */
	private void setFields(List<RelatedProductsBean> array, Resource pageResource,
						   ValueMap properties, ResourceResolver resourceResolver) {
		RelatedProductsBean relatedProductsBean = new RelatedProductsBean();
		SimpleDateFormat simpleDate = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
		SimpleDateFormat publicationDateFormat = new SimpleDateFormat(DATE_FORMAT_PUBLISH);
		if(StringUtils.equals(StringUtils.EMPTY, CommonUtil.getStringProperty(properties, NameConstants.PN_HIDE_IN_NAV))){
			
			String imagePath = CommonUtil.getStringProperty(properties, CommonConstants.TEASER_IMAGE_PATH);
			String teaserTitle = CommonUtil.getStringProperty(properties, CommonConstants.TEASER_TITLE);
			String linkPathWithExtension = CommonUtil.dotHtmlLink(StringUtils.removeEnd(pageResource.getPath(), JcrConstants.JCR_CONTENT));
			relatedProductsBean.setLinkImage(imagePath);
			relatedProductsBean.setDesktopTransformedUrl(getDesktopTrans());
			relatedProductsBean.setMobileTransformedUrl(getMobileTrans());
			relatedProductsBean.setLinkTitle(StringUtils.isNotBlank(teaserTitle) ? teaserTitle : CommonUtil.getLinkTitle(null, StringUtils.removeEnd(pageResource.getPath(), JcrConstants.JCR_CONTENT), resourceResolver));
			relatedProductsBean.setLinkPath(CommonUtil.dotHtmlLink(linkPathWithExtension,resourceResolver));
			relatedProductsBean.setLinkEyebrow(CommonUtil.getStringProperty(properties, CommonConstants.EYEBROW_TITLE));
			relatedProductsBean.setLinkImageAltText(CommonUtil.getAssetAltText(resourceResolver, imagePath));
			relatedProductsBean.setPublicationDate(CommonUtil.getDateProperty(properties, CommonConstants.PUBLICATION_DATE, publicationDateFormat));
			relatedProductsBean.setReplicationDate(CommonUtil.getDateProperty(properties, NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat));
			relatedProductsBean.setLastModifiedDate(CommonUtil.getDateProperty(properties, NameConstants.PN_PAGE_LAST_MOD, simpleDate));
			relatedProductsBean.setCreatedDate(CommonUtil.getDateProperty(properties, CommonConstants.JCR_CREATED, simpleDate));
			relatedProductsBean.setTemplateName(CommonUtil.getStringProperty(properties, CommonConstants.TEMPLATE_PROP_KEY));
			relatedProductsBean.setNewWindow(getOpenNewWindow());
			
			array.add(relatedProductsBean);
		}
	}

	/**
	 * this method is sorting the list based on the author selection.
	 *
	 * @param list the list
	 * @return the sorted list
	 */
	@SuppressWarnings("squid:S1604")
	private void getSortedList(List<RelatedProductsBean> list) {
		Collections.sort(list, new Comparator<RelatedProductsBean>() {
      public int compare(RelatedProductsBean list1, RelatedProductsBean list2) {
          int comparisonValue = 0;
	      switch(sortBy) {
			case CommonConstants.TITLE :
                  comparisonValue = list1.getLinkTitle().compareToIgnoreCase(list2.getLinkTitle());
				  break;
				  case CommonConstants.PUBLISH_DATE :
				  comparisonValue=CommonUtil.compareDate(CommonUtil.getDateFormat(DATE_FORMAT_PUBLISH,list1.getPublicationDate()), CommonUtil.getDateFormat(DATE_FORMAT_PUBLISH,list1.getPublicationDate()));
				  break;
			case CommonConstants.CREATED_DT :
				  comparisonValue = -1 * (list1.getCreatedDate().compareTo(list2.getCreatedDate()));
				  break;
			case CommonConstants.LAST_MOD_DT :
				  comparisonValue = -1 * (list1.getLastModifiedDate().compareTo(list2.getLastModifiedDate()));
				  break;
			case CommonConstants.TEMPLATE :
				  comparisonValue = list1.getTemplateName().compareToIgnoreCase(list2.getTemplateName());
				  break;
			default :
				  comparisonValue = 0;
		 }
          return comparisonValue;
		}
		});
	}

	/**
	 * Gets the list type array.
	 *
	 * @return the list type array
	 */
	public List<RelatedProductsService> getListTypeArray() {
		List<RelatedProductsService> viewList = new ArrayList<>();
		if(StringUtils.equals(CommonConstants.CHILD_PAGES, getListType()) || StringUtils.equals(CommonConstants.TAGS, getListType())) {
			if(maxLimitToDisplay > childPageAndTagsLinksList.size()) {
				maxLimitToDisplay = childPageAndTagsLinksList.size();
			}
			List<RelatedProductsBean> prodBeanList = childPageAndTagsLinksList.subList(0, maxLimitToDisplay);
			for (RelatedProductsBean prodBean : prodBeanList) {
				viewList.add(prodBean);
			}		
		} else if(StringUtils.equals(CommonConstants.FIXED_LIST, getListType())) {
			for (FixedLinksModel flModel : fixedLinksList) {
				viewList.add(flModel);
			}
			
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, getListType())) {			
			for (ManualLinksModel mlModel : manualLinksList) {
				viewList.add(mlModel);
			}			
		}
		return viewList;
	}
	 
	/**
	 * Gets the checks if is eligible for carousel.
	 *
	 * @return the checks if is eligible for carousel
	 */
	public boolean getIsEligibleForCarousel() {
		boolean isCarousel = false;
		if(getListTypeArray().size() > 4) {
			isCarousel = true;
		}
		return isCarousel;
	}
	/**
	 * Gets the products header.
	 *
	 * @return the products header
	 */
	public String getProductsHeader() {
		return productsHeader;
	}
	
	

	/**
	 * Gets the products Description.
	 * @return the productsDescription
	 */
	public String getProductsDescription() {
		return productsDescription;
	}

	/**
	 * Gets the list type.
	 *
	 * @return the list type
	 */
	public String getListType() {
		if(null == listType) {
			listType = StringUtils.EMPTY;
		}
		return listType;
	}

	/**
	 * Gets the parent page.
	 *
	 * @return the parent page
	 */
	public String getParentPage() {
		return parentPage;
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public String[] getTags() {
		return tags;
	}

	/**
	 * Gets the tags type.
	 *
	 * @return the tags type
	 */
	public String getTagsType() {
		return tagsType;
	}

	/**
	 * Gets the open new window.
	 *
	 * @return the open new window
	 */
	public String getOpenNewWindow() {
		String newWindowOpen = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, openNewWindow) ) {
			newWindowOpen = CommonConstants.TARGET_BLANK;
		}
		return newWindowOpen;
	}
	
	
	/**
	 * Gets the fixed links.
	 *
	 * @return the fixed links
	 */
	public Resource getFixedLinks() {
		return fixedLinks;
	}

	/**
	 * Gets the sort by.
	 *
	 * @return the sort by
	 */
	public String getSortBy() {
		return sortBy;
	}

	/**
	 * Gets the max limit to display.
	 *
	 * @return the max limit to display
	 */
	public int getMaxLimitToDisplay() {
		return maxLimitToDisplay;
	}

	/**
	 * Gets the mobile trans.
	 *
	 * @return the mobileTrans
	 */
	public String getMobileTrans() {
		if(null == mobileTrans) {
			mobileTrans = StringUtils.EMPTY;
		}
		return mobileTrans;
	}
	
	/**
	 * Gets the desktop trans.
	 *
	 * @return the desktop trans
	 */
	public String getDesktopTrans() {
		if(null == desktopTrans) {
			desktopTrans = StringUtils.EMPTY;
		}
		return desktopTrans;
	}
}
