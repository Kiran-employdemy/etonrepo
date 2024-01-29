package com.eaton.platform.integration.endeca.util;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.secure.AdvancedSearchFacetWhiteListBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.EndecaFacetTag;
import com.eaton.platform.core.search.api.FacetConfiguration;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.MyEatonConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Utility Class for Endeca Service
 * @author 308059
 *
 */
public class EndecaUtil {

	private static final Logger LOG = LoggerFactory.getLogger(EndecaUtil.class);

	/**
	 * Empty Constructor
	 */
	public EndecaUtil() {
	    throw new IllegalStateException("Utility class");
	  }

	/**
	 * return value
	 * @param str String Value
	 * @return boolean flag
	 */
	public static boolean isBlankOrNull(String str) {
	    return (str == null || "".equals(str.trim()));
	}

	/**
	 *  return value
	 * @param list collection
	 * @param <T>
	 * @return boolean flag
	 */
	public static <T> boolean isNullOrEmpty(Collection<T> list) {
	    return list == null || list.isEmpty();
	}


	/**
	 * Gets Endeca compatible tad id.
	 * @param tm Tag Manager
	 * @param tagid AEM tag
	 * @return endeca tag id
	 */
	public static String getEndecaFacetTag(TagManager tm, String tagid ){
		Tag facetTag = tm.resolve(tagid);
		if(facetTag != null){
			EndecaFacetTag endecaFacetTag = facetTag.adaptTo(EndecaFacetTag.class);
			if(endecaFacetTag != null) {
				return endecaFacetTag.getFacetId();
			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Updates the facet display flag properties from Component Dialog
	 * @param facetObj Facet Object
	 * @param advancedSearchFacetWhiteListBeans AdvancedSearchFacetBean
	 */
	public static void updateFacetFieldTypeFromComponentConfig(JSONObject facetObj, List<FacetConfiguration> advancedSearchFacetWhiteListBeans){
		LOG.debug("Endeca Util: updateFacetFieldTypePropertiesFromComponent() :: Started");
		for(FacetConfiguration advancedSearchFacetWhiteListBean : advancedSearchFacetWhiteListBeans){
			String showGrid = StringUtils.EMPTY;
			String searchEnabled = EndecaConstants.HIDE;
			String singleFacetOrMultiple =  EndecaConstants.CHECKBOX;
			try {
				String facet = advancedSearchFacetWhiteListBean.getFacet();
				if(facet.equalsIgnoreCase(facetObj.getString(EndecaConstants.ENDECA_ADV_FACET_GROUP_ID))) {
					if (advancedSearchFacetWhiteListBean.getShowAsGrid() != null) {
						showGrid = advancedSearchFacetWhiteListBean.getShowAsGrid();
					}
					if (advancedSearchFacetWhiteListBean.getFacetSearchEnabled() != null) {
						searchEnabled = advancedSearchFacetWhiteListBean.getFacetSearchEnabled();
					}
					if (advancedSearchFacetWhiteListBean.getSingleFacetEnabled() != null) {
						singleFacetOrMultiple = advancedSearchFacetWhiteListBean.getSingleFacetEnabled();
					}
					facetObj.put(EndecaConstants.ENDECA_ADV_FACET_GRID_FACET, showGrid);
					facetObj.put(EndecaConstants.ENDECA_ADV_FACET_SEARCH_ENABLED, searchEnabled);
					facetObj.put(EndecaConstants.ENDECA_ADV_FACET_SINGLE_FACET, singleFacetOrMultiple);
				}

			} catch (JSONException e) {
				LOG.error("****** Exception while getting facet group value ***", e);
			}
		}
		LOG.debug("Endeca Util: updateFacetFieldTypePropertiesFromComponent() :: END");
	}

	/**
	 * Updates the facet display flag properties from Site Config
	 * @param facetObj Facet Object
	 * @param updatedFacetGroupList facetGroupBean
	 */
	public static void updateFacetFieldTypeFromSiteConfig(JSONObject facetObj, List<FacetGroupBean> updatedFacetGroupList){

		for(FacetGroupBean facetGroupBean : updatedFacetGroupList){
			String showGrid = StringUtils.EMPTY;
			String searchEnabled = EndecaConstants.HIDE;
			String singleFacetOrMultiple =  EndecaConstants.CHECKBOX;
			try {
				if(facetGroupBean.getFacetGroupId().equalsIgnoreCase(facetObj.getString(EndecaConstants.ENDECA_ADV_FACET_GROUP_ID))) {
					if (facetGroupBean.isGridFacet()) {
						showGrid = CommonConstants.VIEW_TYPE_GRID;
					}
					if (facetGroupBean.isFacetSearchEnabled()) {
						searchEnabled =  CommonConstants.DISPLAY_SHOW;
					}
					if (facetGroupBean.isSingleFacetEnabled()) {
						singleFacetOrMultiple =  CommonConstants.FIELD_TYPE_RADIO;
					}
					facetObj.put(EndecaConstants.ENDECA_ADV_FACET_GRID_FACET, showGrid);
					facetObj.put(EndecaConstants.ENDECA_ADV_FACET_SEARCH_ENABLED, searchEnabled);
					facetObj.put(EndecaConstants.ENDECA_ADV_FACET_SINGLE_FACET, singleFacetOrMultiple);
				}

			} catch (JSONException e) {
				LOG.error("****** Exception while getting facet group value ***", e);
			}
		}
	}

	public static void addSecureFacetToReturnFacets(List<AdvancedSearchFacetWhiteListBean> advancedSearchFacetWhiteListBeans) {
		AdvancedSearchFacetWhiteListBean facetModel = new AdvancedSearchFacetWhiteListBean();
		facetModel.setFacet(EndecaConstants.EATON_SECURE_FACET_GROUP_LABEL);
		facetModel.setShowAsGrid(StringUtils.EMPTY);
		facetModel.setFacetSearchEnabled(EndecaConstants.HIDE);
		facetModel.setSingleFacetEnabled( EndecaConstants.CHECKBOX);
		advancedSearchFacetWhiteListBeans.add(facetModel);

	}

	/**
	 * It builds up a mockup for using My Eaton Sec Filters in author/local environment
	 * @param filters List<FilterBean> Object
	 * @param myEatonConfig MyEatonConfig object
	 */
	public static List<FilterBean> getSampleSecFilter(List<FilterBean> filters, MyEatonConfig myEatonConfig){
		FilterBean filterBean = new FilterBean();

		filterBean.setFilterName(myEatonConfig.sec_account_type_name());
		filterBean.setFilterValues(Arrays.asList(myEatonConfig.sec_account_type_values()));
		filters.add(filterBean);

		filterBean = new FilterBean();
		filterBean.setFilterName(myEatonConfig.sec_application_access_name());
		filterBean.setFilterValues(Arrays.asList(myEatonConfig.sec_application_access_values()));
		filters.add(filterBean);

		filterBean = new FilterBean();
		filterBean.setFilterName(myEatonConfig.sec_company_name());
		filterBean.setFilterValues(Arrays.asList(myEatonConfig.sec_company_values()));
		filters.add(filterBean);

		filterBean = new FilterBean();
		filterBean.setFilterName(myEatonConfig.sec_product_categories_name());
		filterBean.setFilterValues(Arrays.asList(myEatonConfig.sec_product_categories_values()));
		filters.add(filterBean);

		filterBean = new FilterBean();
		filterBean.setFilterName(myEatonConfig.sec_country_name());
		filterBean.setFilterValues(Arrays.asList(myEatonConfig.sec_country_values()));
		filters.add(filterBean);

		filterBean = new FilterBean();
		filterBean.setFilterName(myEatonConfig.sec_tier_level_name());
		filterBean.setFilterValues(Arrays.asList(myEatonConfig.sec_tier_level_values()));
		filters.add(filterBean);

		filterBean = new FilterBean();
		filterBean.setFilterName(myEatonConfig.sec_partner_programme_type_name());
		filterBean.setFilterValues(Arrays.asList(myEatonConfig.sec_partner_programme_type_values()));
		filters.add(filterBean);

		return filters;
	}

	/**
	 * This method resets language & Country filters if the page type is 'global-product-sku-page'
	 * @param endecaRequestBean
	 * @param page
	 */
	public static void resetLanguageAndCountryFilters(EndecaServiceRequestBean endecaRequestBean, Page page){
		LOG.debug("Endeca Util: resetLanguageAndCountryFilters() :: Started");
		final ValueMap valueMap = page.getContentResource().getValueMap();
		if (valueMap != null){
			final String pageType = valueMap.get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
			LOG.debug("Page Type : {}", pageType);
		    /* EAT-8942 - Reset(##IGNORE##) Language & country filter when the page type is 'global-prodcut-sku'
				 and for other page types takes the default settings  */
			if(CommonConstants.PAGE_TYPE_GLOBAL_PRODUCT_SKU_PAGE.equals(pageType) ){
				endecaRequestBean.setLanguage(EndecaConstants.SEARCH_TERM_IGNORE);
				for(FilterBean filterBean : endecaRequestBean.getFilters()){
					if(filterBean.getFilterName().equals(EndecaConstants.COUNTRY_CONSTANT)){
						filterBean.setFilterValues(Arrays.asList(EndecaConstants.SEARCH_TERM_IGNORE));
					}
				}
			}
		}
		LOG.debug("Endeca Util: resetLanguageAndCountryFilters() :: END");

	}
}
