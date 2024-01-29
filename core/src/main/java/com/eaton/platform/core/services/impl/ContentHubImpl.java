package com.eaton.platform.core.services.impl;

import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ContentHubBean;
import com.eaton.platform.core.bean.ImageRenditionBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.services.ContentHubService;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component(service = ContentHubService.class, immediate = true)
public class ContentHubImpl implements ContentHubService {

    private Logger LOGGER = LoggerFactory.getLogger(ContentHubImpl.class);
    @Reference
    private HttpClientBuilderFactory httpFactory;

    @Activate
    protected void activate() {
        LOGGER.debug("Inside activate method for ContentHubImpl");
    }

    /**
     * Populate tags model.
     *
     * @param childPageAndTagsLinksList the child page and tags links list
     * @param tags the tags
     * @param resourceResolver
     * @return the list
     */
    public List<ContentHubBean> populateTagsModel(List<ContentHubBean> childPageAndTagsLinksList, String[] tags, Page resourcePage , ResourceResolver resourceResolver,final Locale language,final String showFilters,String defaultImagePath, String enablePublicationDate, final String sortBy, boolean isUnitedStatesDateFormat) {
    	LOGGER.debug("ContentHubImpl populateTagsModel method has been started");
    	TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        try{
        if(null != tagManager) {
            RangeIterator<Resource> tagsIt = tagManager.find(CommonUtil.getHomePagePath(resourcePage), tags, Boolean.FALSE);
            if(null != tagsIt) {
                while (tagsIt.hasNext()) {
                    Resource tagResource = tagsIt.next();
                    if(null != tagResource && CommonUtil.startsWithAnySiteContentRootPath(tagResource.getPath())){
                        ValueMap pageProperties = tagResource.getValueMap();
                        if(String.join(CommonConstants.COMMA,CommonUtil.getStringArrayProperty(pageProperties, CommonConstants.CQ_TAGS)).contains(CommonConstants.CONTENT_HUB_TAG_PATH)) {
                            setFields(childPageAndTagsLinksList, tagResource, pageProperties, resourceResolver,language,showFilters,defaultImagePath,enablePublicationDate, isUnitedStatesDateFormat);
                        }
                    }
                }
            }
            if(null != sortBy && CommonConstants.TITLE.equalsIgnoreCase(sortBy)){
				getAlphabeticalSortedList(childPageAndTagsLinksList);
			}
			else
			{
				getSortedList(childPageAndTagsLinksList);
			}
        } 
        }catch(Exception e)
        {
        	LOGGER.error("Error in populateTagsModel ");
        }
        LOGGER.debug("ContentHubImpl populateTagsModel method has been ended");
        return childPageAndTagsLinksList;
    }

