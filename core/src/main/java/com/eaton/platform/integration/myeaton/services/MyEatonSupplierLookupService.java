/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services;

/** Supplier Lookup Service for My Eaton */
public interface MyEatonSupplierLookupService {
    /**
     Look up a user based on their Supplier Number
     * @param supplierNumber String Supplier Number of the Supplier being looked up
     * @return Response object containing details on the response
     */
    boolean supplierLookup(String supplierNumber);
}
