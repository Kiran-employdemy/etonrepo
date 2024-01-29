package com.eaton.platform.core.models.categoryhighlightarticle;

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
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.categoryhighlight.CategoryHighlightArticleBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.util.CommonUtil;

/**
 * <html> Description: This Sling Model used in CategoryHighlightArticleHelper class. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CategoryHighlightFeatureCardModel {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CategoryHighlightFeatureCardModel.class);

    /**
     * The Constant ANY.
     */
    private static final String ANY = "any";

    /**
     * The Constant SIMPLE_DATE_FORMAT.
     */
    private static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * The Constant DATE_FORMAT_PUBLISH.
     */
    private static final String DATE_FORMAT_PUBLISH = "MMMM d, yyyy HH:mm:ss.SSSXXX";

    /**
     * The list type.
     */
    @Inject @Via("resource")
    @Named("cardListType")
    private String listType;

    /**
     * The parent page.
     */
    @Inject @Via("resource")
    @Named("cardParentPage")
    private String parentPage;

    /**
     * The tags.
     */
    @Inject @Via("resource")
    @Named("cardTags")
    private String[] tags;

    /**
     * The tags type.
     */
    @Inject @Via("resource")
    @Named("cardTagsType")
    private String tagsType;

    /**
     * The new window.
     */
    @Inject @Via("resource")
    @Named("cardOpenNewWindow")
    private String newWindow;

    /**
     * The alignment desktop.
     */
    @Inject @Via("resource")
    @Named("cardAlignmentDesktop")
    private String alignmentDesktop;

    /**
     * The alignment mobile.
     */
    @Inject @Via("resource")
    @Named("cardAlignmentMobile")
    private String alignmentMobile;

    /**
     * The Mobile Transforms.
     */
    @Inject @Via("resource")
    @Named("featuremobileTrans")
    private String mobileTrans;

    /**
     * The desktop trans.
     */
    @Inject @Via("resource")
    @Named("featuredesktopTrans")
    private String desktopTrans;

    /**
     * The tablet trans.
     */
    @Inject @Via("resource")
    @Named("featuretabletTrans")
    private String tabletTrans;

    /**
     * The fixed links.
     */
    @Inject @Via("resource")
    private Resource cardFixedLinks;
    /**
     * The ImageManual links.
     */
    @Inject @Via("resource")
    private Resource cardManualLinkList;

    /**
     * The resource page.
     */
    @Inject @Via("resource")
    private Page resourcePage;


    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;


    /**
     * The count.
     */
    @Inject @Via("resource")
    @Named("cardMaxLimitToDisplay")
    private int maxLimitToDisplay;

    /**
     * The sort.
     */
    @Inject @Via("resource")
    @Named("cardSortBy")
    private String sortBy;
    
    @EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;
    
    private boolean isUnitedStatesDateFormat = false;

    /**
     * The manual links list.
     */
    private List<CategoryHighlightManualLinksModel> manualLinksList;

    /**
     * The fixed links list.
     */
    private List<CategoryHighlightFixedLinksModel> fixedLinksList;

    /**
     * The child page links list.
     */
    private List<CategoryHighlightArticleBean> childPageAndTagsLinksList;

    /**
     * Inits the.
     */
    @PostConstruct
    protected void init() {
    	LOG.debug("CategoryHighlightFeatureCardModel :: init() :: Started");
    	if (siteResourceSlingModel.isPresent()) {
    		isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
		}
    	if(isNotNullOrEmpty(listType)){
	    	switch (listType) {
	            case CommonConstants.CHILD_PAGES: 
	                populateSortedParentModel();
	                break;
	            case CommonConstants.TAGS: 
	                populateSortedTagModel();
	                break;
	            case CommonConstants.FIXED_LIST:
	                populateFixLinkedModel();
	                break;
	            case CommonConstants.MANUAL_LIST: 
	            	populateManualLinkListModel();
	                break;
	            default:
	                break;
	        }
    	}
        LOG.debug("CategoryHighlightFeatureCardModel :: init() :: Exited");
    }


	/**
	 * populateManualLinkListModel
	 */
	private void populateManualLinkListModel() {
		LOG.debug("CategoryHighlightFeatureCardModel :: MANUAL_LIST::populateManualLinkListModel: Started");
		manualLinksList = new ArrayList<>();
		if (null != cardManualLinkList) {
		    populateManualLinkModel(manualLinksList, cardManualLinkList);
		}
		LOG.debug("CategoryHighlightFeatureCardModel :: MANUAL_LIST::populateManualLinkListModel: Exited");
	}

	/**
	 * populateFixLinkedModel
	 */
	private void populateFixLinkedModel() {
		LOG.debug("CategoryHighlightFeatureCardModel :: FIXED_LIST::populateFixLinkedModel: Started");
		fixedLinksList = new ArrayList<>();
		if (null != cardFixedLinks) {
		    populateFixedLinkModel(fixedLinksList, cardFixedLinks);
		}
		LOG.debug("CategoryHighlightFeatureCardModel :: FIXED_LIST::populateFixLinkedModel: Exited");
	}

	/**
	 * populateSortedTagModel
	 */
	private void populateSortedTagModel() {
		LOG.debug("CategoryHighlightFeatureCardModel :: TAGS::populateSortedTagModel: Started");
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != this.tags) {
		    populateTagsModel(childPageAndTagsLinksList, tags, resourceResolver);
		    // sort the list when author has either selected Parent Page or
		    // Tag
		    getSortedList(childPageAndTagsLinksList);
		}
		LOG.debug("CategoryHighlightFeatureCardModel :: TAGS::populateSortedTagModel: Exited");
	}

	/**
	 * PopulateSortedParentModel
	 */
	private void populateSortedParentModel() {
		LOG.debug("CategoryHighlightFeatureCardModel :: CHILD_PAGES ::populateSortedParentModel: Started");
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != getParentPagePath()) {
		    populateParentModel(childPageAndTagsLinksList,
		            getParentPagePath(), resourceResolver);
		    // sort the list when author has either selected Parent Page or
		    // Tag
		    getSortedList(childPageAndTagsLinksList);
		}
		LOG.debug("CategoryHighlightFeatureCardModel :: CHILD_PAGES::populateSortedParentModel: Exited");
		
	}

    /**
     * Populate parent model.
     *
     * @param childPageAndTagsLinksList the child page and tags links list
     * @param parentPagePath            the parent page path
     * @param resourceResolver of ResourceResolver
     *
     * @return the list
     */
    public List<CategoryHighlightArticleBean> populateParentModel(
            List<CategoryHighlightArticleBean> childPageAndTagsLinksList,
            String parentPagePath, ResourceResolver resourceResolver) {
    	LOG.debug("CategoryHighlightFeatureCardModel :: populateParentModel :: Started");
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page parentPageOfFeatureCard = null;
        if (pageManager != null) {
        	parentPageOfFeatureCard = pageManager.getPage(parentPagePath);
        }
        if (null != parentPageOfFeatureCard) {
            Iterator<Page> childPages = parentPageOfFeatureCard.listChildren();
            while (childPages.hasNext()) {
                Page childPage = childPages.next();
                setFields(childPageAndTagsLinksList, childPage, resourceResolver);
            }
        }
        LOG.debug("CategoryHighlightFeatureCardModel :: populateParentModel :: Exited");
        return childPageAndTagsLinksList;
    }

    /**
     * Populate tags model.
     *
     * @param childPageAndTagsLinksList the child page and tags links list
     * @param tags                      the tags
     * @param resourceResolver of class ResourceResolver
     *
     * @return the list
     */
    public List<CategoryHighlightArticleBean> populateTagsModel(
            List<CategoryHighlightArticleBean> childPageAndTagsLinksList,
            String[] tags, ResourceResolver resourceResolver) {
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        RangeIterator<Resource> tagsIterator = null;
        if (tagManager != null) {
            boolean oneMatchIsEnough = StringUtils.equals(ANY, tagsType);
            tagsIterator = tagManager.find(CommonUtil.getHomePagePath(this.resourcePage), tags, oneMatchIsEnough);
            if (null != tagsIterator) {
                while (tagsIterator.hasNext()) {
                    Resource tagResource = tagsIterator.next();
                    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                    Page tagPage = null;
                    if (pageManager != null) {
                        tagPage = pageManager.getPage(StringUtils.removeEnd(tagResource.getPath(), JcrConstants.JCR_CONTENT));
                    }
                    if (null != tagPage && CommonUtil.startsWithAnySiteContentRootPath(tagResource.getPath())) {
                        setFields(childPageAndTagsLinksList, tagPage, resourceResolver);
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
     * @param resource        the resource
     *
     * @return the list
     */
    public List<CategoryHighlightManualLinksModel> populateManualLinkModel(
            List<CategoryHighlightManualLinksModel> manualLinksList,
            Resource resource) {
        if (null != resource) {
            Iterator<Resource> linkResources = resource.listChildren();
            while (linkResources.hasNext()) {
                CategoryHighlightManualLinksModel manualLink = linkResources.next().adaptTo(CategoryHighlightManualLinksModel.class);
                manualLink.setUnitedStatesDateFormat(isUnitedStatesDateFormat);
                if (null != manualLink) {
                    manualLink.setAlignmentDesktop(alignmentDesktop);
                    manualLink.setAlignmentMobile(alignmentMobile);
                    manualLink.setDesktopTransformedUrl(getDesktopTrans());
                    manualLink.setMobileTransformedUrl(getMobileTrans());
                    manualLink.setTabletTransformedUrl(getTabletTrans());
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
     * @param resource       the resource
     *
     * @return the list
     */
    public List<CategoryHighlightFixedLinksModel> populateFixedLinkModel(
            List<CategoryHighlightFixedLinksModel> fixedLinksList,
            Resource resource) {
        if (null != resource) {
            Iterator<Resource> linkResources = resource.listChildren();
            while (linkResources.hasNext()) {
                CategoryHighlightFixedLinksModel fixedLink = linkResources.next().adaptTo(CategoryHighlightFixedLinksModel.class);
                fixedLink.setUnitedStatesDateFormat(isUnitedStatesDateFormat);
                if (null != fixedLink) {
                    fixedLink.setAlignmentDesktop(alignmentDesktop);
                    fixedLink.setAlignmentMobile(alignmentMobile);
                    fixedLink.setDesktopTransformedUrl(getDesktopTrans());
                    fixedLink.setMobileTransformedUrl(getMobileTrans());
                    fixedLink.setTabletTransformedUrl(getTabletTrans());
                    fixedLinksList.add(fixedLink);
                }
            }
        }
        return fixedLinksList;
    }

    /**
     * Sets the fields.
     *
     * @param array                     the array
     * @param page
     * @param adminReadResourceResolver
     */
    private void setFields(List<CategoryHighlightArticleBean> array, Page page, ResourceResolver adminReadResourceResolver) {
        CategoryHighlightArticleBean cardTypeBean = new CategoryHighlightArticleBean();
        ValueMap properties = page.getProperties();
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
            cardTypeBean.setTabletTransformedUrl(getTabletTrans());
            cardTypeBean.setHeadline(StringUtils.isNotBlank(teaserTitle) ? teaserTitle
                    : CommonUtil.getLinkTitle(null, page.getPath(), adminReadResourceResolver));
            cardTypeBean.setPagePath(page.getPath());
            cardTypeBean.setEyebrow(CommonUtil.getStringProperty(properties, CommonConstants.EYEBROW_TITLE));
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
     *
     * @return the sorted list
     */
    private void getSortedList(List<CategoryHighlightArticleBean> list) {

        Collections.sort(list, new Comparator<CategoryHighlightArticleBean>() {
            public int compare(CategoryHighlightArticleBean linkList1,
                                   CategoryHighlightArticleBean linkList2) {
                int comparisonValue = 0;
                comparisonValue = compareLinkListFields(linkList1, linkList2, comparisonValue);
                return comparisonValue;
                }
        
        });
    }

    /**
     * Gets the list type.
     *
     * @return the list type
     */
    /*
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
        String newWindowVal = StringUtils.EMPTY;
        if (StringUtils.equals(CommonConstants.TRUE, this.newWindow)) {
        	newWindowVal = CommonConstants.TARGET_BLANK;
        }
        return newWindowVal;
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
     * Gets the alignment mobile.
     *
     * @return the alignment mobile
     */
    public String getAlignmentMobile() {
        return alignmentMobile;
    }

    /**
     * Gets the mobile trans.
     *
     * @return the mobileTrans
     */
    public String getMobileTrans() {
        if (null == mobileTrans) {
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
        if (null == desktopTrans) {
            desktopTrans = StringUtils.EMPTY;
        }
        return desktopTrans;
    }

    /**
     * Gets the tablet trans.
     *
     * @return the tabletTrans
     */
    public String getTabletTrans() {
        if (null == tabletTrans) {
            tabletTrans = StringUtils.EMPTY;
        }

        return tabletTrans;
    }

    /**
     * Gets the parentPage.
     *
     * @return the parentPage
     */
    public String getParentPagePath() {
        String parentPagePath = parentPage;
        if (null == parentPagePath) {
            parentPagePath = resourcePage.getPath();
        }
        return parentPagePath;
    }

    /**
     * Gets the Single Feature Card Bean.
     *
     * @return the list type array
     */
    public Object getSingleFeatureCard() {
        List<?> viewList = new ArrayList<>();
        if (StringUtils.equals(CommonConstants.CHILD_PAGES, listType)
                || StringUtils.equals(CommonConstants.TAGS, listType)) {
            viewList = childPageAndTagsLinksList;
        } else if (StringUtils.equals(CommonConstants.FIXED_LIST, listType)) {
            viewList = fixedLinksList;
        } else if (StringUtils.equals(CommonConstants.MANUAL_LIST, listType)) {
            viewList = manualLinksList;
        }else{
        	LOG.info("List Type is null");
         }
        
        return viewList.isEmpty()?viewList :viewList.get(0);
    }
    
    /**
	 * @param linkList1
	 * @param linkList2
	 * @param comparisonValue
	 * @return
	 */
	private int compareLinkListFields(CategoryHighlightArticleBean linkList1,
			CategoryHighlightArticleBean linkList2, int comparisonValue) {
		switch (sortBy) {
            case CommonConstants.TITLE: 
                comparisonValue = compareString(linkList1.getHeadline(), linkList2.getHeadline());
                break;                    
            case CommonConstants.PUBLISH_DATE: 
                comparisonValue = comparePublicationDate(linkList1, linkList2, comparisonValue);
                break;                    
            case CommonConstants.CREATED_DT: 
                comparisonValue = compareDateString(linkList1.getCreatedDate(), linkList2.getCreatedDate());
                break;                    
            case CommonConstants.LAST_MOD_DT: 
                comparisonValue = compareDateString(linkList1.getLastModifiedDate(),(linkList2.getLastModifiedDate()));
                break;                    
            case CommonConstants.TEMPLATE: 
                comparisonValue = compareString(linkList1.getTemplateName(),linkList2.getTemplateName());
                break;                    
            default:
            	comparisonValue = 0;                   
        }
		return comparisonValue;
	}
	/**
	 * @param linkList1
	 * @param linkList2
	 * @param comparisonValue
	 * @return
	 */
	private int comparePublicationDate(CategoryHighlightArticleBean linkList1,
			CategoryHighlightArticleBean linkList2, int comparisonValue) {
		DateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PUBLISH, Locale.ENGLISH);
		return getFormatedDate(linkList1.getPublicationDate(), linkList2.getPublicationDate(), comparisonValue,
				simpleDateFormat);
	}

	/**
	 * @param linkList1
	 * @param linkList2
	 * @param comparisonValue
	 * @param simpleDate
	 * @return
	 */
	private int getFormatedDate(String pubDate, String pubDateCompareTo,
			int comparisonValue, DateFormat simpleDateFormat) {
		Date parsedPubDate;
		Date parsedPubDateCompareTo;
		try {
			parsedPubDate = simpleDateFormat.parse(pubDate);
			parsedPubDateCompareTo = simpleDateFormat.parse(pubDateCompareTo);
		    comparisonValue = -1 * (parsedPubDate.compareTo(parsedPubDateCompareTo));
		} catch (ParseException e) {
		    LOG.error("CategoryHighlightArticleModel | Unable to parse the date %s", e);
		}
		return comparisonValue;
	}

	/**
	 * @param linkList1
	 * @param linkList2
	 * @return
	 */
	private int compareDateString(String compareDate,
			String compareDateTo) {
		return -1 * (compareDate.compareTo(compareDateTo));
	}

	/**
	 * @param linkList1
	 * @param linkList2
	 * @return
	 */
	private int compareString(String compareString,
			String compareToString) {
		return compareString.compareToIgnoreCase(compareToString);
	}

	/**
	 * isNotNullOrEmpty
	 * @return boolean
	 */
	private boolean isNotNullOrEmpty(String stringValue) {
		return stringValue!=null && !stringValue.isEmpty();
	}

}
