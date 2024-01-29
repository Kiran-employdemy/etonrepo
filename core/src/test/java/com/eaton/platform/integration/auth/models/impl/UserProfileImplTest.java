package com.eaton.platform.integration.auth.models.impl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.integration.auth.models.Eatoncustsite;

@ExtendWith(MockitoExtension.class)
 class UserProfileImplTest {
    
    @InjectMocks
    UserProfileImpl userProfileImpl;

    @Mock
    private Eatoncustsite eatoncustsite;
    
    @Test
    void testSetEatoncustsite() {
       userProfileImpl=new UserProfileImpl();
       userProfileImpl=mock(UserProfileImpl.class);
       userProfileImpl.setEatonCustSite(eatoncustsite);
       Eatoncustsite testEatoncustsite = userProfileImpl.getEatonCustSite();
       assertEquals(testEatoncustsite, userProfileImpl.getEatonCustSite()); 
       userProfileImpl.setEatonCustSite(testEatoncustsite);
           }
    @Test
    void testGetEatoncustsite() {
       userProfileImpl=new UserProfileImpl();
       assertNull(userProfileImpl.getEatonCustSite(), "should be null");
       userProfileImpl.getEatonCustSite();
    }
}
