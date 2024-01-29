package com.eaton.platform.core.services.file.purge;

import com.eaton.platform.core.bean.file.purge.FilePurgeConfigBean;
import com.eaton.platform.core.jobs.consumers.FilePurgeConsumerImpl;

/**
 * The Interface FilePurgeConfigService.
 *
 * Configuration service for file deletion used by {@link FilePurgeConsumerImpl}.
 *
 * @author Jaroslav Rassadin
 */
public interface FilePurgeConfigService {

	/**
	 * Gets the config.
	 *
	 * @return the config
	 */
	FilePurgeConfigBean getConfig();
}
