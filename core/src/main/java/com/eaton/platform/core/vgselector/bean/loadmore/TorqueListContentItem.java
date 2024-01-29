package com.eaton.platform.core.vgselector.bean.loadmore;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaton.platform.core.bean.loadmore.Image;
import com.eaton.platform.core.bean.loadmore.Link;
import com.eaton.platform.core.bean.loadmore.ProductAttribute;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "name",
    "description",
    "image",
    "link",
    "productAttributes"
})
public class TorqueListContentItem {
	
	 	@JsonProperty("name")
	    private String name;
	    @JsonProperty("description")
	    private String description;
	    @JsonProperty("image")
	    private Image image;
	    @JsonProperty("link")
	    private Link link;
	    @JsonProperty("productAttributes")
	    private List<ProductAttribute> productAttributes = null;

	    @JsonProperty("name")
	    public String getName() {
	        return name;
	    }

	    @JsonProperty("name")
	    public void setName(String name) {
	        this.name = name;
	    }

	    @JsonProperty("description")
	    public String getDescription() {
	        return description;
	    }

	    @JsonProperty("description")
	    public void setDescription(String description) {
	        this.description = description;
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
	        return new ToStringBuilder(this).append("name", name).append("description", description).append("image", image).append("link", link).append("productAttributes", productAttributes).toString();
	    }

}
