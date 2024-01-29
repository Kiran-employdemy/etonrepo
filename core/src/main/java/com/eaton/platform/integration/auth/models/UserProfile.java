package com.eaton.platform.integration.auth.models;

import java.util.Date;
import java.util.List;

/** Interface for User Profile Data */
public interface UserProfile {

    String getId();

    String getGivenName();

    Iterable<String> getAccountTypes();

    String getEmail();

    String getCountryCode();

    String getNsUniqueId();

    Iterable<String> getProductCategories();

    Iterable<String> getApplicationAccessTags();

    List<String> getAccountTypeTags();

    List<String> getProductCategoriesTags();

    List<String> getAppAccessTags();

	String getEatonPersonType();

	String getLastName();

	String getCompanyName();

	String getCompanyAddress();

	String getCompanyCity() ;

	String getCompanyState();

	String getCompanyZipCode();

	String getCompanyBusinessPhone();

	String getCompanyBusinessFax();

	String getCompanyMobileNumber();

    List<String> getLocaleTags();

    void setAccountTypeTags(List<String> accountTypess);

    void setProductCategoriesTags(List<String> productCategories);

    void setAppAccessTags(List<String> applicationAccessTags);

    void setLocaleTags(List<String> localeTags);

    void setId(String id);

    void setGivenName(String givenName);

    void setAccountTypes(Iterable<String> accountTypess);

    void setEmail(String email);

    void setCountrycode(String countrycode);

    void setProductCategories(Iterable<String> productCategories);

    void setApplicationAccessTags(Iterable<String> applicationAccessTags);
    
    void setEatonCustSite(Eatoncustsite eatoncustsite);

    Eatoncustsite getEatonCustSite();

    void setEatonPersonType(String personType);

    void setLastName(String lastName);

    void setCompanyName(String lastName);

    void setCompanyAddress(String companyAddress);

    void setCompanyCity(String companyCity);

    void setCompanyState(String companyState);

    void setNsUniqueId(String nsUniqueId);

    void setCompanyZipCode(String companyZipCode);

    void setCompanyBusinessPhone(String companyBusinessPhone);

    void setCompanyBusinessFax(String companyBusinessFax);

    void setCompanyMobileNumber(String companyMobileNumber);

    void setUid(String uid);
    String getUid();

    void setEatonSupplierCompanyId(String eatonSupplierCompanyId);

    String getEatonSupplierCompanyId();

    void setEatonEulaAcceptDate(Date eatonEulaAcceptDate);
    Date getEatonEulaAcceptDate();

    void setEatonEshopEulaAcceptDate(Date eatonEshopEulaAcceptDate);
    Date getEatonEshopEulaAcceptDate();

    void setEatonIccVistaUid(String eatonIccVistaUid);
    String getEatonIccVistaUid();

    void setEatonDrcId(String eatonDrcId);
    String getEatonDrcId();

    List<String> getPartnerProgramAndTierLevelTags();

    void setPartnerProgramTypeAndTierLevelTags(List<String> partnerProgramAndTierLevelTags);

    List<String> getPartnerProgramTypeAndTierLevels();

    void setPartnerProgramAndTierLevels(List<String> partnerProgramAndTierLevels);

    List<String> getUserOktaGroups();

    void setUserOktaGroups(List<String> userOktaGroups);
     List<String> getUserOktaAppLinks();
     void setUserOktaAppLinks(List<String> userOktaAppLinks);
}
