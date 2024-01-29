package com.eaton.platform.core.schedulers.jobs;

/**
 * The Interface JobScheduler.
 *
 * Factory service for scheduling of Sling jobs.
 *
 * @author Jaroslav Rassadin
 */
public interface JobScheduler {

	/**
	 * Clear scheduled job.
	 */
	void clearScheduledJob();

	/**
	 * Adds the scheduled job.
	 */
	void addScheduledJob();
}
