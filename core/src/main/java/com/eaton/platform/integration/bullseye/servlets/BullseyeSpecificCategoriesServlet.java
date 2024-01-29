package com.eaton.platform.integration.bullseye.servlets;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.bullseye.constants.BullseyeConstant;
import com.eaton.platform.integration.bullseye.services.BullseyeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/bullseye/bullsEyeSpecificCategories",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class BullseyeSpecificCategoriesServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 7786330212310203900L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BullseyeSpecificCategoriesServlet.class);

    @Reference
    private BullseyeService bullseyeService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug("BullseyeSpecificCategoriesServlet :: doGet() :: Started");
        final String categoryGroupId = request.getParameter("groupId");
        try {
            final ResourceResolver resourceResolver = request.getResourceResolver();//this might be the problem
            final String refererURL = CommonUtil.getRefererURL(request);
            final String resourcePath = CommonUtil.getContentPath(resourceResolver, refererURL);
            final Resource currentPageRes = resourceResolver.resolve(resourcePath);

            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page currentPage = pageManager.getPage(resourcePath);
            final Locale language = CommonUtil.getLocaleFromPagePath(currentPage);

            final JSONObject bullEyeCategoryResponse = bullseyeService.getCategory(currentPageRes, categoryGroupId, language);
            final HashMap<String, String> dropDownMap = new HashMap<>();
            if (null != bullEyeCategoryResponse) {
                final JSONArray categoryArray = bullEyeCategoryResponse.getJSONArray(BullseyeConstant.RESPONSE);
                for (int count = 1; null != categoryArray && count < categoryArray.length(); count++) {
                    final JSONObject categoryJSON = categoryArray.getJSONObject(count);
                    if ((categoryJSON.has(BullseyeConstant.CATEGORY_NAME)
                            && StringUtils.isNotEmpty(categoryJSON.getString(BullseyeConstant.CATEGORY_NAME)))
                            && categoryJSON.has(BullseyeConstant.CATEGORY_ID)) {
                        dropDownMap.put(categoryJSON.getString(BullseyeConstant.CATEGORY_NAME),
                                String.valueOf(categoryJSON.getInt(BullseyeConstant.CATEGORY_ID)));
                    }
                }
            }
            if(!dropDownMap.isEmpty()) {
                response.setContentType(CommonConstants.APPLICATION_JSON);
                response.getWriter().print(bullseyeService.getCategoryDropdownRes(dropDownMap));
                response.getWriter().flush();
            }

        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("Exception in BullseyeSpecificCategoriesServlet : {}", exception.getMessage());
        }

        LOGGER.debug("BullseyeSpecificCategoriesServlet :: doGet() :: Ended");
    }
}
