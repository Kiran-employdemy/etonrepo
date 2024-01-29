package com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import com.eaton.platform.integration.endeca.bean.familymodule.SKUCardParameterBean;

/**
 * The Class ClutchTool.
 */
public class ClutchTool implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6670689849794449631L;
	
	/** The product name. */
	private String productName;
	
	/** The catalog number. */
	private String catalogNumber;
    
    /** The encoded catalog number. */
    private String encodedCatalogNumber;
	
	/** The long desc. */
	private String longDesc;
	
	/** The primary image. */
	private String primaryImage;
	
	/** The dsktop rendition. */
	private String dsktopRendition;
	
	/** The mobile rendition. */
	private String mobileRendition;
	
	/** The clutch size. */
	private String clutchSize;
	
	/** The damper spring count. */
	private String damperSpringCount;
	
	/** The torque capacity. */
	private String torqueCapacity;
	
	/** The input shaft size. */
	private String inputShaftSize;
	
	/** The pre damper. */
	private String preDamper;
	
	/** The quality designation. */
	private String qualityDesignation;
	
	/** The sku card parameters. */
	private List<SKUCardParameterBean> skuCardParameters;
	
	/**
	 * Gets the product name.
	 *
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	
	/**
	 * Sets the product name.
	 *
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
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
	 * Gets the primary image.
	 *
	 * @return the primaryImage
	 */
	public String getPrimaryImage() {
		return primaryImage;
	}
	
	/**
	 * Sets the primary image.
	 *
	 * @param primaryImage the primaryImage to set
	 */
	public void setPrimaryImage(String primaryImage) {
		this.primaryImage = primaryImage;
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
	 * Gets the clutch size.
	 *
	 * @return the clutchSize
	 */
	public String getClutchSize() {
		return clutchSize;
	}
	
	/**
	 * Sets the clutch size.
	 *
	 * @param clutchSize the clutchSize to set
	 */
	public void setClutchSize(String clutchSize) {
		this.clutchSize = clutchSize;
	}
	
	/**
	 * Gets the damper spring count.
	 *
	 * @return the damperSpringCount
	 */
	public String getDamperSpringCount() {
		return damperSpringCount;
	}
	
	/**
	 * Sets the damper spring count.
	 *
	 * @param damperSpringCount the damperSpringCount to set
	 */
	public void setDamperSpringCount(String damperSpringCount) {
		this.damperSpringCount = damperSpringCount;
	}
	
	/**
	 * Gets the torque capacity.
	 *
	 * @return the torqueCapacity
	 */
	public String getTorqueCapacity() {
		return torqueCapacity;
	}
	
	/**
	 * Sets the torque capacity.
	 *
	 * @param torqueCapacity the torqueCapacity to set
	 */
	public void setTorqueCapacity(String torqueCapacity) {
		this.torqueCapacity = torqueCapacity;
	}
	
	/**
	 * Gets the input shaft size.
	 *
	 * @return the inputShaftSize
	 */
	public String getInputShaftSize() {
		return inputShaftSize;
	}
	
	/**
	 * Sets the input shaft size.
	 *
	 * @param inputShaftSize the inputShaftSize to set
	 */
	public void setInputShaftSize(String inputShaftSize) {
		this.inputShaftSize = inputShaftSize;
	}
	
	/**
	 * Gets the pre damper.
	 *
	 * @return the preDamper
	 */
	public String getPreDamper() {
		return preDamper;
	}
	
	/**
	 * Sets the pre damper.
	 *
	 * @param preDamper the preDamper to set
	 */
	public void setPreDamper(String preDamper) {
		this.preDamper = preDamper;
	}
	
	/**
	 * Gets the quality designation.
	 *
	 * @return the qualityDesignation
	 */
	public String getQualityDesignation() {
		return qualityDesignation;
	}
	
	/**
	 * Sets the quality designation.
	 *
	 * @param qualityDesignation the qualityDesignation to set
	 */
	public void setQualityDesignation(String qualityDesignation) {
		this.qualityDesignation = qualityDesignation;
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
  	    	    	  return "Error in toString of ClutchToolResponse"+ex.getMessage();
  	    	      }
  	    	      result.append(newLine);
  	    	    }
  	    	}

  	    return result.toString();
   }
}
