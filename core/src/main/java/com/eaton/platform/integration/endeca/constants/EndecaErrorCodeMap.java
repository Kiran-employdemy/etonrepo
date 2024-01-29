package com.eaton.platform.integration.endeca.constants;

import java.util.HashMap;
import java.util.Map;

public class EndecaErrorCodeMap {
	
	public final Map<String, String> productFamilyErrorMsgMap = createProductFamilyMap();
	
    private static Map<String, String> createProductFamilyMap()
    {
        Map<String,String> productFamilyMap = new HashMap<>();
       
        productFamilyMap.put(EndecaConstants.NO_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_038_STRING);
        productFamilyMap.put(EndecaConstants.INVALID_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_011_STRING);
        productFamilyMap.put(EndecaConstants.NO_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_012_STRING);
        productFamilyMap.put(EndecaConstants.INVALID_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_012_STRING);
        productFamilyMap.put(EndecaConstants.INVALID_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_013_STRING);
        productFamilyMap.put(EndecaConstants.NO_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_013_STRING);
        productFamilyMap.put(EndecaConstants.NO_SEARCH_TERMS_MSG, EndecaConstants.ERR_ENDECA_014_STRING);
        productFamilyMap.put(EndecaConstants.NO_LANGUAGE_VALUE, EndecaConstants.ERR_ENDECA_037_STRING);
        productFamilyMap.put(EndecaConstants.INVALID_FILTER_VALUE_MSG, EndecaConstants.ERR_ENDECA_015_STRING);
        productFamilyMap.put(EndecaConstants.NO_COUNTRY_MSG, EndecaConstants.ERR_ENDECA_015_STRING);
        productFamilyMap.put(EndecaConstants.NO_RETURNFACETS_MSG, EndecaConstants.ERR_ENDECA_015_STRING);
        productFamilyMap.put(EndecaConstants.NO_FACETS_MSG, EndecaConstants.ERR_ENDECA_015_STRING);
        productFamilyMap.put(EndecaConstants.NO_SORTBY_MSG, EndecaConstants.ERR_ENDECA_015_STRING);
        productFamilyMap.put(EndecaConstants.INVALID_ENTRY_MSG, EndecaConstants.ERR_ENDECA_021_STRING);
        return productFamilyMap;
    }

    
    public final Map<String, String> familyModuleErrorMsgMap = createFamilyModuleMap();
	
    private static Map<String, String> createFamilyModuleMap()
    {
        Map<String,String> familyModuleMap = new HashMap<>();
       
        familyModuleMap.put(EndecaConstants.NO_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_036_STRING);
        familyModuleMap.put(EndecaConstants.INVALID_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_006_STRING);
        familyModuleMap.put(EndecaConstants.INVALID_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_007_STRING);
        familyModuleMap.put(EndecaConstants.NO_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_007_STRING);
        familyModuleMap.put(EndecaConstants.INVALID_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_008_STRING);
        familyModuleMap.put(EndecaConstants.NO_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_008_STRING);
        familyModuleMap.put(EndecaConstants.NO_SEARCH_TERMS_MSG, EndecaConstants.ERR_ENDECA_009_STRING);
        familyModuleMap.put(EndecaConstants.NO_LANGUAGE_VALUE, EndecaConstants.ERR_ENDECA_035_STRING);
        familyModuleMap.put(EndecaConstants.INVALID_FILTER_VALUE_MSG, EndecaConstants.ERR_ENDECA_010_STRING);
        familyModuleMap.put(EndecaConstants.NO_COUNTRY_MSG, EndecaConstants.ERR_ENDECA_010_STRING);
        familyModuleMap.put(EndecaConstants.NO_RETURNFACETS_MSG, EndecaConstants.ERR_ENDECA_010_STRING);
        familyModuleMap.put(EndecaConstants.NO_SKUCARDPARAMETERS_MSG, EndecaConstants.ERR_ENDECA_010_STRING);
        familyModuleMap.put(EndecaConstants.NO_FACETS_MSG, EndecaConstants.ERR_ENDECA_010_STRING);
        familyModuleMap.put(EndecaConstants.NO_SORTBY_MSG, EndecaConstants.ERR_ENDECA_010_STRING);
        familyModuleMap.put(EndecaConstants.INVALID_ENTRY_MSG, EndecaConstants.ERR_ENDECA_020_STRING);
        return familyModuleMap;
    }
    
public final Map<String, String> skuDetailsErrorMsgMap = createSKUDetailsMap();
	
