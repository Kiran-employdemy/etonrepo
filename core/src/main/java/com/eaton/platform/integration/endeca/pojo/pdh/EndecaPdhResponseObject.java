package com.eaton.platform.integration.endeca.pojo.pdh;

import com.eaton.platform.integration.endeca.pojo.base.AbstractEndecaResponseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <html> Description: This class is used to store the entire response returned from Endeca request to searchApplication "eatonpdhlstcountries".
 *
 * @author Victoria Pershick
 * @version 1.0
 * @since 2023
 *
 */
public class EndecaPdhResponseObject extends AbstractEndecaResponseObject<EndecaPdhSearchResults> {

    private EndecaPdhSearchResults searchResults;

    /**
     * @return all languages
     */
    public List<String> fetchLanguages() {
        if(searchResults != null) {
            return searchResults.fetchLanguages();
        }
        return new ArrayList<>();
    }

    /**
     * @return all countries
     */
    public List<String> fetchCountries() {
        if(searchResults != null) {
            return searchResults.fetchCountries();
        }
        return new ArrayList<>();
    }


    /**
     * @return the Endeca PDH Object searchResults
     */
    public EndecaPdhSearchResults getSearchResults() {
        return searchResults;
    }
    /**
     * @param searchResults the searchResults to set
     */
    public void setSearchResults(EndecaPdhSearchResults searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EndecaPdhResponseObject that = (EndecaPdhResponseObject) o;
        return Objects.equals(searchResults, that.searchResults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(searchResults);
    }

    @Override
    public String toString() {
        return "EndecaPdhResponseObject{" +
                ", searchResults=" + searchResults +
                '}';
    }
}
