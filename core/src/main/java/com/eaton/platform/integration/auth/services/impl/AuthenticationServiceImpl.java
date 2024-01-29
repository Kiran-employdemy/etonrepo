package com.eaton.platform.integration.auth.services.impl;

import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.auth.models.*;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.services.JwtVerificationService;
import com.eaton.platform.integration.auth.services.UserProfileService;
import com.eaton.platform.integration.auth.util.EatonAuthUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = AuthenticationService.class,immediate = true)
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Reference
    protected AuthenticationServiceConfiguration authenticationServiceConfiguration;

    @Reference
    protected UserProfileService profileService;

    @Reference
    protected JwtVerificationService jwtVerificationService;

    @Reference
    protected HttpClientBuilderFactory httpFactory;

    private String tokenUrl;

    private PoolingHttpClientConnectionManager conMgr;

    @Override
    public String requestToken(String authorizationCode){
        LOG.debug("Start with requestToken method :: AuthenticationServiceImpl");
        String rawJWT = "";

        conMgr = EatonAuthUtil.getMultiThreadedConf(conMgr, authenticationServiceConfiguration);
        String responseContent = EatonAuthUtil.callGetTokenAPI(tokenUrl, conMgr, httpFactory,authenticationServiceConfiguration,authorizationCode);

        try {
            if(!StringUtils.isAllEmpty(responseContent)) {
                JSONObject responseJSON = new JSONObject(responseContent);
                if (responseJSON.getString(AuthConstants.ACCESS_TOKEN_KEY) != null) {
                    rawJWT = responseJSON.getString(AuthConstants.ACCESS_TOKEN_KEY);
                    LOG.debug("AccessToken received from getToken API call :: {}", rawJWT);
                }else{
                    LOG.error("Access Token missing in response received from getToken API call.");
                }
            }else{
                LOG.error("Empty response received from getToken API call.");
            }
        } catch (JSONException e) {
            LOG.error("JSONException while parsing the JSON response from getToken API call : {}", e.getMessage());
        }

        LOG.debug("End with requestToken method :: AuthenticationServiceImpl");
        return rawJWT;
    }

    @Override
    public AuthenticationToken parseToken(String token) {

        LOG.debug("Start with parseToken method :: AuthorizationServiceImpl");
        LOG.debug("Token to be parsed :: {}", token);
        AuthenticationToken authenticationToken = null;

        if(StringUtils.isNotEmpty(token)) {
            LOG.debug("Parsing token via JWT Verification service.");
            authenticationToken = jwtVerificationService.parseToken(token);

            if ((authenticationToken !=null) && (authenticationToken.getUserLDAPId() != null)) {
                LOG.debug("Token verification successful. UserLDAPId retrieved :: {}",authenticationToken.getUserLDAPId());
                UserProfile userProfile = profileService.getUserProfile(authenticationToken.getUserLDAPId(),authenticationToken.getExpiration());

                //If the user profile exists return the simple authentication object
                //Adding a fake expiration time for now
                if (userProfile != null) {
                    LOG.debug("Valid User Profile received from getUserProfile API call ");
                    authenticationToken.setUserProfile(userProfile);
                }else{
                    LOG.error("InValid User Profile received from getUserProfile API call ");
                    //Setting authenticationToken to null
                    authenticationToken = null;
                }
            }else{
                LOG.error("Token verification failed. Invalid Authentication Token received.");
                //Setting authenticationToken to null
                authenticationToken = null;
            }
        }

        LOG.debug("End with parseToken method :: AuthorizationServiceImpl");
        return authenticationToken;
    }

    @Activate
    @Modified
    protected final void activate() {
        tokenUrl = authenticationServiceConfiguration.getTokenUrl();
    }

}
