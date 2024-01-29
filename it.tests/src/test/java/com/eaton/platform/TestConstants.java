package com.eaton.platform;

public class TestConstants {
    private TestConstants () { }

    /* ---------- Content Paths ------- */
    public static final String EATON_CONTENT_PATH = "/content/eaton";
    public static final String PAGES_PATH = EATON_CONTENT_PATH + "/example/pages";
    public static final String EATON_SECURE_CONTENT_PATH = "/content/eaton/secure";

    /* ---------- Example Pages ------- */
    public static final String GENERIC_PAGE_PATH = PAGES_PATH + "/genericpage";

    /* ---------- Example Components -- */
    public static final String SUBMITTAL_INTRO_1 = EATON_CONTENT_PATH + "/submittalintro1";
    public static final String SUBMITTAL_INTRO_2 = EATON_CONTENT_PATH + "/submittalintro2";
    public static final String SUBMITTAL_INTRO_3 = EATON_CONTENT_PATH + "/submittalintro3";
    public static final String SUBMITTAL_INTRO_4 = EATON_CONTENT_PATH + "/submittalintro4";
    public static final String SUBMITTAL_BUILDER_1_PATH = EATON_CONTENT_PATH + "/submittalbuilder1";
    public static final String SUBMITTAL_BUILDER_2_PATH = EATON_CONTENT_PATH + "/submittalbuilder2";
    public static final String SUBMITTAL_BUILDER_3_PATH = EATON_CONTENT_PATH + "/submittalbuilder3";
    public static final String SUBMITTAL_BUILDER_4_PATH = EATON_CONTENT_PATH + "/submittalbuilder4";
    public static final String SUBMITTAL_DOWNLOAD_1 = EATON_CONTENT_PATH + "/submittaldownload1";
    public static final String SUBMITTAL_DOWNLOAD_2 = EATON_CONTENT_PATH + "/submittaldownload2";
    public static final String SUBMITTAL_DOWNLOAD_3 = EATON_CONTENT_PATH + "/submittaldownload3";
    public static final String SUBMITTAL_DOWNLOAD_4 = EATON_CONTENT_PATH + "/submittaldownload4";
    public static final String SUBMITTAL_FILTERS_1 = EATON_CONTENT_PATH + "/submittalfilters1";
    public static final String SUBMITTAL_FILTERS_2 = EATON_CONTENT_PATH + "/submittalfilters2";
    public static final String SUBMITTAL_FILTERS_3 = EATON_CONTENT_PATH + "/submittalfilters3";
    public static final String SUBMITTAL_FILTERS_4 = EATON_CONTENT_PATH + "/submittalfilters4";

    /* ---------- File Paths ---------- */
    public static final String EXAMPLE_COMPONENTS = "/ExampleComponents.json";
    public static final String EXAMPLE_PAGES = "/ExamplePages.json";

    /* ---------- Java Packages ------- */
    public static final String SUBMITTAL_BUILDER_MODELS_PACACKAGE = "com.eaton.platform.core.models.submittalbuilder";

    /*------------ Secure Constants ********* */

    public static final String PARENT_SECURE_PAGE = "/ParentSecurePagePropertiesMock.json";
    public static final String SECURE_ATTRIB_PATH = "/content/dam/eaton/secure/test";
    public static final String SECURE_PAGE = "/SecurePagePropertiesMock.json";
    public static final String SECURE_PAGE_MOCK = "/SecurePageMock.json";
    public static final String SECURE_LOGIN_ID = "etn-id";
    public static final String SECURE_SSOCOOKIE_ID = "iPlanetDirectoryPro";
    public static final String SECURE_JCR_CONTENT = "/content/eaton/secure/jcr:content";
    public static final String USER_AGENT = "User-Agent";
    public static final String ENDECA_USER_VAL = "endeca webcrawler/WebCrawler";
    public static final String SECURE_URI_PAGE = "/content/eaton/secure/en-us/securePage.html";
    public static final String SECURE_URI1 = "http://localhost:4502/content/eaton/us/en-us/okta-login.html?wcmmode=disabled";
    public static final String SECURE_URI2 = "http://localhost:4502/content/eaton/us/en-us.html";
    public static final String SECURE_USER_PROFILE = "src/test/resources/SecureUserProfile.json";
    public static final String SITE_SEARCH_RESPONSE = "src/test/resources/AdvancedSearchResponse.json";

