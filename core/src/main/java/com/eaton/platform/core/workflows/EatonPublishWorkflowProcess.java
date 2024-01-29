package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;


@Component(service = com.adobe.granite.workflow.exec.WorkflowProcess.class, immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Publish content immediately",
                AEMConstants.SERVICE_VENDOR_EATON,
        })
public class EatonPublishWorkflowProcess implements WorkflowProcess {
    private static final Logger log = LoggerFactory.getLogger(EatonPublishWorkflowProcess.class);


    //Replicator Service Reference
    @Reference
    private Replicator replicator;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        log.info("Here in execute method of EatonPublishWorkflowProcess");    //ensure that the execute method is invoked
        final WorkflowData workflowData = workItem.getWorkflowData();
        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        String currentUserID = StringUtils.EMPTY;
        UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            Authorizable authorizable;

        try {
                authorizable = userManager.getAuthorizable(workItem.getWorkflow().getInitiator());
                if (null != userManager.getAuthorizable(authorizable.getID())) {
                    currentUserID = authorizable.getID();
                }
            log.info("Here in try execute method of Publish");
            Session session = workflowSession.adaptTo(Session.class); // get the user session

            final String path = workflowData.getPayload().toString(); // get the Pages Path Submitted

            log.info("-------------------Publish Workdlow metadata for key PROCESS_ARGS and value {}", path);

            replicator.replicate(session, ReplicationActionType.ACTIVATE, path); // Publish the Pages
            Node jcrNode = session.getNode(path + CommonConstants.SLASH_STRING +JcrConstants.JCR_CONTENT);
            if (jcrNode!=null) {	
                jcrNode.setProperty("cq:lastReplicatedBy", currentUserID);
                session.save();
            }

        } catch (Exception e) {
            // If an error occurs that prevents the Workflow from completing/continuing - Throw a WorkflowException
            // and the WF engine will retry the Workflow later (based on the AEM Workflow Engine configuration).
            log.error("Unable to complete processing the Workflow Process step", e);

            throw new WorkflowException("Unable to complete processing the Workflow Process step", e);
        }


    }


}
