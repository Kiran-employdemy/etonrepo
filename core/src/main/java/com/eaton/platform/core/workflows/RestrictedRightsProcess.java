package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.util.WorkflowUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component(
        service = { WorkflowProcess.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Checks Restricted Rights",
                Constants.SERVICE_VENDOR + "=" + "Eaton",
                "process.label" + "=" + "Eaton - Restricted Rights Process"
        },
        immediate = true
)
public class RestrictedRightsProcess implements WorkflowProcess {

    protected final Logger logger = LoggerFactory.getLogger(RestrictedRightsProcess.class);

    private static final String EATON_RIGHTS_RESTRICTED_DETAIL = "xmp:eaton-rights-restricted-detail";
    private static final String EXPIRATION_DATE_PROPERTY = "prism:expirationDate";
    private static final String STATUS_PROPERTY = "dam:status";
    private static final String METADATA_NODE_PATH = "jcr:content/metadata";
    private static final String IMAGE_RIGHTS = "xmp:eaton-image-rights";
    private static final String RIGHTS_MANAGED = "rights-managed";

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        logger.debug("---------- Executing Restricted Rights Process ----------");
        WorkflowUtils.logWorkflowMetadata(logger, workItem, metaDataMap);

        try {
            Session session = workflowSession.adaptTo(Session.class);
            Node root = session.getRootNode();
            Node payloadNode = WorkflowUtils.getPayloadNode(root, workItem);
            Node assetNode = WorkflowUtils.getAssetNode(payloadNode, logger);
            if (assetNode == null) {
                logger.error("Unable to find asset node for {}. Node will not be updated.", payloadNode.getPath());
                return;
            }
            Node assetMetadataNode = assetNode.getNode(METADATA_NODE_PATH);
            logger.debug("payloadNode: {}", payloadNode.getPath());

            logger.debug("assetNode: {}", assetNode.getPath());

            MetaDataMap wfMetadata = workItem.getWorkflowData().getMetaDataMap();
            wfMetadata.put("RESTRICTED_RIGHTS", "no");
            logger.debug("Setting RESTRICTED_RIGHTS to no");

            if (assetMetadataNode.hasProperty(EXPIRATION_DATE_PROPERTY) || assetMetadataNode.hasProperty(EATON_RIGHTS_RESTRICTED_DETAIL)) {
                wfMetadata.put("RESTRICTED_RIGHTS", "yes");
                logger.debug("Setting RESTRICTED_RIGHTS to yes");
            }
            if (assetMetadataNode.hasProperty(STATUS_PROPERTY)){
                if (assetMetadataNode.getProperty(STATUS_PROPERTY).getValue().toString().equals("expired")){
                    wfMetadata.put("RESTRICTED_RIGHTS", "yes");
                    logger.debug("Setting RESTRICTED_RIGHTS to yes");
                }//if
            }//if
            if (assetMetadataNode.hasProperty(IMAGE_RIGHTS)){
                if (assetMetadataNode.getProperty(IMAGE_RIGHTS).getValue().toString().equals(RIGHTS_MANAGED)){
                    wfMetadata.put("RESTRICTED_RIGHTS", "yes");
                    logger.debug("Setting RESTRICTED_RIGHTS to yes");
                }//if
            }//if
        } catch (RepositoryException ex) {
            logger.error("{}", ex);
        }
    }
}
