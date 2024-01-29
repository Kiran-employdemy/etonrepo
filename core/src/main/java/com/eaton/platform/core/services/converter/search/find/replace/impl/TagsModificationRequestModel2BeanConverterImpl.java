package com.eaton.platform.core.services.converter.search.find.replace.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.eaton.platform.core.bean.search.find.replace.request.Mode;
import com.eaton.platform.core.bean.search.find.replace.request.TagsModificationRequestBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.search.find.replace.TagsModificationRequestModel;
import com.eaton.platform.core.services.converter.search.find.replace.TagsModificationRequestModel2BeanConverter;
import com.eaton.platform.core.services.converter.search.find.replace.TagsSearchRequestModel2BeanConverter;

/**
 * The Class TagsModificationRequestModel2BeanConverterImpl.
 *
 * Converts {@link TagsModificationRequestModel} to {@link TagsModificationRequestBean}.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = TagsModificationRequestModel2BeanConverter.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Tags Modification Request Model To Bean Converter",
				AEMConstants.PROCESS_LABEL + "TagsModificationRequestModel2BeanConverterImpl" })
public class TagsModificationRequestModel2BeanConverterImpl implements TagsModificationRequestModel2BeanConverter {

	@Reference
	private TagsSearchRequestModel2BeanConverter tagsSearchRequestModel2BeanConverter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationRequestBean convert(final TagsModificationRequestModel source) {
		return this.convert(source, new TagsModificationRequestBean());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsModificationRequestBean convert(final TagsModificationRequestModel source, final TagsModificationRequestBean target) {
		if ((source != null) && (target != null)) {
			this.tagsSearchRequestModel2BeanConverter.convert(source, target);

			target.setBackup(source.isBackup());
			target.setMode(Mode.fromValue(source.getMode()));
			target.setModificationPaths(source.getModificationPaths());
			target.setReplicate(source.isReplicate());
			target.setRootSearch(source.isRootSearch());
			target.setTagsForModification(source.getTagsForModification());
			target.setUserId(source.getUserId());
		}
		return target;
	}

	/**
	 * Sets the tags search request model 2 bean converter.
	 *
	 * @param tagsSearchRequestModel2BeanConverter
	 *            the tagsSearchRequestModel2BeanConverter to set
	 */
	public void setTagsSearchRequestModel2BeanConverter(final TagsSearchRequestModel2BeanConverter tagsSearchRequestModel2BeanConverter) {
		this.tagsSearchRequestModel2BeanConverter = tagsSearchRequestModel2BeanConverter;
	}

}
