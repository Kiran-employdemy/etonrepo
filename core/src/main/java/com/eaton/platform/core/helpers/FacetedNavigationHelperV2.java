package com.eaton.platform.core.helpers;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.FacetURLBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FacetedNavigationHelperV2 {
	private static final Logger LOG = LoggerFactory.getLogger(FacetedNavigationHelperV2.class);
	private static final String FACET_GROUP_NAME = "name";
	private static final String FACET_GROUP_TITLE = "title";
	private static final String FACET_GROUP_VALUES = "values";
	private static final String FACET_VALUE_NAME = "name";
	private static final String FACET_VALUE_CHECKED = "checked";
	private static final String FACET_VALUE_TITLE = "title";
	private static final String FACET_VALUE_COUNT = "count";
	private static final String FACET_VALUE_ID = "id";
	private static final String FACET_VALUE_VALUE = "value";
	private static final String FACET_VALUE_URL = "url";
	private static final String FACET_VALUE_ACTIVE = "active";
	private static final String FACET_GROUP_HIDE_CLEAR_LINK = "hideClearLink";
	private static final String EMPTY_JSON_ARRAY = "[]";
	private static final String ACTIVE_RADIO_BUTTON_CHECKED = "checked";
	public static final String FACET_GROUP_SHOW_AS_GRID = "showAsGrid";
	public static final String FACET_GROUP_FACET_SEARCH_ENABLED = "facetSearchEnabled";
	public static final String SINGLE_FACET_ENABLED = "singleFacetEnabled";
	private static final String PRODUCTTYPE_CONSTANT=".productType$";
	private static final String FACETS=".facets";
	private static String defaultActivefacetGroupId;
	/**
	 * @return a JSON representation of the facet group list compatible with the
	 *         App.Filters UI Component.
	 */
	public JSONArray getFacetGroupListJsonArray(Page currentPage,List<FacetGroupBean> facetGroupList,List<FacetBean> activeFacetsList,FacetURLBean facetURLBean,String currentSelectedTab, boolean addSelectorToIgnoreCache) {
	LOG.debug("FacetedNavigationHelperV2 : This is Entry getFacetGroupListJsonArray() method");
		JSONArray facetsJsonArray = new JSONArray();
		defaultActivefacetGroupId = null;
		if ( facetURLBean.getFacetStartURL() != null && facetURLBean.getFacetStartURL().equalsIgnoreCase(FACETS)) {
			if(facetGroupList!=null) {
				for (FacetGroupBean facetGroupBean : facetGroupList) {
					List<FacetValueBean> facetValueBeanList = facetGroupBean.getFacetValueList();
					for (FacetValueBean facetValueBean : facetValueBeanList) {
						boolean activeSelection = (null != facetValueBean.getActiveRadioButton())
								? (facetValueBean.getActiveRadioButton().equals(ACTIVE_RADIO_BUTTON_CHECKED)
								|| Boolean.parseBoolean(facetValueBean.getActiveRadioButton()))
								: Boolean.FALSE;
						if (activeSelection) {
							defaultActivefacetGroupId = facetValueBean.getFacetValueId();
							break;
						}
					}
				}
			}
		}
		if (facetGroupList != null) {
			String unCachedSelector = addSelectorToIgnoreCache ?
					CommonConstants.UN_CACHED_SELECTOR : StringUtils.EMPTY;
			facetGroupList.stream().map(facetGroup -> {
				JSONObject facetGroupJsonObject = new JSONObject();

				String facetGroupId = facetGroup.getFacetGroupId() != null ? facetGroup.getFacetGroupId()
						: CommonConstants.PRODUCT_TYPE_FACET_NAME;
				boolean isSingleFacetEnabled = facetGroupId.equals(CommonConstants.PRODUCT_TYPE_FACET_NAME) ? true
						: facetGroup.isSingleFacetEnabled();
				facetGroupJsonObject.put(FACET_GROUP_NAME, facetGroupId);
				facetGroupJsonObject.put(FACET_GROUP_TITLE, facetGroup.getFacetGroupLabel());
				facetGroupJsonObject.put(FACET_GROUP_SHOW_AS_GRID, facetGroup.isGridFacet());
				facetGroupJsonObject.put(FACET_GROUP_FACET_SEARCH_ENABLED, facetGroup.isFacetSearchEnabled());
				facetGroupJsonObject.put(SINGLE_FACET_ENABLED, isSingleFacetEnabled);

				JSONArray facetValuesJsonArray = new JSONArray();
				facetGroup.getFacetValueList().stream().map(facetValueBean -> {
					JSONObject facetValueJsonObject = new JSONObject();
					final Optional<FacetBean> activeFacet = activeFacetsList.stream()
							.filter(activeFacetToFilter -> facetValueBean.getFacetValueId()
									.contains(activeFacetToFilter.getFacetID()))
							.findFirst();
					boolean active = activeFacet.isPresent();
					String facetUrl;
					final String contentResourcePath;
					boolean activeSelection = null != facetValueBean.getActiveRadioButton()
							? (facetValueBean.getActiveRadioButton().equals(ACTIVE_RADIO_BUTTON_CHECKED)
									|| Boolean.parseBoolean(facetValueBean.getActiveRadioButton()))
							: Boolean.FALSE;
					if (facetGroup.getFacetGroupId() == null) {
						// This is the Product_Type scenario.
					    if((facetValueBean.getFacetStartURL()==null) && (currentPage!=null && currentPage.getPath()!=null)){
 					       contentResourcePath= currentPage.getPath() + PRODUCTTYPE_CONSTANT;
                     	   facetURLBean.setExtensionIdStartURL(contentResourcePath);
                     	}
                        if(facetURLBean.getTabEndURL()!=null){
                 	       facetUrl = facetURLBean.getExtensionIdStartURL() + facetValueBean.getFacetValueId() + unCachedSelector +facetURLBean.getTabEndURL();
                  	    }else{
                 	       facetUrl = facetURLBean.getExtensionIdStartURL() + facetValueBean.getFacetValueId() + unCachedSelector + facetURLBean.getExtensionIdEndURL();
                 	    }
						activeSelection = FACET_VALUE_CHECKED.equals(facetValueBean.getActiveRadioButton());
						facetGroupJsonObject.put(FACET_GROUP_HIDE_CLEAR_LINK, true);
					} else if (EndecaConstants.CONTENT_TYPE_FACET.equals(facetGroup.getFacetGroupId())) {
						// This is the Content_Type scenario.
						String startUrl = facetURLBean.getFacetStartURL();
						if (facetGroup.isSingleFacetEnabled()) {
							for (String id : facetGroup.getFacetValueList().stream()
									.map(facetValue -> facetValue.getFacetValueId()).collect(Collectors.toList())) {
								startUrl = startUrl.replace(CommonConstants.FACET_SEPERATOR + id, StringUtils.EMPTY);
							}
						}
						if(FACETS.equalsIgnoreCase(startUrl)) {
							facetUrl = facetURLBean.getContentPath() + startUrl + CommonConstants.FACET_SEPERATOR + facetValueBean.getFacetValueId()
						+ unCachedSelector + facetURLBean.getFacetEndURL();
						}else {
							facetUrl = facetURLBean.getContentPath() + FACETS + CommonConstants.FACET_SEPERATOR + facetValueBean.getFacetValueId()
							+ unCachedSelector + facetURLBean.getFacetEndURL();
						}
						activeSelection = ACTIVE_RADIO_BUTTON_CHECKED.equals(facetValueBean.getActiveRadioButton());
						if(active && activeSelection) {
								facetUrl = activeFacet.get().getFacetDeselectURL();
						}
						
					} else if (active && activeFacet.isPresent()) {
						// This is the active facet scenario
						facetUrl = activeFacet.get().getFacetDeselectURL();
					} else {
						// This is the non active facet scenario.
						String startUrl = facetURLBean.getFacetStartURL();
						if (facetGroup.isSingleFacetEnabled()) {
							for (String id : facetGroup.getFacetValueList().stream()
									.map(facetValue -> facetValue.getFacetValueId()).collect(Collectors.toList())) {
								startUrl = startUrl.replace(CommonConstants.FACET_SEPERATOR + id, StringUtils.EMPTY);
							}
						}
						if(FACETS.equalsIgnoreCase(startUrl) && defaultActivefacetGroupId  != null && !isSingleFacetEnabled) {
							startUrl = startUrl + CommonConstants.FACET_SEPERATOR + defaultActivefacetGroupId;
						}
						facetUrl = facetURLBean.getContentPath() + startUrl + CommonConstants.FACET_SEPERATOR + facetValueBean.getFacetValueId()
						+ unCachedSelector + facetURLBean.getFacetEndURL();
					}

					facetValueJsonObject.put(FACET_VALUE_NAME, facetValueBean.getFacetValueId());
					facetValueJsonObject.put(FACET_VALUE_TITLE, facetValueBean.getFacetValueLabel());
					facetValueJsonObject.put(FACET_VALUE_COUNT, facetValueBean.getFacetValueDocs());
					facetValueJsonObject.put(FACET_VALUE_ID, facetUrl);
					facetValueJsonObject.put(FACET_VALUE_VALUE, facetUrl);
					facetValueJsonObject.put(FACET_VALUE_URL, facetUrl);
					facetValueJsonObject.put(FACET_VALUE_ACTIVE, activeSelection);
					return facetValueJsonObject;
				}).forEach(jsonObject -> facetValuesJsonArray.add(jsonObject));
				facetGroupJsonObject.put(FACET_GROUP_VALUES, facetValuesJsonArray);
				return facetGroupJsonObject;
			}).forEach(jsonObject -> facetsJsonArray.add(jsonObject));
		}
		LOG.debug("FacetedNavigationHelperV2 : This is Exit from getFacetGroupListJsonArray() method");
		return facetsJsonArray;
	}

	/**
	 * @return a JSON String of the ordering of facet groups compatible with the
	 *         App.Filters UI Component.
	 */
	public String getFacetGroupOrderingJson(Page currentPage,List<FacetGroupBean> facetGroupList,List<FacetBean> activeFacetsList,FacetURLBean facetURLBean,String currentSelectedTab, boolean addSelectorToIgnoreCache) {
	LOG.debug("FacetedNavigationHelperV2 : This is Entry getFacetGroupOrderingJson() method");
		JSONArray orderingArray = new JSONArray();

		this.getFacetGroupListJsonArray(currentPage,facetGroupList,activeFacetsList,facetURLBean,currentSelectedTab,addSelectorToIgnoreCache).stream().forEach(facetGroup -> {
			JSONObject orderingObject = new JSONObject();
			orderingObject.put(FACET_GROUP_NAME, ((JSONObject) facetGroup).get(FACET_GROUP_NAME));
			orderingObject.put(FACET_GROUP_FACET_SEARCH_ENABLED,
					((JSONObject) facetGroup).getOrDefault(FACET_GROUP_FACET_SEARCH_ENABLED, false));
			orderingObject.put(FACET_GROUP_SHOW_AS_GRID,
					((JSONObject) facetGroup).getOrDefault(FACET_GROUP_SHOW_AS_GRID, false));
			orderingObject.put(SINGLE_FACET_ENABLED,
					((JSONObject) facetGroup).getOrDefault(SINGLE_FACET_ENABLED, false));
			orderingArray.add(orderingObject);
		});
		try {
			return URIUtil.encodeAll(orderingArray.toJSONString(), CommonConstants.UTF_8);
		} catch (URIException e) {
			LOG.error("Could not uri encode facet group ordering json", e);
			return EMPTY_JSON_ARRAY;
		}
	}

	/**
	 * @return a JSON String of the ordering of facet groups compatible with the
	 *         App.Filters UI Component.
	 */
	public String getFacetGroupListJson(Page currentPage,List<FacetGroupBean> facetGroupList,List<FacetBean> activeFacetsList,FacetURLBean facetURLBean,String currentSelectedTab, boolean addSelectorToIgnoreCache) {
		try {
			return URIUtil.encodeAll(this.getFacetGroupListJsonArray(currentPage,facetGroupList,activeFacetsList,facetURLBean,currentSelectedTab,addSelectorToIgnoreCache).toJSONString(), CommonConstants.UTF_8);
		} catch (URIException e) {
			LOG.error("Could not uri encode facet group list json", e);
			return EMPTY_JSON_ARRAY;
		}
	}

	/**
	 * @return a JSON String of the ordering of facet groups compatible with the
	 *         App.ActiveFilters UI Component.
	 */
	public String getActiveFacetsListJson(List<FacetBean> activeFacetsList) {
		LOG.debug("FacetedNavigationHelperV2 : This is Entry getActiveFacetsListJson() method");
		JSONArray activeFacetsJson = new JSONArray();
		if (activeFacetsList != null) {
			activeFacetsList.stream().map(facetBean -> {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(FACET_VALUE_NAME, facetBean.getLabel());
				jsonObject.put(FACET_VALUE_VALUE, facetBean.getFacetDeselectURL());
				jsonObject.put(FACET_VALUE_TITLE, facetBean.getLabel());
				jsonObject.put(FACET_VALUE_ID, facetBean.getFacetID());

				return jsonObject;
			}).forEach(jsonObject -> activeFacetsJson.add(jsonObject));
		}
		try {
			return URIUtil.encodeAll(activeFacetsJson.toJSONString(), CommonConstants.UTF_8);
		} catch (URIException e) {
			LOG.error("Could not uri encode active facets list json", e);
			return EMPTY_JSON_ARRAY;
		}
	}
}
