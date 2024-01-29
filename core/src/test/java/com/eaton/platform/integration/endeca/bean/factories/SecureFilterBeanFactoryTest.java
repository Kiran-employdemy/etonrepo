package com.eaton.platform.integration.endeca.bean.factories;

import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.eaton.platform.core.services.secure.impl.UserProfileFixtures.pptestAtCompanyWithMappedTags;
import static com.eaton.platform.integration.endeca.bean.FilterBeanFixtures.filterBean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecureFilterBeanFactoryTest {


    @Mock
    AuthenticationToken authenticationToken;

    SecureFilterBeanFactory secureFilterBeanFactory = new SecureFilterBeanFactory();

    @Test
    @DisplayName("Passing null as a authenticationToken should return an empty list.")
    void testSetEndecaSecureRequestContextEndecaServiceAuthenticationTokenNull() {
        Assertions.assertEquals(Collections.emptyList(),
                        secureFilterBeanFactory.createFilterBeans(null, SecureModule.PRODUCTGRID)
                , "should throw an IllegalStateException, passing null as authentication token is not allowed");
    }

    @Test
    @DisplayName("Passing null as a SecureModule should throw an IllegalStateException, this not allowed")
    void testSetEndecaSecureRequestContextEndecaServiceRequestModuleNull() {
        Assertions.assertThrows(IllegalStateException.class,() ->
                        secureFilterBeanFactory.createFilterBeans(authenticationToken, null)
                , "should throw an IllegalStateException, passing null as module is not allowed");
    }

    @Test
    @DisplayName("module = ProductGrid, all expected security filter beans present")
    void testSetEndecaSecureRequestContextProductGrid() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(pptestAtCompanyWithMappedTags());
        List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.PRODUCTGRID);
        assertEquals(getExpectedFilterBeansWithGB(), filterBeans, "filterBeans should contain the correct security beans");
    }

    @Test
    @DisplayName("module = ProductGrid for author, all expected security filter beans present")
    void testSetEndecaSecureRequestContextProductGridForAuthor() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(pptestAtCompanyWithMappedTags());
        List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.PRODUCTGRID_FOR_AUTHOR);
        assertEquals(getExpectedFilterBeansWithGB(), filterBeans, "filterBeans should contain the correct security beans");
    }

    @Test
    @DisplayName("module = ProductGrid, 1 bypass security filter bean present")
    void testSetEndecaSecureRequestContextProductGridBypassSecurity() {
        when(authenticationToken.getBypassAuthorization()).thenReturn(true);
        List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.PRODUCTGRID);
        assertEquals(getExpectedBypassSecurityBean(), filterBeans, "filterBeans should contain the correct security beans");
    }

    @Test
    @DisplayName("module = SecureAssetSearch, all expected security filter beans present")
    void testSetEndecaSecureRequestContextSecureAssetSearch() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(pptestAtCompanyWithMappedTags());
        List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.SECUREASSETSEARCH);
        assertEquals(getExpectedFilterBeansWithGbAndEurope(), filterBeans, "filterBeans should contain the correct security beans");
    }

    @Test
    @DisplayName("module = SecureAssetSearch and when bypassing security, 1 bypass security filter bean present")
    void testSetEndecaSecureRequestContextSecureAssetSearchBypassSecurity() {
        when(authenticationToken.getBypassAuthorization()).thenReturn(true);
        List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.SECUREASSETSEARCH);
        assertEquals(getExpectedBypassSecurityBean(), filterBeans, "filterBeans should contain the correct security beans");
    }

	@Test
	@DisplayName("module = SecureAssetSearch and filter values are present")
	void testSetEndecaSecureRequestContextSecureAssetSearchFilterValuesNotEmpty() throws IOException {
		UserProfile userProfile = pptestAtCompanyWithMappedTags();
		// make sure one filter will have no values from user profile
		userProfile.setAccountTypeTags(Collections.emptyList());

		when(authenticationToken.getUserProfile()).thenReturn(userProfile);

		List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.SECUREASSETSEARCH);

		filterBeans.forEach(f -> assertEquals(false, f.getFilterValues().isEmpty(), "filter values should not be empty"));
	}

    @Test
    @DisplayName("module = SecureSearch, all expected security filter beans present")
    void testSetEndecaSecureRequestContextSecureSearch() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(pptestAtCompanyWithMappedTags());
        List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.SECURESEARCH);
        assertEquals(getExpectedFilterBeansWithGbAndEurope(), filterBeans, "filterBeans should contain the correct security beans");
    }

    @Test
    @DisplayName("module = SecureSearch and when bypassing security, 1 bypass security filter bean present")
    void testSetEndecaSecureRequestContextSecureSearchBypassSecurity() {
        when(authenticationToken.getBypassAuthorization()).thenReturn(true);
        List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.SECURESEARCH);
        assertEquals(getExpectedBypassSecurityBean(), filterBeans, "filterBeans should contain the correct security beans");
    }

    @Test
    @DisplayName("module = SecureTypeAhead, all expected security filter beans present")
    void testSetEndecaSecureRequestContextSecureTypeAhead() throws IOException {
        when(authenticationToken.getUserProfile()).thenReturn(pptestAtCompanyWithMappedTags());
        List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.SECURETYPEAHEAD);
        assertEquals(getExpectedFilterBeansWithGbAndEurope(), filterBeans, "filterBeans should contain the correct security beans");
    }

    @Test
    @DisplayName("module = SecureTypeAhead and bypassing security, 1 bypass security filter bean present")
    void testSetEndecaSecureRequestContextSecureTypeAheadBypassSecurity() {
        when(authenticationToken.getBypassAuthorization()).thenReturn(true);
        List<FilterBean> filterBeans = secureFilterBeanFactory.createFilterBeans(authenticationToken, SecureModule.SECURETYPEAHEAD);
        assertEquals(getExpectedBypassSecurityBean(), filterBeans, "filterBeans should contain the correct security beans");
    }

    private List<FilterBean> getExpectedBypassSecurityBean() {
        return Collections.singletonList(filterBean("SEC-Bypass-Flag", "true"));
    }

    private List<FilterBean> getExpectedFilterBeansWithGB() {
        return getExpectedFilterBeansInserting(filterBean("SEC-country", "GB"));
    }

    private List<FilterBean> getExpectedFilterBeansWithGbAndEurope() {
        return getExpectedFilterBeansInserting(filterBean("SEC-country", "gb", "europe"));
    }

    private List<FilterBean> getExpectedFilterBeansInserting(FilterBean filterBeanToInsert) {
        return Lists.newArrayList(
                filterBean("SEC-Account-Type", "contractor", "end-customer"),
                filterBean("SEC-Application-Access", "eaton-university", "new-myeaton"),
                filterBean("SEC-Product-Categories", "engine-build-up-solutions", "engine-oil-system"),
                filterBeanToInsert,
                filterBean("SEC-Partner-Program-And-Tier-Level", "electrical-installer-programme_premium", "drives-solution-program")
        );
    }


}