package com.eaton.platform.core.workflows;
/**
 * SetMetadataFromDialog.java
 * --------------------------------------
 * This class represents a workflow process step. When executed, the variables that were set by
 * the previous dialog step in the workflow will be injected into the workflow instance metadata node. This way,
 * the values may be retrieved at later steps in the workflow.
 * --------------------------------------
 * Author: Jay LeCavalier (jay@freedomdam.com)
 * Updated By: Julie Rybarczyk (julie@freedomdam.com) 6/27/2018
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

import javax.jcr.Node;
import javax.jcr.RepositoryException;

@Component(
        service= {WorkflowProcess.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Given properties as PROCESS_ARGS, sets workflow metadata accordingly",
                Constants.SERVICE_VENDOR + "=" + "Eaton",
                "process.label" + "=" + "EATON - Set Metadata from Dialog"
        },
        immediate = true
)
public class SetMetadataFromDialog implements WorkflowProcess {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        logger.debug("---------- Executing Eaton - Set Metadata from Dialog Process ----------");
        WorkflowUtils.logWorkflowMetadata(logger, workItem, metaDataMap);

        String[] processArgs = metaDataMap.get("PROCESS_ARGS", String.class).split(",");
        String historyEntryPath = workItem.getMetaDataMap().get("historyEntryPath", String.class);
        try {
            Node metaDataNode = workflowSession.adaptTo(javax.jcr.Session.class).getNode(historyEntryPath + "/workItem/metaData");
            MetaDataMap wfMetadata = workItem.getWorkflowData().getMetaDataMap();
            for (String arg : processArgs) {
                arg = arg.trim();
                if (metaDataNode.hasProperty(arg)) {
                    if (metaDataNode.getProperty(arg).isMultiple()) {
                        logger.info("Found multiple values for: {}", arg);
                        wfMetadata.put(arg, metaDataNode.getProperty(arg).getValues());
                    } else {
                        logger.info("Found a single value for: {}", arg);
                        wfMetadata.put(arg, metaDataNode.getProperty(arg).getString());
                    }
                }
            }
        } catch (RepositoryException e) {
            logger.error("{}", e.getMessage());
        }
        logger.debug("---------- Completed Eaton - Set Metadata from Dialog Process ----------");
    }
}
