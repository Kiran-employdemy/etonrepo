package com.eaton.platform.core.constants;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TagMapTest {

    private TagMap tagMap = new TagMap();

    @ParameterizedTest(name = "In the tag sync list {0} is mapped to {1} ")
    @MethodSource("getSourceForTest")
    void testGetTagSyncListLanguage(String key, String mapped) {
        String actual = tagMap.getTagSyncList().get(key);
        assertEquals(mapped, actual, String.format("The mapped value for key %s should be %s, but it was %s",key,mapped,actual));
    }

    static Stream<Arguments> getSourceForTest() {
        return Stream.of(
                arguments("eaton:language", "xmp:eaton-language"),
                arguments("eaton:eaton-geography", "xmp:eaton-geography"),
                arguments("eaton:news-releases/Topic", "xmp:eaton-topic"),
                arguments("eaton:eaton-persona", "xmp:eaton-persona"),
                arguments("eaton:eaton-audience", "xmp:eaton-audience"),
                arguments("eaton:eaton-customer-journey-stage", "xmp:eaton-customer-journey-stage"),
                arguments("eaton:resources", "xmp:eaton-content-type"),
                arguments("eaton:b-line-submittal-builder", "xmp:eaton-b-line-submittal-builder"),
                arguments("eaton:eaton-brand", "xmp:eaton-eaton-brand"),
                arguments("eaton:search-tabs", "xmp:eaton-search-tabs"),
                arguments("eaton:news-releases", "xmp:eaton-news"),
                arguments("eaton:product-taxonomy", "xmp:eaton-product-taxonomy"),
                arguments("eaton:services", "xmp:eaton-services-taxonomy"),
                arguments("eaton:support-taxonomy", "xmp:eaton-support-taxonomy"),
                arguments("eaton:product-attributes", "xmp:eaton-product-attributes"),
                arguments("eaton:supermarkets", "xmp:eaton-segment"),
                arguments("eaton-secure:product-category", "productCategories"),
                arguments("eaton-secure:accounttype", "accountType"),
                arguments("eaton-secure:application-access", "applicationAccess"),
                arguments("eaton:country", "xmp:eaton-country"),
                arguments("eaton:eaton-business-unit-function-division", "xmp:eaton-business-unit-function-division"),
                arguments("eaton-secure:partner-programme-type", "xmp:eaton-partner-program-and-tier-level"),
                arguments("eaton:myeaton-taxonomy", "xmp:eaton-myeaton-taxonomy")
        );
    }
}