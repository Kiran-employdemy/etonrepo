package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * <html> Description: In post processing of this sling model, it will load the respective model inputs passing
 * resource object</html> .
 * 
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ThumbnailImageListModel {

	/** The image list. */
	@Inject
	private Resource imageList;
	
	/** The Mobile Transforms. */
	@Inject
	private String mobileTrans;
	
	@Inject
	private String desktopTrans;
	
	/** The links. */
	private List<ThumbnailImageMultifieldModel> links;
	
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		if (null != imageList) {
			populateModel(imageList);
		}
	}

	/**
	 * Populate model.
	 *
	 * @param resource the resource
	 * @return the list
	 */
	public List<ThumbnailImageMultifieldModel> populateModel(Resource resource) {
		links = new ArrayList<>();
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
				while (linkResources.hasNext()) {
					ThumbnailImageMultifieldModel imageListLink = linkResources.next().adaptTo(ThumbnailImageMultifieldModel.class);
					if (null != imageListLink) {
						imageListLink.setDesktopTransformedUrl(getDesktopTrans());
						imageListLink.setMobileTransformedUrl(getMobileTrans());
						links.add(imageListLink);
					}
				}
		}
		return links;
	}

	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<ThumbnailImageMultifieldModel> getLinks() {
		return links;
	}

	/**
	 * @return the mobileTrans
	 */
	public String getMobileTrans() {
		if(null == mobileTrans) {
			mobileTrans = StringUtils.EMPTY;
		}
		return mobileTrans;
	}
	
	public String getDesktopTrans() {
		if(null == desktopTrans) {
			desktopTrans = StringUtils.EMPTY;
		}
		return desktopTrans;
	}
	
}
