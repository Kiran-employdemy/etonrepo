package com.eaton.platform.core.services.search.find.replace.text.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TextSearchResultBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AbstractDelegateService;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceTextDataService;
import com.eaton.platform.core.services.search.find.replace.text.FindReplaceTextDataTypeService;

/**
 * The Class FindAndReplaceTexDatatService
 *
 * Delegate for "find and replace" services for text.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceTextDataService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Text Data Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceTextDataServiceImpl" })
public class FindReplaceTextDataServiceImpl extends AbstractDelegateService<FindReplaceTextDataTypeService, ContentType> implements FindReplaceTextDataService {

	@Reference(
			name = "Service",
			cardinality = ReferenceCardinality.MULTIPLE,
			policy = ReferencePolicy.DYNAMIC,
			service = FindReplaceTextDataTypeService.class)
	private final List<FindReplaceTextDataTypeService> services = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextSearchResultBean find(final TextSearchRequestBean searchRequest) {
		return this.getService(searchRequest.getContentType()).find(searchRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextModificationResultBean replace(final TextModificationRequestBean modificationRequest) {
		return this.getService(modificationRequest.getContentType()).replace(modificationRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<FindReplaceTextDataTypeService> getServices() {
		return Collections.unmodifiableList(this.services);
	}
}
