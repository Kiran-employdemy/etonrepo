package com.eaton.platform.core.servlets.config;

import com.eaton.platform.core.servlets.SitemapFamilyProductServlet;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eaton- Family Product Servlet")
public @interface SitemapFamilyProductServletConfig {

    @AttributeDefinition(name = "Family product path")
    String family_product_path() default SitemapFamilyProductServlet.DEFAULT_PATH_SKU_PRODUCT;

    @AttributeDefinition(name = "Want to Generate XML file?",description="Please Check the check box in case want to generate xml file")
    boolean generate_xmlFile() default  false;

    @AttributeDefinition(name = "DAM XML File Path",description="xml file is here")
    String xml_file_path() default SitemapFamilyProductServlet.DEFAULT_XML_DAM_PATH;

}