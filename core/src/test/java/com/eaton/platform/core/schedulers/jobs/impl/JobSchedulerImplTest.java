package com.eaton.platform.core.schedulers.jobs.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Collections;

import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.schedulers.jobs.JobSchedulerConfig;

/**
 * The Class JobSchedulerImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { MockitoExtension.class })
class JobSchedulerImplTest {

	private final String JOB_TOPIC = "eaton/sling/job/rolloutConflict";

	private final String CRON_EXPRESSION = "0 30 2 ? * SUN";

	@Mock
	private JobManager jobManager;

	@Mock
	private ScheduledJobInfo existingJob;

	@Mock
	private JobBuilder jobBuilder;

	@Mock
	private JobBuilder.ScheduleBuilder scheduleBuilder;

	@InjectMocks
	private final JobSchedulerImpl classUnderTest = new JobSchedulerImpl();

	/**
	 * Test that service is activated correctly.
	 */
	@Test
	@DisplayName("Service is activated correctly")
	void testServiceActivatedCorrectly() {
		// set up
		when(this.jobManager.getScheduledJobs(eq(this.JOB_TOPIC), eq(0L), any())).thenReturn(Collections.singletonList(this.existingJob));
		when(this.jobManager.createJob(this.JOB_TOPIC)).thenReturn(this.jobBuilder);
		when(this.jobBuilder.schedule()).thenReturn(this.scheduleBuilder);

		// exercise
		this.classUnderTest.activate(new JobSchedulerConfig() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return JobSchedulerConfig.class;
			}

			@Override
			public String jobTopic() {
				return JobSchedulerImplTest.this.JOB_TOPIC;
			}

			@Override
			public boolean enabled() {
				return true;
			}

			@Override
			public String cronExpression() {
				return JobSchedulerImplTest.this.CRON_EXPRESSION;
			}
		});

		// verify
		verify(this.jobManager, times(1)).getScheduledJobs(eq(this.JOB_TOPIC), eq(0L), any());
		verify(this.existingJob, times(1)).unschedule();
		verify(this.jobManager, times(1)).createJob(this.JOB_TOPIC);
		verify(this.scheduleBuilder, times(1)).cron(this.CRON_EXPRESSION);
		verify(this.scheduleBuilder, times(1)).add();
	}
}
