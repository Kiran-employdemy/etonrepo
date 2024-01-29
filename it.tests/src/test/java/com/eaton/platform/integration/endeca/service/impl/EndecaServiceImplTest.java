package com.eaton.platform.integration.endeca.service.impl;

import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.impl.AdminServiceImpl;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUListResponseBean;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.bean.subcategory.FamilyListResponseBean;
import com.eaton.platform.integration.endeca.bean.typeahead.TypeAheadSiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchToolResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueSelectorResponse;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueToolResponse;
import com.eaton.platform.integration.endeca.helpers.EndecaServiceHelper;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.impl.EndecaServiceImpl;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
@PrepareForTest({EndecaConfig.class, EndecaServiceHelper.class})
@PowerMockIgnore({"javax.net.ssl.*","javax.security.*","com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})

public class EndecaServiceImplTest {
	@Mock
    EndecaServiceRequestBean endecaServiceRequestBean;
	
	@Mock
    EndecaConfig mockEndecaConfigService;
	
	@InjectMocks
    EndecaServiceImpl endecaServiceImpl;
	
    @Mock
    EndecaConfigServiceBean mockEndecaConfigBean;
    
    @Mock
    SiteSearchResponse siteSearchResponse;
    
    @Mock
    TypeAheadSiteSearchResponse typeAheadResponse;
    
    @Mock
    SKUDetailsResponseBean skuDetailsBean;
    
    @Mock
    SKUListResponseBean skuListResponseBean;
    
    @Mock
    FamilyListResponseBean familyListResponseBean;
    
    @Mock
    EndecaServiceHelper endecaServiceHelper;
    
    @Mock
    AdminService mockAdminService;
    
    @Mock
    ResourceResolver mockrResourceResolver;
    
    String filePath = null;
    
    @Mock
    Resource mockSelectedRes;
    
    String jsonRequest = null;
    
    String jsonResponse = null;
    
    @Mock
    ClutchSelectorResponse clutchSelectorResponseBean;
    
    @Mock
    TorqueSelectorResponse torqueSelectorResponseBean;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		endecaServiceImpl = PowerMockito.mock(EndecaServiceImpl.class);
		mockEndecaConfigService = PowerMockito.mock(EndecaConfig.class);
		mockAdminService = PowerMockito.mock(AdminServiceImpl.class);
		mockEndecaConfigBean = new EndecaConfigServiceBean();
		mockEndecaConfigBean.setStubResEnabled(true);
		mockEndecaConfigBean.setStubResponsePath(filePath);
		mockEndecaConfigBean.setEspServiceURL("http://searchv1-dev.tcc.etn.com:8080/EatonSearchApp/EatonSearchWS?WSDL");
		mockEndecaConfigBean.setEndecaPDH1PDH2ServieURL("http://searchv1-qa.etn.com:80/EatonSearchApp/EatonSearchWS");
		mockEndecaConfigBean.setSitesearchAppName("eatonsitesearch");
		mockEndecaConfigBean.setSkuDetailsAppName("eatonpdh1");
		mockEndecaConfigBean.setProductfamilyAppName("eatonpdh2");
		mockEndecaConfigBean.setSubcategoryAppName("eatonpdh3");
		endecaServiceImpl= new EndecaServiceImpl(mockEndecaConfigService);
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
	public void getClutchToolDetailsTest() throws Exception{
		setRequestBean();
		List<FilterBean> filters = new ArrayList<>();
		FilterBean skuCardParameters = new FilterBean();
		skuCardParameters.setFilterName("SKUCardParameters");
		List<String> skuCardValues = new ArrayList<>();
		skuCardValues.add("Description");
		skuCardValues.add("Torque Capacity");
		skuCardValues.add("Clutch Size");
		skuCardParameters.setFilterValues(skuCardValues);
		filters.add(skuCardParameters);

		endecaServiceRequestBean.setFilters(filters);
		endecaServiceRequestBean.setSearchApplication("etnvgselclutch");
		endecaServiceRequestBean.setSearchTerms("RESULTS");
		clutchSelectorResponseBean = new ClutchSelectorResponse();
		clutchSelectorResponseBean.setClutchToolResponse(new ClutchToolResponse());
		clutchSelectorResponseBean.getClutchToolResponse().setStatus("Success");
		
		JsonArray filterValue = new JsonArray();
		Mockito.when(mockEndecaConfigService.getConfigServiceBean()).thenReturn(mockEndecaConfigBean);
		Mockito.when(endecaServiceHelper.convertBeanTOJSON(endecaServiceRequestBean)).thenReturn(jsonRequest);
		Mockito.when(endecaServiceHelper.getSKUCardParameters(jsonRequest)).thenReturn(filterValue);
		Mockito.when(endecaServiceHelper.convertClutchToolListJSONTOBean(jsonResponse,filterValue)).thenReturn(clutchSelectorResponseBean);
		assertEquals(clutchSelectorResponseBean.getClutchToolResponse().getStatus(),endecaServiceImpl.getClutchToolDetails(endecaServiceRequestBean).getClutchToolResponse().getStatus());
	}
	
	@Test
	public void getTorqueToolDetailsTest() throws Exception{
		setRequestBean();
		List<FilterBean> filters = new ArrayList<>();
		FilterBean skuCardParameters = new FilterBean();
		skuCardParameters.setFilterName("SKUCardParameters");
		List<String> skuCardValues = new ArrayList<>();
		skuCardValues.add("Product Name");
		skuCardValues.add("Ring Gear Diameter");
		skuCardValues.add("Spline Count");
		skuCardParameters.setFilterValues(skuCardValues);
		filters.add(skuCardParameters);

		endecaServiceRequestBean.setFilters(filters);
		endecaServiceRequestBean.setSearchApplication("etnvgseltorque");
		endecaServiceRequestBean.setSearchTerms("RESULTS");
		torqueSelectorResponseBean = new TorqueSelectorResponse();
		torqueSelectorResponseBean.setTorqueToolResponse(new TorqueToolResponse());
		torqueSelectorResponseBean.getTorqueToolResponse().setStatus("Success");
		
		JsonArray filterValue = new JsonArray();
		Mockito.when(mockEndecaConfigService.getConfigServiceBean()).thenReturn(mockEndecaConfigBean);
		Mockito.when(endecaServiceHelper.convertBeanTOJSON(endecaServiceRequestBean)).thenReturn(jsonRequest);
		Mockito.when(endecaServiceHelper.getSKUCardParameters(jsonRequest)).thenReturn(filterValue);
		Mockito.when(endecaServiceHelper.convertTorqueToolListJSONTOBean(jsonResponse,filterValue)).thenReturn(torqueSelectorResponseBean);
		assertEquals(torqueSelectorResponseBean.getTorqueToolResponse().getStatus(),endecaServiceImpl.getTorqueToolDetails(endecaServiceRequestBean).getTorqueToolResponse().getStatus());
	}

	@Test
	public void getSubmittalBuilderTest() throws Exception{
		setRequestBean();
		final List<FilterBean> filters = new ArrayList<>();
		final FilterBean returnFacetParameters = new FilterBean();
		returnFacetParameters.setFilterName("ReturnFacetsFor");
		final List<String> returnFacetValues = new ArrayList<>();
		returnFacetValues.add("b-line-submittal-builder_material");
		returnFacetValues.add("b-line-submittal-builder_type");
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
		final FilterBean sortParameters = new FilterBean();
		sortParameters.setFilterName("sortBy");
		final ArrayList<String> sortValue = new ArrayList<>();
		sortValue.add("|0");
		sortParameters.setFilterValues(sortValue);
		filters.add(sortParameters);

		endecaServiceRequestBean.setFilters(filters);
		endecaServiceRequestBean.setSearchApplication("eatoncomsubmittalbuilder");
		endecaServiceRequestBean.setSearchTerms("product-taxonomy_support-systems_cable-tray-and-ladder-systems");
		mockEndecaConfigBean.setStubResEnabled(true);
		Mockito.when(mockEndecaConfigService.getConfigServiceBean()).thenReturn(mockEndecaConfigBean);
		Mockito.when(endecaServiceHelper.convertBeanTOJSON(endecaServiceRequestBean)).thenReturn(jsonRequest);
		JsonObject responseJson = endecaServiceImpl.getSubmittalResponse(endecaServiceRequestBean);
		if(responseJson.has("status")) {
				final String status = responseJson.get("status").getAsString();
				assertEquals("success",status);
		}
	}
}
