package com.eaton.platform.integration.endeca.bean.familymodule;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SKUCardParameterBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8587898317870623836L;

	
	private String label;
	private String skuCardValues;
	private String runtimeGraphURL;
	
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	
	/**
	 * @return the skuCardValues
	 */
	public String getSkuCardValues() {
		return skuCardValues;
	}
	/**
	 * @param skuCardValues the skuCardValues to set
	 */
	public void setSkuCardValues(String skuCardValues) {
		this.skuCardValues = skuCardValues;
	}

	public String getRuntimeGraphURL() {
		return runtimeGraphURL;
	}

	public void setRuntimeGraphURL(String runtimeGraphURL) {
		this.runtimeGraphURL = runtimeGraphURL;
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
    	    	    	  return "Error in toString of SKUCardParameterBean"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }
	
}