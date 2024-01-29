package com.eaton.platform.core.services.file.purge.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.eaton.platform.core.bean.file.purge.FilePurgeConfigBean;
import com.eaton.platform.core.bean.file.purge.FilePurgeConfigBean.Unit;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.jobs.consumers.FilePurgeConsumerImpl;
import com.eaton.platform.core.services.config.FilePurgeConfigServiceConfig;
import com.eaton.platform.core.services.file.purge.FilePurgeConfigService;

/**
 * The Class FilePurgeConfigServiceImpl.
 *
 * Configuration service for file deletion used by {@link FilePurgeConsumerImpl}.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FilePurgeConfigService.class,
		immediate = true,
		configurationPolicy = ConfigurationPolicy.REQUIRE,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "File Purge Config Service",
				AEMConstants.PROCESS_LABEL + "FilePurgeConfigServiceImpl" })
@Designate(
		ocd = FilePurgeConfigServiceConfig.class,
		factory = true)
public class FilePurgeConfigServiceImpl implements FilePurgeConfigService {

	private FilePurgeConfigBean config;

	/**
	 * Activate the service.
	 *
	 * @param config
	 *            the config
	 */
	@Activate
	@Modified
	protected void activate(final FilePurgeConfigServiceConfig config) {
		this.config = new FilePurgeConfigBean(config.enabled(), config.maxAge(), config.path(), Unit.valueOf(config.unit()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FilePurgeConfigBean getConfig() {
		return this.config;
	}

}
