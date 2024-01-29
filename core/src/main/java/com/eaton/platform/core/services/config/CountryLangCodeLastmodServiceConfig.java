package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eaton Master Sitemap Configuraton", description = "Master Sitemap Configuraton")
public @interface CountryLangCodeLastmodServiceConfig {

    @AttributeDefinition(name = "Country Language Code List", description = "Enter value in following format (no spaces) : Country Code || Language Code || Google code")
    
    String[] country_language_code_list();
   
}