    private static Map<String, String> createSKUDetailsMap()
    {
        Map<String,String> skuDetailsMap = new HashMap<>();
       
        skuDetailsMap.put(EndecaConstants.NO_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_034_STRING);
        skuDetailsMap.put(EndecaConstants.INVALID_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_001_STRING);
        skuDetailsMap.put(EndecaConstants.INVALID_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_002_STRING);
        skuDetailsMap.put(EndecaConstants.NO_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_002_STRING);
        skuDetailsMap.put(EndecaConstants.INVALID_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_003_STRING);
        skuDetailsMap.put(EndecaConstants.NO_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_003_STRING);
        skuDetailsMap.put(EndecaConstants.NO_SEARCH_TERMS_MSG, EndecaConstants.ERR_ENDECA_004_STRING);
        skuDetailsMap.put(EndecaConstants.NO_LANGUAGE_VALUE, EndecaConstants.ERR_ENDECA_033_STRING);
        skuDetailsMap.put(EndecaConstants.INVALID_FILTER_VALUE_MSG, EndecaConstants.ERR_ENDECA_005_STRING);
        skuDetailsMap.put(EndecaConstants.NO_COUNTRY_MSG, EndecaConstants.ERR_ENDECA_005_STRING);
        skuDetailsMap.put(EndecaConstants.NO_MAXRELATIONSKU_MSG, EndecaConstants.ERR_ENDECA_005_STRING);
        skuDetailsMap.put(EndecaConstants.INVALID_ENTRY_MSG, EndecaConstants.ERR_ENDECA_019_STRING);
        return skuDetailsMap;
    }
    
public final Map<String, String> typeAheadErrorMsgMap = createTypeAheadMap();
	
    private static Map<String, String> createTypeAheadMap()
    {
        Map<String,String> typeAheadMap = new HashMap<>();
       
        typeAheadMap.put(EndecaConstants.NO_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_042_STRING);
        typeAheadMap.put(EndecaConstants.INVALID_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_023_STRING);
        typeAheadMap.put(EndecaConstants.INVALID_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_024_STRING);
        typeAheadMap.put(EndecaConstants.NO_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_024_STRING);
        typeAheadMap.put(EndecaConstants.INVALID_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_025_STRING);
        typeAheadMap.put(EndecaConstants.NO_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_025_STRING);
        typeAheadMap.put(EndecaConstants.NO_SEARCH_TERMS_MSG, EndecaConstants.ERR_ENDECA_039_STRING);
        typeAheadMap.put(EndecaConstants.NO_LANGUAGE_VALUE, EndecaConstants.ERR_ENDECA_040_STRING);
        typeAheadMap.put(EndecaConstants.INVALID_FILTER_VALUE_MSG, EndecaConstants.ERR_ENDECA_041_STRING);
        typeAheadMap.put(EndecaConstants.NO_COUNTRY_MSG, EndecaConstants.ERR_ENDECA_041_STRING);
        typeAheadMap.put(EndecaConstants.INVALID_ENTRY_MSG, EndecaConstants.ERR_ENDECA_031_STRING);
        return typeAheadMap;
    }
    
    
public final Map<String, String> siteSearchErrorMsgMap = createSiteSearchMap();
	
    private static Map<String, String> createSiteSearchMap()
    {
        Map<String,String> siteSearchMap = new HashMap<>();
       
        siteSearchMap.put(EndecaConstants.NO_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_046_STRING);
        siteSearchMap.put(EndecaConstants.INVALID_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_026_STRING);
        siteSearchMap.put(EndecaConstants.INVALID_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_027_STRING);
        siteSearchMap.put(EndecaConstants.NO_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_027_STRING);
        siteSearchMap.put(EndecaConstants.INVALID_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_028_STRING);
        siteSearchMap.put(EndecaConstants.NO_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_028_STRING);
        siteSearchMap.put(EndecaConstants.NO_SEARCH_TERMS_MSG, EndecaConstants.ERR_ENDECA_043_STRING);
        siteSearchMap.put(EndecaConstants.NO_LANGUAGE_VALUE, EndecaConstants.ERR_ENDECA_044_STRING);
        siteSearchMap.put(EndecaConstants.INVALID_FILTER_VALUE_MSG, EndecaConstants.ERR_ENDECA_045_STRING);
        siteSearchMap.put(EndecaConstants.NO_COUNTRY_MSG, EndecaConstants.ERR_ENDECA_045_STRING);
        siteSearchMap.put(EndecaConstants.NO_RETURNFACETS_MSG, EndecaConstants.ERR_ENDECA_045_STRING);
        siteSearchMap.put(EndecaConstants.NO_FACETS_MSG, EndecaConstants.ERR_ENDECA_045_STRING);
        siteSearchMap.put(EndecaConstants.NO_SORTBY_MSG, EndecaConstants.ERR_ENDECA_045_STRING);
        siteSearchMap.put(EndecaConstants.NO_AUTOCORRECT_MSG, EndecaConstants.ERR_ENDECA_045_STRING);
        siteSearchMap.put(EndecaConstants.INVALID_ENTRY_MSG, EndecaConstants.ERR_ENDECA_032_STRING);
        return siteSearchMap;
    }
    
public final Map<String, String> clutchSelectorErrorMsgMap = createClutchSelectorMap();
	
