package com.eaton.platform.core.models.cardlist;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.CardListBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;


/**
 * <html> Description: This Sling Model used in CardListHelper class. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CardListModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(CardListModel.class); 
	
	/** The Constant SIMPLE_DATE_FORMAT. */
	private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
	/** The Constant DATE_FORMAT_PUBLISH. */
    private static final  String DATE_FORMAT_PUBLISH = "MMMM d, yyyy HH:mm:ss.SSSXXX";
    
	/** The Constant CARDLIST_WITH_IMAGE_VIEW. */
	private static final String CARD_LIST_WITH_IMAGES= "cardList-with-images";
 
	/** The Constant CARDLIST_WITHOUT_IMAGE_VIEW. */
	private static final String CARD_LIST_WITHOUT_IMAGES= "cardList-without-image";

	/** The Constant ANY. */
	private static final String ANY = "any";
	
	/** The manual links list. */
	private List<CardListManualLinksModel> manualLinksList;
	
	/** The fixed links list. */
	private List<CardListFixedLinksModel> fixedLinksList;
	
	/** The child page links list. */
	private List<CardListBean> childPageAndTagsLinksList;
	
	/** The view. */
	@Inject @Via("resource")
	private String view;
	
	/** The header. */
	@Inject @Via("resource")
	private String cardsHeader;
	
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
	
	/** The new window. */
	@Inject @Via("resource") @Named("openNewWindow")		
	private String newWindow;
	
	/** The fixed links. */
	@Inject @Via("resource")
	private Resource fixedLinks;
	/** The  ImageManual links. */
	@Inject @Via("resource")
	private Resource imageManualLinkList;
	
	/** The  No ImageManual links. */
	@Inject @Via("resource")
	private Resource noImageManualLinkList;
	
	/** The alignment desktop. */
	@Inject @Via("resource") @Named("cardListAlignmentDesktop")
	private String alignmentDesktop;

	/** The alignment mobile. */
	@Inject @Via("resource") @Named("cardListAlignmentMobile")
	private String alignmentMobile;
	
	/** The count. */
	@Inject @Via("resource")
	private int maxLimitToDisplay;
	
	/** The sort. */
	@Inject @Via("resource")
	private String sortBy;
	
	/** The Mobile Transforms. */
	@Inject @Via("resource")
	private String mobileTrans;
	
	/** The desktop trans. */
	@Inject @Via("resource")
	private String desktopTrans;
	
	 /** The resource page. */
    @Inject @Via("resource")
    private Page resourcePage;
	
	/** The admin service. */
	@Inject
	AdminService adminService;
	
	@EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;
    
    private boolean isUnitedStatesDateFormat = false;
	
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		if (siteResourceSlingModel.isPresent()) {
			isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
		}
		// get resource resolver object from admin service
		try (ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
			switch (this.listType) {
				case CommonConstants.CHILD_PAGES: {
					childPageAndTagsLinksList = new ArrayList<>();
					if (null != getParentPagePath()) {
						populateParentModel(childPageAndTagsLinksList, getParentPagePath(), adminReadResourceResolver);
						// sort the list when author has either selected Parent Page or Tag
						getSortedList(childPageAndTagsLinksList);
					}
					break;
				}
				case CommonConstants.TAGS: {
					childPageAndTagsLinksList = new ArrayList<>();
					if (null != tags) {
						populateTagsModel(childPageAndTagsLinksList, tags, adminReadResourceResolver);
						// sort the list when author has either selected Parent Page or Tag
						getSortedList(childPageAndTagsLinksList);
					}
					break;
				}
				case CommonConstants.FIXED_LIST: {
					fixedLinksList = new ArrayList<>();
					if (null != this.fixedLinks) {
						populateFixedLinkModel(this.fixedLinksList, fixedLinks);
					}
					break;
				}
				case CommonConstants.MANUAL_LIST: {
					manualLinksList = new ArrayList<>();
					if ((null != imageManualLinkList) && StringUtils.equals(CARD_LIST_WITH_IMAGES, view)) {
						populateManualLinkModel(manualLinksList, imageManualLinkList);
					}
					if ((null != this.noImageManualLinkList) && StringUtils.equals(CARD_LIST_WITHOUT_IMAGES, view)) {
						populateManualLinkModel(manualLinksList, noImageManualLinkList);
					}
					break;
				}
				default: {
					break;
				}
			}
		}
	}
	
	/**
	 * Populate parent model.
	 *
	 * @param childPageAndTagsLinksList the child page and tags links list
	 * @param parentPagePath the parent page path
	 * @param adminReadResourceResolver
	 * @return the list
	 */
	public List<CardListBean> populateParentModel(List<CardListBean> childPageAndTagsLinksList, String parentPagePath, ResourceResolver adminReadResourceResolver) {
	
		PageManager pageManager = adminReadResourceResolver.adaptTo(PageManager.class);
		Page parentPageModel = null;
		if(null != pageManager){
			parentPageModel = pageManager.getPage(parentPagePath);
		}
		if(null != parentPageModel) {
			Iterator<Page> childPages = parentPageModel.listChildren();
			while(childPages.hasNext()) {
				Page childPage = childPages.next();
					setFields(childPageAndTagsLinksList, childPage, adminReadResourceResolver);
			}
		}
		
		return childPageAndTagsLinksList;
	}

	/**
	 * Populate tags model.
	 *
	 * @param childPageAndTagsLinksList the child page and tags links list
	 * @param tags the tags
	 * @param adminReadResourceResolver
	 * @return the list
	 */
	public List<CardListBean> populateTagsModel(List<CardListBean> childPageAndTagsLinksList, String[] tags, ResourceResolver adminReadResourceResolver) {

		TagManager tagManager = adminReadResourceResolver.adaptTo(TagManager.class);
		RangeIterator<Resource> tagsIterator = null;
		if(null != tagManager){
			boolean oneMatchIsEnough = StringUtils.equals(ANY, tagsType) ? true : false;
			tagsIterator = tagManager.find(CommonUtil.getHomePagePath(resourcePage), tags, oneMatchIsEnough);
		  	if(null != tagsIterator) {
			while (tagsIterator.hasNext()) {
				Resource tagResource =  tagsIterator.next();
				PageManager pageManager = adminReadResourceResolver.adaptTo(PageManager.class);
					Page tagPage =null;
					if (null != pageManager){
						tagPage = pageManager.getPage(StringUtils.removeEnd(tagResource.getPath(), JcrConstants.JCR_CONTENT));
					}
					if (null != tagPage && CommonUtil.startsWithAnySiteContentRootPath(tagResource.getPath())) {
						setFields(childPageAndTagsLinksList, tagPage, adminReadResourceResolver);
					}
				}
			}
		}
        return childPageAndTagsLinksList;
	}
	
	 /**
 	 * Populate manual link model.
 	 *
 	 * @param manualLinksList the manual links list
 	 * @param resource the resource
 	 * @return the list
 	 */
	public List<CardListManualLinksModel> populateManualLinkModel(List<CardListManualLinksModel> manualLinksList, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				CardListManualLinksModel manualLink = linkResources.next().adaptTo(CardListManualLinksModel.class);
				manualLink.setUnitedStatesDateFormat(isUnitedStatesDateFormat);
				if (null != manualLink) {
					manualLink.setAlignmentDesktop(alignmentDesktop);
					manualLink.setAlignmentMobile(alignmentMobile);
					manualLink.setDesktopTransformedUrl(getDesktopTrans());
					manualLink.setMobileTransformedUrl(getMobileTrans());
					manualLinksList.add(manualLink);
				}
			}
		}
		return manualLinksList;
	}

	 /**
 	 * Populate fixed link model.
 	 *
 	 * @param fixedLinksList the fixed links list
 	 * @param resource the resource
 	 * @return the list
 	 */
	public List<CardListFixedLinksModel> populateFixedLinkModel(List<CardListFixedLinksModel> fixedLinksList, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				CardListFixedLinksModel fixedLink = linkResources.next().adaptTo(CardListFixedLinksModel.class);
				fixedLink.setUnitedStatesDateFormat(isUnitedStatesDateFormat);
				if (null != fixedLink) {
					fixedLink.setAlignmentDesktop(alignmentDesktop);
					fixedLink.setAlignmentMobile(alignmentMobile);
					fixedLink.setDesktopTransformedUrl(getDesktopTrans());
					fixedLink.setMobileTransformedUrl(getMobileTrans());
					fixedLinksList.add(fixedLink);
				}
			}
		}
		return fixedLinksList;
	}

	/**
	 * Sets the fields.
	 *  @param array the array
	 * @param page the page
	 * @param adminReadResourceResolver
	 */
	private void setFields(List<CardListBean> array, Page page, ResourceResolver adminReadResourceResolver) {
		CardListBean cardTypeBean = new CardListBean(); 
        ValueMap properties =  page.getProperties();
        String eyeBrowTitle = CommonUtil.getStringProperty(properties, CommonConstants.EYEBROW_TITLE);
		SimpleDateFormat simpleDate = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
		SimpleDateFormat publicationDateFormat = new SimpleDateFormat(DATE_FORMAT_PUBLISH);
		SimpleDateFormat defaultPublicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
		if(isUnitedStatesDateFormat) {
			defaultPublicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
		}
		if (!page.isHideInNav()) {
			
			String imagePath = CommonUtil.getStringProperty(properties, CommonConstants.TEASER_IMAGE_PATH);
			String teaserTitle = CommonUtil.getStringProperty(properties, CommonConstants.TEASER_TITLE);
			cardTypeBean.setTeaserImage(imagePath);
			cardTypeBean.setAlignmentDesktop(alignmentDesktop);
			cardTypeBean.setAlignmentMobile(alignmentMobile);
			cardTypeBean.setDesktopTransformedUrl(getDesktopTrans());
			cardTypeBean.setMobileTransformedUrl(getMobileTrans());
			cardTypeBean.setHeadline(StringUtils.isNotBlank(teaserTitle) ? teaserTitle : CommonUtil.getLinkTitle(null, page.getPath(), adminReadResourceResolver));
			cardTypeBean.setPagePath(page.getPath());
			cardTypeBean.setEyebrow(StringUtils.isNotBlank(eyeBrowTitle) ? eyeBrowTitle : CommonUtil.getLinkTitle(null, page.getPath(), adminReadResourceResolver));
			cardTypeBean.setDescriptionFeature(CommonUtil.getStringProperty(properties, CommonConstants.TEASER_DESCRIPTION));
			cardTypeBean.setAltTxt(CommonUtil.getAssetAltText(adminReadResourceResolver, imagePath));
			cardTypeBean.setTemplateName(page.getProperties().get(CommonConstants.TEMPLATE_PROP_KEY, StringUtils.EMPTY));
		    cardTypeBean.setPublicationDate(CommonUtil.getDateProperty(properties, CommonConstants.PUBLICATION_DATE, publicationDateFormat));
		    cardTypeBean.setPublicationDateDisplay(CommonUtil.getDateProperty(properties, CommonConstants.PUBLICATION_DATE, defaultPublicationDateFormat));
			cardTypeBean.setReplicationDate(CommonUtil.getDateProperty(properties, NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat));
			cardTypeBean.setLastModifiedDate(CommonUtil.getDateProperty(properties, NameConstants.PN_PAGE_LAST_MOD, simpleDate));
			cardTypeBean.setCreatedDate(CommonUtil.getDateProperty(properties, CommonConstants.JCR_CREATED, simpleDate));
			cardTypeBean.setNewWindow(getNewWindow());
			array.add(cardTypeBean); 
		}
	}
	
	/**
	 * this method is sorting the list based on the author selection.
	 *
	 * @param list the list
	 * @return the sorted list
	 */
		private void getSortedList(List<CardListBean> list) {
			Collections.sort(list, new Comparator<CardListBean>() {
				public int compare(CardListBean linkList1, CardListBean linkList2) {
					int comparisonValue = 0;
					switch(sortBy) {
						case CommonConstants.TITLE : {
							comparisonValue = linkList1.getHeadline().compareToIgnoreCase(linkList2.getHeadline());
							break;
						}
						case CommonConstants.PUBLISH_DATE : {
							DateFormat simpleDate = new SimpleDateFormat(DATE_FORMAT_PUBLISH, Locale.ENGLISH);
							Date date1;
							Date date2;
							try {
								date1 = simpleDate.parse(linkList1.getPublicationDate());
								date2 = simpleDate.parse(linkList2.getPublicationDate());
								comparisonValue = -1 * (date1.compareTo(date2));
							} catch (ParseException e) {
								LOG.error("CardListModel | Unable to parse the date " + e);
							}
							break;
						}
						case CommonConstants.CREATED_DT : {
							comparisonValue = -1 * (linkList1.getCreatedDate().compareTo(linkList2.getCreatedDate()));
							break;
						}
						case CommonConstants.LAST_MOD_DT : {
							comparisonValue = -1 * (linkList1.getLastModifiedDate().compareTo(linkList2.getLastModifiedDate()));
							break;
						}
						case CommonConstants.TEMPLATE : {
							comparisonValue = linkList1.getTemplateName().compareToIgnoreCase(linkList2.getTemplateName());
							break;
						}
						default : {
							comparisonValue = 0;
						}
					}
					return comparisonValue;
				}
			});
	}
		
 /**
  * Gets the list type.
  *
  * @return the list type
  */
 /* Gets the list type.
	 *
	 * @return the list type
	 */
	public String getListType() {
		return listType;
	}

    /**
     * Gets the view.
     *
     * @return view
     */
	public String getView() {
		return this.view;
	}
	
	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public String getCardsHeader() {
		return cardsHeader;
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
	 * Gets the sort.
	 *
	 * @return the sort
	 */
	public String getSortBy() {
		return sortBy;
	}

	/**
	 * Gets the tags type.
	 *
	 * @return the tagsType
	 */
	public String getTagsType() {
		return tagsType;
	}

	/**
	 * Gets the new window.
	 *
	 * @return the openNewWindow
	 */
	public String getNewWindow() {
		String newWindowOpen = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, newWindow) ) {
			newWindowOpen = CommonConstants.TARGET_BLANK;
		}
		return newWindowOpen;
	}

	/**
	 * Gets the max limit to display.
	 *
	 * @return the maxLimitToDisplay
	 */
	public int getMaxLimitToDisplay() {
		return maxLimitToDisplay;
	}

	/**
	 * Gets the alignment desktop.
	 *
	 * @return the alignment desktop
	 */
	public String getAlignmentDesktop() {
		return alignmentDesktop;
	}

	/**
	 * Sets the alignment desktop.
	 *
	 * @param alignmentDesktop the new alignment desktop
	 */
	public void setAlignmentDesktop(String alignmentDesktop) {
		this.alignmentDesktop = alignmentDesktop;
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

	/**
	 * Gets the parentPage.
	 *
	 * @return the parentPage
	 */
	public String getParentPagePath() {
		String parentPageInfo = this.parentPage;
		if(null == parentPageInfo) {
			parentPageInfo = this.resourcePage.getPath();
		}
		return parentPageInfo;
	}
	
	/**
	 * Gets the list type array.
	 *
	 * @return the list type array
	 */
	public List<?> getListTypeArray() {
		List<?> viewList = new ArrayList<>();
		if(StringUtils.equals(CommonConstants.CHILD_PAGES, listType) || StringUtils.equals(CommonConstants.TAGS, listType)) {
			if(maxLimitToDisplay > childPageAndTagsLinksList.size()) {
				maxLimitToDisplay = childPageAndTagsLinksList.size();
			}
			viewList = childPageAndTagsLinksList.subList(0, maxLimitToDisplay);
		} else if(StringUtils.equals(CommonConstants.FIXED_LIST, listType)) {
			viewList = fixedLinksList;
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, listType)) {
			viewList = manualLinksList;
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
	
}
