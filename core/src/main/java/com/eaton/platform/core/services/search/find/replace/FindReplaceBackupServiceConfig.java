package com.eaton.platform.core.services.search.find.replace;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * The Interface FindReplaceBackupServiceConfig.
 *
 * @author Jaroslav Rassadin
 */
@ObjectClassDefinition(
		name = "Find Replace Backup Service Configuration",
		description = "Configuration for Find Replace Backup Service")
public @interface FindReplaceBackupServiceConfig {

	/**
	 * Group name.
	 *
	 * @return the group name
	 */
	@AttributeDefinition(
			name = "Group name",
			description = "Group name for backup package.")
	String group() default "com.eaton.find.replace.backup";

	/**
	 * Name prefix.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Name prefix",
			description = "Package name prefix.")
	String namePrefix() default "find_replace_backup";

	/**
	 * Max nodes.
	 *
	 * @return the max nodes number
	 */
	@AttributeDefinition(
			name = "Max nodes",
			description = "Maximum number of nodes allowed in backup.")
	int maxNodes() default 0;

	/**
	 * Path patterns.
	 *
	 * @return the path patterns
	 */
	@AttributeDefinition(
			name = "Path patterns",
			description = "Repository path patterns for checking minimum path depth of the resource. Can contain * wildcard. E.g. /content/dam/*.")
	String[] pathPatterns() default {};
}
