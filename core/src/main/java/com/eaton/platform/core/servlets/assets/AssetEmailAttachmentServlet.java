package com.eaton.platform.core.servlets.assets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.email.EmailService;
import com.eaton.platform.core.constants.AssetDownloadConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AssetEmailAttachmentServlet responds with a files including the assets
 * listed and emailId in the provided json as payload in the POST request.
 * 
 * @Param a Json with the list of assets and emailId in the body of the request
 */
@Component(service = Servlet.class, immediate = true, property = {
        Constants.SERVICE_DESCRIPTION + "= Asset Zip Email",
        ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/bulkassetemail",
        ServletConstants.SLING_SERVLET_EXTENSION_JSON,
        ServletConstants.SLING_SERVLET_METHODS_POST
})
public class AssetEmailAttachmentServlet extends SlingAllMethodsServlet {

    private static final String DATA_VALUE_STRING = "dataValue";

    private static final String EMAIL_TO_STRING = "emailTo";

    private static final String DOMAIN_STRING = "domain";

    private static final String DOC_LINKS_STRING = "documentLinks";

    private static final String LI_TAG_INITIALS = "<li><a href=\"";

    private static final String ANCHOR_TAG_ENDING = "\">";

    private static final String LI_TAG_ENDING = "</a></li>";

    private static final String LOGO_IMG = "<img src=\"https://www.eaton.com/content/dam/eaton/global/logos/eaton-logo-small.png\" /><br/>";

    private static final String BAR_IMG = "<img src=\"https://www.eaton.com/content/dam/eaton/eaton-own-or-royalty-free-purchased-images/resources-bar.jpg\" /><br/>";

    private static final Logger LOG = LoggerFactory.getLogger(AssetEmailAttachmentServlet.class);

    private static final String NO_REPLY = "noReply";

    private static final String NO_REPLY_I18N_KEY = "bdeNoReply";

    private Map<String, String> emailParams = new HashMap<>();

    @Reference
    transient EmailService emailService;

    /**
     * Sends an email message with a human-readable summary of the requested documents' titles and links.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws ServletException, IOException {
        LOG.debug("Started AssetEmailAttachmentServlet.doPost");
        response.setContentType(AssetDownloadConstants.APPLICATION_JSON);
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(CommonConstants.NEWLINE_CHARACTER);
                }
            } finally {
                reader.close();
            }
            String requestData = sb.toString();
            LOG.debug("AssetEmailAttachmentServlet JSON Payload : {}", requestData);
            JSONObject json = new JSONObject(requestData);
            JSONArray dataValueArray = json.getJSONArray(DATA_VALUE_STRING);

            String noReplyMsg = getI18nItems(request, "noReplyMsg");
            String subject = getI18nItems(request, "subject");
            String emailBody = buildEmailBodyHtml(request, dataValueArray);
            String recipient = extractRecipient(dataValueArray);

            if (!recipient.equals(CommonConstants.EMPTY_STR)) {
                sendEmail(recipient, subject, emailBody, noReplyMsg);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        catch (JSONException e){
            LOG.error(" JSON Exception {} ::", e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            LOG.error("Error while processing AssetEmailAttachmentServlet : {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        LOG.debug("Ended AssetEmailAttachmentServlet.doPost");
    }

    /**
     * Fetches i18n equivalents of "Resources", "from", and no-reply message.
     *
     * @param request SlingHttpServletRequest
     * @param label String i18n key
     * @return i18n string i18n value
     */
    public static String getI18nItems(SlingHttpServletRequest request, String label) {
        final String refererURL = CommonUtil.getRefererURL(request);
        String resourcePath = CommonUtil.getContentPath(request.getResourceResolver(), refererURL);
        Resource currentPageRes = request.getResourceResolver().resolve(resourcePath);
        Page currentPage = currentPageRes.adaptTo(Page.class);

        final String RESOURCES_I18N = CommonUtil.getI18NFromResourceBundle(request, currentPage, CommonConstants.RESOURCES_TAB, CommonConstants.RESOURCES_TAB);
        final String FROM_I18N = CommonUtil.getI18NFromResourceBundle(request, currentPage, CommonConstants.FROM, CommonConstants.FROM);

        if("subject".equals(label)) {
            return RESOURCES_I18N + " " + FROM_I18N + " " + CommonConstants.TEAM_NAME;
        }
        else if("noReplyMsg".equals(label)) {
            return CommonUtil.getI18NFromResourceBundle(request, currentPage, NO_REPLY_I18N_KEY, CommonConstants.NO_REPLY);
        }
        return "";
    }

