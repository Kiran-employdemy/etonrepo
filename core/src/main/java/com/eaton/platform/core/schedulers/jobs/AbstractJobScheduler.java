package com.eaton.platform.core.schedulers.jobs;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractJobScheduler.
 *
 * @author Jaroslav Rassadin
 */
public abstract class AbstractJobScheduler implements JobScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJobScheduler.class);

	/**
	 * Gets the job manager.
	 *
	 * @return the job manager
	 */
	protected abstract JobManager getJobManager();

	/**
	 * Gets the job topic.
	 *
	 * @return the job topic
	 */
	protected abstract String getJobTopic();

	/**
	 * Gets the cron expression.
	 *
	 * @return the cron expression
	 */
	protected abstract String getCronExpression();

	/**
	 * Scheduler enabled.
	 *
	 * @return the boolean
	 */
	protected abstract boolean enabled();

	/**
	 * Initializes the service.
	 */
	protected void init() {
		this.clearScheduledJob();

		if (this.enabled()) {
			this.addScheduledJob();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearScheduledJob() {
		@SuppressWarnings("unchecked")
		final Collection<ScheduledJobInfo> scheduledJobInfos = this.getJobManager().getScheduledJobs(this.getJobTopic(), 0, (Map<String, Object>) null);

		if (CollectionUtils.isNotEmpty(scheduledJobInfos)) {

			for (final ScheduledJobInfo scheduledJobInfo : scheduledJobInfos) {
				LOGGER.debug("Job {} unscheduled.", this.getJobTopic());
				scheduledJobInfo.unschedule();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addScheduledJob() {
		if (StringUtils.isEmpty(this.getCronExpression())) {
			LOGGER.warn("Cron expression is not defined. Can not create job {}.", this.getJobTopic());
			return;
		}
		final JobBuilder.ScheduleBuilder scheduleBuilder = this.getJobManager().createJob(this.getJobTopic()).schedule();
		scheduleBuilder.cron(this.getCronExpression());

		final ScheduledJobInfo scheduledJobInfo = scheduleBuilder.add();

		if (scheduledJobInfo == null) {
			LOGGER.error("Can not create job {}.", this.getJobTopic());
		} else {
			LOGGER.debug("Job {} created.", this.getJobTopic());
		}
	}
}
