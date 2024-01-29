package com.eaton.platform.integration.auth.services;

import com.eaton.platform.integration.auth.models.AuthenticationToken;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

public interface AuthorizationService {

    /***
     * Takes an AuthenticationToken and places on sling request for consumption further down processing chain.
     * Primarily for consumption by components.
     * @param slingRequest - Request to put authenticationToken on
     * @param authenticationToken
     */
    void setTokenOnSlingRequest(SlingHttpServletRequest slingRequest, AuthenticationToken authenticationToken);

    /***
     * Takes an AuthenticationToken and places on sling request for consumption further down processing chain.
     * Primarily for setting User Profile XML in Sling Request (To Pre-Populate values on Profile Update Forms)
     * @param slingRequest - Request to put authenticationToken on
     * @param authenticationToken
     */
    void setProfileJSONOnSlingRequest(SlingHttpServletRequest slingRequest, AuthenticationToken authenticationToken);

    /***
     * Retrieves an AuthenticationToken from sling request for consumption further down processing chain.
     * Primarily for consumption by components.
     * @param slingRequest - Request to put authenticationToken on
     * @return - AuthenticationToken present on the request for signed in user.  Null if no user signed in.
     */
    AuthenticationToken getTokenFromSlingRequest(SlingHttpServletRequest slingRequest);


    /***
     * Retrieves an AuthenticationToken from sling request for consumption further down processing chain.
     * Primarily for consumption by components.
     * @param slingRequest - Request to put authenticationToken on
     * @return - Auth Profile XML Data.
     */
    String getProfileJSONFromSlingRequest(SlingHttpServletRequest slingRequest);

    /***
     * Checks to see if the logged in user has access to the requested resource, retrieves the user profile via the
     * authentication token on the sling request
     * @param slingRequest - The sling request containing the authentication token for the user being checked
     * @param resourcePath - The path to the resource that authorization should be checked on.
     * @return - True - user authorized, False - user is not authorized
     */
    boolean isAuthorized(SlingHttpServletRequest slingRequest,String resourcePath);

    /***
     * Checks to see if the passed in user associated with the authentication token is authorized for the resource path.
     * @param authenticationToken - The authentication token for the user being checked
     * @param resource - The resource that authorization should be checked on.
     * @return - True - user authorized, False - user is not authorized
     */
    boolean isAuthorized(AuthenticationToken authenticationToken, Resource resource);

    /***
     * Checks to see if the logged in user has access to the requested resource, retrieves the user profile via the
     * authentication token on the sling request
     * @param slingRequest - Request to put authenticationToken on
     * @return - True - AuthenticationToken present, False - AuthenticationToken notPresent.
     */
     boolean isAuthenticated(SlingHttpServletRequest request);


}
