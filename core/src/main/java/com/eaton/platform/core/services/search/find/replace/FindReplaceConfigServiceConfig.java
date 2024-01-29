package com.eaton.platform.core.services.search.find.replace;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * The Interface FindReplaceConfigServiceConfig.
 *
 * Configuration for Find and replace tool for text and tags
 *
 * @author Jaroslav Rassadin
 */
@ObjectClassDefinition(
		name = "Find Replace Config Service Configuration",
		description = "Configuration for Find Replace Config Service")
public @interface FindReplaceConfigServiceConfig {

	/**
	 * Tags.
	 *
	 * @return the string
	 */
	@AttributeDefinition(
			name = "Tags",
			description = "List of tags mappings. Pattern: <id>|<title (can contain unicode)>|"
					+ "<o (own property) or n (nested property)>|<m (multiple valued) or s (single  valued)>|<tag root>. Tag root can be omitted.")
	String[] tags() default {};

	/**
	 * Assets tag properties.
	 *
	 * @return the string[]
	 */
	@AttributeDefinition(
			name = "Assets tag properties",
			description = "Assets tag properties mapping. Pattern: <id>|<property name>|<m (multiple valued) or s (signle valued)>.")
	String[] assetsTagProperties() default {};

	/**
	 * Components tag properties.
	 *
	 * @return the string[]
	 */
	@AttributeDefinition(
			name = "Components tag properties",
			description = "Components tag properties mapping. Pattern: <id>|<property name>|<m (multiple valued) or s (signle valued)>.")
	String[] componentsTagProperties() default {};

	/**
	 * Pages tag properties.
	 *
	 * @return the string[]
	 */
	@AttributeDefinition(
			name = "Pages tag properties",
			description = "Pages tag properties mapping. Pattern: <id>|<property name>|<m (multiple valued) or s (signle valued)>.")
	String[] pagesTagProperties() default {};

	/**
	 * Resource types.
	 *
	 * @return the string[]
	 */
	@AttributeDefinition(
			name = "Resource types",
			description = "Resource types mapping. Pattern: <id>|<resource type>|<subtype (multifield property name)>. Subtype is optional.")
	String[] resourceTypes() default {};

}
