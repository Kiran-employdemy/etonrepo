package com.eaton.platform.integration.endeca.pojo.base;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Locale;
import java.util.Objects;

import static com.eaton.platform.core.constants.CommonConstants.FILE_TYPE_APPLICATION_XML_VALUE;

/**
 * <html> Description: This class is used to store each item inside of "content", from result of Endeca request to searchApplication "eatonpdhlstcountries".
 *
 * @author Victoria Pershick
 * @version 1.0
 * @since 2023
 *
 */
public class EndecaField {

    private String name;
    private String type;
    private String value;

    /**
     * Checks if the field is of type
     * @param endecaFieldEnum type to check
     * @return true if it is
     */
    public boolean isOfType(EndecaFieldEnum endecaFieldEnum) {
        return endecaFieldEnum.name().equals(name.toUpperCase(Locale.getDefault()));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        if (isXML()) {
            return StringEscapeUtils.unescapeHtml(StringEscapeUtils.unescapeHtml(value));
        }
        return value.replace("\r","");
    }

    public boolean isXML() {
        return FILE_TYPE_APPLICATION_XML_VALUE.equals(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EndecaField that = (EndecaField) o;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, value);
    }

    @Override
    public String toString() {
        return "EndecaPdhField{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
