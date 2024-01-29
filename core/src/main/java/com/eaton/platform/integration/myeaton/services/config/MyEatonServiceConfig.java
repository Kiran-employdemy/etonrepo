/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services.config;

import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.AttributeDefinition;

/** Configuration for My Eaton services */
@ObjectClassDefinition(name = "MyEatonService")
public @interface MyEatonServiceConfig {
    int ONE_HUNDRED = 100;
    int ONE_THOUSAND = 1000;
    int SIXTY = 60;

    /**
     * My Eaton Endpoint Root Config
     * @return Endpoint Root */
    @AttributeDefinition(
        name = "Endpoint URL Root",
        description = "Service URL Root for the My Eaton API")
    String myEatonEndpointRoot() default "http://portalapps-dev2.tcc.etn.com:8888/portal-access-api";

    /**
     * User Registration Endpoint Config
     * @return User Registration Endpoint */
    @AttributeDefinition(
        name = "User Registration URL Part",
        description = "Service URL part for self registration")
    String userRegistrationEndpoint() default "/user/self-registration";

    /**
     * User Lookup Endpoint Config
     * @return User Lookup Endpoint */
    @AttributeDefinition(
        name = "User Lookup URL Part",
        description = "Service URL part for user lookup")
    String userLookupEndpoint() default "/user/username/lookup";

    /**
     * Supplier Lookup Endpoint Config
     * @return Supplier Lookup Endpoint */
    @AttributeDefinition(
            name = "Supplier Lookup URL Part",
            description = "Service URL part for Supplier lookup")
    String supplierLookupEndpoint() default "/site-management/v1/sites";


    @AttributeDefinition(
            name = "Update user information",
            description = "Endpoint to update user profile information"
    )
    String updateUserEndpoint() default "/user-management/v1/users";

    /**
     * Fields Endpoint Config
     * @return Fields Endpoint */
    @AttributeDefinition(
        name = "Fields URL Part",
        description = "Service URL part for Fields")
    String fieldsEndpoint() default "/user/self-registration/fields";

    /**
     * Service Username Config
     * @return Service Username */
    @AttributeDefinition(
        name = "My Eaton Username",
        description = "Service Username for Basic Authentication")
    String myEatonUsername() default "user";

    /**
     * Service Password Config
     * @return Service Password */
    @AttributeDefinition(
        name = "My Eaton Password",
        description =
            "Service Password for Basic Authentication. Should be encrypted with crypto service",
        type = AttributeType.PASSWORD)
    String myEatonPassword() default
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
    int maxHostConnections() default ONE_HUNDRED;

    /**
     * Max Cache Size Config
     * @return Max Cache Size */
    @AttributeDefinition(
        name = "Max Cache Size",
        description = "Defines the maximum of the cached response before invalidating the oldest.")
    int maxCacheSize() default ONE_THOUSAND;

    /**
     * Cache Duration Config
     * @return Duration */
    @AttributeDefinition(
        name = "Cache Duration",
        description = "Define (in seconds) the maximum time the cached response will stay valid.")
    int cacheDuration() default SIXTY;

    /**
     * Metadata Path Config
     * @return Metadata Path */
    @AttributeDefinition(
        name = "Metadata Path",
        description = "Portion of path for AEM Asset Metadata.")
    String metadataPath() default "/jcr:content/metadata";

    /**
     * FDM Definitions Path Config
     * @return FDM Definitions Path*/
    @AttributeDefinition(
        name = "FDM Definitions Path",
        description = "Portion of path for FDM Definitions")
    String fdmDefinitionsPath() default "/jcr:content/renditions/fdm-json/jcr:content/definitions";
}