    public static final String SECURE_LOGIN_TOKEN = "eyJraWQiOiJqaWFwMUxqSHAyeGNYVWZnakFxOUFGV0VnejBpUTE5djE2Z3MwcWM2V3dZIiwiYWxnIjoiUlMyNTYi"+
                                                    "fQ.eyJ2ZXIiOjEsImp0aSI6IkFULjVidmV0MW5zU1BGYmVNU1NIX0l1VFl5bjN4NkZoTTA4dmN0M3NYSGpLMG8iL"+
                                                    "CJpc3MiOiJodHRwczovL2lkLXFhLmVhdG9uLmNvbS9vYXV0aDIvYXVzd20waGxsMWR6N1NhVXcwaDciLCJhdWQi"+
                                                    "OiJhcGk6Ly9teWVhdG9uIiwiaWF0IjoxNjEyNTMxODMxLCJleHAiOjE2MTI1Njc4MzEsImNpZCI6IjBvYXY2aG92M"+
                                                    "XFmalFoZHBNMGg3IiwidWlkIjoiMDB1d2JwOW9oZndaSVQwbEMwaDciLCJzY3AiOlsib3BlbmlkIiwiZW1haWwiXSwiZ"+
                                                    "WF0b25pZCI6IkMzMDUyODMxIiwic3ViIjoiUGF0TWNMb3VnaGxpbkBlYXRvbi5jb20iLCJlYXRvbmVtYWlsIjoiUG"+
                                                    "F0TWNMb3VnaGxpbkBlYXRvbi5jb20ifQ.qm9VufwitBgbwtGM9nnFQaRAR-Wf1bnvyIBWr_5aVzGGES6mBPsBXoOy"+
                                                    "Qfevtpe-TShT3P9kttWnJTke8Q5BMB0Vjla77vqFql9chytfZI1uFjt3xoTbkTdUF5Lli82hYL5-"+
                                                    "utjCiq8_oa6V8oVrWhCMJ1JaB2LCh3vCjNYPrXog1RebJwk2AgweIDsms7PQZ2vfeBj1d0_d7Lg6-7ALdsoLtlYQeE3IG"+
                                                    "B1fKLXz6LTWJvCz5IqGNEw7LmYSjsHu0tgHy1KdMtkfBJmVgkHfuiJFtfWMVT3D-"+
                                                    "BnIk7_ymK_eofDftbNHvpyhuJkSeQEyLd5uZFElzV6GwGNYwZ1uvw";
    
    public static final String SECURE_SSOCOOKIE_TOKEN = "GWfAQTdfIpDU6RvJ3EMTNzP8pvf%2BaGpYswAatDN7sZAvQxeieOoE8NCfCEj1iW6N11oQ0AbQqiLGzcJZigdIVqSM"
    													+ "ISIOtGP3a7OogX97QWjddwrzco4RRyEgdUJcGsngvYwN%2FzRIevFwv5SgxaeEmYwN1SDyVb%2BBStq1vXWTsqLh"
    													+ "3i%2BrWU6ybFN0k8OT1vbP1mjwvsC%2FKgsB6xRPXQ%2BMrSAkGKcu0IEWgxMjIUafXsgxCoz%2FFksgZOMCcwqY"
    													+ "CMPhW911SxySBX5t7tF1%2BNWbLHKA8SbNUNUhRhBbi1YLA6k%3D";
    

    public static final String SECURE_ATTRIB_ASSET = "/SecureAttributesServletMock.json";

    public static final String SECURE_LINK_PAGE_MOCK = "/SecureLinkCTAPropertiesMock.json";

    public static final String SECURE_TEST_PAGE_PATH = "/content/en-us/secure/test-page";
    public static final String ACCOUNTTYPE = "eaton-secure:accounttype/contractor";
    public static final String BUSSGROUP = "eaton-secure:business-group/electrical-amer";
    public static final String PRODCAT = "eaton-secure:product-category/aerospace";
    public static final String APPACC = "eaton-secure:application-access/bid-manager";
    public static final String TIER = "eaton-secure:tier-level/qualified";
    public static final String PROGRAM_TYPE = "eaton-secure:partner-programme-type/electrical-wholesaler-programme";
    public static final String SECURE_USERPROFILE = "secure.userProfile";
    public static final String USERPROFILE = "userProfile";

}
