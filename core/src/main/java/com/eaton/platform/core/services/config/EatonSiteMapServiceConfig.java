package com.eaton.platform.core.services.config;

import com.eaton.platform.core.constants.CommonConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eaton SiteMap Service")
public @interface EatonSiteMapServiceConfig {

    @AttributeDefinition( name = "Externalizer Domain",description = "Must correspond to a configuration of the Externalizer component.")
    String externalizer_domain() default "publish";

    @AttributeDefinition(name = "Change Frequency Properties", description = "The set of JCR property names which will contain the change frequency value.")
    String [] changefreq_properties() default {};

    @AttributeDefinition( name = "Priority Properties",description = "The set of JCR property names which will contain the priority value." )
    String [] priority_properties() default {};

    @AttributeDefinition(name = "Page Type")
    String pageType() default "sub-category";

    @AttributeDefinition( name = "Resource Type for Site map", description = "Resource type for Filtering" )
    String resourceType() default "eaton/components/structure/eaton-edit-template-page";

    @AttributeDefinition(name = "Resource Type for Site map",description = "Descendant Page path")
    String descendantPagePath() default CommonConstants.CONTENT_ROOT_FOLDER;
}
