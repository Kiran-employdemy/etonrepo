package com.eaton.platform.core.schedulers;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.JcrUtils;
import com.eaton.platform.core.util.UserUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Iterator;

/**
 * This scheduler launches the Move Triage Folder Assets workflow on assets in the triage folder.
 * The workflow will move the assets within the triage folder to the final location.
 * By default, will run every 10 minutes.  To adjust, use config.
 *
 * Config: apps/eaton/runmodes/config.author/com.eaton.platform.core.schedulers.MoveTriageFolderAssets.xml
 */
@Component(service = Runnable.class, immediate = true)
public class MoveTriageFolderAssets implements Runnable {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(MoveTriageFolderAssets.class);

    /** The admin service. */
    @Reference
    private transient AdminService adminService;

    /** The resource resolver factory service. */
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    /** The Constant SYSTEM_USER. */
    private static final String SYSTEM_USER = "workflowProcessService";

    /** The Constant TRIAGE_FOLDER_ASSETS_QUERY. */
    private static final String TRIAGE_FOLDER_ASSETS_QUERY = "SELECT * FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE([/content/dam/eaton/triage]) and s.[jcr:content/dam:assetState] = 'processed'"; // Confirmed an index exists for query

    /** The Constant RUNNING_WORKFLOW_QUERY. */
    private static final String RUNNING_WORKFLOW_QUERY = "SELECT * FROM [cq:Workflow] AS w WHERE w.[status] = 'RUNNING' AND w.[data/payload/path] LIKE '%s%%'"; // Confirmed an index exists for query

    /** The Constant MOVE_TRIAGE_FOLDER_ASSET_WORKFLOW_MODEL. */
    private static final String MOVE_TRIAGE_FOLDER_ASSET_WORKFLOW_MODEL = "/var/workflow/models/eaton-dam/dam/eaton-move-triage-folder-asset";

    /** The Constant TRIAGE_FOLDER_TITLE. */
    private static final String TRIAGE_FOLDER_TITLE = "Triage Folder";

    /** The Constant TRIAGE_FOLDER_PATH. */
    private static final String TRIAGE_FOLDER_PATH = "/content/dam/eaton/triage";

    /** The Constant SUBASSETS. */
    private static final String SUBASSETS = "/subassets/";

    /** The Constant PRODUCT. */
    private static final String PRODUCT = "jcr:content/metadata/xmp:eaton-product-taxonomy";

    /** The Constant SERVICES. */
    private static final String SERVICES = "jcr:content/metadata/xmp:eaton-services-taxonomy";

    /** The Constant SUPPORT. */
    private static final String SUPPORT = "jcr:content/metadata/xmp:eaton-support-taxonomy";

    /** The Constant COMPANY. */
    private static final String COMPANY = "jcr:content/metadata/xmp:eaton-company-taxonomy";

    /** The Constant S7_SYNC_MODE. */
    private static final String S7_SYNC_MODE = "s7-sync-mode";

    /** The Constant EXCLUDE. */
    private static final String EXCLUDE = "exclude";

    /**
     * Called when the Scheduler is activated.
     *
     * @throws Exception The exception.
     */
    @Activate
    protected final void activate() throws Exception {
        LOG.debug("MoveTriageFolderAssets :: activate()");
        createTriageFolder();
    }

    @Override
    public void run() {
        LOG.debug("MoveTriageFolderAssets :: run()");

        try (ResourceResolver adminResourceResolver = adminService.getReadService(); // Used to Execute the Query
            ResourceResolver workflowResourceResolver = UserUtils.getResourceResolverFromServiceUser(resourceResolverFactory, SYSTEM_USER)) { // Used to Launch the Workflow
            Session adminSession = adminResourceResolver.adaptTo(Session.class);
            Iterator<Node> nodeItr = JcrUtils.executeSQL2(TRIAGE_FOLDER_ASSETS_QUERY, adminSession);
            if (nodeItr != null) {
                while (nodeItr.hasNext()) {
                    Node node = nodeItr.next();
                    if (shouldLaunchWorkflow(adminSession, node)) {
                        startWorkflow(workflowResourceResolver, node);
                    }
                }
            }
        } catch (WorkflowException | RepositoryException ex) {
            LOG.error("An exception occurred while trying to launch the triage workflow.\n{}", ex);
        }
    }

