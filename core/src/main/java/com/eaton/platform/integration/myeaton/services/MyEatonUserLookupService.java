/*
 * Eaton
 * Copyright (C) 2020 Eaton. All Rights Reserved
 */

package com.eaton.platform.integration.myeaton.services;

/** User Lookup Service for My Eaton */
public interface MyEatonUserLookupService {
    /**
     Look up a user based on their email address
     * @param userEmail String email address of the user being looked up
     * @return Response object containing details on the response
     */
    boolean userLookup(String userEmail);
}
