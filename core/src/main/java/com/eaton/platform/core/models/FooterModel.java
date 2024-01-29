package com.eaton.platform.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Source;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import java.util.Calendar;
import java.util.Locale;

import com.day.cq.i18n.I18n;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <html> Description: This class is used to inject the dialog properties.</html> .
 *
 * @author TCS
 * @version 1.0
 * @since 2017
 */
@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class FooterModel {
	private static final String HOMEPAGE_FOOTER_COMPONENT_PATH = "/jcr:content/root/footer";
	private static final String PROPERTY_ENABLE_BACK_TO_TOP = "enableBackToTop";
	private static final String PROPERTY_COPYRIGHT = "copyright";
	private static final String COPYRIGHT_ICON = "\u00a9";

	 /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FooterModel.class);
    
    @Inject
    private Page resourcePage;

    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

	@Inject
	@Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
	private ResourceBundleProvider i18nProvider;

	private Resource footerResource;
	private I18n i18n;

	/** The enableBackToTopHelper. */
	private String enableBackToTopHelper;
	
	@PostConstruct
	protected void init() {
		
		LOG.debug("FooterModel :: init() :: Started");
		
		if (i18nProvider != null) {
			i18n = new I18n(i18nProvider.getResourceBundle(resourcePage.getLanguage(false)));
		}
		String homePagePath = CommonUtil.getHomePagePath(resourcePage);
		String footerResPath = null;
		if(StringUtils.isNotBlank(homePagePath)){
			footerResPath = homePagePath.concat(HOMEPAGE_FOOTER_COMPONENT_PATH);
		} 
		if(null != footerResPath) {
			footerResource  = resourceResolver.getResource(footerResPath);

			if(footerResource!= null) {
                enableBackToTopHelper = getEnableBackToTop();
			}
		}
		LOG.debug(" FooterModel :: int() :: Exit");
	}

	public String getEnableBackToTop() {
		if (footerResource != null && footerResource.getValueMap().get(PROPERTY_ENABLE_BACK_TO_TOP) != null) {
			return footerResource.getValueMap().get(PROPERTY_ENABLE_BACK_TO_TOP).toString();
		} else {
			return null;
		}
	}

	public String getCopyrightYear() {
		return COPYRIGHT_ICON + CommonConstants.BLANK_SPACE + Calendar.getInstance().get(Calendar.YEAR);
	}

	public String getCopyrightText() {
		return footerResource != null ? footerResource.getValueMap().get(PROPERTY_COPYRIGHT, getDefaultCopyright()) : "";
	}

	public String getDefaultCopyright() {
	    return i18n != null ? i18n.get(PROPERTY_COPYRIGHT) : "";
	}

	public String getDefaultLocaleCopyright() {
		i18n = new I18n(i18nProvider.getResourceBundle(Locale.US));
		return  i18n.get(PROPERTY_COPYRIGHT);
	}
	
	/**
	 * Gets the enable back to top helper.
	 *
	 * @return the enable back to top helper
	 */
	public String getEnableBackToTopHelper() {
		return enableBackToTopHelper;
	}
}
