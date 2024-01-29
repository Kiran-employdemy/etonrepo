package com.eaton.platform.core.schedulers.jobs.impl;

import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.schedulers.jobs.AbstractJobScheduler;
import com.eaton.platform.core.schedulers.jobs.JobScheduler;
import com.eaton.platform.core.schedulers.jobs.JobSchedulerConfig;

/**
 * The Class JobSchedulerImpl.
 *
 * Factory service for scheduling of Sling jobs.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = JobScheduler.class,
		immediate = true,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Job Scheduler",
				AEMConstants.PROCESS_LABEL + "JobSchedulerImpl" })
@Designate(
		ocd = JobSchedulerConfig.class,
		factory = true)
public class JobSchedulerImpl extends AbstractJobScheduler {

	@Reference
	private JobManager jobManager;

	private String cronExpression;

	private boolean enabled;

	private String jobTopic;

	/**
	 * Activate the service.
	 *
	 * @param config
	 *            the config
	 */
	@Activate
	@Modified
	protected void activate(final JobSchedulerConfig config) {
		this.cronExpression = config.cronExpression();
		this.enabled = config.enabled();
		this.jobTopic = config.jobTopic();

		super.init();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JobManager getJobManager() {
		return this.jobManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getJobTopic() {
		return this.jobTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getCronExpression() {
		return this.cronExpression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean enabled() {
		return this.enabled;
	}
}
