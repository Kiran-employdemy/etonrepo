package com.eaton.platform.core.models;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.XFUtil;

import java.util.Optional;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.models.annotations.Default;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.experiencefragment.ExperienceFragmentLinkTransformerModel;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class CTAModel.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CTAModel extends ExperienceFragmentLinkTransformerModel {

	@Inject
	private Page page;
	
	/** The trans CTA label. */
	@Inject  @Via("resource")
	private String  label;

	/** The cta link. */
	@Inject  @Via("resource")
	private String link;
	
	/** The new window. */
	@Inject  @Via("resource") @Default(values = "false")
	private String newWindow;

	/** The modal choice. */
	@Inject  @Via("resource") @Default(values = "false")
	private String modal;

	/** The Source tracking javascript. */
	@Inject  @Via("resource") @Default(values = "false")
	private String enableSourceTracking;
	
	/** The noindex,nofollow tag. */
	@Inject  @Via("resource") @Default(values = "false")
	private String applyNoFollowTag;
	
	/**
	 * Gets the cta link.
	 *
	 * @return ctaLink
	 */
	@Inject @Via("resource")
	private String color;

	/** Align Right. */
	@Inject @Via("resource") @Default(values = "false")
	private String alignRight;
	
	@Inject @Source("sling-object")
	private ResourceResolver resourceResolver;

	
	String tag;

	private String localeSpecificLink = null;

	private static final Logger LOGGER = LoggerFactory.getLogger(CTAModel.class);
	/**
	 * Inits the.
	 */
	@PostConstruct
	protected void init() {
		String assetContentId = getAssetContentId();
		if (Boolean.TRUE.equals(this.getIsComponentInXS())) {
			localeSpecificLink = XFUtil.updateHTMLLinkInXF(page, resourceResolver,
					link);
		}
		if (StringUtils.isNotEmpty(assetContentId)) {
			link = localeSpecificLink != null ? (CommonUtil.dotHtmlLink(localeSpecificLink) + CommonConstants.QUESTION_MARK_CHAR
					+ (CommonConstants.ASSET_CONTENT_ID_PARAM + CommonConstants.EQUALS_SYMBOL + assetContentId)) :
					(CommonUtil.dotHtmlLink(link) + CommonConstants.QUESTION_MARK_CHAR
					+ (CommonConstants.ASSET_CONTENT_ID_PARAM + CommonConstants.EQUALS_SYMBOL + assetContentId));
		} else {
			
			link = localeSpecificLink != null ? CommonUtil.dotHtmlLink(localeSpecificLink)
					: CommonUtil.dotHtmlLink(link);
		}
		if (applyNoFollowTag.equalsIgnoreCase("true")) {
			tag = "nofollow";
		} else {
			tag = "follow";
		}
	}

	private String getAssetContentId() {
		String assetContentId = StringUtils.EMPTY;
		try {
			if(link!=null) {
			Resource assetResource = resourceResolver.getResource(URLDecoder.decode(link, CommonConstants.UTF_8));
			Asset asset = CommonUtil.getAsset(assetResource);
			if (asset != null) {
				assetContentId = Optional.ofNullable(
						CommonUtil.getAssetMetadataValue(CommonConstants.DC_PERCOLATE_CONTENT_ID, assetResource))
						.orElse(StringUtils.EMPTY);
			}
		} 
		}catch (UnsupportedEncodingException e) {
			LOGGER.error(String.format("Repository Exception in CTAModel", e.getMessage()));
		}
		return assetContentId;
	}

	public String getLink() {
		return link;
	}

	/**
	 * Gets the CTA label.
	 *
	 * @return label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Gets the new window.
	 *
	 * @return newWindow
	 */
	public String getNewWindow(){
		return newWindow;
	}

	/**
	 * Gets the modal.
	 *
	 * @return modal
	 */
	public String getModal(){
		return modal;
	}
	
	/**
	 * Gets the color.
	 *
	 * @return color
	 */
	public String getColor(){
		return color;
	}

	/**
	 * Align Right.
	 *
	 * @return alignRight
	 */
	public String getAlignRight() {
		return alignRight;
	}
	
	public String getApplyNoFollowTag() {
		applyNoFollowTag = tag;
		return applyNoFollowTag;
	}

	public String getEnableSourceTracking() {
		return enableSourceTracking;
	}
}
