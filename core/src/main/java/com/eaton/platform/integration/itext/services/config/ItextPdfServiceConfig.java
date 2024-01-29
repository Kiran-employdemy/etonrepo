package com.eaton.platform.integration.itext.services.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * ItextPdf Service Configuration
 * 
 * This will provide configuration for ItextPdf Service
 */
@ObjectClassDefinition(name = "ItextPdf Service Configuration", description = "Service Configuration")
public @interface ItextPdfServiceConfig {

	/**
	 * To enable PDF Generation using ItextPdf API.
	 * 
	 * @return
	 */
	@AttributeDefinition(name = "Enable PDF Generation", description = "Check the checkbox to enable PDF generation for sku pages when accessing with extension as .pdf")
	boolean enablePdfGeneration() default true;

	/**
	 * Set to true to enable the debug logging.
	 * 
	 * @return
	 */
	@AttributeDefinition(name = "Debug Log", description = "ItextPdf API Debug Log")
	boolean debugLog() default false;

	/**
	 * The fallback link for logo icons
	 *
	 * @return
	 */
	@AttributeDefinition(name = "Fallback Logo Link", description = "The fallback link for logo icons")
	String fallbackLogoLink() default "https://www.eaton.com";
	
}