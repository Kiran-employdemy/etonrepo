package com.eaton.platform.core.services.converter.search.find.replace.impl;

import org.osgi.service.component.annotations.Component;

import com.day.cq.dam.api.DamConstants;
import com.eaton.platform.core.bean.search.find.replace.request.ContentType;
import com.eaton.platform.core.bean.search.find.replace.request.TagsSearchRequestBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.search.find.replace.TagsSearchRequestModel;
import com.eaton.platform.core.services.converter.search.find.replace.TagsSearchRequestModel2BeanConverter;

/**
 * The Class TagsSearchRequestModel2BeanConverterImpl.
 *
 * Converts {@link TagsSearchRequestModel} to {@link TagsSearchRequestBean}.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = TagsSearchRequestModel2BeanConverter.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Tags Search Request Model To Bean Converter",
				AEMConstants.PROCESS_LABEL + "TagsSearchRequestModel2BeanConverterImpl" })
public class TagsSearchRequestModel2BeanConverterImpl implements TagsSearchRequestModel2BeanConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsSearchRequestBean convert(final TagsSearchRequestModel source) {
		return this.convert(source, new TagsSearchRequestBean());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TagsSearchRequestBean convert(final TagsSearchRequestModel source, final TagsSearchRequestBean target) {
		if ((source != null) && (target != null)) {
			target.setPath(source.getPath());
			target.setContentType(ContentType.fromValue(source.getContentType()));

			if (target.getContentType() == null) {

				if (target.getPath().startsWith(DamConstants.MOUNTPOINT_ASSETS)) {
					target.setContentType(ContentType.ASSET);

				} else {
					target.setContentType(ContentType.PAGE);
				}
			}
			target.setTagsToFind(source.getTagsToFind());
		}
		return target;
	}

}
