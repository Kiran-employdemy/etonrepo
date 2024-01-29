package com.eaton.platform.integration.cad.services;

import com.eaton.platform.core.exception.EatonApplicationException;
import org.apache.sling.api.resource.ResourceResolver;

import java.io.File;

/**
 * This is a Javadoc comment
 * CadenasUrlService interface
 */
public interface CadenasUrlService {
    String getCadQualifierUrl();
    String getPartCommunityUrl();
    String getResponseFromCadenas(String requestURL);
    public File getCSVfilefromDAM(
            ResourceResolver adminResourceResolver, String filePath,
            String csvFileName) throws EatonApplicationException;
}
