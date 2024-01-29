package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CountryLangCodeConfigService;
import com.eaton.platform.core.services.config.CountryLangCodeConfigServiceConfig;
import com.eaton.platform.core.util.CommonUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * The Class ConfigServiceImpl.
 */

@Designate(ocd = CountryLangCodeConfigServiceConfig.class)
@Component(service = CountryLangCodeConfigService.class ,immediate = true,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "CountryLangCodeConfigServiceImpl",
				AEMConstants.PROCESS_LABEL + "CountryLangCodeConfigServiceImpl"
		})
public class CountryLangCodeConfigServiceImpl implements CountryLangCodeConfigService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CountryLangCodeConfigServiceImpl.class);
	private Map<String, List<String>> allDomainCountryLangCodeMap = new HashMap<>();
	private LoadingCache<String, Map<String, List<String>>> countryLangCodeCache;
	private int maxCacheDuration;
	private int maxCacheSize;
	private Map<String,String> xDefaultCodeMap = new HashMap<>();
	private static final Pattern pattern = Pattern.compile("\\|");

	@Reference
	private AdminService adminService;

	private String[] exludeCountryArray;
	/**
	 * Activate.
	 *
	 * @param config the config
	 * @throws Exception the exception
	 */
	@Activate
	@Modified
	protected final void activate(final CountryLangCodeConfigServiceConfig config) {
		LOGGER.debug("CountryLangCodeConfigServiceImpl :: activate() :: Start");
		initializeCountryLists();
		fetchExcludeCountryList(config);
		maxCacheDuration = config.MAX_CACHE_DURATION();
		maxCacheSize = config.MAX_CACHE_SIZE();
		xDefaultCodeMap = getXDefaultConfigMap(config.X_DEFAULT_HREF_LANG_CODE());
		try {
			countryLangCodeCache = Caffeine.newBuilder()
					.maximumSize(config.MAX_CACHE_SIZE())
					.expireAfterWrite(config.MAX_CACHE_DURATION(), TimeUnit.MINUTES)
					.build(countryLangCodes -> allDomainCountryLangCodeMap);
		} catch(Exception e) {
			LOGGER.error("CountryLangCodeConfigServiceImpl :: activate() :: Cache is not created",e);
		}
		LOGGER.debug("CountryLangCodeConfigServiceImpl :: activate() :: Exit");
	}

	private void fetchExcludeCountryList(CountryLangCodeConfigServiceConfig config) {
		exludeCountryArray = config.exclude_country_code_list();	
	}

	/**
	 * Initialize configurations.
	 *
	 */
	private void initializeCountryLists() {
		LOGGER.debug("CountryLangCodeConfigServiceImpl :: initializeConfigurations() :: Start");
		try(final ResourceResolver resourceResolver = adminService.getReadService()){
			allDomainCountryLangCodeMap.put(CommonConstants.EXTERNALIZER_DOMAIN_EATON,
					CommonUtil.getLangCodeListFromRepo(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON));
			allDomainCountryLangCodeMap.put(CommonConstants.EXTERNALIZER_DOMAIN_EATON_CUMMINS,
					CommonUtil.getLangCodeListFromRepo(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON_CUMMINS));
			allDomainCountryLangCodeMap.put(CommonConstants.EXTERNALIZER_DOMAIN_GREENSWITCHING,
					CommonUtil.getLangCodeListFromRepo(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_GREENSWITCHING));
			allDomainCountryLangCodeMap.put(CommonConstants.EXTERNALIZER_DOMAIN_PHOENIXTEC,
					CommonUtil.getLangCodeListFromRepo(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_PHOENIXTEC));
			allDomainCountryLangCodeMap.put(CommonConstants.EXTERNALIZER_DOMAIN_LOGIN,
					CommonUtil.getLangCodeListFromRepo(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_LOGIN));
			LOGGER.debug("CountryLangCodeConfigServiceImpl :: initializeConfigurations() :: Exit");
		}
	}
	 
	public List<String> getCountryLangCodesList(String domainName) {
		LOGGER.debug("CountryLangCodeConfigServiceImpl :: getCountryLangCodesList()");
		if(domainName != null && domainName.equals(CommonConstants.LOGIN_DOMAIN_TEXT)){
			return countryLangCodeCache.get(CommonConstants.EXTERNALIZER_DOMAIN_LOGIN).get(domainName);
		}else {
			return countryLangCodeCache.get(CommonConstants.EXTERNALIZER_DOMAIN_EATON).get(domainName);
		}
	}

	public Map<String, List<String>> getCountryLangCodesMap(String domainName) {
		LOGGER.debug("CountryLangCodeConfigServiceImpl :: getCountryLangCodesMap()");
		if(domainName != null && domainName.equals(CommonConstants.LOGIN_DOMAIN_TEXT)){
			return countryLangCodeCache.get(CommonConstants.EXTERNALIZER_DOMAIN_LOGIN);
		}else {
			return countryLangCodeCache.get(CommonConstants.EXTERNALIZER_DOMAIN_EATON);
		}
	}

	public LoadingCache<String, Map<String, List<String>>> getCountryLangCodeCache() {
		LOGGER.debug("CountryLangCodeConfigServiceImpl :: getCountryLangCodeCache()");
		return countryLangCodeCache;
	}

	public void setCountryLangCodeCache(LoadingCache<String, Map<String, List<String>>> countryLangCodeCache) {
		this.countryLangCodeCache = countryLangCodeCache;
	}

	public int getMaxCacheDuration() {
		return maxCacheDuration;
	}

	public int getMaxCacheSize() {
		return maxCacheSize;
	}

	private static Map<String,String> getXDefaultConfigMap(String[] xDefaultHrefLangCodeArr){
		Map<String,String> xDefaultLangCodeMap = new HashMap<>();
		for(int index = 0; index < xDefaultHrefLangCodeArr.length; index++){
			String[] domainLangCode = pattern.split(xDefaultHrefLangCodeArr[index]);
			xDefaultLangCodeMap.put(domainLangCode[0], domainLangCode[1]);
		}
		return xDefaultLangCodeMap;

	}

	public String getDomainXDefaultHrefLangCode(String domainName) {
		String hrefLangCode;
		String path = "/content/".concat(domainName);
		hrefLangCode = xDefaultCodeMap.get(path);
		return hrefLangCode;
	}

	@Override
	public String[] getExcludeCountryList() {
	    	return exludeCountryArray;
	}
	
}
