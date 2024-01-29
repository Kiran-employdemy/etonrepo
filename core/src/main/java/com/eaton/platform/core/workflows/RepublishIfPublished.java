package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.eaton.platform.core.util.WorkflowUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component(
        service = { WorkflowProcess.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Republish Asset If Published",
                Constants.SERVICE_VENDOR + "=" + "Freedom Marketing",
                "process.label" + "=" + "FM - Republish Asset If Published"
        },
        immediate = true
)
public class RepublishIfPublished implements WorkflowProcess {
    protected final Logger logger = LoggerFactory.getLogger(RepublishIfPublished.class);
    private final String LAST_REPLICATION_ACTION_PROPERTY = "jcr:content/cq:lastReplicationAction";

    @Reference
    private Replicator replicator;

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        logger.debug("---------- Executing Republish Asset If Published Process ----------");
        WorkflowUtils.logWorkflowMetadata(logger, workItem, metaDataMap);

        try {
            Session session = workflowSession.adaptTo(Session.class);
            Node root = session.getRootNode();
            Node payloadNode = WorkflowUtils.getPayloadNode(root, workItem);
            Node assetNode = WorkflowUtils.getAssetNode(payloadNode, logger);
            ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
            String currentUserID = StringUtils.EMPTY;
            
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
                Authorizable authorizable;
                
                    authorizable = userManager.getAuthorizable(workItem.getWorkflow().getInitiator());
                    if (null != userManager.getAuthorizable(authorizable.getID())) {
                        currentUserID = authorizable.getID();
                    }

            logger.debug("Determining whether to re-publish asset.");
            if (assetNode.hasProperty(LAST_REPLICATION_ACTION_PROPERTY) &&
                    assetNode.getProperty(LAST_REPLICATION_ACTION_PROPERTY).getString().equalsIgnoreCase("activate")) {
                replicator.replicate(session, ReplicationActionType.ACTIVATE, assetNode.getPath());
                logger.debug("\tRe-published asset: {}", assetNode.getPath());
               Node jcrNode=assetNode.getNode(JcrConstants.JCR_CONTENT);
               if (jcrNode!=null) {           	
                jcrNode.setProperty("cq:lastReplicatedBy", currentUserID);
                session.save();
            }
            }//if
            else {
                logger.debug("\tAsset was not published. Not re-publishing.");
            }//else
        } catch (RepositoryException | ReplicationException ex) {
            logger.error("{}", ex.getMessage());
        }
    }
}
