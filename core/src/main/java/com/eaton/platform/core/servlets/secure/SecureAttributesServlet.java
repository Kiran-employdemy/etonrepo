package com.eaton.platform.core.servlets.secure;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.secure.SecureAttributesModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.constants.SecureConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/authoring/secure/attribute-tags",
                ServletConstants.SLING_SERVLET_SELECTORS + "{page,asset}",
                ServletConstants.SLING_SERVLET_EXTENSIONS + ServletConstants.SLING_SERVLET_EXTENSION_JSON
        })
public class SecureAttributesServlet extends SlingSafeMethodsServlet{

    private static final Logger LOG = LoggerFactory.getLogger(SecureAttributesServlet.class);

    /** The admin service. */
    @Reference
    private transient AdminService adminService;


    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {
        LOG.debug("******** SecureAttributesServlet execution started ***********");
        if(adminService != null) {
            try(ResourceResolver secureResourceResolver = adminService.getReadService()) {
                JsonObject secureJsonObject = new JsonObject();
                final String configuredPath = req.getParameter(SecureConstants.REQUEST_PARAM_CONFIG_URL) != null ? req.getParameter(SecureConstants.REQUEST_PARAM_CONFIG_URL) : StringUtils.EMPTY;
                if (null != configuredPath && !configuredPath.isEmpty()) {
                    secureJsonObject = prepareSecureJsonResponse(req, secureResourceResolver, configuredPath);
                }
                resp.setContentType(CommonConstants.APPLICATION_JSON);
                resp.getWriter().print(secureJsonObject);
                LOG.debug("******** Final response ****** {}", secureJsonObject);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        LOG.debug("******** SecureAttributesServlet execution completed ***********");
    }

    private static JsonObject prepareSecureJsonResponse(final SlingHttpServletRequest req, final ResourceResolver secureResourceResolver,final String configuredPath){
        Resource secureRes;
        JsonObject secureResponseJsonObject = new JsonObject();
        if(req.getRequestPathInfo().getSelectors()[0].length() > 0
                && req.getRequestPathInfo().getSelectors()[0].equals(DamConstants.ACTIVITY_TYPE_ASSET)){
            secureRes = secureResourceResolver.getResource(
                    configuredPath + SecureConstants.SLASH + JcrConstants.JCR_CONTENT + SecureConstants.SLASH + DamConstants.METADATA_FOLDER);
        }else {
            secureRes = secureResourceResolver.getResource(
                    configuredPath + SecureConstants.SLASH + JcrConstants.JCR_CONTENT);
        }
        if (secureRes != null) {
            final SecureAttributesModel secureAttributesModel = secureRes.adaptTo(SecureAttributesModel.class);
            if (secureAttributesModel != null) {
                secureResponseJsonObject.add(SecureConstants.PROP_ACCOUNT_TYPE_TAG, new Gson().toJsonTree(SecureUtil.getSecureTagNames(secureAttributesModel.getAccountType(),
                        secureResourceResolver, true)));
                secureResponseJsonObject.add(SecureConstants.PROP_PRODUCT_CATEGORIES_TAG, new Gson().toJsonTree(SecureUtil.getSecureTagNames(secureAttributesModel.getProductCategories(),
                        secureResourceResolver, true)));
                secureResponseJsonObject.add(SecureConstants.PROP_APPLICATION_ACCESS_TAG, new Gson().toJsonTree(SecureUtil.getSecureTagNames(secureAttributesModel.getApplicationAccess(),
                        secureResourceResolver, true)));
                secureResponseJsonObject.add(CommonConstants.COUNTRIES_FIELD, new Gson().toJsonTree(SecureUtil.getSecureTagNames(secureAttributesModel.getCountries(), secureResourceResolver, true)));
                secureResponseJsonObject.add(SecureConstants.ROLE_MAPPER_PROGRAM_TYPE_AND_TIERS_KEY
                        , new Gson().toJsonTree(SecureUtil.getSecureTagNames(secureAttributesModel.getPartnerProgramAndTierLevel(),
                        secureResourceResolver, true)));
            }
        }
        return secureResponseJsonObject;
    }

}
