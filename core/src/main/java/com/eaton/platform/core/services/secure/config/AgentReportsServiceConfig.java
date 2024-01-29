package com.eaton.platform.core.services.secure.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/** Configuration for AgentReports services */

@ObjectClassDefinition(name = "AgentReportsService")
public @interface AgentReportsServiceConfig {

	int ONE_HUNDRED = 100;
	int ONE_THOUSAND = 1000;

	/**
	 * User Lookup Endpoint Config
	 * 
	 * @return User Lookup Endpoint
	 */
	@AttributeDefinition(name = "User Lookup URL Part", description = "Service URL part for user lookup")
	String userLookupUrl() default "http://shoputil-qa.tcc.etn.com/public/v1/currentUser/login";

	/**
	 * User Lookup API Username Config
	 * 
	 * @return Service Username
	 */
	@AttributeDefinition(name = "User Lookup API Username", description = "Username for User Lookup API")
	String userLookupAPIUsername() default "myeaton_ws";

	/**
	 * User Lookup API Password Config
	 * 
	 * @return Service Password
	 */
	@AttributeDefinition(name = "User Lookup API  Password", description = "Password for User Lookup API. Should be encrypted with crypto service", 
			type = AttributeType.PASSWORD)
	String userLookupAPIPassword() default "Welcome12";
	
	
	/**
	 * User Info API URL Config
	 * 
	 * @return User Info API URL
	 */
	@AttributeDefinition(name = "User Info API URL", description = "Endpoint URL for User Info API")
	String userInfoAPIEndPointURL() default "http://shoputil-qa.tcc.etn.com/public/v1/userInfo/";
	
	/**
	 * Org Info API URL Config
	 * 
	 * @return Org Info API URL
	 */
	@AttributeDefinition(name = "Org Info API URL", description = "Endpoint URL for Org Info API")
	String orgInfoAPIEndPointURL() default "http://shoputil-qa.tcc.etn.com/public/v1/orgInfo/";
	
	/**
	 * Document Manager API URL Config
	 * 
	 * @return Document Manager API URL
	 */
	@AttributeDefinition(name = "Document Manager API URL", description = "Endpoint URL for Document Manager API")
	String dmAPIEndPointURL() default "http://dm-qa-ws.tcc.etn.com/_dav/urm/idcplg";
	
	/**
	 * Document Manager API Username Config
	 * 
	 * @return Service Username
	 */
	@AttributeDefinition(name = "Document Manager API Username", description = "Username for Document Manager API")
	String getDmAPIUsername() default "ebs_reporting";

	/**
	 * Document Manager API Password Config
	 * 
	 * @return Service Password
	 */
	@AttributeDefinition(name = "Document Manager API  Password", description = "Password for Document Manager API. Should be encrypted with crypto service", 
			type = AttributeType.PASSWORD)
	String getDmAPIPassword() default "R3p0rt1ng";
	

	/**
	 * Max Connections Config
	 * 
	 * @return Max Connections
	 */
	@AttributeDefinition(name = "Max Connections", description = "Defines the maximum number of connections allowed overall. This value only applies to"
			+ " the number of connections from a particular instance of HttpConnectionManager.")
	int maxConnections() default ONE_HUNDRED;

	/**
	 * Max Host Connections Config
	 * 
	 * @return Max Host Connections
	 */
	@AttributeDefinition(name = "Max Host Connections", description = "Defines the maximum number of connections allowed per host configuration. These values"
			+ " only apply to the number of connections from a particular instance of" + " HttpConnectionManager.")
	int maxHostConnections() default ONE_HUNDRED;

	/**
	 * Max Cache Size Config
	 * 
	 * @return Max Cache Size
	 */
	@AttributeDefinition(name = "Max Cache Size", description = "Defines the maximum of the cached response before invalidating the oldest.")
	int maxCacheSize() default ONE_THOUSAND;

	/**
	 * Access token cache TTL
	 * @return accessTokenCacheTTL
	 */
	@AttributeDefinition(name = "Access Token Cache TTL", description = "The length of time the access token cache should exist (Seconds)")
	int accessTokenCacheTTL() default 1800;

	/**
	 * User info cache TTL
	 * @return userInfoCacheTTL
	 */
	@AttributeDefinition(name = "User Info Cache TTL", description = "The length of time the user info cache should exist (Seconds)")
	int userInfoCacheTTL() default 1800;
	
	/**
	 * Agent reports result count
	 * @return result count
	 */
	@AttributeDefinition(name = "Result Count", description = "Agent reports result count")
	int resultCount() default 50;
}
