package com.eaton.platform.core.services;

import com.eaton.platform.core.bean.CountryLangCodeLastmodConfigBean;

/**
 * The Interface EatonConfigService.
 */
public interface CountryLangCodeLastmodConfigService {
	/** The Constant DYNAMIC_DROPDOWN_PATH_ARRAY. */
	public static final String COUNTRY_LANGUAGE_CODE_LIST = "country.language.code.list";
    /**
     * Gets the config service bean.
     *
     * @return the config service bean
     */
    public CountryLangCodeLastmodConfigBean getCountryLangCodeLastmodConfigBean();
}
