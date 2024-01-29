package com.eaton.platform.core.services;

import com.eaton.platform.core.bean.ContentHubAssetsBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface ContentHubAssetsService {
    List<ContentHubAssetsBean> populateTagsModel(List<ContentHubAssetsBean> childAssetAndTagsLinksList, String[] tags, ResourceResolver resourceResolver, final Locale language, final String[] eyebrowTag, Map<String, String> params, String[] countries, String[] languages, String[] taxonomy);
    List<FacetGroupBean> getCleanAEMFilters(final List<ContentHubAssetsBean> childAssetAndTagsLinksList, String[] tags, final Locale locale, final ResourceResolver resourceResolver,String[] taxonomy,Map<String, String> params);
}