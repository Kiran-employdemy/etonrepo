package com.eaton.platform.core.workflows;
/**
 * NormalizeAssetNodeProperties.java
 * ------------------
 * Given a set of source properties and their associated destination property(m:1), updates the destination
 * property's value according to the following specifications:
 * <p>
 * If nothing is given as a source property, then remove the destination property.
 * If an empty string is given as a source property (e.g. ""), then set the destination property value to empty.
 * If a string value is given as a source property, then set the destination property value to this value.
 * If multiple source properties are given, then set the destination property value to the source property value of the first one found to exist (priority).
 * If a single source property is given, then set the destination property value to the source property value.
 * If the destination property is prefixed by '?', then set the destination property value to the source property value
 * if it is empty or does not currently exist.
 * <p>
 * Input Format:
 * ::destination_property_1,
 * ""::destination_property_2,
 * "some value"::destination_property_3,
 * source_property_1||source_property_2||source_property_n::destination_property_4,
 * source_property_1::destination_property_n
 * "some value"::?destination_property_if_empty
 * <p>
 * Input Example:
 * ::jcr:content/metadata/xmp:deleteme,
 * ""::jcr:content/metadata/xmp:makemeblank,
 * "New Creator"::jcr:content/metadata/dc:creator,
 * jcr:content/metadata/idontexist||jcr:content/metadata/dc:description::jcr:content/metadata/xmp:someProperty,
 * jcr:content/metadata/dc:description::jcr:content/metadata/xmp:someProperty,
 * "New Creator"::?jcr:content/metadata/dc:creator
 * <p>
 * Additionally, if the normalization is to be applied to a group of assets specified by a property of the workflow
 * instance metadata, you may name the property to tell the process which assets need to be normalized. Do this
 * according to the following specifications:
 * <p>
 * If no property is specified, then the process will normalize the payload asset by default
 * Provide the name of the property before the rest of the string surrounded by square brackets
 * <p>
 * Input Format:
 * [workflow_instance_metadata_property]::destination_property_1,
 * [workflow_instance_metadata_property]""::destination_property_2,
 * [workflow_instance_metadata_property]"some value"::destination_property_3,
 * [workflow_instance_metadata_property]source_property_1||source_property_2||source_property_n::destination_property_4,
 * [workflow_instance_metadata_property]source_property_1::destination_property_n
 * <p>
 * Input Example:
 * [SELECTED_ASSETS]::jcr:content/metadata/xmp:deleteme,
 * [SELECTED_ASSETS]""::jcr:content/metadata/xmp:makemeblank,
 * [SELECTED_ASSETS]"New Creator"::jcr:content/metadata/dc:creator,
 * [SELECTED_ASSETS]jcr:content/metadata/idontexists||jcr:content/metadata/dc:description::jcr:content/metadata/xmp:someProperty,
 * [SELECTED_ASSETS]jcr:content/metadata/dc:description::jcr:content/metadata/xmp:someProperty
 * ------------------
 * <p>
 * By: Julie Rybarczyk (julie@freedomdam.com)
 * Modified by: Jay LeCavalier (jay@freedomdam.com) on 6/28/2017
 */

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.WorkflowUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.day.cq.dam.api.DamConstants.DC_CREATOR;
import static com.day.cq.dam.api.DamConstants.METADATA_FOLDER;


@Component(
        service = WorkflowProcess.class ,
        property = {
                Constants.SERVICE_DESCRIPTION  + "=" + "Normalize an asset type node's properties given arguments.",
                Constants.SERVICE_VENDOR+ "=" +  "Freedom Marketing",
                "process.label" + "="+  "FM - Normalize Asset Node Properties"
        },
        immediate = true
)
public class NormalizeAssetNodeProperties implements WorkflowProcess {

    protected final Logger log = LoggerFactory.getLogger(NormalizeAssetNodeProperties.class);

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        log.debug("---------- Executing Normalize Asset Node Properties Process ----------");
        WorkflowUtils.logWorkflowMetadata(log, workItem, args);

