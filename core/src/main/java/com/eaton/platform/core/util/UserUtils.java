package com.eaton.platform.core.util;

import com.adobe.granite.security.user.UserProperties;
import com.adobe.granite.security.user.UserPropertiesManager;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class UserUtils {
    private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);

    public static ResourceResolver getResourceResolverFromServiceUser(ResourceResolverFactory resolverFactory, String serviceUserName) {
        ResourceResolver rr = null;
        try {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put(ResourceResolverFactory.SUBSERVICE, serviceUserName);
            // Make sure you get SERVICE resource resolver or the system user stuff won't work
            rr = resolverFactory.getServiceResourceResolver(param);
        } catch (LoginException e) {
            logger.error("There was a problem while getting the resource resolver from the service user: {}.", e.getMessage());
            logger.error("Have you properly configured the service user? Check the following link:");
            logger.error("http://adobeaemclub.com/access-to-resourceresolver-in-osgi-services-aem-6-1/");
        }
        return rr;
    }

    private static ResourceResolver getResourceResolver(Session session, ResourceResolverFactory resourceResolverFactory) throws LoginException {
        return resourceResolverFactory.getResourceResolver(Collections.<String, Object>singletonMap(JcrResourceConstants.AUTHENTICATION_INFO_SESSION,
                session));
    }

    public static boolean userOrGroupExists(String id, ResourceResolverFactory factory, Session session) throws LoginException, RepositoryException {
        Authorizable authorizable;
        ResourceResolver rr = getResourceResolver(session, factory);
        UserManager um = rr.adaptTo(UserManager.class);
        authorizable  = um.getAuthorizable(id);

        return authorizable != null;
    }
}