    /**
     * Sets the fields.
     * @param array the array
     * @param pageResource the page resource
     * @param properties the properties
     * @param resourceResolver
     */
    private void setFields(List<ContentHubBean> array, Resource pageResource,
			ValueMap properties, ResourceResolver resourceResolver, final Locale language,final String showFilters,String defaultImagePath, final String enablePublicationDate, boolean isUnitedStatesDateFormat) {
		LOGGER.debug("ContentHubImpl setFields method has been started");
        ContentHubBean contentHubBean = new ContentHubBean();
        ImageRenditionBean imageRenditionBean = new ImageRenditionBean();
		SimpleDateFormat publicationDateFormat = new SimpleDateFormat(CommonConstants.DATE_TIME_FORMAT_PUBLISH);
            String imagePath = (!CommonUtil.getStringProperty(properties, CommonConstants.TEASER_IMAGE_PATH).equals(StringUtils.EMPTY)) ? CommonUtil.getStringProperty(properties, CommonConstants.TEASER_IMAGE_PATH) : defaultImagePath;
            String teaserTitle = CommonUtil.getStringProperty(properties, CommonConstants.TEASER_TITLE);
			String eyeBrowTitle = CommonUtil.getStringProperty(properties, CommonConstants.EYEBROW_TITLE);
			String eyebrowLink = CommonUtil.getStringProperty(properties, CommonConstants.PRIMARY_SUB_CATEGORY);
            contentHubBean.setLinkImage(imagePath);
            contentHubBean.setLinkTitle(StringUtils.isNotBlank(teaserTitle) ? teaserTitle : CommonUtil.getLinkTitle(null, StringUtils.removeEnd(pageResource.getPath(), JcrConstants.JCR_CONTENT), resourceResolver));
            String pageResourcePath=CommonUtil.dotHtmlLink(StringUtils.removeEnd(pageResource.getPath(), (CommonConstants.SLASH_STRING.concat(JcrConstants.JCR_CONTENT))), resourceResolver);
             contentHubBean.setLinkPath(pageResourcePath);
            if(StringUtils.equals(StringUtils.EMPTY,eyebrowLink)){
            	contentHubBean.setLinkEyebrow(eyeBrowTitle);
			}else {
				contentHubBean.setLinkEyebrow(CommonUtil.getLinkTitle(null, StringUtils.removeEnd(eyebrowLink,CommonConstants.HTML_EXTN), resourceResolver));
			}
            String eyebrowLinkPath=CommonUtil.dotHtmlLink(StringUtils.removeEnd(eyebrowLink, (CommonConstants.SLASH_STRING.concat(JcrConstants.JCR_CONTENT))));
            contentHubBean.setEyebrowLink(CommonUtil.dotHtmlLink(eyebrowLinkPath,resourceResolver));
            contentHubBean.setLinkImageAltText(CommonUtil.getAssetAltText(resourceResolver, imagePath));
            if(null != showFilters && CommonConstants.TRUE.equalsIgnoreCase(showFilters))
            {
			contentHubBean.setCqTags(getCqTagNames(CommonUtil.getStringArrayProperty(properties, CommonConstants.CQ_TAGS),resourceResolver,language));
			contentHubBean.setCqTagsWithPath(CommonUtil.getStringArrayProperty(properties, CommonConstants.CQ_TAGS));
			}
			contentHubBean.setPublicationDate(CommonUtil.getDateProperty(properties, CommonConstants.PUBLICATION_DATE, publicationDateFormat));
            contentHubBean.setReplicationDate(CommonUtil.getDateProperty(properties, NameConstants.PN_PAGE_LAST_REPLICATED, publicationDateFormat));
            if(null != enablePublicationDate && CommonConstants.TRUE.equalsIgnoreCase(enablePublicationDate)){
            	SimpleDateFormat defaultPublicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
				if(isUnitedStatesDateFormat) {
					defaultPublicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
				}
            	contentHubBean.setPublicationDateDisplay(CommonUtil.getDateProperty(properties, CommonConstants.PUBLICATION_DATE, defaultPublicationDateFormat));
			}
			if(imagePath!=null)
            {
                imageRenditionBean.setDesktopTransformedUrl(getTransformedUrl(imagePath,CommonConstants.DEFAULT_DESKTOP_TRANSFORMATION));
                imageRenditionBean.setMobileTransformedUrl(getTransformedUrl(imagePath,CommonConstants.DEFAULT_MOBILE_TRANSFORMATION));
                imageRenditionBean.setTabletTransformedUrl(getTransformedUrl(imagePath,CommonConstants.DEFAULT_TABLET_TRANSFORMATION));
                imageRenditionBean.setAltText(StringUtils.EMPTY);
                contentHubBean.setProductImageBean(imageRenditionBean);
            }
            array.add(contentHubBean);
        LOGGER.debug("ContentHubImpl setFields method has been ended");
    }

    private String getCqTagNames(final String[] pageCqTags,final ResourceResolver resourceResolver,final Locale language)
    {
        final ArrayList<String> cqTagsArray = new ArrayList<String>();
        if (pageCqTags.length > 0) {
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            Arrays.stream(pageCqTags)
                    .forEach(cqTagItem->{
                        final Tag cqTag = tagManager.resolve(cqTagItem);
                        if (null != cqTag && !CommonConstants.CONTENT_HUB_TAG_PATH.equalsIgnoreCase(cqTag.getTagID())) {
                           cqTagsArray.add(StringUtils.isNotEmpty(cqTag.getLocalizedTitle(language)) ? cqTag.getLocalizedTitle(language) : cqTag.getTitle());

                        }});
        }
        return cqTagsArray.isEmpty() ? StringUtils.EMPTY : String.join(CommonConstants.FACET_SEPERATOR, cqTagsArray);
    }

    /**
     * this method is sorting the list based on the author selection.
     *
     * @param list the list
     * @return the sorted list
     */
    private void getSortedList(List<ContentHubBean> list) {
        Collections.sort(list, new Comparator<ContentHubBean>() {
            public int compare(ContentHubBean linkList1, ContentHubBean linkList2) {
                int comparisonValue = 0;
                DateFormat simpleDate = new SimpleDateFormat(CommonConstants.DATE_TIME_FORMAT_PUBLISH, Locale.ENGLISH);
                Date date1;
                Date date2;
                try {
                    date1 = simpleDate.parse(linkList1.getPublicationDate());
                    date2 = simpleDate.parse(linkList2.getPublicationDate());
                    comparisonValue = -1 * (date1.compareTo(date2));
                } catch (ParseException e) {
                    LOGGER.error("ContentHubImpl | Unable to parse the date ");
                }

                return comparisonValue;
            }
        });
    }
	
	/**
	 * this method is sorting the list based on the title.
	 *
	 * @param list the list
	 * @return the sorted list
	 */
	private void getAlphabeticalSortedList(List<ContentHubBean> list) {
		Collections.sort(list, new Comparator<ContentHubBean>() {
			public int compare(ContentHubBean linkList1,
					ContentHubBean linkList2) {
				int comparisonValue = 0;
				comparisonValue = linkList1.getLinkTitle().compareToIgnoreCase(linkList2.getLinkTitle());
				return comparisonValue;
			}
		});
	}
    
