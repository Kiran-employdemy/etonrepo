package com.eaton.platform.core.services.converter.search.find.replace.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.eaton.platform.core.bean.search.find.replace.request.TextModificationRequestBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.search.find.replace.TextModificationRequestModel;
import com.eaton.platform.core.services.converter.search.find.replace.TextModificationRequestModel2BeanConverter;
import com.eaton.platform.core.services.converter.search.find.replace.TextSearchRequestModel2BeanConverter;

/**
 * The Class TextModificationRequestModel2BeanConverterImpl.
 *
 * Converts {@link TextModificationRequestModel} to {@link TextModificationRequestBean}.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = TextModificationRequestModel2BeanConverter.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Text Modification Request Model To Bean Converter",
				AEMConstants.PROCESS_LABEL + "TexModificationRequestModel2BeanConverter" })
public class TextModificationRequestModel2BeanConverterImpl implements TextModificationRequestModel2BeanConverter {

	@Reference
	private TextSearchRequestModel2BeanConverter textSearchRequestModel2BeanConverter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextModificationRequestBean convert(final TextModificationRequestModel source) {
		return this.convert(source, new TextModificationRequestBean());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextModificationRequestBean convert(final TextModificationRequestModel source, final TextModificationRequestBean target) {
		if ((source != null) && (target != null)) {
			this.textSearchRequestModel2BeanConverter.convert(source, target);

			target.setBackup(source.isBackup());
			target.setModificationPaths(source.getModificationPaths());
			target.setReplicate(source.isReplicate());
			target.setRootSearch(source.isRootSearch());

			target.setUserId(source.getUserId());
		}
		return target;
	}

	/**
	 * Sets the text search request model 2 bean converter.
	 *
	 * @param textSearchRequestModel2BeanConverter
	 *            the textSearchRequestModel2BeanConverter to set
	 */
	public void setTextSearchRequestModel2BeanConverter(final TextSearchRequestModel2BeanConverter textSearchRequestModel2BeanConverter) {
		this.textSearchRequestModel2BeanConverter = textSearchRequestModel2BeanConverter;
	}

}
