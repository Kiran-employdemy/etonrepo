package com.eaton.platform.integration.bullseye.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.bullseye.constants.BullseyeConstant;
import com.eaton.platform.integration.bullseye.models.CategoryGroup;
import com.eaton.platform.integration.bullseye.models.CategoryGroupFilters;
import com.eaton.platform.integration.bullseye.services.BullseyeService;
import com.eaton.platform.integration.pim.util.PIMUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.codehaus.jackson.map.ObjectMapper;
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
import java.util.*;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/bullseye/bullsEyeAllCategories",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class BullseyeAllCategoriesServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 7786330212310203900L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BullseyeAllCategoriesServlet.class);

    private static final String PARENT_CATEGORY_ID= "ParentCategoryId";
    private static final String KEY_NAME= "Name";
    private static final String KEY_ID= "ID";
    private Map<String,List<String>> categoryGroupMap = new HashMap<>();

    @Reference
    private BullseyeService bullseyeService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug("BullseyeCategoryGroupServlet :: doGet() :: Started");
        final String respTypeSelector = request.getRequestPathInfo().getSelectorString();
        String responseFormat = request.getParameter("responseFormat");
        try {
            final ResourceResolver resourceResolver = request.getResourceResolver();
            final String refererURL = CommonUtil.getRefererURL(request);
            final String resourcePath = CommonUtil.getContentPath(resourceResolver, refererURL);
            final Resource currentPageRes = resourceResolver.resolve(resourcePath);
            final JSONObject bullEyeCategoryResponse;

            if(StringUtils.isNotEmpty(responseFormat)) {
                bullEyeCategoryResponse = bullseyeService.getCategoryGroup(currentPageRes, false);
            }else{
                bullEyeCategoryResponse = bullseyeService.getCategoryGroup(currentPageRes, true);
            }
            final HashMap<String, String> dropDownMap = new HashMap<>();
            ObjectMapper Obj = new ObjectMapper();
            CategoryGroupFilters categoryGroupFilters = new CategoryGroupFilters();
            List<CategoryGroup> categoryGroupList = new ArrayList<>();
            categoryGroupFilters.setCategoryGroups(categoryGroupList);

            if ((null != bullEyeCategoryResponse) && (!StringUtils.isNotEmpty(responseFormat))) {
                final JSONArray categoryArray = bullEyeCategoryResponse.getJSONArray(BullseyeConstant.RESPONSE);
                for (int count = 0; count <categoryArray.length(); count++) {
                    final JSONObject categoryJSON = categoryArray.getJSONObject(count);

                    if ((categoryJSON.has(BullseyeConstant.CATEGORY_NAME) && StringUtils.isNotEmpty(categoryJSON.getString(BullseyeConstant.CATEGORY_NAME))) && categoryJSON.has(BullseyeConstant.CATEGORY_ID)) {
                        dropDownMap.put(categoryJSON.getString(BullseyeConstant.CATEGORY_NAME),
                                    String.valueOf(categoryJSON.getInt(BullseyeConstant.CATEGORY_ID)));
                    }
                }
            } else if ((null != bullEyeCategoryResponse) && (StringUtils.isNotEmpty(responseFormat))) {
                processResponseForDialogDropDowns(bullEyeCategoryResponse,currentPageRes);
            }

            if (!dropDownMap.isEmpty() && !BullseyeConstant.BULLSEYE_CONFIG_PAGE.equalsIgnoreCase(respTypeSelector)) {
                final List<Resource> dropdownList = PIMUtil.prepareDropDownList(resourceResolver, dropDownMap);
                final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
                request.setAttribute(DataSource.class.getName(), dataSource);
            } else if (StringUtils.isNotEmpty(responseFormat)) {
                for (Map.Entry<String,List<String>> categoryGroupEntry : categoryGroupMap.entrySet()){
                    CategoryGroup categoryGroup = new CategoryGroup();
                    categoryGroup.setCategoryGroup(categoryGroupEntry.getKey());
                    categoryGroup.setCategories(categoryGroupEntry.getValue());
                    categoryGroupFilters.getCategoryGroups().add(categoryGroup);
                }
                response.setContentType(CommonConstants.APPLICATION_JSON);
                response.getWriter().print(Obj.writeValueAsString(categoryGroupFilters));
                response.getWriter().flush();
            } else {
                response.setContentType(CommonConstants.APPLICATION_JSON);
                response.getWriter().print(bullseyeService.getCategoryDropdownRes(dropDownMap));
                response.getWriter().flush();
            }

        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("Exception in BullseyeCategoryGroupServlet : {}", exception.getMessage());
        }

        LOGGER.debug("BullseyeCategoryGroupServlet :: doGet() :: Ended");
    }

    public void processResponseForDialogDropDowns(JSONObject bullEyeCategoryResponse, Resource currentPageRes) throws Exception {

        LOGGER.debug("BullseyeCategoryGroupServlet :: processResponseForDialogDropDowns() :: Started");
        final JSONArray categoryGroupArray = bullEyeCategoryResponse.getJSONArray(BullseyeConstant.RESPONSE);
        for (int count = 0; count < categoryGroupArray.length(); count++) {
            final JSONObject categoryGroupJSON = categoryGroupArray.getJSONObject(count);
            if ((categoryGroupJSON.has(KEY_NAME)) && (!categoryGroupJSON.isNull(KEY_NAME)) &&
                    (categoryGroupJSON.has(KEY_ID)) && (!categoryGroupJSON.isNull(KEY_ID))
                    && (!categoryGroupMap.containsKey(categoryGroupJSON.getString(KEY_NAME)))) {
                List<String> categoryList = new ArrayList<>();
                String categoryGroupKey = StringUtils.EMPTY;
                final JSONObject categoryArrayResponse = bullseyeService.getCategory(currentPageRes, "" + categoryGroupJSON.getInt("ID"), Locale.US);
                if (categoryArrayResponse.has(BullseyeConstant.RESPONSE)) {
                    final JSONArray categoryArray = categoryArrayResponse.getJSONArray(BullseyeConstant.RESPONSE);
                    for (int j = 0; j < categoryArray.length(); j++) {
                        final JSONObject categoryJSON = categoryArray.getJSONObject(j);
                        if ((categoryJSON.has(PARENT_CATEGORY_ID)) && (categoryJSON.isNull(PARENT_CATEGORY_ID))) {
                            categoryGroupKey = categoryGroupJSON.getString(KEY_NAME) + "::" + categoryGroupJSON.getInt(KEY_ID) + "::" + categoryJSON.getInt(BullseyeConstant.CATEGORY_ID);
                            categoryGroupMap.put(categoryGroupKey, categoryList);
                        } else if ((categoryJSON.has(PARENT_CATEGORY_ID)) && (!categoryJSON.isNull(PARENT_CATEGORY_ID))) {
                            categoryList = categoryGroupMap.get(categoryGroupKey);
                            categoryList.add(categoryJSON.getString(BullseyeConstant.CATEGORY_NAME) + "::" + categoryJSON.getInt(BullseyeConstant.CATEGORY_ID));
                        }
                    }
                }
            }
        }
        LOGGER.debug("BullseyeCategoryGroupServlet :: processResponseForDialogDropDowns() :: Ended");
    }
}
