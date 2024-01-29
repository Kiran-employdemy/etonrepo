package com.eaton.platform.core.bean.loadmore;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
		"name",
		"modelCode",
		"description",
		"productPrice",
		"image",
		"link",
		"productLinks",
		"productAttributes"
})
public class SKUListContentItem {
	@JsonProperty("name")
	private String name;
	@JsonProperty("modelCode")
	private String modelCode;
	@JsonProperty("description")
	private String description;
	@JsonProperty("productPrice")
	private String productPrice;
	@JsonProperty("image")
	private Image image;
	@JsonProperty("link")
	private Link link;
	@JsonProperty("productLinks")
	private ProductLinks productLinks;
	@JsonProperty("productAttributes")
	private List<ProductAttribute> productAttributes = null;
     
	private String lampManufacturer;
	private String lampModelNumber;
	private String lampManufacturerlabel;
	private String lampModelNumberlabel;
	public String getLampManufacturerlabel() {
		return lampManufacturerlabel;
	}

	public void setLampManufacturerlabel(String lampManufacturerlabel) {
		this.lampManufacturerlabel = lampManufacturerlabel;
	}

	public String getLampModelNumberlabel() {
		return lampModelNumberlabel;
	}

	public void setLampModelNumberlabel(String lampModelNumberlabel) {
		this.lampModelNumberlabel = lampModelNumberlabel;
	}

	public String getLampManufacturer() {
		return lampManufacturer;
	}

	public void setLampManufacturer(String lampManufacturer) {
		this.lampManufacturer = lampManufacturer;
	}

	public String getLampModelNumber() {
		return lampModelNumber;
	}

	public void setLampModelNumber(String lampModelNumber) {
		this.lampModelNumber = lampModelNumber;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("modelCode")
	public String getModelCode() {
		return modelCode;
	}

	@JsonProperty("modelCode")
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}

	@JsonProperty("productPrice")
	public String getProductPrice() {
		return productPrice;
	}

	@JsonProperty("productPrice")
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}

	@JsonProperty("image")
	public Image getImage() {
		return image;
	}

	@JsonProperty("image")
	public void setImage(Image image) {
		this.image = image;
	}

	@JsonProperty("link")
	public Link getLink() {
		return link;
	}

	@JsonProperty("link")
	public void setLink(Link link) {
		this.link = link;
	}

	@JsonProperty("productLinks")
	public ProductLinks getProductLinks() {
		return productLinks;
	}

	@JsonProperty("productLinks")
	public void setProductLinks(ProductLinks productLinks) {
		this.productLinks = productLinks;
	}

	@JsonProperty("productAttributes")
	public List<ProductAttribute> getProductAttributes() {
		return productAttributes;
	}

	@JsonProperty("productAttributes")
	public void setProductAttributes(List<ProductAttribute> productAttributes) {
		this.productAttributes = productAttributes;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("description", description)
				.append("productPrice", productPrice).append("image", image).append("link", link)
				.append("productLinks", productLinks).append("productAttributes", productAttributes).toString();
	}
}
