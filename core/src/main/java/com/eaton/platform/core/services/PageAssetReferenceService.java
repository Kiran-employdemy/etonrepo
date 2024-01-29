package com.eaton.platform.core.services;

import com.day.cq.search.result.Hit;
import com.eaton.platform.core.servlets.AssetDetails;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import javax.jcr.Node;
import java.util.List;

/**
 * The Interface PageAssetReferenceService.
 */
public interface PageAssetReferenceService {
    /**
     * This will populate all tagged Asset on page.
     *
     * @param tags             the value to be used.
     * @param resourceResolver the value to be used.
     * @param resourceType     the value to be used.
     * @return list of AssetDetails.
     */
    List<AssetDetails> populateTagsAsset(String[] tags, ResourceResolver resourceResolver, String resourceType);

    /**
     * This will find Asset on page.
     *
     * @param resourceResolver the value to be used.
     * @param tagList          the value to be used.
     * @return list of Hit.
     */
    List<Hit> findAssetsByTags(ResourceResolver resourceResolver, final String[] tagList);

    /**
     * This will find fetch all Asset on page.
     *
     * @param resourceResolver the value to be used.
     * @param request          the value to be used.
     * @return list of AssetDetails.
     */
    List<AssetDetails> fetchAllTagedAssets(SlingHttpServletRequest request, ResourceResolver resourceResolver,String resTyep);

    /**
     * This will fetch all the page Asset.
     *
     * @param currentNode the value to be used.
     * @param request     the value to be used.
     * @return list of AssetDetails.
     */
    List<AssetDetails> fetchAllPageAsset(Node currentNode, SlingHttpServletRequest request);

}
