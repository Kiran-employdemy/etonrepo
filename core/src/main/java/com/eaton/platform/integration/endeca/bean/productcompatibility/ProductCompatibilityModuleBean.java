
package com.eaton.platform.integration.endeca.bean.productcompatibility;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import com.eaton.platform.core.bean.ImageRenditionBean;

/**
 * FamilyModuleBean class
 */
public class ProductCompatibilityModuleBean {

	/** Default image URL to fall back to, if unavailable from Endeca TST_EXT_Image_URL for LEDs. */
	private static final String DEFAULT_IMAGE_URL = "/content/dam/eaton/resources/default-sku-image.jpg";

	//get all the items from response
	private String mktgDesc;
	private String mktgDescLabel;
	private String country;
	private String countryLabel;
    public String getCountryLabel() {
		return countryLabel;
	}
	public void setCountryLabel(String countryLabel) {
		this.countryLabel = countryLabel;
	}
	public String getMktgDescLabel() {
		return mktgDescLabel;
	}
	public void setMktgDescLabel(String mktgDescLabel) {
		this.mktgDescLabel = mktgDescLabel;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getExtModelNumber() {
		return extModelNumber;
	}
	public void setExtModelNumber(String extModelNumber) {
		this.extModelNumber = extModelNumber;
	}
	public String getExtType() {
		return extType;
	}
	public void setExtType(String extType) {
		this.extType = extType;
	}
	public String getControlFamily() {
		return controlFamily;
	}
	public void setControlFamily(String controlFamily) {
		this.controlFamily = controlFamily;
	}
	public String getFamilyID() {
		return familyID;
	}
	public void setFamilyID(String familyID) {
		this.familyID = familyID;
	}
	public String getDimmerCatalogNumber() {
		return dimmerCatalogNumber;
	}
	public void setDimmerCatalogNumber(String dimmerCatalogNumber) {
		this.dimmerCatalogNumber = dimmerCatalogNumber;
	}
	public String getLowesItemNumber() {
		return lowesItemNumber;
	}
	public void setLowesItemNumber(String lowesItemNumber) {
		this.lowesItemNumber = lowesItemNumber;
	}
	public String getLowesModelNumber() {
		return lowesModelNumber;
	}
	public void setLowesModelNumber(String lowesModelNumber) {
		this.lowesModelNumber = lowesModelNumber;
	}
	public String getLedRating() {
		return ledRating;
	}
	public void setLedRating(String ledRating) {
		this.ledRating = ledRating;
	}
	public String getMaxofloads() {
		return maxofloads;
	}
	public void setMaxofloads(String maxofloads) {
		this.maxofloads = maxofloads;
	}
	public String getExtLampModelNumber() {
		return extLampModelNumber;
	}
	public void setExtLampModelNumber(String extLampModelNumber) {
		this.extLampModelNumber = extLampModelNumber;
	}
	public String getExtImageURL() {
		return extImageURL;
	}
	public void setExtImageURL(String extImageURL) {
		this.extImageURL = extImageURL;
	}
	public String getLEDImageURL() {
    	return ledImageURL;
    }
	public void setLEDImageURL(String url) {
		if(url != null && !"".equals(url)) {
			this.ledImageURL = url;
		} else {
			this.ledImageURL = DEFAULT_IMAGE_URL;
		}
    }
	public String getExtLampVoltage() {
		return extLampVoltage;
	}
	public void setExtLampVoltage(String extLampVoltage) {
		this.extLampVoltage = extLampVoltage;
	}
	public String getExtColorTemperature() {
		return extColorTemperature;
	}
	public void setExtColorTemperature(String extColorTemperature) {
		this.extColorTemperature = extColorTemperature;
	}
	public String getLedCompatibleScore() {
		return ledCompatibleScore;
	}
	public void setLedCompatibleScore(String ledCompatibleScore) {
		this.ledCompatibleScore = ledCompatibleScore;
	}
	public String getExtBase() {
		return extBase;
	}
	public void setExtBase(String extBase) {
		this.extBase = extBase;
	}
	public String getExtLampManufacturer() {
		return extLampManufacturer;
	}
	public void setExtLampManufacturer(String extLampManufacturer) {
		this.extLampManufacturer = extLampManufacturer;
	}
	public String getExtDateCode() {
		return extDateCode;
	}
	public void setExtDateCode(String extDateCode) {
		this.extDateCode = extDateCode;
	}
	public String getExtLampLumenOutput() {
		return extLampLumenOutput;
	}
	public void setExtLampLumenOutput(String extLampLumenOutput) {
		this.extLampLumenOutput = extLampLumenOutput;
	}
	public String getFlicker() {
		return flicker;
	}
	public void setFlicker(String flicker) {
		this.flicker = flicker;
	}
	public String getLowEndTrim() {
		return lowEndTrim;
	}
	public void setLowEndTrim(String lowEndTrim) {
		this.lowEndTrim = lowEndTrim;
	}
	public String getRelativeLowEnd() {
		return relativeLowEnd;
	}
	public void setRelativeLowEnd(String relativeLowEnd) {
		this.relativeLowEnd = relativeLowEnd;
	}
	public String getPerceivedDimmingrange() {
		return perceivedDimmingrange;
	}
	public void setPerceivedDimmingrange(String perceivedDimmingrange) {
		this.perceivedDimmingrange = perceivedDimmingrange;
	}
	public String getPerceivedLowend() {
		return perceivedLowend;
	}
	public void setPerceivedLowend(String perceivedLowend) {
		this.perceivedLowend = perceivedLowend;
	}
	public String getTestNotes() {
		return testNotes;
	}
	public void setTestNotes(String testNotes) {
		this.testNotes = testNotes;
	}
	public String getExtModelNumberLabel() {
		return extModelNumberLabel;
	}
	public void setExtModelNumberLabel(String extModelNumberLabel) {
		this.extModelNumberLabel = extModelNumberLabel;
	}
	public String getExtTypeLabel() {
		return extTypeLabel;
	}
	public void setExtTypeLabel(String extTypeLabel) {
		this.extTypeLabel = extTypeLabel;
	}
	public String getControlFamilyLabel() {
		return controlFamilyLabel;
	}
	public void setControlFamilyLabel(String controlFamilyLabel) {
		this.controlFamilyLabel = controlFamilyLabel;
	}
	public String getFamilyIDLabel() {
		return familyIDLabel;
	}
	public void setFamilyIDLabel(String familyIDLabel) {
		this.familyIDLabel = familyIDLabel;
	}
	public String getDimmerCatalogNumberLabel() {
		return dimmerCatalogNumberLabel;
	}
	public void setDimmerCatalogNumberLabel(String dimmerCatalogNumberLabel) {
		this.dimmerCatalogNumberLabel = dimmerCatalogNumberLabel;
	}
	public String getLowesItemNumberLabel() {
		return lowesItemNumberLabel;
	}
	public void setLowesItemNumberLabel(String lowesItemNumberLabel) {
		this.lowesItemNumberLabel = lowesItemNumberLabel;
	}
	public String getLowesModelNumberLabel() {
		return lowesModelNumberLabel;
	}
	public void setLowesModelNumberLabel(String lowesModelNumberLabel) {
		this.lowesModelNumberLabel = lowesModelNumberLabel;
	}
	public String getLedRatingLabel() {
		return ledRatingLabel;
	}
	public void setLedRatingLabel(String ledRatingLabel) {
		this.ledRatingLabel = ledRatingLabel;
	}
	public String getMaxofloadsLabel() {
		return maxofloadsLabel;
	}
	public void setMaxofloadsLabel(String maxofloadsLabel) {
		this.maxofloadsLabel = maxofloadsLabel;
	}
	public String getExtLampModelNumberLabel() {
		return extLampModelNumberLabel;
	}
	public void setExtLampModelNumberLabel(String extLampModelNumberLabel) {
		this.extLampModelNumberLabel = extLampModelNumberLabel;
	}
	public String getExtImageURLLabel() {
		return extImageURLLabel;
	}
	public void setExtImageURLLabel(String extImageURLLabel) {
		this.extImageURLLabel = extImageURLLabel;
	}
	public String getExtLampVoltageLabel() {
		return extLampVoltageLabel;
	}
	public void setExtLampVoltageLabel(String extLampVoltageLabel) {
		this.extLampVoltageLabel = extLampVoltageLabel;
	}
	public String getExtColorTemperatureLabel() {
		return extColorTemperatureLabel;
	}
	public void setExtColorTemperatureLabel(String extColorTemperatureLabel) {
		this.extColorTemperatureLabel = extColorTemperatureLabel;
	}
	public String getLedCompatibleScoreLabel() {
		return ledCompatibleScoreLabel;
	}
	public void setLedCompatibleScoreLabel(String ledCompatibleScoreLabel) {
		this.ledCompatibleScoreLabel = ledCompatibleScoreLabel;
	}
	public String getExtBaseLabel() {
		return extBaseLabel;
	}
	public void setExtBaseLabel(String extBaseLabel) {
		this.extBaseLabel = extBaseLabel;
	}
	public String getExtLampManufacturerLabel() {
		return extLampManufacturerLabel;
	}
	public void setExtLampManufacturerLabel(String extLampManufacturerLabel) {
		this.extLampManufacturerLabel = extLampManufacturerLabel;
	}
	public String getExtDateCodeLabel() {
		return extDateCodeLabel;
	}
	public void setExtDateCodeLabel(String extDateCodeLabel) {
		this.extDateCodeLabel = extDateCodeLabel;
	}
	public String getExtLampLumenOutputLabel() {
		return extLampLumenOutputLabel;
	}
	public void setExtLampLumenOutputLabel(String extLampLumenOutputLabel) {
		this.extLampLumenOutputLabel = extLampLumenOutputLabel;
	}
	public String getFlickerLabel() {
		return flickerLabel;
	}
	public void setFlickerLabel(String flickerLabel) {
		this.flickerLabel = flickerLabel;
	}
	public String getLowEndTrimLabel() {
		return lowEndTrimLabel;
	}
	public void setLowEndTrimLabel(String lowEndTrimLabel) {
		this.lowEndTrimLabel = lowEndTrimLabel;
	}
	public String getLowEndLabel() {
		return lowEndLabel;
	}
	public void setLowEndLabel(String lowEndLabel) {
		this.lowEndLabel = lowEndLabel;
	}
	public String getRelativeLowEndLabel() {
		return relativeLowEndLabel;
	}
	public void setRelativeLowEndLabel(String relativeLowEndLabel) {
		this.relativeLowEndLabel = relativeLowEndLabel;
	}
	public String getPerceivedDimmingrangeLabel() {
		return perceivedDimmingrangeLabel;
	}
	public void setPerceivedDimmingrangeLabel(String perceivedDimmingrangeLabel) {
		this.perceivedDimmingrangeLabel = perceivedDimmingrangeLabel;
	}
	public String getPerceivedLowendLabel() {
		return perceivedLowendLabel;
	}
	public void setPerceivedLowendLabel(String perceivedLowendLabel) {
		this.perceivedLowendLabel = perceivedLowendLabel;
	}
	public String getTestNotesLabel() {
		return testNotesLabel;
	}
	public void setTestNotesLabel(String testNotesLabel) {
		this.testNotesLabel = testNotesLabel;
	}

	private String extModelNumber;
	private String extModelNumberLabel;
	private String extType;
	private String extTypeLabel;
	private String controlFamily;
	private String controlFamilyLabel;
	private String familyID;
	private String familyIDLabel;
	private String dimmerCatalogNumber;
	private String dimmerCatalogNumberLabel;
	private String lowesItemNumber;
	private String lowesItemNumberLabel;
	private String lowesModelNumber;
	private String lowesModelNumberLabel;
	private String ledRating;
	private String ledRatingLabel;
	private String maxofloads;
	private String maxofloadsLabel;
	private String extLampModelNumber;
	private String extLampModelNumberLabel;
	private String extImageURL;
	private String extImageURLLabel;
	private String ledImageURL;
	private String extLampVoltage;
	private String extLampVoltageLabel;
	private String extColorTemperature;
	private String extColorTemperatureLabel;
	private String ledCompatibleScore;
	private String ledCompatibleScoreLabel;
	private String extBase;
	private String extBaseLabel;
	private String extLampManufacturer;
	private String extLampManufacturerLabel;
	private String extDateCode;
	private String extDateCodeLabel;
	private String extLampLumenOutput;
	private String extLampLumenOutputLabel;
    private String flicker;
	private String flickerLabel;
	private String lowEndTrim;
	private String lowEndTrimLabel;
	private String lowEnd;
	private String lowEndLabel;
	private String relativeLowEnd;
	private String relativeLowEndLabel;
	private String perceivedDimmingrange;
	private String perceivedDimmingrangeLabel;
	private String perceivedLowend;
	private String perceivedLowendLabel;
	private String testNotes;
	private String testNotesLabel;


	// family bean
    private String catalogNumber;
    private String encodedCatalogNumber;
    private String familyName;
    
    private String longDesc;
    private String primaryImage;
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
	 * @return the lowEnd
	 */
	public String getLowEnd() {
		return lowEnd;
	}
	/**
	 * @param lowEnd the lowEnd to set
	 */
	public void setLowEnd(String lowEnd) {
		this.lowEnd = lowEnd;
	}
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
