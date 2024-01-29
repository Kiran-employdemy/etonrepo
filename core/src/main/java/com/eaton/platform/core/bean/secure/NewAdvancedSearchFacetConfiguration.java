package com.eaton.platform.core.bean.secure;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.search.api.FacetConfiguration;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

/**
 * This Model is for mapping facet configuration data.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewAdvancedSearchFacetConfiguration implements FacetConfiguration {

    private static final String GRID_CSS_CLASS = "faceted-navigation__facet-group--grid";

    @Inject
    private String facet;

    @Inject
    @Default(values = "")
    private String showAsGrid;

    @Inject
    @Default(values = "hide")
    private String facetSearchEnabled;

    @Inject
    @Default(values = "checkbox")
    private String singleFacetEnabled;

    /**
     * Factory method taking as
     *
     * @param facetGroupBean to create the FacetConfiguration for
     * @return the created FacetConfiguration
     */
    public static NewAdvancedSearchFacetConfiguration of(FacetGroupBean facetGroupBean) {
        NewAdvancedSearchFacetConfiguration advancedSearchFacetConfiguration = new NewAdvancedSearchFacetConfiguration();
        advancedSearchFacetConfiguration.facet = facetGroupBean.getFacetGroupId();
        advancedSearchFacetConfiguration.showAsGrid = determineShowAsGrid(facetGroupBean);
        advancedSearchFacetConfiguration.facetSearchEnabled = determineFacetSearchEnabled(facetGroupBean);
        advancedSearchFacetConfiguration.singleFacetEnabled = determineSingleFacetEnabled(facetGroupBean);
        return advancedSearchFacetConfiguration;
    }

    private static String determineSingleFacetEnabled(FacetGroupBean facetGroupBean) {
        if (facetGroupBean.isSingleFacetEnabled()) {
            return CommonConstants.FIELD_TYPE_RADIO;
        }
        return EndecaConstants.CHECKBOX;
    }

    private static String determineFacetSearchEnabled(FacetGroupBean facetGroupBean) {
        if (facetGroupBean.isFacetSearchEnabled()) {
            return CommonConstants.DISPLAY_SHOW;
        }
        return EndecaConstants.HIDE;
    }

    private static String determineShowAsGrid(FacetGroupBean facetGroupBean) {
        if (facetGroupBean.isGridFacet()) {
            return GRID_CSS_CLASS;
        }
        return StringUtils.EMPTY;
    }

    /**
     * Factory method for
     *
     * @return the default FacetConfiguration
     */
    public static FacetConfiguration defaultConfiguration() {
        NewAdvancedSearchFacetConfiguration advancedSearchFacetConfiguration = new NewAdvancedSearchFacetConfiguration();
        advancedSearchFacetConfiguration.singleFacetEnabled = EndecaConstants.CHECKBOX;
        advancedSearchFacetConfiguration.facetSearchEnabled = EndecaConstants.HIDE;
        advancedSearchFacetConfiguration.showAsGrid = StringUtils.EMPTY;
        return advancedSearchFacetConfiguration;
    }


    @Override
    public String getFacet() {
        return facet.substring(facet.indexOf(':') + 1).replace('/', '_');
    }

    @Override
    public String getShowAsGrid() {
        return showAsGrid;
    }

    @Override
    public String getFacetSearchEnabled() {
        return facetSearchEnabled;
    }

    @Override
    public String getSingleFacetEnabled() {
        return singleFacetEnabled;
    }

}
