package com.eaton.platform.integration.bullseye.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.bullseye.constants.BullseyeConstant;
import com.eaton.platform.integration.bullseye.services.BullseyeService;
import com.eaton.platform.integration.pim.util.PIMUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.List;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/bullseye/bullsEyeServiceValidation",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class BullseyeCategoryGroupServlet extends SlingAllMethodsServlet {
    private static final long serialVersionUID = 7786330212310203900L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BullseyeCategoryGroupServlet.class);

    @Reference
    private BullseyeService bullseyeService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException {
        LOGGER.debug("BullseyeCategoryServlet :: doGet() :: Started");
        final String respTypeSelector = request.getRequestPathInfo().getSelectorString();
        try {
            final ResourceResolver resourceResolver = request.getResourceResolver();
            final String refererURL = CommonUtil.getRefererURL(request);
            final String resourcePath = CommonUtil.getContentPath(resourceResolver, refererURL);
            final Resource currentPageRes = resourceResolver.resolve(resourcePath);
            final JSONObject bullEyeCategoryResponse = bullseyeService.getCategoryGroup(currentPageRes, false);
            final HashMap<String, String> dropDownMap = new HashMap<>();
            if (null != bullEyeCategoryResponse) {
                final JSONArray categoryArray = bullEyeCategoryResponse.getJSONArray(BullseyeConstant.RESPONSE);
                for (int count = 0; count <categoryArray.length(); count++) {
                    final JSONObject categoryJSON = categoryArray.getJSONObject(count);
                    if ((categoryJSON.has(BullseyeConstant.NAME) && StringUtils.isNotEmpty(categoryJSON.getString(BullseyeConstant.NAME))) && categoryJSON.has(BullseyeConstant.ID)) {
                        dropDownMap.put(categoryJSON.getString(BullseyeConstant.NAME),
                                String.valueOf(categoryJSON.getInt(BullseyeConstant.ID)));
                    }
                }
            }

            if (!dropDownMap.isEmpty() && !BullseyeConstant.BULLSEYE_CONFIG_PAGE.equalsIgnoreCase(respTypeSelector)) {
                final List<Resource> dropdownList = PIMUtil.prepareDropDownList(resourceResolver, dropDownMap);
                final DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
                request.setAttribute(DataSource.class.getName(), dataSource);
            } else {
                response.setContentType(CommonConstants.APPLICATION_JSON);
                response.getWriter().print(bullseyeService.getCategoryDropdownRes(dropDownMap));
                response.getWriter().flush();
            }

        } catch (IOException exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOGGER.error("Exception in BullseyeCategoryServlet :" + exception.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        LOGGER.debug("BullseyeCategoryServlet :: doGet() :: Ended");
    }
}
