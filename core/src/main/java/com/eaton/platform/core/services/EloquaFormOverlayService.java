package com.eaton.platform.core.services;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;

/**
 * This service is used for Eloqua form manipulation.
 */
public interface EloquaFormOverlayService {

    String generateDisclaimerDom(final String extraHTML, final String formId, final String disclaimerMsg);
    List<Element> getFilteredElement(final String fieldType, Document htmlDoc);
    List<Resource> extractFormFields(final String eloquaFormResponse, final ResourceResolver resourceResolver,
                                     final String fieldType);

}
