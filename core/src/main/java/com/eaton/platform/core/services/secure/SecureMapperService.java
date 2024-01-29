package com.eaton.platform.core.services.secure;

import com.eaton.platform.integration.auth.models.UserProfile;

public interface SecureMapperService {

    /**
     *
     * @param key - Role value
     * @return Mapped AEM tag based on the current role.
     */
    public String getRoleBasedKeys(String key, String secureCategory);

    /**
     * This method returns UserProfile object with Mapped AEM Tags based on the Roles from USER API Service
     * @param userProfile - UserProfile object filled basic profile information
     * @return UserProfile MappedObject
     */
    public UserProfile mapSecureTags(UserProfile userProfile);

    /**
     * This method loads updated secure mapper file from DAM.
     */
    public void loadUpdatedSecureMapperFile();
}
