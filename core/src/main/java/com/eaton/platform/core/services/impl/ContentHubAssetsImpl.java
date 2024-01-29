package com.eaton.platform.core.services.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.eaton.platform.core.bean.ContentHubAssetsBean;
import com.eaton.platform.core.bean.ImageRenditionBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.ContentHubAssetsService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;


@Component(service = ContentHubAssetsService.class, immediate = true)
public class ContentHubAssetsImpl implements ContentHubAssetsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentHubAssetsImpl.class);

    @Activate
    protected void activate() {

    	LOGGER.debug("Inside activate method for ContentHubAssetsImpl");
    }

    /**
     * Populate tags model.
     *
     * @param childAssetAndTagsLinksList the child page and tags links list
     * @param tags the tags
     * @param resourceResolver
     * @return the list
     */
    public List<ContentHubAssetsBean> populateTagsModel(List<ContentHubAssetsBean> childAssetAndTagsLinksList, String[] tags, ResourceResolver resourceResolver,final Locale language, String[] eyebrowTag, Map<String, String> params,String[] countries, String[] languages, String[] taxonomy ) {
    	LOGGER.debug("ContentHubAssetsImpl populateTagModel method has been started");
    	TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
    	String sortBy = params.get(CommonConstants.CONTENT_HUB_SORT_BY);
        try{
			if (null != tagManager) {
				RangeIterator<Resource> tagsIt = tagManager.find(CommonConstants.CONTENT_DAM_EATON, tags, Boolean.TRUE);
				RangeIterator<Resource> taxonomyIt = tagManager.find(CommonConstants.CONTENT_DAM_EATON, taxonomy,
						Boolean.TRUE);
				ValueMap taxonomyAssetProperties = null;
				String[] taxonomyResourcetags = null;
				if (null != tagsIt) {
					if (null != taxonomyIt) {
						while (taxonomyIt.hasNext()) {
							Resource taxonomyResource = taxonomyIt.next();
							if (null != taxonomyResource) {
								taxonomyAssetProperties = taxonomyResource.getValueMap();
								taxonomyResourcetags = CommonUtil.getStringArrayProperty(
										taxonomyAssetProperties, CommonConstants.CQ_TAGS);
							}
						}
					}
					while (tagsIt.hasNext()) {
						Resource tagResource = tagsIt.next();
						if (null != tagResource) {
							ValueMap assetProperties = tagResource.getValueMap();
							String[] resourcetags = CommonUtil.getStringArrayProperty(assetProperties,
									CommonConstants.CQ_TAGS);
							boolean countryTagCheck = checkAccessEligibilityForAttribute(resourcetags, countries);
							boolean languageTagCheck = checkAccessEligibilityForAttribute(resourcetags, languages);

							if (String
									.join(CommonConstants.COMMA,
											CommonUtil.getStringArrayProperty(assetProperties, CommonConstants.CQ_TAGS))
									.contains(CommonConstants.CONTENT_HUB_TAG_PATH) && countryTagCheck
									&& languageTagCheck) {
								boolean compareTags=false;
								if(null!=taxonomyResourcetags) {
								 compareTags=compareAssetTags(assetProperties,taxonomyResourcetags);
								}
								if(null == taxonomyResourcetags || compareTags ) {
									setFields(childAssetAndTagsLinksList, tagResource, assetProperties, resourceResolver,
											language, eyebrowTag, params);
								}
							}
						}

					}

				}
            if(null != sortBy && CommonConstants.TITLE.equalsIgnoreCase(sortBy)){
				getAlphabeticalSortedList(childAssetAndTagsLinksList);
			}
			else
			{
				getSortedList(childAssetAndTagsLinksList);
			}
        } 
        }catch(Exception e)
        {
        	LOGGER.error("Error in populateTagsModel ");
        }
        LOGGER.debug("ContentHubAssetsImpl populateTagsModel method has been ended");
        return childAssetAndTagsLinksList;
    }
    /*
     * This method will compare
     * taxonomy tags with asset properties to provide the results having taxonomy tags authored.
     * */
    
	private boolean compareAssetTags(ValueMap assetProperties, String[] taxonomyResourcetags) {
		for (Entry<String, Object> key : assetProperties.entrySet()) {
			String keyVal=key.getKey();

			if (keyVal.equals(CommonConstants.CQ_TAGS)) {

				Object assetPropertyValue = assetProperties.get(keyVal);
				String[] assetPropertyValueArray = (String[]) assetPropertyValue;

				for (String taxononmyTag : taxonomyResourcetags) {
					for (String valueArray : assetPropertyValueArray) {
						if (valueArray.equalsIgnoreCase(taxononmyTag)) {
							return true;
						}
					}
				}

			}

		}
		return false;
	}

	/**
     * Sets the fields.
     * @param array the array
     * @param tagResource the page resource
     * @param properties the properties
     * @param resourceResolver
     */
    private void setFields(List<ContentHubAssetsBean> array, Resource tagResource,
			ValueMap properties, ResourceResolver resourceResolver, final Locale language,String[] eyebrowTag, Map<String, String> params) {
		LOGGER.debug("ContentHubAssetsImpl setFields method has been started");
		ContentHubAssetsBean contentHubAssetsBean = new ContentHubAssetsBean();
        ImageRenditionBean imageRenditionBean = new ImageRenditionBean();
		SimpleDateFormat publicationDateFormat = new SimpleDateFormat(CommonConstants.DATE_TIME_FORMAT_PUBLISH);
		String defaultImagePath = params.get(CommonConstants.CONTENT_HUB_DEFAULT_IMAGE_PATH);
		String enablePublicationDate = params.get(CommonConstants.CONTENT_HUB_ENABLE_PUBLICATION_DATE);
		String showFilters = params.get(CommonConstants.CONTENT_HUB_SHOW_FILTERS);
		String showFiltersTaxonomy = params.get(CommonConstants.CONTENT_HUB_SHOW_FILTERS_TAXONOMY);

        if(StringUtils.equals(StringUtils.EMPTY, CommonUtil.getStringProperty(properties, NameConstants.PN_HIDE_IN_NAV))){

			String assetPath = StringUtils.removeEnd(tagResource.getPath(), CommonConstants.SLASH_STRING.concat(JcrConstants.JCR_CONTENT).concat("/metadata"));
			Resource assetResource = resourceResolver.getResource(assetPath);
			Asset damAsset = CommonUtil.getAsset(assetResource);
            String assetTitle = CommonUtil.getAssetTitle(assetPath, assetResource);
            String assetType = !CommonUtil.getStringProperty(properties, CommonConstants.DAM_MIME_TYPE_PROPERTY).isEmpty() ?
					CommonUtil.getStringProperty(properties, CommonConstants.DAM_MIME_TYPE_PROPERTY) : CommonUtil.getStringProperty(properties, CommonConstants.DAM_APPLICATION_PROPERTY);
            if(null != showFilters && CommonConstants.TRUE.equalsIgnoreCase(showFilters) || null != showFiltersTaxonomy && CommonConstants.TRUE.equalsIgnoreCase(showFiltersTaxonomy))
            {
				contentHubAssetsBean.setCqTags(getCqTagNames(CommonUtil.getStringArrayProperty(properties, CommonConstants.CQ_TAGS),resourceResolver,language));
				contentHubAssetsBean.setCqTagsWithPath(CommonUtil.getStringArrayProperty(properties, CommonConstants.CQ_TAGS));
			}
			contentHubAssetsBean.setAssetTitle(assetTitle);
			contentHubAssetsBean.setAssetPath(CommonUtil.dotHtmlLink(assetPath,resourceResolver));
			contentHubAssetsBean.setAssetEyebrowTag(getCqTagNames(eyebrowTag,resourceResolver,language));
            contentHubAssetsBean.setAssetType(!assetType.isEmpty() ? assetType : CommonUtil.getType(damAsset));
			contentHubAssetsBean.setAssetSize(getAssetSize(damAsset));
			String imagePath;
			if(null != damAsset) {
				Rendition rendition = damAsset.getRendition(CommonConstants.DAM_THUMBNAIL_WEB);
				if (rendition != null) {
					Resource renditionResource = resourceResolver.getResource(rendition.getPath());
					imagePath = null != renditionResource ? renditionResource.getPath() : defaultImagePath;
				} else {
					imagePath = defaultImagePath;
				}
				contentHubAssetsBean.setAssetImage(imagePath);
				if(imagePath!=null)
				{
					imageRenditionBean.setDesktopTransformedUrl(getTransformedUrl(imagePath,CommonConstants.DEFAULT_DESKTOP_TRANSFORMATION));
					imageRenditionBean.setMobileTransformedUrl(getTransformedUrl(imagePath,CommonConstants.DEFAULT_MOBILE_TRANSFORMATION));
					imageRenditionBean.setTabletTransformedUrl(getTransformedUrl(imagePath,CommonConstants.DEFAULT_TABLET_TRANSFORMATION));
					imageRenditionBean.setAltText(StringUtils.EMPTY);
					contentHubAssetsBean.setProductImageBean(imageRenditionBean);
				}
			}

			String assetReplicationDate = CommonUtil.getDateProperty(properties, CommonConstants.ASSET_PUBLICATION_DATE, publicationDateFormat);
			if(null != assetReplicationDate && !assetReplicationDate.isEmpty()){
				contentHubAssetsBean.setPublicationDate(assetReplicationDate);
			}else {
				assetReplicationDate = CommonUtil.getDateProperty(properties, CommonConstants.JCR_MODIFIED, publicationDateFormat);
				if(null != assetReplicationDate){
					contentHubAssetsBean.setPublicationDate(assetReplicationDate);
				}
			}
			if(null != enablePublicationDate && CommonConstants.TRUE.equalsIgnoreCase(enablePublicationDate)){
				contentHubAssetsBean.setPublicationDateDisplay();
			}
            array.add(contentHubAssetsBean);
        }
        LOGGER.debug("ContentHubAssetsImpl setFields method has been ended");
    }
    
    private String getCqTagNames(final String[] pageCqTags,final ResourceResolver resourceResolver,final Locale language)
    {
        final ArrayList<String> cqTagsArray = new ArrayList<>();
        if (pageCqTags.length > 0) {
            final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
            Arrays.stream(pageCqTags)
                    .forEach(cqTagItem->{
                        final Tag cqTag = tagManager.resolve(cqTagItem);
                        if (null != cqTag && !CommonConstants.CONTENT_HUB_TAG_PATH.equalsIgnoreCase(cqTag.getTagID())) {
                           cqTagsArray.add(StringUtils.equals(language.toString(),CommonConstants.FALLBACK_LOCALE) ? cqTag.getTitle() : cqTag.getLocalizedTitle(language));
                        }});
        }
        return cqTagsArray.isEmpty() ? StringUtils.EMPTY : String.join(CommonConstants.FACET_SEPERATOR, cqTagsArray);
    }

	private String getAssetSize(Asset asset)
	{
		String assetSize = StringUtils.EMPTY;
		if (null != asset) {
			String spaceUnit = "";
			// get the asset size in Bytes unit
			double sizeOfAsset = asset.getOriginal().getSize() / Math.pow(10, 6);
			sizeOfAsset = Math.floor(sizeOfAsset*100) / 100;
			// set the asset size unit
			if (sizeOfAsset < 1) {
				sizeOfAsset = asset.getOriginal().getSize() / Math.pow(10, 3);
				sizeOfAsset = Math.floor(sizeOfAsset*100) / 100;
				spaceUnit = CommonConstants.KB;
				if (sizeOfAsset < 1) {
					sizeOfAsset = (asset.getOriginal().getSize());
					spaceUnit = CommonConstants.B;
				}
			} else {
				spaceUnit = CommonConstants.MB;
			}

			assetSize = sizeOfAsset + CommonConstants.BLANK_SPACE + spaceUnit;
		}
		return assetSize;
	}

    /**
     * this method is sorting the list based on the author selection.
     *
     * @param list the list
     * @return the sorted list
     */
    private void getSortedList(List<ContentHubAssetsBean> list) {
        Collections.sort(list, (ContentHubAssetsBean linkList1, ContentHubAssetsBean linkList2) -> {
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
            });
        }
	
	/**
	 * this method is sorting the list based on the title.
	 *
	 * @param list the list
	 * @return the sorted list
	 */
	private void getAlphabeticalSortedList(List<ContentHubAssetsBean> list) {
		Collections.sort(list,(ContentHubAssetsBean linkList1,
							   ContentHubAssetsBean linkList2) -> {
				int comparisonValue = 0;
				comparisonValue = linkList1.getAssetTitle().compareToIgnoreCase(linkList2.getAssetTitle());
				return comparisonValue;
		});
	}
    
    private String[] concatWithArrayCopy(String[] tags, String[] taxonomy) {
    	String[] result = Arrays.copyOf(tags, tags.length + taxonomy.length);
 	    System.arraycopy(taxonomy, 0, result, tags.length, taxonomy.length);
 	    return result;
		
	}
    
    public List<FacetGroupBean> getCleanAEMFilters(List<ContentHubAssetsBean> childAssetAndTagsLinksList, String[] tags,final Locale locale,final ResourceResolver resourceResolver, String[] taxonomy,Map<String, String> params)
   	{   
   		LOGGER.debug("ContentHubAssetsImpl getCleanAEMFilters method has been started");
   		List<FacetGroupBean> cleanFilters = new ArrayList<>();
   		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
   		String showFilters = params.get(CommonConstants.CONTENT_HUB_SHOW_FILTERS);
		String showFiltersTaxonomy = params.get(CommonConstants.CONTENT_HUB_SHOW_FILTERS_TAXONOMY);
		
   		try{
   			if(null != childAssetAndTagsLinksList)
   			{
   				if((null != showFilters && CommonConstants.TRUE.equalsIgnoreCase(showFilters)) && (null != showFiltersTaxonomy && CommonConstants.TRUE.equalsIgnoreCase(showFiltersTaxonomy))) {
   			    
   				String[] finaltag=concatWithArrayCopy(tags,taxonomy);
   				
   				Arrays.stream(finaltag).forEach(tagItem->{
   					boolean isFilterGrpNameMatch = false;
   					FacetGroupBean tempFacetGroupBean = new FacetGroupBean();
   					tempFacetGroupBean.setFacetGroupLabel(getFacetGroupTitle(tagManager,tagItem,locale));
   					List<FacetValueBean> tempFacetValueList = new ArrayList<>();
   					childAssetAndTagsLinksList.forEach(contentHubAssetsBean ->
   						Arrays.stream(contentHubAssetsBean.getCqTagsWithPath())
   						.forEach(assetTagItem -> {
   							if(assetTagItem.contains(tagItem))
   							{
   								final Tag assetTag = tagManager.resolve(assetTagItem);
   								String tagTitle = StringUtils.equals(locale.toString(),CommonConstants.FALLBACK_LOCALE) ?
                                           assetTag.getTitle() : assetTag.getLocalizedTitle(locale);
   								FacetValueBean tempFacetValueBean = new FacetValueBean();
   								tempFacetValueBean.setFacetValueLabel(tagTitle);
   								tempFacetValueBean.setFacetValueId(assetTag.getTagID());
   								tempFacetValueList.add(tempFacetValueBean);
   							}
   						})

   					);
   					tempFacetGroupBean.setFacetValueList(tempFacetValueList.stream().collect(Collectors.toCollection(()->
                               new TreeSet<FacetValueBean>(Comparator.comparing(FacetValueBean::getFacetValueLabel)))).stream().collect(Collectors.toList()));
   					isFilterGrpNameMatch = mergeFilterGroupLabels(cleanFilters, tempFacetGroupBean);
   					if(cleanFilters.isEmpty() || !isFilterGrpNameMatch) {
   						cleanFilters.add(tempFacetGroupBean);
   					}
   				});
   				}else if(null != showFilters && CommonConstants.TRUE.equalsIgnoreCase(showFilters)) {
   					Arrays.stream(tags).forEach(tagItem->{
   	   					boolean isFilterGrpNameMatch = false;
   	   					FacetGroupBean tempFacetGroupBean = new FacetGroupBean();
   	   					tempFacetGroupBean.setFacetGroupLabel(getFacetGroupTitle(tagManager,tagItem,locale));
   	   					List<FacetValueBean> tempFacetValueList = new ArrayList<>();
   	   					childAssetAndTagsLinksList.forEach(contentHubAssetsBean ->
   	   						Arrays.stream(contentHubAssetsBean.getCqTagsWithPath())
   	   						.forEach(assetTagItem -> {
   	   							if(assetTagItem.contains(tagItem))
   	   							{
   	   								final Tag assetTag = tagManager.resolve(assetTagItem);
   	   								String tagTitle = StringUtils.equals(locale.toString(),CommonConstants.FALLBACK_LOCALE) ?
   	                                           assetTag.getTitle() : assetTag.getLocalizedTitle(locale);
   	   								FacetValueBean tempFacetValueBean = new FacetValueBean();
   	   								tempFacetValueBean.setFacetValueLabel(tagTitle);
   	   								tempFacetValueBean.setFacetValueId(assetTag.getTagID());
   	   								tempFacetValueList.add(tempFacetValueBean);
   	   							}
   	   						})

   	   					);
   	   					tempFacetGroupBean.setFacetValueList(tempFacetValueList.stream().collect(Collectors.toCollection(()->
   	                               new TreeSet<FacetValueBean>(Comparator.comparing(FacetValueBean::getFacetValueLabel)))).stream().collect(Collectors.toList()));
   	   					isFilterGrpNameMatch = mergeFilterGroupLabels(cleanFilters, tempFacetGroupBean);
   	   					if(cleanFilters.isEmpty() || !isFilterGrpNameMatch) {
   	   						cleanFilters.add(tempFacetGroupBean);
   	   					}
   	   				});
   				}else if(null != showFiltersTaxonomy && CommonConstants.TRUE.equalsIgnoreCase(showFiltersTaxonomy)) {
   					Arrays.stream(taxonomy).forEach(tagItem->{
   	   					boolean isFilterGrpNameMatch = false;
   	   					FacetGroupBean tempFacetGroupBean = new FacetGroupBean();
   	   					tempFacetGroupBean.setFacetGroupLabel(getFacetGroupTitle(tagManager,tagItem,locale));
   	   					List<FacetValueBean> tempFacetValueList = new ArrayList<>();
   	   					childAssetAndTagsLinksList.forEach(contentHubAssetsBean ->
   	   						Arrays.stream(contentHubAssetsBean.getCqTagsWithPath())
   	   						.forEach(assetTagItem -> {
   	   							if(assetTagItem.contains(tagItem))
   	   							{
   	   								final Tag assetTag = tagManager.resolve(assetTagItem);
   	   								String tagTitle = StringUtils.equals(locale.toString(),CommonConstants.FALLBACK_LOCALE) ?
   	                                           assetTag.getTitle() : assetTag.getLocalizedTitle(locale);
   	   								FacetValueBean tempFacetValueBean = new FacetValueBean();
   	   								tempFacetValueBean.setFacetValueLabel(tagTitle);
   	   								tempFacetValueBean.setFacetValueId(assetTag.getTagID());
   	   								tempFacetValueList.add(tempFacetValueBean);
   	   							}
   	   						})

   	   					);
   	   					tempFacetGroupBean.setFacetValueList(tempFacetValueList.stream().collect(Collectors.toCollection(()->
   	                               new TreeSet<FacetValueBean>(Comparator.comparing(FacetValueBean::getFacetValueLabel)))).stream().collect(Collectors.toList()));
   	   					isFilterGrpNameMatch = mergeFilterGroupLabels(cleanFilters, tempFacetGroupBean);
   	   					if(cleanFilters.isEmpty() || !isFilterGrpNameMatch) {
   	   						cleanFilters.add(tempFacetGroupBean);
   	   					}
   	   				});
   				}else{
   					LOGGER.debug(" No filter selected");
   				}
   			}

   		}catch(Exception e)
   		{
   			LOGGER.error(" ContentHubImpl | Error in getCleanFilters method ");
   		}
   		LOGGER.debug("ContentHubAssetsImpl getCleanAEMFilters method has been started");
   		return cleanFilters;
   	}
	
	private String getFacetGroupTitle(final TagManager tagManager,final String tagItem,final Locale locale){
		String groupTitle = null;
		try{
			final Tag cqTag = tagManager.resolve(tagItem);
			if( tagItem.chars().filter(ch -> ch == '/').count() == 2)
              groupTitle = StringUtils.equals(locale.toString(),CommonConstants.FALLBACK_LOCALE) ? cqTag.getParent().getTitle() : cqTag.getParent().getLocalizedTitle(locale);
            else
              groupTitle = StringUtils.equals(locale.toString(),CommonConstants.FALLBACK_LOCALE) ? cqTag.getTitle() : cqTag.getLocalizedTitle(locale);
		}catch(Exception e){
	        LOGGER.error("ContentHubAssetsImpl | Error in getFacetGroupTitle method", e.getMessage());
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
	private boolean checkAccessEligibilityForAttribute(String[] resourceSecureTags, String[] contenthubtags){
        boolean allowAccess = false;
        if( contenthubtags != null){
        	if(resourceSecureTags !=null) {
        		for (String contenthubtag : contenthubtags ) {
                	 for (String  resourceSecureTag : resourceSecureTags) {
                		 if(resourceSecureTag.equals(contenthubtag)){
                		    	allowAccess= true;	
                		 }
                	 }
        	         if(allowAccess) {
        	    	   break;
        	         }
                 }
        	}
        }
        else{
           allowAccess = true;
        }
        return allowAccess;
    }
}