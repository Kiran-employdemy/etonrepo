package com.eaton.platform.integration.endeca.bean;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.ALWAYS)
@JsonPropertyOrder({
"binningSet"
})
public class FacetsBean implements Serializable {

	
	private static final long serialVersionUID = -2013205279644301462L;
	@JsonProperty("binningSet")
    private List<FacetGroupBean> facetGroupList;
	
	
	

	/**
	 * @return the facetGroupList
	 */
	public List<FacetGroupBean> getFacetGroupList() {
		return facetGroupList;
	}




	/**
	 * @param facetGroupList the facetGroupList to set
	 */
	public void setFacetGroupList(List<FacetGroupBean> facetGroupList) {
		this.facetGroupList = facetGroupList;
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
    	    	    	  return "Error in toString of FacetsBean"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }
}