    public List<FacetGroupBean> getCleanAEMFilters(List<ContentHubBean> childPageAndTagsLinksList,final String[] tags,final Locale locale,final ResourceResolver resourceResolver) 
	{
		LOGGER.debug("ContentHubImpl getCleanAEMFilters method has been started");
		List<FacetGroupBean> cleanFilters = new ArrayList<FacetGroupBean>();
		try{
			if(null != childPageAndTagsLinksList)
			{
				TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
				Arrays.stream(tags)
				.forEach(tagItem->{
					boolean isFilterGrpNameMatch = false;
					FacetGroupBean tempFacetGroupBean = new FacetGroupBean();
					tempFacetGroupBean.setFacetGroupLabel(getFacetGroupTitle(tagManager,tagItem,locale));
					List<FacetValueBean> tempFacetValueList = new ArrayList<FacetValueBean>();
					childPageAndTagsLinksList.forEach(contentHubBean ->{
						Arrays.stream(contentHubBean.getCqTagsWithPath())
						.forEach(pageTagItem->{
							if(pageTagItem.contains(tagItem))
							{
								final Tag pageTag = tagManager.resolve(pageTagItem);
								String tagTitle = StringUtils.isNotEmpty(pageTag.getLocalizedTitle(locale)) ? pageTag.getLocalizedTitle(locale) : pageTag.getTitle();
								FacetValueBean tempFacetValueBean = new FacetValueBean();
								tempFacetValueBean.setFacetValueLabel(tagTitle);
								tempFacetValueBean.setFacetValueId(pageTag.getTagID());
								tempFacetValueList.add(tempFacetValueBean);
							}
						});

					});
					tempFacetGroupBean.setFacetValueList(tempFacetValueList.stream().collect(Collectors.toCollection(()->new TreeSet<FacetValueBean>(Comparator.comparing(FacetValueBean::getFacetValueLabel)))).stream().collect(Collectors.toList()));
					isFilterGrpNameMatch = mergeFilterGroupLabels(cleanFilters, tempFacetGroupBean);
					if(cleanFilters.isEmpty() || !isFilterGrpNameMatch) {
						cleanFilters.add(tempFacetGroupBean);
					}
				});
			}

		}catch(Exception e)
		{
			LOGGER.error(" ContentHubImpl | Error in getCleanFilters method ");
		}
		LOGGER.debug("ContentHubImpl getCleanAEMFilters method has been started");
		return cleanFilters;
	}
	
	private String getFacetGroupTitle(final TagManager tagManager,final String tagItem,final Locale locale){
		String groupTitle = null;
		try{
			final Tag cqTag = tagManager.resolve(tagItem);
			if( tagItem.chars().filter(ch -> ch == '/').count() == 2)
              groupTitle = StringUtils.isNotEmpty(cqTag.getParent().getLocalizedTitle(locale)) ? cqTag.getParent().getLocalizedTitle(locale) : cqTag.getParent().getTitle();
            else
              groupTitle = StringUtils.isNotEmpty(cqTag.getLocalizedTitle(locale)) ? cqTag.getLocalizedTitle(locale) : cqTag.getTitle();
		}catch(Exception e){
	        LOGGER.error(" ContentHubImpl | Error in getFacetGroupTitle method "+ e.getMessage());
		}
		return groupTitle;
    }
	
	private boolean mergeFilterGroupLabels(List<FacetGroupBean> cleanFilters, FacetGroupBean tempFacetGroupBean) {
		boolean isFilterGrpNameMatch = false;
		if(!cleanFilters.isEmpty()) {
			for (FacetGroupBean facetGroupBean : cleanFilters) {
				if(facetGroupBean.getFacetGroupLabel().equalsIgnoreCase(tempFacetGroupBean.getFacetGroupLabel())) {
					facetGroupBean.getFacetValueList().addAll(tempFacetGroupBean.getFacetValueList());
					isFilterGrpNameMatch = true;
					break;
				}
			}
		}
		return isFilterGrpNameMatch;
	}
	
	private String getTransformedUrl(String teaserImage,String transformType) {
		String transformedUrl;
		if (StringUtils.isNotBlank(teaserImage)) {
			transformedUrl = StringUtils.isNotBlank(teaserImage) ? teaserImage.trim()
					.concat(CommonConstants.TRANSTORM).trim()
					.concat(transformType.trim())
					.concat(CommonConstants.IMAGE_JPG) : StringUtils.EMPTY;
		} else {
			transformedUrl = StringUtils.isNotBlank(teaserImage) ? teaserImage.trim() : StringUtils.EMPTY;
		}
		return transformedUrl;
	}
}