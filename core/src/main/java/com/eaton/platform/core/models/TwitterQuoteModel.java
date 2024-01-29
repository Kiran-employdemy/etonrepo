package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


/**
 * Twitter Quote Model Class
 *
 * @author e9909320
 * @version 1.0
 * @since 2021
 */

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TwitterQuoteModel {

	private static final Logger LOG = LoggerFactory.getLogger(TwitterQuoteModel.class);

	private static final String WEB_INTENT_URL = "https://twitter.com/intent/tweet?";
	private static final String TEXT_PARAM_NAME = "text=";
	private static final String URL_PARAM_NAME = "&url=";

	@Inject @Via("resource") @Default(values= CommonConstants.DEFAULT)
	private String quoteView;

	@Inject @Via("resource")
	private String quoteText;

	@Inject @Via("resource") @Default(values = CommonConstants.TRUE)
	private boolean toggleInnerGrid;

	private String buttonLink;
	private String buttonText = "Click to tweet";
	private String buttonImage = "/content/dam/eaton/resources/icons/socialicons/twitter.png";
	private String buttonImageAlt = "Twitter icon";

	@Inject @Source("sling-object")
	private ResourceResolver resourceResolver;

	@Inject
	private Page currentPage;

	/**
	 * Init
	 */
	@PostConstruct
	public void init() {

		LOG.debug( "TwitterModel :: init() :: Started" );

		createWebIntentUri();

		LOG.debug( "TwitterModel :: init() :: Ended" );
	}

	private void setButtonLink(String buttonLink){
		this.buttonLink = buttonLink;
	}

	public String getButtonLink(){
		return buttonLink;
	}

	public String getQuoteView(){
		return quoteView;
	}

	public String getQuoteText() {
		return quoteText;
	}

	public String getButtonText() {
		return buttonText;
	}

	public String getButtonImage() {
		return buttonImage;
	}

	public String getButtonImageAlt() {
		return buttonImageAlt;
	}

	public boolean getToggleInnerGrid() {
		return toggleInnerGrid;
	}

	/**
	 * Create Twitter Web Intent URI for the anchor
	 */
	private void createWebIntentUri() {

		LOG.debug("TwitterModel :: createWebIntentUri() :: Started");

		String quoteTextEncoded = StringUtils.EMPTY;
		String currentPageUrl = StringUtils.EMPTY;
		String currentPageUrlEncoded = StringUtils.EMPTY;
		String webIntentUri = StringUtils.EMPTY;
		String utf8 = StandardCharsets.UTF_8.toString();

		try {
			// Externalize and encode the current page's url
			currentPageUrl = CommonUtil.dotHtmlLink( currentPage.getPath(), resourceResolver );
			currentPageUrlEncoded = CommonUtil.encode( currentPageUrl, utf8 );

			// Encode the quote's text
			quoteTextEncoded = CommonUtil.encode( getQuoteText(), utf8 );

			if ( StringUtils.isEmpty( quoteTextEncoded ) && StringUtils.isEmpty( currentPageUrlEncoded ) ) {
				throw new UnsupportedEncodingException("TwitterModel :: createWebIntentUri() :: quoteTextEncoded and/or currentPageUrlEncoded are blank after externalization and encoding.");
			}

			// Create web intent uri
			webIntentUri = WEB_INTENT_URL + TEXT_PARAM_NAME + quoteTextEncoded + URL_PARAM_NAME + currentPageUrlEncoded;

		} catch (Exception e) {

			// Set uri to fallback
			webIntentUri = StringUtils.EMPTY;

			LOG.error("TwitterModel :: createWebIntentUri() :: Twitter Quote Component had issue externalizing or encoding the text and url web intent parameter values. Returned fallback uri to component's link.", e);

		} finally {

			setButtonLink( webIntentUri );
		}

		LOG.debug("TwitterModel :: createWebIntentUri() :: Ended");

	}

}