    private static Map<String, String> createClutchSelectorMap()
    {
        Map<String,String> clutchMsgMap = new HashMap<>();
       
        clutchMsgMap.put(EndecaConstants.NO_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_047_STRING);
        clutchMsgMap.put(EndecaConstants.INVALID_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_047_STRING);
        clutchMsgMap.put(EndecaConstants.INVALID_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_048_STRING);
        clutchMsgMap.put(EndecaConstants.NO_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_048_STRING);
        clutchMsgMap.put(EndecaConstants.INVALID_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_049_STRING);
        clutchMsgMap.put(EndecaConstants.NO_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_049_STRING);
        clutchMsgMap.put(EndecaConstants.NO_SEARCH_TERMS_MSG, EndecaConstants.ERR_ENDECA_050_STRING);
        clutchMsgMap.put(EndecaConstants.NO_LANGUAGE_VALUE, EndecaConstants.ERR_ENDECA_061_STRING);
        clutchMsgMap.put(EndecaConstants.INVALID_FILTER_VALUE_MSG, EndecaConstants.ERR_ENDECA_051_STRING);
        clutchMsgMap.put(EndecaConstants.NO_COUNTRY_MSG, EndecaConstants.ERR_ENDECA_051_STRING);
        clutchMsgMap.put(EndecaConstants.NO_RETURNFACETS_MSG, EndecaConstants.ERR_ENDECA_051_STRING);
        clutchMsgMap.put(EndecaConstants.NO_FACETS_MSG, EndecaConstants.ERR_ENDECA_051_STRING);
        clutchMsgMap.put(EndecaConstants.NO_SORTBY_MSG, EndecaConstants.ERR_ENDECA_051_STRING);
        clutchMsgMap.put(EndecaConstants.INVALID_ENTRY_MSG, EndecaConstants.ERR_ENDECA_059_STRING);
        
        return clutchMsgMap;
    }
    
public final Map<String, String> torqueSelectorErrorMsgMap = createTorqueSelectorMap();
	
    private static Map<String, String> createTorqueSelectorMap()
    {
        Map<String,String> torqueMsgMap = new HashMap<>();
       
        torqueMsgMap.put(EndecaConstants.NO_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_052_STRING);
        torqueMsgMap.put(EndecaConstants.INVALID_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_052_STRING);
        torqueMsgMap.put(EndecaConstants.INVALID_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_053_STRING);
        torqueMsgMap.put(EndecaConstants.NO_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_053_STRING);
        torqueMsgMap.put(EndecaConstants.INVALID_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_054_STRING);
        torqueMsgMap.put(EndecaConstants.NO_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_054_STRING);
        torqueMsgMap.put(EndecaConstants.NO_SEARCH_TERMS_MSG, EndecaConstants.ERR_ENDECA_055_STRING);
        torqueMsgMap.put(EndecaConstants.NO_LANGUAGE_VALUE, EndecaConstants.ERR_ENDECA_062_STRING);
        torqueMsgMap.put(EndecaConstants.INVALID_FILTER_VALUE_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        torqueMsgMap.put(EndecaConstants.NO_COUNTRY_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        torqueMsgMap.put(EndecaConstants.NO_RETURNFACETS_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        torqueMsgMap.put(EndecaConstants.NO_FACETS_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        torqueMsgMap.put(EndecaConstants.NO_SORTBY_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        torqueMsgMap.put(EndecaConstants.INVALID_ENTRY_MSG, EndecaConstants.ERR_ENDECA_060_STRING);
        
        return torqueMsgMap;
    }
    
public final Map<String, String> contentHubErrorMsgMap = createContentHubMap();
	
    private static Map<String, String> createContentHubMap()
    {
        Map<String,String> contentHubMsgMap = new HashMap<>();
       
        contentHubMsgMap.put(EndecaConstants.NO_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_052_STRING);
        contentHubMsgMap.put(EndecaConstants.INVALID_SEARCH_APPLICATION_MSG, EndecaConstants.ERR_ENDECA_052_STRING);
        contentHubMsgMap.put(EndecaConstants.INVALID_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_053_STRING);
        contentHubMsgMap.put(EndecaConstants.NO_SEARCH_APPLICATIONKEY_MSG, EndecaConstants.ERR_ENDECA_053_STRING);
        contentHubMsgMap.put(EndecaConstants.INVALID_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_054_STRING);
        contentHubMsgMap.put(EndecaConstants.NO_FUNCTION_MSG, EndecaConstants.ERR_ENDECA_054_STRING);
        contentHubMsgMap.put(EndecaConstants.NO_SEARCH_TERMS_MSG, EndecaConstants.ERR_ENDECA_055_STRING);
        contentHubMsgMap.put(EndecaConstants.NO_LANGUAGE_VALUE, EndecaConstants.ERR_ENDECA_062_STRING);
        contentHubMsgMap.put(EndecaConstants.INVALID_FILTER_VALUE_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        contentHubMsgMap.put(EndecaConstants.NO_COUNTRY_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        contentHubMsgMap.put(EndecaConstants.NO_RETURNFACETS_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        contentHubMsgMap.put(EndecaConstants.NO_FACETS_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        contentHubMsgMap.put(EndecaConstants.NO_SORTBY_MSG, EndecaConstants.ERR_ENDECA_056_STRING);
        contentHubMsgMap.put(EndecaConstants.INVALID_ENTRY_MSG, EndecaConstants.ERR_ENDECA_060_STRING);
        
        return contentHubMsgMap;
    } 
    
}
