
package com.eaton.platform.core.bean.loadmore;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "mobile",
    "tablet",
    "desktop",
    "altText"
})
public class Image {

    @JsonProperty("mobile")
    private String mobile;
    @JsonProperty("tablet")
    private String tablet;
    @JsonProperty("desktop")
    private String desktop;
    @JsonProperty("altText")
    private String altText;

    @JsonProperty("mobile")
    public String getMobile() {
        return mobile;
    }

    @JsonProperty("mobile")
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonProperty("tablet")
    public String getTablet() {
        return tablet;
    }

    @JsonProperty("tablet")
    public void setTablet(String tablet) {
        this.tablet = tablet;
    }

    @JsonProperty("desktop")
    public String getDesktop() {
        return desktop;
    }

    @JsonProperty("desktop")
    public void setDesktop(String desktop) {
        this.desktop = desktop;
    }

    @JsonProperty("altText")
    public String getAltText() {
        return altText;
    }

    @JsonProperty("altText")
    public void setAltText(String altText) {
        this.altText = altText;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("mobile", mobile).append("tablet", tablet).append("desktop", desktop).append("altText", altText).toString();
    }

}
