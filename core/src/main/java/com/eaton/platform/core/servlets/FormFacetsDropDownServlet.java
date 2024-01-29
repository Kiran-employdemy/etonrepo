package com.eaton.platform.core.servlets;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.ResourceDecorator;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.eaton.platform.core.constants.CommonConstants.ASCENDING_SORT;
import static com.eaton.platform.core.constants.CommonConstants.PRODUCT_SELECTOR_FORM_RESOURCE_TYPE;

/**
 * This servlet will populate facets to dropdown in AEM Forms page.
 * It will populate the facet json only in page where pageType(productFamily) is specified.
 * And won't populate the facet in form page where page path starts with /content/forms.
 * */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + PRODUCT_SELECTOR_FORM_RESOURCE_TYPE,
                ServletConstants.SLING_SERVLET_SELECTORS + FormFacetsDropDownServlet.SERVLET_SELECTOR,
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class FormFacetsDropDownServlet extends SlingSafeMethodsServlet{
    private static final Logger LOG = LoggerFactory.getLogger(FormFacetsDropDownServlet.class);
    public static final String SERVLET_SELECTOR = "formfacetdropdown";
    public static final String FACETS_SELECTOR = "activeFacets";

    @Reference
    private transient EndecaService endecaService;

    @Reference
    private EndecaRequestService endecaRequestService;

    @Reference
    private EatonSiteConfigService eatonSiteConfigService;

    private static Integer totalCount;
    private static boolean isExceedLimit;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        LOG.info("******** Form Facet Dropdown Servlet execution started ***********");
        totalCount = 0;
        isExceedLimit = false;
        final List<String> selectedFacets = getFacets(request);
        final Optional<Page> page = request.getResource().adaptTo(ResourceDecorator.class).getContainingPage();
        final List<FacetGroupBean> facetGroupResponse = getFormResponseForPage(page, selectedFacets);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(getFormFacets(facetGroupResponse));
    }

    private static List<String> getFacets(final SlingHttpServletRequest request) {
        final Optional<String> facetsSelector = Arrays.asList(request.getRequestPathInfo().getSelectors()).stream()
                .filter(selector -> selector.startsWith(FACETS_SELECTOR))
                .findFirst();

        final List<String> splitSelector = facetsSelector.isPresent()
                ? Arrays.asList(facetsSelector.get().split("\\$"))
                : new ArrayList<>();

        // We do this subList because we don't want the first "activeFacets" part of the selector in the facets list.
        return splitSelector.size() > 1
                ? splitSelector.subList(1, splitSelector.size())
                : new ArrayList<>();
    }

    /**
     * @param page The page to create a SKU request for.
     * @param selectedFacets The facets to apply to the SKU request.
     * @return The list of facet groups returned from Endeca for the given SKU request.
     */
    private List<FacetGroupBean> getFormResponseForPage(final Optional<Page> page, final List<String> selectedFacets) {
        final List<FacetGroupBean> facetGroupResponse;

        if (page.isPresent()) {
            final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService.getProductFamilyEndecaRequestBean(page.get(), selectedFacets, ASCENDING_SORT, null);
            final SKUListResponseBean skuListResponseBean = endecaService.getSKUList(endecaServiceRequestBean, page.get().getContentResource());
            final Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(page.get());
            totalCount = skuListResponseBean.getFamilyModuleResponse().getTotalCount();
            if(siteConfig.isPresent() && null != totalCount) {
                isExceedLimit = siteConfig.get().getPageSize() < totalCount;
            }
            if (skuListResponseBean != null && skuListResponseBean.getFamilyModuleResponse() != null && skuListResponseBean.getFamilyModuleResponse().getFacets() != null) {
                facetGroupResponse = skuListResponseBean.getFamilyModuleResponse().getFacets().getFacetGroupList();
            } else {
                LOG.error("SKU Request failed for FormFacetsDropDownServlet");
                facetGroupResponse = new ArrayList<>();
            }
        } else {
            LOG.error("Could not find containing page");
            facetGroupResponse = new ArrayList<>();
        }

        return facetGroupResponse;
    }

    /**
     * A list of facet json objects each with a name and a list of values.
     * @param facetGroups The facet group list to base the JSON object off of.
     * @return The JSON representation of the facet list.
     */
    private static JsonObject getFormFacets(final List<FacetGroupBean> facetGroups) {
        JsonArray facetRespArray = null;
        JsonObject responseObj = new JsonObject();
        try {
            facetRespArray = new JsonArray();
            List<JsonObject> facetRespCollect = facetGroups.stream()
                    .map(facetGroupBean -> {
                        JsonObject jsonFacet = new JsonObject();

                        List<JsonObject> facetValues = getFacetValues(facetGroupBean);

                        try {
                            jsonFacet.add("name", new Gson().toJsonTree(facetGroupBean.getFacetGroupId()));
                            jsonFacet.add("values", new Gson().toJsonTree(facetValues));
                        } catch (Exception e) {
                            LOG.error("Error adding facet to form facet dropdown servlet response.", e);
                        }

                        return jsonFacet;
                    }).collect(Collectors.toList());
            Iterator<JsonObject> iterator = facetRespCollect.iterator();
            while (iterator.hasNext()) {
                JsonObject next = iterator.next();
                facetRespArray.add(next);
            }

            responseObj.add("formFacets", new Gson().toJsonTree(facetRespArray));
            responseObj.add("totalCount", new Gson().toJsonTree(totalCount));
            responseObj.add("isExceedLimit",new Gson().toJsonTree(isExceedLimit));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseObj;

    }

    /**
     * A facet containing a name and a list of values.
     * @param facetGroupBean The Endeca response to base the JSON object off of.
     * @return The JSON representation of the facet.
     */
    private static List<JsonObject> getFacetValues(final FacetGroupBean facetGroupBean) {
        return facetGroupBean.getFacetValueList().stream()
        .map(facetValue -> {
            JsonObject jsonValueFacet = new JsonObject();

            try {
                jsonValueFacet.add("value", new Gson().toJsonTree(facetValue.getFacetValueId()));
                jsonValueFacet.add("label", new Gson().toJsonTree(facetValue.getFacetValueLabel()));
            } catch (Exception e) {
                LOG.error("Error adding value to form facet dropdown servlet repsonse.", e);
            }

            return jsonValueFacet;
        }).collect(Collectors.toList());
    }
}