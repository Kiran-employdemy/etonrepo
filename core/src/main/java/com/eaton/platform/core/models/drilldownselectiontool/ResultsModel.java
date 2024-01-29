package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.commons.RangeIterator;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ResultsModel {

    private long total;

    private List<ResultsItemModel> resultsItemModelList;


    public static ResultsModel of(SlingHttpServletRequest slingHttpServletRequest,
                                  AuthorizationService authorizationService,
                                  ResourceResolver resourceResolver,
                                  EatonSiteConfigService eatonSiteConfigService,
                                  Page currentPage,
                                  List<String> selectedDropdownOptionTags,
                                  int startIndex,
                                  int pageSize) {

        ResultsModel resultsModel = new ResultsModel();
        resultsModel.resultsItemModelList = resultsModel.getResults(slingHttpServletRequest, authorizationService, resourceResolver, eatonSiteConfigService, currentPage, selectedDropdownOptionTags);
        resultsModel.total = resultsModel.resultsItemModelList.size();
        resultsModel.resultsItemModelList = resultsModel.limitResults(startIndex, pageSize);
        return resultsModel;
    }

    private List<ResultsItemModel> limitResults(int startIndex, int pageSize) {
        int endIndex = Math.min(startIndex + pageSize, getResultsItemModelList().size());
        return this.getResultsItemModelList().subList(startIndex, endIndex);
    }

    private List<ResultsItemModel> getResults(SlingHttpServletRequest slingHttpServletRequest,
                                                           AuthorizationService authorizationService,
                                                           ResourceResolver resourceResolver,
                                                           EatonSiteConfigService eatonSiteConfigService,
                                                           Page currentPage,
                                                           List<String> selectedDropdownOptionTags) {

        String[] selectedDropdownOptionTagsArray = selectedDropdownOptionTags.toArray(new String[0]);
        TagManager tagManager = Objects.requireNonNull(resourceResolver).adaptTo(TagManager.class);
        String currentPageHomePagePath = CommonUtil.getHomePagePath(Objects.requireNonNull(currentPage));
        boolean oneMatchIsEnough = Boolean.FALSE;
        RangeIterator<Resource> resourceIterator = Objects.requireNonNull(tagManager).find(currentPageHomePagePath, selectedDropdownOptionTagsArray, oneMatchIsEnough);

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(Objects.requireNonNull(resourceIterator), Spliterator.ORDERED), false)
            .filter(resource -> !SecureUtil.isSecureResource(resource)
                || Objects.requireNonNull(authorizationService).isAuthorized(Objects.requireNonNull(slingHttpServletRequest), resource.getPath()))
            .map(resource -> ResultsItemModel.of(resourceResolver, resource, eatonSiteConfigService, currentPage))
            .collect(Collectors.toList());
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getTotal() {
        return total;
    }

    public List<ResultsItemModel> getResultsItemModelList() {
        return resultsItemModelList;
    }

    public void setResultsItemModelList(List<ResultsItemModel> resultsItemModelList) {
        this.resultsItemModelList = resultsItemModelList;
    }
}
