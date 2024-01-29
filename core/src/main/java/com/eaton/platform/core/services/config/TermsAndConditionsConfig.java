package com.eaton.platform.core.services.config;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Terms and Conditions Service")
public @interface TermsAndConditionsConfig {

    @AttributeDefinition(name = "Enable T&C redirect", description = "Check to enable terms and conditions")
    boolean enable() default false;

    @AttributeDefinition(name = "Default T&C Path", description = "Default Terms and Conditions path if and only if none can be mapped.")
    String defaultPath() default StringUtils.EMPTY;

    @AttributeDefinition(name = "T&C Path Mapping", description = "A language code and path mapping. <lang_code>_<country>|<path>. Ex: en_US|/content/eaton/us/en-us/tcpage",cardinality = Integer.MAX_VALUE)
    String[] languagePaths() default {};

}
