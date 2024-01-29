package com.eaton.platform.integration.pim.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.*;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.pim.util.PIMUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.apache.sling.query.SlingQuery.$;

/**
 * This servlet pre-populates the SortBy Options dropdown in Product Grid component
 *
 * @author - Sravya
 */

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/sortbyoptions",
        })
public class SortByOptionsDropdownServlet extends SlingSafeMethodsServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SortByOptionsDropdownServlet.class);

    @Reference
    private CloudConfigService cloudConfigService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.info("******** SortByOptionsDropdownServlet execution started 1*********** ");
        // Set fallback.
        request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        final ResourceResolver resourceResolver = request.getResourceResolver();
        boolean isViewSubCategory = StringUtils.contains(request.getPathInfo(), CommonConstants.SUBCATEGORY_PRODUCT_GRID_RESOURCE_PATH);
        final String pagePath = PIMUtil.getPagePathFromRequest(request);
        List<String> permittedSortOptions = new ArrayList<>();

        if (StringUtils.isNotEmpty(pagePath) && CommonUtil.startsWithAnySiteContentRootPath(pagePath)) {
            List<String> pimPaths = null;
            final Page currentPage = request.getResourceResolver().adaptTo(PageManager.class).getPage(pagePath);
            if (currentPage != null) {
                pimPaths = currentPage.adaptTo(PageDecorator.class).getPimPagePaths();
                permittedSortOptions = getPermittedSortOptions(currentPage.getContentResource());
            } else {
                LOG.error("SortByOptionsDropdownServlet: Could not retrieve a PIM path from the page: " + pagePath);
            }

            if (currentPage != null) {
            	final Optional<Resource> productGridResource = $(currentPage.getContentResource()).find(CommonConstants.PRODUCT_GRID_RESOURCE_TYPE).asList().stream().findFirst();
                HashMap<String, String> filteredMap = constructDropdownMap(request, resourceResolver, isViewSubCategory, permittedSortOptions, pimPaths, productGridResource);
	            final ValueMap dataSourceValueMap = request.getResource().getChild(CommonConstants.DATASOURCE_NODE_NAME).getValueMap();
	            final boolean addNoneOption = dataSourceValueMap.get(CommonConstants.DATASOURCE_ADD_NONE_PROP, true);
	            final String addNoneLabel = dataSourceValueMap.get(CommonConstants.DATASOURCE_ADD_NONE_TITLE_PROP, CommonConstants.DROPDOWN_DEFAULT_OPTION);
	            final List<Resource> dropdownList = PIMUtil.prepareDropDownList(resourceResolver, filteredMap, addNoneOption, addNoneLabel);
	
	            // Create a DataSource that is used to populate the drop-down control.
	            final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
	            request.setAttribute(DataSource.class.getName(), dataSource);
            }
        } else {
            LOG.error("SortByOptionsDropdownServlet: Must be accessed from the a '/content' page because it depends upon the Endeca Cloud Config. This request was accessed from: " + pagePath);
        }
    }

    /***
     * This method will construct the filterMap based on the pageType configured.
     * @param resourceResolver
     * @param isViewSubCategory
     * @param pimPaths
     * @param permittedSortOptions
     * @param productGridResource
     * @param request
     * @return filterMap
     */
    private HashMap<String, String> constructDropdownMap(SlingHttpServletRequest request, ResourceResolver resourceResolver, boolean isViewSubCategory, List<String> permittedSortOptions, List<String> pimPaths, Optional<Resource> productGridResource ) {
        HashMap<String, String> filteredMap = new HashMap<>();
        if (productGridResource.isPresent()) {
            if (isViewSubCategory) {
                filteredMap = prepareSelectedSubCategoryTagsMap(resourceResolver, productGridResource.get(), permittedSortOptions);
            } else {
                if (null != pimPaths) {
                    for (String pimPath : pimPaths) {
                        filteredMap.putAll(prepareSelectedTaxonomyAttributesMap(pimPath, resourceResolver, productGridResource.get(), permittedSortOptions));
                    }
                }
            }
        } else {
            LOG.error("No product grid resource found under resource: " + request.getResource().getPath());
        }
        return filteredMap;
    }

    private HashMap<String, String> prepareSelectedSubCategoryTagsMap(final ResourceResolver adminResourceResolver, final Resource productGridResource, final List<String> permittedSortOptions) {
        final HashMap<String, String> filteredMap = new HashMap<>();
        final ProductGridSlingModel productGrid = productGridResource.adaptTo(ProductGridSlingModel.class);

        if (productGrid != null) {
            final List<String> selectedTags = productGrid.getFacetTags();
            final TagManager tagManager = adminResourceResolver.adaptTo(TagManager.class);
            selectedTags.forEach(facetId -> {
                if (permittedSortOptions.size() == 0 || permittedSortOptions.contains(facetId)) {
                    final String tagId = EndecaFacetTag.convertId(facetId);
                    if (tagId != null) {
                        final Tag selectedTag = tagManager.resolve(tagId);
                        if (Objects.nonNull(selectedTag)) {
                            filteredMap.put(selectedTag.getTitle(), selectedTag.getTagID());
                        }
                    }
                }
            });
        }

        return filteredMap;
    }

    private HashMap<String, String> prepareSelectedTaxonomyAttributesMap(final String pimPagePath, final ResourceResolver adminResourceResolver, final Resource productGridResource, final List<String> permittedSortOptions) {
        final String englishPimPagePath = PIMUtil.getEnglishPIMPagePath(pimPagePath);

        // Find the full list taxonomy attributes.
        HashMap<String, String> unsortedMap = new HashMap<>();
        if (englishPimPagePath != null) {
            final Resource pimResource = adminResourceResolver.getResource(englishPimPagePath);
            if (Objects.nonNull(pimResource)) {
                Optional<EndecaConfigModel> endecaConfigModel = cloudConfigService.getEndecaCloudConfig(pimResource);
                final PIMResourceSlingModel pimResourceSlingModel = pimResource.adaptTo(PIMResourceSlingModel.class);
                if(null !=pimResourceSlingModel) {
                    unsortedMap = pimResourceSlingModel.getTaxonomyAttributesLookup(endecaConfigModel);
                }
            }
        }

        // Find the list of currently selected taxonomy attributes.
        final List<String> selectedTaxonomyList = new ArrayList<>();

        // Currently this assumes it is on a product family page.
        if (productGridResource != null) {
            final Resource selectedTaxonomyAttributesResource = productGridResource.adaptTo(ProductGridSlingModel.class).getTaxonomyAttributes();
            if (Objects.nonNull(selectedTaxonomyAttributesResource) && selectedTaxonomyAttributesResource.hasChildren()) {
                selectedTaxonomyAttributesResource.listChildren().forEachRemaining(child ->
                        selectedTaxonomyList.add(CommonUtil.getStringProperty(child.getValueMap(), "taxonomyAttributeValue")));
            }
        }

        // Filter the full taxonomy list by the selected and permitted attributes.
        final HashMap<String, String> filteredMap = new HashMap<>();
        unsortedMap.entrySet().stream()
                .filter(entry -> selectedTaxonomyList.contains(entry.getValue()))
                .filter(entry -> permittedSortOptions.contains(entry.getValue()))
                .forEach(entry -> filteredMap.put(entry.getKey(), entry.getValue()));

        return filteredMap;
    }

    private List<String> getPermittedSortOptions(final Resource resource) {
        Optional<EndecaConfigModel> endecaConfigModel = cloudConfigService.getEndecaCloudConfig(resource);
        return endecaConfigModel.isPresent() ? endecaConfigModel.get().getSortableAttributes() : new ArrayList<>();
    }
}
