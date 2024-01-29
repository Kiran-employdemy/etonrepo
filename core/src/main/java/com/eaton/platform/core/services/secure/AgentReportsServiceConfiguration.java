package com.eaton.platform.core.services.secure;

public interface AgentReportsServiceConfiguration {

	String getUserLookupUrl();

	String getUserLookupAPIUsername();

	String getUserLookupAPIPassword();

	String getUserInfoAPIEndPointURL();

	String getOrgInfoAPIEndPointURL();

	String getDmAPIEndPointURL();
	
	String getDmAPIUsername();
	
	String getDmAPIPassword();

	int getMaxConnections();

	int getMaxHostConnections();

	int getMaxCacheSize();

	int getAccessTokenCacheTTL();
	
	int getUserInfoCacheTTL();
	
	int getResultCount();

}
