package com.eaton.platform.core.models.downloadlinklist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import com.eaton.platform.core.constants.AssetConstants;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.adobe.forms.foundation.service.util.AssetUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.util.EatonAssetUtils;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.DownloadLinkBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.JcrQueryConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.JcrQueryUtils;


/**
 * <html> Description: This Sling Model used in Download Link List component. </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DownloadLinkListModel {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DownloadLinkListModel.class);

	@Inject
	private AuthorizationService authorizationService;

	@Inject
	private Resource resource;

	/** The list type. */
	@Inject @Via("resource")
	private String listType;

	/** The parent page. */
	@Inject @Via("resource")
	private String parentDamPath;

	/** The tags. */
	@Inject @Via("resource")
	private String[] tags;

	/** The new window. */
	@Inject @Via("resource")
	private String openNewWindow;

	/** The maxLimitToDisplay. */
	@Inject @Via("resource")
	private int maxLimitToDisplay;

	/** The enable teaser content. */
	@Inject @Via("resource")
	private String enableTeaserContent;

	/** The sort. */
	@Inject @Via("resource")
	private String sortBy;

	/** The maxLimitInEachCol. */
	@Inject @Via("resource")
	private int maxLimitInEachCol;

	/** The download linksheader. */
	@Inject @Via("resource")
	private String downloadLinksheader;

	/** The download links. */
	@Inject @Via("resource")
	private Resource downloadLinks;

	/** The enable inner grid. */
	@Inject @Via("resource")
	private String enableInnerGrid;

	/** The resource page. */
	@Inject
	private Page resourcePage;

	@Inject @ScriptVariable
	private Page currentPage;

	@Inject @Via("resource")
	private Resource res;

	@Inject @Source("sling-object")
	private SlingHttpServletRequest slingRequest;

	@Inject @Source("sling-object")
	private ResourceResolver resourceResolver;

	/** The links. */
	private List<FixedLinksModel> downloadLink;

	/** The child page links list. */
	private List<DownloadLinkBean> childPageAndTagsLinksList;
	
	@EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;
    
    private boolean isUnitedStatesDateFormat = false;

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		LOG.debug("DownloadLinkListModel :: init() :: Started");
		if (siteResourceSlingModel.isPresent()) {
			isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
		}
		if (null != resourcePage){
			switch(getListType()) {
			case CommonConstants.CHILD_PAGES :
				childPageChildList();
				break;
			case CommonConstants.TAGS :
				childPageTagList();
				break;
			case CommonConstants.FIXED_LIST :
				fixedList();
				break;
			default:
				this.listType = CommonConstants.FIXED_LIST;
				fixedList();
				break;
			}
		}
		LOG.debug("DownloadLinkListModel :: init() :: Exit");
	}

	public List<?> getListTypeArray() {
		List<?> viewList = new ArrayList<>();
		if(StringUtils.equals(CommonConstants.CHILD_PAGES, this.listType) || StringUtils.equals(CommonConstants.TAGS, this.listType)) {
			if(maxLimitToDisplay > childPageAndTagsLinksList.size()) {
				maxLimitToDisplay = childPageAndTagsLinksList.size();
			}
			viewList = childPageAndTagsLinksList.subList(0, maxLimitToDisplay);
		} else if(StringUtils.equals(CommonConstants.FIXED_LIST, this.listType)){
			viewList = downloadLink;
		} else {
			LOG.info("The List Type is Empty");
		}
		return viewList;
	}

	private void fixedList() {
		downloadLink = new ArrayList<>();
		if (null != downloadLinks) {
			populateModel(downloadLinks);
		}
	}

	/**
	 * Populate model.
	 *
	 * @param resource the resource
	 * @return List
	 */
	public List<FixedLinksModel> populateModel(Resource resource) {
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();	
			List<FixedLinksModel> downloadLists = new ArrayList<>();
			while (linkResources.hasNext()) {
				FixedLinksModel downloadListFields = linkResources.next().adaptTo(FixedLinksModel.class);
				if(null != downloadListFields) {
					updateSecureFilter(downloadListFields, downloadLists);
				}

			}
			this.downloadLink = downloadLists;
		}
		return this.downloadLink;
	}

	/**
	 * This will return the childPageAndTagsLinksList for child
	 * 
	 */
	private void childPageChildList() {
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != getParentDamPath()) {
			populateParentModel(childPageAndTagsLinksList, getParentDamPath(),
					resourceResolver);
			getSortedList(childPageAndTagsLinksList);
		}
	}

	/**
	 * @param childPageAndTagsLinksList
	 * @param parentPagePath
	 * @param resourceResolver
	 * @return
	 * @throws RepositoryException 
	 */
	public List<DownloadLinkBean> populateParentModel(List<DownloadLinkBean> childPageAndTagsLinksList,
			String damPath, ResourceResolver resourceResolver) {
		try {
			if(null != resourceResolver) {
				LOG.debug("DownloadLinkListModel: populateParentModel Method Started");
				List < Hit > hitList = findChildPageAssets(resourceResolver, damPath);
				if (CollectionUtils.isNotEmpty(hitList)) {
					DownloadLinkBean downloadLinkBean = null;
					for (final Hit hit : hitList) {
						final Resource childAssetResource = hit.getResource();
						final Asset asset = childAssetResource.adaptTo(Asset.class);
						if(null != asset){
							downloadLinkBean = getAssetDetails(asset, childAssetResource);
							childPageAndTagsLinksList.add(downloadLinkBean);
						}
					}
				}
			}
		} catch(RepositoryException e) {
			LOG.error("Repository Exception in populateParentModel" + e.getMessage(),e);
		}
		LOG.debug("DownloadLinkListModel: populateParentModel Method Ended");
		return childPageAndTagsLinksList;
	}

	private static List < Hit > findChildPageAssets(ResourceResolver resourceResolver, String damPath) {

		LOG.debug("DownloadLinkListModel: findChildPageAssets Method Started");
		List<Hit> hitList = null;
		if(null != resourceResolver && null != damPath){
			final Map < String, String > queryParams = new HashMap < > ();
			final Session session = resourceResolver.adaptTo(Session.class);
			final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
			queryParams.put(JcrQueryConstants.PROP_TYPE, DamConstants.NT_DAM_ASSET);
			queryParams.put(CommonConstants.DAM_PATH, damPath);
			queryParams.put(JcrQueryConstants.P_HITS, JcrQueryConstants.FULL);
			queryParams.put(JcrQueryConstants.P_LIMIT, JcrQueryConstants.INFINITY_VALUE);

			hitList = JcrQueryUtils.excuteGenericQuery(session, queryBuilder, queryParams);
		}
		LOG.debug("DownloadLinkListModel: findChildPageAssets Method Ended");
		return hitList;
	}
	
	/* This method will 
	 * return the
	 * childPageAndTagsLinksList for tags 
	 */
	private void childPageTagList() {
		childPageAndTagsLinksList = new ArrayList<>();
		if (null != tags) {
			populateTagsModel(childPageAndTagsLinksList, tags, resourceResolver);
			getSortedList(childPageAndTagsLinksList);
		}
	}

	/**
	 * @param childPageAndTagsLinksList
	 * @param tags
	 * @param resourceResolver
	 * @return
	 */
	public List<DownloadLinkBean> populateTagsModel(List<DownloadLinkBean> childPageAndTagsLinksList, String[] tags, ResourceResolver resourceResolver) {
		try {
			if (null != resourceResolver) {
				List < Hit > hitList = findTagAssets(resourceResolver, tags);
				if (CollectionUtils.isNotEmpty(hitList)) {
					DownloadLinkBean downloadLinkBean = null;
					for (final Hit hit : hitList) {
						final Resource tagsAssetResource = hit.getResource();
						final Asset asset = tagsAssetResource.adaptTo(Asset.class);
						if(null != asset){
							downloadLinkBean = getAssetDetails(asset, tagsAssetResource);
							childPageAndTagsLinksList.add(downloadLinkBean);
						}
					}
				}
			} 
		}catch(RepositoryException e) {
			LOG.error("Repository Exception in populateParentModel" + e.getMessage(),e);
		}
		return childPageAndTagsLinksList;
	}

	private static List<Hit> findTagAssets(ResourceResolver resourceResolver, final String[] tagList) {
		LOG.debug("DownloadLinkListModel: findTagAssets Method Started");
		List<Hit> hitList = null;
		if(null != resourceResolver && null != tagList){
			final Map < String, String > queryParams = new HashMap < > ();
			final Session session = resourceResolver.adaptTo(Session.class);
			final QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
			queryParams.put(JcrQueryConstants.PROP_TYPE, DamConstants.NT_DAM_ASSET);
			queryParams.put(CommonConstants.DAM_PATH, CommonConstants.CONTENT_DAM_EATON);
			queryParams.put(JcrQueryConstants.P_HITS, JcrQueryConstants.FULL);
			queryParams.put(JcrQueryConstants.P_LIMIT, JcrQueryConstants.INFINITY_VALUE);
			queryParams.put(CommonConstants.PROPERTY, CommonConstants.METADATA_CQ_TAGS);
			queryParams.put(CommonConstants.PROPERTY_OPERATION, CommonConstants.LIKE);

			for (int count = 0; count < tagList.length; count++) {
				queryParams.put((count + 1) + CommonConstants.UNDERSCORE_PROPERTY, CommonConstants.METADATA_CQ_TAGS);
				queryParams.put((count + 1) + CommonConstants.PROPERTY_VALUE, tagList[count]);
			}
			hitList = JcrQueryUtils.excuteGenericQuery(session, queryBuilder, queryParams);
		}
		LOG.debug("DownloadLinkListModel: findTagAssets Method Ended");
		return hitList;
	}
	
	/**
	 * @param asset
	 * @param assetResource
	 * @return
	 */
	public DownloadLinkBean getAssetDetails(Asset damAsset, Resource assetResource) {

		DownloadLinkBean downloadLinkListBean = new DownloadLinkBean();
		final String assetPath = damAsset.getPath();
		if(!assetPath.isEmpty()) {
			//EAT-6090 Execute if the asset/Page has secure attribute
			if(SecureUtil.isSecureResource(assetResource) && checkSecureTagLink(assetPath)){
				downloadLinkListBean.setSecure(true);
				updateAssetList(assetResource, damAsset, downloadLinkListBean, assetPath);
			}
			//EAT-6090 Execute if the asset/Page do not have secure attribute
			if(!SecureUtil.isSecureResource(assetResource)){
				updateAssetList(assetResource, damAsset, downloadLinkListBean, assetPath);
			}
		}
		LOG.debug("DownloadLinkListModel: getAssetDetails Method Ended");
		return downloadLinkListBean;
	}

	private DownloadLinkBean updateAssetList(Resource assetResource, Asset damAsset, DownloadLinkBean downloadLinkBean, String assetPath){
		Resource jcrResourcePath = null;
		Resource assetResourceMetadata = assetResource.getChild(CommonConstants.JCR_CONTENT_METADATA);
		final String assetDescription = Optional.ofNullable(damAsset.getMetadataValue(DamConstants.DC_DESCRIPTION))
				.orElse(StringUtils.EMPTY);
		final String assetContentId = Optional.ofNullable(damAsset.getMetadataValue(CommonConstants.DC_PERCOLATE_CONTENT_ID))
				.orElse(StringUtils.EMPTY);

		downloadLinkBean.setAssetTitle(CommonUtil.getAssetTitle(assetPath, assetResource));
		if(StringUtils.isNotEmpty(assetContentId)) {
			downloadLinkBean.setAssetLink(assetPath+"?"+CommonConstants.ASSET_CONTENT_ID_PARAM+"="+assetContentId);
		}else {
			downloadLinkBean.setAssetLink(assetPath);
		}
		downloadLinkBean.setTrackDownload(EatonAssetUtils.trackThisAsset(assetResourceMetadata));
		downloadLinkBean.setAssetFileSize(CommonUtil.getAssetSize(damAsset));
		downloadLinkBean.setAssetFileType(CommonUtil.getType(damAsset));
		downloadLinkBean.setAssetDescription(assetDescription);
		if(assetResourceMetadata !=null) {
			jcrResourcePath = assetResourceMetadata.getParent();
		}
		if (assetResourceMetadata != null && jcrResourcePath != null) {
			Calendar assetReplicationDate = assetResourceMetadata.getValueMap().get(CommonConstants.ASSET_PUBLICATION_DATE, GregorianCalendar.class);
			if(null != assetReplicationDate){
				downloadLinkBean.setPublicationDate(assetReplicationDate.toString());
			}else {
				assetReplicationDate = jcrResourcePath.getValueMap().get(CommonConstants.JCR_MODIFIED, GregorianCalendar.class);
				if(null != assetReplicationDate){
					downloadLinkBean.setPublicationDate(assetReplicationDate.toString());
				}
			}
			if(assetReplicationDate != null){
				SimpleDateFormat defaultPublicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
				if(isUnitedStatesDateFormat) {
					defaultPublicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
				}
				final String assetPublicationDate = CommonUtil.format(assetReplicationDate, defaultPublicationDateFormat);
				downloadLinkBean.setAssetPublicationDate(assetPublicationDate);
			}
			Calendar assetModifiedDate = jcrResourcePath.getValueMap().get(CommonConstants.JCR_MODIFIED, GregorianCalendar.class);
			if(null != assetModifiedDate){
				downloadLinkBean.setLastModifiedDate(assetModifiedDate.toString());
			}
		}
		Calendar assetCreatedDate = assetResource.getValueMap().get(CommonConstants.JCR_CREATED, GregorianCalendar.class);
		if(null != assetCreatedDate){
			downloadLinkBean.setCreatedDate(assetCreatedDate.toString());
		}

		// EAT-7286 - ECCN & shaHash
		final String eccn = Optional.ofNullable(damAsset.getMetadataValue(CommonConstants.ECCN))
				.orElse(StringUtils.EMPTY);
		final String shaHash = Optional.ofNullable(damAsset.getMetadataValue(CommonConstants.SHA))
				.orElse(StringUtils.EMPTY);
        final String assetName = Optional.ofNullable(damAsset.getMetadataValue(AssetConstants.ASSET_NAME))
                .orElse(StringUtils.EMPTY);
		downloadLinkBean.setECCN(eccn);
		downloadLinkBean.setSHA(shaHash);
        downloadLinkBean.setEatonFileName(assetName);
		return null;
	}
	/**
	 * this method is sorting the list based on the author selection.
	 *
	 * @param list the list
	 * @return the sorted list
	 */
	private void getSortedList(List<DownloadLinkBean> list) {
		Collections.sort(list, new Comparator<DownloadLinkBean>() {
			public int compare(DownloadLinkBean linkList1,
					DownloadLinkBean linkList2) {
				int comparisonValue = 0;
				switch (sortBy) {
					case CommonConstants.TITLE:
						if (linkList1.getAssetTitle() != null && linkList2.getAssetTitle() != null) {
							comparisonValue = linkList1.getAssetTitle().compareToIgnoreCase(linkList2.getAssetTitle());
						}
						break;
					case CommonConstants.PUBLISH_DATE:
						if (linkList1.getPublicationDate() != null && linkList2.getPublicationDate() != null) {
							comparisonValue = -1* (linkList1.getPublicationDate().compareTo(linkList2.getPublicationDate()));
						}
						break;
					case CommonConstants.CREATED_DT:
						if (linkList1.getCreatedDate() != null && linkList2.getCreatedDate() != null) {
							comparisonValue = -1* (linkList1.getCreatedDate().compareTo(linkList2.getCreatedDate()));
						}
						break;
					case CommonConstants.LAST_MOD_DT:
						if (linkList1.getLastModifiedDate() != null && linkList2.getLastModifiedDate() != null) {
							comparisonValue = -1* (linkList1.getLastModifiedDate().compareTo(linkList2.getLastModifiedDate()));
						}
						break;
					default:
						comparisonValue = 0;
				}
				return comparisonValue;
			}
		});
	}
	
	/**
	 * Return the asset file type to match.
	 *
	 */
	public enum assetFileType {
		
		APPLICATION_PDF("application/pdf"),
		APPLICATION_XLS("application/vnd.ms-excel"),
		APPLICATION_XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
		APPLICATION_CSV("text/csv"),
		APPLICATION_MDB("application/x-msaccess"),
		APPLICATION_DOC("application/msword"),
		APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
		APPLICATION_PPT("application/vnd.ms-powerpoint"),
		APPLICATION_PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
		APPLICATION_ZIP("application/zip"),
		APPLICATION_7Z("application/x-7z-compressed"),
		APPLICATION_RAR("application/x-rar-compressed");

		private String fileType = StringUtils.EMPTY;

		private assetFileType(final String fileType){
			this.fileType = fileType;
		}
		public String getFileType() {
			return fileType;
		}
	}

	/**
	 * Gets the links.
	 *
	 * @return the links
	 */
	public List<FixedLinksModel> getDownloadLink() {
		return downloadLink;
	}

	/**
	 * Gets the download linksheader.
	 *
	 * @return the download linksheader
	 */
	public String getDownloadLinksheader() {
		return downloadLinksheader;
	}

	/**
	 * Gets the enable inner grid.
	 *
	 * @return the enable inner grid
	 */
	public String getEnableInnerGrid() {
		String isInnerGrid;
		if(null != enableInnerGrid) {
			isInnerGrid = "true";
		} else {
			isInnerGrid = "false";
		}
		return isInnerGrid;
	}

	/** Gets the list type.
	 *
	 * @return the
	 */
	public String getListType() {
		if(null == listType) {
			listType = StringUtils.EMPTY;
		}
		return listType;
	}

	/**
	 * Gets the parentPage.
	 *
	 * @return the parentPage
	 */

	public String getParentDamPath() {
		return parentDamPath;
	}

	/**
	 * Gets the tags.
	 *
	 * @return the tags
	 */

	public String[] getTags() {
		return tags;
	}
	/**
	 * Gets the sort.
	 *
	 * @return the sort
	 */
	public String getSortBy() {
		return sortBy;
	}

	/**
	 * Gets the new window.
	 *
	 * @return the openNewWindow
	 */

	public String getNewWindow() {
		return openNewWindow;
	}

	/**
	 * Gets the max limit to display.
	 *
	 * @return the maxLimitToDisplay
	 */
	public int getMaxLimitToDisplay() {
		return maxLimitToDisplay;
	}

	private boolean checkSecureTagLink(String assetPath){
		if(assetPath.contains("?"+CommonConstants.ASSET_CONTENT_ID_PARAM)){
			assetPath = assetPath.split("\\?assetID") !=null ? assetPath.split("\\?assetID")[0] : assetPath;
		}
		return SecureUtil.isAuthorisedLink(resource, assetPath, authorizationService, slingRequest);
	}

	private void updateSecureFilter(FixedLinksModel downloadListFields, List<FixedLinksModel> downloadLists){
		if(downloadListFields != null && StringUtils.isNotEmpty(downloadListFields.getDownloadLinkPath())) {
			String assetPath = downloadListFields.getDownloadLinkPath();
			if (SecureUtil.isSecureResource(downloadListFields.getAssetResource()) && checkSecureTagLink(assetPath)) {
				downloadListFields.setSecure(true);
				Resource assetResourceMetadata = downloadListFields.getAssetResource().getChild(CommonConstants.JCR_CONTENT_METADATA);
				downloadListFields.setTrackDownload(EatonAssetUtils.trackThisAsset(assetResourceMetadata));
				downloadLists.add(downloadListFields);
			}
			if(!SecureUtil.isSecureResource(downloadListFields.getAssetResource())) {
				downloadLists.add(downloadListFields);
			}
		}
	}
}
