package com.eaton.platform.integration.endeca.bean.subcategory;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaton.platform.integration.endeca.bean.FacetsBean;
import com.eaton.platform.integration.endeca.bean.StatusDetailsBean;

@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
"status",
"statusDetails",
"binning"
})
public class FamilyResponseBean implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -6793909936627727108L;
	
	@JsonProperty("status")
	private String status;
    @JsonProperty("statusDetails")
    private StatusDetailsBean statusdetails;
    @JsonProperty("binning")
    private FacetsBean facets;
    
    private int totalCount;
    private List<ProductFamilyBean> productFamilyBean;
    
    private Map<String,Object> facetValueMap;
	public Map<String, Object> getFacetValueMap() {
		return facetValueMap;
	}

	public void setFacetValueMap(Map<String, Object> facetValueMap) {
		this.facetValueMap = facetValueMap;
	}
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
	 * @return the statusdetails
	 */
	public StatusDetailsBean getStatusdetails() {
		return statusdetails;
	}
	/**
	 * @param statusdetails the statusdetails to set
	 */
	public void setStatusdetails(StatusDetailsBean statusdetails) {
		this.statusdetails = statusdetails;
	}
	
	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}
	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	/**
	 * @return the productFamilyBean
	 */
	public List<ProductFamilyBean> getProductFamilyBean() {
		return productFamilyBean;
	}
	/**
	 * @param productFamilyBean the productFamilyBean to set
	 */
	public void setProductFamilyBean(List<ProductFamilyBean> productFamilyBean) {
		this.productFamilyBean = productFamilyBean;
	}
	/**
     * Intended only for debugging.
     *
     * <P>Here, the contents of every field are placed into the result, with
     * one field per line.
     */
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
    	    	    	  return "Error in toString of FamilyResponseBean"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }

}