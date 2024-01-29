package com.eaton.platform.core.util;
/**
 * JcrUtils.java
 * --------------------------------------
 * Utilities methods for interacting with JCR.
 * --------------------------------------
 * Author: Soo Woo (soo@freedomdam.com)
 */
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

public class JcrUtils {
    private static final Logger log = LoggerFactory.getLogger(JcrUtils.class);

    public static void closeResourceResolver(ResourceResolver resourceResolver) {
        if (resourceResolver != null && resourceResolver.isLive()) {
            resourceResolver.close();
        }
    }

    public static void closeSession(Session session) {
        if (session != null && session.isLive()) {
            session.logout();
        }
    }

    public static NodeIterator executeSQL2(String expression, Session session) {
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
            QueryResult result = query.execute();
            NodeIterator nodeIterator = result.getNodes();
            return nodeIterator;
        } catch (RepositoryException ex) {
            log.error("Error in fetching nodes from JCR {}", ex.getMessage());
            return null;
        }
    }

}
