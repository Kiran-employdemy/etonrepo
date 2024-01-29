package com.eaton.platform.core.models.resourcelist;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ResourceListTagsBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.JcrQueryConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.DocumentGroupWithAemTagsModel;
import com.eaton.platform.core.models.ExternalResourceModel;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.models.downloadable.AbstractBulkDownloadableImpl;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.EatonAssetUtils;
import com.eaton.platform.core.util.JcrQueryUtils;
import com.eaton.platform.core.util.MultiLingualUtil;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eaton.platform.core.constants.AssetConstants;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

/**
 * Class ResourceListTagsModel.
 *
 */
@Model(adaptables = {
        Resource.class,
        SlingHttpServletRequest.class
}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResourceListTagsModel extends AbstractBulkDownloadableImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceListTagsModel.class);
    private static final String METADATA_CQ_TAGS = "jcr:content/metadata/cq:tags";
    private static final String PROP_PATH = "path";
    private static final String JCR_CONTENT_METADATA = "jcr:content/metadata";
    private static final String PROPERTY = "0_property";
    private static final String LIKE = "like";
    private static final String PROPERTY_OPERATION = "0_property.operation";
    private static final String PROPERTY1 = "0_property.";
    private static final String VALUE = "_value";
    private static final String _PROPERTY = "_property";
    private static final String PROPERTY_VALUE = "_property.value";
    private static final String EXTERNAL_RESOURCE_LIST = "externalResourceList";
    private static final String ETN_RESOURCE_TAGS_PATH = "eaton:resources";
    private static final Pattern REGEX_PATTERN = Pattern.compile("[^a-zA-Z0-9]");
    public static final int COUNT = 2;

    @Inject
    private Resource resource;

    @Inject
    private SlingHttpServletRequest slingRequest;

    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Inject
    private Page currentPage;

    @Inject
    @Via("resource")
    private String damPath;

    @Inject
    @Via("resource")
    @Default(values = "500")
    private String maxLimit;
    @Inject
    @Via("resource")
    private Boolean enableBulkDownload;

    @Inject
    private AuthorizationService authorizationService;

    private List <DocumentGroupWithAemTagsModel> documentGroupList = new ArrayList<>();

    private int noOfDays;

    private UserProfile userProfile;

    @OSGiService
    private transient EatonConfigService configService;

    @EatonSiteConfigInjector
 	private Optional<SiteResourceSlingModel> siteResourceSlingModel;

     private boolean isUnitedStatesDateFormat = false;

     private String bulkDownloadToolTip;

    /**
     * init method which is invoked upon completion of all injections.
     */
    @PostConstruct
    public void init() {
    	LOGGER.debug("Start of ResourceListTagsModel - init().");
    	if (siteResourceSlingModel.isPresent()) {
    		isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
		}
        List < Hit > hitList = null;
        try {
            final EatonSiteConfigModel eatonSiteConfigModel = slingRequest.adaptTo(EatonSiteConfigModel.class);
            final SiteConfigModel siteConfiguration = eatonSiteConfigModel.getSiteConfig();
            if (null != siteConfiguration ) {
        		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                if (null != tagManager ) {
                    Tag rootTag = tagManager.resolve(ETN_RESOURCE_TAGS_PATH);
                    Locale locale = CommonUtil.getLocaleFromPagePath(currentPage);
                    getResourceTags(slingRequest, rootTag, documentGroupList, locale);
                    documentGroupList = documentGroupList.stream().sorted(Comparator.comparing(DocumentGroupWithAemTagsModel::getGroupName)).collect(Collectors.toList());
                    noOfDays = siteConfiguration.getNoOfDays();
                }
            }
            final String[] pageTeaserTagList = getPageProperty(CommonConstants.PRIMARY_SUB_CATEGORY_TAG);
            if (StringUtils.isEmpty(damPath)) {
                damPath = CommonConstants.CONTENT_DAM_EATON;
            }
            if (CollectionUtils.isNotEmpty(documentGroupList)) {
                hitList = getDAMAssetsForDocumentGroups(pageTeaserTagList,slingRequest);
                List<Resource> documentResourceMetaList = documentResourceMetaList(hitList);
                documentResourceMetaList = filterOutNonAuthorizedAssets(documentResourceMetaList);
                final List < DocumentGroupWithAemTagsModel > newDocumentGroupList = new ArrayList < > ();
                for (DocumentGroupWithAemTagsModel documentGroupBean: documentGroupList) {
                    final String aemtags = documentGroupBean.getAemtags();
                    final List < ResourceListTagsBean > resourceListTagsBeanList = new ArrayList < > ();
                    extractHitsForDocumentGroup(documentResourceMetaList,aemtags,resourceListTagsBeanList);
                    checkExternalLinksForDocumentGroup(documentGroupBean, resourceListTagsBeanList);
                    if (!resourceListTagsBeanList.isEmpty()) {
                        getDoucumentSortedList(resourceListTagsBeanList);
                        documentGroupBean.setResourceListTagsBean(resourceListTagsBeanList);
                        newDocumentGroupList.add(documentGroupBean);
                    }
                }
                documentGroupList = newDocumentGroupList;
                documentGroupList.forEach(documentGroupBean -> documentGroupBean.getResourceListTagsBean()
                        .sort(Comparator.comparing(ResourceListTagsBean::getAssetTitle,String.CASE_INSENSITIVE_ORDER)));
                for (Resource documentResourceMeta : documentResourceMetaList) {
                    documentResourceMeta.getResourceResolver().close();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while fetching DAM assets tags :: {}", e.getMessage());
        }
        LOGGER.debug("End of ResourceListTagsModel - init().");
    }

    private List<Resource> filterOutNonAuthorizedAssets(List<Resource> documentResourceMetaList) {
        AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
        if (authenticationToken == null) {
            return documentResourceMetaList.stream().filter(SecureAssetFilter.nonSecured()).collect(Collectors.toList());
        }
        if (authenticationToken.getBypassAuthorization()) {
            return documentResourceMetaList;
        }
        UserProfile userProfile = authenticationToken.getUserProfile();
        return documentResourceMetaList.stream().filter(SecureAssetFilter.secured(new SecureAssetValidatorFactory(), userProfile)).collect(Collectors.toList());
    }

    private void extractHitsForDocumentGroup(List<Resource> documentResourceMetaList, String aemtags, final List<ResourceListTagsBean> resourceListTagsBeanList){
    	LOGGER.debug("Start of ResourceListTagsModel - extractHitsForDocumentGroup().");
        if (CollectionUtils.isNotEmpty(documentResourceMetaList)) {
            for (Resource documentResourceMeta : documentResourceMetaList) {
                final ResourceListTagsBean documentBean = convertDocumentResourcetoBean(documentResourceMeta);
                if (null != documentResourceMeta) {
                    final String[] assetTags = documentResourceMeta.getValueMap().get(TagConstants.PN_TAGS, new String[0]);
                    final List<String> tagsList = Arrays.asList(assetTags);
                    if (tagsList.contains(aemtags)) {
                        resourceListTagsBeanList.add(documentBean);
                    }
                }
            }
        }
        LOGGER.debug("End of ResourceListTagsModel - extractHitsForDocumentGroup().");
    }
    private static List<Resource> documentResourceMetaList(final List<Hit> hitList)
            throws RepositoryException {
    	LOGGER.debug("Start of ResourceListTagsModel - documentResourceMetaList().");
        List<Resource> documentResourceMetaList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(hitList)) {
            for (final Hit hit : hitList) {
                final Resource docResource = hit.getResource();
                Resource documentResourceMeta = docResource.getChild(JCR_CONTENT_METADATA);
                if (null != documentResourceMeta) {
                    documentResourceMetaList.add(documentResourceMeta);
                }
            }
        }
        LOGGER.debug("End of ResourceListTagsModel - documentResourceMetaList().");
        return documentResourceMetaList;
    }

    private List < Hit > getDAMAssetsForDocumentGroups(final String[] pageTeaserTagList, SlingHttpServletRequest request) throws JsonProcessingException {
    	LOGGER.debug("Start of ResourceListTagsModel - getDAMAssetsForDocumentGroups().");
        List<Hit> hitList = null;
        if(null != pageTeaserTagList && pageTeaserTagList.length >0){
            final Map < String, String > queryParams = new LinkedHashMap<>();
            final Session session = resourceResolver.adaptTo(Session.class);
            final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
            queryParams.put(JcrQueryConstants.PROP_TYPE, DamConstants.NT_DAM_ASSET);
            queryParams.put(PROP_PATH, damPath);
            queryParams.put(JcrQueryConstants.P_HITS, JcrQueryConstants.FULL);
            queryParams.put(JcrQueryConstants.P_LIMIT, maxLimit);
            queryParams.put(PROPERTY, METADATA_CQ_TAGS);
            queryParams.put(PROPERTY_OPERATION, LIKE);

            for (int count = 0; count < documentGroupList.size(); count++) {
                String documentGroupWithAemTag = documentGroupList.get(count).getAemtags();
                if (StringUtils.isNotEmpty(documentGroupWithAemTag)) {
                    queryParams.put(PROPERTY1 + count + VALUE, documentGroupWithAemTag);
                }
            }
            for (int count = 0; count < pageTeaserTagList.length; count++) {
                queryParams.put((count + 1) + _PROPERTY, METADATA_CQ_TAGS);
                queryParams.put((count + 1) + PROPERTY_VALUE, pageTeaserTagList[count]);
            }
            hitList = JcrQueryUtils.excuteGenericQuery(session, queryBuilder, queryParams, maxLimit );
        }
        LOGGER.debug("End of ResourceListTagsModel - getDAMAssetsForDocumentGroups().");
        return hitList;
    }
    /**
     * As this method will search external links for the document group.
     *
     * @param documentGroup,resourceListTagsBeanList
     * @param documentGroup
     * @return null
     */
	private void checkExternalLinksForDocumentGroup(final DocumentGroupWithAemTagsModel documentGroup,
                                                    final List<ResourceListTagsBean> resourceListTagsBeanList) {
		LOGGER.debug("Start of ResourceListTagsModel - checkExternalLinksForDocumentGroup().");
		if (resource != null && resource.hasChildren()) {
            AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(slingRequest);
			final Resource externalResourceList = resource.getChild(EXTERNAL_RESOURCE_LIST);
			if (null != externalResourceList && externalResourceList.hasChildren()) {
				final Iterable<Resource> externalResList = externalResourceList.getChildren();
				externalResList.forEach(externalResources -> {
        final ExternalResourceModel externalResourceModel = externalResources
							.adaptTo(ExternalResourceModel.class);
        if (null != externalResourceModel
							&& StringUtils.isNotEmpty(externalResourceModel.getAccordionName())
							&& externalResourceModel.getAccordionName().equals(documentGroup.getAnchorId())) {
            final ResourceListTagsBean documentBean = new ResourceListTagsBean();
            documentBean.setAssetLink(CommonUtil.dotHtmlLink(externalResourceModel.getResourceLink()));
            documentBean.setAssetTitle(externalResourceModel.getExternalLinkText());
            documentBean.setAnchorID(externalResourceModel.getAccordionName());
            documentBean.setExternal(externalResourceModel.isExternalLink());
            final Resource linkPathResource = CommonUtil.getResourceFromLinkPath(externalResourceModel.getResourceLink(), resourceResolver);
            if(SecureUtil.isSecureResource(linkPathResource) && authorizationService.isAuthorized(authenticationToken, linkPathResource)){
                documentBean.setSecure(true);
                resourceListTagsBeanList.add(documentBean);
            }
            if(!SecureUtil.isSecureResource(linkPathResource)) {
                resourceListTagsBeanList.add(documentBean);
            }
            }
				});
			}
		}
		LOGGER.debug("End of ResourceListTagsModel - checkExternalLinksForDocumentGroup().");
	}
    private ResourceListTagsBean convertDocumentResourcetoBean(final Resource documentResource) {
    	LOGGER.debug("Start of ResourceListTagsModel - convertDocumentResourcetoBean().");
        final Resource actualDocumentResource = documentResource.getParent().getParent();
        final Resource jcrResourcePath = documentResource.getParent();
        final Asset documentAsset = CommonUtil.getAsset(actualDocumentResource);
        Locale locale = CommonUtil.getLocaleFromPagePath(currentPage);
        final ResourceListTagsBean documentBean = new ResourceListTagsBean();
        // Asset Resource List Bean
        final ValueMap valueMap = documentResource.getValueMap();
        if(valueMap.get(SecureConstants.CONTENT_ROOT_SECURE_IS_ASSET_SECURE) != null && valueMap.get(SecureConstants.CONTENT_ROOT_SECURE_IS_ASSET_SECURE).equals("YES")){
            documentBean.setSecure(true);
        }
        documentBean.setAssetFileSize(CommonUtil.getAssetSize(documentAsset));
        documentBean.setAssetFileType(CommonUtil.getType(documentAsset));

        final String assetDescription = Optional.ofNullable(documentAsset.getMetadataValue(MultiLingualUtil.getMultiLingualPropertyKeyByLocale(DamConstants.DC_DESCRIPTION,
        locale, documentAsset))).orElse(StringUtils.EMPTY);

        documentBean.setAssetDescription(assetDescription);
        setIfAssetTypeIsDrawing(documentAsset,documentBean);

        if( null != documentAsset.getMetadataValue(MultiLingualUtil.getMultiLingualPropertyKeyByLocale(DamConstants.DC_TITLE, locale, documentAsset)))
        {
            documentBean.setAssetTitle(documentAsset.getMetadataValue(MultiLingualUtil.getMultiLingualPropertyKeyByLocale(DamConstants.DC_TITLE,
                    locale, documentAsset)));

        }else if (null != documentAsset.getMetadataValue(JcrConstants.JCR_TITLE)
                    && null == documentAsset.getMetadataValue(DamConstants.DC_TITLE)) {
                documentBean.setAssetTitle(documentAsset.getMetadataValue(JcrConstants.JCR_TITLE));
            } else if (null != documentAsset.getMetadataValue(DamConstants.DC_TITLE) ) {
                if (documentAsset.getMetadataValue(DamConstants.DC_TITLE).contains(CommonConstants.COMMA)) {
                    final String dcTitleString = documentAsset.getMetadataValue(DamConstants.DC_TITLE);
                    final int commaCount = dcTitleString.split(CommonConstants.COMMA, -1).length - 1;
                    if (commaCount < COUNT) {
                        final String[] assetTitles = documentAsset.getMetadataValue(DamConstants.DC_TITLE).split(CommonConstants.COMMA);
                        if (assetTitles[0].trim().equalsIgnoreCase(assetTitles[1].trim())) {
                            documentBean.setAssetTitle(assetTitles[0]);
                        } else {
                            documentBean.setAssetTitle(documentAsset.getMetadataValue(DamConstants.DC_TITLE));
                        }
                    } else if (commaCount > 1) {
                        final int subindex = ((commaCount - 1) / 2) + 1;
                        final int commaindex = StringUtils.ordinalIndexOf(dcTitleString, CommonConstants.COMMA, subindex);
                        if (dcTitleString.substring(0, commaindex).trim().equalsIgnoreCase(dcTitleString.substring(commaindex + 1).trim())) {
                            documentBean.setAssetTitle(dcTitleString.substring(0, commaindex));
                        } else {
                            documentBean.setAssetTitle(documentAsset.getMetadataValue(DamConstants.DC_TITLE));
                        }
                    }

                } else {
                    documentBean.setAssetTitle(documentAsset.getMetadataValue(DamConstants.DC_TITLE));
                }
            } else if ( null != jcrResourcePath && jcrResourcePath.getValueMap().get(AEMConstants.CQ_NAME) != null) {
                final String assetCqName = jcrResourcePath.getValueMap().get(AEMConstants.CQ_NAME).toString();
                documentBean.setAssetTitle(assetCqName);
            }

        try {
            if (null != jcrResourcePath && null != actualDocumentResource) {
                Calendar assetReplicationDate = documentResource.getValueMap().get(CommonConstants.ASSET_PUBLICATION_DATE, GregorianCalendar.class);
                if (null == assetReplicationDate) {
                    assetReplicationDate = actualDocumentResource.getValueMap().get(CommonConstants.JCR_CREATED, GregorianCalendar.class);
                }

                if (null != assetReplicationDate ) {
					Calendar localDate = Calendar.getInstance();
					SimpleDateFormat defaultPublicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
					if(isUnitedStatesDateFormat) {
						defaultPublicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
					}
					final String assetPublicationDate = CommonUtil.format(assetReplicationDate, defaultPublicationDateFormat);
            		long dateDifferenceInMilis = localDate.getTimeInMillis() - assetReplicationDate.getTimeInMillis();
            		long diffenceInDays =TimeUnit.MILLISECONDS.toDays(dateDifferenceInMilis);
            		documentBean.setAssetPublicationDate(assetPublicationDate);
            		Calendar bornOnDate = documentResource.getValueMap().get(CommonConstants.ASSET_BORN_ON_DATE, GregorianCalendar.class);
					if((bornOnDate != null) && (toIntExact(diffenceInDays) <= noOfDays)) {
						documentBean.setNewAsset("updated");
					} else {
						if((bornOnDate == null) && (toIntExact(diffenceInDays) <= noOfDays)) {
							documentBean.setNewAsset("new");
						}
					}
                }
            }
            final String assetContentId = Optional.ofNullable(documentAsset.getMetadataValue(CommonConstants.DC_PERCOLATE_CONTENT_ID))
    				.orElse(StringUtils.EMPTY);
            documentBean.setTrackDownload(EatonAssetUtils.trackThisAsset(documentResource));
            if(StringUtils.isNotEmpty(assetContentId)) {
            	documentBean.setAssetLink(documentAsset.getPath()+"?"+CommonConstants.ASSET_CONTENT_ID_PARAM+"="+assetContentId);
            }else {
            	documentBean.setAssetLink(documentAsset.getPath());
            }
            // EAT-7286 - ECCN & shaHash
            final String eccn = Optional.ofNullable(documentAsset.getMetadataValue(CommonConstants.ECCN))
                    .orElse(StringUtils.EMPTY);
            final String shaHash = Optional.ofNullable(documentAsset.getMetadataValue(CommonConstants.SHA))
                    .orElse(StringUtils.EMPTY);
            final String assetName = Optional.ofNullable(documentAsset.getMetadataValue(AssetConstants.ASSET_NAME))
                    .orElse(StringUtils.EMPTY);
            documentBean.setEccn(eccn);
            documentBean.setShaHash(shaHash);
            documentBean.setEatonFileName(assetName);


        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        LOGGER.debug("End of ResourceListTagsModel - convertDocumentResourcetoBean().");
        return documentBean;
    }

    /**
     * Set multilingual flag if asset type is whitelisted.
     * @param documentAsset
     * @param documentBean
     */
    private void setIfAssetTypeIsDrawing(Asset documentAsset, ResourceListTagsBean documentBean){
        if(configService.getConfigServiceBean().getResourceListingDrawingsFileExtensionList().
                contains(documentAsset.getMetadataValue(DamConstants.DC_FORMAT))){
            documentBean.setDrawingType(true);
            documentBean.setBigImage(EatonAssetUtils.getThumbnailImage(documentAsset, CommonConstants.ASSET_1280_1280_THUMBNAIL,
                    CommonConstants.ASSET_1280_VIEW_PORT, DamConstants.PREFIX_ASSET_WEB));
            documentBean.setRendition140x100(EatonAssetUtils.getThumbnailImage(documentAsset, CommonConstants.ASSET_140_100_THUMBNAIL,
                    CommonConstants.ASSET_140_VIEW_PORT, DamConstants.PREFIX_ASSET_THUMBNAIL));
            documentBean.setRendition319x319(EatonAssetUtils.getThumbnailImage(documentAsset, CommonConstants.ASSET_319_319_THUMBNAIL,
                    CommonConstants.ASSET_319_VIEW_PORT, DamConstants.PREFIX_ASSET_THUMBNAIL));
            documentBean.setRendition48x48(EatonAssetUtils.getThumbnailImage(documentAsset,  CommonConstants.ASSET_48_48_THUMBNAIL,
                    CommonConstants.ASSET_48_VIEW_PORT, DamConstants.PREFIX_ASSET_THUMBNAIL));
        }
    }

    private static final void getDoucumentSortedList(final List < ResourceListTagsBean > resourceListTagsBeanList) {
    	LOGGER.debug("Start of ResourceListTagsModel - getDoucumentSortedList().");
        Collections.sort(resourceListTagsBeanList, (resourceListTagsBean1, resourceListTagsBean2) -> {
            int soretedResult = 0;
            final String assetTitle = resourceListTagsBean1.getAssetTitle();
            final String assetTitle1 = resourceListTagsBean2.getAssetTitle();
            if (StringUtils.isNotEmpty(assetTitle) && StringUtils.isNotEmpty(assetTitle1)) {
                soretedResult = resourceListTagsBean1.getAssetTitle().compareTo(resourceListTagsBean2.getAssetTitle());
            }
            return soretedResult;
        });
        LOGGER.debug("End of ResourceListTagsModel - getDoucumentSortedList().");
    }
    /**
     * Gets the page property.
     *
     * @param propertyName the property name
     * @return the page property
     */
    public String[] getPageProperty(final String propertyName) {
    	LOGGER.debug("Start of ResourceListTagsModel - getPageProperty().");
        String[] propertyValue = null;
        if (null != currentPage) {
            final ValueMap pageProperties = currentPage.getProperties();
            propertyValue = pageProperties.get(propertyName, new String[0]);
        }
        LOGGER.debug("End of ResourceListTagsModel - getPageProperty().");
        return propertyValue;
    }
    /**
     * @return the documentGroupList
     */
    public final List < DocumentGroupWithAemTagsModel > getDocumentGroupList() {
        return documentGroupList;
    }
    private  void getResourceTags(SlingHttpServletRequest request,Tag rootTag,List<DocumentGroupWithAemTagsModel> documentGroupList,Locale locale) {
		LOGGER.debug("Start of ResourceListTagsModel - getResourceTags().");
		if (null != rootTag) {
			Iterator<Tag> childTags = rootTag.listChildren();
			if(childTags.hasNext()) {
				while (childTags.hasNext()) {
					Tag childTag = childTags.next();
					getResourceTags(request,childTag,documentGroupList,locale);
				}
			}else {
				DocumentGroupWithAemTagsModel docGrpTagModel = new DocumentGroupWithAemTagsModel();
				docGrpTagModel.setAemtags(rootTag.getTagID());
                if(locale.getCountry().equals(CommonConstants.ISRAEL_LOCALE)) {
                    docGrpTagModel.setGroupName(getIsraelTagTitle(rootTag));
                } else {
                    docGrpTagModel.setGroupName(rootTag.getTitle(locale));
                }
				docGrpTagModel.setAnchorId(getAnchorId(rootTag.getTitle(locale)));
				documentGroupList.add(docGrpTagModel);
			}
		}
		LOGGER.debug("End of ResourceListTagsModel - getResourceTags().");
	}
    private static String getAnchorId(String groupName) {
		LOGGER.debug("Start of ResourceListTagsModel - getAnchorId().");
		String anchorId = StringUtils.EMPTY;
		if (StringUtils.isNotEmpty(groupName)) {
			Matcher matcher = REGEX_PATTERN.matcher(groupName);
			anchorId = matcher.replaceAll(CommonConstants.HYPHEN).toLowerCase(Locale.ENGLISH);
		}
		LOGGER.debug("End of ResourceListTagsModel - getAnchorId().");
		return anchorId;
	}

    private  String getIsraelTagTitle(Tag rootTag) {
        String tagTitle = StringUtils.EMPTY;
        final Resource tagResource = resourceResolver.getResource(rootTag.getPath());
        if(Objects.nonNull(tagResource)) {
            final ValueMap tagValueMap = tagResource.getValueMap();
            if(tagValueMap.containsKey(CommonConstants.JCR_TITLE_HE_IL)) {
                tagTitle = tagValueMap.get(CommonConstants.JCR_TITLE_HE_IL).toString();
            } else {
                tagTitle = tagValueMap.get(CommonConstants.JCR_TITLE).toString();
            }
        }
        return tagTitle;
    }

    @Override
    protected SlingHttpServletRequest getSlingHttpServletRequest() {
        return slingRequest;
    }

    @Override
    protected Page getCurrentPage() {
        return currentPage;
    }

    public Boolean getEnableBulkDownload() {
        if (enableBulkDownload == null) {
            return true;
        }
        return enableBulkDownload;
    }
}
