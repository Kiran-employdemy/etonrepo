package com.eaton.platform.core.models.articlelist;

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
import org.apache.sling.models.annotations.Default;
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
import com.eaton.platform.core.bean.ArticleListBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in ArticleListHelper class. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleListModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(ArticleListModel.class); 
	
	/** The Constant SIMPLE_DATE_FORMAT. */
	private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
	/** The Constant DATE_FORMAT_PUBLISH. */
    private static final  String DATE_FORMAT_PUBLISH = "MMMM d, yyyy HH:mm:ss.SSSXXX";
    
	/** The Constant ANY. */
	private static final String ANY = "any";
	
	/** The Constant FEATURE. */
	private static final String FEATURE = "feature";
	
	/** The Constant NO_FEATURE. */
	private static final String NO_FEATURE = "no-feature";
	
	/** The manual links list. */
	private List<ManualLinksModel> manualLinksList;
	
	/** The fixed links list. */
	private List<FixedLinksModel> fixedLinksList;
	
	/** The child page links list. */
	private List<ArticleListBean> childPageAndTagsLinksList;
	
	/** The view. */
	@Inject @Via("resource") @Default(values="default")
	private String view;
	
	/** The cards header. */
	@Inject @Via("resource")
	private String cardsHeader;
	
	/** The cards description. */
	@Inject @Via("resource")
	private String cardsDescription;
	
	/** The list type. */
	@Inject @Via("resource")
	private String listType;
	
	/** The parent page. */
	@Inject @Via("resource")
	private String parentPage;
	
	/** The tags. */
	@Inject @Via("resource")
	private String tags[];
	
	/** The tags type. */
	@Inject @Via("resource")
	private String tagsType;
	
	/** The open new window. */
	@Inject @Via("resource") @Named("openNewWindow")
	private String newWindow;
	
	/** The fixed links. */
	@Inject @Via("resource")
	private Resource fixedLinks;
	
	/** The image manual link list. */
	@Inject @Via("resource")
	private Resource imageManualLinkList;
	
	/** The no image manual link list. */
	@Inject @Via("resource")
	private Resource noImageManualLinkList;
	
	/** The sort by. */
	@Inject @Via("resource")
	private String sortBy;

	/** The maxLimitToDisplay. */
	@Inject @Via("resource")
	private int maxLimitToDisplay;

	/** The desktop trans. */
	@Inject @Via("resource")
	private String desktopTrans;
	
	/** The mobile trans. */
	@Inject @Via("resource")
	private String mobileTrans;

    /** The resource page. */
    @Inject @Via("resource")
    private Page resourcePage;
    
    @EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;
    
    private boolean isUnitedStatesDateFormat = false;
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		
		ResourceResolver resourceResolver = resourcePage.getContentResource().getResourceResolver();
		if (siteResourceSlingModel.isPresent()) {
			isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
		}
		switch (this.listType) {
			case CommonConstants.CHILD_PAGES: {
				childPageAndTagsLinksList = new ArrayList<>();
				if (null != parentPage) {
					populateParentModel(childPageAndTagsLinksList, parentPage, resourceResolver);
					// sort the list when author has either selected Parent Page
					getSortedList(childPageAndTagsLinksList);
				}
				break;
			}
			case CommonConstants.TAGS: {
				childPageAndTagsLinksList = new ArrayList<>();
				if (null != tags) {
					populateTagsModel(childPageAndTagsLinksList, tags, resourceResolver);
					// sort the list when author has either selected Tag
					getSortedList(childPageAndTagsLinksList);
				}
				break;
			}
			case CommonConstants.FIXED_LIST: {
				fixedLinksList = new ArrayList<>();
				if (null != fixedLinks) {
					populateFixedLinkModel(fixedLinksList, fixedLinks);
				}
				break;
			}
			case CommonConstants.MANUAL_LIST: {
				manualLinksList = new ArrayList<>();
				if (StringUtils.equals(FEATURE, view) && null != imageManualLinkList) {
					populateManualLinkModel(manualLinksList, imageManualLinkList);
				} else if (StringUtils.equals(NO_FEATURE, view) && null != noImageManualLinkList) {
					populateManualLinkModel(manualLinksList, noImageManualLinkList);
				}
				break;
			}
			default:
				break;
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
	public List<ArticleListBean> populateParentModel(List<ArticleListBean> childPageAndTagsLinksList, String parentPagePath, ResourceResolver adminReadResourceResolver) {
		if (StringUtils.equals(CommonConstants.CHILD_PAGES, getListType())) {
			Resource parentResource = adminReadResourceResolver.getResource(parentPagePath);
			
			if(null != parentResource){
				Iterator<Resource> childrenResource = parentResource.listChildren();
				while(childrenResource.hasNext()){
					Resource childResource = childrenResource.next();
					if(StringUtils.equals(CommonUtil.getStringProperty(childResource.getValueMap(), JcrConstants.JCR_PRIMARYTYPE), NameConstants.NT_PAGE)){
						Resource jcrResource = childResource.getChild(JcrConstants.JCR_CONTENT);
						if(null != jcrResource) {
							ValueMap pageProperties = jcrResource.getValueMap();
							setFields(childPageAndTagsLinksList, childResource, pageProperties, adminReadResourceResolver);
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
	public List<ArticleListBean> populateTagsModel(List<ArticleListBean> childPageAndTagsLinksList, String[] tags, ResourceResolver resourceResolver) {
		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		if(null != tagManager) {
			boolean oneMatchIsEnough = StringUtils.equals(ANY, this.tagsType) ? true : false;
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
				fixedLink.setUnitedStatesDateFormat(isUnitedStatesDateFormat);
				if (null != fixedLink) {
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
				manualLink.setUnitedStatesDateFormat(isUnitedStatesDateFormat);
				if (null != manualLink) {
					if (StringUtils.equals(FEATURE, view)) {
						manualLink.setDesktopTransformedUrl(getDesktopTrans());
						manualLink.setMobileTransformedUrl(getMobileTrans());
					}
					manualLinksList.add(manualLink);
				}
			}
		}
		return manualLinksList;
	}

	/**
	 * Sets the fields.
	 * @param array the array
	 * @param pageResource the page resource
	 * @param properties the properties
	 * @param resourceResolver
	 */
	private void setFields(List<ArticleListBean> array, Resource pageResource,
						   ValueMap properties, ResourceResolver resourceResolver) {
		ArticleListBean articleListBean = new ArticleListBean();
		SimpleDateFormat simpleDate = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
		SimpleDateFormat publicationDateFormat = new SimpleDateFormat(DATE_FORMAT_PUBLISH);
		SimpleDateFormat defaultPublicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
		if(isUnitedStatesDateFormat) {
			defaultPublicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
		}
		if(StringUtils.equals(StringUtils.EMPTY, CommonUtil.getStringProperty(properties, NameConstants.PN_HIDE_IN_NAV))){
			
			String imagePath = CommonUtil.getStringProperty(properties, CommonConstants.TEASER_IMAGE_PATH);
			String teaserTitle = CommonUtil.getStringProperty(properties, CommonConstants.TEASER_TITLE);
			articleListBean.setLinkImage(imagePath);
			articleListBean.setDesktopTransformedUrl(getDesktopTrans());
			articleListBean.setMobileTransformedUrl(getMobileTrans());
			articleListBean.setLinkTitle(StringUtils.isNotBlank(teaserTitle) ? teaserTitle : CommonUtil.getLinkTitle(null, StringUtils.removeEnd(pageResource.getPath(), ("/" + JcrConstants.JCR_CONTENT)), resourceResolver));
			articleListBean.setLinkPath(CommonUtil.dotHtmlLink(StringUtils.removeEnd(pageResource.getPath(), ("/" + JcrConstants.JCR_CONTENT)), resourceResolver));
			articleListBean.setLinkEyebrow(CommonUtil.getStringProperty(properties, CommonConstants.EYEBROW_TITLE));
			articleListBean.setLinkImageAltText(CommonUtil.getAssetAltText(resourceResolver, imagePath));
			articleListBean.setPublicationDate(CommonUtil.getDateProperty(properties, CommonConstants.PUBLICATION_DATE, publicationDateFormat));
			articleListBean.setPublicationDateDisplay(CommonUtil.getDateProperty(properties, CommonConstants.PUBLICATION_DATE, defaultPublicationDateFormat));
			articleListBean.setReplicationDate(CommonUtil.getDateProperty(properties, NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat));
			articleListBean.setLastModifiedDate(CommonUtil.getDateProperty(properties, NameConstants.PN_PAGE_LAST_MOD, simpleDate));
			articleListBean.setCreatedDate(CommonUtil.getDateProperty(properties, CommonConstants.JCR_CREATED, simpleDate));
			articleListBean.setTemplateName(CommonUtil.getStringProperty(properties, CommonConstants.TEMPLATE_PROP_KEY));
			articleListBean.setNewWindow(getNewWindow());

			array.add(articleListBean);
		}
	}

	/**
	 * this method is sorting the list based on the author selection.
	 *
	 * @param list the list
	 * @return the sorted list
	 */
	private void getSortedList(List<ArticleListBean> list) {
		Collections.sort(list, new Comparator<ArticleListBean>() {
			public int compare(ArticleListBean linkList1, ArticleListBean linkList2) {
				int comparisonValue = 0;
				switch(sortBy) {
					case CommonConstants.TITLE : {
						comparisonValue = linkList1.getLinkTitle().compareToIgnoreCase(linkList2.getLinkTitle());
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
							LOG.error("ArticleListModel | Unable to parse the date " + e);
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
	 * Gets the list type array.
	 *
	 * @return the list type array
	 */
	public List<?> getListTypeArray() {
		List<?> viewList = new ArrayList<>();
		if(StringUtils.equals(CommonConstants.CHILD_PAGES, getListType()) || StringUtils.equals(CommonConstants.TAGS, getListType())) {
			if(maxLimitToDisplay > childPageAndTagsLinksList.size()) {
				maxLimitToDisplay = childPageAndTagsLinksList.size();
			}
			viewList = childPageAndTagsLinksList.subList(0, maxLimitToDisplay);
		} else if(StringUtils.equals(CommonConstants.FIXED_LIST, getListType())) {
			viewList = fixedLinksList;
		} else if(StringUtils.equals(CommonConstants.MANUAL_LIST, getListType())) {
			viewList = manualLinksList;
		}
		return viewList;
	}
	 
	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	public String getView() {
		return view;
	}

	/**
	 * Gets the cards header.
	 *
	 * @return the cards header
	 */
	public String getCardsHeader() {
		return cardsHeader;
	}

	/**
	 * Gets the cards description.
	 *
	 * @return the cards description
	 */
	public String getCardsDescription() {
		return cardsDescription;
	}

	/**
	 * Gets the list type.
	 *
	 * @return the list type
	 */
	public String getListType() {
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
	 * Gets the new window manual.
	 *
	 * @return the new window manual
	 */
	public String getNewWindow() {
		String manualLinkOpenNewWindow = StringUtils.EMPTY;
		if(StringUtils.equals(CommonConstants.TRUE, newWindow) ) {
			manualLinkOpenNewWindow = CommonConstants.TARGET_BLANK;
		}
		return manualLinkOpenNewWindow;
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
	 * Gets the image manual link list.
	 *
	 * @return the image manual link list
	 */
	public Resource getImageManualLinkList() {
		return imageManualLinkList;
	}

	/**
	 * Gets the no image manual link list.
	 *
	 * @return the no image manual link list
	 */
	public Resource getNoImageManualLinkList() {
		return noImageManualLinkList;
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
	 * Gets the desktop trans.
	 *
	 * @return the desktop trans
	 */
	public String getDesktopTrans() {
		return desktopTrans;
	}

	/**
	 * Gets the mobile trans.
	 *
	 * @return the mobile trans
	 */
	public String getMobileTrans() {
		return mobileTrans;
	}
}
