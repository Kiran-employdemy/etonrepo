package com.eaton.platform.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.helpers.FacetedNavigationHelperV2;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;




import java.util.ArrayList;
import java.util.List;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SearchResultsModel {
	
	/** The view. */
	@Inject
	private String view;
	@Default(values = CommonConstants.SEARCH_DEFAULT_VIEW)
    
	/** The content type option. */
	@Inject
	private String contentTypeOption;
	
	/** The tags. */
	@Inject
	private String tags;
	
	/** The default sort. */
	@Inject
	private String defaultSort;
	
	/** The zero search results main message. */
	@Inject
	private String zeroSearchResultsMainMessage;

    @Inject
    private String hideGlobalFacetSearch;

    private List<FacetGroupBean> facetGroupBeanList;

    @Inject
    private List<Resource> tagAttributes;

	@Inject
	protected AdminService adminService;

	@Inject
	protected TagManager tagManager;

	private FacetedNavigationHelperV2 facetedNavigationHelperV2=new FacetedNavigationHelperV2();

    @PostConstruct
    private void init(){
		if (CollectionUtils.isNotEmpty(tagAttributes) && null != adminService){
			facetGroupBeanList = new ArrayList<>();
		   if(null != tagManager){
                        tagAttributes.stream().forEach(resource -> {
                            final FacetGroupBean facetGroupBean = getFacetGroupBean(tagManager, resource);
							facetGroupBeanList.add(facetGroupBean);
                        });
		   }
		}

	}

	private FacetGroupBean getFacetGroupBean(final TagManager tagManager, final Resource resource) {
		final FacetGroupBean facetGroupBean = new FacetGroupBean();
		final ValueMap valueMap = resource.getValueMap();
		final Tag tag = tagManager.resolve(valueMap.get(CommonConstants.CQ_TAGS, StringUtils.EMPTY));
		if (null != tag){
			if(tag.adaptTo(EndecaFacetTag.class) != null) {
				facetGroupBean.setFacetGroupId(tag.adaptTo(EndecaFacetTag.class).getFacetId());
			}
		}
		facetGroupBean.setGridFacet(valueMap.get(facetedNavigationHelperV2.FACET_GROUP_SHOW_AS_GRID, false));
		facetGroupBean.setFacetSearchEnabled(valueMap.get(facetedNavigationHelperV2.FACET_GROUP_FACET_SEARCH_ENABLED, false));
		facetGroupBean.setSingleFacetEnabled(valueMap.get(facetedNavigationHelperV2.SINGLE_FACET_ENABLED, false));
		return facetGroupBean;
	}


	/**
	 * Gets the view.
	 *
	 * @return view
	 */
	public String getView() {
		if(null == view) {
			view = CommonConstants.SEARCH_DEFAULT_VIEW;
		}
		return view;
	}

	/**
	 * Gets the content type option.
	 *
	 * @return the content type option
	 */
	public String getContentTypeOption() {
		return contentTypeOption;
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * Gets the default sort.
	 *
	 * @return the default sort
	 */
	public String getDefaultSort() {
		return defaultSort;
	}

	/**
	 * Gets the zero search results main message.
	 *
	 * @return the zero search results main message
	 */
	public String getZeroSearchResultsMainMessage() {
		return zeroSearchResultsMainMessage;
	}

	public String getHideGlobalFacetSearch() {
		return hideGlobalFacetSearch;
	}

	/**
	 * Sets the zero search results main message.
	 *
	 * @param zeroSearchResultsMainMessage the new zero search results main message
	 */
	public void setZeroSearchResultsMainMessage(String zeroSearchResultsMainMessage) {
		this.zeroSearchResultsMainMessage = zeroSearchResultsMainMessage;
	}

	public List<FacetGroupBean> getFacetGroupBeanList() {
		return facetGroupBeanList;
	}

	public void setFacetGroupBeanList(List<FacetGroupBean> facetGroupBeanList) {
		this.facetGroupBeanList = facetGroupBeanList;
	}
}
