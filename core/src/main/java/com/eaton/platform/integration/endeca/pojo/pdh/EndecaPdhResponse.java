package com.eaton.platform.integration.endeca.pojo.pdh;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <html> Description: This class is used to store the "response" information from Endeca request to searchApplication "eatonpdhlstcountries".
 *
 * @author Victoria Pershick
 * @version 1.0
 * @since 2023
 *
 */
public class EndecaPdhResponse {

    private EndecaPdhResponseObject response;

    /**
     * Return a list of all languages included in "response" -> "searchResults" -> "document" -> "content" -> {"name": "Language", ...}
     *
     * @return the list of countries, as a list of Strings
     *
     */
    public List<String> fetchLanguages() {
        if(response != null) {
            return response.fetchLanguages();
        }
        return new ArrayList<>();
    }

    /**
     * Return a list of all countries included in "response" -> "searchResults" -> "document" -> "content" -> {"name": "Country", ...}
     *
     * @return the list of countries, as a list of Strings
     *
     */
    public List<String> fetchCountries() {
        if(response != null) {
            return response.fetchCountries();
        }
        return new ArrayList<>();
    }

    /**
     * @return the Endeca PDH Response Object response
     */
    public EndecaPdhResponseObject getResponse() {
        return response;
    }
    /**
     * @param response the searchResults to set
     */
    public void setResponse(EndecaPdhResponseObject response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EndecaPdhResponse that = (EndecaPdhResponse) o;
        return Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(response);
    }

    @Override
    public String toString() {
        return "EndecaPdhResponse{" +
                "response=" + response +
                '}';
    }
}
