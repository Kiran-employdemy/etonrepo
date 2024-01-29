package com.eaton.platform.core.models;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.eaton.platform.core.services.AdminService;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import static com.eaton.platform.core.constants.CommonConstants.CQ_TAGS;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductGridSlingModel {
    private static final Logger LOG = LoggerFactory.getLogger(ProductGridSlingModel.class);

    @Inject
    private Resource resource;

    @Inject @Optional
    private Resource taxonomyAttributes;

    @Inject @Optional
    private Resource globalAttributes;

    @Inject @Optional
    private Resource tagAttributes;

    @Inject @Optional
    private String[] tags;

    @Inject @Optional
    private Resource tabularFields;

    @Inject @Optional
    private String subCategoryHideShow;

    @Inject @Optional
    private Resource sortByOptions;

    @Inject @Optional
    private String defaultSortingOption;

    @Inject @Optional
    private String defaultSortingDirection;

    @Inject @Optional
    private String hideGlobalFacetSearch;

    @Inject @Optional
    private String view;

    @Inject @Default(booleanValues = false)
    private boolean turnOffProductTypes;

    @Inject
    @Default(values = "false")
    private boolean hidePriceDisc;

    @Inject
    @Optional
    private String hideProductDescription;

    @OSGiService
    private AdminService adminService;

    private List<String> facetTags;
    private Set<String> selectedSortByOptions;
    private Map<String,String[]> selectedTabularFields;
    private final static String SORT_BY_OPTIONS = "sortByOptionsValue";

    @PostConstruct
    protected void init() {
        if (adminService != null) {
            try (ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
                TagManager tagManager = adminReadResourceResolver.adaptTo(TagManager.class);
                facetTags = createFacetTags(tagAttributes, tags, tagManager);
                selectedSortByOptions = createSelectedSortByOptions(sortByOptions);
                selectedTabularFields = createSelectedTabularFields(tabularFields);
            }
        } else {
            LOG.error("Could not retrieve an admin service in ProductGridSlingModel.init");
        }
    }

    public String[] getTags() { return tags; }
    public String getSubCategoryHideShow() { return subCategoryHideShow; }
    public String getHideGlobalFacetSearch() { return hideGlobalFacetSearch; }
    public Resource getTaxonomyAttributes() { return taxonomyAttributes; }
    public Resource getGlobalAttributes() { return globalAttributes; }
    public Resource getTagAttributes() { return tagAttributes; }
    public Resource getSortByOptions() { return sortByOptions; }
    public String getDefaultSortingDirection() { return defaultSortingDirection; }
    public List<String> getFacetTags() { return facetTags; }
    public Set<String> getSelectedSortByOptions() { return selectedSortByOptions; }
    public String getView() { return view; }
    public Resource getTabularFields() { return tabularFields; }
    public Map<String, String[]> getSelectedTabularFields() { return selectedTabularFields; }

    public boolean isSubCategory() {
        return CommonConstants.SUBCATEGORY.equals(view);    
    }

    public boolean isProductFamily() {
        return CommonConstants.PRODUCT_FAMILY.equals(view);
    }

    /**
     * The full set of configured attributes based upon the global attributes and taxonomy attributes.
     * @return
     */
    public Set<String> getAttributeSet() {
    	Set<String> globalAttrSet = getGlobalAttributeSet();
    	globalAttrSet.remove(StringUtils.EMPTY);
    	return Stream.concat(getTaxonomyAttributeSet().stream(), globalAttrSet.stream()).collect(Collectors.toSet());
    }

    /**
     * The global attributes selected for this product grid. The list returned is the exact list the author has selected
     * and is not converted based upon the Endeca cloud config.
     * @return The global attributes configured on the product grid.
     */
    public Set<String> getGlobalAttributeSet() {
        return globalAttributes != null
                ? StreamSupport.stream(globalAttributes.getChildren().spliterator(), false)
                    .map(globalAttribute -> globalAttribute.getValueMap().get(CommonConstants.GLOBAL_ATTRIBUTE_VALUE, StringUtils.EMPTY))
                    .collect(Collectors.toSet())
                : new HashSet<>();
    }

    /**
     * All of the taxonomy attributes configured in this product grid. Each selection might map to more than one attribute
     * or multiple selected attributes might map to the same attribute based upon the Endeca cloud config
     * @return The taxonomy attributes configured for this product grid.
     */
    public Set<String> getTaxonomyAttributeSet() {
        return taxonomyAttributes != null
                ? StreamSupport.stream(taxonomyAttributes.getChildren().spliterator(), false)
                    .map(resource -> resource.adaptTo(SelectedTaxonomyAttrModel.class))
                    .filter(Objects::nonNull)
                    .collect(HashSet::new, (set, taxonomyAttribute) -> set.addAll(taxonomyAttribute.getAttributeNames()), HashSet::addAll)
                : new HashSet<>();
    }

    public String getDefaultSortingOption() {
        String formattedOption = "";
        if (this.isSubCategory()) {
            try (ResourceResolver adminReadResourceResolver = adminService.getReadService()) {
                TagManager tagManager = adminReadResourceResolver.adaptTo(TagManager.class);
                if(null != tagManager) {
                    Tag facetTag = tagManager.resolve(defaultSortingOption);
                    if (facetTag != null) {
                        EndecaFacetTag endecaFacetTag = facetTag.adaptTo(EndecaFacetTag.class);
                        formattedOption = endecaFacetTag.getFacetId();
                    }
                }
            }
        }

        if (formattedOption == "") {
            formattedOption = defaultSortingOption;
        }

        return formattedOption;
    }

    private static Set<String> createSelectedSortByOptions(Resource sortByOptions) {
        final Set<String> selectedSortByOptions = new HashSet<>();
        if(Objects.nonNull(sortByOptions) && sortByOptions.hasChildren()) {
            sortByOptions.listChildren().forEachRemaining(child ->
                    selectedSortByOptions.add(child.getValueMap().get(SORT_BY_OPTIONS, String.class)));
        }
        return selectedSortByOptions;
    }

    private static Map <String,String[]> createSelectedTabularFields(Resource sortByOptions) {
        Map<String,String[]> mapFacetGrpValList = new HashMap<>();
        if(null != sortByOptions && sortByOptions.hasChildren()) {
            sortByOptions.listChildren().forEachRemaining(facetGrpValList -> {
                if (facetGrpValList.getValueMap().containsKey(CommonConstants.FACET_GROUP) && facetGrpValList.getValueMap().containsKey(CommonConstants.FACET_VALUE)) {
                    final String key = facetGrpValList.getValueMap().get(CommonConstants.FACET_GROUP, StringUtils.EMPTY);
                    final String[] value = facetGrpValList.getValueMap().get(CommonConstants.FACET_VALUE, String[].class);
                    mapFacetGrpValList.put(key, value);
                }
            });
        }
        return mapFacetGrpValList;
    }

    private static List<String> createFacetTags(Resource tagAttributes, String[] tags, TagManager tm) {
        final List<String> facetTags = new ArrayList<>();
        List<String> authoredFacetTags = new ArrayList<>();

        if (tagAttributes != null) {
            for (Resource tagAttrResource : tagAttributes.getChildren()) {
                String facetTagName = tagAttrResource.getValueMap().get(CQ_TAGS, "");
                if (!facetTagName.isEmpty()) {
                    authoredFacetTags.add(facetTagName);
                }
            }
        }

        // Fallback to the tags property if there are no tagAttributes property didn't have any values.
        if (authoredFacetTags.isEmpty() && tags != null) {
            authoredFacetTags = Arrays.asList(tags);
        }

        if (authoredFacetTags != null) {
            for (String authoredFacetTag : authoredFacetTags) {
                Tag facetTag = tm.resolve(authoredFacetTag);
                if (facetTag != null) {
                    EndecaFacetTag endecaFacetTag = facetTag.adaptTo(EndecaFacetTag.class);
                    if (endecaFacetTag.hasFacetId()) {
                        facetTags.add(endecaFacetTag.getFacetId());
                    }
                }
            }
            facetTags.add(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL);
        }

        return facetTags;
    }
    public boolean isTurnOffProductTypes() {
        return turnOffProductTypes;
    }

    public boolean getHidePriceDisc() {
        return this.hidePriceDisc;
    }

    public String getHideProductDescription(){
        return this.hideProductDescription;
    }
}


