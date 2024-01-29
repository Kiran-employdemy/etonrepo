package com.eaton.platform.core.services.impl;

import javax.jcr.RepositoryException;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.AssetConstants;
import com.eaton.platform.core.constants.JcrQueryConstants;
import com.eaton.platform.core.models.DocumentGroupWithAemTagsModel;
import com.eaton.platform.core.servlets.AssetDetails;
import com.eaton.platform.core.util.JcrQueryUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.eaton.platform.core.services.PageAssetReferenceService;
import com.eaton.platform.core.constants.CommonConstants;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import static com.eaton.platform.core.constants.CommonConstants.SPACE_STRING;

/**
 * This OSGI returns information about all tag assets used on the page.
 */
@Component(service = PageAssetReferenceService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "PageAssetReferenceServiceImpl",
                AEMConstants.PROCESS_LABEL + "PageAssetReferenceServiceImpl"
        })
public class PageAssetReferenceServiceImpl implements PageAssetReferenceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageAssetReferenceServiceImpl.class);
    private static final String ASSETREF = ".assetreferences.json";
    private static final String QUERY_PARAM_VAL = "_value";
    private static final String PROPERTY_ZERO = "0_property.";
    private static final String DOWNLOAD_LIST_TYPE = "eaton/components/general/download-link-list";
    private static final String RESOURCE_LIST_TYPE = "eaton/components/product/resource-list";
    private Page currentPage;
    private List<DocumentGroupWithAemTagsModel> documentGroupList = new ArrayList<>();


    /**
     * This will populate all Asset on page.Internally it is calling findAssetsByTags method
     * which excute the query and fetch al the Hits, based on that we are searching the assets
     * and forming the list of Assetdetails
     *
     * @param resourceResolver the value to be used.
     * @param tags             the value to be used.
     * @param resType          the value to be used.
     * @return list of AssetDetails.
     */
    public List<AssetDetails> populateTagsAsset(String[] tags, ResourceResolver resourceResolver, String resType) {
        LOGGER.info("In populateTagsAsset method :: Started");
        List<AssetDetails> tagAsset = new ArrayList<>();
        try {
            if (null != resourceResolver) {
                List<Hit> hitList = findAssetsByTags(resourceResolver, tags);
                tagAsset = fetchAssetDetails(hitList, resType);
            }
        } catch (RuntimeException e) {
            LOGGER.error(" Exception in populateParentModel" + e.getMessage(), e);
        }
        return tagAsset;
    }

    private static List<AssetDetails> fetchAssetDetails(List<Hit> hitList, String resType) {
        List<AssetDetails> tagAssetsDetails = new ArrayList<>();
        try {
            if (CollectionUtils.isNotEmpty(hitList)) {
                for (final Hit hit : hitList) {
                    final Resource tagsAssetResource = hit.getResource();
                    final Asset asset = tagsAssetResource.adaptTo(Asset.class);
                    if (null != asset) {
                        tagAssetsDetails.addAll(tagAssetListWithResourceType(asset, resType));
                    }
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Repository Exception in fetchAssetDetails" + e.getMessage(), e);
        }
        return tagAssetsDetails;
    }

    private static List<AssetDetails> tagAssetListWithResourceType(Asset asset, String resType) {
        List<AssetDetails> tagAssetsDetails = new ArrayList<>();
        if (null != asset) {
            String countryStr = Optional.ofNullable(asset.getMetadataValue(AssetConstants.EATON_COUNTRY))
                    .orElse(StringUtils.EMPTY);
            String languageStr = Optional.ofNullable(asset.getMetadataValue(AssetConstants.EATON_LANGAUGE))
                    .orElse(StringUtils.EMPTY);
            String searchTabStr = Optional.ofNullable(asset.getMetadataValue(AssetConstants.EATON_SEARCHTAB))
                    .orElse(StringUtils.EMPTY);
            AssetDetails assetDetails = new AssetDetails(asset.getName(), asset.getPath(), asset.getMimeType()
                    , resType, asset.getID(), countryStr, languageStr, searchTabStr);
            tagAssetsDetails.add(assetDetails);
        }
        return tagAssetsDetails;
    }

    /**
     * findAssetsByTags method which excute the query and fetch all the Hits.
     *
     * @param resourceResolver the value to be used.
     * @param tagList          the value to be used.
     * @return list of Hit.
     */
    public List<Hit> findAssetsByTags(ResourceResolver resourceResolver, final String[] tagList) {
        List<Hit> hitList = null;
        if (null != resourceResolver && null != tagList) {
            final Map<String, String> queryParams = new HashMap<>();
            final Session session = resourceResolver.adaptTo(Session.class);
            final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
            queryParams.put(JcrQueryConstants.PROP_TYPE, DamConstants.NT_DAM_ASSET);
            queryParams.put(CommonConstants.DAM_PATH, CommonConstants.CONTENT_DAM_EATON);
            queryParams.put(JcrQueryConstants.P_HITS, JcrQueryConstants.FULL);
            queryParams.put(JcrQueryConstants.P_LIMIT, JcrQueryConstants.INFINITY_VALUE);
            queryParams.put(CommonConstants.PROPERTY, CommonConstants.METADATA_CQ_TAGS);
            queryParams.put(CommonConstants.PROPERTY_OPERATION, CommonConstants.LIKE);
            for (int count = 0; count < tagList.length; count++) {
                queryParams.put((count + 1) + CommonConstants.UNDERSCORE_PROPERTY, CommonConstants.METADATA_CQ_TAGS);
                queryParams.put((count + 1) + CommonConstants.PROPERTY_VALUE, tagList[count]);
            }
            hitList = JcrQueryUtils.excuteGenericQuery(session, queryBuilder, queryParams);
        }
        return hitList;
    }

    private List<AssetDetails> populateTagAssetList(ValueMap dropdownCompPropMap, ResourceResolver resResolver) {
        List<AssetDetails> tagAssetList = new ArrayList<>();
        if (null != dropdownCompPropMap.get(CommonConstants.TAGS) && null != dropdownCompPropMap.get(CommonConstants.SLING_RESOURCETYPE)) {
            final Object mainTags = dropdownCompPropMap.get(CommonConstants.TAGS);
            String resourceType = dropdownCompPropMap.get(CommonConstants.SLING_RESOURCETYPE).toString();
            if (mainTags instanceof String[]) {
                String[] tags = (String[]) dropdownCompPropMap.get(CommonConstants.TAGS);
                if (null != tags && tags.length > 0) {
                    tagAssetList = populateTagsAsset(tags, resResolver, resourceType);
                }
            } else {
                String stringTags = dropdownCompPropMap.get(CommonConstants.TAGS).toString();
                String[] tagsArray = new String[]{stringTags};
                tagAssetList = populateTagsAsset(tagsArray, resResolver, resourceType);
            }
        }
        return tagAssetList;
    }

    /**
     * This will find fetch all the tagged Asset on page.
     *
     * @param resourceResolver the value to be used.
     * @param request          the value to be used.
     * @return list of AssetDetails.
     */
    public List<AssetDetails> fetchAllTagedAssets(SlingHttpServletRequest request, ResourceResolver resourceResolver, String resourceType) {
        List<AssetDetails> tagAssetList = new ArrayList<>();
        try {
            if (null != request.getPathInfo()) {
                String pageUrl = request.getPathInfo();
                String pagePath = pageUrl.replace(ASSETREF, CommonConstants.ARTICLE_PAGE_TEMPLATE_PATH);
                final Session session = request.getResourceResolver().adaptTo(Session.class);
                QueryManager queryManager = null;
                queryManager = session.getWorkspace().getQueryManager();
                tagAssetList = fetchComponentTaggedAsset(queryManager, pagePath, resourceResolver, resourceType, request);
            }
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return tagAssetList;
    }

    private List<AssetDetails> fetchComponentTaggedAsset(QueryManager queryManager, String pagePath, ResourceResolver resResolver, String resourceType, SlingHttpServletRequest slingRequest) {
        List<AssetDetails> tagAssetList = new ArrayList<>();
        Query queryStr = null;
        NodeIterator nodes = null;
        try {
            if (null != queryManager) {
                queryStr = queryManager.createQuery(
                        "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE(["
                                + pagePath + "]) AND ([sling:resourceType] = " + "'" + resourceType + "'" + ")", Query.JCR_SQL2);
                QueryResult queryResult = null;
                if (null != queryStr) {
                    queryResult = queryStr.execute();
                    if (null != queryResult) {
                        nodes = queryResult.getNodes();
                    }
                }
                if (resourceType.equals(DOWNLOAD_LIST_TYPE)) {
                    tagAssetList = formTagList(nodes, resResolver);
                } else if (resourceType.equals(RESOURCE_LIST_TYPE)) {
                    tagAssetList = formTagResourceList(nodes, resResolver, slingRequest);
                } else {
                    LOGGER.info("Component is not DownloadLinkList or ResourceList");
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Repository Exception in fetchDownLoadLinkListTaggedAsset" + e.getMessage(), e);
        }
        return tagAssetList;
    }

    private List<AssetDetails> formTagList(NodeIterator nodes, ResourceResolver resourceResolver) {
        List<String> nodeToPublish = new ArrayList<>();
        List<AssetDetails> tagAssetList = new ArrayList<>();
        Node node = null;
        try {
            if (null != nodes) {
                while (nodes.hasNext()) {
                    node = nodes.nextNode();
                    nodeToPublish.add(node.getPath());
                }
                tagAssetList = fetchNodeTagAsset(nodeToPublish, resourceResolver);
            }
        } catch (RepositoryException e) {
            LOGGER.error("Repository Exception in formTagList" + e.getMessage(), e);
        }
        return tagAssetList;
    }

    private List<AssetDetails> fetchNodeTagAsset(List<String> nodeToPublish, ResourceResolver resourceResolver) {
        Resource res = null;
        List<AssetDetails> tagAssetList = new ArrayList<>();
        if (!nodeToPublish.isEmpty()) {
            for (String path : nodeToPublish) {
                res = resourceResolver.getResource(path);
                if (null != res) {
                    ValueMap dropdownCompPropMap = res.getValueMap();
                    if (!populateTagAssetList(dropdownCompPropMap, resourceResolver).isEmpty()) {
                        tagAssetList = populateTagAssetList(dropdownCompPropMap, resourceResolver);
                    }
                }
            }
        }
        return tagAssetList;
    }

    /**
     * This will fetch all the page Asset.
     *
     * @param currentNode the value to be used.
     * @param request     the value to be used.
     * @return list of AssetDetails.
     */
    public List<AssetDetails> fetchAllPageAsset(Node currentNode, SlingHttpServletRequest request) {
        List<AssetDetails> pageAssetList = new ArrayList<>();
        // Using AssetReferenceSearch which will do all the work for us
        AssetReferenceSearch assetReferenceSearch = new AssetReferenceSearch(currentNode, CommonConstants.CONTENT_DAM_NO_EXTRA_SLASH,
                request.getResourceResolver());
        Map<String, Asset> result = assetReferenceSearch.search();
        Asset asset = null;
        for (Map.Entry<String, Asset> assetValue : result.entrySet()) {
            if (assetValue != null && assetValue.getKey() != null && !(assetValue.getKey().isEmpty())
                    && assetValue.getValue() != null) {
                asset = assetValue.getValue();
                if (null != asset) {
                    String countryStr = Optional.ofNullable(asset.getMetadataValue(AssetConstants.EATON_COUNTRY))
                            .orElse(StringUtils.EMPTY);
                    String languageStr = Optional.ofNullable(asset.getMetadataValue(AssetConstants.EATON_LANGAUGE))
                            .orElse(StringUtils.EMPTY);
                    String searchTabStr = Optional.ofNullable(asset.getMetadataValue(AssetConstants.EATON_SEARCHTAB))
                            .orElse(StringUtils.EMPTY);
                    AssetDetails assetDetails = new AssetDetails(asset.getName(), asset.getPath(), asset.getMimeType()
                            , SPACE_STRING, asset.getID(), countryStr, languageStr, searchTabStr);
                    pageAssetList.add(assetDetails);
                }
            }
        }
        return pageAssetList;
    }

    /**
     * This will fetch all the resourceList Asset.
     *
     * @param propertyName the value to be used.
     * @param resourceResolver     the value to be used.
     * @param request     the value to be used.
     * @return list of propertyValue.
     */
    public String[] getPageProperty(final String propertyName, ResourceResolver resourceResolver, SlingHttpServletRequest request) {
        LOGGER.debug("Start of PageAssetReferenceServiceImpl - getPageProperty().");
        String[] propertyValue = null;
        currentPage = returnCurrentPage(resourceResolver, request);
        if (null != currentPage) {
            final ValueMap pageProperties = currentPage.getProperties();
            if (null != pageProperties) {
                propertyValue = pageProperties.get(propertyName, new String[0]);
            }
        }
        LOGGER.debug("End of PageAssetReferenceServiceImpl - getPageProperty().");
        return propertyValue;
    }

    private Page returnCurrentPage(ResourceResolver resourceResolver, SlingHttpServletRequest request) {
        if (null != request.getPathInfo()) {
            String pageUrl = request.getPathInfo();
            if (null != pageUrl) {
                String pagePath = pageUrl.replace(ASSETREF, CommonConstants.ARTICLE_PAGE_TEMPLATE_PATH);
                currentPage = resourceResolver.resolve(pagePath).adaptTo(Page.class);
            }
        }
        return currentPage;
    }

    private final List<Hit> getDAMAssetsForDocumentGroups(final String[] pageTeaserTagList, ResourceResolver resourceResolver) {
        LOGGER.debug("Start of PageAssetReferenceServiceImpl - getDAMAssetsForDocumentGroups().");
        List<Hit> hitList = null;
        if (null != pageTeaserTagList && pageTeaserTagList.length > 0) {
            final Map<String, String> queryParams = new LinkedHashMap<>();
            final Session session = resourceResolver.adaptTo(Session.class);
            final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
            queryParams.put(JcrQueryConstants.PROP_TYPE, DamConstants.NT_DAM_ASSET);
            queryParams.put(CommonConstants.QUERY_PATH, CommonConstants.CONTENT_DAM_EATON);
            queryParams.put(JcrQueryConstants.P_HITS, JcrQueryConstants.FULL);
            queryParams.put(JcrQueryConstants.P_LIMIT, JcrQueryConstants.INFINITY_VALUE);
            queryParams.put(CommonConstants.PROPERTY, CommonConstants.METADATA_CQ_TAGS);
            queryParams.put(CommonConstants.PROPERTY_OPERATION, CommonConstants.LIKE);
            for (int count = 0; count < documentGroupList.size(); count++) {
                String documentGroupWithAemTag = documentGroupList.get(count).getAemtags();
                if (StringUtils.isNotEmpty(documentGroupWithAemTag)) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(PROPERTY_ZERO);
                    builder.append(count);
                    builder.append(QUERY_PARAM_VAL);
                    queryParams.put(builder.toString(), documentGroupWithAemTag);
                }
            }
            for (int count = 0; count < pageTeaserTagList.length; count++) {
                queryParams.put((count + 1) + CommonConstants.UNDERSCORE_PROPERTY, CommonConstants.METADATA_CQ_TAGS);
                queryParams.put((count + 1) + CommonConstants.PROPERTY_VALUE, pageTeaserTagList[count]);
            }
            hitList = JcrQueryUtils.excuteGenericQuery(session, queryBuilder, queryParams);
        }
        LOGGER.debug("End of PageAssetReferenceServiceImpl - getDAMAssetsForDocumentGroups().");
        return hitList;
    }

    private List<AssetDetails> fetchResourceListTagAsset(List<String> nodeList, ResourceResolver resourceResolver, SlingHttpServletRequest request) {
        Resource res = null;
        List<AssetDetails> tagAssetList = new ArrayList<>();
        if (!nodeList.isEmpty()) {
            for (String path : nodeList) {
                res = resourceResolver.getResource(path);
                if (null != res) {
                    ValueMap propMap = res.getValueMap();
                    String resourceType = propMap.get(CommonConstants.SLING_RESOURCETYPE).toString();
                    if (resourceType.equals(RESOURCE_LIST_TYPE)) {
                        tagAssetList = getResourceListTaggedAssets(resourceType, resourceResolver, request);
                    }
                }
            }
        }
        return tagAssetList;
    }
    /**
     * This will fetch all the resourceList node Asset.
     *
     * @param resourceType the value to be used.
     * @param resourceResolver     the value to be used.
     * @param request     the value to be used.
     * @return list of propertyValue.
     */
    public List<AssetDetails> getResourceListTaggedAssets(String resourceType, ResourceResolver resourceResolver, SlingHttpServletRequest request) {
        List<AssetDetails> tagAssetList = new ArrayList<>();
        List<Hit> hitList = null;
        final String[] pageTeaserTagList = getPageProperty(CommonConstants.PRIMARY_SUB_CATEGORY_TAG, resourceResolver, request);
        if (null != pageTeaserTagList && pageTeaserTagList.length > 0) {
            hitList = getDAMAssetsForDocumentGroups(pageTeaserTagList, resourceResolver);
            if (null != hitList && !hitList.isEmpty()) {
                tagAssetList = fetchAssetDetails(hitList, resourceType);
            }
        }
        return tagAssetList;
    }

    private List<AssetDetails> formTagResourceList(NodeIterator nodes, ResourceResolver resourceResolver, SlingHttpServletRequest request) {
        List<String> nodeList = new ArrayList<>();
        List<AssetDetails> tagAssetList = new ArrayList<>();
        Node node = null;
        try {
            if (null != nodes) {
                while (nodes.hasNext()) {
                    node = nodes.nextNode();
                    nodeList.add(node.getPath());
                }
                tagAssetList = fetchResourceListTagAsset(nodeList, resourceResolver, request);
            }
        } catch (RepositoryException e) {
            LOGGER.error("Repository Exception in formTagList" + e.getMessage(), e);
        }
        return tagAssetList;
    }
}
