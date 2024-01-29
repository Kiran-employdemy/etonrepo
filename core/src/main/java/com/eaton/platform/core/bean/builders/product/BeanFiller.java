package com.eaton.platform.core.bean.builders.product;

import com.eaton.platform.core.bean.builders.product.exception.MissingFillingBeanException;

public interface BeanFiller<T> {
    void fill(T bean) throws MissingFillingBeanException;
}
