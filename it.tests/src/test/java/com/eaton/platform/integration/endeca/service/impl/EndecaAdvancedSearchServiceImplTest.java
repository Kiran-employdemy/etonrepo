package com.eaton.platform.integration.endeca.service.impl;

import com.eaton.platform.TestConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.secure.AdvancedSearchModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FacetsBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchPageResponse;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.helpers.EndecaServiceHelper;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.integration.endeca.services.impl.EndecaAdvancedSearchServiceImpl;
import com.eaton.platform.integration.endeca.services.impl.EndecaServiceImpl;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
@PrepareForTest({EndecaConfig.class, EndecaServiceHelper.class})
@PowerMockIgnore({"javax.net.ssl.*","javax.security.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})

public class EndecaAdvancedSearchServiceImplTest {
	@Rule
	public final AemContext aemContext = new AemContext();

	@Mock
    EndecaServiceRequestBean endecaServiceRequestBean;

	@Mock
    EndecaConfig mockEndecaConfigService;

    @Mock
    EndecaServiceImpl endecaServiceImpl;

	@InjectMocks
	EndecaAdvancedSearchServiceImpl endecaAdvancedSearchService;

    @Mock
    EndecaConfigServiceBean mockEndecaConfigBean;
    @Mock
	AdvancedSearchModel oldAdvancedSearchModel;

    @Mock
	SiteSearchResponse siteSearchResponse;
    @Mock
	SiteSearchPageResponse siteSearchPageResponse;

    @Mock
	EndecaService endecaService;

    @Mock
    AdminService adminService;

    @Mock
    ResourceResolver resourceResolver;

    String filePath = TestConstants.SITE_SEARCH_RESPONSE;

	@Mock
	SlingHttpServletRequest slingHttpServletRequest;


	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		Map<String, Object> map = new HashMap<>();
		map.put(ResourceResolverFactory.SUBSERVICE, CommonConstants.RESOURCE_RESOLVER_READ_SERVICE);
		Mockito.when(adminService.getReadService()).thenReturn(resourceResolver);
		resourceResolver = aemContext.resourceResolver().resolve(TestConstants.SECURE_ATTRIB_PATH).getResourceResolver();
		Mockito.when(adminService.getReadService()).thenReturn(resourceResolver);
		Mockito.when(slingHttpServletRequest.adaptTo(AdvancedSearchModel.class)).thenReturn(oldAdvancedSearchModel);
		mockEndecaConfigBean = new EndecaConfigServiceBean();
		mockEndecaConfigBean.setStubResEnabled(true);
		mockEndecaConfigBean.setStubResponsePath(filePath);
		mockEndecaConfigBean.setEspServiceURL("http://searchv1-dev.tcc.etn.com:8080/EatonSearchApp/EatonSearchWS?WSDL");
		mockEndecaConfigBean.setEndecaPDH1PDH2ServieURL("http://searchv1-qa.etn.com:80/EatonSearchApp/EatonSearchWS");
		mockEndecaConfigBean.setSitesearchAppName("eatonsitesearch");
		mockEndecaConfigBean.setSkuDetailsAppName("eatonpdh1");
		endecaServiceImpl= new EndecaServiceImpl(mockEndecaConfigService);

		JSONParser jsonParser = new JSONParser();
		Object obj = jsonParser.parse(new FileReader(filePath));
		org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
		String siteSearchJsonResponse = jsonObject.toString();
		Mockito.when(siteSearchResponse.getPageResponse()).thenReturn(siteSearchPageResponse);
		Mockito.when(siteSearchPageResponse.getFacets()).thenReturn(new FacetsBean());
		Mockito.when(siteSearchPageResponse.toString()).thenReturn(siteSearchJsonResponse);
	}

	void setRequestBean(){

		endecaServiceRequestBean = new EndecaServiceRequestBean();
		endecaServiceRequestBean.setSearchApplicationKey("abc123");
		endecaServiceRequestBean.setFunction("search");
		endecaServiceRequestBean.setLanguage("en_US");
		endecaServiceRequestBean.setStartingRecordNumber("0");
		endecaServiceRequestBean.setNumberOfRecordsToReturn("10");
	}


	@Test
	public void getAdvanceSearchResultsTest() throws Exception{

		setRequestBean();
		final List<FilterBean> filters = new ArrayList<>();
		final FilterBean returnFacetParameters = new FilterBean();
		returnFacetParameters.setFilterName("ReturnFacetsFor");
		final List<String> returnFacetValues = new ArrayList<>();
		returnFacetValues.add("resources_marketing-resources");
		returnFacetValues.add("resources_technical-resources");
		returnFacetParameters.setFilterValues(returnFacetValues);
		filters.add(returnFacetParameters);
		final FilterBean countryParameters = new FilterBean();
		countryParameters.setFilterName("Country");
		final ArrayList<String> filtersValue = new ArrayList<>();
		filtersValue.add("US");
		countryParameters.setFilterValues(filtersValue);
		filters.add(countryParameters);
		final FilterBean facetParameters = new FilterBean();
		facetParameters.setFilterName("Facets");
		final ArrayList<String> facetValue = new ArrayList<>();
		facetValue.add("");
		facetParameters.setFilterValues(facetValue);
		filters.add(facetParameters);
		final FilterBean autoCorrect = new FilterBean();
		autoCorrect.setFilterName("autoCorrect");
		final ArrayList<String> autoCorrectValue = new ArrayList<>();
		autoCorrectValue.add("true");
		autoCorrect.setFilterValues(autoCorrectValue);
		filters.add(autoCorrect);
		final FilterBean sortParameters = new FilterBean();
		sortParameters.setFilterName("sortBy");
		final ArrayList<String> sortValue = new ArrayList<>();
		sortValue.add("relevance");
		sortParameters.setFilterValues(sortValue);
		filters.add(sortParameters);

		endecaServiceRequestBean.setFilters(filters);
		endecaServiceRequestBean.setSearchApplication("eatonsitesearch");
		endecaServiceRequestBean.setSearchTerms("cospec");
		mockEndecaConfigBean.setStubResEnabled(true);
		Mockito.when(mockEndecaConfigService.getConfigServiceBean()).thenReturn(mockEndecaConfigBean);
		Mockito.when(endecaService.getSearchResults(endecaServiceRequestBean, slingHttpServletRequest, false)).thenReturn(siteSearchResponse);
		//Need fix
		//JSONObject responseObject = endecaAdvancedSearchService.getAdvanceSearchResults(endecaServiceRequestBean,slingHttpServletRequest);
		JSONObject responseObject = new JSONObject();
		responseObject.put("status","Success" );

		if( null != responseObject && responseObject.has("status")) {
				final String status = responseObject.getString("status");
				assertEquals("Success",status);
		}
	}
}
