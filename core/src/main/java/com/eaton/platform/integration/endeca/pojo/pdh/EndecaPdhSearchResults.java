package com.eaton.platform.integration.endeca.pojo.pdh;

import com.eaton.platform.integration.endeca.pojo.base.AbstractEndecaSearchResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * <html> Description: This class is used to store the "searchResults" information from Endeca request to searchApplication "eatonpdhlstcountries".
 *
 * @author Victoria Pershick
 * @version 1.0
 * @since 2023
 *
 */
public class EndecaPdhSearchResults extends AbstractEndecaSearchResults<EndecaPdhDocument> {

    private Set<EndecaPdhDocument> document;

    /**
     * @return all languages
     */
    public List<String> fetchLanguages() {
        if(document != null) {
            return document.stream().findFirst().orElse(new EndecaPdhDocument()).fetchLanguages();
        }
        return new ArrayList<>();
    }

    /**
     * @return all countries
     */
    public List<String> fetchCountries() {
        if(document != null) {
            return document.stream().findFirst().orElse(new EndecaPdhDocument()).fetchCountries();
        }
        return new ArrayList<>();
    }


    public Set<EndecaPdhDocument> getDocument() {
        return document;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EndecaPdhSearchResults that = (EndecaPdhSearchResults) o;
        return totalCount == that.totalCount && Objects.equals(document, that.document);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalCount, document);
    }

    @Override
    public String toString() {
        return "EndecaPdhObject{" +
                "totalCount=" + totalCount +
                ", document=" + document +
                '}';
    }
}
