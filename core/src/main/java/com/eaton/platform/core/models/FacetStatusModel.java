package com.eaton.platform.core.models;

import java.util.List;

import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;
import com.eaton.platform.core.injectors.annotations.FacetURLBeanServiceResponseInjector;
import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.ProductGridSelectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FacetStatusModel {

	 /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FacetStatusModel.class);

    @Inject
    private String text;
    private ProductGridSelectors productGridSelectors;
    private String facetStatus;
    private String uncheckUrl;

    @FacetURLBeanServiceResponseInjector
    private FacetURLBeanServiceResponse facetURLBeanServiceResponse;

    @PostConstruct
    public void init() {
        LOGGER.debug("Entered into init method");
        //String text = get("text", String.class);
         facetStatus = "notChecked";
         uncheckUrl = "#";
    	try{
    		if(null != facetURLBeanServiceResponse){
				productGridSelectors = facetURLBeanServiceResponse.getProductGridSelectors();
				List <FacetBean> activeFacetsRecievedList = productGridSelectors.getFacets();
				if(activeFacetsRecievedList != null && CollectionUtils.isNotEmpty(activeFacetsRecievedList)){
					for(int i=0;i<activeFacetsRecievedList.size();i++){
						FacetBean activeFacetsRecieved = activeFacetsRecievedList.get(i);
						if(activeFacetsRecieved.getFacetID() != null && activeFacetsRecieved.getFacetID().equals(text)){
							facetStatus = "checked";
							uncheckUrl = activeFacetsRecieved.getFacetDeselectURL();
						}
					}
				}
			}

    	}catch(Exception e){
    		LOGGER.debug("Exception occured in isFacetChecked() "+e);
    	}
    	  LOGGER.debug("Exited from activate method");
    }


    public boolean isFacetChecked(){
    	boolean returnValue = false;
    	try{
		productGridSelectors = facetURLBeanServiceResponse.getProductGridSelectors();
    	List <FacetBean> activeFacetsRecievedList = productGridSelectors.getFacets();
    	if(activeFacetsRecievedList != null){
    	for(int i=0;i<activeFacetsRecievedList.size();i++){
    		FacetBean activeFacetsRecieved = activeFacetsRecievedList.get(i);

    		if(activeFacetsRecieved.getFacetID() != null && activeFacetsRecieved.getFacetID().equals(text)){
    			returnValue = true;
    		}
    	}
    	}
    	}catch(Exception e){
    		LOGGER.debug("Exception occured in isFacetChecked() "+e);
    	}
		return returnValue;
	}

	public String getFacetStatus() {
		return facetStatus;
	}


	public String getUncheckUrl() {
		return uncheckUrl;
	}




}
