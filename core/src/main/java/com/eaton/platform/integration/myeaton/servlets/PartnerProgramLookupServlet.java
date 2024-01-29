package com.eaton.platform.integration.myeaton.servlets;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.eclipse.jetty.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Partner program lookup servlet
 */
@Component(
        service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/secure/partner-program-table",
                ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })

public class PartnerProgramLookupServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(PartnerProgramLookupServlet.class);

    @Reference
    private transient AuthorizationService authorizationService;


    @Override
    public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.trace("doGet : START");

        response.setContentType(CommonConstants.APPLICATION_JSON);

        Page currentPage = getCurrentPage(request, response);

        try (PrintWriter out = response.getWriter()) {

            AuthenticationToken authenticationToken = authorizationService.getTokenFromSlingRequest(request);

            if (null != authenticationToken) {

                LOG.debug("Authorization token found in sling request.");

                UserProfile userProfile = authenticationToken.getUserProfile();

                if (null != userProfile) {

                    LOG.debug("User profile found from authentication token.");

                    String uid = userProfile.getUid();
                    LOG.debug("Getting partner program data for {}", uid);

                    List<String> data = userProfile.getPartnerProgramTypeAndTierLevels();
                    LOG.debug("Partner program data for {} is {}", uid, data);

                    if (data.isEmpty()) {

                        LOG.debug("User ({}) has no partner program data. Sending empty response.", uid);

                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "User has no partner program data.");
                        out.write("");

                    } else {

                        LOG.debug("User ({}) has partner program data. Translating the data.", uid);
                        JSONArray translatedData = translateData(request, currentPage, data);
                        LOG.debug("Translated partner program data for {}: {}", uid, translatedData);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("data", translatedData);
                        String jsonObjectString = jsonObject.toString();

                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write(jsonObjectString);

                    }

                } else {
                    LOG.error("Cannot find user profile from authentication token.");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Cannot find user profile from authentication token.");
                }


            } else {
                LOG.error("{}: Authorization token not found in sling request.", HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization token not found in sling request.");
            }
        } catch(IOException e) {
            LOG.error("Error while writing partner program data to PrintWriter.", e);
        } catch (JSONException e) {
            LOG.error("Error while creating json object.", e);
        }

        LOG.trace("doGet : END");
    }

    /**
     * Get current page resource
     * @param request - sling servlet request
     * @param response - sling servlet response
     * @return current page resource
     * @throws IOException when adminService, refererURL, or resourcePath are not found
     */
    public Page getCurrentPage(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        LOG.trace("getCurrentPage : START");

        String refererURL = request.getHeader(CommonConstants.REFERER_URL);
        LOG.debug("Referer url: {}", refererURL);

        // referer url gets page url servlet comes from. this is used in i18n translation
        if (StringUtil.isNotBlank(refererURL)) {

            ResourceResolver resourceResolver = request.getResourceResolver();

            String resourcePath = CommonUtil.getContentPath(resourceResolver, refererURL);
            LOG.debug("Resource content path: {}", resourcePath);

            if (StringUtils.isNotBlank(resourcePath)) {

                Resource currentPageRes = resourceResolver.getResource(resourcePath);
                assert currentPageRes != null;
                return currentPageRes.adaptTo(Page.class);

            } else {
                LOG.error("Resource content path is blank for referer url of {}.", refererURL);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Resource content path is blank for page.");
            }

        } else {
            LOG.error("Referer url from sling request is blank.");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Referer url from sling request is blank.");
        }

        LOG.trace("getCurrentPage : END");
        return null;
    }

    /**
     * Translate the user's partner program data
     * This method will get the i18n value for each program name and tier level
     * splitting each with pipe symbol then translating
     * @param request - sling servlet request
     * @param currentPage - current page resource
     * @param data - data to translate (expected to be in
     * @return partner program data translated
     */
    public JSONArray translateData(SlingHttpServletRequest request, Page currentPage, List<String> data) {
        LOG.trace("translateData : START");

        JSONArray translatedData = new JSONArray();

        for (String item : data) {

            // when data item has a program type and tier level, a pipe separates the two
            // this will split the two and translate each then put back together
            // if there's no pipe, the data item is only program type and is translated and set back to the item
            if (item.contains(CommonConstants.PIPE)) {
                LOG.debug("Item contains delimiter: {}", item);

                String translatedWords = "";

                String[] words = item.split(CommonConstants.ESCAPED_PIPE);
                for (String word : words) {

                    String wordToAdd = CommonUtil.getI18NFromResourceBundle(request, currentPage, word);
                    LOG.debug("Translated words for item before adding {}. : {}", wordToAdd, translatedWords);

                    translatedWords = StringUtils.join(translatedWords, CommonConstants.PIPE, wordToAdd);
                    LOG.debug("Translated words for item after adding {}. : {}", wordToAdd, translatedWords);
                }

                if (StringUtils.isNotBlank(translatedWords)) {

                    String translatedWordsWithoutFirstDelimiter = translatedWords.substring(1);
                    LOG.debug("Adding final translated words string for item to new json array: {}", translatedWordsWithoutFirstDelimiter);

                    LOG.debug("Translated data before adding {} to it: {}", translatedWordsWithoutFirstDelimiter, translatedData);
                    translatedData.put(translatedWordsWithoutFirstDelimiter);
                    LOG.debug("Translated data after adding {} to it: {}", translatedWordsWithoutFirstDelimiter, translatedData);

                }


            } else {

                String wordToAdd = CommonUtil.getI18NFromResourceBundle(request, currentPage, item);
                LOG.debug("Translated data before adding {} to it: {}", wordToAdd, translatedData);
                translatedData.put(wordToAdd);
                LOG.debug("Translated data after adding {} to it: {}", wordToAdd, translatedData);

            }
        }

        LOG.trace("translateData : END");
        return translatedData;
    }

}
