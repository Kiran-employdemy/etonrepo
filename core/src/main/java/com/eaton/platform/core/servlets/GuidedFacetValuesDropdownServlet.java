package com.eaton.platform.core.servlets;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.google.gson.Gson;
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.eaton.platform.core.constants.CommonConstants.ASCENDING_SORT;
import static org.apache.sling.query.SlingQuery.$;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/GuidedFacetValuesDropdown",
        })
public class GuidedFacetValuesDropdownServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(GuidedFacetValuesDropdownServlet.class);

    @Reference
    private EndecaRequestService endecaRequestService;

    @Reference
    private transient EndecaService endecaService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        List<FacetGroupBean> facetGroupResponse = null;
        final ResourceResolver resourceResolver = request.getResourceResolver();
        String refererURL = CommonUtil.getRefererURL(request);
        String facetList = StringUtils.EMPTY;
        final PrintWriter responseWriter = response.getWriter();
        response.setContentType(AssetDownloadConstants.APPLICATION_JSON);
        String pagePathFromRequest = CommonUtil.getContentPath(resourceResolver, refererURL);
        final Resource pageResource = resourceResolver.resolve(pagePathFromRequest);
        final String groupId = Optional.ofNullable(request.getParameter("groupId")).orElse(StringUtils.EMPTY);
        final Optional<Resource> resourceOptional = $(pageResource).find(CommonConstants.GUIDE_CONTAINER).asList().stream().findFirst();
        if (resourceOptional.isPresent()) {
            final Resource resourceOfTxnmyAttributeDropDown = resourceOptional.get();
            final ValueMap valueMapOfResourceOfTxnmyAttributeDropDown = resourceOfTxnmyAttributeDropDown.getValueMap();
            if (valueMapOfResourceOfTxnmyAttributeDropDown.containsKey(CommonConstants.REDIRECT)) {
                final String productFamilyPagePath = valueMapOfResourceOfTxnmyAttributeDropDown.get(CommonConstants.REDIRECT).toString();
                if (StringUtils.isNotEmpty(productFamilyPagePath)) {
                    final Page PfpPage = request.getResourceResolver().resolve(productFamilyPagePath).adaptTo(Page.class);
                    if (null != PfpPage) {
                        LOG.info(PfpPage + PfpPage.getPath());
                        ArrayList<String> emptyList = new ArrayList<>();
                        final EndecaServiceRequestBean endecaServiceRequestBean = endecaRequestService.getProductFamilyEndecaRequestBean(PfpPage, emptyList, ASCENDING_SORT, null);
                        LOG.info("EndecaServiceRequestBean ::" + endecaServiceRequestBean.getSearchTerms());
                        final List<FilterBean> filters = endecaServiceRequestBean.getFilters();
                        final Optional<FilterBean> returnFacetsForOptional = filters.stream()
                                .filter(filterBean -> filterBean.getFilterName().equals("ReturnFacetsFor"))
                                .findFirst();
                        if (returnFacetsForOptional.isPresent()) {
                            FilterBean filterBean = returnFacetsForOptional.get();
                            ArrayList<String> returnFacets = new ArrayList<String>();
                            returnFacets.add(groupId);
                            filterBean.setFilterValues(returnFacets);
                        }
                        final SKUListResponseBean skuListResponseBean = endecaService.getSKUList(endecaServiceRequestBean, PfpPage.getContentResource());

                        if (skuListResponseBean != null && skuListResponseBean.getFamilyModuleResponse() != null && skuListResponseBean.getFamilyModuleResponse().getFacets() != null) {
                            facetGroupResponse = skuListResponseBean.getFamilyModuleResponse().getFacets().getFacetGroupList();
                            final Gson gson = new Gson();
                            facetList = gson.toJson(facetGroupResponse);
                            LOG.info("facetList ::" + facetList);
                        } else {
                            LOG.error("SKU Request failed for FormFacetsDropDownServlet");
                            facetGroupResponse = new ArrayList<>();
                        }
                        LOG.info("facetGroupResponse" + facetGroupResponse);
                    }
                }
            }
        }
        responseWriter.write(facetList);
        responseWriter.flush();
    }
}