    /**
     * Checks if we should launch the workflow.
     *
     * @param session  Session used to perform the checks.
     * @param node The node to check.
     * @return true if we should launch the workflow, false otherwise.
     * @throws RepositoryException If an error occurs while performing checks.
     */
    private static boolean shouldLaunchWorkflow(Session session, Node node) throws RepositoryException {
        if (node.getPath().contains(SUBASSETS)) {
            LOG.info("Not launching workflow because the following asset is an embedded asset extracted as a subasset: {}", node.getPath());
            return false;
        }

        if (!node.hasProperty(PRODUCT) && !node.hasProperty(SERVICES) && !node.hasProperty(SUPPORT) && !node.hasProperty(COMPANY)) {
            LOG.info("Not launching workflow because the following asset does not have a Product or Services or Support or Company: {}", node.getPath());
            return false;
        }

        String runningWorkflowQuery = String.format(RUNNING_WORKFLOW_QUERY, node.getPath());
        Iterator<Node> nodeItr = JcrUtils.executeSQL2(runningWorkflowQuery, session);
        if (nodeItr != null && nodeItr.hasNext()) {
            LOG.info("Not launching workflow because the following asset is already in a running workflow: {}", node.getPath());
            return false;
        }

        return true;
    }

    /**
     * Starts the move triage folder asset workflow.
     *
     * @param resourceResolver Resolver used to start the workflow.
     * @param node The node to use.
     * @throws WorkflowException If an error occurs while launching the workflow.
     * @throws RepositoryException If an error occurs while accessing asset note.
     */
    private void startWorkflow(ResourceResolver resourceResolver, Node node) throws WorkflowException, RepositoryException {
        WorkflowSession wfSession = resourceResolver.adaptTo(WorkflowSession.class);
        WorkflowModel wfModel = wfSession.getModel(MOVE_TRIAGE_FOLDER_ASSET_WORKFLOW_MODEL);
        WorkflowData wfData = wfSession.newWorkflowData(CommonConstants.JCR_PATH, node.getPath());
        wfSession.startWorkflow(wfModel, wfData);
        LOG.info("Started Move Triage Folder Asset workflow for asset: {}", node.getPath());
    }

    /**
     * Creates the triage folder.
     */
    private void createTriageFolder() {
        try (ResourceResolver adminResourceResolver = adminService.getWriteService()) {
            if (adminResourceResolver.getResource(TRIAGE_FOLDER_PATH + CommonConstants.SLASH_STRING + JcrConstants.JCR_CONTENT) == null) {
                Session adminSession = adminResourceResolver.adaptTo(Session.class);
                Node triageFolderContent = org.apache.jackrabbit.commons.JcrUtils.getOrCreateByPath(TRIAGE_FOLDER_PATH  + CommonConstants.SLASH_STRING + JcrConstants.JCR_CONTENT, JcrResourceConstants.NT_SLING_ORDERED_FOLDER, JcrConstants.NT_UNSTRUCTURED, adminSession, true);
                if (!triageFolderContent.hasProperty(CommonConstants.JCR_TITLE)) {
                    triageFolderContent.setProperty(CommonConstants.JCR_TITLE, TRIAGE_FOLDER_TITLE);
                    triageFolderContent.setProperty(S7_SYNC_MODE, EXCLUDE);
                    adminSession.save();
                }
                LOG.info("Created Triage Folder: {}", TRIAGE_FOLDER_PATH);
            }
        } catch (RepositoryException ex) {
            LOG.error("An exception occurred while creating the triage folder.");
        }
    }
}