        try {
            Session session = workflowSession.adaptTo(Session.class);
            String input = args.get("PROCESS_ARGS", String.class);

            // Exit if arguments are not provided.
            if(input == null || input.trim().equals("")) { return; }

            List<String> propertyMappings = getPropertyMappings(input);
            for (String propertyMappingStr : propertyMappings) {
                log.debug("Evaluating Mapping: {}", propertyMappingStr);
                List<Node> assetNodes = getAssetNodes(propertyMappingStr, session, workItem);
                updateMetadata(assetNodes, propertyMappingStr.replaceAll("\\[.*\\]", ""), session);
            }
        } catch (RepositoryException e) {
            log.error("{}", e);
        }
    }

    private List<String> getPropertyMappings(String input) {
        // Get the property mappings as specified by the arguments input given.
        String[] propertyMappingsArray = input.trim().split(",");
        List<String> propertyMappings = new ArrayList<String>();
        // Evaluate the format of each property mapping string, and determine if it should be added to the property mappings
        // list that will be processed for the asset type node.
        // If the format is invalid, log a warning, and do not add it to the list. Otherwise, add it to the list.
        for (String propertyMappingStr : propertyMappingsArray) {
            String[] propertyMapping = propertyMappingStr.split("::");
            if (propertyMapping.length != 2) {
                log.warn("Invalid format for {}. Property will not be updated.", propertyMappingStr);
            } else {
                // String has correct format with respect to ::
                String destinationProperty = propertyMapping[1].trim();
                if (!WorkflowUtils.isDestinationPropertyValid(destinationProperty, log)) {
                    log.warn("{} is not a valid destination property.", destinationProperty);
                } else {
                    // Destination property is valid
                    String sourcePropertiesStr = propertyMapping[0].trim();
                    String[] sourceProperties = sourcePropertiesStr.split("\\|\\|");

                    boolean flag = validateProperty(sourceProperties);
                    if (!flag) {
                        propertyMappings.add(propertyMappingStr);
                    }
                }
            }
        }
        return propertyMappings;
    }

    @SuppressWarnings("squid:S2583")
    private List<Node> getAssetNodes(String propertyMappingStr, Session session, WorkItem workItem) throws RepositoryException {

        List<Node> assetNodes = new ArrayList<Node>();

        if (propertyMappingStr.matches("^\\[.*\\].*")) {

            // Make sure the user only specified one asset source
            if (StringUtils.countMatches(propertyMappingStr, "[") == 1 &&
                    StringUtils.countMatches(propertyMappingStr, "]") == 1) {
                String property = propertyMappingStr.substring(propertyMappingStr.indexOf("[") + 1,
                        propertyMappingStr.indexOf("]"));
                log.debug("Getting assets from property {}", property);

                MetaDataMap wfMetadata = workItem.getWorkflowData().getMetaDataMap();
                if (wfMetadata.containsKey(property)) {
                    String assets = wfMetadata.get(property).toString();
                    assetNodes = getAssetNodes(assets, session);

                } else {
                    log.warn("The workflow instance metadata contained no such property: {}", property);
                    return assetNodes;
                }
            } else {
                log.warn("{} was not properly formatted", propertyMappingStr);
                return assetNodes;
            }

        } else {

            // Get the asset type node to update.
            Node payloadNode = WorkflowUtils.getPayloadNode(session.getRootNode(), workItem);
            Node assetNode = WorkflowUtils.getAssetNode(payloadNode, log);

            if (assetNode == null) {
                log.error("Unable to find asset node for {}. Node will not be updated.", payloadNode.getPath());
                return assetNodes;
            }
            log.debug("payloadNode: {}", payloadNode.getPath());
            log.debug("assetNode: {}", assetNode.getPath());

            assetNodes.add(assetNode);
        }

        return assetNodes;
    }

    private void updateMetadata(List<Node> assetNodes, String propertyMappingStr, Session session) throws RepositoryException {
        // Update the asset type node given the filtered property mappings list.
        for (Node assetNode : assetNodes) {
			updateProducTaxonomyExtended(assetNode);

            String[] propertyMapping = propertyMappingStr.split("::");

            String destinationProperty = propertyMapping[1].trim();
            boolean conditionalModify = false;
            // If the destination property is prefixed by '?', then it will only be modified if it is empty or does not
            // currently exist.
            if (destinationProperty.charAt(0) == '?') {
                destinationProperty = destinationProperty.substring(1);
                conditionalModify = true;
            }
            String destinationPath = WorkflowUtils.getPath(destinationProperty);
            String destinationName = WorkflowUtils.getName(destinationProperty);
            String sourcePropertiesStr = propertyMapping[0].trim();
            boolean doModify = true;

            // This means the user prefixed the destination property with a '?'
            if (conditionalModify) {
                // Check whether the property already has a value or not
                if (assetNode.getNode(destinationPath).hasProperty(destinationName)) {
                    doModify = assetNode.getNode(destinationPath).getProperty(destinationName).getString().equals("");
                }
            }

            if (doModify) {
                if (sourcePropertiesStr.equals("")) {
                    // If the destination property exists, delete it.
                    log.debug("Removing property: {}", destinationProperty);
                    if (assetNode.getNode(destinationPath).hasProperty(destinationName)) {
                        assetNode.getNode(destinationPath).getProperty(destinationName).remove();
                    }
                    continue;
                }

                String[] sourceProperties = sourcePropertiesStr.split("\\|\\|");

                boolean destinationValueUpdated = false;
                for (String sourceProperty : sourceProperties) {
                    sourceProperty = sourceProperty.trim();
                    destinationValueUpdated = updateSourceProperty(sourceProperty, destinationName, destinationPath, session, assetNode);
                }
                if (!destinationValueUpdated) {
                    log.warn("source property does not exist for {}. Property will not be updated.", propertyMappingStr);
                }
            }
            if (session.hasPendingChanges()) {
                session.save();
            }
        }
    }

    /**
     * Given the destination property name, return true if this property should be a multi-value type property
     * regardless of what the source property type is.
     * The destinationNames list specifies the different destination property names where this could be true.
     * These are special cases.
     *
     * @param destinationName The destination property name. e.g. "xmp:some-name"
     * @return True if this property should be a multi-value type property; False otherwise.
     */
    private boolean isDestinationValueTypeMultivalue(String destinationName) {
        List<String> destinationNames = new ArrayList<String>(Arrays.asList(DC_CREATOR));
        return destinationNames.contains(destinationName);
    }

    private boolean updateSourceProperty(String sourceProperty, String destinationName, String destinationPath, Session session, Node assetNode) throws RepositoryException {
        if (sourceProperty.startsWith("\"") && sourceProperty.endsWith("\"")) {
            // Set the destination property value to the given string.
            String value = sourceProperty.substring(1, sourceProperty.length() - 1);
            log.debug("Setting {} to {}.", destinationName, value);
            session.refresh(false);
            assetNode.getNode(destinationPath).setProperty(destinationName, value);
            session.save();
            return true;
        }

        String sourcePath = WorkflowUtils.getPath(sourceProperty);
        String sourceName = WorkflowUtils.getName(sourceProperty);

        if (assetNode.getNode(sourcePath).hasProperty(sourceName)) {

            if (assetNode.getNode(sourcePath).getProperty(sourceName).isMultiple()) {
                // The property is multi-valued.
                Value[] values = assetNode.getNode(sourcePath).getProperty(sourceName).getValues();
                if (values.length > 0) {
                    log.debug("Setting {} to {}.", destinationName, values.toString());
                    session.refresh(false);
                    assetNode.getNode(destinationPath).setProperty(destinationName, values, values[0].getType());
                    session.save();
                }
            } else {
                // The property is a single value.
                Value value = assetNode.getNode(sourcePath).getProperty(sourceName).getValue();
                log.debug("Setting {} to {}.", destinationName, value.toString());
                if (isDestinationValueTypeMultivalue(destinationName)) {
                    log.debug("The property source is a single value, but the destination will be a multi-value.");
                    String[] valueArray = {value.toString()};
                    session.refresh(false);
                    assetNode.getNode(destinationPath).setProperty(destinationName, valueArray);
                    session.save();
                } else {
                    session.refresh(false);
                    assetNode.getNode(destinationPath).setProperty(destinationName, value, value.getType());
                    session.save();
                }
            }
            return true;
        }
        return false;
    }

    private static boolean validateProperty(String[] sourceProperties) {
        for (String sourceProperty : sourceProperties) {
            sourceProperty = sourceProperty.replaceAll("\\[.*\\]", "");
            if (!sourceProperty.equals("")
                    && !(sourceProperty.startsWith("\"") && sourceProperty.endsWith("\""))
                    && WorkflowUtils.getName(sourceProperty).equals("")) {
                return true;
            }
        }
        return false;
    }

    private List<Node> getAssetNodes(String assets, Session session) throws RepositoryException {
        List<Node> assetNodes = new ArrayList<Node>();
        for (String assetPath : assets.split(",")) {
            assetPath = assetPath.trim();
            Node assetNode = session.getNode(assetPath);

            if (assetNode == null) {
                log.error("Unable to find asset node for {}. Nodes will not be updated.", assetPath);
                return assetNodes;
            }
            log.debug("assetNode: {}", assetNode.getPath());

            assetNodes.add(assetNode);
        }
        return assetNodes;
    }


    private void updateProducTaxonomyExtended(Node assetNode){
        try {
			if (assetNode.hasNode(Node.JCR_CONTENT.concat(CommonConstants.SLASH_STRING).concat(METADATA_FOLDER))) {
				Node metadata = assetNode.getNode(Node.JCR_CONTENT.concat(CommonConstants.SLASH_STRING).concat(METADATA_FOLDER));
				String productTaxonomyTag = "xmp:eaton-".concat(CommonConstants.TAG_NAMESPACE_PRODUCT_TAXONOMY);
				if (metadata.hasProperty(productTaxonomyTag)) {
					Value[] values = metadata.getProperty(productTaxonomyTag).getValues();
					List<String> categoryList = getTagList(values);
					String[] newCategories = categoryList.toArray(new String[categoryList.size()]);
				}
				if(metadata.getSession().hasPendingChanges()) {
					metadata.getSession().save();
				}
			}
        }catch(RepositoryException e){
            log.error("Error while trying to update node: {} {}", e.getMessage(), e);
        }
    }

	private boolean isTagDuplicate(List<String> tagList, String sampleTag){
		final String TAG_NAMESPACE_PRODUCT_TAXONOMY = "eaton:product-taxonomy";
		if(sampleTag.equals(TAG_NAMESPACE_PRODUCT_TAXONOMY)){
			return true;
		}
		for(String tag:tagList){
			if(tag.equals(sampleTag)){
				return true;
			}
		}
		return false;
	}

	private List<String> getTagList(Value[] values){
		List<String> tagList = new ArrayList<>();
		for(int i=0;i< values.length;i++){
			try {
				String[] categories = values[i].getString().split(CommonConstants.SLASH_STRING);
				String categoryChain = "";
				for(String c:categories){
					if(categoryChain.length()==0) {
						categoryChain = c;
					}else{
						categoryChain = categoryChain.concat(CommonConstants.SLASH_STRING).concat(c);
					}
					if(!isTagDuplicate(tagList, categoryChain)) {
						tagList.add(categoryChain);
					}
				}
			}catch (Exception e){
				log.error("Error while trying to get values: {} {}", e.getMessage(), e);
			}

		}
		return tagList;
	}
}