package com.eaton.platform.core.models;

import javax.inject.Inject;
import javax.inject.Named;

import com.eaton.platform.core.bean.ResourceListTagsBean;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DocumentGroupWithAemTagsModel {
	
	@Inject @Named("groupName")
	private String groupName;
	
	@Inject @Named("groupDescription")
	private String groupDescription;
	
	@Inject @Named("anchorId")
	private String anchorId;
	
	@Inject @Named("aemtags")
	private String  aemtags;

    public List<ResourceListTagsBean> resourceListTagsBean;
	
	
	public String getGroupName() {
		return groupName;
	}

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

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public String getAemtags() {
		return aemtags;
	}

	public void setAemtags(String aemtags) {
		this.aemtags = aemtags;
	}

    public List<ResourceListTagsBean> getResourceListTagsBean() {
        return this.resourceListTagsBean;
    }

    public void setResourceListTagsBean(List<ResourceListTagsBean> resourceListTagsBean) {
        this.resourceListTagsBean = resourceListTagsBean;
    }
}
