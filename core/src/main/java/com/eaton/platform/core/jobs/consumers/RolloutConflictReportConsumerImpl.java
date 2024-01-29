package com.eaton.platform.core.jobs.consumers;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.msm.RolloutConflictManager;

/**
 * The Class RolloutConflictReportConsumerImpl.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = JobConsumer.class,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Rollout Conflict Report Job Consumer",
				AEMConstants.PROCESS_LABEL + "RolloutConflictReportConsumerImpl",
				JobConsumer.PROPERTY_TOPICS + "=" + CommonConstants.ROLLOUT_CONFLICT_REPORT_JOB_TOPIC })
public class RolloutConflictReportConsumerImpl implements JobConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(RolloutConflictReportConsumerImpl.class);

	@Reference
	private RolloutConflictManager rolloutConflictManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JobResult process(final Job job) {
		LOGGER.debug("Job execution started.");

		try {
			this.rolloutConflictManager.reportConflicts();

			LOGGER.debug("Job execution finished successfully.");
			return JobResult.OK;

		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while job execution.", ex);
			return JobResult.FAILED;
		}
	}

}
