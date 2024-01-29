package com.eaton.platform.core.maintenance;


import com.adobe.granite.maintenance.MaintenanceConstants;
import com.eaton.platform.core.constants.TagMap;
import com.eaton.platform.core.util.JcrUtils;
import com.eaton.platform.core.util.UserUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobExecutionContext;
import org.apache.sling.event.jobs.consumer.JobExecutionResult;
import org.apache.sling.event.jobs.consumer.JobExecutor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static com.day.cq.wcm.api.NameConstants.PN_TAGS;

/**
 * CopyCqTagsField.java
 * ----------------------------
 * This maintenance task looks for assets that have any tags under the cq:tags metadata field and copies them into their own namespaces.
 * ----------------------------
 * By: Ceirra Schuette (ceirra@freedomdam.com)
 */

@Component(
        service = {JobExecutor.class},
        property = {
                MaintenanceConstants.PROPERTY_TASK_SCHEDULE + "=" + MaintenanceConstants.SCHEDULE_DAILY,
                MaintenanceConstants.PROPERTY_TASK_CONSERVATIVE + "=" + "false",
                MaintenanceConstants.PROPERTY_TASK_STOPPABLE + "=" + "true",
                JobExecutor.PROPERTY_TOPICS + "=" + MaintenanceConstants.TASK_TOPIC_PREFIX + "CopyCqTagsField",
                MaintenanceConstants.PROPERTY_TASK_NAME + "=" + "com.eaton.platform.core.maintenance.CopyCqTagsField",
                MaintenanceConstants.PROPERTY_TASK_TITLE + "=" + "Eaton - Copy cq:tags field"
        },
        immediate = true
)

public class CopyCqTagsField implements JobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CopyCqTagsField.class);

    private static final String CQ_TAGS_QUERY = "SELECT * FROM [dam:Asset] AS s WHERE ISDESCENDANTNODE(s, '/content/dam') AND s.[jcr:content/metadata/cq:tags] IS NOT NULL";
    private static final String SERVICE_USER = "writeService";
    private static final String METADATA_NODE_PATH = "jcr:content/metadata";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    public CopyCqTagsField() {
    }

    @Override
    public JobExecutionResult process(Job job, JobExecutionContext jobExecutionContext) {
        logger.info("*********************** Starting Copy cq:tags Field ***********************");
        JobExecutionResult jobExecutionResult;
        Session session = null;
        ResourceResolver resolver = UserUtils.getResourceResolverFromServiceUser(resourceResolverFactory, SERVICE_USER);
        if (resolver == null) {
            logger.error("Unable to retrieve service user resolver");
            return jobExecutionContext.result().message("Unable to retrieve service user resolver").failed();
        }
        session = resolver.adaptTo(Session.class);
        if (session == null) {
            logger.error("Unable to retrieve service user session");
            return jobExecutionContext.result().message("Unable to retrieve service user session").failed();
        }
        try {
            Iterator<Node> results = JcrUtils.executeSQL2(CQ_TAGS_QUERY, session);
            if (results == null) {
                logger.info("No assets found with the metadata property cq:tags.");
                return jobExecutionContext.result().succeeded();
            }
            while (results.hasNext()) {
                Node assetNode = results.next();
                logger.info("Copying cq:tags for {}", assetNode.getPath());
                //prevent workflows from launching
                session.getWorkspace().getObservationManager().setUserData("changedByWorkflowProcess");
                Node assetMetadataNode = assetNode.getNode(METADATA_NODE_PATH);
                ArrayList<Value> currentCqTags;
                //get the tags in cq:tags on the asset
                currentCqTags = getCurrentCqTags(assetMetadataNode);
                //process the cq:tags
                if (processCqTags(currentCqTags, session, assetMetadataNode)) {
                    logger.info("cq:tags successfully copied for {}", assetNode.getPath());
                } else {
                    logger.info("cq:tags not copied for {}", assetNode.getPath());
                }
            }
            jobExecutionResult = jobExecutionContext.result().succeeded();
        } catch (RepositoryException e) {
                logger.error("{}", e.getMessage());
                jobExecutionContext.log("Exception while copying cq:tag field: {0}", new Object[]{e});
                jobExecutionResult = jobExecutionContext.result().message(e.getMessage()).failed();
        } finally {
            JcrUtils.closeSession(session);
            JcrUtils.closeResourceResolver(resolver);
            logger.info("*********************** Copy cq:tags Field Job Complete ***********************");
        }
        return jobExecutionResult;
    }

    private static boolean processCqTags(ArrayList<Value> currentCqTags, Session session, Node assetMetadataNode) {
        boolean success = false;
        Map<String, String> tagSyncList = new TagMap().getTagSyncList();
        for (Value cqTag : currentCqTags) {
            if (tagSyncList.containsKey(cqTag.toString().split("/")[0])) {
                if (setOrAddMoreTags(session, assetMetadataNode, tagSyncList.get(cqTag.toString().split("/")[0]), cqTag)) {
                    success = true;
                }
            }
        }
        return success;
    }

    private static boolean setOrAddMoreTags(Session session, Node assetMetadataNode, String newTagProperty, Value newTagValue) {
        try {
            ArrayList<Value> currentTags = new ArrayList<>();
            //if the tag metadata property already exists grab the tags to add to them
            if (assetMetadataNode.hasProperty(newTagProperty)) {
                if (assetMetadataNode.getProperty(newTagProperty).isMultiple()) {
                    currentTags.addAll(Arrays.asList(assetMetadataNode.getProperty(newTagProperty).getValues()));
                } else {
                    currentTags.add(assetMetadataNode.getProperty(newTagProperty).getValue());
                }
                if (!currentTags.contains(newTagValue)) {
                    currentTags.add(newTagValue);
                    //remove all the tags in case it was singular
                    assetMetadataNode.getProperty(newTagProperty).remove();
                    assetMetadataNode.setProperty(newTagProperty, currentTags.toArray(new Value[0]));
                }
            } else {
                assetMetadataNode.setProperty(newTagProperty, new Value[]{newTagValue});
            }
            session.save();
        } catch (RepositoryException e) {
            logger.error("{}", e.getMessage());
            return false;
        }
        return true;
    }

    private static ArrayList<Value> getCurrentCqTags(Node assetMetadataNode) throws RepositoryException {
        ArrayList<Value> currentCqTags = new ArrayList<>();
        //get the tags in cq:tags on the asset
        if (assetMetadataNode.hasProperty(PN_TAGS)) {
            if (assetMetadataNode.getProperty(PN_TAGS).isMultiple()) {
                currentCqTags.addAll(Arrays.asList(assetMetadataNode.getProperty(PN_TAGS).getValues()));
            } else {
                currentCqTags.add(assetMetadataNode.getProperty(PN_TAGS).getValue());
            }
        }
        return currentCqTags;
    }
}
