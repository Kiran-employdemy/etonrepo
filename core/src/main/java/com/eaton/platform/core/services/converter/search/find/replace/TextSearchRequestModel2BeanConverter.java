package com.eaton.platform.core.services.converter.search.find.replace;

import com.eaton.platform.core.bean.search.find.replace.request.TextSearchRequestBean;
import com.eaton.platform.core.models.search.find.replace.TextSearchRequestModel;
import com.eaton.platform.core.services.converter.Converter;

/**
 * The Interface TextSearchRequestModel2BeanConverter.
 *
 * Converts {@link TextSearchRequestModel} to {@link TextSearchRequestBean}.
 *
 * @author Jaroslav Rassadin
 */
public interface TextSearchRequestModel2BeanConverter extends Converter<TextSearchRequestModel, TextSearchRequestBean> {

}
