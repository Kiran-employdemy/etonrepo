package com.eaton.platform.core.models.resourcelistpdh;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.AttrNmBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceListPDHModelTest {

    public static final String LANGUAGE_LIST = "am_ET,ar_AE,az_AZ,bg_BG,bs_BA,ca_ES,cs_CZ,da_DK,de_DE,eg_EG,el_GR,en_GB,en_US,es_ES,es_MX,es_US,et_EE,fi_FI,fr_CA,fr_FR,he_IL,hr_HR,hu_HU,hy_AM,id_ID,is_IS,it_IT,ja_JP,kk_KZ,ko_KR,lt_LT,lv_LV,mk_MK,ms_MY,mt_MT,nl_NL,no_NO,pl_PL,pt_BR,pt_PT";
    public static final String COUNTRY_LIST = "US,MX,CA";
    @InjectMocks
    private ResourceListPDHModel resourceListPDHModel = new ResourceListPDHModel();
    @Mock
    AttrNmBean attrNmBean;
    @Mock
    Page currentPage;
    @Mock
    Page pageAtLevel2;

    @Test
    @DisplayName("when the current page is null, the document must be skipped")
    void testIsDocumentEligibleCurrentPageNull() {
        Assertions.assertFalse(new ResourceListPDHModel().isDocumentEligible(attrNmBean), "should be false");
    }

    @Test
    @DisplayName("when the language in the AttrNmBean is null, the document must be skipped")
    void testIsDocumentEligibleLanguageNull() {
        when(attrNmBean.toBeDisplayed()).thenReturn(true);
        when(attrNmBean.getLanguage()).thenReturn(null);
        Assertions.assertFalse(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be false");
    }

    @Test
    @DisplayName("when the country in the AttrNmBean is null, the document must be skipped")
    void testIsDocumentEligibleCountryNull() {
        when(attrNmBean.getLanguage()).thenReturn("global");
        when(attrNmBean.toBeDisplayed()).thenReturn(true);
        when(attrNmBean.getCountry()).thenReturn(null);
        Assertions.assertFalse(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be false");
    }

    @Test
    @DisplayName("when the language and country in the AttrNmBean is Global, the document must be included")
    void testIsDocumentEligibleGlobal() {
        when(currentPage.getLanguage(false)).thenReturn(new Locale("en", "us"));
        when(attrNmBean.toBeDisplayed()).thenReturn(true);
        when(attrNmBean.getLanguage()).thenReturn("Global");
        when(attrNmBean.getCountry()).thenReturn("Global");
        Assertions.assertTrue(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be true");
    }

    @Test
    @DisplayName("when the country is global and current language is in the language list, the document must be included")
    void testIsDocumentEligibleCountryGlobalCurrentLanguageInList() {
        when(currentPage.getLanguage(false)).thenReturn(new Locale("en", "us"));
        when(attrNmBean.toBeDisplayed()).thenReturn(true);
        when(attrNmBean.getLanguage()).thenReturn(LANGUAGE_LIST);
        when(attrNmBean.getCountry()).thenReturn("Global");
        Assertions.assertTrue(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be true");
    }

    @Test
    @DisplayName("when the language is global and current country is in the country list, the document must be included")
    void testIsDocumentEligibleLanguageGlobalCurrentCountryInList() {
        when(currentPage.getLanguage(false)).thenReturn(new Locale("en", "gb"));
        when(currentPage.getAbsoluteParent(2)).thenReturn(pageAtLevel2);
        when(pageAtLevel2.getName()).thenReturn("ca");
        when(attrNmBean.toBeDisplayed()).thenReturn(true);
        when(attrNmBean.getLanguage()).thenReturn("Global");
        when(attrNmBean.getCountry()).thenReturn(COUNTRY_LIST);
        Assertions.assertTrue(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be true");
    }
    @Test
    @DisplayName("when the current language is int the list and current country is in the country list, the document must be included")
    void testIsDocumentEligibleLanguageInListAndCurrentCountryInList() {
        when(currentPage.getLanguage(false)).thenReturn(new Locale("en", "gb"));
        when(currentPage.getAbsoluteParent(2)).thenReturn(pageAtLevel2);
        when(pageAtLevel2.getName()).thenReturn("ca");
        when(attrNmBean.toBeDisplayed()).thenReturn(true);
        when(attrNmBean.getLanguage()).thenReturn(LANGUAGE_LIST);
        when(attrNmBean.getCountry()).thenReturn(COUNTRY_LIST);
        Assertions.assertTrue(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be true");
    }

    @Test
    @DisplayName("when the current language is not the list and current country is in the country list, the document must be skipped")
    void testIsDocumentEligibleLanguageNotInListAndCurrentCountryInList() {
        when(currentPage.getLanguage(false)).thenReturn(new Locale("en", "nl"));
        when(currentPage.getAbsoluteParent(2)).thenReturn(pageAtLevel2);
        when(pageAtLevel2.getName()).thenReturn("ca");
        when(attrNmBean.toBeDisplayed()).thenReturn(true);
        when(attrNmBean.getLanguage()).thenReturn(LANGUAGE_LIST);
        when(attrNmBean.getCountry()).thenReturn(COUNTRY_LIST);
        Assertions.assertFalse(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be false");
    }

    @Test
    @DisplayName("when the current language is in the list and current country is not in the country list, the document must be skipped")
    void testIsDocumentEligibleLanguageInListAndCurrentCountryNotInList() {
        when(currentPage.getLanguage(false)).thenReturn(new Locale("en", "gb"));
        when(currentPage.getAbsoluteParent(2)).thenReturn(pageAtLevel2);
        when(pageAtLevel2.getName()).thenReturn("nl");
        when(attrNmBean.toBeDisplayed()).thenReturn(true);
        when(attrNmBean.getLanguage()).thenReturn(LANGUAGE_LIST);
        when(attrNmBean.getCountry()).thenReturn(COUNTRY_LIST);
        Assertions.assertFalse(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be false");
    }

    @Test
    @DisplayName("when the current language is not in the list and current country is not in the country list, the document must be skipped")
    void testIsDocumentEligibleLanguageNotInListAndCurrentCountryNotInList() {
        when(currentPage.getLanguage(false)).thenReturn(new Locale("en", "nl"));
        when(currentPage.getAbsoluteParent(2)).thenReturn(pageAtLevel2);
        when(pageAtLevel2.getName()).thenReturn("nl");
        when(attrNmBean.toBeDisplayed()).thenReturn(true);
        when(attrNmBean.getLanguage()).thenReturn(LANGUAGE_LIST);
        when(attrNmBean.getCountry()).thenReturn(COUNTRY_LIST);
        Assertions.assertFalse(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be false");
    }
    @Test
    @DisplayName("when the DisplayFlagValuein the AttrNmBean is N, the document must be skipped")
    void testIsDocumentEligibleDisplayFlagValueIsN(){
        when(attrNmBean.toBeDisplayed()).thenReturn(false);
       Assertions.assertFalse(resourceListPDHModel.isDocumentEligible(attrNmBean), "should be false");
    }
}