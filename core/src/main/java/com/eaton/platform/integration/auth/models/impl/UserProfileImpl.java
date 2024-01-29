package com.eaton.platform.integration.auth.models.impl;

import com.eaton.platform.integration.auth.models.Eatoncustsite;
import com.eaton.platform.integration.auth.models.UserProfile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of UserProfile
 */
public class UserProfileImpl implements UserProfile {

    private String id;
    private String givenName;
    private String eatonPersonType;
    private String lastName;
    private Iterable<String> accountTypes;
    private String email;
    private String countryCode;
    private Iterable<String> productCategories;
    private Iterable<String> applicationAccessTags;
    private List<String> accountTypeTags;
    private List<String> productCategoriesTags;
    private List<String> appAccessTags;
    private List<String> localeTags;
    private List<String> partnerProgramAndTierLevelTags;
    private List<String> partnerProgramAndTierLevels;
    private List<String> userOktaGroups = new ArrayList<>();
    private List<String> userOktaAppLinks = new ArrayList<>();
    private String companyName;
    private String companyAddress;
    private String companyCity;
    private String companyState;
    private String companyZipCode;
    private String companyBusinessPhone;
    private String companyBusinessFax;
    private String companyMobileNumber;
    private String uid;
    private String nsUniqueId;
    private String eatonSupplierCompanyId;
    private Date eatonEulaAcceptDate;
    private Date eatonEshopEulaAcceptDate;
    private String eatonIccVistaUid;
    private String eatonDrcId;
    private Eatoncustsite eatonCustSite;

    public UserProfileImpl() {

    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getGivenName() {
        return this.givenName;
    }

    @Override
    public Iterable<String> getAccountTypes() {
        return this.accountTypes;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getCountryCode() {
        return this.countryCode;
    }

    @Override
    public Iterable<String> getProductCategories() {
        return this.productCategories;
    }

    @Override
    public Iterable<String> getApplicationAccessTags() {
        return this.applicationAccessTags;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @Override
    public void setAccountTypes(Iterable<String> accountTypes) {
        this.accountTypes = accountTypes;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setCountrycode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public void setProductCategories(Iterable<String> productCategories) {
        this.productCategories = productCategories;
    }

    @Override
    public void setApplicationAccessTags(Iterable<String> applicationAccessTags) {
        this.applicationAccessTags = applicationAccessTags;
    }

    @Override
    public List<String> getAccountTypeTags() {
        return accountTypeTags;
    }

    public void setAccountTypeTags(List<String> accountTypeTags) {
        this.accountTypeTags = accountTypeTags;
    }

    @Override
    public List<String> getProductCategoriesTags() {
        return productCategoriesTags;
    }

    public void setProductCategoriesTags(List<String> productCategoriesTags) {
        this.productCategoriesTags = productCategoriesTags;
    }

    @Override
    public List<String> getAppAccessTags() {
        return appAccessTags;
    }

    public void setAppAccessTags(List<String> appAccessTags) {
        this.appAccessTags = appAccessTags;
    }

    @Override
    public List<String> getLocaleTags() {
        return localeTags;
    }

    public void setLocaleTags(List<String> localeTags) {
        this.localeTags = localeTags;
    }

    @Override
    public String getEatonPersonType() {
        return eatonPersonType;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setEatonPersonType(String personType) {
        this.eatonPersonType = personType;

    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public String getCompanyName() {
        return companyName;
    }

    @Override
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public String getCompanyState() {
        return companyState;
    }

    public void setCompanyState(String companyState) {
        this.companyState = companyState;
    }

    public String getCompanyZipCode() {
        return companyZipCode;
    }

    public void setCompanyZipCode(String companyZipCode) {
        this.companyZipCode = companyZipCode;
    }

    public String getCompanyBusinessPhone() {
        return companyBusinessPhone;
    }

    public void setCompanyBusinessPhone(String companyBusinessPhone) {
        this.companyBusinessPhone = companyBusinessPhone;
    }

    public String getCompanyBusinessFax() {
        return companyBusinessFax;
    }

    public void setCompanyBusinessFax(String companyBusinessFax) {
        this.companyBusinessFax = companyBusinessFax;
    }

    public String getCompanyMobileNumber() {
        return companyMobileNumber;
    }

    public void setCompanyMobileNumber(String companyMobileNumber) {
        this.companyMobileNumber = companyMobileNumber;
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getNsUniqueId() {
        return this.nsUniqueId;
    }

    @Override
    public void setNsUniqueId(String nsUniqueId) {
        this.nsUniqueId = nsUniqueId;
    }

    public String getEatonSupplierCompanyId() {
        return eatonSupplierCompanyId;
    }

    @Override
    public void setEatonSupplierCompanyId(String eatonSupplierCompanyId) {
        this.eatonSupplierCompanyId = eatonSupplierCompanyId;
    }

    @Override
    public void setEatonEulaAcceptDate(Date eatonEulaAcceptDate) {
        this.eatonEulaAcceptDate = eatonEulaAcceptDate;
    }

    @Override
    public Date getEatonEulaAcceptDate() {
        return this.eatonEulaAcceptDate;
    }

    @Override
    public void setEatonEshopEulaAcceptDate(Date eatonEshopEulaAcceptDate) {
        this.eatonEshopEulaAcceptDate = eatonEshopEulaAcceptDate;
    }

    @Override
    public Date getEatonEshopEulaAcceptDate() {
        return this.eatonEshopEulaAcceptDate;
    }

    @Override
    public String getEatonIccVistaUid() {
        return eatonIccVistaUid;
    }

    @Override
    public void setEatonIccVistaUid(String eatonIccVistaUid) {
        this.eatonIccVistaUid = eatonIccVistaUid;
    }

    @Override
    public String getEatonDrcId() {
        return eatonDrcId;
    }

    @Override
    public List<String> getPartnerProgramAndTierLevelTags() {
        return partnerProgramAndTierLevelTags;
    }

    @Override
    public void setPartnerProgramTypeAndTierLevelTags(List<String> partnerProgramAndTierLevelTags) {
        this.partnerProgramAndTierLevelTags = partnerProgramAndTierLevelTags;
    }

    @Override
    public List<String> getPartnerProgramTypeAndTierLevels() {
        return partnerProgramAndTierLevels;
    }

    @Override
    public void setPartnerProgramAndTierLevels(List<String> partnerProgramAndTierLevels) {
        this.partnerProgramAndTierLevels = partnerProgramAndTierLevels;
    }

    @Override
    public void setEatonDrcId(String eatonDrcId) {
        this.eatonDrcId = eatonDrcId;
    }
    
    @Override
	public List<String> getUserOktaGroups() {
		return userOktaGroups;
	}
    
    @Override
	public void setUserOktaGroups(List<String> userOktaGroups) {
		this.userOktaGroups = userOktaGroups;
	}
    @Override
    public void setEatonCustSite(Eatoncustsite eatonCustSite) {
        this.eatonCustSite = eatonCustSite;
        }
    @Override       
 public Eatoncustsite getEatonCustSite() {
        return eatonCustSite;
    }

    public List<String> getUserOktaAppLinks() {
        return userOktaAppLinks;
    }

    public void setUserOktaAppLinks(List<String> userOktaAppLinks) {
        this.userOktaAppLinks = userOktaAppLinks;
    }
}
