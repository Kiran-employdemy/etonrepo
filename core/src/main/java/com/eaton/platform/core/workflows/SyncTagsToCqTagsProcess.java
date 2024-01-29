package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.constants.TagMap;
import com.eaton.platform.core.util.WorkflowUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.version.VersionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static com.day.cq.wcm.api.NameConstants.PN_TAGS;


@Component(
        service = {WorkflowProcess.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Syncs tags with the cq:tags field.",
                Constants.SERVICE_VENDOR + "=" + "Eaton",
                "process.label" + "=" + "Eaton - cq:tags Sync"
        },
        immediate = true
)
public class SyncTagsToCqTagsProcess implements WorkflowProcess {

    protected final Logger log = LoggerFactory.getLogger(SyncTagsToCqTagsProcess.class);
    private static final String METADATA_NODE_PATH = "jcr:content/metadata";

    private Map<String, String> tagsMap;
    private VersionManager versionManager;

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        log.debug("---------- Executing Sync Tags to cq:tags Process ----------");
        WorkflowUtils.logWorkflowMetadata(log, workItem, metaDataMap);
        try {
            Session session = workflowSession.adaptTo(Session.class);
            Node root = session.getRootNode();
            Node payloadNode = WorkflowUtils.getPayloadNode(root, workItem);
            Node assetNode = WorkflowUtils.getAssetNode(payloadNode, log);
            if (assetNode != null) {
                versionManager = assetNode.getSession().getWorkspace().getVersionManager();
            } else {
                log.error("Asset node is null. Exiting process.");
                return;
            }

            Node assetMetadataNode = assetNode.getNode(METADATA_NODE_PATH);
            ArrayList<Value> tagsToAddToCqTags = new ArrayList<>();
            ArrayList<Value> tagsToRemoveFromCqTags = new ArrayList();
            ArrayList<Value> currentCqTags = new ArrayList<>();
            //get the tags in cq:tags
            if (assetMetadataNode.hasProperty(PN_TAGS)) {
                if (assetMetadataNode.getProperty(PN_TAGS).isMultiple()) {
                    currentCqTags.addAll(Arrays.asList(assetMetadataNode.getProperty(PN_TAGS).getValues()));
                } else {
                    currentCqTags.add(assetMetadataNode.getProperty(PN_TAGS).getValue());
                }
            }
            // grab all tags that are set on individual metadata properties
            ArrayList<Value> currentTags = getCurrentCqTags(assetMetadataNode);
            Map<String, String> tagSyncList = new TagMap().getTagSyncList();
            // check if the tags are in cq:tags
            for (Value currentTagValue : currentTags) {
                if (!currentCqTags.contains(currentTagValue)) {
                    tagsToAddToCqTags.add(currentTagValue);
                }
            }
            // check if there are tags to be removed from cq:tags
            if (!currentCqTags.isEmpty()) {
                for (Value cqTagValue : currentCqTags) {
                    // only remove tags under the Eaton namespace on tag sync list
                    if (!currentTags.contains(cqTagValue) && tagSyncList.containsKey(cqTagValue.toString().split("/")[0])) {
                        tagsToRemoveFromCqTags.add(cqTagValue);
                    }
                }
                currentCqTags.removeAll(tagsToRemoveFromCqTags);
            }
            currentCqTags.addAll(tagsToAddToCqTags);
            if (!tagsToAddToCqTags.isEmpty() || !tagsToRemoveFromCqTags.isEmpty()) {
                // Cannot set property if node is checked in.
                boolean isCheckedOut = versionManager.isCheckedOut(assetNode.getPath());
                if (!isCheckedOut) {
                    versionManager.checkout(assetNode.getPath());
                }
                assetMetadataNode.setProperty(PN_TAGS, currentCqTags.toArray(new Value[0]));
            }
            session.save();
        } catch (RepositoryException e) {
            log.error("{}", e.getMessage());
        }
    }

    private static ArrayList<Value> getCurrentCqTags (Node assetMetadataNode) throws RepositoryException {
        ArrayList<Value> currentTags = new ArrayList<>();
        Map<String, String> tagSyncList = new TagMap().getTagSyncList();
        for (String metadataTagsPropertyName : tagSyncList.values()) {
            // grab all tags that are set on individual metadata properties
            if (assetMetadataNode.hasProperty(metadataTagsPropertyName)) {
                if (assetMetadataNode.getProperty(metadataTagsPropertyName).isMultiple()) {
                    currentTags.addAll(Arrays.asList(assetMetadataNode.getProperty(metadataTagsPropertyName).getValues()));
                } else {
                    currentTags.add(assetMetadataNode.getProperty(metadataTagsPropertyName).getValue());
                }
            }
        }
        return currentTags;
    }
}
