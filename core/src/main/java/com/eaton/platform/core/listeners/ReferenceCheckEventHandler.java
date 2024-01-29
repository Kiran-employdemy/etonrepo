package com.eaton.platform.core.listeners;

import com.day.cq.dam.api.DamEvent;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.ReferenceService;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component(service = EventHandler.class,
        immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Demo to listen on changes in the resource tree",
                EventConstants.EVENT_TOPIC +"="+PageEvent.EVENT_TOPIC,
                EventConstants.EVENT_TOPIC +"="+DamEvent.EVENT_TOPIC,
        })
public class ReferenceCheckEventHandler implements EventHandler {

    @Reference
    private ReferenceService referenceService;

    @Reference
    private JobManager jobManager;

    @Override
    public void handleEvent(Event event) {

        final String eventTopic = event.getTopic();
        if (eventTopic.equals(PageEvent.EVENT_TOPIC)) {
            handlePageEvent(event);
        }
    }

    private void handlePageEvent(final Event event) {
        final PageEvent pageEvent = PageEvent.fromEvent(event);
        final Iterator<PageModification> modification = pageEvent.getModifications();
        while (modification.hasNext()) {
            final PageModification pageModification = modification.next();
            if(null != pageModification && PageModification.ModificationType.MOVED.equals(pageModification.getType())
                    && StringUtils.isNotBlank(pageModification.getPath())
                    && StringUtils.isNotBlank(pageModification.getDestination())) {
                final String currentPath = pageModification.getPath();
                final String destinationPath = pageModification.getDestination();
                final Map<String, Object> props = new HashMap<>();
                props.put(CommonConstants.CURRENT_PATH, currentPath);
                props.put(CommonConstants.DESTINATION_PATH, destinationPath);
                jobManager.addJob(CommonConstants.REFERENCE_UPDATE_JOB_TOPIC, props);

            }
        }
    }


}
