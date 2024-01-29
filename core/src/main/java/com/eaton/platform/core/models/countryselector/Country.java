package com.eaton.platform.core.models.countryselector;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.inject.Inject;
import java.util.List;

/**
 * The Class Country.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Country{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1409656172706414691L;


    /** The languageList. */
	@ChildResource
    private List<Language> languageList;
    
    /** The countryId. */
    @Inject
    private String countryId;

	/**
	 * Gets the languageList.
	 *
	 * @return the languageList
	 */
	public List<Language> getLanguageList() {
		return languageList;
	}
	
	/**
	 * Sets the languageList.
	 *
	 * @param languageList the new languageList
	 */
	public void setLanguageList(List<Language> languageList) {
		this.languageList = languageList;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}
}
