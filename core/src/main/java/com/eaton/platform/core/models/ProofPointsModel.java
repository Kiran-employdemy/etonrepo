package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProofPointsModel {
	
	/** The proof points info list. */
	@Inject
    private Resource proofPointsInfoList;
	
	/** The links. */
	public List<ProofPointsListModel> links;
	
	/** The toggle inner grid. */
	@Inject
	private String toggleInnerGrid;
	
	@Inject
	private String mobileTrans;
	
	/** The Desktop Transforms. */
	@Inject
	private String desktopTrans;
	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<ProofPointsListModel> getLinks() {
		return links;
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		links = new ArrayList<ProofPointsListModel>();
		if (proofPointsInfoList != null) {
			populateModel(links, proofPointsInfoList);
		}
	}
	
	/**
	 * Populate model.
	 *
	 * @param array the array
	 * @param resource the resource
	 * @return the list
	 */
	public  List<ProofPointsListModel> populateModel(List<ProofPointsListModel> array, Resource resource) {
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				ProofPointsListModel link = linkResources.next().adaptTo(ProofPointsListModel.class);
				if (link != null){
					link.setMobileTransformedUrl(getMobileTrans());
					link.setDesktopTransformedUrl(getDesktopTrans());
					array.add(link);
					
				
				}
			}
		}
		return array;
	}

	/**
	 * Gets the toggle inner grid.
	 *
	 * @return the toggle inner grid
	 */
	public String getToggleInnerGrid() {
		return toggleInnerGrid;
	}
	/**
	 * @return the mobileTrans
	 */
	public String getMobileTrans() {
		return mobileTrans;
	}

	/**
	 * @return the desktopTrans
	 */
	public String getDesktopTrans() {
		return desktopTrans;
	}
}
