package com.eaton.platform.core.services.secure.impl;

import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.models.UserProfileResponse;
import com.eaton.platform.integration.auth.util.EatonAuthUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class UserProfileFixtures {


    public static final String PPTEST_AT_COMPANY = "pptestAtCompany.json";
    public static final String TEST_EW_PREM_UK_AT_COMPANY = "testewpremukatcompanycom.json";

    public static final String TEST_OKTA_GROUP_USER = "oktagroupuserreponse.json";

    public static UserProfile pptestAtCompany() throws IOException {
        return EatonAuthUtil.getUserProfile(new ObjectMapper().readValue(UserProfileFixtures.class.getResourceAsStream(PPTEST_AT_COMPANY), UserProfileResponse.class));
    }

    public static UserProfile oktaGroupUser() throws IOException {
        return EatonAuthUtil.getUserProfile(new ObjectMapper().readValue(UserProfileFixtures.class.getResourceAsStream(TEST_OKTA_GROUP_USER), UserProfileResponse.class));
    }

    public static UserProfile testewpremukatcompanycom() throws IOException {
        return EatonAuthUtil.getUserProfile(new ObjectMapper().readValue(UserProfileFixtures.class.getResourceAsStream(TEST_EW_PREM_UK_AT_COMPANY), UserProfileResponse.class));
    }

    public static UserProfile pptestAtCompanyWithMappedTags() throws IOException {
        UserProfile userProfile = EatonAuthUtil.getUserProfile(new ObjectMapper().readValue(UserProfileFixtures.class.getResourceAsStream(PPTEST_AT_COMPANY), UserProfileResponse.class));
        userProfile.setAccountTypeTags(Arrays.asList("eaton-secure:accounttype/contractor","eaton-secure:accounttype/end-customer"));
        userProfile.setProductCategoriesTags(Arrays.asList("eaton-secure:product-category/aerospace/engine-build-up-solutions", "eaton-secure:product-category/aerospace/engine-oil-system"));
        userProfile.setAppAccessTags(Arrays.asList("eaton-secure:application-access/eaton-university", "eaton-secure:application-access/new-myeaton"));
        userProfile.setLocaleTags(Arrays.asList("eaton:country/europe/gb", "eaton:country/europe"));
        userProfile.setPartnerProgramTypeAndTierLevelTags(Arrays.asList("eaton-secure:partner-programme-type/electrical-installer-programme/premium", "eaton-secure:partner-programme-type/drives-solution-program"));
        return userProfile;
    }

    public static UserProfile oktaGroupWithMappedTags() throws IOException {
        UserProfile userProfile = oktaGroupUser();
        userProfile.setAccountTypeTags(Arrays.asList("eaton-secure:accounttype/contractor","eaton-secure:accounttype/end-customer"));
        userProfile.setProductCategoriesTags(Arrays.asList("eaton-secure:product-category/aerospace/engine-build-up-solutions", "eaton-secure:product-category/aerospace/engine-oil-system"));
        userProfile.setAppAccessTags(Arrays.asList("eaton-secure:application-access/eaton-university", "eaton-secure:application-access/new-myeaton"));
        userProfile.setLocaleTags(Arrays.asList("eaton:country/europe/gb", "eaton:country/europe"));
        userProfile.setPartnerProgramTypeAndTierLevelTags(Arrays.asList("eaton-secure:partner-programme-type/electrical-installer-programme/premium", "eaton-secure:partner-programme-type/drives-solution-program"));
        userProfile.setUserOktaGroups(Collections.singletonList("eaton-secure:okta-group/revenera_pq_product-manager"));
        return userProfile;
    }

    public static RoleMappingObject roleMapping() {
        return new Gson().fromJson(new InputStreamReader(Objects.requireNonNull(UserProfileFixtures.class.getResourceAsStream("role-mapping.json"))), RoleMappingObject.class);
    }


}
