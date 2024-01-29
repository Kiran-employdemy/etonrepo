package com.eaton.platform.core.workflows;

/**
 * UpdateDuration.java
 * ------------------
 * When a video asset is ran through the Dynamic Media Encode Video workflow and Cloud Dynamic Media is
 * configured, the video duration is saved as a String to jcr:content/metadata/duration in seconds.
 * This process step will update the value to a long and to milliseconds. This new value will then
 * be stored to jcr:content/metadata/dc:extent which is the duration property typically used.
 *
 * By: Julie Rybarczyk (julie@freedomdam.com) 7/19/2019
 **/

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

@Component(
        service = { WorkflowProcess.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Updates video duration property.",
                Constants.SERVICE_VENDOR + "=" + "Freedom Marketing",
                "process.label" + "=" + "FM - Update Duration"
        },
        immediate = true
)
public class UpdateDuration implements WorkflowProcess {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        logger.debug("---------- Executing Update Duration Process ----------");
        WorkflowUtils.logWorkflowMetadata(logger, workItem, args);
        try {
            Session session = workflowSession.adaptTo(Session.class);
            // Get the asset node.
            Node payloadNode = WorkflowUtils.getPayloadNode(session.getRootNode(), workItem);
            logger.debug("payloadNode: {}", payloadNode.getPath());
            Node assetNode = WorkflowUtils.getAssetNode(payloadNode, logger);
            if (assetNode == null) {
                logger.error("Unable to find asset node for {}.", payloadNode.getPath());
                return;
            }
            String assetNodePath = assetNode.getPath();
            logger.debug("assetNode: {}", assetNodePath);
            // Get the metadata node.
            String metadataPath = "jcr:content/metadata";
            if (!assetNode.hasNode(metadataPath)) {
                logger.error("Unable to find metadata node for {}.", assetNodePath);
                return;
            }
            Node metadataNode = assetNode.getNode(metadataPath);
            // Get the duration property value.
            String durationProp = "duration";
            if (!metadataNode.hasProperty(durationProp)) {
                logger.error("Unable to find duration property for {}.", assetNodePath);
                return;
            }
            String duration = metadataNode.getProperty(durationProp).getString();
            // Convert value to a double.
            Double durationDouble = null;
            try {
                durationDouble = new Double(duration);
            } catch (NumberFormatException nfe) {
                logger.error("Duration value {} cannot be converted into a double for {}.", duration, assetNodePath);
                return;
            }
            // Multiply value by 1000 to represent value in milliseconds (instead of seconds).
            durationDouble = durationDouble * 1000;
            // Convert double to long.
            long durationLong = 0;
            try {
                durationLong = durationDouble.longValue();
            } catch (NumberFormatException nfe) {
                logger.error("Duration value {} cannot be converted into a long for {}.", durationDouble, assetNodePath);
                return;
            }
            // Save value to dc:extent property.
            metadataNode.setProperty("dc:extent", durationLong);
            session.save();
            logger.debug("Set duration value {} to dc:extent for {}", durationLong, assetNode.getPath());
        } catch (RepositoryException e) {
            logger.error("{}", e);
        }
    }
}