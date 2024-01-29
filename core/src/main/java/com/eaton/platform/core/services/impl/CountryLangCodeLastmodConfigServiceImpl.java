package com.eaton.platform.core.services.impl;

import org.apache.commons.lang3.StringUtils;

import com.eaton.platform.core.bean.CountryLangCodeLastmodConfigBean;
import com.eaton.platform.core.bean.CountryLanguageCodeLastmodBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.CountryLangCodeLastmodConfigService;
import com.eaton.platform.core.services.config.CountryLangCodeLastmodServiceConfig;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * The Class ConfigServiceImpl.
 */

@Designate(ocd = CountryLangCodeLastmodServiceConfig.class)
@Component(service = CountryLangCodeLastmodConfigService.class ,immediate = true,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "CountryLangCodeLastmodConfigServiceImpl",
				AEMConstants.PROCESS_LABEL + "CountryLangCodeLastmodConfigServiceImpl"
		})
public class CountryLangCodeLastmodConfigServiceImpl implements CountryLangCodeLastmodConfigService {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CountryLangCodeLastmodConfigServiceImpl.class);

	/** The service reference. */
	CountryLangCodeLastmodConfigBean countryLangCodeLastmodConfigBean;

	/**
	 * Activate.
	 *
	 * @param config the config
	 * @throws Exception the exception
	 */
	@Activate
	@Modified
	protected final void activate(final CountryLangCodeLastmodServiceConfig config) throws Exception {
		this.countryLangCodeLastmodConfigBean = new CountryLangCodeLastmodConfigBean();
		initializeConfigurations(config);
	}

	/**
	 * Deactivate.
	 */
	@Deactivate
	protected void deactivate() {
		this.countryLangCodeLastmodConfigBean = null;
	}

	/**
	 * Initialize configurations.
	 *
	 * @param properties the properties
	 */
	private void initializeConfigurations(CountryLangCodeLastmodServiceConfig properties) {
		LOGGER.debug("CountryLangCodeLastmodConfigServiceImpl :: initializeConfigurations() :: Start");

		if((properties!=null) ){
			Map<String,CountryLanguageCodeLastmodBean> countryLanguageCodeLastmodMap = new HashMap<String,CountryLanguageCodeLastmodBean>();
			String[] countryLanguageCodeArray = properties.country_language_code_list();
			final String X_DEFAULT = "x-default";

			if(countryLanguageCodeArray!=null){
				for (String countryLanguageCodeArrayElement: countryLanguageCodeArray) {

					if(countryLanguageCodeArrayElement!=null){
						String[] codes = countryLanguageCodeArrayElement.split("\\|\\|");
						String countryCode = StringUtils.EMPTY;
						String languageCode = StringUtils.EMPTY;
						String googleCode = StringUtils.EMPTY;
						CountryLanguageCodeLastmodBean countryLanguageCodeLastmodBean = new CountryLanguageCodeLastmodBean();

						if((codes.length>0) && (codes.length<2)){
							countryCode = codes[0];
						}else if((codes.length>0) && (codes.length<3)){
							countryCode = codes[0];
							languageCode = codes[1];
						}else if((codes.length>0) && (codes.length<4)){
							countryCode = codes[0];
							languageCode = codes[1];
							if(!languageCode.equals(X_DEFAULT)){
							googleCode = languageCode.split(CommonConstants.HYPHEN)[0] + CommonConstants.HYPHEN + countryCode;
							}else {
								googleCode = codes[2];
							}
						}
						
						if(countryCode!=null){
							countryLanguageCodeLastmodBean.setCountryCode(countryCode);
						}
						if(languageCode!=null){
							countryLanguageCodeLastmodBean.setLanguageCode(languageCode);
						}
						if(googleCode!=null){
							countryLanguageCodeLastmodBean.setGoogleCode(googleCode);
						}
						countryLanguageCodeLastmodMap.put(countryLanguageCodeArrayElement, countryLanguageCodeLastmodBean);
					}
				}
			}

			this.countryLangCodeLastmodConfigBean.setCountryLanguageCodesLastmodMap(countryLanguageCodeLastmodMap);

		}

		LOGGER.debug("CountryLangCodeLastmodConfigServiceImpl :: initializeConfigurations() :: Exit");
	}

	/* (non-Javadoc)
	 * @see com.eaton.platform.core.services.EatonConfigService#getConfigServiceBean()
	 */
	public CountryLangCodeLastmodConfigBean getCountryLangCodeLastmodConfigBean() {
		return countryLangCodeLastmodConfigBean;
	}


}
