package com.eaton.platform.core.models.brandingselectortool;

import com.eaton.platform.core.services.CloudConfigService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BrandFontSelectionConfigModel {

    public static final String CONFIG_NAME = "brandfontselection";
    private static final String DEFAULT_PRIMARY_BRAND_FONT = "Eaton";
    private static final String DEFAULT_PRIMARY_BRAND_FONT_MEDIUM_ = "EatonMedium";
    private static final String DEFAULT_PRIMARY_BRAND_FONT_BOLD_ = "EatonBold";
    private static final String DEFAULT_BODY_FONT = "Arial";
    private static final String DEFAULT_FALLBACK_FONT = "Arial";
    private static final String DEFAULT_FONT_IMPACT_GUIDELINES_LINK = "https://eatonbrand.frontify.com/document/34#/aem-authoring-intro/authoring/microsites-banding-font-impact";

    @OSGiService
    private CloudConfigService cloudConfigService;

    @Inject
    private Resource resource;

    /**
     * The primaryBrandFont.
     */
    @Inject
    @Default(values = DEFAULT_PRIMARY_BRAND_FONT)
    private String primaryBrandFont;

    /**
     * The primaryBrandFontFallbackFont.
     */
    @Inject
    @Default(values = DEFAULT_FALLBACK_FONT)
    private String primaryBrandFontFallbackFont;

    /**
     * The primaryBrandFontMedium.
     */
    @Inject
    @Default(values = DEFAULT_PRIMARY_BRAND_FONT_MEDIUM_)
    private String primaryBrandFontMedium;

    /**
     * The primaryBrandFontMediumFallbackFont.
     */
    @Inject
    @Default(values = DEFAULT_FALLBACK_FONT)
    private String primaryBrandFontMediumFallbackFont;

    /**
     * The primaryBrandFontBold.
     */
    @Inject
    @Default(values = DEFAULT_PRIMARY_BRAND_FONT_BOLD_)
    private String primaryBrandFontBold;

    /**
     * The primaryBrandFontBoldFallbackFont.
     */
    @Inject
    @Default(values = DEFAULT_FALLBACK_FONT)
    private String primaryBrandFontBoldFallbackFont;

    /**
     * The bodyFont.
     */
    @Inject
    @Default(values = DEFAULT_BODY_FONT)
    private String bodyFont;

    /**
     * The bodyFontFallbackFont.
     */
    @Inject
    @Default(values = DEFAULT_FALLBACK_FONT)
    private String bodyFontFallbackFont;

    /**
     * The fontImpactGuidelinesLink.
     */
    @Inject
    @Default(values = DEFAULT_FONT_IMPACT_GUIDELINES_LINK)
    private String fontImpactGuidelinesLink;


    /**
     * Gets the primaryBrandFont.
     *
     * @return the primary brand font
     */
    public String getPrimaryBrandFont() {
        return primaryBrandFont;
    }

    /**
     * Gets the primaryBrandFontFallbackFont.
     *
     * @return the primary brand font fall back font
     */
    public String getPrimaryBrandFontFallbackFont() {
        return primaryBrandFontFallbackFont;
    }

    /**
     * Gets the primaryBrandFontMedium.
     *
     * @return the primary brand font medium
     */
    public String getPrimaryBrandFontMedium() {
        return primaryBrandFontMedium;
    }

    /**
     * Gets the primaryBrandFontMediumFallbackFont.
     *
     * @return the primary brand font medium fall back font
     */
    public String getPrimaryBrandFontMediumFallbackFont() {
        return primaryBrandFontMediumFallbackFont;
    }

    /**
     * Gets the primaryBrandFontBold.
     *
     * @return the primary brand font bold
     */
    public String getPrimaryBrandFontBold() {
        return primaryBrandFontBold;
    }

    /**
     * Gets the primaryBrandFontBoldFallbackFont.
     *
     * @return the primary brand font bold fall back font
     */
    public String getPrimaryBrandFontBoldFallbackFont() {
        return primaryBrandFontBoldFallbackFont;
    }

    /**
     * Gets the bodyFont.
     *
     * @return the body font
     */
    public String getBodyFont() {
        return bodyFont;
    }

    /**
     * Gets the bodyFontFallbackFont.
     *
     * @return the body font fall back font
     */
    public String getBodyFontFallbackFont() {
        return bodyFontFallbackFont;
    }

    /**
     * Gets the fontImpactGuidelinesLink.
     *
     * @return the font impact guidelines document link
     */
    public String getFontImpactGuidelinesLink() {
        return fontImpactGuidelinesLink;
    }
}
