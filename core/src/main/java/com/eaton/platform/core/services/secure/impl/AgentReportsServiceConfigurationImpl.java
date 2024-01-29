package com.eaton.platform.core.services.secure.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.secure.AgentReportsServiceConfiguration;
import com.eaton.platform.core.services.secure.config.AgentReportsServiceConfig;

@Component(service = AgentReportsServiceConfiguration.class, immediate = true, property = {
		AEMConstants.SERVICE_VENDOR_EATON, AEMConstants.SERVICE_DESCRIPTION + "Agent Reports Service Configuration",
		AEMConstants.PROCESS_LABEL + "AgentReportsServiceConfiguration" })
@Designate(ocd = AgentReportsServiceConfig.class)
public class AgentReportsServiceConfigurationImpl implements AgentReportsServiceConfiguration {

	private String userLookupUrl;
	private String userLookupAPIUsername;
	private String userLookupAPIPassword;
	private String userInfoAPIEndPointURL;
	private String orgInfoAPIEndPointURL;
	private String dmAPIEndPointURL;
	private String dmAPIUsername;
	private String dmAPIPassword;
	private int maxConnections;
	private int maxHostConnections;
	private int maxCacheSize;
	private int accessTokenCacheTTL;
	private int userInfoCacheTTL;
	private int resultCount;

	@Activate
	@Modified
	protected final void activate(final AgentReportsServiceConfig config) {
		if (config != null) {
			userLookupUrl = config.userLookupUrl();
			userLookupAPIUsername = config.userLookupAPIUsername();
			userLookupAPIPassword = config.userLookupAPIPassword();
			userInfoAPIEndPointURL = config.userInfoAPIEndPointURL();
			orgInfoAPIEndPointURL = config.orgInfoAPIEndPointURL();
			dmAPIEndPointURL = config.dmAPIEndPointURL();
			dmAPIUsername = config.getDmAPIUsername();
			dmAPIPassword = config.getDmAPIPassword();
			maxConnections = config.maxConnections();
			maxHostConnections = config.maxHostConnections();
			maxCacheSize = config.maxCacheSize();
			accessTokenCacheTTL = config.accessTokenCacheTTL();
			userInfoCacheTTL = config.userInfoCacheTTL();
			resultCount = config.resultCount();
		}
	}

	@Override
	public String getUserLookupUrl() {
		return userLookupUrl;
	}

	@Override
	public String getUserLookupAPIUsername() {
		return userLookupAPIUsername;
	}

	@Override
	public String getUserLookupAPIPassword() {
		return userLookupAPIPassword;
	}

	@Override
	public String getUserInfoAPIEndPointURL() {
		return userInfoAPIEndPointURL;
	}

	@Override
	public String getOrgInfoAPIEndPointURL() {
		return orgInfoAPIEndPointURL;
	}

	@Override
	public String getDmAPIEndPointURL() {
		return dmAPIEndPointURL;
	}
	
	public String getDmAPIUsername() {
		return dmAPIUsername;
	}

	public String getDmAPIPassword() {
		return dmAPIPassword;
	}

	@Override
	public int getMaxConnections() {
		return maxConnections;
	}

	@Override
	public int getMaxHostConnections() {
		return maxHostConnections;
	}

	@Override
	public int getMaxCacheSize() {
		return maxCacheSize;
	}

	public int getAccessTokenCacheTTL() {
		return accessTokenCacheTTL;
	}
	
	public int getUserInfoCacheTTL() {
		return userInfoCacheTTL;
	}

	@Override
	public int getResultCount() {
		return resultCount;
	}

}
