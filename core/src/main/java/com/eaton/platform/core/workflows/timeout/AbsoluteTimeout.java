package com.eaton.platform.core.workflows.timeout;
/**
 * AbsoluteTimeout.java
 * --------------------------------------
 * This class adds the TIMEOUT property once the timeout is reached.
 * --------------------------------------
 * Author: Soo Woo (soo@freedomdam.com)
 */

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.job.AbsoluteTimeoutHandler;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.adobe.granite.workflow.model.WorkflowTransition;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

@Component(
        service= {WorkflowProcess.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=" + "Eaton - Absolute Timeout",
                Constants.SERVICE_VENDOR + "=" + "Eaton",
                "process.label" + "=" + "EATON - Absolute Timeout"
        },
        immediate = true
)
public class AbsoluteTimeout implements WorkflowProcess, AbsoluteTimeoutHandler {
    protected final Logger log = LoggerFactory.getLogger(AbsoluteTimeout.class);

    private static final String TIMEOUT = "TIMEOUT";

    public AbsoluteTimeout() {
    }

    public void execute(WorkItem item, WorkflowSession session, MetaDataMap metaData) throws WorkflowException {
        // Inject "TIMEOUT" into metadata
        MetaDataMap wfMetadata = item.getWorkflowData().getMetaDataMap();
        wfMetadata.put(TIMEOUT, "true");

        try {
            boolean advanced = false;
            List<Route> routes = session.getRoutes(item, false);
            Iterator var6 = routes.iterator();

            while(var6.hasNext()) {
                Route route = (Route)var6.next();
                if (route.hasDefault()) {
                    String fromTitle = item.getNode().getTitle();
                    String toTitle = ((WorkflowTransition)route.getDestinations().get(0)).getTo().getTitle();
                    session.complete(item, route);
                    this.log.debug(item.getId() + " advanced from " + fromTitle + " to " + toTitle);
                    advanced = true;
                }
            }

            if (!advanced) {
                session.complete(item, (Route)routes.get(0));
                String fromTitle = item.getNode().getTitle();
                String toTitle = ((WorkflowTransition)((Route)routes.get(0)).getDestinations().get(0)).getTo().getTitle();
                this.log.debug(item.getId() + " advanced from " + fromTitle + " to " + toTitle);
            }
        } catch (WorkflowException var10) {
            this.log.error("Could not advance workflow.", var10);
        }
    }

    public long getTimeoutDate(WorkItem workItem) {
        Long date = (Long)workItem.getWorkflowData().getMetaDataMap().get("absoluteTime", Long.class);
        return date != null ? date : -1L;
    }
}

