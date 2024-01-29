package com.eaton.platform.core.servlets;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.Tag;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.services.ReferenceService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_GET,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/referenceList",
        })
public class ReferenceCheckServlet extends SlingAllMethodsServlet {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceCheckServlet.class);
    private static final String REQUEST_PARAM_PATH = "path";
    private static final String ZERO_VALUE = "0";
    private static final String DELETE_REFERENCE = "deleteReference";
    private static final String DELETE_PAGE = "deletePage";
    private static final String DELETE_TAG = "deleteTag";
    private static final String DELETE_ASSET = "deleteAsset";
    private static final String ACTION_TYPE = "actionType";

    @Reference
    private ReferenceService referenceService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
            throws IOException {
        response.setHeader(CommonConstants.CACHE_CONTROL, CommonConstants.NO_CACHE);
        response.setHeader(CommonConstants.PRAGMA, CommonConstants.NO_CACHE);
        response.setHeader(CommonConstants.EXPIRES, ZERO_VALUE);
        response.setContentType(CommonConstants.APPLICATION_JSON);
        response.setCharacterEncoding(CommonConstants.UTF_8);
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final Map parameterMap = request.getParameterMap();

        if (parameterMap.containsKey(REQUEST_PARAM_PATH)) {
            final String[] currentDeletePaths = (String[]) parameterMap.getOrDefault(REQUEST_PARAM_PATH, new String[0]);
            final String action = request.getParameter(ACTION_TYPE);
            final JsonArray deleteReferenceArray = new JsonArray();
            for (String currentDeletePath: currentDeletePaths) {
                final Resource resource = resourceResolver.getResource(currentDeletePath);
                if (null != resource) {
                    currentDeletePath = getTagID(action, resource).isEmpty() ? currentDeletePath : getTagID(action, resource);
                    final JsonArray deletePageReferenceArray = new JsonParser().parse(new Gson().toJson(referenceService.getDeletePageReference(currentDeletePath, resourceResolver))).getAsJsonArray();
                    try {
                        switch (action) {
                            case DELETE_PAGE:
                                final Resource childPageResource = resource.getChild(JcrConstants.JCR_CONTENT);
                                if (null != childPageResource) {
                                    final JsonObject pageReferenceJson = getReferenceJson(childPageResource, JcrConstants.JCR_TITLE,
                                            deletePageReferenceArray);
                                    deleteReferenceArray.add(pageReferenceJson);
                                }
                                break;
                            case DELETE_TAG:
                                final JsonObject tagReferenceJson = getReferenceJson(resource, JcrConstants.JCR_TITLE,
                                        deletePageReferenceArray);
                                deleteReferenceArray.add(tagReferenceJson);
                                break;
                            case DELETE_ASSET:
                                final Resource childAssetResource = resource.getChild(JcrConstants.JCR_CONTENT);
                                if (null != childAssetResource) {
                                    final Asset asset = CommonUtil.getAsset(resource);
                                    JsonObject assetReferenceJson = null;
                                    if (null != asset) {
                                        assetReferenceJson = new JsonObject();
                                        final String title = asset.getName();
                                        assetReferenceJson.add(REQUEST_PARAM_PATH, new Gson().toJsonTree(title));
                                        assetReferenceJson.add(DELETE_REFERENCE, deletePageReferenceArray);
                                    } else {
                                        final String referenceProperty = JcrConstants.JCR_TITLE;
                                        assetReferenceJson = getReferenceJson(childAssetResource, referenceProperty,
                                                deletePageReferenceArray);
                                    }
                                    deleteReferenceArray.add(assetReferenceJson);
                                }
                                break;
                            default: break;
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }

            }
            final PrintWriter writer = response.getWriter();
            writer.print(deleteReferenceArray);
            writer.flush();
        }
        logger.info("Reference array for delete page retrived");
    }

    private JsonObject getReferenceJson(final Resource resource, final String propertyName,
                                        final JsonArray deletePageReferenceArray) throws Exception {
        final JsonObject referenceJson = new JsonObject();
        if (null != resource && null != deletePageReferenceArray) {
            ValueMap valueMap = resource.getValueMap();
            if (valueMap.containsKey(propertyName)) {
                final String title = valueMap.get(propertyName, StringUtils.EMPTY);
                referenceJson.add(REQUEST_PARAM_PATH, new Gson().toJsonTree(title));
                referenceJson.add(DELETE_REFERENCE, deletePageReferenceArray);
            }
        }
        return referenceJson;
    }

    private String getTagID(final String itemType, final Resource resource) {
        String currentDeletePath = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(itemType) && itemType.equals(DELETE_TAG)) {
            final Tag tags = resource.adaptTo(Tag.class);
            if (null != tags && StringUtils.isNotBlank(tags.getLocalTagID())) {
                currentDeletePath = tags.getLocalTagID();
            }
        }
        return currentDeletePath;
    }
}