    private static String extractRecipient(JSONArray dataValueArray) throws JSONException {
        if (dataValueArray.length() > 0) {
            JSONObject dataValue = dataValueArray.getJSONObject(0);
            if (dataValue.has(EMAIL_TO_STRING)) {
                return dataValue.getString(EMAIL_TO_STRING);
            }
        }
        return CommonConstants.EMPTY_STR;
    }

    /**
     * Create the body of the email to send to the recipient.
     *
     * @param request  servlet request
     * @param dataValueArray  data sent in the request body
     * @return emailBody
     * @throws JSONException JSONException
     */
    public String buildEmailBodyHtml(SlingHttpServletRequest request, JSONArray dataValueArray) throws JSONException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        String emailBody = LOGO_IMG + BAR_IMG + "<h1>" + getI18nItems(request, "subject") + "</h1>";
        //build the links
        String links = CommonConstants.EMPTY_STR;
        List<String> listOfLinks = new ArrayList<>();
        if (dataValueArray.length() > 0) {
            JSONObject dataValue = dataValueArray.getJSONObject(0);
            if (dataValue.has(DOC_LINKS_STRING) && dataValue.has(DOMAIN_STRING)) {
                String domainName = dataValue.getString(DOMAIN_STRING);
                JSONArray documentLinksArray = dataValue.getJSONArray(DOC_LINKS_STRING);
                for (int i = 0; i < documentLinksArray.length(); i++) {
                    String resourcePath = documentLinksArray.getString(i);
                    StringBuilder linkBuilder = new StringBuilder();
                    linkBuilder.append(LI_TAG_INITIALS)
                            .append(constructAssetLink(domainName, resourcePath))
                            .append(ANCHOR_TAG_ENDING)
                            .append(constructAssetTitle(domainName, resourcePath, resourceResolver))
                            .append(LI_TAG_ENDING);
                    listOfLinks.add(linkBuilder.toString());
                }
                links = String.join(CommonConstants.SPACE_STRING, listOfLinks);
            }
        }
        emailBody += links;
        return emailBody;
    }

    private static String constructAssetLink(String domainName, String resourcePath) {
        if(!resourcePath.contains("http://") && !resourcePath.contains("https://")) {
            return domainName + resourcePath;
        } else {
            return resourcePath;
        }
    }

    private static String constructAssetTitle(String domainName, String resourcePath, ResourceResolver resourceResolver) {
        String assetTitle = CommonUtil.getAssetTitle(resourcePath, resourceResolver.getResource(resourcePath));
        if(!"".equals(assetTitle)) {
            return assetTitle;
        } else {
            return constructAssetLink(domainName, resourcePath);
        }
    }

    private void sendEmail(String recepient, String subject, String emailBody, String noReplyMsg){
        LOG.debug("Started AssetEmailAttachmentServlet.sendEmail");
        emailParams.put(CommonConstants.STRING_TO, recepient);
        emailParams.put(CommonConstants.STRING_SUBJECT, subject);
        emailParams.put(CommonConstants.LIST_OF_LINKS, emailBody);
        emailParams.put(NO_REPLY, noReplyMsg);
        emailService.sendEmail(CommonConstants.ASSET_EMAIL_TEMPLATE_PATH, emailParams, recepient);
        LOG.debug("Ended AssetEmailAttachmentServlet.sendEmail");
    }
}
