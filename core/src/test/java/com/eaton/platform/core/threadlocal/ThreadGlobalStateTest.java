package com.eaton.platform.core.threadlocal;

import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class ThreadGlobalStateTest {

    private static final String KEY = "key";
    private static final String OTHER_KEY = "other key";
    @Mock
    ProductFamilyPIMDetails productFamilyPIMDetails;
    @BeforeEach
    void resetThreadGlobalState(){
        ThreadGlobalState.releaseGlobalState();
    }

    @Test
    @DisplayName("getProductFamilyDetails, if ThreadLocal is null, returns null for a given key")
    void testGetProductFamilyPIMDetailsThreadLocalNull() {
        Assertions.assertNull(ThreadGlobalState.getProductFamilyPIMDetails(KEY), "Should be null");
    }

    @Test
    @DisplayName("getProductFamilyDetails, if ThreadLocal is not null, if the Map on that ThreadLocal is null, returns null for a given key")
    void testGetProductFamilyPIMDetailsMapOnThreadLocalNull() {
        ThreadGlobalState.getProductFamilyPIMDetails().set(null);

        Assertions.assertNull(ThreadGlobalState.getProductFamilyPIMDetails(KEY), "Should be null");
    }

    @Test
    @DisplayName("getProductFamilyDetails: ThreadLocal is not null, Map on ThreadLocal is not null, empty map returns null for given key")
    void testGetProductFamilyPIMDetailsEmptyMap() {
        ThreadGlobalState.getProductFamilyPIMDetails().set(new HashMap<>());

        Assertions.assertNull(ThreadGlobalState.getProductFamilyPIMDetails(KEY), "Should be null");
    }

    @Test
    @DisplayName("getProductFamilyDetails: ThreadLocal is not null, Map on ThreadLocal is not null, no enntry for given key, returns null")
    void testGetProductFamilyPIMDetailsNoEntryForGivenKey() {
        HashMap<String, ProductFamilyPIMDetails> productFamilyPIMDetailsHashMap = new HashMap<>();
        productFamilyPIMDetailsHashMap.put(OTHER_KEY, productFamilyPIMDetails);
        ThreadGlobalState.getProductFamilyPIMDetails().set(productFamilyPIMDetailsHashMap);

        Assertions.assertNull(ThreadGlobalState.getProductFamilyPIMDetails(KEY), "Should be null");
    }

    @Test
    @DisplayName("getProductFamilyDetails: ThreadLocal is not null, Map on ThreadLocal is not null, enntry for given key, returns value for that key")
    void testGetProductFamilyPIMDetailsEntryForGivenKey() {
        HashMap<String, ProductFamilyPIMDetails> productFamilyPIMDetailsHashMap = new HashMap<>();
        productFamilyPIMDetailsHashMap.put(KEY, productFamilyPIMDetails);
        ThreadGlobalState.getProductFamilyPIMDetails().set(productFamilyPIMDetailsHashMap);

        Assertions.assertSame(this.productFamilyPIMDetails, ThreadGlobalState.getProductFamilyPIMDetails(KEY), "Should be the value for given key");
    }

    @Test
    @DisplayName("registerProductFamilyDetails: ThreadLocal is null, creates the ThreadLocal")
    void testRegisterProductFamilyPIMDetailsThreadLocalNull(){
        ThreadGlobalState.registerProductFamilyPIMDetails(KEY, productFamilyPIMDetails);
        ThreadLocal<Map<String, ProductFamilyPIMDetails>> threadLocal = ThreadGlobalState.getProductFamilyPIMDetails();
        Assertions.assertNotNull(threadLocal, "should not be null");
    }
    @Test
    @DisplayName("registerProductFamilyDetails: ThreadLocal not null, Map on ThreadLocal null, creates the ThreadLocal and HashMap")
    void testRegisterProductFamilyPIMDetailsHahsMapNull(){
        ThreadGlobalState.getProductFamilyPIMDetails().set(null);
        ThreadGlobalState.registerProductFamilyPIMDetails(KEY, productFamilyPIMDetails);
        Map<String, ProductFamilyPIMDetails> hashMap = ThreadGlobalState.getProductFamilyPIMDetails().get();
        Assertions.assertNotNull(hashMap, "should not be null");
    }
    @Test
    @DisplayName("registerProductFamilyDetails: ThreadLocal not null, Map on ThreadLocal not null, puts the key and pimDetails to HashMap")
    void testRegisterProductFamilyPIMDetailsHahsMapNotNull(){
        ThreadGlobalState.getProductFamilyPIMDetails().set(new HashMap<>());
        ThreadGlobalState.registerProductFamilyPIMDetails(KEY, productFamilyPIMDetails);
        Map<String, ProductFamilyPIMDetails> hashMap = ThreadGlobalState.getProductFamilyPIMDetails().get();
        Assertions.assertSame(productFamilyPIMDetails, hashMap.get(KEY), "should be same as registered for this key");
    }

}