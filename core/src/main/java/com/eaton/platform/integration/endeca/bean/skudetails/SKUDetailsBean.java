package com.eaton.platform.integration.endeca.bean.skudetails;

import com.google.common.base.Strings;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.lang.reflect.Field;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SKUDetailsBean implements Serializable {

	public static final String MODEL_CODE_BEGIN = "<ATR_NM id=\"Model_Code\"";
	public static final String ATTRIBUTE_END = "</ATR_NM>";
	public static final String CDATA_BEGIN = "<![CDATA[";
	public static final String CDATA_END = "]]>";

	/**
	 *
	 */
	private static final long serialVersionUID = 898433819227467274L;

	private String inventoryId;
	private String extensionId;
	private String brand;
	private String subBrand;
	private String tradeName;
	private String familyName;
	private String mktgDesc;
	private String globalAttrs;
	private String taxonomyAttrs;
	private String skuImgs;
	private String documents;
	private String upSell;
	private String crossSell;
	private String cid;
	private String status;
	private String replacement;
	private Boolean serialFlag;
	private Boolean serialAuthFlag;
	private String repEmail;
	private String[] countryList;
	/**
	 * @return the inventoryId
	 */
	public String getInventoryId() {
		return inventoryId;
	}
	/**
	 * @param inventoryId the inventoryId to set
	 */
	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}
	/**
	 * @return the extensionId
	 */
	public String getExtensionId() {
		return extensionId;
	}
	/**
	 * @param extensionId the extensionId to set
	 */
	public void setExtensionId(String extensionId) {
		this.extensionId = extensionId;
	}
	/**
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}
	/**
	 * @param brand the brand to set
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}
	/**
	 * @return the subBrand
	 */
	public String getSubBrand() {
		return subBrand;
	}
	/**
	 * @param subBrand the subBrand to set
	 */
	public void setSubBrand(String subBrand) {
		this.subBrand = subBrand;
	}
	/**
	 * @return the tradeName
	 */
	public String getTradeName() {
		return tradeName;
	}
	/**
	 * @param tradeName the tradeName to set
	 */
	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
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
	 * @return the globalAttrs
	 */
	public String getGlobalAttrs() {
		return globalAttrs;
	}
	/**
	 * @param globalAttrs the globalAttrs to set
	 */
	public void setGlobalAttrs(String globalAttrs) {
		this.globalAttrs = globalAttrs;
	}
	/**
	 * @return the taxonomyAttrs
	 */
	public String getTaxonomyAttrs() {
		return taxonomyAttrs;
	}
	/**
	 * @param taxonomyAttrs the taxonomyAttrs to set
	 */
	public void setTaxonomyAttrs(String taxonomyAttrs) {
		this.taxonomyAttrs = taxonomyAttrs;
	}
	/**
	 * @return the skuImgs
	 */
	public String getSkuImgs() {
		return skuImgs;
	}
	/**
	 * @param skuImgs the skuImgs to set
	 */
	public void setSkuImgs(String skuImgs) {
		this.skuImgs = skuImgs;
	}
	/**
	 * @return the documents
	 */
	public String getDocuments() {
		return documents;
	}
	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(String documents) {
		this.documents = documents;
	}
	/**
	 * @return the upSell
	 */
	public String getUpSell() {
		return upSell;
	}
	/**
	 * @param upSell the upSell to set
	 */
	public void setUpSell(String upSell) {
		this.upSell = upSell;
	}
	/**
	 * @return the crossSell
	 */
	public String getCrossSell() {
		return crossSell;
	}
	/**
	 * @param crossSell the crossSell to set
	 */
	public void setCrossSell(String crossSell) {
		this.crossSell = crossSell;
	}


	 /**
	 * @return the cid
	 */
	public String getCid() {
		return cid;
	}
	/**
	 * @param cid the cid to set
	 */
	public void setCid(String cid) {
		this.cid = cid;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the replacement
	 */
	public String getReplacement() {
		return replacement;
	}
	/**
	 * @param replacement the replacement to set
	 */
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	public Boolean getSerialFlag() {
		return serialFlag;
	}

	public void setSerialFlag(Boolean serialFlag) {
		this.serialFlag = serialFlag;
	}

	public Boolean getSerialAuthFlag() {
		return serialAuthFlag;
	}

	public void setSerialAuthFlag(Boolean serialAuthFlag) {
		this.serialAuthFlag = serialAuthFlag;
	}

	public String getRepEmail() {
		return repEmail;
	}

	public void setRepEmail(String repEmail) {
		this.repEmail = repEmail;
	}

	public String[] getCountryList() {
		return countryList;
	}

	public void setCountryList(String[] countryList) {
		this.countryList = countryList;
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
    	    	    	  return "Error in toString of SKUDetailsBean"+ex.getMessage();
    	    	      }
    	    	      result.append(newLine);
    	    	    }
    	    	}

    	    return result.toString();
     }

	public String getModelCode() {
		if (Strings.isNullOrEmpty(globalAttrs) || !globalAttrs.contains(MODEL_CODE_BEGIN)) {
			return null;
		}
		String fromModelCode = globalAttrs.substring(globalAttrs.indexOf(MODEL_CODE_BEGIN));
		String modelCodeTag = fromModelCode.substring(0, fromModelCode.indexOf(ATTRIBUTE_END));
		String cdataPart = modelCodeTag.substring(modelCodeTag.indexOf(CDATA_BEGIN), modelCodeTag.indexOf(CDATA_END));
		return cdataPart.replace(CDATA_BEGIN, "");
	}
}
