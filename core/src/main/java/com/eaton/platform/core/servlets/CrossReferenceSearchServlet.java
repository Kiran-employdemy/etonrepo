package com.eaton.platform.core.servlets;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.CrossReferenceModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/product/cross-reference",
                ServletConstants.SLING_SERVLET_SELECTORS + "crossreference",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class CrossReferenceSearchServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 6986390122000208721L;
    private static final Logger LOGGER = LoggerFactory.getLogger(CrossReferenceSearchServlet.class);
    private static final String PAGE = "page";
    private static final String SORT_BY = "sortBy";
    private static final String DEFAULT_STARTING_RECORD = "0";
    private static final String BASE_SKU_PATH = "baseSKUPath";

    @Reference
    private EndecaRequestService endecaRequestService;

    @Reference
    private EndecaService endecaService;

    @Reference
    private EatonConfigService configService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {
        LOGGER.debug("CrossReferenceSearchServlet :: doGet() :: Started");

        final String refererURL = CommonUtil.getRefererURL(request);
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final String pagePath = CommonUtil.getContentPath(resourceResolver, refererURL);
        final Page currentPage = resourceResolver.resolve(pagePath).adaptTo(Page.class);
        final CrossReferenceModel crossReferenceModel = request.adaptTo(CrossReferenceModel.class);
        final ArrayList<String> returnFacet = crossReferenceModel.getReturnFacet();
        final String keyword = request.getParameter(EndecaConstants.SEARCH_STRING);
        final String sortBy = request.getParameter(SORT_BY);
        final List<String> activeFacets = request.getParameter(EndecaConstants.FACETS_STRING) != null ? Arrays.asList(request.getParameter(EndecaConstants.FACETS_STRING).split(CommonConstants.COMMA)) : new ArrayList<>();
        final int pageSize = request.adaptTo(EatonSiteConfigModel.class).getSiteConfig().getPageSize();
        String skuPageName = StringUtils.EMPTY;
        if (null != configService) {
            skuPageName = configService.getConfigServiceBean().getSkupagename();
        }
        final String baseSKUPath = CommonUtil.getSKUPagePath(currentPage, skuPageName);
        String startingRecord;
        try {
            startingRecord = Integer.toString(Integer.parseInt(request.getParameter(PAGE) != null ? request.getParameter(PAGE) : DEFAULT_STARTING_RECORD) * pageSize);
        } catch (NumberFormatException e) {
            startingRecord = DEFAULT_STARTING_RECORD;
        }
        final EndecaServiceRequestBean endecaServiceRequest = endecaRequestService
                .getCrossReferenceEndecaRequestBean(currentPage, activeFacets, keyword,
                        startingRecord, Integer.toString(pageSize), sortBy, returnFacet);
        final JsonObject crossReferenceResponse = endecaService.getCrossReferenceResponse(endecaServiceRequest, request, currentPage);
        try {
            crossReferenceResponse.add(BASE_SKU_PATH, new Gson().toJsonTree(baseSKUPath));
            if (request.getRequestPathInfo().getExtension().equals(CommonConstants.EXTENSION_JSON)) {
                response.setContentType(CommonConstants.APPLICATION_JSON);
                response.setCharacterEncoding(CommonConstants.UTF_8);
                response.getWriter().print(crossReferenceResponse.toString());
                response.getWriter().flush();
            }
        } catch (Exception e) {
            LOGGER.error("Exception while calling CrossReferenceSearchServlet.", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{ \"error\": \"Error calling cross reference search service.\" }");
        }

        LOGGER.debug("CrossReferenceSearchServlet :: doGet() :: Ended");
    }
}
