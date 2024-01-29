package com.eaton.platform.core.models.verticallinklist;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.VerticalLinkListBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <html> Description: This Sling Model used in VerticalLinkListHelper class. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class VerticalLinkListModel{ 

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(VerticalLinkListModel.class);
	
	/** The Constant ANY. */
	private static final String ANY = "any";

	/** The Constant SIMPLE_DATE_FORMAT. */
	private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** The Constant DATE_FORMAT_PUBLISH. */
    private static final  String DATE_FORMAT_PUBLISH = "MMMM d, yyyy HH:mm:ss.SSSXXX";

	/** The manual links list. */
	private List<VerticalLinkManualLinksModel> manualLinksList;

	/** The fixed links list. */
	private List<VerticalLinkFixedLinksModel> fixedLinksList;

	/** The child page links list. */
	private List<VerticalLinkListBean> childPageAndTagsLinksList;

	/** The vertical header title. */
	@Inject @Via("resource")
	private String verticalHeaderTitle;

	@Inject
	private AuthorizationService authorizationService;

	/** The view. */
	@Inject @Via("resource")
	private String view;
	
	/** The header. */
	@Inject @Via("resource")
	private String header;

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
	@Inject @Via("resource")
	@Named("openNewWindow")
	private String newWindow;

	/** The fixed links. */
	@Inject @Via("resource")
	private Resource fixedLinks;

	/** The  ImageManual links. */
	@Inject @Via("resource")
	private Resource manualLinkList;

	/** The maxLimitToDisplay. */
	@Inject @Via("resource")
	private int maxLimitToDisplay;

	/** The enable teaser content. */
	@Inject @Via("resource")
	private String enableTeaserContent;

	/** The sort. */
	@Inject @Via("resource")
	private String sortBy;

	/** The maxLimitInEachCol. */
	@Inject @Via("resource")
	private int maxLimitInEachCol;

	/** The links alignment. */
	@Inject @Via("resource")
	private String linksAlignment;

	/** The enable inner grid. */
	@Inject @Via("resource")
	private String enableInnerGrid;

	@Inject @Via("resource")
	@Default(values = "false")
	private boolean overrideFooter;

	 /** The resource page. */
    @Inject
    private Page resourcePage;
    
    @Inject @ScriptVariable
    private Page currentPage;
    
    @Inject @Via("resource")
    private Resource res;
    
    @Inject @Source("sling-object")
    private SlingHttpServletRequest slingRequest;

	@Inject  @Via("resource") @Default(values = "false")
	private String enableSourceTracking;
	
    /** The Constant FOOTER_VERTICAL_LINK_LIST_1_SELECTOR. */
	private static final String FOOTER_VERTICAL_LINK_LIST_1_SELECTOR = "footer-link-list1";
	
	/** The Constant FOOTER_VERTICAL_LINK_LIST_2_SELECTOR. */
	private static final String FOOTER_VERTICAL_LINK_LIST_2_SELECTOR = "footer-link-list2";
	
	/** The Constant LINKS_VIEW_MEGA_MENU. */
	private static final String LINKS_VIEW_MEGA_MENU = "links";
	
	/** The link view list. */
	private List<List<?>> linkViewList;

	@Inject @Source("sling-object")
	private ResourceResolver resourceResolver;

	private AuthenticationToken authenticationToken;

	Collator langCollator ;

	/** The resource resolver. */
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {

		Locale languageValue = currentPage.getLanguage(false);
		if ((languageValue != null) && (languageValue.getLanguage() != null) && (!languageValue.getLanguage().isEmpty())) {
			langCollator = Collator.getInstance(new Locale(languageValue.getLanguage(), languageValue.getCountry()));
		}
		
		//EAT-5280 - Set default view to links(Mega Menu) only if listType is configured.
		if (isSecureLinkedList() && StringUtils.isNotBlank(getListType()) ){
			this.view = LINKS_VIEW_MEGA_MENU;
		}
		if (null != resourcePage){
			LOG.debug("VerticalLinkListModel :: setComponentValues() :: Started");
			String selector = null;
			Resource linkListRes = null;
			final Page homePage = CommonUtil.getHomePage(currentPage);
			authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);

			// get selector from footer passed while including vertical link list component
			selector = slingRequest.getRequestPathInfo().getSelectorString();
			/* if selector is available, component is statically included in footer of home page template,
			* in pages other than homepage, vertical listlink component needs to be derived programmatically because vertical linklist 
			*resources are not present under page resources but inherited from home page
			*/
			if(null != selector){
				switch(selector) {
					case FOOTER_VERTICAL_LINK_LIST_1_SELECTOR :
						linkListRes = resourceResolver.getResource(homePage.getPath().concat("/jcr:content/root/footer/link-list-2nd-col"));
						break;
					case FOOTER_VERTICAL_LINK_LIST_2_SELECTOR :
						linkListRes = resourceResolver.getResource(homePage.getPath().concat("/jcr:content/root/footer/link-list-3rd-col"));
						break;
					default:
						LOG.debug("Default Value of the switch case");
						linkListRes = res;
						break;
				}
			} else {
				linkListRes = res;
			}
			
	        LOG.debug("VerticalLinkListModel :: setComponentValues() :: Exit");
			switch(getListType()) {
                case CommonConstants.CHILD_PAGES :
                	childPageChildList();
                    break;
                case CommonConstants.TAGS :
                    childPageTagList();
                    break;
                case CommonConstants.FIXED_LIST :
                    fixedLinksList = new ArrayList<>();
                    if (null != fixedLinks ) {
                        populateFixedLinkModel(fixedLinksList, fixedLinks);
                    }
                    break;
                case CommonConstants.MANUAL_LIST :
                    manualLinksList = new ArrayList<>();
                    if ((null != manualLinkList)) {
                        populateManualLinkModel(manualLinksList, manualLinkList);
                    }
                    break;
                default:
                    break;
            }
			if(null != linkListRes && StringUtils.equals(LINKS_VIEW_MEGA_MENU, getView())) {
				getColumnLinksList();
			}
			
		}
	}
	/* This method will 
	 * return the
	 * childPageAndTagsLinksList for tags 
	 */
	private void childPageTagList() {
		// This method will return the childPageAndTagsLinksList for tags 
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != tags) {
			LOG.debug("tags list called");
			populateTagsModel(childPageAndTagsLinksList, tags, resourceResolver);
			// sort the list when author has either selected Parent Page or Tag
			getSortedList(childPageAndTagsLinksList);
		}
	}
	/*This method will 
	 * return the
	 *  childPageAndTagsLinksList for child
	 * */ 
	private void childPageChildList() {
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != getParentPagePath()) {
			LOG.debug("child page list called");
			populateParentModel(childPageAndTagsLinksList, getParentPagePath(),
					resourceResolver);
			// sort the list when author has either selected Parent Page or Tag
			getSortedList(childPageAndTagsLinksList);
		}
		
	}

	/**
	 * Gets the column links list.
	 *
	 * @return the column links list
	 */

	private void getColumnLinksList() {
		linkViewList = new ArrayList<>();
		int columnCount = getMaxLimitInEachCol() == 0 ? 10 : getMaxLimitInEachCol();
		List<?> typeList = getListTypeArray();
		int typeListSize = getListTypeArray().size();
		
		if(columnCount*4 < (typeListSize-1)) {
			if((typeListSize-1) % 4 == 0) {
				columnCount = (typeListSize-1)/4;
			} else {
				columnCount = ((typeListSize-1)/4)+1;
			}
		}
		
		int listColumnCount = 1;
		if(typeListSize< columnCount+2) {
			linkViewList.add(typeList);
		} else if(typeList.size() > columnCount+1 && typeList.size() < (2*columnCount)+2) {
			linkViewList.add(typeList.subList(0, columnCount+1));
			linkViewList.add(typeList.subList(columnCount+1, typeList.size()));
			listColumnCount = 2;
		} else if((typeList.size()> 2*columnCount+1) && typeList.size() < (3*columnCount)+2) {
			linkViewList.add(typeList.subList(0, columnCount+1));
			linkViewList.add(typeList.subList(columnCount+1,2*columnCount+1));
			linkViewList.add(typeList.subList(2*columnCount+1, typeList.size()));
			listColumnCount = 3;
		} else {
			linkViewList.add(typeList.subList(0, columnCount+1));
			linkViewList.add(typeList.subList(columnCount+1,2*columnCount+1));
			linkViewList.add(typeList.subList(2*columnCount+1,3*columnCount+1));
			linkViewList.add(typeList.subList(3*columnCount+1, typeList.size()));
			listColumnCount = 4;
		}
		slingRequest.setAttribute("listColumnCount", listColumnCount);
	}
	

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.verticallinklist.VerrticalModelListService#populateParentModel(java.util.List, java.lang.String, org.apache.sling.api.resource.ResourceResolver)
	 */
	public List<VerticalLinkListBean> populateParentModel(List<VerticalLinkListBean> childPageAndTagsLinksList,
														  String parentPagePath, ResourceResolver resourceResolver) {
		if (null != resourceResolver) {
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			Page parentPageModel = null;
			if(null != pageManager){
                parentPageModel=pageManager.getPage(parentPagePath);
            }
			if (null != parentPageModel) {
                Iterator<Page> childPages = parentPageModel.listChildren();
                while (childPages.hasNext()) {
					Page childPage = childPages.next();
					if (SecureUtil.isAuthorisedLink(childPage.getContentResource(), authorizationService, slingRequest)) {
						ValueMap childProps = childPage.getProperties();
						final Set<String> instanceRunmode = CommonUtil.getRunModes();
						String lastReplicated = childProps.get("cq:lastReplicated", String.class);
						String lastModified = childProps.get("cq:lastModified", String.class);
						String lastRolledOut = childProps.get("cq:lastRolledout", String.class);
						if (instanceRunmode.contains(Externalizer.AUTHOR)) {
							setFields(childPageAndTagsLinksList, childPage, resourceResolver);
							LOG.debug("Set fields of author instance");
						} else if ((instanceRunmode.contains(Externalizer.PUBLISH)) && (lastReplicated != null || lastModified != null || lastRolledOut != null)) {
							setFields(childPageAndTagsLinksList, childPage, resourceResolver);
						}
					}
				}
            }
		}
		return childPageAndTagsLinksList;
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.verticallinklist.VerrticalModelListService#populateTagsModel(java.util.List, java.lang.String[], org.apache.sling.api.resource.ResourceResolver)
	 */
	public List<VerticalLinkListBean> populateTagsModel(List<VerticalLinkListBean> childPageAndTagsLinksList, String[] tags, ResourceResolver resourceResolver) {
		if (null != resourceResolver) {
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			RangeIterator<Resource> tagsIterator = null;
			if(null != tagManager) {
                boolean oneMatchIsEnough = StringUtils.equals(ANY, this.tagsType) ? true : false;
                tagsIterator = tagManager.find(CommonUtil.getHomePagePath(this.resourcePage), tags, oneMatchIsEnough);
                if(null != tagsIterator) {
                    while (tagsIterator.hasNext()) {
                    	childPageAndTagsList(tagsIterator);
                       
                        }
                }
            }
		}
		return childPageAndTagsLinksList;
	}

	 private void childPageAndTagsList(RangeIterator<Resource> tagsIterator) {
		// Returns childPageAndTagsLinksList
		final Resource tagResource =  tagsIterator.next();
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
             Page tagPage = null;
             if (null != pageManager) {
                 tagPage = pageManager.getPage(StringUtils.removeEnd(tagResource.getPath(), JcrConstants.JCR_CONTENT));
             }
             if (null != tagPage && CommonUtil.startsWithAnySiteContentRootPath(tagResource.getPath()) &&
					 SecureUtil.isAuthorisedLink(tagPage.getContentResource(),authorizationService,slingRequest)) {
                     setFields(childPageAndTagsLinksList, tagPage, resourceResolver);
             }
		
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.models.verticallinklist.VerrticalModelListService#populateManualLinkModel(java.util.List, org.apache.sling.api.resource.Resource)
	 */
	public List<VerticalLinkManualLinksModel> populateManualLinkModel(List<VerticalLinkManualLinksModel> manualLinksList, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				VerticalLinkManualLinksModel manualLink = linkResources.next().adaptTo(VerticalLinkManualLinksModel.class);
				if (null != manualLink && !CommonUtil.isExternalURL(manualLink.getPagePath())) {
					//EAT-4774 -  Exclude secure Page/Asset from primary navigation list
					Resource linkPathResource = CommonUtil.getResourceFromLinkPath(manualLink.getPagePath(),resourceResolver);
					manualLink.setSecure(SecureUtil.isSecureResource(linkPathResource));
					if(linkPathResource != null && SecureUtil.isAuthorisedLink(linkPathResource,authorizationService,slingRequest)) {
						manualLink.setPagePath(CommonUtil.dotHtmlLink(manualLink.getPagePath()));
                        manualLinksList.add(manualLink);
				    }
				}else{
					if (null != manualLink && CommonUtil.isExternalURL(manualLink.getPagePath())) {
						manualLink.setPagePath(CommonUtil.dotHtmlLink(manualLink.getPagePath()));
					}
					manualLinksList.add(manualLink);
				}
			}
		}
		return manualLinksList;
	}

	 /* (non-Javadoc)
	 * @see com.eaton.platform.core.models.verticallinklist.VerrticalModelListService#populateFixedLinkModel(java.util.List, org.apache.sling.api.resource.Resource)
	 */
	public List<VerticalLinkFixedLinksModel> populateFixedLinkModel(List<VerticalLinkFixedLinksModel> fixedLinksList, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				VerticalLinkFixedLinksModel fixedLink = linkResources.next().adaptTo(VerticalLinkFixedLinksModel.class);
				if (null != fixedLink  &&  !CommonUtil.isExternalURL(fixedLink.getPagePath())) {
					//EAT-4774 -  Exclude secure Page from primary navigation list
					Resource linkPathResource = CommonUtil.getResourceFromLinkPath(fixedLink.getPagePath(),resourceResolver);
					fixedLink.setSecure(SecureUtil.isSecureResource(linkPathResource));
					if(linkPathResource != null && SecureUtil.isAuthorisedLink(linkPathResource,authorizationService,slingRequest)) {
						fixedLink.setEnableTeaserContent(getEnableTeaserContent());
						fixedLink.setPagePath(CommonUtil.dotHtmlLink(fixedLink.getPagePath()));
						fixedLinksList.add(fixedLink);
					}
				}else{
					if (null != fixedLink && CommonUtil.isExternalURL(fixedLink.getPagePath())) {
						fixedLink.setPagePath(CommonUtil.dotHtmlLink(fixedLink.getPagePath()));
					}
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
	 * @param resourceResolver
	 */
	private void setFields(List<VerticalLinkListBean> array, Page page, ResourceResolver resourceResolver) {
		VerticalLinkListBean verticalLinkTypeBean = new VerticalLinkListBean();
	    ValueMap properties =  page.getProperties();
	      SimpleDateFormat dateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
	      SimpleDateFormat publicationDateFormat = new SimpleDateFormat(DATE_FORMAT_PUBLISH);
		//EAT-4774 -  Exclude secure Page/Asset from primary navigation list
		if (!page.isHideInNav()) {
			String teaserTitle;

			// check if enable teaser content is checked, if checked then get value from teaser tab > teaser title
			if(StringUtils.equals(CommonConstants.TRUE, getEnableTeaserContent())) {
				teaserTitle = CommonUtil.getStringProperty(properties, CommonConstants.TEASER_TITLE);
				if(StringUtils.isBlank(teaserTitle)) {
					teaserTitle = CommonUtil.getLinkTitle(null, page.getPath(), resourceResolver);
				}
			} else {
				teaserTitle = CommonUtil.getLinkTitle(null, page.getPath(), resourceResolver);
			}
			
			verticalLinkTypeBean.setHeadline(teaserTitle);
			verticalLinkTypeBean.setPagePath(CommonUtil.dotHtmlLink(page.getPath()));
			verticalLinkTypeBean.setDescriptionFeature(CommonUtil.getStringProperty(properties, CommonConstants.TEASER_DESCRIPTION));
			verticalLinkTypeBean.setTemplateName(page.getProperties().get(CommonConstants.TEMPLATE_PROP_KEY, StringUtils.EMPTY));
			verticalLinkTypeBean.setReplicationDate(CommonUtil.getDateProperty(properties, NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat));
			verticalLinkTypeBean.setPublicationDate(CommonUtil.getDateProperty(properties, CommonConstants.PUBLICATION_DATE, publicationDateFormat));
			verticalLinkTypeBean.setLastModifiedDate(CommonUtil.getDateProperty(properties, NameConstants.PN_PAGE_LAST_MOD, dateFormat));
			verticalLinkTypeBean.setCreatedDate(CommonUtil.getDateProperty(properties, CommonConstants.JCR_CREATED, dateFormat));
			verticalLinkTypeBean.setNewWindow(getNewWindow());
			verticalLinkTypeBean.setEnableSourceTracking(getEnableSourceTracking());
			verticalLinkTypeBean.setSecure(SecureUtil.isSecureResource(page.getContentResource()));
			array.add(verticalLinkTypeBean);
		}
	}

	/**
	 * this method is sorting the list based on the author selection.
	 *
	 * @param list the list
	 * @return the sorted list
	 */
	private void getSortedList(List<VerticalLinkListBean> list) {
		Collections.sort(list, new Comparator<VerticalLinkListBean>() {
			public int compare(VerticalLinkListBean linkList1,
					VerticalLinkListBean linkList2) {
          int comparisonValue = 0;
		  switch (sortBy) {
                case CommonConstants.TITLE:
				comparisonValue = langCollator.compare(linkList1.getHeadline(), linkList2.getHeadline());
				break;
				case CommonConstants.PUBLISH_DATE:
				comparisonValue=CommonUtil.compareDate(CommonUtil.getDateFormat(DATE_FORMAT_PUBLISH,linkList1.getPublicationDate()), CommonUtil.getDateFormat(DATE_FORMAT_PUBLISH,linkList2.getPublicationDate()));
				break;
				case CommonConstants.CREATED_DT:
                comparisonValue = -1* (linkList1.getCreatedDate().compareTo(linkList2.getCreatedDate()));
				break;
				case CommonConstants.LAST_MOD_DT:
				comparisonValue = -1* (linkList1.getLastModifiedDate().compareTo(linkList2.getLastModifiedDate()));
				break;
				case CommonConstants.TEMPLATE:
                comparisonValue = linkList1.getTemplateName().compareToIgnoreCase(linkList2.getTemplateName());
				break;
				default:
				 comparisonValue = 0;
				}
				return comparisonValue;
			}
		});
	}

	public boolean isOverrideFooter(){
		return overrideFooter;
	}

	/** Gets the list type.
	 *
	 * @return the
	 */
	public String getListType() {
		if(null == listType) {
			listType = StringUtils.EMPTY;
		}
		return listType;
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
	 * Gets the header.
	 *
	 * @return the header
	 */
	public String getVerticalHeaderTitle() {
		return verticalHeaderTitle;
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
	 * Gets the enable teaser content.
	 *
	 * @return the enable teaser content
	 */
	public String getEnableTeaserContent() {
		if(null == enableTeaserContent) {
			enableTeaserContent = CommonConstants.FALSE;
		}
		return enableTeaserContent;
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
		return CommonConstants.TRUE.equalsIgnoreCase(this.newWindow) ? CommonConstants.TARGET_BLANK : CommonConstants.TARGET_SELF;
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
	 * Gets the links alignment.
	 *
	 * @return the links alignment
	 */
	
	public String getLinksAlignment() {
		return linksAlignment;
	}

	/**
	 * Gets the parentPage.
	 *
	 * @return the parentPage
	 */
	
	public String getParentPagePath() {
		String parentPageValue = this.parentPage;
		if(null == parentPageValue) {
			parentPageValue = this.resourcePage.getPath();
		}
		return parentPageValue;
	}

	/**
	 * Gets the list type array.
	 *
	 * @return the list type array
	 */
	
	@SuppressWarnings("squid:S1452")
	public List<?> getListTypeArray() {
		List<?> viewList = new ArrayList<>();
		if(StringUtils.equals(CommonConstants.CHILD_PAGES, this.listType) || StringUtils.equals(CommonConstants.TAGS, this.listType)) {
			if(maxLimitToDisplay > childPageAndTagsLinksList.size()) {
				maxLimitToDisplay = childPageAndTagsLinksList.size();
			}
			viewList = childPageAndTagsLinksList.subList(0, maxLimitToDisplay);
		} else if(StringUtils.equals(CommonConstants.FIXED_LIST, this.listType)) {
			viewList = fixedLinksList;
		} else {
			viewList = manualLinksList;
		}
		return viewList;
	}

	/**
	 * Gets the max limit in each col.
	 *
	 * @return the max limit in each col
	 */
	public int getMaxLimitInEachCol() {
		return maxLimitInEachCol;
	}

	/**
	 * Gets the enable inner grid.
	 *
	 * @return the enable inner grid
	 */
	public String getEnableInnerGrid() {
		String isInnerGrid;
		if(null != enableInnerGrid) {
			isInnerGrid = "true";
		} else {
			isInnerGrid = "false";
		}
		return isInnerGrid;
	}
	/**
	* Gets the link view list.
	*
	* @return the link view list
	*/
	public List<List<?>> getLinkViewList() {
	return linkViewList;
	}

	/**
	 * //EAT-5280
	 * This method checks if component is secure-linked-list
	 * Returns True : If Component resourceType is SECURE_LINKED_LIST_RESOURCE_TYPE.
	 * Returns False : If Component resourceType is not equal to SECURE_LINKED_LIST_RESOURCE_TYPE
	 * @return
	 */
	public boolean isSecureLinkedList() {
		if(res != null) {
			final String resourceType = res.getResourceType();
			if (StringUtils.isNotBlank(resourceType)&& resourceType.equals(SecureConstants.SECURE_LINKED_LIST_RESOURCE_TYPE)) {
					return true;
				}
		}
		return false;
	}

	public String getEnableSourceTracking() {
		return enableSourceTracking;
	}
}
