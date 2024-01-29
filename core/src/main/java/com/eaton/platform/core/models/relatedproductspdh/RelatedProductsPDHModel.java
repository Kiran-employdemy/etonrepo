package com.eaton.platform.core.models.relatedproductspdh;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.eaton.platform.core.models.PIMResourceSlingModel;
import com.eaton.platform.core.util.ResourceUtil;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.RelatedProductsPDHBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.RelatedProductsService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in RelatedProductsPDHHelper class. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RelatedProductsPDHModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(RelatedProductsPDHModel.class);

	/** The Constant SIMPLE_DATE_FORMAT. */
	private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** The Constant DATE_FORMAT_PUBLISH. */
    private static final  String DATE_FORMAT_PUBLISH = "MMMM d, yyyy HH:mm:ss.SSSXXX";

	/** The fixed links list. */
	private List<FixedLinksModel> fixedLinksList;

	/** The child page links list. */
	private List<RelatedProductsPDHBean> childPageAndTagsLinksList;

	/** The header. */
	@Inject @Via("resource") @Default(values = "RelatedProducts")
	private String header;

	/** The products description. */
	@Inject @Via("resource")
	private String productsDescription;

	/** The list type. */
	@Inject @Via("resource")
	private String listType;

	/** The parent page. */
	@Inject @Via("resource")
	private String parentPage;

	/** The tags. */
	@Inject @Via("resource")
	private String[] tags;

	/** The tags type. */
	@Inject @Via("resource")
	private String tagsType;

	/** The open new window. */
	@Inject @Via("resource")
	private String openNewWindow;

	/** The fixed links. */
	@Inject @Via("resource")
	private Resource fixedLinks;

	/** The sort by. */
	@Inject @Via("resource")
	private String sortBy;

	/** The maxLimitToDisplay. */
	@Inject @Via("resource")
	private int maxLimitToDisplay;

	@Inject @Via("resource")
	private int productTimeToLive;

	/** The desktop trans. */
	@Inject @Via("resource")
	private String desktopTrans;

	/** The mobile trans. */
	@Inject @Via("resource")
	private String mobileTrans;

	@Inject @Via("resource")
	private String showMarketingDescription;

	/** The enable page description. */
	@Inject @Via("resource")
	private String pageDescription;

	/** The view all products link label. */
	@Inject @Default(values="View all new product launches")  @Via("resource")
	private String viewAllProductsLinkLabel;

	/** The view all products link. */
	@Inject @Via("resource")
	private String viewAllProductsLink;

	/** The enable readMore link. */
	@Inject @Via("resource")
	private String enableReadMoreLink;

	/** The readMore link label. */
	@Inject @Via("resource")
	private String readMoreLinkLabel;

	/** AuthorizationService **/
	@Inject
	private AuthorizationService authorizationService;

	@Inject @Source("sling-object")
	private SlingHttpServletRequest slingRequest;

    /** The resource resolver. */
    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

    /**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.debug("Related Products PDH :: Init() :: Started");
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

			default :
				break;
		}
		LOG.debug("Related Products PDH :: Init() :: Exit");
	}

	/** Calls populateParentModel if ListType is Child Page. */

	private void setParentModel(){
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != parentPage) {
			populateParentModel(childPageAndTagsLinksList, parentPage);
			// sort the list when author has either selected Parent Page
			getSortedList(childPageAndTagsLinksList);
		}
	}

	/** Calls populateTagsModel if ListType is Tags. */
	private void setTagsModel(){
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != tags) {
			populateTagsModel(childPageAndTagsLinksList, tags, parentPage);
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
	/**
	 * Populate parent model.
	 *
	 * @param childPageAndTagsLinksList the child page and tags links list
	 * @param parentPagePath the parent page path
	 * @return the list
	 */
	public List<RelatedProductsPDHBean> populateParentModel(List<RelatedProductsPDHBean> childPageAndTagsLinksList, String parentPagePath) {
		final Resource parentResource = resourceResolver.getResource(parentPagePath);
		if(null != parentResource){
			Iterator<Resource> childrenResource = parentResource.listChildren();
			while(childrenResource.hasNext()){
				Resource childResource = childrenResource.next();
				String linkPath = childResource.getPath();
				if(StringUtils.equals(CommonUtil.getStringProperty(childResource.getValueMap(), JcrConstants.JCR_PRIMARYTYPE), NameConstants.NT_PAGE)){
					final Resource childPageResource = childResource.getChild(JcrConstants.JCR_CONTENT);
					if(null != childPageResource) {
						setBeanFields(childPageResource,linkPath);
					}
				}
			}
		}
		return childPageAndTagsLinksList;
	}
	private void setBeanFields(final Resource childPageResource, String linkPath){
		final ValueMap pageProperties = childPageResource.getValueMap();
		final boolean isSecureResource = SecureUtil.isSecureResource(childPageResource);
		if(StringUtils.equals(StringUtils.EMPTY, CommonUtil.getStringProperty(pageProperties, NameConstants.PN_HIDE_IN_NAV)) &&
				StringUtils.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE, CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_TYPE))
	            && SecureUtil.isAuthorisedLink(childPageResource,authorizationService,slingRequest)	)
		{
			final String pimPagePath = CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_PIM_PATH);
			if(StringUtils.isNotBlank(pimPagePath)) {
				final Resource pimPagePathResource = resourceResolver.getResource(pimPagePath);
				if(null != pimPagePathResource) {
					setFields(childPageAndTagsLinksList, pimPagePathResource,linkPath,isSecureResource);
				}
			}
		}
	}
	
	/**
	 * Populate tags model.
	 *
	 * @param childPageAndTagsLinksList the child page and tags links list
	 * @param tags                      the tags
	 * @return the list
	 */
	public List<RelatedProductsPDHBean> populateTagsModel(List<RelatedProductsPDHBean> childPageAndTagsLinksList,
			String[] tags, String parentPage) {

		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page page = null != pageManager ? pageManager.getPage(parentPage): null;

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
		return childPageAndTagsLinksList;
	}
	
	/**
	 * sets parent page for fetching childpages.
	 *
	 * @param pageList the child page list
	 * @param tags                      the tags
	 *
	 */
	private void setPageListForTagsModel(Iterator<Page> pageList,String[] tags) {
		if (pageList != null) {
			while (pageList.hasNext()) {
				Resource pageContentRes = pageList.next().getContentResource();
				if (null != pageContentRes && CommonUtil.startsWithAnySiteContentRootPath(pageContentRes.getPath())) {
					setTagFields(pageContentRes, tags);
				}
			}
		}
	}
	
	private void setTagFields(Resource tagResource, String[] tags){

		ValueMap pageProperties = tagResource.getValueMap();
		final boolean isSecureResource = SecureUtil.isSecureResource(tagResource);
		String linkPath = tagResource.getPath();
		if(StringUtils.equals(StringUtils.EMPTY, CommonUtil.getStringProperty(pageProperties, NameConstants.PN_HIDE_IN_NAV)) &&
				StringUtils.equals(CommonConstants.PAGE_TYPE_PRODUCT_FAMILY_PAGE, CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_TYPE))
		        && CommonUtil.checkProductLifeSpanIsWithinThePeriod(tagResource,productTimeToLive)
				&& SecureUtil.isAuthorisedLink(tagResource,authorizationService,slingRequest)) {
			String pimPagePath = CommonUtil.getStringProperty(pageProperties, CommonConstants.PAGE_PIM_PATH);
			if(StringUtils.isNotBlank(pimPagePath)) {
				final Resource pimPagePathResource = resourceResolver.getResource(pimPagePath);
				if(null != pimPagePathResource) {
					setFields(childPageAndTagsLinksList, pimPagePathResource,linkPath, tags,isSecureResource);
				}
			}
		}
	}


	/**
	 * Populate fixed link model.
	 *
	 * @param fixedLinksList the fixed links list
	 * @param resource the resource
	 * @return the list
	 */
	private void populateFixedLinkModel(List<FixedLinksModel> fixedLinksList, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				Resource linkResource = linkResources.next();
				FixedLinksModel fixedLinkModel = linkResource.adaptTo(FixedLinksModel.class);
				if (null != fixedLinkModel) {
					Resource fixedLink = resourceResolver.resolve(ResourceUtil.getPropertyValue(linkResource,"fixedLinkPath"));
					if(SecureUtil.isAuthorisedLink(fixedLink, authorizationService, slingRequest)) {
						fixedLinkModel.setDesktopTransformedUrl(getDesktopTrans());
						fixedLinkModel.setMobileTransformedUrl(getMobileTrans());
						fixedLinkModel.setShowMarketingDescription(showMarketingDescription != null && showMarketingDescription.equals(CommonConstants.TRUE));
						fixedLinkModel.setSecure(SecureUtil.isSecureResource(fixedLink));
						fixedLinkModel.setMarketingDescription();
						fixedLinksList.add(fixedLinkModel);
					}
				}
			}
		}
	}
	
	/**
	 * Sets the fields.
	 *
	 * @param relatedProductsPDHBeanList the array
	 * @param pimPagePathResource the pimPagePathResource
	 */
	private void setFields(List<RelatedProductsPDHBean> relatedProductsPDHBeanList, final Resource pimPagePathResource, String linkPath, boolean isSecureResource) {
		final PIMResourceSlingModel pimResourceSlingModel = pimPagePathResource.adaptTo(PIMResourceSlingModel.class);
		final ValueMap pimPageProperties = pimPagePathResource.getValueMap();
		final RelatedProductsPDHBean relatedProductsBean = new RelatedProductsPDHBean();
		final SimpleDateFormat simpleDate = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
		final SimpleDateFormat publicationDateFormat = new SimpleDateFormat(DATE_FORMAT_PUBLISH);

		String eyebrowTitle = StringUtils.EMPTY;
		final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		if (null != pimResourceSlingModel) {
			String imagePath = pimResourceSlingModel.getPrimaryImage();
			String eyebrow = pimResourceSlingModel.getPrimarySubCategory();
			String linkTitle = pimResourceSlingModel.getProductName();

			if ((imagePath == null) || (imagePath.equals(""))) {
				imagePath = pimResourceSlingModel.getPdhPrimaryImg();
			}
			if ((linkTitle == null) || (linkTitle.equals(""))) {
				linkTitle = pimResourceSlingModel.getPdhProdName();
			}


            if (null != pageManager && null != eyebrow) {
				eyebrowTitle = CommonUtil.getLinkTitle(null, eyebrow, resourceResolver);
				relatedProductsBean.setLinkEyebrow(eyebrowTitle);
			}

            //EAT-5215
			setMarketingDescriptions(relatedProductsBean, pimPageProperties);

			linkPath = linkPath.replace("/jcr:content", StringUtils.EMPTY);
			linkPath = CommonUtil.dotHtmlLink(linkPath);
			relatedProductsBean.setLinkPath(CommonUtil.dotHtmlLink(linkPath, resourceResolver));
			relatedProductsBean.setLinkTitle(linkTitle);
			relatedProductsBean.setLinkImage(imagePath);
			relatedProductsBean.setLinkImageAltText(CommonUtil.getAssetAltText(resourceResolver, imagePath));
			relatedProductsBean.setLinkEyebrow(eyebrowTitle);
			relatedProductsBean.setDesktopTransformedUrl(getDesktopTrans());
			relatedProductsBean.setMobileTransformedUrl(getMobileTrans());
			relatedProductsBean.setReplicationDate(CommonUtil.getDateProperty(pimPageProperties, NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat));
			relatedProductsBean.setPublicationDate(CommonUtil.getDateProperty(pimPageProperties, CommonConstants.PUBLICATION_DATE, publicationDateFormat));
			relatedProductsBean.setLastModifiedDate(CommonUtil.getDateProperty(pimPageProperties, NameConstants.PN_PAGE_LAST_MOD, simpleDate));
			relatedProductsBean.setCreatedDate(CommonUtil.getDateProperty(pimPageProperties, CommonConstants.JCR_CREATED, simpleDate));
			relatedProductsBean.setTemplateName(CommonUtil.getStringProperty(pimPageProperties, CommonConstants.TEMPLATE_PROP_KEY));
			relatedProductsBean.setNewWindow(getOpenNewWindow());
			relatedProductsBean.setIsSecure(isSecureResource);

			relatedProductsPDHBeanList.add(relatedProductsBean);
	    }
	}

	/**
	 * Sets the fields.
	 *
	 * @param relatedProductsPDHBeanList the array
	 * @param pimPagePathResource the pimPagePathResource
	 */
	private void setFields(List<RelatedProductsPDHBean> relatedProductsPDHBeanList, final Resource pimPagePathResource, String linkPath, String[] tags, boolean isSecureResource) {
		final PIMResourceSlingModel pimResourceSlingModel = pimPagePathResource.adaptTo(PIMResourceSlingModel.class);
		String[] pimTags =  null != pimResourceSlingModel ? pimResourceSlingModel.getTags(): ArrayUtils.EMPTY_STRING_ARRAY;
		boolean contains = false;
		for (String tag : tags) {
			if(null != pimTags) {
				contains = Arrays.stream(pimTags).anyMatch(tag::equals);
				if (contains) {
					break;
				}
			}
		}
		if(contains && null != pimResourceSlingModel) {
			final ValueMap pimPageProperties = pimPagePathResource.getValueMap();
			final RelatedProductsPDHBean relatedProductsBean = new RelatedProductsPDHBean();
			final SimpleDateFormat simpleDate = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
			final SimpleDateFormat publicationDateFormat = new SimpleDateFormat(DATE_FORMAT_PUBLISH);
	
			String eyebrowTitle = StringUtils.EMPTY;
			String imagePath = pimResourceSlingModel.getPrimaryImage();
			String eyebrow = pimResourceSlingModel.getPrimarySubCategory();
			String linkTitle = pimResourceSlingModel.getProductName();

			if(!StringUtils.isNotBlank(imagePath)) {
				imagePath = pimResourceSlingModel.getPdhPrimaryImg();
			}
			if(!StringUtils.isNotBlank(linkTitle)) {
				linkTitle = pimResourceSlingModel.getPdhProdName();
			}
			//EAT-5215
			setMarketingDescriptions(relatedProductsBean, pimPageProperties);

			if (null != eyebrow) {
				eyebrowTitle = CommonUtil.getLinkTitle(null, eyebrow, resourceResolver);
				relatedProductsBean.setLinkEyebrow(eyebrowTitle);
			}

			linkPath = linkPath.replace("/jcr:content", StringUtils.EMPTY);
			linkPath = CommonUtil.dotHtmlLink(linkPath);
			relatedProductsBean.setLinkPath(CommonUtil.dotHtmlLink(linkPath, resourceResolver));
			relatedProductsBean.setLinkTitle(linkTitle);
			relatedProductsBean.setLinkImage(imagePath);
			relatedProductsBean.setLinkImageAltText(CommonUtil.getAssetAltText(resourceResolver, imagePath));
			relatedProductsBean.setLinkEyebrow(eyebrowTitle);
			relatedProductsBean.setDesktopTransformedUrl(getDesktopTrans());
			relatedProductsBean.setMobileTransformedUrl(getMobileTrans());
			relatedProductsBean.setReplicationDate(CommonUtil.getDateProperty(pimPageProperties, NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat));
			relatedProductsBean.setPublicationDate(CommonUtil.getDateProperty(pimPageProperties, CommonConstants.PUBLICATION_DATE, publicationDateFormat));
			relatedProductsBean.setLastModifiedDate(CommonUtil.getDateProperty(pimPageProperties, NameConstants.PN_PAGE_LAST_MOD, simpleDate));
			relatedProductsBean.setCreatedDate(CommonUtil.getDateProperty(pimPageProperties, CommonConstants.JCR_CREATED, simpleDate));
			relatedProductsBean.setTemplateName(CommonUtil.getStringProperty(pimPageProperties, CommonConstants.TEMPLATE_PROP_KEY));
			relatedProductsBean.setNewWindow(getOpenNewWindow());
			relatedProductsBean.setIsSecure(isSecureResource);

			relatedProductsPDHBeanList.add(relatedProductsBean);
		}
		
	}

	private void setMarketingDescriptions(RelatedProductsPDHBean relatedProductsBean, ValueMap pimPageProperties){
		if(showMarketingDescription != null && showMarketingDescription.equalsIgnoreCase(CommonConstants.TRUE)){
			if(StringUtils.isNoneEmpty( CommonUtil.getStringProperty(pimPageProperties,CommonConstants.PIM_MARKETING_DESCRIPTION))){
				relatedProductsBean.setMarketingDescription(CommonUtil.getStringProperty(pimPageProperties,CommonConstants.PIM_MARKETING_DESCRIPTION));
			}else{
				relatedProductsBean.setMarketingDescription(CommonUtil.getStringProperty(pimPageProperties,CommonConstants.PIM_PDH_DESCRIPTION));
			}
		}
	}

	/**
	 * this method is sorting the list based on the author selection.
	 *
	 * @param list the list
	 * @return the sorted list
	 */
	@SuppressWarnings("squid:S1604")
	private void getSortedList(List<RelatedProductsPDHBean> list) {
		Collections.sort(list, new Comparator<RelatedProductsPDHBean>() {
      public int compare(RelatedProductsPDHBean list1, RelatedProductsPDHBean list2) {
          int comparisonValue = 0;
		  switch(sortBy) {
			 case CommonConstants.TITLE :
                  comparisonValue = list1.getLinkTitle().compareToIgnoreCase(list2.getLinkTitle());
				  break;
			 case CommonConstants.PUBLISH_DATE :
				  comparisonValue= publishDate(comparisonValue,list1,list2);
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
	private int publishDate(int comparisonValue,RelatedProductsPDHBean list1, RelatedProductsPDHBean list2 ) {
		DateFormat simpleDate = new SimpleDateFormat(DATE_FORMAT_PUBLISH, Locale.ENGLISH);
		Date date1;
		Date date2;
		try {
			date1 = simpleDate.parse(list1.getPublicationDate());
			date2 = simpleDate.parse(list2.getPublicationDate());
			comparisonValue = -1 * (date1.compareTo(date2));
		} catch (ParseException e) {
			LOG.error("Related Products PDH | Unable to parse the date ", e);
		}
		return comparisonValue;
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
			List<RelatedProductsPDHBean> prodBeanList = childPageAndTagsLinksList.subList(0, maxLimitToDisplay);
			for (RelatedProductsPDHBean prodBean : prodBeanList) {
				viewList.add(prodBean);
			}

		} else if(StringUtils.equals(CommonConstants.FIXED_LIST, getListType())) {
			for (FixedLinksModel flModel : fixedLinksList) {
				viewList.add(flModel);
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
		if(getListTypeArray().size() > 3) {
			isCarousel = true;
		}
		return isCarousel;
	}
	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public String getHeader() {
		return header;
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
	 *
	 * @return marketing description from PIM Node
	 */
	public String getShowMarketingDescription() {
		return showMarketingDescription;
	}

	/**
	 *
	 * @return View All Link
	 */
	public String getViewAllProductsLinkLabel() {
		return viewAllProductsLinkLabel;
	}

	/**
	 *
	 * @return View All Product Link
	 */
	public String getViewAllProductsLink() {
		return viewAllProductsLink;
	}

	/**
	 *
	 * @return Enable Read More Link
	 */
	public String getEnableReadMoreLink() {
		return enableReadMoreLink;
	}

	/**
	 *
	 * @return
	 */
	public String getReadMoreLinkLabel() {
		return readMoreLinkLabel;
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
