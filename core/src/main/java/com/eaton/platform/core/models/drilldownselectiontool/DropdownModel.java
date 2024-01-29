package com.eaton.platform.core.models.drilldownselectiontool;

import com.day.cq.commons.RangeIterator;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DropdownModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(DropdownModel.class);

    @Inject
    private String label;

    @Inject
    private String dropdownTagPath;

    private List<DropdownOptionModel> dropdownOptions;

    @Inject
    private AuthorizationService authorizationService;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDropdownTagPath() {
        return dropdownTagPath;
    }

    public void setDropdownTagPath(String dropdownTagPath) {
        this.dropdownTagPath = dropdownTagPath;
    }

    public List<DropdownOptionModel> getDropdownOptions() {
        return dropdownOptions;
    }

    public void setDropdownOptions(List<String> selectedDropdownOptionTagsList, SlingHttpServletRequest slingHttpServletRequest, Page currentPage) {
        LOGGER.debug("setDropdownOptions: START");
        LOGGER.debug("selectedDropdownOptionTagsList: {}", selectedDropdownOptionTagsList);

        List<DropdownOptionModel> dropdownOptionModelList = new ArrayList<>();
        Resource tagResource = Objects.requireNonNull(slingHttpServletRequest).getResourceResolver().getResource(this.getDropdownTagPath());
        Iterator<Resource> childTagResourceIterator = Objects.requireNonNull(tagResource).listChildren();

        if (Objects.requireNonNull(tagResource).hasChildren()) {

            while (childTagResourceIterator.hasNext()) {
                Resource childTagResource = childTagResourceIterator.next();

                List<String> tagsForResourceSearch = new ArrayList<>();
                tagsForResourceSearch.add(childTagResource.getPath());
                LOGGER.debug("tagsForResourceSearch before attempting to add previously selected options: {}", tagsForResourceSearch);

                if (!selectedDropdownOptionTagsList.isEmpty()) {
                    LOGGER.debug("At least 1 previous dropdown has been selected");
                    LOGGER.debug("Adding the selected options' tag paths of previously selected dropdowns to tagsForResourceSearch");
                    tagsForResourceSearch.addAll(selectedDropdownOptionTagsList);
                }

                if (atLeastOneResourceMatchesAllTags(tagsForResourceSearch, slingHttpServletRequest, currentPage)) {
                    LOGGER.debug("At least 1 resource contains all of the tagsForResourceSearch: {}", tagsForResourceSearch);
                    DropdownOptionModel dropdownOptionModel = DropdownOptionModel.of(childTagResource, Objects.requireNonNull(currentPage));
                    dropdownOptionModelList.add(dropdownOptionModel);
                    LOGGER.debug("dropdownOptionModel added to dropdownOptionModelList: {}", dropdownOptionModel);
                } else {
                    LOGGER.debug("No resources match all tags: {}", tagsForResourceSearch);
                }

            }

        } else {
            LOGGER.warn("dropdownTagPath {} has no child resources", dropdownTagPath);
        }

        LOGGER.debug("dropdownOptionModelList: {}", dropdownOptionModelList);
        this.dropdownOptions = dropdownOptionModelList;
        LOGGER.debug("setDropdownOptions: END");
    }


    /**
     * Verify at least one resource contains all tags
     * @param tagsForResourceSearch list of tags to match in page search
     * @return true if at least one resource matches all tags in tagsForResourceSearch
     */
    private boolean atLeastOneResourceMatchesAllTags(List<String> tagsForResourceSearch, SlingHttpServletRequest slingHttpServletRequest, Page currentPage) {

        LOGGER.debug("atLeastOneResourceMatchesAllTags: START");
        String[] tagsForResourceSearchArray = tagsForResourceSearch.toArray(new String[0]);
        LOGGER.debug("tagsForResourceSearchArray: {}", (Object) tagsForResourceSearchArray);
        TagManager tagManager = Objects.requireNonNull(slingHttpServletRequest).getResourceResolver().adaptTo(TagManager.class);
        String currentPageHomePagePath = CommonUtil.getHomePagePath(Objects.requireNonNull(currentPage));
        LOGGER.debug("currentPageHomePagePath: {}", currentPageHomePagePath);
        boolean oneMatchIsEnough = Boolean.FALSE;
        RangeIterator<Resource> resourceIterator = Objects.requireNonNull(tagManager).find(currentPageHomePagePath, tagsForResourceSearchArray, oneMatchIsEnough);

        if (null == resourceIterator) {
            LOGGER.debug("Cannot find any matching resources with all tags ({}) beneath {}", tagsForResourceSearchArray, currentPageHomePagePath);
            return false;
        }

        Stream<Resource> resourceStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(Objects.requireNonNull(resourceIterator), Spliterator.ORDERED), false);

        Optional<Resource> firstMatchResource = resourceStream
            .filter(resource -> !SecureUtil.isSecureResource(resource)
                || Objects.requireNonNull(authorizationService).isAuthorized(Objects.requireNonNull(slingHttpServletRequest), resource.getPath()))
            .findFirst();

        LOGGER.debug("atLeastOneResourceMatchesAllTags: END");
        return firstMatchResource.isPresent();
    }
}
