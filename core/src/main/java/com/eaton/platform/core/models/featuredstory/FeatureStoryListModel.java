package com.eaton.platform.core.models.featuredstory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * <html> Description: In post processing of this sling model,
 *  it will load the respective model inputs passing resource object</html> .
 * 
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FeatureStoryListModel {

	/** The featureStoryList. */
	@Inject
	private Resource featureStoryList;

	/** The links. */
	private List<FeatureStoryModel> links;
	
	/** The tablet trans. */
	@Inject
	private String tabletTrans;
	
	/** The desktop trans. */
	@Inject
	private String desktopTrans;
	
	/** The mobile trans. */
	@Inject
	private String mobileTrans;
	
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		if (null != featureStoryList) {
			populateModel(featureStoryList);
		}
	}

	/**
	 * Populate model.
	 *
	 * @param resource the resource
	 * @return social links
	 */
	public List<FeatureStoryModel> populateModel(Resource resource) {
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();

			links = new ArrayList<>();
			while (linkResources.hasNext()) {
				FeatureStoryModel featureStoryModel = linkResources.next().adaptTo(FeatureStoryModel.class);
				if (featureStoryModel != null) {
					featureStoryModel.setTabletTransformedUrl(getTabletTrans());
					featureStoryModel.setDesktopTransformedUrl(getDesktopTrans());
					featureStoryModel.setMobileTransformedUrl(getMobileTrans());
					links.add(featureStoryModel);
				}
			}
		}
		return this.links;
	}

	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<FeatureStoryModel> getLinks() {
		return links;
	}
	/**
	 * Gets the tablet trans.
	 *
	 * @return the tablet trans
	 */
	public String getTabletTrans() {
		return tabletTrans;
	}

	/**
	 * Gets the desktop trans.
	 *
	 * @return the desktop trans
	 */
	public String getDesktopTrans() {
		return desktopTrans;
	}

	/**
	 * Gets the mobile trans.
	 *
	 * @return the mobile trans
	 */
	public String getMobileTrans() {
		return mobileTrans;
	}
	
}
