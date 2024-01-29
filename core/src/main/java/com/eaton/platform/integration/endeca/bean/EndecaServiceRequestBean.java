package com.eaton.platform.integration.endeca.bean;

import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.commons.lang.SerializationUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static com.eaton.platform.core.constants.CommonConstants.EN_LANG;
import static com.eaton.platform.core.constants.CommonConstants.US_STRING;
import static com.eaton.platform.integration.endeca.constants.EndecaConstants.SEARCH_TERM_IGNORE;

@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
"searchApplication",
"searchApplicationKey",
"function",
"searchTerms",
"language",
"startingRecordNumber",
"numberOfRecordsToReturn",
"filters"
})
public class EndecaServiceRequestBean implements Serializable{

	private static final long serialVersionUID = 6941740308025470887L;
	@JsonProperty("searchApplication")
	private String searchApplication;
	@JsonProperty("searchApplicationKey")
	private String searchApplicationKey;
	@JsonProperty("function")
	private String function;
	@JsonProperty("searchTerms")
	private String searchTerms;
	@JsonProperty("language")
	private String language;
	@JsonProperty("startingRecordNumber")
	private String startingRecordNumber;
	@JsonProperty("numberOfRecordsToReturn")
	private String numberOfRecordsToReturn;
	@JsonProperty("filters")
	private List<FilterBean> filters = new ArrayList<>();

	public List<FilterBean> getFilters() {
		return filters;
	}

	public void setFilters(List<FilterBean> filters) {
		this.filters = filters;
	}

	@JsonProperty("searchApplication")
	public String getSearchApplication() {
		return searchApplication;
	}

	@JsonProperty("searchApplication")
	public void setSearchApplication(String searchApplication) {
		this.searchApplication = searchApplication;
	}

	@JsonProperty("searchApplicationKey")
	public String getSearchApplicationKey() {
		return searchApplicationKey;
	}

	@JsonProperty("searchApplicationKey")
	public void setSearchApplicationKey(String searchApplicationKey) {
		this.searchApplicationKey = searchApplicationKey;
	}

	@JsonProperty("function")
	public String getFunction() {
		return function;
	}

	@JsonProperty("function")
	public void setFunction(String function) {
		this.function = function;
	}

	@JsonProperty("searchTerms")
	public String getSearchTerms() {
		return searchTerms;
	}

	@JsonProperty("searchTerms")
	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}

	@JsonProperty("language")
	public String getLanguage() {
		return language;
	}

	@JsonProperty("language")
	public void setLanguage(String language) {
		this.language = language;
	}

	@JsonProperty("startingRecordNumber")
	public String getStartingRecordNumber() {
		return startingRecordNumber;
	}

	@JsonProperty("startingRecordNumber")
	public void setStartingRecordNumber(String startingRecordNumber) {
		this.startingRecordNumber = startingRecordNumber;
	}

	@JsonProperty("numberOfRecordsToReturn")
	public String getNumberOfRecordsToReturn() {
		return numberOfRecordsToReturn;
	}

	@JsonProperty("numberOfRecordsToReturn")
	public void setNumberOfRecordsToReturn(String numberOfRecordsToReturn) {
		this.numberOfRecordsToReturn = numberOfRecordsToReturn;
	}

	@JsonIgnore
	public Locale getLocale() {
		if(SEARCH_TERM_IGNORE.equals(language)){
			return new Locale.Builder().setLanguage(EN_LANG).setRegion(US_STRING).build();
		}else {
			final String languageId = language.split("_")[0];
			final String regionId = language.split("_")[1];
			return new Locale.Builder().setLanguage(languageId).setRegion(regionId).build();
		}
	}

	@JsonIgnore
	public String getUniqueId() {
		final StringBuffer stringBuffer = new StringBuffer();
		final byte[] serializedBytes = SerializationUtils.serialize(this);
		for (byte bytes : serializedBytes) {
			stringBuffer.append(String.format("%02x", bytes & 0xff));
		}
		return stringBuffer.toString();
	}

	/**
	 * check a valid value for SearchTerm in endeca request bean and filter value
	 * @return boolean as per validity of searchTerm in endecarequesbean
	 */
	public boolean hasValidSearchTerm() {
		return (null != this.getSearchTerms()) && !this.getSearchTerms().isEmpty() && (null != this.getFilters());
	}

	/**
	 * checking if the request has a valid "ReturnFacetsFor"
	 * @return boolean for ReturnFacetsFor value in endecarequesbean
	 */
	public boolean hasValidReturnFacetsFor() {
		Optional<FilterBean> returnFacetsFor = this.getFilters().stream().filter(filterBean -> filterBean.getFilterName() == EndecaConstants.RETURN_FACETS_FOR).findFirst();
		return returnFacetsFor.isPresent() && !returnFacetsFor.get().getFilterValues().isEmpty() && !returnFacetsFor.get().getFilterValues().get(0).isEmpty();
	}

	@Override
	public String toString() {
		return "EndecaServiceRequestBean{" +
				"searchApplication='" + searchApplication + '\'' +
				", searchApplicationKey='" + searchApplicationKey + '\'' +
				", function='" + function + '\'' +
				", searchTerms='" + searchTerms + '\'' +
				", language='" + language + '\'' +
				", startingRecordNumber='" + startingRecordNumber + '\'' +
				", numberOfRecordsToReturn='" + numberOfRecordsToReturn + '\'' +
				", filters=" + filters +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		EndecaServiceRequestBean that = (EndecaServiceRequestBean) o;
		return Objects.equals(searchApplication, that.searchApplication) && Objects.equals(searchApplicationKey, that.searchApplicationKey)
				&& Objects.equals(function, that.function) && Objects.equals(searchTerms, that.searchTerms)
				&& Objects.equals(language, that.language) && Objects.equals(startingRecordNumber, that.startingRecordNumber)
				&& Objects.equals(numberOfRecordsToReturn, that.numberOfRecordsToReturn) && Objects.equals(filters, that.filters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(searchApplication, searchApplicationKey, function, searchTerms, language, startingRecordNumber, numberOfRecordsToReturn, filters);
	}
}
