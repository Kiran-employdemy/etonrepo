package com.eaton.platform.core.servlets.config;

import com.eaton.platform.core.servlets.AttributeIdJsonGeneratorServlet;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Attribute Id Json Generator Servlet")
public @interface AttributeIdJsonGeneratorServletConfig {

    @AttributeDefinition(name = "DAM Excel File Path",description="Uploaded excel file is here")
    String excel_file_path() default AttributeIdJsonGeneratorServlet.DEFAULT_EXCEL_FILE_UPLOAD_PATH;

    @AttributeDefinition(name = "Excel File Archive Path",description="existing excel file archived here")
    String excel_file_archive_path() default  AttributeIdJsonGeneratorServlet.DEFAULT_EXCEL_FILE_ARCHIVE_PATH;

    @AttributeDefinition(name = "Json File Path",description="Generated json file is here")
    String json_file_path() default AttributeIdJsonGeneratorServlet.DEFAULT_JSON_FILE_UPLOAD_PATH;

    @AttributeDefinition(name = "Json File Archive Path",description="existing json file archived here")
    String json_file_archive_path() default AttributeIdJsonGeneratorServlet.DEFAULT_JSON_FILE_ARCHIVE_PATH;

    @AttributeDefinition(name = "Json root element name",description="existing json file archived here")
    String json_root_element() default AttributeIdJsonGeneratorServlet.DEFAULT_JSON_ROOT_ELEMENT;
}
