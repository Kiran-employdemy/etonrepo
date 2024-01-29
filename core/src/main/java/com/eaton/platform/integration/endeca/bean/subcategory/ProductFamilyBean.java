package com.eaton.platform.integration.endeca.bean.subcategory;

import com.eaton.platform.core.bean.ImageRenditionBean;
import org.apache.commons.lang.StringUtils;


import java.io.Serializable;
import java.lang.reflect.Field;

import java.util.List;
public class ProductFamilyBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -332307284266283791L;
	private String productName;
	private String productImage;
	private String subCategoryName;
	private String url;
	private ImageRenditionBean productImageBean;
	private String productStatus;
	private boolean secure;
	private String productGridDescription;
	private boolean newBadgeVisible;
	private boolean partnerBadgeVisible;
	private String productType;	
	private String companyName;
	private List<String> productTags;
	private String publishDate;


	private static final String SERIALVERSIONUID_STRING = "serialVersionUID";

	/**
	 *
	 * @return if product family is secure
	 */
	public boolean isSecure() {
		return secure;
	}

	/**
	 *
	 * @param secure set if product family is secure
	 */
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the productImage
	 */
	public String getProductImage() {
		return productImage;
	}
	/**
	 * @param productImage the productImage to set
	 */
	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}


	/**
	 * @return the subCategoryName
	 */
	public String getSubCategoryName() {
		return subCategoryName;
	}
	/**
	 * @param subCategoryName the subCategoryName to set
	 */
	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}
	/**
	 * @return the productStatus
	 */
	public String getProductStatus() {
		return productStatus;
	}
	/**
	 * @param productStatus the productStatus to set
	 */
	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
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
				if(!SERIALVERSIONUID_STRING.equalsIgnoreCase(field.getName())){
					 result.append("  ");
					  try {
						result.append(field.getName());
						result.append(": ");
						//requires access to private field:
						result.append(field.get(this));
					  }
					  catch (IllegalAccessException ex) {
						  return "Error in toString of ProductFamilyBean"+ex.getMessage();
					  }
					  result.append(newLine);
					}
				}

    	    return result.toString();
     }
	public ImageRenditionBean getProductImageBean() {
		return productImageBean;
	}
	public void setProductImageBean(ImageRenditionBean productImageBean) {
		this.productImageBean = productImageBean;
	}

	public void setProductGridDescription(String productGridDescription) {
		this.productGridDescription = productGridDescription;
	}

	public String getProductGridDescription() {
		if(productGridDescription!=null) {
			return productGridDescription;
		}else {
			return StringUtils.EMPTY;
		}
	}
	public boolean getNewBadgeVisible() {
		return newBadgeVisible;
	}

	public void setNewBadgeVisible(boolean newBadgeVisible) {
		this.newBadgeVisible = newBadgeVisible;
	}

	public boolean getPartnerBadgeVisible() {
		return partnerBadgeVisible;
	}

	public void setPartnerBadgeVisible(boolean partnerBadgeVisible) {
		this.partnerBadgeVisible = partnerBadgeVisible;
	}	
		 
	public String getProductType() {
			return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
		 
	public String getCompanyName() {
		 return this.companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public List<String> getProductTags() {
		return productTags;
	}

	public void setProductTags(List<String> productTags) {
		this.productTags = productTags;
	}

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

}