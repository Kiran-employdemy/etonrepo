package com.eaton.platform.integration.auth.models.impl;

import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import org.apache.commons.lang.StringUtils;

public class SimpleAuthenticationTokenImpl implements AuthenticationToken {
    private final String subject;
    private final long expiration;
    private final String userLDAPId;
    private UserProfile userProfile;
    private boolean bypassAuthorization = false;
    private String uid;

    public SimpleAuthenticationTokenImpl(String subject, String userLDAPId, long expiration) {
        this.subject = subject;
        this.expiration = expiration;
        this.userLDAPId = userLDAPId;
    }

    public SimpleAuthenticationTokenImpl(boolean bypassAuthorization){
        this.bypassAuthorization = bypassAuthorization;
        this.subject = StringUtils.EMPTY;
        this.expiration = 0;
        this.userLDAPId = StringUtils.EMPTY;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getUserLDAPId() {
        return userLDAPId;
    }

    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public UserProfile getUserProfile() {
        return userProfile;
    }

    @Override
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public boolean getBypassAuthorization(){
        return bypassAuthorization;
    }

    @Override
    public String getUid(){
        return uid;
    }

    @Override
    public void setUid(String uid){
        this.uid=uid;
    }
}
