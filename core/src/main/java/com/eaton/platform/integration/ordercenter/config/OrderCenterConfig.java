package com.eaton.platform.integration.ordercenter.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "OrderCenterStoreIdServiceImpl")
public @interface OrderCenterConfig {

    @AttributeDefinition( name = "Order Center URL",description="Order Center URL for API" )
    String orderCenterURL() default "http://shoputil.tcc.etn.com/public/v1/metaData/";

    @AttributeDefinition( name = "Order Center Tag Path",description="The path under which order center tags are created" )
    String orderCenterTagPath() default "/content/cq:tags/order-center";

    @AttributeDefinition(name = "Enabled", description = "Enable this service request.")
    boolean enabled() default true;

    @AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to the number of connections from a particular instance of HttpConnectionManager.")
    int maxConnections() default 100;

    @AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values only apply to the number of connections from a particular instance of HttpConnectionManager.")
    int maxHostConnections() default 100;

}