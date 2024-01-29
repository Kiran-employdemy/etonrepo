package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * PagePropUpdateOnRolloutConfig
 * This class is used to provide OSGI configuration for Page Properties Update Rolloutt config
 */
@ObjectClassDefinition(name = "RolloutPagePropertiesLiveActionFactory")
public @interface PagePropUpdateOnRolloutConfig {

	/**
	 * page properties can be configured using this configuration
	 * 
	 */
	@AttributeDefinition(name = "Rollout Page Properties", description = "List of page properties to be updated on rollout :: Use <Name to be displayed to authors>|<name of the page-property used>")
	String[] ROLLOUT_PAGE_PROPERTIES_LIST() default {"Tags|cq:tags", "Hide-in-navigation|hideInNav", "On-Time|onTime", "Off-Time|offTime", "Redirect|cq:redirectTarget", "Design|cq:designPath", "Alias|sling:alias", "Cloud-Configuration|cq:conf", "Allowed-Templates|cq:allowedTemplates",  "Authentication-Required|cq:authenticationRequired", "Login-Path|cq:loginPath", "Page-Type|page-type", "Publication-Date|publication-date", "Teaser-Image-Path|teaser-image-path", "Overlay-Path|overlay-path", "Page-Change-Frequency|changefrequency", "Priority|priority", "Meta-Robot-Tags|meta-robot-tags", "View|view", "Percolate-Content-Id|percolate-content-id", "Form-Routing|form_routing_tag/cq:tags", "Pim-Page-Path|pimPagePath", "Product-Selector-PIMs|pimPagePaths", "Primary-Sub-Category-Tag|primary-sub-category-tag", "Primary-Sub-Category|primarySubCategory", "Social-Media|socialMedia", "Variant-Path|variantPath", "Context-Hub-Path|cq:contextHubPath", "Context-Hub-Segments-Path|cq:contextHubSegmentsPath", "Target-Ambits|cq:target-ambits", "Closed-User-Groups|closedUserGroup", "Permissions|permissions"};

}
