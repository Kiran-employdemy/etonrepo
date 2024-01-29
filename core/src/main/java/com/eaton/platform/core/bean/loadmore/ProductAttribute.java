
package com.eaton.platform.core.bean.loadmore;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "productAttributeLabel",
    "productAttributeValue"
})
public class ProductAttribute {

    @JsonProperty("productAttributeLabel")
    private String productAttributeLabel;
    @JsonProperty("productAttributeValue")
    private String productAttributeValue;

    @JsonProperty("productAttributeLabel")
    public String getProductAttributeLabel() {
        return productAttributeLabel;
    }

    @JsonProperty("productAttributeLabel")
    public void setProductAttributeLabel(String productAttributeLabel) {
        this.productAttributeLabel = productAttributeLabel;
    }

    @JsonProperty("productAttributeValue")
    public String getProductAttributeValue() {
        return productAttributeValue;
    }

    @JsonProperty("productAttributeValue")
    public void setProductAttributeValue(String productAttributeValue) {
        this.productAttributeValue = productAttributeValue;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("productAttributeLabel", productAttributeLabel).append("productAttributeValue", productAttributeValue).toString();
    }

}
