package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * PublishOnRolloutConfig
 * This class is used to provide OSGI configuration for Rollout
 */
@ObjectClassDefinition(name = "PublishOnRolloutLiveActionFactory")
public @interface PublishOnRolloutConfig {

	/**
	 * The Live site country codes can be configured using this configuration
	 * 
	 */
	@AttributeDefinition(name = "Live Copies ", description = "List of live sites to publish on rollout. ")
	String[] LIVE_COPY_LIST() default { "/us/", "/br/", "/mx/", "/sg/", "/cn/", "/in/" };
}
