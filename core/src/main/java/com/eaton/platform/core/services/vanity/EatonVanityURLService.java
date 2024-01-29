package com.eaton.platform.core.services.vanity;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONException;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import java.io.IOException;

public interface EatonVanityURLService {
    /**
     * This method checks if a given request URI (after performing the Resource Resolver Mapping) is a valid vanity URL,
     * if true it will perform the SendRedirect using Response.
     *
     * @param request the request object
     * @param response the response object
     * @return true if this response is redirected because it's a valid Vanity path, else false.
     */

    boolean dispatchMethod(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException, RepositoryException, JSONException;
}
