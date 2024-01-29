package com.eaton.platform.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.EndecaFacetTag;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/tagAttributesFacetValuesDropdown",
        })
public class TagAttributesFacetValuesDropdownServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TagAttributesFacetValuesDropdownServlet.class);

    @Reference
    private EndecaRequestService endecaRequestService;

    @Reference
    private transient EndecaService endecaService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.info("******** TagAttributesFacetValuesDropdownServlet execution begin ***********");

        final ResourceResolver resourceResolver = request.getResourceResolver();
        final PrintWriter responseWriter = response.getWriter();
        String page = request.getParameter(CommonConstants.PAGE_LINK);
              if(null != page && page.contains(CommonConstants.HTML_EXTN) && page.contains("/editor.html")){
                  page = page.replace("/editor.html",StringUtils.EMPTY);
                  page =  page.replace(CommonConstants.HTML_EXTN,StringUtils.EMPTY);
              }
        Page cwcPage = resourceResolver.resolve(page).adaptTo(Page.class);
        JSONObject json = new JSONObject();
        String[] selectors = request.getRequestPathInfo().getSelectors();
        final EndecaServiceRequestBean endecaRequestBean =
                endecaRequestService.getEndecaRequestBean(cwcPage, selectors, StringUtils.EMPTY);
        final FamilyListResponseBean catWithCardList = endecaService.getProductFamilyList(endecaRequestBean);
        final List<FacetGroupBean> facetGroupList = catWithCardList.getResponse().getFacets().getFacetGroupList();
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        facetGroupList.forEach(facetGroupBean -> {
            JSONArray jsonArray = new JSONArray();
            if (null != tagManager) {
                final EndecaFacetTag endecaFacetTag = tagManager.resolve(EndecaFacetTag.convertId(facetGroupBean.getFacetGroupId())).adaptTo(EndecaFacetTag.class);
                if (null != endecaFacetTag) {
                    facetGroupBean.setFacetGroupLabel(endecaFacetTag.getLocalizedFacetLabel(endecaRequestBean.getLocale()));
                }
            }
            final List<FacetValueBean> facetValueList = facetGroupBean.getFacetValueList();
            String facetGroupName = facetGroupBean.getFacetGroupLabel();
            facetValueList.forEach(facetValueBean -> {
                String facetVal = facetValueBean.getFacetValueLabel();
                if (StringUtils.isNotEmpty(facetGroupName) && StringUtils.isNotEmpty(facetVal)) {
                    jsonArray.add(facetVal);
                }
            });
            json.put(facetGroupName,jsonArray);
            // set fallback
            request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
        });
        responseWriter.write(json.toString());
    }
}

