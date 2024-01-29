
package com.eaton.platform.integration.endeca.bean.familymodule;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import com.eaton.platform.core.bean.ImageRenditionBean;

/**
 * FamilyModuleBean class
 */
public class FamilyModuleBean implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -6068195613198957240L;
	
    private String catalogNumber;
    private String encodedCatalogNumber;
    private String familyName;
    private String mktgDesc;
    private String modelCode;
    private String longDesc;
    private String primaryImage;
    private List<SKUCardParameterBean> skuCardParameters;
    private String price;
    private ImageRenditionBean productImageBean;
    
	private String dsktopRendition;
	private String mobileRendition;
	private Boolean isAltSku = false;
	private String status;
	
	private String[] skusArray;
	public static final String SERIAL_VERSION_UID ="serialVersionUID";
	public static final String LINE_SEPARATOR ="line.separator";
	
	/**
	 * @return the dsktopRendition
	 */
	public String getDsktopRendition() {
		return dsktopRendition;
	}
	/**
	 * @param dsktopRendition the dsktopRendition to set
	 */
	public void setDsktopRendition(String dsktopRendition) {
		this.dsktopRendition = dsktopRendition;
	}
	/**
	 * @return the mobileRendition
	 */
	public String getMobileRendition() {
		return mobileRendition;
	}
	/**
	 * @param mobileRendition the mobileRendition to set
	 */
	public void setMobileRendition(String mobileRendition) {
		this.mobileRendition = mobileRendition;
	}
    
    
    
    public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
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
	 * @return the familyName
	 */
	public String getFamilyName() {
		return familyName;
	}
	/**
	 * @param familyName the familyName to set
	 */
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	/**
	 * @return the mktgDesc
	 */
	public String getMktgDesc() {
		return mktgDesc;
	}
	/**
	 * @param mktgDesc the mktgDesc to set
	 */
	public void setMktgDesc(String mktgDesc) {
		this.mktgDesc = mktgDesc;
	}
	/**
	 * @return the modelCode
	 */
	public String getModelCode() {
		return modelCode;
	}
	/**
	 * @param modelCode the modelCode to set
	 */
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	/**
	 * @return the longDesc
	 */
	public String getLongDesc() {
		return longDesc;
	}
	/**
	 * @param longDesc the longDesc to set
	 */
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}
	

	/**
	 * @return the primaryImage
	 */
	public String getPrimaryImage() {
		return primaryImage;
	}
	/**
	 * @param primaryImage the primaryImage to set
	 */
	public void setPrimaryImage(String primaryImage) {
		this.primaryImage = primaryImage;
	}
	/**
	 * @return the skuCardParameters
	 */
	public List<SKUCardParameterBean> getSkuCardParameters() {
		return skuCardParameters;
	}
	/**
	 * @param skuCardParameters the skuCardParameters to set
	 */
	public void setSkuCardParameters(List<SKUCardParameterBean> skuCardParameters) {
		this.skuCardParameters = skuCardParameters;
	}

	/**
	 * Intended only for debugging.
	 *
	 * <P>
	 * Here, the contents of every field are placed into the result, with one field
	 * per line.
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty(LINE_SEPARATOR);
		result.append(newLine);
		// determine fields declared in this class only (no fields of superclass)
		Field[] fields = this.getClass().getDeclaredFields();
		// print field names paired with their values
		for (Field field : fields) {
			if (!SERIAL_VERSION_UID.equalsIgnoreCase(field.getName())) {
				result.append("  ");
				try {
					result.append(field.getName());
					result.append(": ");
					// requires access to private field:
					result.append(field.get(this));
				} catch (IllegalAccessException ex) {
					return "Error in toString of FamilyModuleBean" + ex.getMessage();
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
	public String getEncodedCatalogNumber() {
		return encodedCatalogNumber;
	}
	public void setEncodedCatalogNumber(String encodedCatalogNumber) {
		this.encodedCatalogNumber = encodedCatalogNumber;
	}

	public Boolean getAltSku() {
		return isAltSku;
	}

	public void setAltSku(Boolean altSku) {
		isAltSku = altSku;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String[] getSkusArray() {
		return skusArray;
	}
	public void setSkusArray(String[] skusArray) {
		this.skusArray = skusArray;
	}
	
}
