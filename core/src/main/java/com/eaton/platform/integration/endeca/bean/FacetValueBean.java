package com.eaton.platform.integration.endeca.bean;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Objects;
/**
 * Object to use for converting Facet Value information to and from json
 */
@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
"label",
"ndocs",
"value",
"selected"
})
public class FacetValueBean implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -4356599636400841882L;
	@JsonProperty("label")
	private String facetValueLabel;
	@JsonProperty("ndocs")
    private int facetValueDocs;
	@JsonProperty("value")
    private String facetValueId;
	@JsonProperty("selected")
	private String activeRadioButton;

	private String facetURL;
	private String facetStartURL;
	private String facetEndURL;


	/**
	 * @return the facetValueLabel
	 */
	public String getFacetValueLabel() {
		return facetValueLabel;
	}
	/**
	 * @param facetValueLabel the facetValueLabel to set
	 */
	public void setFacetValueLabel(String facetValueLabel) {
		this.facetValueLabel = facetValueLabel;
	}
	/**
	 * @return the facetValueDocs
	 */
	public int getFacetValueDocs() {
		return facetValueDocs;
	}
	/**
	 * @param facetValueDocs the facetValueDocs to set
	 */
	public void setFacetValueDocs(int facetValueDocs) {
		this.facetValueDocs = facetValueDocs;
	}
	/**
	 * @return the facetValueId
	 */
	public String getFacetValueId() {
		return facetValueId;
	}
	/**
	 * @param facetValueId the facetValueId to set
	 */
	public void setFacetValueId(String facetValueId) {
		this.facetValueId = facetValueId;
	}

	public String getFacetURL() {
		return facetURL;
	}
	public void setFacetURL(String facetURL) {
		this.facetURL = facetURL;
	}
	public String getActiveRadioButton() {
		return activeRadioButton;
	}
	public void setActiveRadioButton(String activeRadioButton) {
		this.activeRadioButton = activeRadioButton;
	}
	public String getFacetStartURL() {
		return facetStartURL;
	}
	public void setFacetStartURL(String facetStartURL) {
		this.facetStartURL = facetStartURL;
	}
	public String getFacetEndURL() {
		return facetEndURL;
	}
	public void setFacetEndURL(String facetEndURL) {
		this.facetEndURL = facetEndURL;
	}

	@Override
	public String toString() {
		return "FacetValueBean{" +
				"facetValueLabel='" + facetValueLabel + '\'' +
				", facetValueDocs=" + facetValueDocs +
				", facetValueId='" + facetValueId + '\'' +
				", activeRadioButton='" + activeRadioButton + '\'' +
				", facetURL='" + facetURL + '\'' +
				", facetStartURL='" + facetStartURL + '\'' +
				", facetEndURL='" + facetEndURL + '\'' +
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
		FacetValueBean that = (FacetValueBean) o;
		return facetValueDocs == that.facetValueDocs && Objects.equals(facetValueLabel, that.facetValueLabel)
				&& Objects.equals(facetValueId, that.facetValueId) && Objects.equals(activeRadioButton, that.activeRadioButton)
				&& Objects.equals(facetURL, that.facetURL) && Objects.equals(facetStartURL, that.facetStartURL)
				&& Objects.equals(facetEndURL, that.facetEndURL);
	}

	@Override
	public int hashCode() {
		return Objects.hash(facetValueLabel, facetValueDocs, facetValueId, activeRadioButton, facetURL, facetStartURL, facetEndURL);
	}
}