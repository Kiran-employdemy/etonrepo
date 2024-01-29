package com.eaton.platform.integration.endeca.pojo.pdh;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.pojo.base.BaseEndecaDocument;
import com.eaton.platform.integration.endeca.pojo.base.EndecaFieldEnum;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <html> Description: This class is used to store the "document" information from Endeca request to searchApplication "eatonpdhlstcountries".
 *
 * @author Victoria Pershick
 * @version 1.0
 * @since 2023
 *
 */
public class EndecaPdhDocument extends BaseEndecaDocument {

    /**
     * @return all languages
     */
    public List<String> fetchLanguages() {
        if(content.isEmpty()) {
            return new ArrayList<>();
        }
        return splitIfNotEmpty(getValueFor(EndecaFieldEnum.LANGUAGE));
    }

    private List<String> splitIfNotEmpty(String languages) {
        if (Strings.isNullOrEmpty(languages)) {
            return new ArrayList<>();
        }
        return Arrays.asList(languages.split(CommonConstants.COMMA));
    }

    /**
     * @return all countries
     */
    public List<String> fetchCountries() {
        if(content.isEmpty()) {
            return new ArrayList<>();
        }
        return splitIfNotEmpty(getValueFor(EndecaFieldEnum.COUNTRY));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EndecaPdhDocument that = (EndecaPdhDocument) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return "EndecaPdhDocument{" +
                "content=" + content +
                '}';
    }
}
