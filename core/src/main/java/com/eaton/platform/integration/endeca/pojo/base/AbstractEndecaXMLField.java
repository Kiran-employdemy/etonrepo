package com.eaton.platform.integration.endeca.pojo.base;

import com.eaton.platform.core.util.CommonUtil;
import com.google.common.base.Strings;

import java.io.InputStream;

/**
 * Abstract Endeca implementation detail when the EndecaField is of type XML, this wrapper will allow to unmarshall the xml value of that field
 * @param <T> the type of class to unmarshall to
 */
public abstract class AbstractEndecaXMLField<T> extends EndecaField {

    private final EndecaField endecaField;

    public AbstractEndecaXMLField(EndecaField endecaField) {
        this.endecaField = endecaField;
    }

    public T unmarshalledValue() {
        String value = endecaField.getValue();
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        return CommonUtil.getUnmarshaledClass(value, getClazz(), getXsdInputStream());
    }


    /**
     * @return the class of the field value to unmarshal to
     */
    protected abstract Class<T> getClazz();

    /**
     * @return the xsd input stream to use
     */
    protected abstract InputStream getXsdInputStream();

}
