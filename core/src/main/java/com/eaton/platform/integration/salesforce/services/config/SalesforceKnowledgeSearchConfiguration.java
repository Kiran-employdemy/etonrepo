package com.eaton.platform.integration.salesforce.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Salesforce knowledge search configuration
 */
@ObjectClassDefinition(name = "Salesforce Knowledge Search Configuration")
public @interface SalesforceKnowledgeSearchConfiguration {
    /**
     * isGlobalEnabled
     * @return isGlobalEnabled
     */
    @AttributeDefinition(name = "Enable salesforce knowledge search at a global level", description = "turn on salesforce knowledge search for pages containing any of the target tags")
    boolean isGlobalEnabled() default true;

    /**
     * targetTags
     * @return targetTags
     */
    @AttributeDefinition(name = "Target Tags", description = "list of tags that enable knowledge search on page")
    String[] targetTags() default {};

    /**
     * salesforceSiteUrl
     * @return salesforceSiteUrl
     */
    @AttributeDefinition(name = "Salesforce Site Url")
    String salesforceSiteUrl() default "https://eaton--rollhier.sandbox.my.site.com/EatonKnowledge";

    /**
     * salesforceSearchPath
     * @return salesforceSearchPath
     */
    @AttributeDefinition(name = "Salesforce Search Path", description = "path to global search within salesforce site ( ex: /s/global-search )")
    String salesforceSearchPath() default "/s/global-search";

    /**
     * imagePath
     * @return imagePath
     */
    @AttributeDefinition(name = "Salesforce Knowledge Search Image Path", description = "path to image for salesforce knowledge search")
    String imagePath() default "/content/dam/eaton/resources/knowledgeSearchIcon.png";

}
