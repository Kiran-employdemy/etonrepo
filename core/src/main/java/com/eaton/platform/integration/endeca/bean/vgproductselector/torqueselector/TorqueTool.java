package com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import com.eaton.platform.integration.endeca.bean.familymodule.SKUCardParameterBean;

/**
 * The Class TorqueTool.
 */
public class TorqueTool implements Serializable{
	
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1741006001535227070L;
	
	/** The catalog number. */
	private String catalogNumber;
    
    /** The encoded catalog number. */
    private String encodedCatalogNumber;
	
	/** The long desc. */
	private String longDesc;
	
	/** The dsktop rendition. */
	private String dsktopRendition;
	
	/** The mobile rendition. */
	private String mobileRendition;
	
	/** The axle info. */
	private String axleInfo;
	
	/** The spline count. */
	private String splineCount;
	
	/** The sku card parameters. */
	private List<SKUCardParameterBean> skuCardParameters;
	
	/**
	 * Gets the catalog number.
	 *
	 * @return the catalogNumber
	 */
	public String getCatalogNumber() {
		return catalogNumber;
	}
	
	/**
	 * Sets the catalog number.
	 *
	 * @param catalogNumber the catalogNumber to set
	 */
	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}
	
	/**
	 * Gets the long desc.
	 *
	 * @return the longDesc
	 */
	public String getLongDesc() {
		return longDesc;
	}
	
	/**
	 * Sets the long desc.
	 *
	 * @param longDesc the longDesc to set
	 */
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}
	
	/**
	 * Gets the dsktop rendition.
	 *
	 * @return the dsktopRendition
	 */
	public String getDsktopRendition() {
		return dsktopRendition;
	}
	
	/**
	 * Sets the dsktop rendition.
	 *
	 * @param dsktopRendition the dsktopRendition to set
	 */
	public void setDsktopRendition(String dsktopRendition) {
		this.dsktopRendition = dsktopRendition;
	}
	
	/**
	 * Gets the mobile rendition.
	 *
	 * @return the mobileRendition
	 */
	public String getMobileRendition() {
		return mobileRendition;
	}
	
	/**
	 * Sets the mobile rendition.
	 *
	 * @param mobileRendition the mobileRendition to set
	 */
	public void setMobileRendition(String mobileRendition) {
		this.mobileRendition = mobileRendition;
	}
	
	/**
	 * Gets the axle info.
	 *
	 * @return the axleInfo
	 */
	public String getAxleInfo() {
		return axleInfo;
	}
	
	/**
	 * Sets the axle info.
	 *
	 * @param axleInfo the axleInfo to set
	 */
	public void setAxleInfo(String axleInfo) {
		this.axleInfo = axleInfo;
	}
	
	/**
	 * Gets the spline count.
	 *
	 * @return the splineCount
	 */
	public String getSplineCount() {
		return splineCount;
	}
	
	/**
	 * Sets the spline count.
	 *
	 * @param splineCount the splineCount to set
	 */
	public void setSplineCount(String splineCount) {
		this.splineCount = splineCount;
	}
	
	 /**
 	 * Gets the sku card parameters.
 	 *
 	 * @return the skuCardParameters
 	 */
	public List<SKUCardParameterBean> getSkuCardParameters() {
		return skuCardParameters;
	}
	
	/**
	 * Sets the sku card parameters.
	 *
	 * @param skuCardParameters the skuCardParameters to set
	 */
	public void setSkuCardParameters(List<SKUCardParameterBean> skuCardParameters) {
		this.skuCardParameters = skuCardParameters;
	}
	
	/**
	 * Gets the encoded catalog number.
	 *
	 * @return the encoded catalog number
	 */
	public String getEncodedCatalogNumber() {
		return encodedCatalogNumber;
	}
	
	/**
	 * Sets the encoded catalog number.
	 *
	 * @param encodedCatalogNumber the new encoded catalog number
	 */
	public void setEncodedCatalogNumber(String encodedCatalogNumber) {
		this.encodedCatalogNumber = encodedCatalogNumber;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
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
    	    	    	  return "Error in toString of TorqueSearchResults"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }

}
