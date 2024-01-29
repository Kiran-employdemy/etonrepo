package com.eaton.platform.core.workflows.dynamicParticipant;
/**
 * Given a property, returns the user/group value at the given property if valid.
 * If property not found or the user/group value is not valid and a valid default user/group is given, returns default user/property.
 * If all else fails, returns admin user.
 *
 * Input Format:
 *      property
 *      property,default-user-or-group
 *
 * Input Example:
 *      jcr:content/metadata/jcr:lastModifiedBy
 *      jcr:content/metadata/jcr:lastModifiedBy,asset-taggers
 *
 * By: Julie Rybarczyk (julie@freedomdam.com) 7/12/2019
 **/
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import com.eaton.platform.core.util.UserUtils;
import com.eaton.platform.core.util.WorkflowUtils;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.osgi.framework.Constants.SERVICE_DESCRIPTION;

@Component(
        service = { ParticipantStepChooser.class },
        property = {
                SERVICE_DESCRIPTION + "=" + "Retrieves user or group given a property.",
                "chooser.label" + "=" + "FM - Get Dynamic Participant by Property"
        },
        immediate = true
)
public class GetDynamicParticipantByProperty implements ParticipantStepChooser {

    private static final Logger logger = LoggerFactory.getLogger(GetDynamicParticipantByProperty.class);
    private static final String SYSTEM_USER = "workflowServiceUser";

    @Reference
    private ResourceResolverFactory factory;

    @SuppressWarnings("squid:S2583")
    public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap) throws WorkflowException {
        logger.debug("---------- Executing Get Dynamic Participant by Property----------");
        // Get the required default user/group and property.
        String args = metaDataMap.get("PROCESS_ARGS", String.class);
        String[] argsArray = args.split(",");
        if (argsArray.length < 1 || argsArray.length > 2) {
            logger.error("{} requires 1-2 arguments. Please see class documentation for more info.", this.getClass());
            return "admin";
        }
        String property = argsArray[0];
        String defaultUserOrGroup = "";
        if (argsArray.length > 1) {
            defaultUserOrGroup = argsArray[1];
        }
        try {
            ResourceResolver resourceResolver = UserUtils.getResourceResolverFromServiceUser(factory, SYSTEM_USER);
            Session session = resourceResolver.adaptTo(Session.class);
            // Get the asset node to update.
            Node payloadNode = WorkflowUtils.getPayloadNode(session.getRootNode(), workItem);
            Node assetNode = WorkflowUtils.getAssetNode(payloadNode, logger);
            logger.debug("payloadNode: {}", payloadNode.getPath());

            if (assetNode == null) {
                logger.error("Unable to find asset node for {}.", payloadNode.getPath());
                return "admin";
            }
            logger.debug("assetNode: {}", assetNode.getPath());
            // Get the User Manager to verify user/group.
            UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            // If property exists and value is a valid user/group, return it.
            if (assetNode.hasProperty(property)) {
                String dynamicParticipant = assetNode.getProperty(property).getValue().getString();
                if (userManager.getAuthorizable(dynamicParticipant) != null) {
                    logger.debug("Returning dynamic participant: {}", dynamicParticipant);
                    return dynamicParticipant;
                }
            }
            // If valid, return the default user/group.
            if (userManager.getAuthorizable(defaultUserOrGroup) != null) {
                logger.debug("Returning default user/group: {}", defaultUserOrGroup);
                return defaultUserOrGroup;
            }
        } catch (RepositoryException re) {
            logger.error("", re);
        }
        // If all else fails, return the admin user.
        logger.debug("Returning default admin user.");
        return "admin";
    }
}