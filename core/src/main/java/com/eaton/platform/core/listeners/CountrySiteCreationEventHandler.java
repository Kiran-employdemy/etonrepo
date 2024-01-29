/**
 *
 * @author ICF
 * On 19th Jan 2021
 * EAT- 4495
 *
 * */
package com.eaton.platform.core.listeners;

import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


/**
 *
 * The handler is used to listen to language page creations
 * and to update caffeine cache with newly created
 * language sites.
 *
 * */
@Component(service = EventHandler.class,
        immediate = true,
        property = {
                AEMConstants.SERVICE_DESCRIPTION + "Demo to listen on changes in the resource tree",
                EventConstants.EVENT_TOPIC + "=" +PageEvent.EVENT_TOPIC,
                EventConstants.EVENT_FILTER + "(&" + "((path=/content/eaton/*/*/jcr:content)(path=/content/eaton-cummins/*/*/jcr:content)"+
                        "(path=/content/greenswitching/*/*/jcr:content)(path=/content/phoenixtec/*/*/jcr:content))"+"(path=/content/login/*/*/jcr:content)" + ")"
        })
public class CountrySiteCreationEventHandler implements EventHandler {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CountrySiteCreationEventHandler.class);
    private static final int PAGE_LENGTH = 5;

    @Reference
    private JobManager jobManager;

    @Reference
    private SlingSettingsService slingSettingsService;

    @Override
    public void handleEvent(Event event) {
        LOGGER.info("HANDLE EVENT CALLED  ::::::::::::::::::");
        LOGGER.debug("CountrySiteCreationEventHandler :: Into the handleEvent method()");
        final PageEvent pageEvent = PageEvent.fromEvent(event);
        final Map<String, Object> payload = new HashMap<>();
        if (Objects.nonNull(pageEvent)) {
            for (Iterator<PageModification> iter = pageEvent.getModifications(); iter.hasNext(); ) {
                PageModification pageModification = iter.next();
                String[] pathArr = pageModification.getPath().split(CommonConstants.SLASH_STRING);
                if (pageModification.getType() == PageModification.ModificationType.CREATED && pathArr.length == PAGE_LENGTH) {
                    payload.put(CommonConstants.CURRENT_PATH, pageModification.getPath());
                    initializeJob(payload);
                    break;
                }
            }
        }
        LOGGER.debug("CountrySiteCreationEventHandler :: Exits the handleEvent method()");
    }

    private void initializeJob(Map<String, Object> payload) {
        LOGGER.debug("CountrySiteCreationEventHandler :: Into the initializeJob method()");
        jobManager.addJob(CommonConstants.COUNTRY_SITE_CREATION_JOB_TOPIC, payload);
    }

}
