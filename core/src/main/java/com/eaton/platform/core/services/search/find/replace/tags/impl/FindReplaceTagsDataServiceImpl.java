package com.eaton.platform.core.services.search.find.replace.tags.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsModificationResultBean;
import com.eaton.platform.core.bean.search.find.replace.response.TagsSearchResultBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.AbstractDelegateService;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsDataService;
import com.eaton.platform.core.services.search.find.replace.tags.FindReplaceTagsDataTypeService;

/**
 * The Class FindReplaceTagsDataServiceImpl.
 *
 * Delegate for "find and replace" services for tags.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = FindReplaceTagsDataService.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Find Replace Tags Data Service",
				AEMConstants.PROCESS_LABEL + "FindReplaceTagsDataServiceImpl" })
public class FindReplaceTagsDataServiceImpl extends AbstractDelegateService<FindReplaceTagsDataTypeService, ContentType> implements FindReplaceTagsDataService {

	@Reference(
			name = "Service",
			cardinality = ReferenceCardinality.MULTIPLE,
			policy = ReferencePolicy.DYNAMIC,
			service = FindReplaceTagsDataTypeService.class)
	private final List<FindReplaceTagsDataTypeService> services = new ArrayList<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean add(final TagsModificationRequestBean modificationRequest) {
		return this.getService(modificationRequest.getContentType()).add(modificationRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean delete(final TagsModificationRequestBean modificationRequest) {
		return this.getService(modificationRequest.getContentType()).delete(modificationRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsSearchResultBean find(final TagsSearchRequestBean searchRequest) {
		return this.getService(searchRequest.getContentType()).find(searchRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationResultBean replace(final TagsModificationRequestBean modificationRequest) {
		return this.getService(modificationRequest.getContentType()).replace(modificationRequest);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<FindReplaceTagsDataTypeService> getServices() {
		return Collections.unmodifiableList(this.services);
	}

}
