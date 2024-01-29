/*
 * CategoryHighlightArticleModel class is bind with Category Highlight Article Component.
 *
 */
package com.eaton.platform.core.models.categoryhighlightarticle;

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
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <html> Description: This Sling Model used in CategoryHighlightArticleHelper class. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CategoryHighlightArticleModel {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CategoryHighlightArticleModel.class);

    /** The single feature card model. */
    private CategoryHighlightFeatureCardModel singleFeatureCardModel;

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
     * The enable featured card.
     */
    @Inject @Via("resource")
    private String enableFeaturedCard;

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
    private String mobileTrans;
    /**
     * The desktop trans.
     */
    @Inject @Via("resource")
    private String desktopTrans;
    /**
     * The Mobile Transforms.
     */
    @Inject @Via("resource")
    private String tabletTrans;
    /**
     * The resource page.
     */
    @Inject @Via("resource")
    private Page resourcePage;

    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Self
    private SlingHttpServletRequest slingHttpServletRequest;

    @Inject @Via("resource")
    private String enablePublicationDate;

    @EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;

    private boolean isUnitedStatesDateFormat = false;

	/**
     * Inits the.
     */

    @PostConstruct
	protected void init() {
    	LOG.debug("CategoryHighlightArticleModel :: init() :: Started");
    	if (siteResourceSlingModel.isPresent()) {
    		isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
		}
    	singleFeatureCardModel = slingHttpServletRequest.adaptTo(CategoryHighlightFeatureCardModel.class);
		if (StringUtils.isNotBlank(listType)) {
			switch (listType) {
			case CommonConstants.CHILD_PAGES:
				    populateSortedParentModel();
				    break;
			case CommonConstants.TAGS:
				    populateSortedTagsModel();
				    break;
			case CommonConstants.FIXED_LIST:
                    populateCardFixedModel();
				    break;
			case CommonConstants.MANUAL_LIST:
				    populateManualModel();
				    break;
			default:
				    break;
			}
		} else {
			LOG.debug("CategoryHighlightArticleModel ::init():: listType is blank");
		}
		LOG.debug("CategoryHighlightArticleModel :: init() :: Exited");
	}

	/**
	 *
	 */
	private void populateManualModel() {
		LOG.debug("CategoryHighlightArticleModel ::MANUAL_LIST:: populateManualModel :Started");
		manualLinksList = new ArrayList<>();
		if (null != cardManualLinkList) {
			populateManualLinkModel(manualLinksList, cardManualLinkList);
		}
		LOG.debug("CategoryHighlightArticleModel ::MANUAL_LIST:: populateManualModel :Exited");
	}

	/**
	 *
	 */
	private void populateCardFixedModel() {
		LOG.debug("CategoryHighlightArticleModel ::FIXED_LIST:: populateCardFixedModel :Started");
		fixedLinksList = new ArrayList<>();
		if (null != cardFixedLinks) {
			populateFixedLinkModel(fixedLinksList, cardFixedLinks);
		}
		LOG.debug("CategoryHighlightArticleModel ::FIXED_LIST:: populateCardFixedModel :Exited");
	}

	/**
	 *
	 */
	private void populateSortedTagsModel() {
		LOG.debug("CategoryHighlightArticleModel ::TAGS:: populateSortedTagsModel :Started");
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != this.tags) {
			populateTagsModel(childPageAndTagsLinksList, tags, resourceResolver);
			// sort the list when author has either selected Parent Page OR Tag
			getSortedList(this.childPageAndTagsLinksList);
		}
		LOG.debug("CategoryHighlightArticleModel ::TAGS:: populateSortedTagsModel :Exited");
	}

	/**
	 *
	 */
	private void populateSortedParentModel() {
		LOG.debug("CategoryHighlightArticleModel ::CHILD_PAGES:: populateSortedParentModel :Started");
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != getParentPagePath()) {
			populateParentModel(childPageAndTagsLinksList, getParentPagePath(), resourceResolver);
			getSortedList(childPageAndTagsLinksList);
		}
		LOG.debug("CategoryHighlightArticleModel ::CHILD_PAGES:: populateSortedParentModel :Exited");
	}

    /**
     * Gets the single feature card model.
     * @return the singleFeatureCardModel
     */
    public CategoryHighlightFeatureCardModel getSingleFeatureCardModel() {
        return singleFeatureCardModel;
    }

    /**
     * Populate parent model.
     *
     * @param childPageAndTagsLinksList the child page and tags links list
     * @param parentPagePath            the parent page path
     * @param resourceResolver the parameter of the class ResourceResolver
     *
     * @return the list
     */
	public List<CategoryHighlightArticleBean> populateParentModel(
			List<CategoryHighlightArticleBean> childPageAndTagsLinksList, String parentPagePath,
			ResourceResolver resourceResolver) {

		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		Page parentPageInPopMethod = null;
		if (pageManager != null) {
			parentPageInPopMethod = pageManager.getPage(parentPagePath);
		}
		if (null != parentPageInPopMethod) {
			Iterator<Page> childPages = parentPageInPopMethod.listChildren();
			while (childPages.hasNext()) {
				Page childPage = childPages.next();
				setFields(childPageAndTagsLinksList, childPage, resourceResolver);
			}
		}

		return childPageAndTagsLinksList;
	}

    /**
     * Populate tags model.
     *
     * @param childPageAndTagsLinksList the child page and tags links list
     * @param tags                      the tags
     * @param resourceResolver of the class resourceResolver
     *
     * @return the list
     */
	public List<CategoryHighlightArticleBean> populateTagsModel(
			List<CategoryHighlightArticleBean> childPageAndTagsLinksList, String[] tags,
			ResourceResolver resourceResolver) {

		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		RangeIterator<Resource> tagsIterator = null;
		if (tagManager != null) {
			boolean oneMatchIsEnough = StringUtils.equals(ANY, this.tagsType);
			tagsIterator = tagManager.find(CommonUtil.getHomePagePath(resourcePage), tags, oneMatchIsEnough);
			if (null != tagsIterator) {
				while (tagsIterator.hasNext()) {
					Resource tagResource = tagsIterator.next();
					PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
					Page tagPage = null;
					if (pageManager != null) {
						tagPage = pageManager
								.getPage(StringUtils.removeEnd(tagResource.getPath(), JcrConstants.JCR_CONTENT));
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
                CategoryHighlightFixedLinksModel fixedLink = linkResources
                        .next().adaptTo(CategoryHighlightFixedLinksModel.class);
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
			if (StringUtils.isNotBlank(teaserTitle)) {
				cardTypeBean.setHeadline(teaserTitle);
			} else {
				cardTypeBean.setHeadline(CommonUtil.getLinkTitle(null, page.getPath(), adminReadResourceResolver));
			}
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
        });
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
        String newWindowVal = StringUtils.EMPTY;
        if (StringUtils.equals(CommonConstants.TRUE, this.newWindow)) {
        	newWindowVal = CommonConstants.TARGET_BLANK;
        }

        return newWindowVal;
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
        String parentPageVal = this.parentPage;
        if (null == parentPageVal) {
        	parentPageVal = this.resourcePage.getPath();
        }
        return parentPageVal;
    }

    /**
     * Gets the list type array.
     *
     * @return the list type array
     */

	public List<CategoryHighLightArticleService> getListTypeArray() {
		List<CategoryHighLightArticleService> viewList = new ArrayList<>();
		int endIndex = getMaxLimitToDisplay();
		if (StringUtils.equals(CommonConstants.CHILD_PAGES, listType)
				|| StringUtils.equals(CommonConstants.TAGS, listType)) {

			for (CategoryHighlightArticleBean childPagTagList : childPageAndTagsLinksList) {
				viewList.add(childPagTagList);
			}
		} else if (StringUtils.equals(CommonConstants.FIXED_LIST, listType)) {

			for (CategoryHighlightFixedLinksModel fixedList : fixedLinksList) {
				viewList.add(fixedList);
			}
			}else if (StringUtils.equals(CommonConstants.MANUAL_LIST, listType)) {
			for (CategoryHighlightManualLinksModel manualList : manualLinksList) {
				viewList.add(manualList);
			}
		}else{
			if(LOG.isInfoEnabled()){
				LOG.info("List Type is Empty");
			}

		}

        if (StringUtils.equals(CommonConstants.TRUE, enableFeaturedCard)) {
           if(!viewList.isEmpty()){
             viewList.remove(0);
           }

        }
        if (endIndex > viewList.size()) {
            endIndex = viewList.size();
        }
        return viewList.subList(0, endIndex);
    }

    /**
     * Gets the enable featured card.
     *
     * @return the enable featured card
     */
    public String getEnableFeaturedCard() {
        return enableFeaturedCard;
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
     * Gets the checks if is eligible for carousel.
     *
     * @return the checks if is eligible for carousel
     */
    public boolean isEligibleForCarousel() {
        boolean isCarousel = false;
        if (getListTypeArray().size() > CommonConstants.LIST_TYPE_SIZE) {
            isCarousel = true;
        }
        return isCarousel;
    }

    /**
     * Gets the checks if is eligible for publication date display.
     * @return the checks if ready to display publication date.
     */
    public String getEnablePublicationDate() {
		return enablePublicationDate;
	}
    /**
     * Sets to publication date display/Hide.
     */
	public void setEnablePublicationDate(String enablePublicationDate) {
		this.enablePublicationDate = enablePublicationDate;
	}
}
