package com.eaton.platform.core.models;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.Random;

import com.day.cq.tagging.Tag;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceNotFoundException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.TabbedMenuItem;
import com.eaton.platform.core.bean.TabbedMenuLink;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.models.annotations.Source;

/**
 * The Class TabbedMenuListModel.
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TabbedMenuListModel {

	private static final Logger LOG = LoggerFactory.getLogger(TabbedMenuListModel.class);

	/** The Constant VIEW_ALL_KEY. */
	private static final String VIEW_ALL_KEY = "View_all";

	/** The Constant DEFAULT_VIEW_ALL. */
	private static final String DEFAULT_VIEW_ALL = "View all";

	private static final int RANDOM_UNIQUE_ID_UPPERBOUND = 10000;
	
	private static final String TAG_PREFIX = StringUtils.join(CommonConstants.STARTS_WITH_CONTENT, CommonConstants.SLASH_STRING, CommonConstants.CQ_TAGS, CommonConstants.SLASH_STRING);

	/** The tabs list. */
	private List<TabbedMenuItem> tabsList = new ArrayList<>();

	/** The tab bean. */
	private TabbedMenuModel tabbedMenuConfig;

	/** The view all label. */
	private String viewAllLabel;

	/** The unique id (name). */
	private String uniqueId;

	/** the tags formatted with correct prefix */
	private List<String> tagsFormatted = new ArrayList<>();
	
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

	@Inject
	@Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	@Inject
	/* The current page. */
	protected Page currentPage;

	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	@Inject
	/* The page manager. */
	protected PageManager pageManager;

	@Inject
	protected AdminService adminService;

	@Inject
	/* The res. */
	protected Resource res;

	private Collator langCollator;

	@PostConstruct
	protected void init() {
		LOG.debug("TabbedMenuListModel : init : Start");

		Locale languageValue = currentPage.getLanguage(false);
		if ((languageValue != null) && (languageValue.getLanguage() != null) && (!languageValue.getLanguage().isEmpty())) {
			langCollator = Collator.getInstance(new Locale(languageValue.getLanguage(), languageValue.getCountry()));
		}
		viewAllLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, VIEW_ALL_KEY, DEFAULT_VIEW_ALL).concat(CommonConstants.SPACE_STRING);
		
		tabbedMenuConfig = res.adaptTo(TabbedMenuModel.class);
		if (tabbedMenuConfig == null) {
			LOG.error("Cannot adapt resource to TabbedMenuModel. No TabbedMenuModel set.");
		} else {
			uniqueId = tabbedMenuConfig.getName();
			if (null == uniqueId || uniqueId.isEmpty()) {
				LOG.warn("TabbedMenuModel uniqueId (name) field is empty. Generating random uniqueId.");
				uniqueId = getRandomUniqueId();
			} else {
				LOG.debug("TabbedMenuModel uniqueId (name) set to {}", uniqueId);
			}

			LOG.debug("{} list type is selected.", tabbedMenuConfig.getListType());
			
			String target = ( tabbedMenuConfig.getNewWindow() != null && tabbedMenuConfig.getNewWindow().equalsIgnoreCase(CommonConstants.TRUE) ) ? CommonConstants.TARGET_BLANK : CommonConstants.TARGET_SELF;
			LOG.debug("TabbedMenuModel getNewWindow (for child-pages and tags list type) set to {}", target);
			
			if (tabbedMenuConfig.getListType().equalsIgnoreCase(CommonConstants.CHILD_PAGES)) {
				tabsList = getChildList(target);
				sortList();
			} else if (tabbedMenuConfig.getListType().equalsIgnoreCase(CommonConstants.TAGS)) {
				tabsList = getTaggedList(target);
				sortList();
			} else {
				tabsList = getFixedList();
			}
			
		}
		LOG.debug("TabbedMenuListModel : init : End");
	}

	private static String getRandomUniqueId() {
		LOG.debug("TabbedMenuListModel : getRandomUniqueId : START");
		
		Random rand = new Random();
		int random_unique_id_int = rand.nextInt(RANDOM_UNIQUE_ID_UPPERBOUND);
		String random_unique_id_str = Integer.toString(random_unique_id_int);
		LOG.debug("Random unique id generated: {}", random_unique_id_str);
		
		LOG.debug("TabbedMenuListModel : getRandomUniqueId : END");
		return random_unique_id_str;
	}

	/*
	 * Populates a list of TabbedMenuItems with the child 
	 * and grandchild pages of the parent page from the TabbedMenuModel
	 *
	 * @param target - self/blank
	 * @return list of TabbedMenuItems
	 */
	private List<TabbedMenuItem> getChildList(String target) {
		LOG.debug("TabbedMenuListModel : getChildList : START");
		
		List<TabbedMenuItem> tabbedMenuItems = new ArrayList<>();
		
		Page parentPage = pageManager.getPage(tabbedMenuConfig.getParentPage());
		if(null!=parentPage) {
			Iterator<Page> childPages = parentPage.listChildren();
			if (null != childPages) {
				while (childPages.hasNext()) {

					Page childPage = childPages.next();
					if (childPage.isHideInNav()) {
						LOG.debug("Child page ({}) is set to hide in navigation. TabbedMenuItem not set for this child page.", childPage.getPath());
					} else {
						TabbedMenuItem tabbedMenuItem = getSingleTabbedMenuItem(childPage, target);
						tabbedMenuItems.add(tabbedMenuItem);
					}
				}
			}
		}

		LOG.debug("TabbedMenuListModel : getChildList : END");
		return tabbedMenuItems;
	}

	/*
	 * Populates a list of TabbedMenuItems with the child and grandchild pages of the parent page
	 * from the TabbedMenuModel that are tagged with the tags set in the TabbedMenuModel
	 * 
	 * @param target - self/blank
	 * @return list of TabbedMenuItems
	 */
	private List<TabbedMenuItem> getTaggedList(String target) {
		LOG.debug("TabbedMenuListModel : getTaggedList : START");
		
		LOG.debug("Tags set in Tabbed Menu List authoring configuration");
		for (String tag : tabbedMenuConfig.getTags()) {
			LOG.debug("{}", tag);
			tagsFormatted.add(StringUtils.join(TAG_PREFIX, tag.replace(CommonConstants.COLON, CommonConstants.SLASH_STRING)));
		}

		List<TabbedMenuItem> tabbedMenuItems = new ArrayList<>();

		Page parentPage = pageManager.getPage(tabbedMenuConfig.getParentPage());
		Iterator<Page> childPages = parentPage.listChildren();
		if (null != childPages) {
			while (childPages.hasNext()) {
				
				Page childPage = childPages.next();
				
				if (childPage.isHideInNav()) {
					LOG.debug("Child page ({}) is set to hide in navigation. TabbedMenuItem not set for this child page.", childPage.getPath());
				} else {

					TabbedMenuItem tabbedMenuItem = getSingleTabbedMenuItem(childPage, target);

					if (isPageTagged(childPage)) {
						tabbedMenuItems.add(tabbedMenuItem);
					} else if (null != tabbedMenuItem.getTabbedMenuLinkList() && !tabbedMenuItem.getTabbedMenuLinkList().isEmpty()) {
						tabbedMenuItems.add(tabbedMenuItem);
					} else {
						LOG.debug("tabbedMenuItem with child ({}) has no TabbedMenuLinks tagged with tag set in TabbedMenuModel.", childPage.getPath());
					}

				}
			}
		}

		LOG.debug("TabbedMenuListModel : getTaggedList : END");
		return tabbedMenuItems;
	}
	
	/*
	* Populates a list of TabbedMenuItems with the fixed set of links 
	* from the TabbedMenuModel authoring configuration
	* 
	* @return list of TabbedMenuItems
	 */
	private List<TabbedMenuItem> getFixedList() {
		LOG.debug("TabbedMenuListModel : getFixedList : START");

		List<TabbedMenuItem> tabbedMenuItemList = new ArrayList<>();
		Resource fixedPath = tabbedMenuConfig.getFixedLinks();
		if (null != fixedPath) {
			Iterator<Resource> fixedListItems = fixedPath.listChildren();
			while (fixedListItems.hasNext()) {
				Resource fixedListItem = fixedListItems.next();
				tabbedMenuItemList.add(getFixedListTabbedMenuItem(fixedListItem));
			}
		}
		LOG.debug("TabbedMenuListModel : getFixedList : END");
		return tabbedMenuItemList;
	}
	
	private TabbedMenuItem getFixedListTabbedMenuItem(Resource fixedListItem){

		LOG.debug("TabbedMenuListModel : getFixedListTabbedMenuItem : START");
		
		TabbedMenuItem tabbedMenuItem = null;

		ValueMap fixedListItemProperties = fixedListItem.getValueMap();
		if (fixedListItemProperties.containsKey(CommonConstants.LINK_PATH)) {
			String fixedListItemLinkPath = CommonUtil.getStringProperty(fixedListItemProperties, CommonConstants.LINK_PATH);
			LOG.debug("Attempting to find the fixed list item with page path set to: {}", fixedListItemLinkPath);
			Page fixedListItemPage = pageManager.getPage(fixedListItemLinkPath);
			if (null == fixedListItemPage) {
				LOG.error("Fixed list item with page path of ({}) not found. Page not found.", fixedListItemLinkPath);
			} else {
				LOG.debug("Setting fixed list item for page {}", fixedListItemLinkPath);
				String fixedListItemTargetProperty = CommonUtil.getStringProperty(fixedListItemProperties, CommonConstants.NEW_WINDOW_FIXED);
				LOG.debug("Setting target property to {}", fixedListItemTargetProperty);
				String target = StringUtils.equalsIgnoreCase(fixedListItemTargetProperty, CommonConstants.TRUE) ? CommonConstants.TARGET_BLANK : CommonConstants.TARGET_SELF;
				if (fixedListItemPage.isHideInNav()){
					LOG.debug("Child page ({}) is set to hide in navigation. TabbedMenuItem not set for this child page.", fixedListItemPage.getPath());
				} else {
					tabbedMenuItem = getSingleTabbedMenuItem(fixedListItemPage, target);
				}
			}
		}
		
		LOG.debug("TabbedMenuListModel : getFixedListTabbedMenuItem : END");
		return tabbedMenuItem;
	}

	/*
	* Get a single tabbed menu item set with attributes from passed page
	* @return a populated tabbed menu item
	 */
	private TabbedMenuItem getSingleTabbedMenuItem(Page childPage, String target) {
		LOG.debug("TabbedMenuListModel : getSingleTabbedMenuItem : START");
		LOG.debug("Creating new TabbedMenuItem for page {}", childPage.getPath());
		ValueMap childPageProperties = childPage.getProperties();
		TabbedMenuItem tabbedMenuItem = new TabbedMenuItem();
		final String teaserTitle = CommonUtil.getStringProperty(childPageProperties, CommonConstants.TEASER_TITLE);
		tabbedMenuItem.setTitle(StringUtils.isNotBlank(teaserTitle) ? teaserTitle : CommonUtil.getLinkTitle(null, childPage.getPath(), resourceResolver));
		tabbedMenuItem.setPublicationDate(CommonUtil.getDateProperty(childPageProperties, CommonConstants.PUBLICATION_DATE, simpleDateFormat));
		tabbedMenuItem.setLastModifiedDate(CommonUtil.getDateProperty(childPageProperties, NameConstants.PN_PAGE_LAST_MOD, simpleDateFormat));
		tabbedMenuItem.setCreatedDate(CommonUtil.getDateProperty(childPageProperties, CommonConstants.JCR_CREATED, simpleDateFormat));
		tabbedMenuItem.setTemplateName(CommonUtil.getStringProperty(childPageProperties, CommonConstants.TEMPLATE_PROP_KEY));
		LOG.debug("TabbedMenuItem title: {}", tabbedMenuItem.getTitle());
		LOG.debug("TabbedMenuItem publication date: {}", tabbedMenuItem.getPublicationDate());
		LOG.debug("TabbedMenuItem last modified date: {}", tabbedMenuItem.getLastModifiedDate());
		LOG.debug("TabbedMenuItem created date: {}", tabbedMenuItem.getCreatedDate());
		LOG.debug("TabbedMenuItem template name: {}", tabbedMenuItem.getTemplateName());

		List<TabbedMenuLink> tabbedMenuLinks = new ArrayList<>();
		if (!tabbedMenuConfig.getListType().equalsIgnoreCase(CommonConstants.FIXED_LIST)) {
			LOG.debug("List type is set to {}. TabbedMenuLinks are being set", tabbedMenuConfig.getListType());
			tabbedMenuLinks = populateAllTabbedMenuLinks(childPage, target);
		} else {
			LOG.debug("List type is set to {}. TabbedMenuLinks were not set.", tabbedMenuConfig.getListType());
		}

		if (tabbedMenuLinks.isEmpty()) {
			LOG.debug("No tabbedMenuLinks set. Child page ({}) doesn't contain any children (grandchildren to parentPage). Setting target and link for child page's title.", childPage.getPath());
			tabbedMenuItem.setTitleTarget(target);
			tabbedMenuItem.setTitleLink(CommonUtil.dotHtmlLink(childPage.getPath(), resourceResolver));
			LOG.debug("TabbedMenuItem title target: {}", tabbedMenuItem.getTitleTarget());
			LOG.debug("TabbedMenuItem title link: {}", tabbedMenuItem.getTitleLink());
		} else {
			LOG.debug("TabbedMenuItem ({}) has tabbedMenuLinks.", childPage.getPath());
			tabbedMenuItem.setHeaderTarget(target);
			tabbedMenuItem.setHeaderText(viewAllLabel + tabbedMenuItem.getTitle());
			tabbedMenuItem.setHeaderLink(CommonUtil.dotHtmlLink(childPage.getPath(), resourceResolver));
			tabbedMenuItem.setTabbedMenuLinkList(tabbedMenuLinks);
			LOG.debug("TabbedMenuItem header target: {}", tabbedMenuItem.getHeaderTarget());
			LOG.debug("TabbedMenuItem header text: {}", tabbedMenuItem.getHeaderText());
			LOG.debug("TabbedMenuItem header link: {}", tabbedMenuItem.getHeaderLink());
			LOG.debug("TabbedMenuItem list of TabbedMenuLinks: {}", tabbedMenuItem.getTabbedMenuLinkList());
		}
		
		LOG.debug("TabbedMenuListModel : getSingleTabbedMenuItem : END");
		return tabbedMenuItem;
	}
	
	/*
	* Get a list of tabbed menu links objects with the passed page's child pages
	* 
	* @param page
	* @return list of TabbedMenuLink objects
	 */
	private List<TabbedMenuLink> populateAllTabbedMenuLinks(Page childPage, String target){
		LOG.debug("TabbedMenuListModel : populateAllTabbedMenuLinks : START");
		List<TabbedMenuLink> tabbedMenuLinks = new ArrayList<>();
		
		Iterator<Page> grandchildPages = childPage.listChildren();
		if (null != grandchildPages) {
			while (grandchildPages.hasNext()) {
				Page grandchildPage = grandchildPages.next();

				if (grandchildPage.isHideInNav()) {
					LOG.warn("TabbedMenuLink for page ({}) not populated. Page is hidden from navigation.", grandchildPage.getPath());
				} else {
					tabbedMenuLinks = addTabbedMenuLinks(tabbedMenuLinks, childPage.getPath(), grandchildPage, target);
				}
			}
		}
		
		LOG.debug("TabbedMenuListModel : populateAllTabbedMenuLinks : END");
		return tabbedMenuLinks;
	}
	
	/*
	* Add menu link to tabbed menu links list
	* @param tabbedMenuLinks
	* @param childPagePath
	* @param grandchildPage
	* @param target
	* 
	* @return list of TabbedMenuLink objects
	 */
	private List<TabbedMenuLink> addTabbedMenuLinks(List<TabbedMenuLink> tabbedMenuLinks, String childPagePath, Page grandchildPage, String target){
		LOG.debug("TabbedMenuListModel : addTabbedMenuLinks : START");
		
		String grandchildPagePath = grandchildPage.getPath();
		LOG.debug("TabbedMenuLink being set for {} under the TabbedMenuItem for {}", grandchildPagePath, childPagePath);

		if (tabbedMenuConfig.getListType().equalsIgnoreCase(CommonConstants.TAGS)) {
			if (isPageTagged(grandchildPage)) {
				LOG.debug("Setting tagged children of page ({}) to TabbedMenuLinks", childPagePath);
				tabbedMenuLinks.add(populateSingleTabbedMenuLink(grandchildPage, target));
			} else {
				LOG.debug("TabbedMenuLinks not populated since child page is not tagged ({})", childPagePath);
			}

		} else {
			LOG.debug("Setting all child pages of page ({}) to TabbedMenuLinks", childPagePath);
			tabbedMenuLinks.add(populateSingleTabbedMenuLink(grandchildPage, target));

		}
		
		LOG.debug("TabbedMenuListModel : addTabbedMenuLinks : END");
		return tabbedMenuLinks;
	}

	/*
	* Populates a single TabbedMenuLink object with the properties from the passed page
	* 
	* @param page - page to populate the TabbedMenuLink object with
	* @param target - self/blank
	* @return populated TabbedMenuLink object
	 */
	private TabbedMenuLink populateSingleTabbedMenuLink(Page page, String target){
		LOG.debug("TabbedMenuListModel : populateSingleTabbedMenuLink : START");
		
		TabbedMenuLink tabbedMenuLink = new TabbedMenuLink();
		
		try {

			if (null == page) {
				throw new ResourceNotFoundException("Page resource passed to populateTabbedMenuListLink cannot be found.");
			} else {
				ValueMap pageProperties = page.getProperties();
				if (verifyPageDateProperties(page.getPath(), pageProperties)) {

					String pageRedirectUrl = CommonUtil.getStringProperty(pageProperties, CommonConstants.REDIRECT_URL);
					tabbedMenuLink.setLink(CommonUtil.dotHtmlLink(pageRedirectUrl.isEmpty() ? page.getPath() : pageRedirectUrl, resourceResolver));
					if (!pageRedirectUrl.isEmpty()) {
						LOG.debug("tabbedMenuLink link set to the menu link page's redirect url instead of the menu link page's url");
					}
					LOG.debug("tabbedMenuLink link set to {}", tabbedMenuLink.getLink());

					String title = CommonUtil.getStringProperty(pageProperties, CommonConstants.TEASER_TITLE);
					tabbedMenuLink.setText(StringUtils.isNotBlank(title) ? title : CommonUtil.getLinkTitle(null, page.getPath(), resourceResolver));
					LOG.debug("tabbedMenuLink title set to {}", tabbedMenuLink.getText());
					tabbedMenuLink.setTarget(target);
					LOG.debug("tabbedMenuLink target set to {}", tabbedMenuLink.getTarget());
					LOG.debug("tabbedMenuLink for ({}) populated successfully", page.getPath());
				}
			}
		} catch (ResourceNotFoundException e) {
			LOG.error("TabbedMenuLink not populated : ", e);
		}
		
		LOG.debug("TabbedMenuListModel : populateSingleTabbedMenuLink : END");
		return tabbedMenuLink;
	}
	
	/* 
	* Verify date properties are valid in page's page properties
	* 
	* @param pagePath
	* @param pageProperties
	* @return true if the passed page properties have valid last replicated, last modified and last rolled out date values
	 */
	private static boolean verifyPageDateProperties(String pagePath, ValueMap pageProperties){
		LOG.debug("TabbedMenuListModel : verifyPageDateProperties : START");
		
		List<String> dateProperties = new ArrayList<>();
		dateProperties.add(CommonConstants.CQ_LAST_MODIFIED);
		
		for (String datePropertyName : dateProperties) {
			String datePropertyValue = pageProperties.get(datePropertyName, String.class);
			if (null == datePropertyValue || datePropertyValue.isEmpty()) {
				LOG.warn("{} property is blank for {}", datePropertyName, pagePath);
				LOG.debug("Date properties not valid. Returning false.");
				LOG.debug("TabbedMenuListModel : verifyPageDateProperties : END");
				return false;
			}
		}

		LOG.debug("Date properties valid for {}. Returning true.", pagePath);
		LOG.debug("TabbedMenuListModel : verifyPageDateProperties : END");
		return true;
	}

	/*
	 * Does page contain all tags passed
	 *
	 * @param page
	 * @param tags
	 * @return true if all tags exist for page
	 */
	private boolean isPageTagged(Page page) {
		LOG.debug("TabbedMenuListModel : isPageTagged : START");
		
		boolean isTagged = false;
		List tagsList = Arrays.asList(tagsFormatted).get(0);

		if (null != adminService) {

			try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
				TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);
				if (null != tagManager) {
					Resource pageContentResource = page.getContentResource();
					Tag[] pageTags = tagManager.getTags(pageContentResource);
					String pageTagsString = Arrays.toString(pageTags).replace(CommonConstants.OPEN_SQUARE_BRACKET,StringUtils.EMPTY).replace(CommonConstants.CLOSE_SQUARE_BRACKET,StringUtils.EMPTY);
					String[] pageTagsStringArray = pageTagsString.split(StringUtils.join(CommonConstants.COMMA, CommonConstants.SPACE_STRING));
					isTagged = Arrays.asList(pageTagsStringArray).containsAll(tagsList);
				}
			}

		}

		LOG.debug("TabbedMenuListModel : isPageTagged : END");
		return isTagged;
	}

	/**
	 * Sort the tabsList by the sort type selected in the tabbed menu model's configuration
	 */
	private void sortList() {
		Collections.sort(tabsList, (listItem1, listItem2) -> {
			int sortedResult = sortItems(listItem1, listItem2);
			return sortedResult;
		});
	}
	
	private int sortItems(TabbedMenuItem listItem1, TabbedMenuItem listItem2) {
		int sortedResult = 0;
		switch (tabbedMenuConfig.getSort()) {
			case CommonConstants.PUBLISH_DATE:
				sortedResult = sortByPublicationDate(listItem1, listItem2, sortedResult);
				break;
			case CommonConstants.CREATED_DT:
				sortedResult = CommonUtil.getDatefromString(listItem1.getCreatedDate()).compareTo(CommonUtil.getDatefromString(listItem2.getCreatedDate()));
				break;
			case CommonConstants.LAST_MOD_DT:
				sortedResult = CommonUtil.getDatefromString(listItem1.getLastModifiedDate()).compareTo(CommonUtil.getDatefromString(listItem2.getLastModifiedDate()));
				break;
			case CommonConstants.TEMPLATE:
				sortedResult = listItem1.getTemplateName().compareTo(listItem2.getTemplateName());
				break;
			default:
				sortedResult = langCollator.compare(listItem1.getTitle(), listItem2.getTitle());
				break;
		}
		return sortedResult;
	}
	
	/*
	* Sort by each TabbedMenuItem's publication date
	* 
	* @param listItem1 - first TabbedMenuItem
	* @param listItem2 - second TabbedMenuItem
	* @param sortedResult - result number
	* @return sortedResult - result number to return
	 */
	private static int sortByPublicationDate(TabbedMenuItem listItem1, TabbedMenuItem listItem2, int sortedResult) {
		if (!listItem1.getPublicationDate().isEmpty() && !listItem2.getPublicationDate().isEmpty()) {
			sortedResult = CommonUtil.getDatefromString(listItem1.getPublicationDate()).compareTo(CommonUtil.getDatefromString(listItem2.getPublicationDate()));
		} else {
			if (listItem1.getPublicationDate().isEmpty() && !listItem2.getPublicationDate().isEmpty()) {
				sortedResult = listItem2.getPublicationDate().length();
			}
			if (!listItem1.getPublicationDate().isEmpty() && listItem2.getPublicationDate().isEmpty()) {
				sortedResult = listItem1.getPublicationDate().length();
			}
		}
		return sortedResult;
	}

	/**
	 * Gets the tabbed menu items.
	 *
	 * @return the tabsList
	 */
	public List<TabbedMenuItem> getTabsList() {
		return tabsList;
	}

	/**
	 * Gets the uniqueId.
	 *
	 * @return the uniqueId
	 */
	public String getUniqueId() {
		return uniqueId;
	}

}
