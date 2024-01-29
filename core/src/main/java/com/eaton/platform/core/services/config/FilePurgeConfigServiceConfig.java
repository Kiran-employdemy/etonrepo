package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

/**
 * The Interface FilePurgeConfigServiceConfig.
 *
 * @author Jaroslav Rassadin
 */
@ObjectClassDefinition(
		name = "File Purge Config Service Configuration",
		description = "Configuration for File Purge Config Service")
public @interface FilePurgeConfigServiceConfig {

	/**
	 * Config enabled.
	 *
	 * @return true, if config enabled
	 */
	@AttributeDefinition(
			name = "Enabled",
			description = "Config enabled.")
	boolean enabled() default true;

	/**
	 * Repository path.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Path",
			description = "CRX repository path.")
	String path() default "";

	/**
	 * Max age.
	 *
	 * @return the integer
	 */
	@AttributeDefinition(
			name = "Max age",
			description = "Max age value.")
	int maxAge() default 0;

	/**
	 * Temporal unit.
	 *
	 * @return the integer
	 */
	@AttributeDefinition(
			name = "Unit",
			description = "Temporal unit.",
			cardinality = 4,
			options = {
					@Option(
							label = "day",
							value = "DAY"),
					@Option(
							label = "week",
							value = "WEEK"),
					@Option(
							label = "month",
							value = "MONTH"),
					@Option(
							label = "year",
							value = "YEAR") })
	String unit() default "DAY";
}
