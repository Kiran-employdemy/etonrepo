package com.eaton.platform.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.resource.Resource;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import javax.inject.Named;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author Eaton
 * @version 1.0
 * @since 2020	
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HelpListModel {
	
	/** Variable The alt. */
	
	@Inject @Source("sling-object")
    private ResourceResolver resourceResolver;
	
	//private String featureCTATitle;
	
	@Inject
	Resource helplistLink;
	
	private List<HelpListLinks> userDownStreamLink;
	
	@PostConstruct
	protected void init() {
		if (null != helplistLink) {				
			populateModel(helplistLink);
		}
	}
	
	public List<HelpListLinks> populateModel(Resource resource){
		if(resource != null) {
			Iterator<Resource> downStreamApplicationLink = resource.listChildren();
			List<HelpListLinks> helpList = new ArrayList<>();
			while(downStreamApplicationLink.hasNext()){
				HelpListLinks helpListsLink = downStreamApplicationLink.next().adaptTo(HelpListLinks.class);
				if(null != helpListsLink) {
					helpList.add(helpListsLink);
				}
			}
			userDownStreamLink = helpList;
		}
		return userDownStreamLink;
	}
	
	public Resource getHelplistLink() {
		return helplistLink;
	}
	public List<HelpListLinks> getUserDownStreamLink(){
		return userDownStreamLink;
	}
	public ResourceResolver getResourceResolver() {
		return resourceResolver;
	}
}
