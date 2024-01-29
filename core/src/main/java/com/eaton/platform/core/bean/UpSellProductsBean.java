package com.eaton.platform.core.bean;

import java.io.Serializable;
import java.util.List;



/**
 * <html> Description: This bean class used in UpSellProductsBean class to store content </html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
public class UpSellProductsBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7130265800501204353L;

		
	private String image;
	
	private String familyName;
	
	private String catalogNumber;
	
	private String link;
	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
	  this.image = image;
	}
	/**
	 * @return the familyName
	 */
	public String getFamilyName() {
		return familyName;
	}
	/**
	 * @param familyName the familyName to set
	 * @return 
	 */
	public void setFamilyName(String familyName) {
		 this.familyName = familyName;
	}
	/**
	 * @return the catalogNumber
	 */
	public String getCatalogNumber() {
		return catalogNumber;
	}
	/**
	 * @param catalogNumber the catalogNumber to set
	 */
	public void setCatalogNumber(String catalogNumber) {
		this.catalogNumber = catalogNumber;
	}
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
}
