package com.eaton.platform.core.workflows;

/**
 * SetDcFormat.java
 * --------------------------------------
 * This class sets the dc:format property to asset metadata if it is not set by evaluating the file's extension.
 * The 'dam/content/predicates/mimetypes' node must be overlayed under '/apps'.
 * The node must be overlayed with the 'extension' property and it's value added to each
 * mimetype node in order for this process step to execute properly.
 * This process step is only needed if the 'file type hierarchy' search predicate is being used.
 *
 * As of 6.5 the File Type Hierarchy search predicate is deprecated for OOTB File Type Predicate
 * --------------------------------------
 * Author: Julie Rybarczyk (julie@freedomdam.com)
 **/

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.util.WorkflowUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.ArrayList;
import java.util.List;

import static com.day.cq.dam.api.DamConstants.DC_FORMAT;

@Component(
        service = { WorkflowProcess.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Workflow step to set dc:format to asset metadata.",
                Constants.SERVICE_VENDOR + "=" + "Freedom Marketing",
                "process.label" + "=" + "FM - Set dc:format property"
        },
        immediate = true
)
public class SetDcFormat implements WorkflowProcess {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    @SuppressWarnings("CQRules:CQBP-71")
    private static final String MIME_TYPES_NODE_REL_PATH = "/apps/dam/content/predicates/mimetypes";

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        log.debug("---------- Executing: Set {} process ----------", DC_FORMAT);
        WorkflowUtils.logWorkflowMetadata(log, workItem, metaDataMap);
        ResourceResolver rr = workflowSession.adaptTo(ResourceResolver.class);
        Session session = rr.adaptTo(Session.class);

        try {
            // Get the asset node.
            String payloadPath = workItem.getWorkflowData().getPayload().toString();
            Node thisAssetNode = rr.getResource(payloadPath).adaptTo(Node.class);
            thisAssetNode = WorkflowUtils.getAssetNode(thisAssetNode, log);
            String thisAssetNodePath = thisAssetNode.getPath();
            log.debug("This asset node path: {}", thisAssetNodePath);

            // Get the extension of the asset node (e.g. jpg, gif, etc.)
            int startPos = thisAssetNodePath.lastIndexOf(".") + 1;
            if (startPos == 0) {
                log.warn("Unable to identify the extension for node {}.", thisAssetNodePath);
                return;
            }
            String extension = thisAssetNodePath.substring(startPos, thisAssetNodePath.length());
            log.debug("Extension for node {} is '{}'.", thisAssetNodePath, extension);

            // Get the extension's dc:format values from the mimetype node under /apps if it exists.
            // Otherwise, exit. The mimetypes under /libs do not have the 'extension' property which is needed.
            Node mimeTypesNode = null;
            if (session.nodeExists(MIME_TYPES_NODE_REL_PATH)) {
                mimeTypesNode = session.getNode(MIME_TYPES_NODE_REL_PATH);
            }
            else {
                log.error("The 'dam/content/predicates/mimetypes' node is not overlayed under '/apps'."
                + " The node must be overlayed with the 'extension' property and it's value added to each"
                + " mimetype node in order for this process step to execute properly. Exiting.");
                return;
            }

            String dcFormatNewValue = getDcFormatNewValue(mimeTypesNode, extension);
            if ("".equals(dcFormatNewValue)) {
                log.debug("{} property value does not need to be set for this extension type. Exiting.", DC_FORMAT);
                return;
            }

            thisAssetNode = thisAssetNode.getNode("jcr:content/metadata");
            boolean hasDcFormatProperty = thisAssetNode.hasProperty(DC_FORMAT);
            String dcFormatCurrentValue = "";

            if (hasDcFormatProperty) {
                dcFormatCurrentValue = thisAssetNode.getProperty(DC_FORMAT).getValue().getString().trim();
                log.debug("The asset node has the dc:format property.");
            }

            if(!hasDcFormatProperty || "".equals(dcFormatCurrentValue)) {
                // The asset metadata does not have the dc:format property, or it does, but the value is blank.
                // Set the property and its value.
                log.debug("Setting node {} {} property value to {}.", thisAssetNodePath, DC_FORMAT, dcFormatNewValue);
                thisAssetNode.setProperty(DC_FORMAT, dcFormatNewValue);
            }
            else if (hasDcFormatProperty && !dcFormatNewValue.equals(dcFormatCurrentValue)){
                log.warn("The asset metadata {} property's actual value of '{}' does not match the expected value '{}'.",
                        DC_FORMAT, dcFormatCurrentValue, dcFormatNewValue);
            }
            else {
                log.debug("The node has the dc:format property and its actual value matches the expected value.");
            }
            session.save();
        }
        catch(RepositoryException e) {
            log.error("{}", e);
        }
    }

    /**
     * Gets the dc:format value based on the asset node's extension.
     * @param node The mimetypes root node.
     * @param extension The asset node's extension.
     * @return The new dc:format value if found. Otherwise, return an empty string.
     */
    private String getDcFormatNewValue(Node node, String extension) {
        try {
            if (node.hasProperty("extension") && node.hasProperty("value")) {
                List<String> nodeExtensions = new ArrayList<String>();
                if (node.getProperty("extension").isMultiple()) {
                    Value[] nodeExtensionValues = node.getProperty("extension").getValues();
                    for (Value nodeExtensionValue : nodeExtensionValues) {
                        nodeExtensions.add(nodeExtensionValue.getString());
                    }
                } else {
                    nodeExtensions.add(node.getProperty("extension").getString());
                }
                for (String nodeExtension : nodeExtensions) {
                    if (nodeExtension.equalsIgnoreCase(extension)) {
                        return node.getProperty("value").getString();
                    }
                }
            }
            if (node.hasNodes()) {
                NodeIterator nodeIter = node.getNodes();
                while (nodeIter.hasNext()) {
                    String dcFormatNewValue = getDcFormatNewValue(nodeIter.nextNode(), extension);
                    if (!"".equals(dcFormatNewValue)) {
                        return dcFormatNewValue;
                    }
                }
            }
        } catch (RepositoryException e) {
            log.error("{}", e);
        }
        return "";
    }
}
