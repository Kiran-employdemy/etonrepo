package com.eaton.platform.integration.informatica.helper;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.integration.informatica.constants.InformaticaConstants;

public class XMLValidator implements ValidationEventHandler {

	private static final Logger LOG = LoggerFactory.getLogger(InformaticaGlobalAttrHelper.class);

	@Override
	public boolean handleEvent(ValidationEvent event) {

		if (event.getSeverity() == ValidationEvent.ERROR || event.getSeverity() == ValidationEvent.FATAL_ERROR) {

			LOG.error(InformaticaConstants.INVALID_FILE_FORMAT_ERROR_CODE, InformaticaConstants.INVALID_FILE_FORMAT_ERROR_MSG);
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

			return false;
		}

		return true;

	}

}
