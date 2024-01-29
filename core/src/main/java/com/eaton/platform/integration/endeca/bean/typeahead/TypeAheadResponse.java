package com.eaton.platform.integration.endeca.bean.typeahead;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import com.eaton.platform.core.bean.sitesearch.KeywordBean;
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
public class TypeAheadResponse implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4984109859027503710L;
	@JsonProperty("status")
    private String status;
	@JsonProperty("statusDetails")
	private StatusDetailsBean statusDetails;
	@JsonIgnore
    private List<KeywordBean> resultList;

   
	@JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
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
	 * @return the resultList
	 */
	public List<KeywordBean> getResultList() {
		return resultList;
	}

	/**
	 * @param resultList the resultList to set
	 */
	public void setResultList(List<KeywordBean> resultList) {
		this.resultList = resultList;
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
   	    	    	return "Error in toString of TypeAheadResponse"+ex.getMessage();
   	    	      }
   	    	      result.append(newLine);
   	    	    }
   	    	}

   	    return result.toString();
    }

}

