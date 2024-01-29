package com.eaton.platform.core.webtools.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * <html> Description: Configuration for  Webtools Services.
 *
 * @author ICF
 * @version 1.0
 * @since 2022
 *
 */
@ObjectClassDefinition(name = "WebtoolsService")
public @interface WebtoolsServiceConfig {

    int ONE_HUNDRED = 100;
    int ONE_THOUSAND = 1000;

    //To-Do
    // The configuration will be updated post API development.
    /**
     * Webtools Endpoint Root Config
     * @return Endpoint Root */
    @AttributeDefinition(
            name = "Endpoint URL Root",
            description = "Service URL Root for the Webtools API")
    String webtoolsBaseEndpointRoot() default "http://localhost:8080/eaton-utilities/v1/products";

    /**
     * Arinc600 Components Endpoint Config
     * @return Arinc600 Components Endpoint */
    @AttributeDefinition(
            name = "Arinc600 Components URL Part",
            description = "Service URL part for arinc600 components")
    String arincComponentEndpoint() default "/arinc/components";

    /**
     * Arinc600 Packages Endpoint Config
     * @return Arinc600 Packages Endpoint */
    @AttributeDefinition(
            name = "Arinc600 Packaging URL Part",
            description = "Service URL part for arinc600 packages")
    String arincPackagesEndpoint() default "/arinc/packages";

    /**
     * Backshell Metadata Endpoint Config
     * @return Backshell Metadata Endpoint */
    @AttributeDefinition(
            name = "Backshell Metadata URL Part",
            description = "Service URL part for backshell metadata")
    String backshellMetadataEndpoint() default "/backshell/metadata";

    /**
     * Backshell Partbuild Endpoint Config
     * @return Backshell Partbuild Endpoint */
    @AttributeDefinition(
            name = "Backshell Partbuild URL Part",
            description = "Service URL part for Backshell Partbuild")
    String backshellPartbuildEndpoint() default "/backshell/partBuild";

    /**
     * FreeSample Endpoint Config
     * 
     * @return FreeSample catalog Endpoint
     */
    @AttributeDefinition(name = "Free sample Endpoint", description = "Service URL part for Backshell Partbuild")
    String freeSampleStockAvailabilityEndpoint() default "/freesample/getFreeSamplePerName";

    /**
     * Service Username Config
     * @return Service Username */
    @AttributeDefinition(
            name = "Webtools Username",
            description = "Service Username for Basic Authentication")
    String webtoolsUsername() default "user";

    /**
     * Service Password Config
     * @return Service Password */
    @AttributeDefinition(
            name = "Webtools Password",
            description =
                    "Service Password for Basic Authentication. Should be encrypted with crypto service",
            type = AttributeType.PASSWORD)
    String webtoolsPassword() default
            "{fa771e1a37f7991485059fa6934ce8066e3c8aa7547c1b3aa998b3450d55435c}";

    /**
     * Max Connections Config
     * @return Max Connections */
    @AttributeDefinition(
            name = "Max Connections",
            description =
                    "Defines the maximum number of connections allowed overall. This value only applies to"
                            + " the number of connections from a particular instance of HttpConnectionManager.")
    int maxConnections() default ONE_HUNDRED;

    /**
     * Max Host Connections Config
     * @return Max Host Connections */
    @AttributeDefinition(
            name = "Max Host Connections",
            description =
                    "Defines the maximum number of connections allowed per host configuration. These values"
                            + " only apply to the number of connections from a particular instance of"
                            + " HttpConnectionManager.")
    int maxHostConnections() default ONE_THOUSAND;

    @AttributeDefinition(name = "Arinc600 Packaging URL Part", description = "Service URL part for arinc600 packages")
    String arincPackages() default "/arinc/packageValidation";

}
