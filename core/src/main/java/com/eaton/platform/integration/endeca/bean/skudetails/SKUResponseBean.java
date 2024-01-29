package com.eaton.platform.integration.endeca.bean.skudetails;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaton.platform.integration.endeca.bean.StatusDetailsBean;

@JsonSerialize(include = JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
    "status",
    "statusDetails"
})
public class SKUResponseBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1805889841942063631L;
	
	 	@JsonProperty("status")
	    private String status;
	    @JsonProperty("statusDetails")
	    private StatusDetailsBean statusDetails;
	    private Integer totalCount;
	    @JsonIgnore
	    private SKUDetailsBean skuDetails;
		private List<SKUDetailsBean> skuDetailsList;

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
		 * @return the skuDetails
		 */
		public List<SKUDetailsBean> getSkuDetailsList() {
			return skuDetailsList;
		}

		/**
		 * @param skuDetails the skuDetails to set
		 */
		public void setSkuDetailsList(List<SKUDetailsBean> skuDetailsList) {
			this.skuDetailsList = skuDetailsList;
			setSkuDetails(skuDetailsList!= null && !skuDetailsList.isEmpty()? skuDetailsList.get(0):null);			
		}
		
	    public SKUDetailsBean getSkuDetails() {
			return skuDetails;
		}

		private void setSkuDetails(SKUDetailsBean skuDetails) {
			this.skuDetails = skuDetails;
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
	    	    	    	  return "Error in toString of SKUResponseBean"+ex.getMessage();
	    	    	      }
	    	    	      result.append(newLine);
	    	    	    }
	    	    	}

	    	    return result.toString();
	     }

}