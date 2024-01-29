package com.eaton.platform.integration.auth.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.integration.auth.models.Eatoncustsite;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.models.UserProfileResponse;
import com.eaton.platform.integration.auth.models.impl.UserProfileImpl;

@ExtendWith( MockitoExtension.class )
public class EatonAuthUtilTest {
    
    @InjectMocks
    EatonAuthUtil eatonAuthUtil;

    @Mock
    UserProfileResponse userProfileResponse;

    @InjectMocks
    Eatoncustsite eatoncustsite;

    UserProfile userProfile;
    @Test
    void testGetUserProfile() {
       userProfile=new UserProfileImpl();
       userProfile.setEatonCustSite(userProfileResponse.getEatoncustsite());
       Eatoncustsite testEatoncustsite=userProfile.getEatonCustSite();
       assertEquals(testEatoncustsite, userProfile.getEatonCustSite());
       EatonAuthUtil.getUserProfile(userProfileResponse);

    }
  
}
