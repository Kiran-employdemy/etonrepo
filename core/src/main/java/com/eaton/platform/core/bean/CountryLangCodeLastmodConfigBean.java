package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.util.Map;

/**
 * The Class ConfigServiceBean.
 */
public class CountryLangCodeLastmodConfigBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient Map<String, CountryLanguageCodeLastmodBean> countryLanguageCodesLastmodMap;

	public Map<String, CountryLanguageCodeLastmodBean> getCountryLanguageCodesLastmodMap() {
		return countryLanguageCodesLastmodMap;
	}

	public void setCountryLanguageCodesLastmodMap(Map<String, CountryLanguageCodeLastmodBean> countryLanguageCodesLastmodMap) {
		this.countryLanguageCodesLastmodMap = countryLanguageCodesLastmodMap;
	}
}
