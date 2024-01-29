package com.eaton.platform.integration.auth.services;

import com.eaton.platform.integration.auth.models.AuthenticationToken;

public interface JwtVerificationService {

    /*
        Verifies JWT using JJWT Library and returns Eaton Secure Authentication Token
     */
    AuthenticationToken parseToken(String token);
}
