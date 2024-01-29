package com.eaton.platform.core.featureflags;

import java.util.Objects;

/**
 * POJO for the data of feature flags
 */
public class FeatureFlagPojo {
    private final String propertyName;
    private final String propertyDescription;

    private String tooltipText;
    /**
     * Constructor taking as parameters:
     * @param propertyName to set
     * @param propertyDescription to set
     */
    public FeatureFlagPojo(String propertyName, String propertyDescription) {
        this.propertyName = propertyName;
        this.propertyDescription = propertyDescription;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    /**
     * Factory method with
     * @param tooltipText to set
     * @return this
     */
    public FeatureFlagPojo withTooltipText(String tooltipText) {
        this.tooltipText = tooltipText;
        return this;
    }

    public String getTooltipText() {
        return tooltipText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FeatureFlagPojo that = (FeatureFlagPojo) o;
        return Objects.equals(propertyName, that.propertyName) && Objects.equals(propertyDescription, that.propertyDescription) && Objects.equals(tooltipText, that.tooltipText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyName, propertyDescription, tooltipText);
    }
}
