package com.eaton.platform.core.util;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.dam.api.DamConstants;
import org.slf4j.Logger;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WorkflowUtils {
    public static void logWorkflowMetadata(Logger logger, WorkItem workItem, MetaDataMap metaDataMap) {
        Iterator<String> mdIter = metaDataMap.keySet().iterator();

        logger.debug("-- MetaDataMap Metadata Follows --");
        while(mdIter.hasNext()) {
            String key = mdIter.next();
            logger.debug("Key: {}\tValue: {}", key, metaDataMap.get(key, String.class));
        }//while
        logger.debug("-- WorkItem Metadata Follows: --");
        mdIter = workItem.getMetaDataMap().keySet().iterator();
        while(mdIter.hasNext()) {
            String key = mdIter.next();
            logger.debug("Key: {}\tValue: {}", key, workItem.getMetaDataMap().get(key, String.class));
        }//while
        logger.debug("-- Workflow Metadata Follows: --");
        mdIter = workItem.getWorkflowData().getMetaDataMap().keySet().iterator();
        while(mdIter.hasNext()) {
            String key = mdIter.next();
            logger.debug("Key: {}\tValue: {}", key, workItem.getWorkflowData().getMetaDataMap().get(key, String.class));
        }//while
    }//end method LogWorkflowMetadata

    public static String getStringFromMetaDataMap(MetaDataMap map, String key) {
        String retVal = "";
        retVal = map.get(key, "** NOT SET **");
        if(retVal.equals("** NOT SET **")) {
            retVal = Arrays.toString(map.get(key, new String[]{""}));
            if(retVal.equals("[]")) {
                retVal = "";
            }//if
            else {
                retVal = retVal.substring(1);
                retVal = retVal.substring(0, retVal.length() - 1);
                retVal = retVal.replace(", ", ",");
            }//else
        }//if
        return retVal;
    }//end method getStringFromMetaDataMap

    public static Node getPayloadNode(Node rootNode, WorkItem workItem) throws RepositoryException {
        return rootNode.getNode(((String) workItem.getWorkflowData().getPayload()).substring(1));
    }

    public static String getStringFromPayload(WorkItem workItem) {
        return workItem.getWorkflowData().getPayload().toString();
    }

    /**
     * Returns the path of the property. This is the string before the last occurrence of a forward slash '/'.
     * @param property e.g. 'jcr:content/metadata/dc:description'
     * @return e.g. 'jcr:content/metadata'
     */
    public static String getPath(String property) {
        int lastIndexOfSlash = property.lastIndexOf('/');
        if (lastIndexOfSlash == -1) {
            return "";
        }
        return property.substring(0, lastIndexOfSlash);
    }

    /**
     * Returns the property name. This is the string after the last occurrence of a forward slash '/'.
     * @param property e.g. 'jcr:content/metadata/dc:description'
     * @return e.g. 'dc:description'
     */
    public static String getName(String property) {
        int lastIndexOfSlash = property.lastIndexOf('/');
        if (lastIndexOfSlash == -1) {
            return property;
        }
        if (property.length() < 2) {
            return "";
        }
        return property.substring(lastIndexOfSlash + 1, property.length());
    }

    /**
     * Given a node of any type, returns the asset type node associated by iterating through the node's
     * parents if the given node is not already an asset type node.
     * This method is useful in the event the given node is a rendition of an asset node.
     * @param node Node of any type
     * @param log The log for this workflow instance
     * @return the associated asset type node; Null otherwise
     * @throws RepositoryException
     */
    public static Node getAssetNode(Node node, Logger log) throws RepositoryException {
        String originalNodePath = node.getPath();
        try {

            while(! node.getPrimaryNodeType().isNodeType(DamConstants.NT_DAM_ASSET) && node.getDepth() > 0) {
               
                node = node.getParent();
            }//while

            if(! node.getPrimaryNodeType().isNodeType(DamConstants.NT_DAM_ASSET)) {
                log.warn("Couldn't find asset node for original node path: {}", originalNodePath);
                node = null;
            }//if
        }
        catch (ItemNotFoundException infe) {
            log.warn("The parent node is the root node of the workspace for {}", node.toString());
            log.warn("{}", infe.getMessage());
            String hi="dfsdf";
            return null;
            
        }
        catch (AccessDeniedException ade) {
            log.warn("The current session does not have sufficient access to retrieve the parent node for {}", node.toString());
            log.warn("{}", ade.getMessage());
            String hi="sdf";
            return null;
        }
        return node;
    }

    public static boolean isDestinationPropertyValid(String destinationProperty, Logger log) {
        String destinationPath = WorkflowUtils.getPath(destinationProperty);
        String destinationName = WorkflowUtils.getName(destinationProperty);

        if (destinationName.equals("")) {
            log.warn("Invalid destination property name given for {}. Property will not be updated.", destinationProperty);

            return false;
        }
        if (destinationPath.equals("")) {
            log.warn("Write-protected or invalid destination property name given for {}. Property will not be updated.", destinationProperty);

            return false;
        }
        return true;
    }

    /**
     * Return the user who completed the last step.
     * @param workflowSession
     * @param workItem
     * @param log
     * @return
     */
    public static String previousParticipant(WorkflowSession workflowSession, WorkItem workItem, Logger log) {
        String found = "";
        try {
            List<HistoryItem> history = workflowSession.getHistory(workItem.getWorkflow());
            for (int index = history.size() - 1; index >= 0; index--) {
                HistoryItem previous = history.get(index);
                if (previous != null && previous.getUserId() != null) {
                    String previousUser = previous.getUserId();
                    if (!previousUser.equalsIgnoreCase("workflow-service")) {
                        found = previousUser;
                        break;
                    }
                }
            }
        } catch (WorkflowException ex) {
            log.error("{}", ex.getMessage());
        }

        // If we're unable to find a "previous participant", assign to workflow-administrators
        if (found.equals("")) {
            found = "workflow-administrators";
        }

        return found;
    }
}
