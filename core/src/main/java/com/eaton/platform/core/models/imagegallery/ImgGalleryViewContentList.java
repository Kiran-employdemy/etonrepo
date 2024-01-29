package com.eaton.platform.core.models.imagegallery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import com.day.cq.commons.RangeIterator;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.handler.StandardImageHandler;
import com.day.cq.tagging.TagManager;
import com.eaton.platform.core.bean.ImageBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.models.annotations.Source;

/**
 * <html> Description: This Sling Model used in ImageGalleryHelper class.
 * </html>
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImgGalleryViewContentList {

	/** The manual links list. */
	private List<ManualLinksModel> manualLinksList;

	/** The fixed links list. */
	private List<FixedLinksModel> fixedLinksList;

	/** The child page links list. */
	private List<ImageBean> childImgAndTagsLinksList;

	/** The parent page. */
	@Inject
	private String parentPage;

	/** The tags. */
	@Inject
	private String tags;

	/** The fixed links. */
	@Inject
	private Resource fixedLinks;

	/** The manual links. */
	@Inject
	private Resource manualLinks;

	/** The limit. */
	@Inject
	private int limit;

	/** The sort. */
	@Inject
	private String sort;

	/** The gallery headline. */
	@Inject
	private String galleryHeadline;

	/** The gallery desc. */
	@Inject
	private String galleryDesc;

	/** The list type. */
	@Inject
	private String listType;
	
	/** The desktop trans. */
	@Inject
	private String desktopTrans;
	
	/** The thumbnail trans. */
	@Inject
	private String thumbnailTrans;

	/**
	 * The resource page.
	 */
	@Inject
	private Page resourcePage;
	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		switch (getListType()) {
			case CommonConstants.CHILD_PAGES: {
				childImgAndTagsLinksList = new ArrayList<>();
				if (null != parentPage) {
					populateParentModel(childImgAndTagsLinksList, parentPage);
					// sort the list when author has either selected Parent Page or
					// Tag
					getSortedList(childImgAndTagsLinksList);
				}
				break;
			}
			case CommonConstants.TAGS: {
				childImgAndTagsLinksList = new ArrayList<>();
				if (null != tags) {
					populateTagsModel(childImgAndTagsLinksList, tags, resourceResolver);
					// sort the list when author has either selected Parent Page or
					// Tag
					getSortedList(childImgAndTagsLinksList);
				}
				break;
			}
			case CommonConstants.FIXED_LIST: {
				fixedLinksList = new ArrayList<>();
				if (null != fixedLinks) {
					populateFixedLinkModel(fixedLinksList, fixedLinks);
				}
				break;
			}
			case CommonConstants.MANUAL_LIST: {
				manualLinksList = new ArrayList<>();
				if (null != manualLinks) {
					populateManualLinkModel(manualLinksList, manualLinks);
				}
				break;
			}
			default: {
				break;
			}
		}
	}


	/**
	 * Populate parent model.
	 *
	 * @param childImgAndTagsLinksList the child img and tags links list
	 * @param parentPage the parent page
	 * @return the list
	 */
	public List<ImageBean> populateParentModel(List<ImageBean> childImgAndTagsLinksList, String parentPage) {
		if (StringUtils.equals(CommonConstants.CHILD_PAGES, getListType()) && (null != parentPage)) {
				Resource res = resourceResolver.getResource(parentPage);
				Iterator<Resource> childImagesRes = null;
				if (res != null) {
					childImagesRes = res.listChildren();
				}
				if (childImagesRes != null) {
					while (childImagesRes.hasNext()) {
						ImageBean imageBean = new ImageBean();
						Resource assetResource = childImagesRes.next();
						if (null != assetResource) {
							ValueMap assetValueMap = assetResource.getValueMap();
								String assetType = CommonUtil.getStringProperty(assetValueMap, CommonConstants.JCR_PRIMARY_TYPE);
								if (StringUtils.equals(assetType, DamConstants.NT_DAM_ASSET)
										&& childImgAndTagsLinksList.size() < getLimit()) {
									Resource jcrResource = assetResource.getChild(CommonConstants.JCR_CONTENT_STR);
									if (null != jcrResource) {
										ValueMap jcrValueMap = jcrResource.getValueMap();
											Resource metadata = jcrResource.getChild(DamConstants.METADATA_FOLDER);
											if (null != metadata) {
												ValueMap metadataValueMap = metadata.getValueMap();
												if (CommonUtil.getStringProperty(metadataValueMap, DamConstants.DC_FORMAT).equalsIgnoreCase(DamConstants.THUMBNAIL_MIMETYPE)
														|| CommonUtil.getStringProperty(metadataValueMap, DamConstants.DC_FORMAT).equalsIgnoreCase(StandardImageHandler.JPEG_MIMETYPE)) {
													imageBean.setImgPathDownload(assetResource.getPath());
													imageBean.setImgPathPreview(getDesktopTrans());
													imageBean.setImgPathZoom(getDesktopTrans());
													imageBean.setImgPathThumbnail(getThumbnailTrans());
													imageBean.setAltText(CommonUtil.getAssetAltText(resourceResolver, imageBean.getImgPathDownload()));
													imageBean.setCreatedDate(CommonUtil.getDateProperty(assetValueMap, CommonConstants.JCR_CREATED).toString());
													imageBean.setLastModifiedDate(CommonUtil.getDateProperty(jcrValueMap, CommonConstants.JCR_MODIFIED).toString());
													imageBean.setTitle(CommonUtil.getStringProperty(metadataValueMap, DamConstants.DC_TITLE));
													imageBean.setDescription(CommonUtil.getStringProperty(metadataValueMap, DamConstants.DC_DESCRIPTION));
													imageBean.setIsVideo(true);
													childImgAndTagsLinksList.add(imageBean);
											}
										}
								}
							}
						}
					}
				}
		}
		return childImgAndTagsLinksList;
	}

	/**
	 * Populate tags model.
	 *
	 * @param childPageAndTagsLinksList
	 *            the child page and tags links list
	 * @param tags
	 *            the tags
	 * @param resourceResolver
	 * @return the list
	 */
	public List<ImageBean> populateTagsModel(List<ImageBean> childPageAndTagsLinksList, String tags, ResourceResolver resourceResolver) {
		if (StringUtils.equals(CommonConstants.TAGS, getListType())) {
			TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
			RangeIterator<Resource> tagsIterator = null;
			if(null != tagManager){
				tagsIterator = tagManager.find(tags);
			}
			if (null != tagsIterator) {
				while (tagsIterator.hasNext()) {
					ImageBean imageBean = new ImageBean();
					Resource tagResource =  tagsIterator.next();
					if (null != tagResource	&& StringUtils.startsWith(tagResource.getPath(), CommonConstants.CONTENT_DAM)) {
						String imgPath = StringUtils.substringBeforeLast(tagResource.getPath(), "/jcr:content");
						imageBean.setImgPathDownload(imgPath);
						imageBean.setImgPathPreview(getDesktopTrans());
						imageBean.setImgPathZoom(getDesktopTrans());
						imageBean.setImgPathThumbnail(getThumbnailTrans());
						imageBean.setAltText(CommonUtil.getAssetAltText(resourceResolver, imgPath));
						Resource imageResource = resourceResolver.getResource(imgPath);
						if (null != imageResource) {
							ValueMap imageValueMap = imageResource.getValueMap();
								imageBean.setCreatedDate(CommonUtil.getDateProperty(imageValueMap, CommonConstants.JCR_CREATED).toString());
							Resource jcrResource = imageResource.getChild(CommonConstants.JCR_CONTENT_STR);
							if (null != jcrResource) {
								ValueMap jcrValueMap = jcrResource.getValueMap();
								Resource metadata = jcrResource.getChild(DamConstants.METADATA_FOLDER);
								if (null != metadata) {
									ValueMap metadataValueMap = metadata.getValueMap();
									imageBean.setDescription(CommonUtil.getStringProperty(metadataValueMap, DamConstants.DC_DESCRIPTION));
								}
								Calendar lastModified = CommonUtil.getDateProperty(jcrValueMap, CommonConstants.JCR_MODIFIED);
								if(null != lastModified) {
									imageBean.setLastModifiedDate(lastModified.toString());
								}
							}
						}
						childPageAndTagsLinksList.add(imageBean);

					}
				}
			}
		}
		return childPageAndTagsLinksList;
	}

	/**
	 * Populate manual link model.
	 *
	 * @param manualLinksList
	 *            the manual links list
	 * @param resource
	 *            the resource
	 * @return the list
	 */
	public List<ManualLinksModel> populateManualLinkModel(List<ManualLinksModel> manualLinksList, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				ManualLinksModel manualLink = linkResources.next().adaptTo(ManualLinksModel.class);
				if (null != manualLink) {
					manualLink.setImgPathDownload(manualLink.getImgPath());
					manualLink.setImgPathPreview(getDesktopTrans());
					manualLink.setImgPathZoom(getDesktopTrans());
					manualLink.setImgPathThumbnail(getThumbnailTrans());
					manualLinksList.add(manualLink);
				}
			}
		}
		return manualLinksList;
	}

	/**
	 * Populate fixed link model.
	 *
	 * @param fixedLinksList
	 *            the fixed links list
	 * @param resource
	 *            the resource
	 * @return the list
	 */
	public List<FixedLinksModel> populateFixedLinkModel(List<FixedLinksModel> fixedLinksList, Resource resource) {
		if (null != resource) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				FixedLinksModel fixedLink = linkResources.next().adaptTo(FixedLinksModel.class);
				if (null != fixedLink) {
					fixedLink.setImgPathDownload(fixedLink.getImgPath());
					fixedLink.setImgPathPreview(getDesktopTrans());
					fixedLink.setImgPathZoom(getDesktopTrans());
					fixedLink.setImgPathThumbnail(getThumbnailTrans());
					fixedLinksList.add(fixedLink);
				}
			}
		}
		return fixedLinksList;
	}

	/**
	 * this method is sorting the list based on the author selection.
	 *
	 * @param list
	 *            the list
	 * @return the sorted list
	 */
	private void getSortedList(List<ImageBean> list) {
		Collections.sort(list,new Comparator<ImageBean>() {
			public int compare(ImageBean linkList1, ImageBean linkList2) {
				int comparisonValue = 0;
				switch (sort) {
				case CommonConstants.CREATED_DT: {
					comparisonValue = -linkList1.getCreatedDate().compareTo(linkList2.getCreatedDate());
					break;
				}
				case CommonConstants.LAST_MOD_DT: {
					comparisonValue = -linkList1.getLastModifiedDate().compareTo(linkList2.getLastModifiedDate());
					break;
				}
				default: {
					comparisonValue = 0;
				}
				}
				return comparisonValue;
			}
		});
	}

	/**
	 * Gets the list type array.
	 *
	 * @return the list type array
	 */
	public List<?> getListTypeArray() {
		List<?> viewList = new ArrayList<>();
		if (StringUtils.equals(CommonConstants.CHILD_PAGES, getListType())
				|| StringUtils.equals(CommonConstants.TAGS, getListType())) {
			viewList = childImgAndTagsLinksList;
		} else if (StringUtils.equals(CommonConstants.FIXED_LIST, getListType())) {
			viewList = fixedLinksList;
		} else if (StringUtils.equals(CommonConstants.MANUAL_LIST, getListType())) {
			viewList = manualLinksList;
		}
		return viewList;
	}

	/**
	 * Gets the limit.
	 *
	 * @return the limit
	 */
	public int getLimit() {
		if (limit == 0) {
			limit = Integer.MAX_VALUE;
		}
		return limit;
	}

	/**
	 * Gets the gallery headline.
	 *
	 * @return the gallery headline
	 */
	public String getGalleryHeadline() {
		return galleryHeadline;
	}

	/**
	 * Gets the gallery desc.
	 *
	 * @return the gallery desc
	 */
	public String getGalleryDesc() {
		return galleryDesc;
	}

	/**
	 * Gets the list type.
	 *
	 * @return the list type
	 */
	public String getListType() {
		String listTypeStruc = this.listType;
		if (null == listTypeStruc) {
			listTypeStruc = StringUtils.EMPTY;
		}
		return listTypeStruc;
	}

	/**
	 * Gets the desktop trans.
	 *
	 * @return the desktop trans
	 */
	public String getDesktopTrans() {
		if (null == desktopTrans) {
			desktopTrans = StringUtils.EMPTY;
		}
		return desktopTrans;
	}

	/**
	 * Gets the thumbnail trans.
	 *
	 * @return the thumbnail trans
	 */
	public String getThumbnailTrans() {
		if (null == thumbnailTrans) {
			thumbnailTrans = StringUtils.EMPTY;
		}
		return thumbnailTrans;
	}

}
