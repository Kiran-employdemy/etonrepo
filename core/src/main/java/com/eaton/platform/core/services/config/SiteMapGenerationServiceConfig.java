package com.eaton.platform.core.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "SiteMap Generation Service")
public @interface SiteMapGenerationServiceConfig {

    @AttributeDefinition(name = "Sling Resource Type", description = "Sling Resource Type of the HomePage Component or Components.")
    String[]  slingServletResourceTypes() default {"eaton/components/structure/eaton-edit-template-page","/apps/eaton/components/structure/eaton-folder-page-component"};

    @AttributeDefinition( name = "Include Last Modified",description = "If true, the last modified value will be included in the sitemap.")
    boolean includeLastmod() default true;

    @AttributeDefinition(name = "Include Inheritance Property",description = "If true searches for the frequency and priority attribute in the current page if null looks in the parent.")
    boolean includeInherit() default false;

    @AttributeDefinition(name = "Change Frequency Properties", description = "The set of JCR property names which will contain the change frequency value.")
    String[]  changefreqProperties() default {"changefrequency"};

    @AttributeDefinition( name = "Priority Properties",description = "The set of JCR property names which will contain the priority value." )
    String[]  priorityProperties() default {"priority"};

    @AttributeDefinition(name = "DAM Folder Property",description = "The JCR property name which will contain DAM folders to include in the sitemap.")
    String damasssetsProperty() default "";

    @AttributeDefinition( name = "DAM Asset MIME Types", description = "MIME types allowed for DAM assets." )
    String[] damassetTypes() default {};

    @AttributeDefinition(name = "Exclude from External (xml) Sitemap Property",description = "The boolean [cq:Page]/jcr:content property name which indicates if the Page should be hidden from the xml Sitemap. Default value: hideInXml")
    String externalExcludeProperty() default "hideInXml";

    @AttributeDefinition(name = "Exclude from Internal (html) Sitemap Property",description = "The boolean [cq:Page]/jcr:content property name which indicates if the Page should be hidden from the html Sitemap. Default value: hideInHtml")
    String internalExcludeProperty() default "hideInHtml";

    @AttributeDefinition(name = "DEFAULT_EXTENSIONLESS_URLS",description = "TIf true, page links included in sitemap are generated without .html extension and the path is included with a trailing slash, e.g. /content/geometrixx/en/.")
    boolean extensionlessUrls() default false;

    @AttributeDefinition( name = "DEFAULT_REMOVE_TRAILING_SLASH", description = "Only relevant if Extensionless URLs is selected.  If true, the trailing slash is removed from extensionless page links, e.g. /content/geometrixx/en." )
    boolean removeSlash() default false;

    @AttributeDefinition(name = "Character Encoding",description = "TIf not set, the container's default is used (ISO-8859-1 for Jetty)")
    String characterEncoding() default "UTF-8";

}
