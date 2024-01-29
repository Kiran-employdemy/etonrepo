package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.util.WorkflowUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;

import static com.day.cq.commons.jcr.JcrConstants.*;
import static com.day.cq.commons.jcr.JcrConstants.JCR_LAST_MODIFIED_BY;
import static com.day.cq.dam.api.DamConstants.*;

/**
 * UpdateCQNameProcess.java
 * ------------------------
 * This process ensures that the file name is searchable in AEM, as the cq:name property is not reliably searchable.
 * Also saves the original uploader of the asset as a separate metadata property.
 * Additionally, this process updates a custom "modified by" property in case users want to search by that value.
 *
 * Workflow Process Step Arguments:
 * In order for this process step to function properly, you will need to provide three comma-delimited arguments
 * when configuring the workflow. The three arguments correspond to:
 * 1. A client-specific backup asset metadata property where the filename will be stored
 * 2. A client-specific asset metadata property which stores the name of the user who uploaded the asset
 * 3. A client-specific asset metadata property which stores the name of the user who last modified the asset
 *
 * Example argument input:
 * xmp:freedom-file-name,xmp:freedom-imported-by,xmp:freedom-modified-by
 * ------------------------
 * By: Jay LeCavalier (jay@freedomdam.com)
 * Modified: 7/23/19 by Jay LeCavalier (jay@freedomdam.com)
 */

@Component(
        service = { WorkflowProcess.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Try to ensure the File Name is populated for every asset.",
                Constants.SERVICE_VENDOR + "=" + "Freedom Marketing",
                "process.label" + "=" + "FM - Update CQ Name"
        },
        immediate = true
)
public class UpdateCQNameProcess implements WorkflowProcess {

    protected final Logger logger = LoggerFactory.getLogger(UpdateCQNameProcess.class);
    private final String TARGET_PROPERTY = PN_NAME;

    private String backupProperty = "";
    private String uploadedProperty = "";
    private String modifiedProperty = "";

    private VersionManager versionManager;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        logger.debug("---------- Executing Update CQ Name Process ----------");
        WorkflowUtils.logWorkflowMetadata(logger, workItem, metaDataMap);

        if (!parseArguments(metaDataMap)) {
            // If we get here, it means there was a problem with parsing the arguments, so just terminate the workflow
            // after parseArguments logs the error.
            return;
        }

        try {
            if (workItem.getWorkflowData().getPayloadType().equals("JCR_PATH")) {
                Session session = workflowSession.adaptTo(Session.class);
                Node node = session.getNode(workItem.getWorkflowData().getPayload().toString());
                // Get the version manager from the asset node.
                if (node != null) {
                    versionManager = node.getSession().getWorkspace().getVersionManager();
                } else {
                    logger.error("Asset node is null. Exiting process.");
                    return;
                }

                logger.debug("Workflow payload: {}", node.getPath());

                if(node.hasProperty(JCR_PRIMARYTYPE) &&
                        node.getProperty(JCR_PRIMARYTYPE).getString().equals(NT_DAM_ASSET) &&
                        node.hasNode(JCR_CONTENT)) {
                    node = node.getNode(JCR_CONTENT);
                }//if
                else {
                    while (node.hasProperty(JCR_PRIMARYTYPE) &&
                            (!node.getProperty(JCR_PRIMARYTYPE).getString().equals(NT_DAM_ASSETCONTENT))) {
                        node = node.getParent();
                    }//while
                }//else
                logger.debug("The node we're going to modify: {}", node.getPath());

                // Cannot set property if node is checked in.
                boolean isCheckedOut = versionManager.isCheckedOut(node.getPath());
                if (!isCheckedOut) {
                    versionManager.checkout(node.getPath());
                }

                String name = null;
                Node mdNode = node.getNode("metadata");
                Node parentNode = node.getParent();

                if (!node.hasProperty(TARGET_PROPERTY)) {
                    // If the node doesn't have a cq:name, then we need to create one.
                    // The pathname of the parent node should have the file name in it, which will serve
                    // perfectly as a cq:name value
                    name = generateName(node);
                    node.setProperty(TARGET_PROPERTY, name);
                }//if
                else {
                    // If the node does have a cq:name, we still need to copy the value to the
                    // xmp:freedom-file-name metadata property for search purposes.
                    name = node.getProperty(TARGET_PROPERTY).getString();
                }//else

                // Copy the file name to xmp:freedom-file-name for searching purposes.
                // This action needs to happen every time, in case the file name changes!
                mdNode.setProperty(backupProperty, name);

                // If the metadata node doesn't have the UPLOADED_PROPERTY, set it.
                // This action shouldn't need to happen every time, as the original uploader won't change.
                if(! mdNode.hasProperty(uploadedProperty)) {
                    // If there is a jcr:createdBy property, copy it to xmp:freedom-imported-by for
                    // future search purposes.

                    if (parentNode.hasProperty(JCR_CREATED_BY)) {
                        mdNode.setProperty(uploadedProperty, parentNode.getProperty(JCR_CREATED_BY).getString());
                    }//if
                }//if
                else {
                    logger.debug("Skipped updating {}, because it already exists.", uploadedProperty);
                }//else

                // Update the MODIFIED_PROPERTY
                if (node.hasProperty(JCR_LAST_MODIFIED_BY)) {
                    mdNode.setProperty(modifiedProperty, node.getProperty(JCR_LAST_MODIFIED_BY).getString());
                } else {
                    logger.debug("Skipped updating {} since the last modifying user is not known.");
                }

                session.save();
            } else {
                logger.warn("This process only works with JCR_PATH payload types!");
            }//else
        } catch (RepositoryException e) {
            logger.error("{}", e);
        }//catch
    }//end method execute

    private String generateName(Node n) throws RepositoryException {
        // The pathname of the parent node will work for the cq:name
        String[] parentPath = n.getParent().getPath().split("/");
        return parentPath[parentPath.length - 1];
    }//end method generateName

    // Parses the arguments in the workflow model metadata. Returns true if successful and false otherwise.
    private boolean parseArguments(MetaDataMap properties) {
        String rawArguments = "NULL";
        if (properties.containsKey("PROCESS_ARGS")) {
            rawArguments = properties.get("PROCESS_ARGS", String.class);
        }
        String[] arguments = rawArguments.split(",");
        if (arguments.length == 3) {
            backupProperty = arguments[0];
            uploadedProperty = arguments[1];
            modifiedProperty = arguments[2];
            return true;
        }

        logger.error("{} is not a valid list of arguments for {}. Please see the class file for documentation.", rawArguments, getClass().getName());
        return false;
    }

}//end class UpdateCQNameProcess