package com.eaton.platform.core.models;

import javax.inject.Inject;

import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;


@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TabbedMenuModel {
	
	@Inject	@Optional @Default(values = StringUtils.EMPTY)
	private String name;
	
	@Inject @Optional @Default(values = CommonConstants.CHILD_PAGES)
	private String listType;
	
	@Inject @Optional @Default(values = CommonConstants.TITLE)
	private String sort;
	
	@Inject @Optional
	private String parentPage;
	
	@Inject @Optional
	private String[] tags;
	
	@Inject @Optional @Default(values = CommonConstants.FALSE)
	private String newWindow;
	
	@Inject @Optional
    private Resource fixedLinks;
	

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the listType
	 */
	public String getListType() {
		return listType;
	}

	/**
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * @return the parentPage
	 */
	public String getParentPage() {
		return parentPage;
	}

	/**
	 * @return the tags
	 */
	public String[] getTags() {
		return tags;
	}

	/**
	 * @return the newWindow
	 */
	public String getNewWindow() {
		return newWindow;
	}

	/**
	 * @return the fixedList
	 */
	public Resource getFixedLinks() {
		return fixedLinks;
	}
    
    	
}
