package com.eaton.platform.core.services.converter.search.find.replace.impl;

import org.osgi.service.component.annotations.Component;

import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.models.search.find.replace.TextSearchRequestModel;
import com.eaton.platform.core.services.converter.search.find.replace.TextSearchRequestModel2BeanConverter;

/**
 * The Class TextSearchRequestModel2BeanConverterImpl.
 *
 * Converts {@link TextSearchRequestModel} to {@link TextSearchRequestBean}.
 *
 * @author Jaroslav Rassadin
 */
@Component(
		service = TextSearchRequestModel2BeanConverter.class,
		property = {
				AEMConstants.SERVICE_VENDOR_EATON,
				AEMConstants.SERVICE_DESCRIPTION + "Text Search Request Model To Bean Converter",
				AEMConstants.PROCESS_LABEL + "TextSearchRequestModel2BeanConverterImpl" })
public class TextSearchRequestModel2BeanConverterImpl implements TextSearchRequestModel2BeanConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextSearchRequestBean convert(final TextSearchRequestModel source) {
		return this.convert(source, new TextSearchRequestBean());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextSearchRequestBean convert(final TextSearchRequestModel source, final TextSearchRequestBean target) {
		if ((source != null) && (target != null)) {
			target.setCaseSensitive(source.isCaseSensitive());
			target.setPath(source.getPath());
			target.setSearchText(source.getSearchText());
			target.setReplaceText(source.getReplaceText());
			target.setWholeWords(source.isWholeWords());
		}
		return target;
	}

}
