package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.eaton.platform.core.util.WorkflowUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.oak.commons.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;

import static com.day.cq.commons.jcr.JcrConstants.*;
import static com.day.cq.dam.api.DamConstants.DC_FORMAT;
import static com.day.cq.dam.api.DamConstants.NT_DAM_ASSET;

/**
 * We use this workflow step to guarantee that each asset added to the dam has a valid Create Date metadata value
 */
@Component(
        service = { WorkflowProcess.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Try to ensure the correct Create Date metadata is visible for every asset.",
                Constants.SERVICE_VENDOR + "=" + "Freedom Marketing",
                "process.label" + "=" + "FM - Update Create Date Process"
        },
        immediate = true
)
@Designate(ocd = UpdateCreateDateProcess.Config.class)
public class UpdateCreateDateProcess implements WorkflowProcess {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ObjectClassDefinition(
            name = "FM - Update Create Date Process",
            description = "Try to ensure the correct Create Date metadata is visible for every asset."
    )
    @interface Config {
        @AttributeDefinition(
                type = AttributeType.STRING,
                name = "Target Property",
                description = "Asset metadata property to contain the created date."
        )
        String target_property() default "";

        @AttributeDefinition(
                type = AttributeType.STRING,
                name = "MIME Mappings",
                description = "Define mappings from a MIME type (dc:format) to a metadata property containing its created date. (i.e. - application/vnd.openxmlformats-officedocument.wordprocessingml.document=dam:created)",
                cardinality = Integer.MAX_VALUE
        )
        String[] mime_mappings() default "";
    }

    private Config config;

    private static final String DEFAULT_TARGET_PROPERTY = "xmp:CreateDate";
    private static final String DEFAULT_CREATE_DATE_PROPERTY = JCR_CREATED;
    private static final String MIME_TYPE_PROPERTY = DC_FORMAT;

    private String targetProperty = DEFAULT_TARGET_PROPERTY;
    private HashMap<String,String> mimeMappings = new HashMap<String,String>();


    @Activate
    @Modified
    protected void activate(Config config) {
        this.config = config;
        this.targetProperty = getTargetProperty(config);
        this.mimeMappings = getMimeMappings(config);
    }


    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        logger.debug("---------- Executing Update Create Date Process ----------");
        WorkflowUtils.logWorkflowMetadata(logger, workItem, metaDataMap);

        try {
            if(StringUtils.equals(workItem.getWorkflowData().getPayloadType(), "JCR_PATH")) {
                Session session = workflowSession.adaptTo(Session.class);
                Node root = session.getRootNode();
                Node payloadNode = WorkflowUtils.getPayloadNode(root, workItem);
                Node assetNode = WorkflowUtils.getAssetNode(payloadNode, logger);
                logger.debug("Asset Node: {}", assetNode.getPath());

                Node metadataNode = assetNode.getNode(JCR_CONTENT + "/metadata");
                logger.debug("Metadata Node: {}", metadataNode.getPath());
                setMetadata(metadataNode, assetNode, session);
            }//if
        }//try
        catch(RepositoryException e) {
            logger.error("{}", e.getMessage());
        }//catch
    }//end method execute

    private void setMetadata(Node metadataNode, Node assetNode, Session session) throws RepositoryException {
        if(! metadataNode.hasProperty(this.targetProperty)) {
            if(metadataNode.hasProperty(MIME_TYPE_PROPERTY)) {
                //If the asset has the MIME type property, then decide whether we use the date
                //found in a MIME-type-specific property or the default date.
                String assetMimeType = metadataNode.getProperty(MIME_TYPE_PROPERTY).getString();
                if(this.mimeMappings.containsKey(assetMimeType)) {
                    if(metadataNode.hasProperty(this.mimeMappings.get(assetMimeType))) {
                        metadataNode.setProperty(this.targetProperty, metadataNode.getProperty(this.mimeMappings.get(assetMimeType)).getDate());
                        session.save();
                    }//if
                    else {
                        logger.warn("Couldn't find property {} on node {}", this.mimeMappings.get(assetMimeType), metadataNode.getPath());
                    }//else
                }//if
                else {
                    if(assetNode.hasProperty(DEFAULT_CREATE_DATE_PROPERTY)) {
                        metadataNode.setProperty(this.targetProperty, assetNode.getProperty(DEFAULT_CREATE_DATE_PROPERTY).getDate());
                        session.save();
                    }//if
                    else {
                        logger.warn("Couldn't find property {} on node {}", DEFAULT_CREATE_DATE_PROPERTY, assetNode.getPath());
                    }//else
                }//else
                session.save();
            }//if
        }//if
    }

    private String getTargetProperty(final Config config) {
        String targetProperty = PropertiesUtil.toString(config.target_property(), "");
        if(targetProperty.equals("")) {
            targetProperty = this.targetProperty;
        }//if
        return targetProperty;
    }//end method getTargetProperty


    private HashMap<String,String> getMimeMappings(final Config config) {
        HashMap<String,String> retVal = new HashMap<String,String>();
        final String[] mimeMappings = PropertiesUtil.toStringArray(config.mime_mappings(), new String[0]);
        for(String mapping : mimeMappings) {
            String[] tokens = mapping.split("=");
            if(tokens.length >= 2) {
                String mimeType = tokens[0];
                String createProp = tokens[1];
                retVal.put(mimeType, createProp);
                if(tokens.length > 2) {
                    logger.warn("Created a mapping, but received more tokens than expected from mapping string: {}", mapping);
                }//if
            }//if
            else {
                logger.warn("Couldn't create mapping. Not enough tokens in mapping string: {}", mapping);
            }//else
        }//for

        return retVal;
    }//end method getMimeMappings


}//end class UpdateCreateDateProcess
