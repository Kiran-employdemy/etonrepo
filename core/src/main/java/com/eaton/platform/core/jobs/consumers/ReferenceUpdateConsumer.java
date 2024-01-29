package com.eaton.platform.core.jobs.consumers;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.ReferenceService;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = JobConsumer.class,
        immediate = true,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=" + CommonConstants.REFERENCE_UPDATE_JOB_TOPIC,
        })
public class ReferenceUpdateConsumer implements JobConsumer {
    private static final Logger logger = LoggerFactory.getLogger(ReferenceUpdateConsumer.class);

    @Reference
    ReferenceService referenceService;

    @Override
    public JobResult process(Job job) {
        logger.info("Reference Update Job execution started");
        JobResult result = JobResult.FAILED;
        try {
            final String currentPath = (String) job.getProperty(CommonConstants.CURRENT_PATH);
            final String destinationPath = (String) job.getProperty(CommonConstants.DESTINATION_PATH);
            if(StringUtils.isNotBlank(currentPath) && StringUtils.isNotBlank(destinationPath)) {
                referenceService.updatePathReferences(currentPath, destinationPath);
                result = JobResult.OK;
            }
        } catch (Exception exc) {
            logger.error("ERROR -- JOB RESULT IS FAILURE:::".concat(exc.getMessage()));
            result = JobResult.FAILED;
        }

        logger.info("Reference Update Job execution ended");
        return result;
    }
}
