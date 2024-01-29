package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * This workflow process relocates the assets within the triage folder to the final location.
 */
@Component(service = WorkflowProcess.class,immediate = true,
    property = {
        AEMConstants.SERVICE_DESCRIPTION + "Eaton - Move Triage Folder Asset",
        AEMConstants.SERVICE_VENDOR_EATON,
        AEMConstants.PROCESS_LABEL + "Eaton - Move Triage Folder Asset"
    })
public class MoveTriageFolderAsset implements WorkflowProcess {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MoveTriageFolderAsset.class);

    /** The admin service. */
    @Reference
    private transient AdminService adminService;

    /** The Constant INVALID_FOLDER_CHARS. */
    private static final String[] INVALID_FOLDER_CHARS = new String[] { "*", "/", ":", "[", "\\", "]", "|", "#", "%", "{", "}", "?", "\"", ".", "^", ";", "+", "&", " ", "\t", "(", ")", "," };

    /** The Constant EATON_NS. */
    private static final String EATON_NS = "eaton:";

    /** The Constant PRODUCT_TAG. */
    private static final String PRODUCT_TAG = "jcr:content/metadata/xmp:eaton-product-taxonomy";

    /** The Constant SERVICES_TAG. */
    private static final String SERVICES_TAG = "jcr:content/metadata/xmp:eaton-services-taxonomy";

    /** The Constant SUPPORT_TAG. */
    private static final String SUPPORT_TAG = "jcr:content/metadata/xmp:eaton-support-taxonomy";

    /** The Constant COMPANY_TAG. */
    private static final String COMPANY_TAG = "jcr:content/metadata/xmp:eaton-company-taxonomy";

    /** The Constant PRODUCT_TAXONOMY. */
    private static final String PRODUCT_TAXONOMY = "product-taxonomy";

    /** The Constant PRODUCTS. */
    private static final String PRODUCTS = "products";

    /** The Constant SUPPORT_TAXONOMY. */
    private static final String SUPPORT_TAXONOMY = "support-taxonomy";

    /** The Constant SUPPORT. */
    private static final String SUPPORT = "support";

    /** The Constant COMPANY_TAXONOMY. */
    private static final String COMPANY_TAXONOMY = "company-taxonomy";

    /** The Constant COMPANY. */
    private static final String COMPANY = "company";

    /** The Constant MULTIPLE_HYPHEN_REGEX. */
    private static final String MULTIPLE_HYPHEN_REGEX = "--+";

    @Override
    public void execute(final WorkItem workItem, final WorkflowSession workflowSession, final MetaDataMap metaDataMap) throws WorkflowException {
        LOG.debug("MoveTriageFolderAsset :: execute()");

        String payloadPath = workItem.getWorkflowData().getPayload().toString();
        try (ResourceResolver adminResourceResolver = adminService.getWriteService()) {
            Session adminSession = adminResourceResolver.adaptTo(Session.class);
            String currentUserID = StringUtils.EMPTY;
            UserManager userManager = adminResourceResolver.adaptTo(UserManager.class);
                Authorizable authorizable;
                    authorizable = userManager.getAuthorizable(workItem.getWorkflow().getInitiator());
                    if (null != userManager.getAuthorizable(authorizable.getID())) {
                        currentUserID = authorizable.getID();
                    }
            if (!adminSession.nodeExists(payloadPath)) {
                LOG.error("Unable to find asset node for {}.", payloadPath);
                return;
            }

            Node assetNode = adminSession.getNode(payloadPath);
            String tagId = null;
            if (assetNode.hasProperty(PRODUCT_TAG) && StringUtils.isNotBlank(getFirstEntryOfTag(PRODUCT_TAG, assetNode))) {
                tagId = getFirstEntryOfTag(PRODUCT_TAG, assetNode);
            } else if (assetNode.hasProperty(SERVICES_TAG) && StringUtils.isNotBlank(getFirstEntryOfTag(SERVICES_TAG, assetNode))) {
                tagId = getFirstEntryOfTag(SERVICES_TAG, assetNode);
            } else if (assetNode.hasProperty(SUPPORT_TAG) && StringUtils.isNotBlank(getFirstEntryOfTag(SUPPORT_TAG, assetNode))) {
                tagId = getFirstEntryOfTag(SUPPORT_TAG, assetNode);
            } else if (assetNode.hasProperty(COMPANY_TAG) && StringUtils.isNotBlank(getFirstEntryOfTag(COMPANY_TAG, assetNode))) {
                tagId = getFirstEntryOfTag(COMPANY_TAG, assetNode);
            }

            if (StringUtils.isNotBlank(tagId)) {
                Resource sourceResource = adminResourceResolver.getResource(assetNode.getPath());
                Node destFolderNode = getOrCreateFinalLocationFolder(adminResourceResolver, tagId);
                if (destFolderNode != null) {
                    String destPath = destFolderNode.getPath() + CommonConstants.SLASH_STRING + sourceResource.getName();
                    if (adminSession.nodeExists(destPath)) {
                        // An asset already exists.
                        // Overwrite pre-existing asset (and delete the triage asset).  This approach should retain the pre-existing asset's Scene7 Name.
                        AssetManager assetManager = adminResourceResolver.adaptTo(AssetManager.class);
                        assetManager.createOrUpdateAsset(destPath, sourceResource.adaptTo(Asset.class).getOriginal().getBinary(), sourceResource.adaptTo(Asset.class).getMimeType(), true);
                        sourceResource.adaptTo(Node.class).remove(); // Delete the triage folder asset.
                        if (adminSession.hasPendingChanges()) {
                            adminSession.save();
                        }
                    } else {
                        // An asset does not exist.
                        // Move new triage asset to the final location.
                        PageManager pageManager = adminResourceResolver.adaptTo(PageManager.class);
                        pageManager.move(sourceResource, destPath, null, false, true, null); // Use PageManager.move() to fully simulate a move request from the UI.
                        adminSession.getNode(destPath+CommonConstants.SLASH_STRING+JcrConstants.JCR_CONTENT).setProperty("jcr:lastModifiedBy", currentUserID);
                        adminSession.save();
                    }
                    LOG.info("Successfully moved triage folder asset: {}", destPath);
                }
            }
        } catch (RepositoryException | WCMException ex) {
            LOG.error("An exception occurred during move triage folder asset: {}\n{}", payloadPath, ex);
        }
    }

    /**
     * Gets the first entry of the tag which should be of type String[].
     *
     * @param propName The property name to search for.
     * @param assetNode The node to search on.
     * @return The first entry of the tag array.
     * @throws RepositoryException If an error occurs while retrieving the tag.
     */
    private static String getFirstEntryOfTag(String propName, Node assetNode) throws RepositoryException {
        Property prop = assetNode.getProperty(propName);
        if (prop.isMultiple()) {
            return prop.getValues()[0].getString();
        } else {
            return prop.getValue().getString();
        }
    }

    /**
     * Gets a valid folder node-name.  Is necessary to ensure folder name is Dynamic Media compliant.
     *
     * @param folderNodeName The original folder node-name.
     * @return The valid folder node-name.
     */
    private static String getValidFolderNodeName(String folderNodeName) {
        String str = folderNodeName;
        for (String symbol : INVALID_FOLDER_CHARS) {
            str = str.replace(symbol, CommonConstants.HYPHEN);
        }
        str = str.replaceAll(MULTIPLE_HYPHEN_REGEX, CommonConstants.HYPHEN); // Replace multiple hyphen with single hyphen
        str = StringUtils.stripStart(str, CommonConstants.HYPHEN); // Trim leading hyphen
        str = StringUtils.stripEnd(str, CommonConstants.HYPHEN); // Trim trailing hyphen
        return str;
    }

    /**
     * Gets or creates the final location for the triage asset.
     *
     * Final Location = `<CONTENT_DAM_EATON>/<TAG_FOLDERS>`
     *
     * @param resourceResolver Resolver used to get or create the final location.
     * @param tagId The tag id.
     * @return The final folder for the triage asset.
     * @throws RepositoryException If an error occurs while getting or creating the final folder.
     */
    private static Node getOrCreateFinalLocationFolder(ResourceResolver resourceResolver, String tagId) throws RepositoryException {
        Session session = resourceResolver.adaptTo(Session.class);
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);

        String[] pieces = tagId
            .replaceAll(EATON_NS, "")
            .split(CommonConstants.SLASH_STRING);

        StringBuilder pathBuilder = new StringBuilder(CommonConstants.CONTENT_DAM_EATON);
        StringBuilder tagBuilder = new StringBuilder(EATON_NS); // Used for setting dest folder's jcr:title

        Node returnNode = null;
        for (String piece : pieces) {
            pathBuilder.append(CommonConstants.SLASH_STRING);
            pathBuilder.append(getPreExistingFolder(piece) != null ? getPreExistingFolder(piece) : getValidFolderNodeName(piece));

            tagBuilder.append(piece);
            tagBuilder.append(CommonConstants.SLASH_STRING);

            Node intermFolderContent = JcrUtils.getOrCreateByPath(pathBuilder.toString() + CommonConstants.SLASH_STRING + JcrConstants.JCR_CONTENT, JcrResourceConstants.NT_SLING_ORDERED_FOLDER, JcrConstants.NT_UNSTRUCTURED, session, true);
            if (!intermFolderContent.hasProperty(CommonConstants.JCR_TITLE)) {
                Tag tag = tagManager.resolve(tagBuilder.toString());
                if (tag != null && StringUtils.isNotBlank(tag.getTitle())) {
                    intermFolderContent.setProperty(CommonConstants.JCR_TITLE, tag.getTitle());
                    session.save();
                }
            }
            returnNode = intermFolderContent.getParent();
        }
        return returnNode;
    }

    /**
     * Gets the pre-existing folder name.  We discovered there are pre-existing folders in Eaton's AEM.  These were created long ago and
     * are currently in use.  We're going to do our best to minimize duplicates.
     *
     * @param folder The folder name.
     * @return The matching folder name in Eaton's AEM.
     */
    private static String getPreExistingFolder(String folder) {
        if (StringUtils.equals(folder, PRODUCT_TAXONOMY)) {
            return PRODUCTS;
        } else if (StringUtils.equals(folder, SUPPORT_TAXONOMY)) {
            return SUPPORT;
        } else if (StringUtils.equals(folder, COMPANY_TAXONOMY)) {
            return COMPANY;
        }
        return null;
    }
}
