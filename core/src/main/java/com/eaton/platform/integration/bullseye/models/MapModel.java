package com.eaton.platform.integration.bullseye.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.injectors.annotations.CurrentPage;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.bullseye.bean.CategoryGroupBean;
import com.eaton.platform.integration.bullseye.constants.BullseyeConstant;
import com.eaton.platform.integration.bullseye.services.BullseyeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.EatonSiteConfigService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * MapModel
 * This is a Sling Model for Map.
 *
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MapModel {
    public static final String VALUE = "value";
    private static final Logger LOGGER = LoggerFactory.getLogger(MapModel.class);
    private static final String TITLE = "title";
    private static final String NAME = "name";
    private static final String VALUES = "values";
    private static final String DISTANCE = "distance";
    private static final String DISTANCE_I18N = "Map.Distance";
    private static final String RESPONSE = "response";
    private static final String ID = "id";
    private static final String COUNT = "count";
    private static final String SINGLE_FACET_ENABLED = "singleFacetEnabled";
    private static final String DISTANCE_UNITS = "distanceUnits";
    private static final String DISTANCE_UNIT = "distanceunit";
    private static final String RADIUS_UNIT = "radiusunit";
    private static final String DISTANCE_UNITS_I18N = "Map.DistanceUnits";
    private static final String MILES = "miles";
    private static final String KILOMETERS = "kilometers";
    private static final String RADIUS = "radius";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String ACTIVE = "active";

    private static final int NUMBER_TWO = 2;
    private static final int NUMBER_ONE = 1;

    private final JSONArray filtersArray = new JSONArray();
    private final JSONArray csvColumnsArray = new JSONArray();
    private final JSONArray facetGroupOrderingJson = new JSONArray();

    private String mappingVendor;
    private String mappingApiKey;
    private String pageSize;
    private String [] prefilters;

    @OSGiService
    private CloudConfigService cloudConfigService;

    @OSGiService
    private BullseyeService bullseyeService;

    @CurrentPage
    private Page currentPage;

    @Inject
    @Via("resource")
    private String [] category;

    @Inject
    @Via("resource")
    private String [] csvCategoryColumns;

    @Inject
    @Via("resource")
    private String labelCategory;

    @Inject @Default(values = "0")
    @Via("resource")
    private String defaultLatitude;

    @Inject @Default(values = "0")
    @Via("resource")
    private String defaultLongitude;

    @Inject @Default(values = "kilometers")
    @Via("resource")
    private String defaultDistanceUnit;

    @Inject @Default(values = "100")
    @Via("resource")
    private String defaultRadius;

    @Inject
    @Via("resource")
    private String enableExpander;

    @Inject
    @Via("resource")
    private String enableLocationType;

    @Inject
    @Via("resource")
    private String expanderTitle;

    @Inject
    @Via("resource")
    private String searchType;

    @Inject
    @Via("resource")
    private String hideDownload;

    @Inject
    @Via("resource")
    private String hideMap;

    @Inject
    @Via("resource")
    private String hideEmail;

    @Inject
    @Via("resource")
    private String hideDirection;

    @Inject
    @Via("resource")
    private String hideWebsite;

    @Inject
    private SlingHttpServletRequest slingHttpServletRequest;

	@Inject
   	protected EatonSiteConfigService eatonSiteConfigService;

    /**Facet value count from site config*/
   	private int facetValueCount;

    @Inject
    @Via("resource")
    private static List<CategoryGroupModel> categoryGroupList;
    
    @Inject
    @Via("resource")
    private List<PreSeltCategoryGroupModel> preSeltCategoryGroupList;

    @Inject
    @Via("resource")
    private static List<CSVCategoryGroupModel> csvCategoryGroupList;
    
    public String preSeltCategoryList = "";

    @PostConstruct
    void init() {
        LOGGER.debug("MapModel :: init() :: Started");
        try {
            RequestParameterMap urlParams = slingHttpServletRequest.getRequestParameterMap();
            String[] radValues = {"5", "10", "25", "50", "100"};
            String[] radUnitValues = {MILES, KILOMETERS};
            List<String> radList = Arrays.asList(radValues);
            List<String> radUnitList = Arrays.asList(radUnitValues);
            final Locale language = CommonUtil.getLocaleFromPagePath(currentPage);

            if(urlParams.size() > 0){
                for(String param: urlParams.keySet()){
                    String value = Objects.requireNonNull(urlParams.getValue(param)).getString();

                    if((RADIUS.equalsIgnoreCase(param) || DISTANCE.equalsIgnoreCase(param)) && radList.contains(value)){
                        defaultRadius = value;
                    }else if((RADIUS_UNIT.equalsIgnoreCase(param) ||DISTANCE_UNIT.equalsIgnoreCase(param)) && radUnitList.contains(value.toLowerCase())){
                        defaultDistanceUnit = value;
                    }else if(LATITUDE.equalsIgnoreCase(param) && isValidLatOrLon(value)){
                        defaultLatitude = value;
                    }else if(LONGITUDE.equalsIgnoreCase(param) && isValidLatOrLon(value)){
                        defaultLongitude = value;
                    }else {
                        LOGGER.debug("unused url parameter found");
                    }
                }
            }
            final JSONObject distanceJSON = new JSONObject();
            final JSONArray distanceUnitArray = new JSONArray();
            if (null != cloudConfigService) {
                final Optional<BullseyeConfigModel> bullsEyeCloudConfig = cloudConfigService
                        .getBullsEyeCloudConfig(currentPage.getContentResource());

				if(null != eatonSiteConfigService){
					Optional<SiteResourceSlingModel> siteConfig = eatonSiteConfigService.getSiteConfig(currentPage);
					if (siteConfig.isPresent()) {
						facetValueCount = siteConfig.get().getFacetValueCount();
					} else {
						LOGGER.debug("Site config was not authored : siteResourceSlingModel is null");
					}
				}
                if (bullsEyeCloudConfig.isPresent()) {
                    final BullseyeConfigModel bullseyeConfigModel = bullsEyeCloudConfig.get();
                    mappingApiKey = bullseyeConfigModel.getMappingApiKey();
                    mappingVendor = bullsEyeCloudConfig.get().getMappingVendor();
                    pageSize = bullseyeConfigModel.getPageSize();
                    prefilters = bullseyeConfigModel.getPrefilters();
                    final String[] distances = bullsEyeCloudConfig.get().getDistances();
                    if (null != distances) {
                        final JSONObject distanceList = getDistanceList(distances);
                        filtersArray.put(distanceList);

                        final JSONObject mileUnit = new JSONObject();
                        mileUnit.put(TITLE, CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest,
                                currentPage, BullseyeConstant.MILES_I18N));
                        mileUnit.put(NAME, MILES);
                        mileUnit.put(ID, MILES);
                        mileUnit.put(VALUE, DISTANCE);
                        if (defaultDistanceUnit.equals(MILES)) {
                            mileUnit.put(ACTIVE, true);
                        }
                        final JSONObject kmUnit = new JSONObject();
                        kmUnit.put(TITLE, CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest,
                                currentPage, BullseyeConstant.KILOMETERS_I18N));
                        kmUnit.put(NAME, KILOMETERS);
                        kmUnit.put(ID, KILOMETERS);
                        kmUnit.put(VALUE, DISTANCE);
                        if (defaultDistanceUnit.equals(KILOMETERS)) {
                            kmUnit.put(ACTIVE, true);
                        }
                        distanceUnitArray.put(kmUnit);
                        distanceUnitArray.put(mileUnit);
                        distanceJSON.put(NAME, DISTANCE_UNITS);
                        distanceJSON.put(TITLE, CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest,
                                currentPage, DISTANCE_UNITS_I18N));
                        distanceJSON.put(ID, DISTANCE_UNITS);
                        distanceJSON.put(SINGLE_FACET_ENABLED, Boolean.TRUE);
                        facetGroupOrderingJson.put(distanceJSON);
                        distanceJSON.put(VALUES, distanceUnitArray);
                        filtersArray.put(distanceJSON);
                    }
                }
            }
            if (null != bullseyeService) {
            	if(getPreSeltFilterList()!=null) {
            	preSeltCategoryList = getPreSeltFilterList();
            	}
                final List<CategoryGroupBean> filterCategoryList = getFilterList();
                final List<CategoryGroupBean> csvCategoryList = getCSVFilterList();
                final List<CategoryGroupBean> allAuthoredCategories = new ArrayList<>();
                allAuthoredCategories.addAll(filterCategoryList);
                allAuthoredCategories.addAll(csvCategoryList);
                // This is done in order to ensure that if the same category is authored for both the filters and the CSV
                // download, we only make one request to Bullseye for that category.
				allAuthoredCategories.stream().distinct().forEach(categoryGroupBean -> getBullseyeCategory(filterCategoryList, csvCategoryList, categoryGroupBean, language));
            }
        } catch (JSONException e) {
            LOGGER.error("JSON exception in Map Model", e);
        }
        LOGGER.debug("MapModel :: init() :: Ended");
    }

	private void getBullseyeCategory(final List<CategoryGroupBean> filterCategoryList, final List<CategoryGroupBean> csvCategoryList,
			CategoryGroupBean categoryGroupBean, Locale language) {
		try {
			String categoryElement = categoryGroupBean.getCategoryGroupID();
		    final JSONObject categoryJson = bullseyeService.getCategory(currentPage.getContentResource(), categoryElement, language);
		    if (filterCategoryList.contains(categoryGroupBean)) {
		        constructCategoryFilters(categoryJson, facetGroupOrderingJson, categoryGroupBean, filtersArray);
		    }
		    if (csvCategoryList.contains(categoryGroupBean)) {
		        constructCategoryFilters(categoryJson, csvColumnsArray, categoryGroupBean);
		    }
		} catch (Exception e) {
		    LOGGER.error("Exception in Map Model", e);
		}
	}

    private static void constructCategoryFilters(final JSONObject category, final JSONArray categoryGroupArray, CategoryGroupBean categoryGroupBean) {
        constructCategoryFilters(category, categoryGroupArray, categoryGroupBean, null);
    }

    private static void constructCategoryFilters(final JSONObject category, final JSONArray categoryGroupArray, CategoryGroupBean categoryGroupBean, final JSONArray categoryGroupArrayWithValues) {
        final JSONArray categoryArray = new JSONArray();
        String categoryTitle = StringUtils.EMPTY;
        String categoryGroupId = StringUtils.EMPTY;
        try {
            if (category.has(RESPONSE)) {
                final JSONArray response = category.getJSONArray(RESPONSE);
                for (int count = 0; count < response.length(); count++) {
                    final JSONObject categoryJSON = response.getJSONObject(count);
                    if (categoryJSON.has(BullseyeConstant.CATEGORY_ID) && categoryJSON.has(BullseyeConstant.CATEGORY_NAME)) {
                        final Integer categoryId = categoryJSON.getInt(BullseyeConstant.CATEGORY_ID);
                        final String categoryName = categoryJSON.getString(BullseyeConstant.CATEGORY_NAME);
                        if (count == 0) {
                            categoryTitle = categoryName;
                            categoryGroupId = categoryId.toString();
                        }
                        final JSONObject categoryFilterJSON = new JSONObject();
                        categoryFilterJSON.put(TITLE, categoryName);
                        categoryFilterJSON.put(NAME, categoryId.toString());
                        categoryFilterJSON.put(ID, categoryId.toString());
                        if ((count != 0) && (!hideFilter(categoryGroupId,categoryId.toString()))) {
                            categoryArray.put(categoryFilterJSON);
                        }
                    }
                }
                final JSONObject categoryFilterJSON = new JSONObject();
                categoryFilterJSON.put(TITLE, categoryTitle);
                categoryFilterJSON.put(NAME, categoryTitle.replace(" ", "-"));
                categoryFilterJSON.put(ID, categoryTitle);
                categoryFilterJSON.put(SINGLE_FACET_ENABLED, Boolean.parseBoolean(categoryGroupBean.getSingleFacetEnabled()));
                categoryGroupArray.put(categoryFilterJSON);
                categoryFilterJSON.put(VALUES, categoryArray);
                if (categoryGroupArrayWithValues != null) {
                    categoryGroupArrayWithValues.put(categoryFilterJSON);
                }
            }
        } catch (JSONException e) {
            LOGGER.error("JSON Exception", e);
        }
    }

    private JSONObject getDistanceList(final String[] distances) throws JSONException {
        final JSONObject distanceFilter = new JSONObject();
        final JSONArray distanceArray = new JSONArray();
        final List<String> distanceList = Arrays.asList(distances);

        distanceList.forEach(distance -> {
            try {
                final JSONObject distanceJSON = new JSONObject(distance);
                final String distanceValue = distanceJSON.getString(DISTANCE);
                final JSONObject filterItem = getFilterItem(distanceValue);
                distanceArray.put(filterItem);
            } catch (JSONException e) {
                LOGGER.error("JSON exception while constructing distance JSON", e);
            }
        });

        distanceFilter.put(NAME, DISTANCE);
        distanceFilter.put(TITLE, CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest,
                currentPage, CommonUtil.getI18NFromResourceBundle(slingHttpServletRequest,
                        currentPage, DISTANCE_I18N)));
        distanceFilter.put(ID, DISTANCE);
        distanceFilter.put(SINGLE_FACET_ENABLED, Boolean.TRUE);
        distanceFilter.put(VALUE, DISTANCE);
        facetGroupOrderingJson.put(distanceFilter);
        distanceFilter.put(VALUES, distanceArray);

        return distanceFilter;
    }

    private JSONObject getFilterItem(final String service) {
        final JSONObject serviceObject = new JSONObject();
        try {
            serviceObject.put(TITLE, service);
            serviceObject.put(NAME, service);
            serviceObject.put(ID, service);
            serviceObject.put(COUNT, "0");
            serviceObject.put(VALUE, DISTANCE);
            if (defaultRadius.equals(service)) {
                serviceObject.put(ACTIVE, true);
            }
        } catch (JSONException e) {
            LOGGER.error("JSON Exception while forming service JSON object", e);
        }
        return serviceObject;
    }

    public String getMappingApiKey() {
        return mappingApiKey;
    }

    public String getMappingVendor() {
        return mappingVendor;
    }

    public String getFacetGroupListJson() {
        return filtersArray.toString();
    }

    public JSONArray getFiltersArray() {
        return filtersArray;
    }

    public JSONArray getCsvColumnsArray() {
        return csvColumnsArray;
    }

    public String getFacetGroupOrderingJson() {
        return facetGroupOrderingJson.toString();
    }

    public String getPageSize() {
        return pageSize;
    }

    public String[] getPrefilters() {
        return prefilters;
    }

    public String getLabelCategory() {
        return labelCategory;
    }

    public String getExpanderTitle() {
        return expanderTitle;
    }

    public String getEnableExpander() {
        return enableExpander;
    }

    public String getEnableLocationType() {
        return enableLocationType;
    }

    public String getSearchType() {
        return searchType;
    }
    public String getHideDownload() {
        return hideDownload;
    }

    public String getHideMap() {
        return hideMap;
    }

    public String getHideEmail() {
        return hideEmail;
    }

    public String getHideDirection() {
        return hideDirection;
    }

    public String getHideWebsite() {
        return hideWebsite;
    }

    /**
     * Method to verify the given parameter is a valid latitude or longitude value
     * Valid latitude or longitude values are floats between -90 and +90.
     * @param parameter String that contains possible latitude or longitude value
     * @return true if parameter is valid latitude or longitude value
     */
    public boolean isValidLatOrLon(String parameter){
        if(parameter == null){
            return false;
        }
        final int maxDegrees = 90;
        try {
            Float f = Float.parseFloat(parameter);
            if(maxDegrees >= Math.abs(f)){
                return true;
            }
        } catch(NumberFormatException nfe){
            return false;
        }
        return false;
    }

	/**
   	 * Gets the facet value count for siteconfig.
   	 *
   	 * @return the facetvaluecount
   	 */

   	public int getFacetValueCount() {
   		return facetValueCount;
   	}

    public List<CategoryGroupModel> getCategoryGroupList() {
        return categoryGroupList;
    }

    private static List<CategoryGroupBean> getFilterList(){
        LOGGER.debug("MapModel :: getFilterList() :: Started");
        List<CategoryGroupBean> filterCategoryList = new ArrayList<>();
        if(categoryGroupList!=null) {
            for (int i = 0; i < categoryGroupList.size(); i++) {
                CategoryGroupModel categoryGroup = categoryGroupList.get(i);
                if ((categoryGroup != null) && (categoryGroup.getCategoryGroup() != null)) {
                    String[] cateogryGroupElements = StringUtils.split(categoryGroup.getCategoryGroup(),"::");
                    if ((cateogryGroupElements != null) && (cateogryGroupElements.length > 1)) {
                    	CategoryGroupBean categoryGroupBean = new CategoryGroupBean();
                    	categoryGroupBean.setCategoryGroupID(cateogryGroupElements[1]);
                    	categoryGroupBean.setSingleFacetEnabled(categoryGroup.getSingleFacetEnabled());
                        filterCategoryList.add(categoryGroupBean);
                    }
                }
            }
        }
        LOGGER.debug("MapModel :: getFilterList() :: Ended");
        return filterCategoryList;
    }

    private static List<CategoryGroupBean> getCSVFilterList(){

        LOGGER.debug("MapModel :: getCSVFilterList() :: Started");
        List<CategoryGroupBean> filterCSVCategoryList = new ArrayList<>();
        if(csvCategoryGroupList!=null) {
            for (int i = 0; i < csvCategoryGroupList.size(); i++) {
                CSVCategoryGroupModel csvCategoryGroup = csvCategoryGroupList.get(i);
                if ((csvCategoryGroup != null) && (csvCategoryGroup.getCSVCategoryGroup() != null)) {
                    String[] csvCateogryGroupElements = StringUtils.split(csvCategoryGroup.getCSVCategoryGroup(),"::");
                    if ((csvCateogryGroupElements != null) && (csvCateogryGroupElements.length > 1)) {
                    	CategoryGroupBean categoryGroupBean = new CategoryGroupBean();
                    	categoryGroupBean.setCategoryGroupID(csvCateogryGroupElements[1]);
                    	categoryGroupBean.setSingleFacetEnabled(csvCategoryGroup.getSingleFacetEnabled());
                        filterCSVCategoryList.add(categoryGroupBean);
                    }
                }
            }
        }
        LOGGER.debug("MapModel :: getCSVFilterList() :: Ended");
        return filterCSVCategoryList;
    }
        
        public String getPreSeltFilterList(){
            LOGGER.debug("MapModel :: getPreSeltFilterList() :: Started");
            StringBuilder filterPreSeltCategoryList = new StringBuilder();
            if(preSeltCategoryGroupList!=null) {
                for (int i = 0; i < preSeltCategoryGroupList.size(); i++) {
                	PreSeltCategoryGroupModel preSeltCategoryGroup = preSeltCategoryGroupList.get(i);
                    if ((preSeltCategoryGroup != null) && (preSeltCategoryGroup.getCategoryGroup() != null)) {
                        String[] preSeltCateogryGroupElements = StringUtils.split(preSeltCategoryGroup.getCategoryGroup(),"::");
                        if ((preSeltCateogryGroupElements != null) && (preSeltCateogryGroupElements.length > 1) && 
                        		(preSeltCategoryGroup.getCategoryList()!=null) && (!(preSeltCategoryGroup.getCategoryList().isEmpty()))) {
                        	for (int j = 0; j < preSeltCategoryGroup.getCategoryList().size(); j++) {
                        		PreSeltCategoryModel preSeltCategory = preSeltCategoryGroup.getCategoryList().get(j);
                        		String[] cateogryElements = StringUtils.split(preSeltCategory.getCategory(),"::");
                        		if ((cateogryElements != null) && (cateogryElements.length > NUMBER_ONE)) {
                        			if(filterPreSeltCategoryList.length() < NUMBER_ONE) {
                        				filterPreSeltCategoryList.append(cateogryElements[NUMBER_ONE]);
                        			}else{
                        				filterPreSeltCategoryList.append(",");
                        				filterPreSeltCategoryList.append(cateogryElements[NUMBER_ONE]);
                        			}
                        	    }
                        	}
                        }
                    }
               }
           }   
        String preSeltCategoryListString= filterPreSeltCategoryList.toString().trim();
        LOGGER.debug("MapModel :: getPreSeltFilterList() :: Ended");
        return preSeltCategoryListString;
    }

    private static boolean hideFilter(String categoryGroupId, String categoryId){

        LOGGER.debug("MapModel :: hideFilter() :: Started");
        boolean hideFilter = true;
        for(int i = 0; i < categoryGroupList.size(); i++){
            CategoryGroupModel categoryGroup = categoryGroupList.get(i);
            if(categoryGroup.getCategoryGroup()!=null) {
                String[] cateogryGroupElements = StringUtils.split(categoryGroup.getCategoryGroup(),"::");
                if ((cateogryGroupElements != null) && (cateogryGroupElements.length > NUMBER_TWO) && (categoryGroupId.equals(cateogryGroupElements[NUMBER_TWO]))
                && (categoryGroup.getCategoryList()!=null)) {
                    for (int j = 0; j < categoryGroup.getCategoryList().size(); j++) {
                        CategoryModel category = categoryGroup.getCategoryList().get(j);
                        String[] cateogryElements = StringUtils.split(category.getCategory(),"::");
                        if ((cateogryElements != null) && (cateogryElements.length > NUMBER_ONE) && (categoryId.equals(cateogryElements[NUMBER_ONE]))) {
                            hideFilter = false;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        LOGGER.debug("MapModel :: hideFilter() :: Ended");
        return hideFilter;
    }
}
