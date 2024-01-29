package com.eaton.platform.integration.auth.services;

import com.eaton.platform.integration.auth.models.UserProfile;

import org.apache.sling.api.SlingHttpServletRequest;
import org.json.JSONObject;

import java.util.Map;

public interface UserProfileService {
    UserProfile getUserProfile(String id, Long uniqueIdentifier);
     boolean updateUserTermsAndConditionsTimestamp(String id, Long uniqueCacheIdentifier);
    /**
     * This methods accepts oldPassword and newPassword as an input from the FORM and calls the USER API to update the
     * Password.
     * @param oldPassword - Old Password of the User
     * @param newPassword - New Password
     * @param userProfile - Current Logged-in Profile
     * @return JSONObject holding the response code and message.
     */
    JSONObject changePassword(String oldPassword, String newPassword, UserProfile userProfile);
    void removeUserCacheEntry(String id, Long uniqueIdentifier);
    void updateLastLogin(UserProfile userProfile);

    /**
     * This method is used to tack the assets which are marked as trackDownload = true.
     * @param userProfile Current Logged In Profile
     * @param request Sling request
     * @param links Asset Links
     * @return Return JSON Object as Response
     */
    JSONObject trackUserProfileForDownload(UserProfile userProfile, final SlingHttpServletRequest request, Map<String, String[]> links);
}
