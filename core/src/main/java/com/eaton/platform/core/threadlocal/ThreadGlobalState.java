package com.eaton.platform.core.threadlocal;

import com.eaton.platform.core.bean.ProductFamilyPIMDetails;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to manage the ThreadLocal that keeps the ProductFamilyPIMDetails for a given key
 * use getProductFamilyPIMDetails to get the familyPIMDetails for a given key during current thread
 * use registerProductFamilyPIMDetails to add the familyPIMDetails for a given key to the global state for the current thread
 */
public class ThreadGlobalState {

    private static ThreadLocal<Map<String, ProductFamilyPIMDetails>> threadLocalForProductFamilyPimDetails;

    static ThreadLocal<Map<String, ProductFamilyPIMDetails>> getProductFamilyPIMDetails() {
        if (threadLocalForProductFamilyPimDetails == null) {
            threadLocalForProductFamilyPimDetails = new ThreadLocal<>();
        }
        return threadLocalForProductFamilyPimDetails;
    }

    static void releaseGlobalState() {
        threadLocalForProductFamilyPimDetails = null;
    }

    /**
     * To fetch the familyPIMDetails for a given key during current thread
     * @param key to fetch the PIM details for
     * @return the PIM details for given key
     */
    public static ProductFamilyPIMDetails getProductFamilyPIMDetails(String key) {
        if (threadLocalForProductFamilyPimDetails == null) {
            return null;
        }
        if (threadLocalForProductFamilyPimDetails.get() == null) {
            return null;
        }
        if (threadLocalForProductFamilyPimDetails.get().isEmpty()) {
            return null;
        }
        return threadLocalForProductFamilyPimDetails.get().get(key);
    }

    /**
     * To register the familyPIMDetails for a given key during current thread
     * @param key to use for registration
     * @param productFamilyPIMDetails to register
     */
    public static void registerProductFamilyPIMDetails(String key, ProductFamilyPIMDetails productFamilyPIMDetails) {
        threadLocalForProductFamilyPimDetails = getProductFamilyPIMDetails();
        if (threadLocalForProductFamilyPimDetails.get() == null) {
            threadLocalForProductFamilyPimDetails.set(new HashMap<>());
        }
        threadLocalForProductFamilyPimDetails.get().put(key, productFamilyPIMDetails);
    }
}
