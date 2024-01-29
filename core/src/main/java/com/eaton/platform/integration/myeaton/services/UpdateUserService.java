package com.eaton.platform.integration.myeaton.services;

import com.eaton.platform.integration.myeaton.dto.UserProfilePayload;
import com.eaton.platform.integration.myeaton.services.exception.UpdateUserInformationException;

public interface UpdateUserService {
    boolean updateUserInformation(UserProfilePayload userProfilePayload) throws UpdateUserInformationException;
}
