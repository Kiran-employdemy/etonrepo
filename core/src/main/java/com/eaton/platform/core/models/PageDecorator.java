package com.eaton.platform.core.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Model(adaptables = { Page.class, Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageDecorator {
    private static final String NONE = "none";

    @Inject @Named(JcrConstants.JCR_CONTENT + CommonConstants.SLASH_STRING + CommonConstants.PAGE_PIM_PATH)
    private String pimPagePath;

    @Inject @Named(JcrConstants.JCR_CONTENT + CommonConstants.SLASH_STRING + CommonConstants.PAGE_PIM_PATHS)
    private String[] pimPagePaths;

    @Inject
    @Named(JcrConstants.JCR_CONTENT + CommonConstants.SLASH_STRING + "modelViewer")
    @Default(values = NONE)
    private String modelViewer;

    @Self
    private Page currentPage;

    private ResourceDecorator resourceDecorator;

	@PostConstruct
	protected void init() {
	    if (currentPage != null && currentPage.getContentResource() != null) {
            resourceDecorator = currentPage.getContentResource().adaptTo(ResourceDecorator.class);
        }
	}

    /**
     * See ResourceDecorator.findByResourceType.
     * @param resourceType The resource type to pass into the ResourceDecorator.findByResourceType method.
     * @return The first resource found under this page with the given resource type.
     */
	public Optional<Resource> findByResourceType(String resourceType) {
	    return resourceDecorator != null ? resourceDecorator.findByResourceType(resourceType) : Optional.empty();
    }

    /**
     * @return Either a list containing the single valued pim page path field if it is authored or the multivalued
     * product selector pims field if the single valued field is empty. This method will never return null and any
     * strings within the list will never be empty.
     */
    public List<String> getPimPagePaths() {
        if (StringUtils.isNotEmpty(pimPagePath)) {
            return Arrays.asList(pimPagePath);
        } else if (Objects.nonNull(pimPagePaths)) {
            return Arrays.asList(pimPagePaths);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * The PIMs associated to this page. If a product grid is present the PIMs come from this pages properties. If the
     * product grid is not present but the product selector form is present the PIMs returned come from the page properties
     * of the thank you page of the form.
     * @return The PIMs associate to this page as described above.
     */
    public List<PIMResourceSlingModel> getPims() {
        final List<String> pimPagePaths = new ArrayList<>();
        final Optional<Resource> productSelectorResource = this.findByResourceType(CommonConstants.PRODUCT_SELECTOR_FORM_RESOURCE_TYPE);

        if (productSelectorResource.isPresent()) {
            // If the page contains a product selector treat it as such and retrieve the pim list from the linked thank you page of the form.
            ProductSelectorFormModel productSelectorForm = productSelectorResource.get().adaptTo(ProductSelectorFormModel.class);

            if (productSelectorForm != null) {
                if (productSelectorForm.getRedirectPage().isPresent()) {
                    pimPagePaths.addAll(productSelectorForm.getRedirectPage().get().getPimPagePaths());
                }
            }
        } else {
            // Otherwise simply use the configured pim paths of this page itself.
            pimPagePaths.addAll(this.getPimPagePaths());
        }

        return pimPagePaths.stream()
                .map(path -> resourceDecorator.getResource().getResourceResolver().resolve(path)).filter(Objects::nonNull)
                .map(resource -> resource.adaptTo(PIMResourceSlingModel.class)).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * The facets associated to this page. This is either the taxonomy attribute list configured in the product grid if
     * it is present or the taxonomy attributes configured in the product selector form if such a form exists on the page.
     * @return The facets associated to this page as described above.
     */
    public Set<String> getFacets() {
        final Set<String> facets = new HashSet<>();

        Optional<Resource> productGridResource = resourceDecorator.findByResourceType(CommonConstants.PRODUCT_GRID_RESOURCE_TYPE);
        if (productGridResource.isPresent()) {
            ProductGridSlingModel productGrid = productGridResource.get().adaptTo(ProductGridSlingModel.class);
            if (productGrid != null) {
                facets.addAll(productGrid.getAttributeSet());
            }
        } else {
            Optional<Resource> productSelectorFormResource = resourceDecorator.findByResourceType(CommonConstants.PRODUCT_SELECTOR_FORM_RESOURCE_TYPE);

            if (productSelectorFormResource.isPresent()) {
                ProductSelectorFormModel productSelectorForm = productSelectorFormResource.get().adaptTo(ProductSelectorFormModel.class);

                if (productSelectorForm != null) {
                    facets.addAll(productSelectorForm.getAttributeSet());
                }
            }
        }

         return facets;
    }

    public String getModelViewer() {
        return modelViewer;
    }

	public Boolean isShow3dModel() {
		return ! NONE.equals(this.getModelViewer());
	}
}
