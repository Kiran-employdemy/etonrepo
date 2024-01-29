package com.eaton.platform.core.workflows;

/**
 * PropertiesValuesCheckProcess.java
 * --------------------------------------
 * Given a list of one or more comma-separated properties and their associated values, checks to see if this asset
 * has at least one of the given properties with the matching value. Sets HAS_PROP_VALUE to true or false.
 *
 * Input Format:
 *      property1::value1,property2::value2
 *
 * Input Example:
 *      jcr:content/dam:portalReplicationAction::Activate,
 *      jcr:content/metadata/dam:scene7FileStatus::PublishComplete
 *
 * By: Julie Rybarczyk (julie@freedomdam.com) 7/23/2019
 * --------------------------------------
 */

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

@Component(
        service = { WorkflowProcess.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Checks if asset has at least one of the given properties and matching value.",
                Constants.SERVICE_VENDOR + "=" + "Freedom Marketing",
                "process.label" + "=" + "FM - Properties Values Check Process"
        },
        immediate = true
)
public class PropertiesValuesCheckProcess implements WorkflowProcess {

    protected final Logger logger = LoggerFactory.getLogger(PropertiesValuesCheckProcess.class);

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        logger.debug("---------- Executing Properties Values Check Process ----------");
        WorkflowUtils.logWorkflowMetadata(logger, workItem, metaDataMap);
        String input = metaDataMap.get("PROCESS_ARGS", String.class);
        // Exit if arguments are not provided.
        if (input == null || input.trim().equals("")) {
            logger.error("Invalid input: {}. See class definition for input format required.", input);
            return;
        }
        try {
            Session session = workflowSession.adaptTo(Session.class);
            Node root = session.getRootNode();
            Node payloadNode = WorkflowUtils.getPayloadNode(root, workItem);
            Node assetNode = WorkflowUtils.getAssetNode(payloadNode, logger);
            logger.debug("payloadNode: {}", payloadNode.getPath());
            if (assetNode == null) {
                logger.error("Unable to find asset node for {}. Node will not be checked.", payloadNode.getPath());
                return;
            }
            logger.debug("assetNode: {}", assetNode.getPath());

            MetaDataMap wfMetadata = workItem.getWorkflowData().getMetaDataMap();
            String[] publishValsAndPropsArray = input.split(",");
            for (String s : publishValsAndPropsArray) {
                String[] publishValAndProp = s.split("::");
                if (publishValAndProp.length != 2) {
                    logger.error("Invalid input: {}. See class definition for input format required.", input);
                    return;
                }
                String prop = publishValAndProp[0].trim();
                String val = publishValAndProp[1].trim();
                logger.debug("Checking if asset has...");
                logger.debug("Property: {}", prop);
                logger.debug("Value: {}", val);
                if (assetNode.hasProperty(prop) && assetNode.getProperty(prop).getString().equalsIgnoreCase(val)) {
                    wfMetadata.put("HAS_PROP_VALUE", "YES");
                    logger.debug("Asset has property and value.");
                    logger.debug("Setting HAS_PROP_VALUE to YES.");
                    return;
                } else {
                    logger.debug("Asset does not have property and value.");
                }
            }
            logger.debug("Setting HAS_PROP_VALUE to NO.");
            wfMetadata.put("HAS_PROP_VALUE", "NO");
        } catch (RepositoryException ex) {
            logger.error("{}", ex);
        }
    }
}
