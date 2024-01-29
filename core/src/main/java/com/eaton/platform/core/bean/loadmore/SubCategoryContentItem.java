
package com.eaton.platform.core.bean.loadmore;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "name",
    "subcategory",
    "image",
    "link",
	"secure"
})
public class SubCategoryContentItem {

    @JsonProperty("name")
    private String name;
    @JsonProperty("subcategory")
    private String subcategory;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("link")
    private Link link;	
    @JsonProperty("secure")
    private Boolean secure;
    @JsonProperty("newBadgeVisible")
    private boolean newBadgeVisible;
    @JsonProperty("partnerBadgeVisible")
	private boolean partnerBadgeVisible;
    @JsonProperty("productType")
	private String productType;	
    @JsonProperty("companyName")
	private String companyName;	
    @JsonProperty("productGridDescription")
    private String productGridDescription;


    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("subcategory")
    public String getSubcategory() {
        return subcategory;
    }

    @JsonProperty("subcategory")
    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
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
	
	@JsonProperty("secure")
    public Boolean getSecure() {
        return secure;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("subcategory", subcategory).append("image", image).append("link", link).toString();
    }
	
	@JsonProperty("secure")
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    @JsonProperty("newBadgeVisible")
    public boolean getNewBadgeVisible() {
		return newBadgeVisible;
	}

    @JsonProperty("newBadgeVisible")
	public void setNewBadgeVisible(boolean newBadgeVisible) {
		this.newBadgeVisible = newBadgeVisible;
	}

    @JsonProperty("partnerBadgeVisible")
	public boolean getPartnerBadgeVisible() {
		return partnerBadgeVisible;
	}

    @JsonProperty("partnerBadgeVisible")
	public void setPartnerBadgeVisible(boolean partnerBadgeVisible) {
		this.partnerBadgeVisible = partnerBadgeVisible;
	}
	
    @JsonProperty("productType")
	public String getProductType() {
			return productType;
	}

    @JsonProperty("productType")
	public void setProductType(String productType) {
		this.productType = productType;
	}

    @JsonProperty("productGridDescription")
    public String getProductGridDescription() {
        return productGridDescription;
    }

    @JsonProperty("productGridDescription")
    public void setProductGridDescription(String productGridDescription) {
        this.productGridDescription = productGridDescription;
    }
	
    @JsonProperty("companyName")
	public String getCompanyName() {
		 return this.companyName;
	}

    @JsonProperty("companyName")
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

}
