package com.eaton.platform.core.schedulers.jobs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * The Interface JobSchedulerConfig.
 *
 * @author Jaroslav Rassadin
 */
@ObjectClassDefinition(
		name = "Job Scheduler Config")
public @interface JobSchedulerConfig {

	/**
	 * Cron expression.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Cron expression",
			description = "Cron expression.")
	String cronExpression() default "";

	/**
	 * Scheduler enabled.
	 *
	 * @return true, if scheduler enabled
	 */
	@AttributeDefinition(
			name = "Enabled",
			description = "Scheduler enabled.")
	boolean enabled() default true;

	/**
	 * Job topic.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Job topic",
			description = "Job topic.")
	String jobTopic() default "";
}
