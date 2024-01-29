package com.eaton.platform.core.models.secure;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.stream.Stream;

/**
 * Model that maps to the secure properties of a page or asset
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecureAttributesModel {

    @Inject @Named("securePage")
    private String securePage;

    @Inject @Named("secureAsset")
    private String secureAsset;

    @Inject @Named("accountType")
    private String[] accountType;

    @Inject @Named("productCategories")
    private String[] productCategories;

    @Inject @Named("applicationAccess")
    private String[] applicationAccess ;

    @Inject @Named("countries")
    private String[] countries ;

    @Inject @Named("tierLevel")
    private String[] tierLevel ;

    @Inject @Named("xmp:eaton-tier-level")
    private String[] assetTierLevel ;

    @Inject @Named("partnerProgrammeType")
    private String[] partnerProgrammeType ;

    @Inject
    private String[] oktaGroups;

    @Inject @Named("xmp:eaton-partner-program-type")
    private String[] assetPartnerProgramType ;

    @Inject @Named("excludeCountries")
    private String excludeCountries;
    @Inject @Named("partnerProgramAndTierLevel")
    private String[] partnerProgramAndTierLevel;

    @Inject @Named("xmp:eaton-partner-program-and-tier-level")
    private String[] assetPartnerProgramAndTierLevel;

    public String getExcludeCountries() {
        return excludeCountries;
    }

    public void setExcludeCountries(String excludeCountries) {
    	this.excludeCountries = excludeCountries;
    }

    public String getSecurePage() {
        return securePage;
    }

    public void setSecurePage(String securePage) {
        this.securePage = securePage;
    }

    public String getSecureAsset() {
        return secureAsset;
    }

    public void setSecureAsset(String secureAsset) {
        this.secureAsset = secureAsset;
    }

    public String[] getAccountType() {
        return accountType;
    }

    public String[] getProductCategories() {
        return productCategories;
    }

    public String[] getApplicationAccess() {
        return applicationAccess;
    }

    public String[] getCountries() {
        return countries;
    }


    public String[] getPartnerProgramAndTierLevel() {
        return partnerProgramAndTierLevel;
    }

    public String[] getOktaGroups() {
        return oktaGroups;
    }

    /**
     * Inits the.
     */
    @PostConstruct
    protected void init() {
        // Re-Initialize the asset tier attribute to original instance variable if the type is Asset .
        if(assetPartnerProgramAndTierLevel != null){
            partnerProgramAndTierLevel = assetPartnerProgramAndTierLevel;
        }
    }

    /**
     *
     * @return true if any one of the secure attribute value is present else false.
     */
    public boolean isPresent(){
        return  Stream.of(this.accountType, this.applicationAccess, this.productCategories,
                this.countries, this.partnerProgramAndTierLevel,this.oktaGroups).anyMatch(item -> item != null && item.length > 0);
    }
}
