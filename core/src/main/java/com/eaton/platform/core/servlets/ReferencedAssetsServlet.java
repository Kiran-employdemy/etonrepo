package com.eaton.platform.core.servlets;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.sling.api.resource.ResourceResolver;
import com.eaton.platform.core.services.PageAssetReferenceService;

/**
 * This servlet returns informations about all assets used on the page as JSON.
 * <p>
 * It binds with the selector "assetreferences" and the extension "json" and can be used only for all
 * pages (jcr:primaryType = cq:Page).
 * In case of problems or errors an empty JSON is returned.
 * <p>
 * you can use it like this: http://localhost:4502/content/geometrixx/en/products.assetreferences.json
 */
@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=GET",
                "sling.servlet.resourceTypes=cq/Page",
                "sling.servlet.selectors=assetreferences",
                "sling.servlet.extensions=json",
                "service.ranking=1000"
        }
)
public class ReferencedAssetsServlet extends SlingSafeMethodsServlet {
    // Generated serial version UID
    private static final long serialVersionUID = 8446564170082865006L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferencedAssetsServlet.class);
    private static final String EMPTY_JSON = "{}";
    private static final String DOWNLOAD_LIST_TYPE = "eaton/components/general/download-link-list";
    private static final String RESOURCE_LIST_TYPE = "eaton/components/product/resource-list";
    /**
     * This is a PageAssetReferenceService instance.
     */
    @Reference
    public transient PageAssetReferenceService pageAssetReferenceService;
    transient List<AssetDetails> pageAssetList = new LinkedList<>();
    transient List<AssetDetails> tagAssetList = new LinkedList<>();
    transient List<AssetDetails> resourceListTagAsset = new LinkedList<>();
    /**
     * This is a doGet method
     *
     * @param response the parameter of the class
     * @param request  the parameter of the class
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        LOGGER.info("ReferencedAssetsServlet :: doGet() :: Started");
        ArrayList<AssetDetails> allAssetList = new ArrayList<>();
        ResourceResolver resourceResolver = request.getResourceResolver();
        response.setContentType(CommonConstants.APPLICATION_JSON);
        response.setCharacterEncoding(CommonConstants.UTF_8);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            // Get the current node reference from the resource object
            Node currentNode = request.getResource().adaptTo(Node.class);
            if (currentNode == null) {
                // Every adaptTo() can return null, so let's handle the case here
                // However, it is very unlikely
                LOGGER.error("Cannot adapt resource {} to a node", request.getResource().getPath());
                response.getWriter().write(EMPTY_JSON);
                return;
            }
            pageAssetList = pageAssetReferenceService.fetchAllPageAsset(currentNode, request);
            tagAssetList = pageAssetReferenceService.fetchAllTagedAssets(request, resourceResolver,DOWNLOAD_LIST_TYPE);
            resourceListTagAsset = pageAssetReferenceService.fetchAllTagedAssets(request, resourceResolver,RESOURCE_LIST_TYPE);
            allAssetList.addAll(pageAssetList);
            allAssetList.addAll(tagAssetList);
            allAssetList.addAll(resourceListTagAsset);
            String jsonOutput = gson.toJson(allAssetList);
            response.getWriter().write(jsonOutput);
        } catch (Exception e) {
            LOGGER.error("Error while processing ReferencedAssetsServlet : {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        LOGGER.info("ReferencedAssetsServlet :: doGet() :: ENDED");
    }
}

