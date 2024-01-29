package com.eaton.platform.core.bean;

import java.util.List;

/**
 * This class is for Brightcove Player Bean.
 * @author TCS
 *
 */
public class DocumentGroupBean  {
	/** groupName. */
	public String groupName;
	
	/** anchorId. */
	public String anchorId;
	
	
	/** groupDescription. */
	public String groupDescription; 
	
	
	/** aemtags. */
	public String[] aemtags;
	
	public List<ResourceListTagsBean> resourceListTagsBean;
	
	
	/**
	 * @return the groupName
	 */
	public String getGroupName() {
		return groupName;
	}
	/**
	 * @param groupName the groupName to set
	 */
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * @return the anchorId
	 */
	public String getAnchorId() {
		return anchorId;
	}
	/**
	 * @param anchorId the anchorId to set
	 */
	public void setAnchorId(String anchorId) {
		this.anchorId = anchorId;
	}
	/**
	 * @return the groupDescription
	 */
	public String getGroupDescription() {
		return groupDescription;
	}
	/**
	 * @param groupDescription the groupDescription to set
	 */
	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}
	/**
	 * @return the aemtags
	 */
	public String[] getAemtags() {
		return aemtags;
	}
	/**
	 * @param aemtags the aemtags to set
	 */
	public void setAemtags(String[] aemtags) {
		this.aemtags = aemtags;
	}
	public List<ResourceListTagsBean> getResourceListTagsBean() {
		return resourceListTagsBean;
	}
	public void setResourceListTagsBean(List<ResourceListTagsBean> resourceListTagsBean) {
		this.resourceListTagsBean = resourceListTagsBean;
	}
	
	

}