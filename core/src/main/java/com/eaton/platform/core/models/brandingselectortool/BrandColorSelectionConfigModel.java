package com.eaton.platform.core.models.brandingselectortool;

import com.eaton.platform.core.services.CloudConfigService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BrandColorSelectionConfigModel {

    private static final String DEFAULT_PRIMARY_BRANDING_COLOR = "005EB8";
    private static final String DEFAULT_ACCENT_COLOR_ONE = "00B2A9";
    private static final String DEFAULT_ACCENT_COLOR_TWO = "ED8B00";
    private static final String DEFAULT_LINK_COLOR = "005EB8";
    private static final String DEFAULT_COLOR_IMACT_GUIDELINES_LINK = "https://eatonbrand.frontify.com/document/34#/aem-authoring-intro/authoring/microsites-branding-color-impact";
    private static final String DEFAULT_SITE_NAME = "";
    private static final String DEFAULT_TWITTER_HANDLE = "";
    private static final String DEFAULT_BRAND_FONT = "";
    public static final String CONFIG_NAME = "brandcolorselection";

    private boolean isBrandColorSelectionConfigPresent;

    @OSGiService
    private CloudConfigService cloudConfigService;

    @Inject
    private Resource resource;


    /**
     * The primaryBrandingColor.
     */
    @Inject @Default(values = DEFAULT_PRIMARY_BRANDING_COLOR)
    private String primaryBrandingColor;

    /**
     * The accentColorOne.
     */
    @Inject @Default(values = DEFAULT_ACCENT_COLOR_ONE)
    private String accentColorOne;

    /**
     * The accentColorTwo.
     */
    @Inject @Default(values = DEFAULT_ACCENT_COLOR_TWO)
    private String accentColorTwo;


    /**
     * The linkColor.
     */
    @Inject
    @Default(values = DEFAULT_LINK_COLOR)
    private String linkColor;


    /**
     * The colorImpactGuidelinesLink.
     */
    @Inject
    @Default(values = DEFAULT_COLOR_IMACT_GUIDELINES_LINK)
    private String colorImpactGuidelinesLink;

    /**
     * The siteName
     */
    @Inject
    @Default(values = DEFAULT_SITE_NAME)
    private String siteName;

    /**
     * The twitterSite
     */
    @Inject
    @Default(values = DEFAULT_TWITTER_HANDLE)
    private String twitterSite;

    /**
     * The brandFont
     */
    @Inject
    @Default(values = DEFAULT_BRAND_FONT)
    private String primaryBrandFont;

    @PostConstruct
    protected void init() {
        final Optional<BrandColorSelectionConfigModel> brandColorSelectionCloudConfigObj = cloudConfigService != null
                ? cloudConfigService.getBrandColorSelectionConfig(resource) : java.util.Optional.empty();
        if (brandColorSelectionCloudConfigObj.isPresent()) {
            isBrandColorSelectionConfigPresent=true;
            primaryBrandingColor = brandColorSelectionCloudConfigObj.get().getPrimaryBrandingColor();
            accentColorOne = brandColorSelectionCloudConfigObj.get().getAccentColorOne();
            accentColorTwo = brandColorSelectionCloudConfigObj.get().getAccentColorTwo();
            linkColor = brandColorSelectionCloudConfigObj.get().getLinkColor();
            siteName = brandColorSelectionCloudConfigObj.get().getSiteName();
            twitterSite = brandColorSelectionCloudConfigObj.get().getTwitterSite();
            primaryBrandFont = brandColorSelectionCloudConfigObj.get().getPrimaryBrandFont();
        }
    }


    /**
     * Gets the landing hero header.
     *
     * @return the landing hero header
     */
    public String getPrimaryBrandingColor() {
        return primaryBrandingColor;
    }

    /**
     * Gets the accent color one.
     *
     * @return the accent color one
     */

    public String getAccentColorOne() {
        return accentColorOne;
    }

    /**
     * Gets the accent color two.
     *
     * @return the accent color two
     */
    public String getAccentColorTwo() {
        return accentColorTwo;
    }

    /**
     * Gets the link color.
     *
     * @return the text links color
     */
    public String getLinkColor() {
        return linkColor;
    }

    /**
     * Gets the site name for SEO purposes
     *
     * @return the owner specified site name
     */
    public String getSiteName() {return siteName;}

    /**
     * Gets the Twitter handle for social platform integration
     *
     * @return the owner specified Twitter handle
     */
    public String getTwitterSite() {return twitterSite;}

    /**
     * Gets the Primary Brand font 
     *
     * @return the owner specified Primary Brand Font
     */
    public String getPrimaryBrandFont() {
        return primaryBrandFont;
    }

    /**
     * Gets the colorImpactGuidelinesLink.
     *
     * @return the color impact guidelines link
     */
    public String getColorImpactGuidelinesLink() {
        return colorImpactGuidelinesLink;
    }

    /**
     * Gets the brandColorSelectionConfigPresent.
     *
     * @return the brand color selection config present or not
     */
    public boolean isBrandColorSelectionConfigPresent() {
        return isBrandColorSelectionConfigPresent;
    }
}
