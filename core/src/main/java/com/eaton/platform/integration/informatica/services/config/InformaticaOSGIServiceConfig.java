package com.eaton.platform.integration.informatica.services.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eaton Informatica Configration Service")
public @interface InformaticaOSGIServiceConfig {

    @AttributeDefinition(name = "Product Data File Name",description = "Product data file Name")
    String family_file_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Product Data File Input Path",description = "Input path for product data file")
    String input_family_file_path() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Product Data Archive Path",description = "Archive path product data file")
    String archive_family_file_path() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Failed Product Data File Path",description = "Path for Failed product data file")
    String failed_family_file_path() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Language",description = "Please enter languages",cardinality = 50)
    String [] family_languages() default {};

    @AttributeDefinition(name = "Recipients Email Ids For Product Family",description = "Please enter Email IDs of the recipients for product data file")
    String [] family_notification_emailIds() default {};

    @AttributeDefinition(name = "Taxonomy Attribute File Name",description = "Taxonomy Attribute File Name")
    String taxattr_file_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Global Attribute File Name",description = "Global Attribute File Name")
    String globalattr_file_name() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Attribute Data file Input Path",description = "Input path for Attribute data file")
    String input_attribute_file_path() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Attribute Data file Archival Path",description = "Archive path Attribute data file")
    String archive_attribute_file_path() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Failed Attribute Data file Path",description = "Path for Failed Attribute data file")
    String failed_attribute_file_path() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Recipients Email Ids For Attribute File",description = "Please enter Email IDs of the recipients for Attribute data file",cardinality = 50)
    String [] attribute_notification_emailIds() default {};
}


