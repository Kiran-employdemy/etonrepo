package com.eaton.platform.core.models.countryselector;

import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The Class Regionlist.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Region {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1409656172706414694L;

    /** The regionname. */
    @Inject
    private String regionName;

    @Inject
    private String regionId;


    /** The country. */
    @ChildResource
    private List<Country> countryList;


    @PostConstruct
    protected void init() {
        if (StringUtils.isNotBlank(regionName)) {
            final String[] regionArray = regionName.split(Pattern.quote(CommonConstants.PIPE));
            this.regionName = regionArray[0];
            this.regionId = regionArray[1];
        }
    }

    /**
     * Sets the regionname.
     *
     * @param regionName the new regionname
     */
    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    /**
     * Gets the regionname.
     *
     * @return the regionname
     */
    public String getRegionName() {
        return regionName;
    }

    /**
     * Sets the countryList.
     *
     * @param countryList the new countryList
     */
    public void setCountryList(List<Country> countryList) {
        this.countryList = countryList;
    }

    /**
     * Gets the countrylist.
     *
     * @return the countrylist
     */
    public List<Country> getCountryList() {
        return countryList;
    }

    /**
     * Gets the region ID.
     *
     * @return the region ID
     */
    public String getRegionId() {
        return regionId;
    }
}
