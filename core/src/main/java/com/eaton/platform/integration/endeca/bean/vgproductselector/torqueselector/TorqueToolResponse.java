package com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaton.platform.integration.endeca.bean.FacetsBean;
import com.eaton.platform.integration.endeca.bean.StatusDetailsBean;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "status",
    "statusDetails",
    "binning"
})
public class TorqueToolResponse implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1998648680002655642L;
	@JsonProperty("status")
    private String status;
    @JsonProperty("statusDetails")
    private StatusDetailsBean statusDetails;
    @JsonProperty("binning")
    private FacetsBean facets;
	
    private Integer totalCount;

    private List<TorqueTool> torqueSearchResults;
    
    private Map<String,Object> facetValueMap;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the statusDetails
	 */
	public StatusDetailsBean getStatusDetails() {
		return statusDetails;
	}

	/**
	 * @param statusDetails the statusDetails to set
	 */
	public void setStatusDetails(StatusDetailsBean statusDetails) {
		this.statusDetails = statusDetails;
	}

	/**
	 * @return the facets
	 */
	public FacetsBean getFacets() {
		return facets;
	}

	/**
	 * @param facets the facets to set
	 */
	public void setFacets(FacetsBean facets) {
		this.facets = facets;
	}

	/**
	 * @return the totalCount
	 */
	public Integer getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @return the torqueSearchResults
	 */
	public List<TorqueTool> getTorqueSearchResults() {
		return torqueSearchResults;
	}

	/**
	 * @param torqueSearchResults the torqueSearchResults to set
	 */
	public void setTorqueSearchResults(List<TorqueTool> torqueSearchResults) {
		this.torqueSearchResults = torqueSearchResults;
	}
	
	 /**
	 * @return the facetValueMap
	 */
	public Map<String, Object> getFacetValueMap() {
		return facetValueMap;
	}

	/**
	 * @param facetValueMap the facetValueMap to set
	 */
	public void setFacetValueMap(Map<String, Object> facetValueMap) {
		this.facetValueMap = facetValueMap;
	}

	@Override 
	 public String toString() {
    	 StringBuilder result = new StringBuilder();
    	    String newLine = System.getProperty("line.separator");

    	    result.append(newLine);

    	    //determine fields declared in this class only (no fields of superclass)
    	    Field[] fields = this.getClass().getDeclaredFields();

    	    //print field names paired with their values
    	    for (Field field : fields) {
    	    	if(!field.getName().equalsIgnoreCase("serialVersionUID")){
    	    		 result.append("  ");
    	    	      try {
    	    	        result.append(field.getName());
    	    	        result.append(": ");
    	    	        //requires access to private field:
    	    	        result.append(field.get(this));
    	    	      }
    	    	      catch (IllegalAccessException ex) {
    	    	    	  return "Error in toString of TorqueToolResponse"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }
}
