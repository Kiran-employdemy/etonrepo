package com.eaton.platform.core.util;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AemXmlValidator implements ValidationEventHandler{
	private static final Logger LOG = LoggerFactory.getLogger(AemXmlValidator.class);

    public boolean handleEvent(ValidationEvent event) {
    	//if (event.getSeverity() == ValidationEvent.ERROR || event.getSeverity() == ValidationEvent.FATAL_ERROR) {
		LOG.error("\nEVENT");

		LOG.error("SEVERITY: {} ", event.getSeverity());

		LOG.error("MESSAGE:  {}", event.getMessage());

		LOG.error(" LINKED EXCEPTION: {} ", event.getLinkedException());

		LOG.error("LOCATOR");

		LOG.error("    LINE NUMBER:  {}", event.getLocator().getLineNumber());

		LOG.error("     COLUMN NUMBER: {} ", event.getLocator().getColumnNumber());

		LOG.error("     OFFSET:  {}", event.getLocator().getOffset());

		LOG.error("    OBJECT:  {}", event.getLocator().getObject());

		LOG.error("    NODE:  {}", event.getLocator().getNode());

		LOG.error("    URL:  {}", event.getLocator().getURL());

		
	//return false;
    //	}
    	return true;

    }
}
