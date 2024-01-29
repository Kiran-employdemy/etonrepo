package com.eaton.platform.core.services.file.purge.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.eaton.platform.core.bean.file.purge.FilePurgeConfigBean;
import com.eaton.platform.core.bean.file.purge.FilePurgeConfigBean.Unit;
import com.eaton.platform.core.services.config.FilePurgeConfigServiceConfig;

/**
 * The Class FilePurgeConfigServiceImplTest.
 *
 * @author Jaroslav Rassadin
 */
class FilePurgeConfigServiceImplTest {

	private final String JOB_TOPIC = "/tmp/eaton/msm/conflict/reports";

	private final FilePurgeConfigServiceImpl classUnderTest = new FilePurgeConfigServiceImpl();

	/**
	 * Test that service is activated correctly.
	 */
	@Test
	@DisplayName("Service is activated correctly")
	void testServiceActivatedCorrectly() {
		// set up and exercise
		this.classUnderTest.activate(new FilePurgeConfigServiceConfig() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return FilePurgeConfigServiceConfig.class;
			}

			@Override
			public String unit() {
				return "DAY";
			}

			@Override
			public String path() {
				return FilePurgeConfigServiceImplTest.this.JOB_TOPIC;
			}

			@Override
			public int maxAge() {
				return 30;
			}

			@Override
			public boolean enabled() {
				return true;
			}
		});

		// verify
		assertEquals(new FilePurgeConfigBean(true, 30, this.JOB_TOPIC, Unit.DAY), this.classUnderTest.getConfig(), "Should be equal configs");
	}
}
