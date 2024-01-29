package com.eaton.platform.core.models;

import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.AuthorizationService;





@ExtendWith(MockitoExtension.class)
public class FreeSampleButtonModelTest {

    @InjectMocks
    FreeSampleButtonModel freeSampleButtonModel;
    @Mock
    SlingHttpServletRequest request;
    @Mock
    AuthorizationService authorizationService;
    @Mock
    AuthenticationServiceConfiguration authenticationServiceConfiguration;
    @Mock
    Page currentPage;
    private static final String LOGIN_URL="https://login-sit.tcc.etn.com/us/en-us/login.html";
	String unauthorizedsamplebutton = "Add a free sample to the cart";
	String authorizedsamplebutton = "Add a free sample to the cart";


    @Test
     void testCTADetailsForLoggedInUser() throws NoSuchFieldException{
        when(authorizationService.isAuthenticated(request)).thenReturn(true);
        freeSampleButtonModel.init();
	    Assert.assertEquals(null, freeSampleButtonModel.getFreeSampleCTALink());
	    Assert.assertEquals(authorizedsamplebutton, freeSampleButtonModel.getFreeSampleCTALabel());
  
     }
    
    @Test
    void testCTADetailsForAnonymousUser() throws NoSuchFieldException{
       when(authorizationService.isAuthenticated(request)).thenReturn(false);
       when(authenticationServiceConfiguration.getOktaLoginURI()).thenReturn(LOGIN_URL);
      freeSampleButtonModel.init();
       Assert.assertEquals(LOGIN_URL, freeSampleButtonModel.getFreeSampleCTALink());
       Assert.assertEquals(unauthorizedsamplebutton, freeSampleButtonModel.getFreeSampleCTALabel());
      
  
    }


}
