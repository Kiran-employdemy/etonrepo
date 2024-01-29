package com.eaton.platform.integration.auth.services;

import com.eaton.platform.integration.auth.models.AuthenticationToken;

public interface AuthenticationService {

    /*
    Exchanges the Okta Authorization Code for JWT
     */
    String requestToken(String authorizationCode);

    /*
    Verifies JWT and returns Eaton Secure Authentication Token
     */
    AuthenticationToken parseToken(String token);
}
