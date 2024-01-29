package com.eaton.platform.integration.endeca.bean.sitesearch;


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
    "didYouMean",
    "binning",
    "searchResults"
})
public class SiteSearchPageResponse implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8684951062685715120L;
	@JsonProperty("status")
    private String status;
    @JsonProperty("statusDetails")
    private StatusDetailsBean statusDetails;
    @JsonProperty("didYouMean")
    private List<String> didYouMean;
    @JsonProperty("binning")
    private FacetsBean facets;

    private Integer totalCount;
    private Integer productsCount;
    private Integer newsCount;
    private Integer resourcesCount;
    private Integer servicesCount;
    
    private String productsId;
    private String newsId;
    private String resourcesId;
    private String servicesId;
    

    private List<SiteSearchResultsBean> siteSearchResults;
    
    private Map<String,Object> facetValueMap;

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

   

    @JsonProperty("didYouMean")
    public List<String> getDidYouMean() {
        return didYouMean;
    }

    @JsonProperty("didYouMean")
    public void setDidYouMean(List<String> didYouMean) {
        this.didYouMean = didYouMean;
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
	 * @return the productsCount
	 */
	public Integer getProductsCount() {
		return productsCount;
	}

	/**
	 * @param productsCount the productsCount to set
	 */
	public void setProductsCount(Integer productsCount) {
		this.productsCount = productsCount;
	}

	/**
	 * @return the newsCount
	 */
	public Integer getNewsCount() {
		return newsCount;
	}

	/**
	 * @param newsCount the newsCount to set
	 */
	public void setNewsCount(Integer newsCount) {
		this.newsCount = newsCount;
	}

	/**
	 * @return the resourcesCount
	 */
	public Integer getResourcesCount() {
		return resourcesCount;
	}

	/**
	 * @param resourcesCount the resourcesCount to set
	 */
	public void setResourcesCount(Integer resourcesCount) {
		this.resourcesCount = resourcesCount;
	}

	/**
	 * @return the siteSearchResults
	 */
	public List<SiteSearchResultsBean> getSiteSearchResults() {
		return siteSearchResults;
	}

	/**
	 * @param siteSearchResults the siteSearchResults to set
	 */
	public void setSiteSearchResults(List<SiteSearchResultsBean> siteSearchResults) {
		this.siteSearchResults = siteSearchResults;
	}

	 public Map<String, Object> getFacetValueMap() {
		return facetValueMap;
	}

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
    	    	    	  return "Error in toString of SiteSearchPageResponse"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }

	public String getProductsId() {
		return productsId;
	}

	public void setProductsId(String productsId) {
		this.productsId = productsId;
	}

	public String getNewsId() {
		return newsId;
	}

	public void setNewsId(String newsId) {
		this.newsId = newsId;
	}

	public String getResourcesId() {
		return resourcesId;
	}

	public void setResourcesId(String resourcesId) {
		this.resourcesId = resourcesId;
	}

	public Integer getServicesCount() {
		return servicesCount;
	}

	public void setServicesCount(Integer servicesCount) {
		this.servicesCount = servicesCount;
	}

	public String getServicesId() {
		return servicesId;
	}

	public void setServicesId(String servicesId) {
		this.servicesId = servicesId;
	}
	 
	
}