package com.eaton.platform.core.listeners;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChange.ChangeType;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.msm.RolloutConflictManager;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * The Class AsyncRolloutJobChangeListenerImplTest.
 *
 * @author Jaroslav Rassadin
 */
@ExtendWith(
		value = { AemContextExtension.class, MockitoExtension.class })
class AsyncRolloutJobChangeListenerImplTest {

	private final static String BASE_JSON_PATH = "/com/eaton/platform/core/listeners/";

	private final static String BASE_RESOURCE_PATH = "/var/eventing/jobs/finished/com.adobe.cq.wcm.jobs.async.rollout/2023/8/3/13/28/";

	private final static String JOB_NO_TARGET = "3b23278b-6514-4b1a-a239-e685c2876074_1_no_target";

	private final static String JOB_WITH_TARGET = "3b23278b-6514-4b1a-a239-e685c2876074_1_with_target";

	private final static String JOB_WITH_ERROR = "3b23278b-6514-4b1a-a239-e685c2876074_1_with_error";

	private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

	@Mock
	private AdminService adminService;

	@Mock
	private RolloutConflictManager rolloutConflictManager;

	@InjectMocks
	private final ResourceChangeListener classUnderTest = new AsyncRolloutJobChangeListenerImpl();

	/**
	 * Sets up test data.
	 */
	@BeforeEach
	public void setUp() {
		when(this.adminService.getReadService()).thenReturn(this.context.resourceResolver());
	}

	/**
	 * Test that RolloutConflictManager is is not called for job that not succeeded.
	 */
	@Test
	@DisplayName("RolloutConflictManager is not called for job that not succeeded")
	void testConflictManagerIsNotCalledForJobThatNotSucceeded() {
		// set up
		this.context.load().json(BASE_JSON_PATH + JOB_WITH_ERROR + ".json", BASE_RESOURCE_PATH + JOB_WITH_ERROR);
		final ResourceChange resourceChange = new ResourceChange(ChangeType.ADDED, BASE_RESOURCE_PATH + JOB_WITH_ERROR, false, null, null, null);

		// exercise
		this.classUnderTest.onChange(Collections.singletonList(resourceChange));

		// verify
		verify(this.rolloutConflictManager, never()).checkAndNotify("/content/eaton/language-masters/de-de/catalog/emobility/battery-isolators", true,
				"admin");

		verify(this.rolloutConflictManager, never()).checkAndNotify(
				"/content/eaton/language-masters/de-de/catalog/emobility/battery-isolators", Arrays.asList(new String[] {
						"/content/eaton/de/de-de/catalog/emobility/battery-isolators", "/content/eaton/at/de-de/catalog/emobility/battery-isolators" }),
				true, "admin");
	}

	/**
	 * Test that RolloutConflictManager is called without target paths.
	 */
	@Test
	@DisplayName("RolloutConflictManager is called without target paths")
	void testConflictManagerIsCalledWithoutTargetPaths() {
		// set up
		this.context.load().json(BASE_JSON_PATH + JOB_NO_TARGET + ".json", BASE_RESOURCE_PATH + JOB_NO_TARGET);
		final ResourceChange resourceChange = new ResourceChange(ChangeType.ADDED, BASE_RESOURCE_PATH + JOB_NO_TARGET, false, null, null, null);

		// exercise
		this.classUnderTest.onChange(Collections.singletonList(resourceChange));

		// verify
		verify(this.rolloutConflictManager, times(1)).checkAndNotify("/content/eaton/language-masters/de-de/catalog/emobility/battery-isolators", true,
				"admin");
	}

	/**
	 * Test that RolloutConflictManager is called with target paths.
	 */
	@Test
	@DisplayName("RolloutConflictManager is called with target paths")
	void testConflictManagerIsCalledWithTargetPaths() {
		// set up
		this.context.load().json(BASE_JSON_PATH + JOB_WITH_TARGET + ".json", BASE_RESOURCE_PATH + JOB_WITH_TARGET);
		final ResourceChange resourceChange = new ResourceChange(ChangeType.ADDED, BASE_RESOURCE_PATH + JOB_WITH_TARGET, false, null, null, null);

		// exercise
		this.classUnderTest.onChange(Collections.singletonList(resourceChange));

		// verify
		verify(this.rolloutConflictManager, times(1)).checkAndNotify(
				"/content/eaton/language-masters/de-de/catalog/emobility/battery-isolators", Arrays.asList(new String[] {
						"/content/eaton/de/de-de/catalog/emobility/battery-isolators", "/content/eaton/at/de-de/catalog/emobility/battery-isolators" }),
				true, "admin");
	}
}
