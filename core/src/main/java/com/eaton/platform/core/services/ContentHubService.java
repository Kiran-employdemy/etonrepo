package com.eaton.platform.core.services;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.ContentHubBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Locale;
public interface ContentHubService {
    List<ContentHubBean> populateTagsModel(List<ContentHubBean> childPageAndTagsLinksList, String[] tags, Page resourcePage , ResourceResolver resourceResolver, final Locale language,final String showFilters,String defaultImagePath, String enablePublicationDate,final String sortBy, boolean isUnitedStatesDateFormat);
    List<FacetGroupBean> getCleanAEMFilters(final List<ContentHubBean> childPageAndTagsLinksList,String[] tags, final Locale locale,final ResourceResolver resourceResolver);
}