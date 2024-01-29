package com.eaton.platform.core.models;

import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import com.day.cq.commons.Externalizer;
import java.util.*;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import com.eaton.platform.core.services.CloudConfigService;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eaton.platform.core.servlets.FormFacetsDropDownServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import static com.eaton.platform.core.constants.CommonConstants.PRODUCT_SELECTOR_FORM_DROPDOWN_RESOURCE_TYPE;
import static com.eaton.platform.core.constants.CommonConstants.GUIDED_PRODUCT_SELECTOR_FORM_DROPDOWN_RESOURCE_TYPE;
import static com.eaton.platform.core.constants.CommonConstants.GUIDE_CONTAINER_RESOURCE_TYPE;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductSelectorFormModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSelectorFormModel.class);
    public static final List<String> DROPDOWN_LIST_RESOURCE_TYPES = Arrays.asList(PRODUCT_SELECTOR_FORM_DROPDOWN_RESOURCE_TYPE, GUIDED_PRODUCT_SELECTOR_FORM_DROPDOWN_RESOURCE_TYPE);

    @OSGiService
    private CloudConfigService cloudConfigService;

    @OSGiService
    private Externalizer externalizer;

    @Inject
    private String formRef;

    @Inject
    private Resource resource;

    @Inject
    private SlingHttpServletRequest slingRequest;

    private Optional<ResourceDecorator> form = Optional.empty();
    private Optional<PageDecorator> redirectPage = Optional.empty();

    @PostConstruct
    private void init() {
        if (formRef != null) {
            // All Adaptive forms will create under "/content/dam/formsanddocuments" and the mirror of the page (cq:page) path is always "/content/forms/af"
            formRef = formRef.replace(CommonConstants.AEM_FORMS_MIRROR_BASE_PATH, CommonConstants.AEM_FORMS_ACTUAL_BASE_PATH);
            Resource formRefResource = resource.getResourceResolver().resolve(formRef);

            if (formRefResource != null) {
                form = Optional.ofNullable(formRefResource.adaptTo(ResourceDecorator.class));
                if (form.isPresent()) {
                    Optional<Resource> formContainer = form.get().findByResourceType(GUIDE_CONTAINER_RESOURCE_TYPE);

                    setRedirectPath(formContainer);
                }
            }
        }else {
            LOGGER.debug("\n ###### Looks like form is not configured #####\n");
        }


    }

    /**
     * This method will update the redirect page from the form resource
     * */
    private void setRedirectPath(Optional<Resource> formContainer) {
        if (formContainer.isPresent()) {
            String redirectPath = formContainer.get().getValueMap().get(CommonConstants.REDIRECT, String.class);

            if (redirectPath != null) {
                Resource redirectResource = resource.getResourceResolver().resolve(redirectPath);

                if (redirectResource != null) {
                    redirectPage = Optional.ofNullable(redirectResource.adaptTo(PageDecorator.class));
                }
            }else {
                LOGGER.debug("\n######## Redirect page is not configured in the form #########\n");
            }
        }
    }

    public Optional<PageDecorator> getRedirectPage() {
        return redirectPage;
    }

    /**
     * @return The component path. This will be externalized if a slingRequest object is available.
     */
    public String getComponentPath() {
        return externalizer != null
                ? externalizer.relativeLink(slingRequest, resource.getPath())
                : resource.getPath();
    }

    /**
     * @return The url that can be used to make requests to the FormFacetsDropDownServlet.
     */
    public String getProductUrl() {
        return StringUtils.join(
                    Arrays.asList(
                            getComponentPath(),
                            FormFacetsDropDownServlet.SERVLET_SELECTOR,
                            FormFacetsDropDownServlet.FACETS_SELECTOR,
                            CommonConstants.EXTENSION_JSON)
                            .toArray(), ".");
    }

    public String getFacetSelector() {
        return FormFacetsDropDownServlet.FACETS_SELECTOR;
    }

    /**
     * @return The list of product dropdown components configured for this product selector form component.
     */
    public List<Resource> getDropDownListResources() {
        return form.isPresent()
                ? form.get().findAllByResourceTypes(DROPDOWN_LIST_RESOURCE_TYPES)
                : new ArrayList<>();
    }

    /**
     * The list of attributes configured for this product selector form component based upon the product dropdown components
     * that have been configured on the form.
     * @return The list of taxonomy attributes based upon the configured product selector dropdown components.
     */
    public Set<String> getAttributeSet() {
        final Optional<EndecaConfigModel> endecaConfigModel = cloudConfigService != null
                ? cloudConfigService.getEndecaCloudConfig(resource) : Optional.empty();
        return getDropDownListResources().stream()
                .map(resource -> resource.adaptTo(SelectedTaxonomyAttrModel.class))
                .filter(Objects::nonNull)
                .map(selectedAttr -> selectedAttr.getAttributeNames(endecaConfigModel))
                .collect(HashSet::new, HashSet::addAll, HashSet::addAll);
    }
}
