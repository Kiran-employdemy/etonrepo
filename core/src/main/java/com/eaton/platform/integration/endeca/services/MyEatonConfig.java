package com.eaton.platform.integration.endeca.services;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name="My Eaton Config", description = "This config contains a My Eaton mockup for local author environment")
public @interface MyEatonConfig {
	@AttributeDefinition(
			name = "sec_account_type_name",
			type = AttributeType.STRING
	)
	String sec_account_type_name() default "SEC-Account-Type";

	@AttributeDefinition(
			name = "sec_account_type_values",
			type = AttributeType.STRING
	)
	String[] sec_account_type_values() default {""};

	@AttributeDefinition(
			name = "sec_application_access_name",
			type = AttributeType.STRING
	)
	String sec_application_access_name() default "SEC-Application-Access";

	@AttributeDefinition(
			name = "sec_application_access_values",
			type = AttributeType.STRING
	)
	String[] sec_application_access_values() default {""};

	@AttributeDefinition(
			name = "sec_company_name",
			type = AttributeType.STRING
	)
	String sec_company_name() default "SEC-Company";

	@AttributeDefinition(
			name = "sec_company_values",
			type = AttributeType.STRING
	)
	String[] sec_company_values() default {""};

	@AttributeDefinition(
			name = "sec_product_categories_name",
			type = AttributeType.STRING
	)
	String sec_product_categories_name() default "SEC-Product-Categories";

	@AttributeDefinition(
			name = "sec_product_categories_values",
			type = AttributeType.STRING
	)
	String[] sec_product_categories_values() default {""};

	@AttributeDefinition(
			name = "sec_country_name",
			type = AttributeType.STRING
	)
	String sec_country_name() default "SEC-country";

	@AttributeDefinition(
			name = "sec_country_values",
			type = AttributeType.STRING
	)
	String[] sec_country_values() default {""};

	@AttributeDefinition(
			name = "sec_tier_level_name",
			type = AttributeType.STRING
	)
	String sec_tier_level_name() default "SEC-Tier-Level";

	@AttributeDefinition(
			name = "sec_tier_level_values",
			type = AttributeType.STRING
	)
	String[] sec_tier_level_values() default {""};

	@AttributeDefinition(
			name = "sec_partner_programme_type_name",
			type = AttributeType.STRING
	)
	String sec_partner_programme_type_name() default "SE-Partner-Programme-Type";

	@AttributeDefinition(
			name = "sec_partner_programme_type_values",
			type = AttributeType.STRING
	)
	String[] sec_partner_programme_type_values() default {""};
}
