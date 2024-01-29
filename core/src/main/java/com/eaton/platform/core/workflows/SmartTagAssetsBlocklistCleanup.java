package com.eaton.platform.core.workflows;

/**
 * SmartTagAssetsBlocklistCleanup.java
 * --------------------------------------
 * Given a list of blocklisted smart tags, removes those tags from given asset node if they exist.
 * The Blocklist Smart Tags namespace is /content/cq:tags/fm-smart-tag-blocklist by default. To use a different
 * namespace, provide input. e.g. If input is "blocklist", then the namespace will be /content/cq:tags/blocklist instead.
 *
 * By: Julie Prendergast (julie@freedomdam.com) 7/10/2020
 * --------------------------------------
 */

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.util.WorkflowUtils;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.day.cq.tagging.TagConstants.NT_TAG;
import static com.day.cq.tagging.TagConstants.PN_TAGS;

@Component(
        service = { WorkflowProcess.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Removes blocklisted smart tags from asset.",
                Constants.SERVICE_VENDOR + "=" + "Freedom Marketing",
                "process.label" + "=" + "FM - Smart Tag Assets Blocklist Cleanup"
        },
        immediate = true
)
public class SmartTagAssetsBlocklistCleanup implements WorkflowProcess {

    protected final Logger logger = LoggerFactory.getLogger(SmartTagAssetsBlocklistCleanup.class);
    private final String TAGS_ROOT_REL_PATH = "content/" + PN_TAGS;
    private final String SMART_TAGS_REL_PATH = "jcr:content/metadata/predictedTags";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) {
        logger.debug("---------- Smart Tag Assets Blocklist Cleanup ----------");
        WorkflowUtils.logWorkflowMetadata(logger, workItem, metaDataMap);
        String input = metaDataMap.get("PROCESS_ARGS", String.class);
        String blocklistSmartTagsRelPath = TAGS_ROOT_REL_PATH;
        // If an optional Smart Tag Blocklist node name is provided, use it. Otherwise, use the default.
        if (input != null && !input.trim().equals("")) {
            blocklistSmartTagsRelPath += "/" + input;
        } else {
            blocklistSmartTagsRelPath += "/fm-smart-tag-blocklist";
        }
        try {
            Session session = workflowSession.adaptTo(Session.class);
            Node root = session.getRootNode();
            Node blocklistSmartTagsNode = null;
            // Get the Smart Tag Blocklist tag namespace, or create it if it does not exist.
            if (!root.hasNode(blocklistSmartTagsRelPath)) {
                if (!root.hasNode(TAGS_ROOT_REL_PATH)) {
                    logger.error("Unable to access node {}. Either this node does not exist or this workflow system user {} does not have permission to access it. "
                            + "Blocklisted Smart Tags will not be removed.", TAGS_ROOT_REL_PATH, workItem.getWorkflow().getInitiator());
                    return;
                }
                // Create the Smart Tag Blocklist Tag Namespace and exit. Nothing more to do since there currently are not blocklisted tags.
                blocklistSmartTagsNode = root.addNode(blocklistSmartTagsRelPath, NT_TAG);
                blocklistSmartTagsNode.setProperty(JCR_TITLE, "Smart Tag Blocklist");
                logger.info("Created Smart Tag Blocklist Tag Namespace: {}", blocklistSmartTagsRelPath);
                session.save();
                return;
            }
            blocklistSmartTagsNode = root.getNode(blocklistSmartTagsRelPath);
            if (!blocklistSmartTagsNode.hasNodes()) {
                logger.debug("No blocklisted Smart Tags found at {}.", blocklistSmartTagsNode.getPath());
                return;
            }
            // Get the asset node.
            Node payloadNode = WorkflowUtils.getPayloadNode(root, workItem);
            Node assetNode = WorkflowUtils.getAssetNode(payloadNode, logger);
            logger.debug("payloadNode: {}", payloadNode.getPath());
            if (assetNode == null) {
                logger.error("Unable to find asset node for {}. Blocklisted Smart Tags will not be removed.", payloadNode.getPath());
                return;
            }
            logger.debug("assetNode: {}", assetNode.getPath());
            // Get the asset's smart tags.
            if (!assetNode.hasNode(SMART_TAGS_REL_PATH)) {
                logger.info("Unable to find smart tags for {}. Blocklisted Smart Tags will not be removed.", payloadNode.getPath());
                return;
            }
            Node smartTagsNode = assetNode.getNode(SMART_TAGS_REL_PATH);
            NodeIterator smartTagNodeIt = smartTagsNode.getNodes();
            while (smartTagNodeIt.hasNext()) {
                Node smartTagNode = smartTagNodeIt.nextNode();
                if (!smartTagNode.hasProperty("name")) {
                    logger.error("Smart Tag node {} does not have required 'name' property. Skipping this Smart Tag.", payloadNode.getPath());
                    continue;
                }
                String smartTagName = smartTagNode.getProperty("name").getString();
                NodeIterator blocklistSmartTagNodeIt = blocklistSmartTagsNode.getNodes();
                while (blocklistSmartTagNodeIt.hasNext()) {
                    Node blocklistSmartTagNode = blocklistSmartTagNodeIt.nextNode();
                    if (!blocklistSmartTagNode.hasProperty(JCR_TITLE)) {
                        logger.error("Blocklisted Smart Tag {} does not have required '{}' property. Skipping this Blocklisted Smart Tag.", blocklistSmartTagNode.getPath(), JCR_TITLE);
                        continue;
                    }
                    String blocklistSmartTagTitle = blocklistSmartTagNode.getProperty(JCR_TITLE).getString();
                    logger.debug("Comparing Smart Tag name '{}' to Blocklisted Smart Tag title: '{}'", smartTagName, blocklistSmartTagTitle);
                    if (smartTagName.equalsIgnoreCase(blocklistSmartTagTitle)) {
                        smartTagNode.remove();
                        session.save();
                        logger.info("Blocklisted Smart Tag '{}' removed from {}.", smartTagName, assetNode.getPath());
                    }
                }
            }
        } catch (RepositoryException ex) {
            logger.error("{}", ex);
        }
    }
}
