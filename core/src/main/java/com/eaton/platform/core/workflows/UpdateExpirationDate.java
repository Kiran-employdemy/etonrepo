package com.eaton.platform.core.workflows;
/**
 * UpdateExpirationDate.java
 * --------------------------------------
 * This class represents a workflow process step. When executed, the date selected by the asset owner will be
 * set to the expiration date for the payload asset.
 * --------------------------------------
 * Author: Jay LeCavalier (jay@freedomdam.com)
 */

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import com.eaton.platform.core.util.WorkflowUtils;
import org.osgi.framework.Constants;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Component(
        service= {WorkflowProcess.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Given a date, set the expiration date of the payload asset to that date.",
                Constants.SERVICE_VENDOR + "=" + "Eaton",
                "process.label" + "=" + "EATON - Update Expiration Date"
        },
        immediate = true
)
public class UpdateExpirationDate implements WorkflowProcess {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String WORKFLOW_FIELD_NAME = "NEW_EXPIRATION_DATE";
    private static final String EXPIRATION_DATE_PROPERTY = "prism:expirationDate";

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        logger.debug("---------- Executing Eaton - Update Expiration Date ----------");

        MetaDataMap wfMetadata = workItem.getWorkflowData().getMetaDataMap();

        Session session = workflowSession.adaptTo(Session.class);

        try {
            Node payloadNode = WorkflowUtils.getPayloadNode(session.getRootNode(), workItem);
            Node assetMetadata = session.getNode(payloadNode.getPath() + "/" + "jcr:content/metadata");

            if (wfMetadata.containsKey(WORKFLOW_FIELD_NAME)) {
                String value = (String) wfMetadata.get(WORKFLOW_FIELD_NAME); // e.g. 2018-01-26T00:00:00.000-06:00 yyyy-MM-dd'T'HH:mm:ss.SSSXXX
                if ("".equals(value)) {
                    assetMetadata.setProperty(EXPIRATION_DATE_PROPERTY, (Value) null, PropertyType.DATE);
                    logger.info("User did not select an expiration date. Expiration date has been cleared.");
                } else {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                    cal.setTime(format.parse(value));
                    assetMetadata.setProperty(EXPIRATION_DATE_PROPERTY, cal);
                }
                session.save();
            } else {
                logger.error("Expiration date was not set by workflow!");
            }
        } catch (RepositoryException | ParseException e) {
            logger.error("{}", e.getMessage());
        }
        logger.debug("---------- Completed Eaton - Update Expiration Date Process ----------");
    }
}