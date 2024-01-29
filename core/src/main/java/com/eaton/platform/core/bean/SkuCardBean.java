package com.eaton.platform.core.bean;

import java.io.Serializable;

public class SkuCardBean implements Serializable  {
private static final long serialVersionUID = 1L;

private String image;
private String catalog;

private String productName;

private String productFamily;

private byte priority;

private String longDesc;

private String link;
	
private String target;


/**
 * @return the target
 */
public String getTarget() {
	return target;
}

/**
 * @param target the target to set
 */
public void setTarget(String target) {
	this.target = target;
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

public String getImage() {
	return image;
}

public void setImage(String image) {
	this.image = image;
}

public String getCatalog() {
	return catalog;
}

public void setCatalog(String catalog) {
	this.catalog = catalog;
}

public String getProductName() {
	return productName;
}

public void setProductName(String productName) {
	this.productName = productName;
}

public String getProductFamily() {
	return productFamily;
}

public void setProductFamily(String productFamily) {
	this.productFamily = productFamily;
}

public String getLongDesc() {
	return longDesc;
}

public void setLongDesc(String longDesc) {
	this.longDesc = longDesc;
}

public byte getPriority() {
	return priority;
}

public void setPriority(byte priority) {
	this.priority = priority;
}

	
}
