package com.eaton.platform.core.workflows.dynamicParticipant;

/**
 * Gets and returns the user/group the workflow is currently assigned to.
 *
 * By: Julie Rybarczyk (julie@freedomdam.com) 7/16/2019
 **/
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.util.UserUtils;
import com.eaton.platform.core.util.WorkflowUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;

@Component(
        service = { ParticipantStepChooser.class },
        property = {
                SERVICE_DESCRIPTION + "=" + "Retrieves user or group that the workflow is currently assigned to.",
                "chooser.label" + "=" + "FM -  Get Dynamic Participant by Current Assignee"
        },
        immediate = true
)
public class GetDynamicParticipantByCurrentAssignee implements ParticipantStepChooser {

    private static final Logger logger = LoggerFactory.getLogger(GetDynamicParticipantByCurrentAssignee.class);
    private static final String SYSTEM_USER = "workflowServiceUser";

    @Reference
    private ResourceResolverFactory factory;

    public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap) throws WorkflowException {
        logger.debug("---------- Executing Get Dynamic Participant by Current Assignee----------");
        WorkflowUtils.logWorkflowMetadata(logger, workItem, metaDataMap);

        ResourceResolver resourceResolver = UserUtils.getResourceResolverFromServiceUser(factory, SYSTEM_USER);
        Session session = resourceResolver.adaptTo(Session.class);
        String currentUserGroup = workItem.getWorkflowData().getMetaDataMap().get("ASSIGNED_TO", String.class);
        logger.debug("Assigned user/group found in work-item's workflow-data's metadatamap: " + currentUserGroup);
        try {
            Boolean userOrGroupExists = UserUtils.userOrGroupExists(currentUserGroup, factory, session);
            if (userOrGroupExists) {
                logger.debug("Returning assigned user/group: {}", currentUserGroup);
                return currentUserGroup;
            }
        } catch (RepositoryException re) {
            logger.error("", re);
        } catch (LoginException le) {
            logger.error("", le);
        }
        // If all else fails, return the admin user.
        logger.debug("Returning default admin user.");
        return "admin";
    }
}