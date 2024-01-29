package com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector;


import java.io.Serializable;
import java.lang.reflect.Field;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "response"
})
public class TorqueSelectorResponse implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6590624093355874790L;
	@JsonProperty("response")
    private TorqueToolResponse torqueToolResponse;
	/**
	 * @return the torqueToolResponse
	 */
	public TorqueToolResponse getTorqueToolResponse() {
		return torqueToolResponse;
	}
	/**
	 * @param torqueToolResponse the torqueToolResponse to set
	 */
	public void setTorqueToolResponse(TorqueToolResponse torqueToolResponse) {
		this.torqueToolResponse = torqueToolResponse;
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
    	    	    	  return "Error in toString of TorqueSelectorResponse"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }

}

