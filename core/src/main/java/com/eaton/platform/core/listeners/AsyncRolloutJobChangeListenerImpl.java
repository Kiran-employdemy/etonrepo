package com.eaton.platform.core.listeners;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.msm.RolloutConflictManager;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class AsyncRolloutJobChangeListenerImpl.
 *
 * Listens for asynchronous rollout job finish event to validate rolled out pages.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = ResourceChangeListener.class,
		immediate = true,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = { AEMConstants.SERVICE_DESCRIPTION + "Listens for asynchronous rollout job finish event to validate rolled out pages",
				ResourceChangeListener.PATHS + "=/var/eventing/jobs/finished/com.adobe.cq.wcm.jobs.async.rollout",
				ResourceChangeListener.CHANGES + "=" + "ADDED" })
public class AsyncRolloutJobChangeListenerImpl implements ResourceChangeListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncRolloutJobChangeListenerImpl.class);

	private static final String SLING_EVENT_JOB_RESOURCE_TYPE = "slingevent:Job";

	private static final String SLING_EVENT_JOB_FINISHED_STATE_PROPERTY_NAME = "slingevent:finishedState";

	private static final String SLING_EVENT_JOB_PATH_PROPERTY_NAME = "path";

	private static final String SLING_EVENT_JOB_TYPE_PROPERTY_NAME = "type";

	private static final String SLING_EVENT_JOB_USER_PROPERTY_NAME = "user";

	private static final String SLING_EVENT_JOB_LIVE_COPY_PATHS_PROPERTY_NAME = "msm%3AtargetPath";

	private static final String SLING_EVENT_JOB_TYPE_DEEP = "deep";

	private static final String SLING_EVENT_JOB_FINISHED_STATE_SUCCEEDED = "SUCCEEDED";

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Reference
	private AdminService adminService;

	@Reference
	private RolloutConflictManager rolloutConflictManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onChange(final List<ResourceChange> changes) {

		try (ResourceResolver resourceResolver = this.adminService.getReadService()) {

			for (final ResourceChange resourceChange : changes) {
				final Resource resource = resourceResolver.getResource(resourceChange.getPath());

				if ((resource != null) && resource.getResourceType().equals(SLING_EVENT_JOB_RESOURCE_TYPE) && SLING_EVENT_JOB_FINISHED_STATE_SUCCEEDED
						.equals(resource.getValueMap().get(SLING_EVENT_JOB_FINISHED_STATE_PROPERTY_NAME, String.class))) {
					LOGGER.debug("Processing job {}", resource.getPath());

					final ValueMap valueMap = resource.getValueMap();

					final String blueprintPath = valueMap.get(SLING_EVENT_JOB_PATH_PROPERTY_NAME, String.class);
					final List<String> liveCopyPaths = this.getLivecopyPaths(valueMap);
					final boolean deep = SLING_EVENT_JOB_TYPE_DEEP.equals(valueMap.get(SLING_EVENT_JOB_TYPE_PROPERTY_NAME, String.class));
					final String userId = valueMap.get(SLING_EVENT_JOB_USER_PROPERTY_NAME, String.class);

					if (CollectionUtils.isNotEmpty(liveCopyPaths)) {
						this.rolloutConflictManager.checkAndNotify(blueprintPath, liveCopyPaths, deep, userId);
					} else {
						this.rolloutConflictManager.checkAndNotify(blueprintPath, deep, userId);
					}
				}
			}
		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while processing async rollout job finished event.", ex);
		}

	}

	private List<String> getLivecopyPaths(final ValueMap valueMap) throws IOException {
		final String liveCopyPathsRaw = valueMap.get(SLING_EVENT_JOB_LIVE_COPY_PATHS_PROPERTY_NAME, String.class);

		if (StringUtils.isNotEmpty(liveCopyPathsRaw)) {
			return Arrays.asList(OBJECT_MAPPER.readerFor(String[].class).readValue(liveCopyPathsRaw));
		}
		return Collections.emptyList();
	}

}
