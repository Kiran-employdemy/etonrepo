package com.eaton.platform.integration.auth.models;

public interface AuthenticationToken {
    String getSubject();

    long getExpiration();

    UserProfile getUserProfile();

    String getUserLDAPId();

    void setUserProfile(UserProfile userProfile);

    boolean getBypassAuthorization();

    String getUid();

    void setUid(String uid);
}
