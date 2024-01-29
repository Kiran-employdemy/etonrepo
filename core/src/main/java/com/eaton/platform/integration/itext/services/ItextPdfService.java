package com.eaton.platform.integration.itext.services;

import java.io.IOException;

import org.apache.sling.api.SlingHttpServletRequest;

/**
 * The Interface ItextPdfService.
 */
public interface ItextPdfService {

	byte[] getPDFByHtmlSource(SlingHttpServletRequest request) throws IOException;

	boolean isEnablePdfGeneration();

}
