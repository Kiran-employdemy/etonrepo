package com.eaton.platform.core.jobs.consumers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.bean.file.purge.FilePurgeConfigBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.file.purge.FilePurgeConfigService;
import com.eaton.platform.core.util.CommonUtil;

/**
 * The Class FilePurgeConsumerImpl.
 *
 * Deletes files from repository based on existing {@link FilePurgeConfigService} configurations.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = JobConsumer.class,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "File Purge Job Consumer",
				AEMConstants.PROCESS_LABEL + "FilePurgeConsumerImpl",
				JobConsumer.PROPERTY_TOPICS + "=" + FilePurgeConsumerImpl.JOB_TOPIC })
public class FilePurgeConsumerImpl implements JobConsumer {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilePurgeConsumerImpl.class);

	/** The Constant JOB_TOPIC. */
	public static final String JOB_TOPIC = "eaton/sling/job/filePurge";

	@Reference
	private AdminService adminService;

	@Reference(
			name = "Service",
			cardinality = ReferenceCardinality.MULTIPLE,
			policy = ReferencePolicy.DYNAMIC,
			service = FilePurgeConfigService.class)
	private final List<FilePurgeConfigService> configServices = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JobResult process(final Job job) {
		LOGGER.debug("Job execution started.");

		if (CollectionUtils.isEmpty(this.configServices)) {
			LOGGER.warn("Job execution failed. Configuration not provided.");
			return JobResult.FAILED;
		}
		try (ResourceResolver resourceResolver = this.adminService.getWriteService()) {

			for (final FilePurgeConfigService configService : this.configServices) {
				if (!configService.getConfig().isEnabled()) {
					continue;
				}
				this.purgeFiles(configService.getConfig(), resourceResolver);
			}
			LOGGER.debug("Job execution finished successfully.");
			return JobResult.OK;

		} catch (final Exception ex) {
			LOGGER.error("An error has occurred while job execution.", ex);
			return JobResult.FAILED;
		}
	}

	private void purgeFiles(final FilePurgeConfigBean config, final ResourceResolver resourceResolver) throws PersistenceException, RepositoryException {
		if (StringUtils.isEmpty(config.getPath()) || (config.getMaxAge() < 1)) {
			LOGGER.warn("Incomplete configuration: {}.", config);
			return;
		}
		final Resource rootResource = resourceResolver.getResource(config.getPath());

		if (rootResource == null) {
			LOGGER.warn("Resource at path {} does not exist.", config.getPath());
			return;
		}
		final Iterator<Resource> iterator = rootResource.listChildren();

		while (iterator.hasNext()) {
			final Resource resource = iterator.next();
			final ValueMap valueMap = resource.getValueMap();

			if (JcrConstants.NT_FILE.equals(valueMap.get(JcrConstants.JCR_PRIMARYTYPE, String.class)) && this.isMaxAgeExceeded(valueMap.get(
					JcrConstants.JCR_CREATED, GregorianCalendar.class), config)) {
				CommonUtil.adapt(resource, Node.class).remove();
			}
		}
		resourceResolver.commit();
	}

	private boolean isMaxAgeExceeded(final GregorianCalendar creationDate, final FilePurgeConfigBean config) {
		return config.getUnit().getChronoUnit().between(creationDate.toZonedDateTime().toInstant(), Instant.now()) > config.getMaxAge();
	}

	/**
	 * Bind service.
	 *
	 * @param service
	 *            the service
	 */
	protected void bindService(final FilePurgeConfigService service) {

		synchronized (this.configServices) {
			this.configServices.add(service);
		}
	}

	/**
	 * Unbind service.
	 *
	 * @param service
	 *            the service
	 */
	protected void unbindService(final FilePurgeConfigService service) {

		synchronized (this.configServices) {
			this.configServices.remove(service);
		}
	}

}
