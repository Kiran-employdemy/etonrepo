package com.eaton.platform.core.models.digital.v1;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;

import javax.inject.Inject;
import static com.eaton.platform.core.constants.CommonConstants.NOFOLLOWNOINDEX;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CTALink {

    /** The CTA link title */
    @Inject
    protected String ctaLinkTitle;

    /** The CTA link path */
    @Inject
    protected String ctaLinkPath;

    /** The new window check */
    @Inject
    private String newWindow;

    /** The link enabling source tracking */
    @Inject
    @Default(values = "false")
    private String enableSourceTracking;

    /** The link applying no follow */
    @Inject
    @Default(values = "false")
    private String applyNoFollowTag;

    @Inject
    @Source("sling-object")
    private ResourceResolver resourceResolver;
    
    /**
     * Gets the CTA link title
     * @return the CTA link title
     */
    public String getCtaLinkTitle(){ 
    	if(StringUtils.isNotBlank(ctaLinkTitle)){
			return ctaLinkTitle;
		}else {
			Page page = null != resourceResolver.adaptTo(PageManager.class) ? resourceResolver.adaptTo(PageManager.class).getPage(ctaLinkPath): null;
			if(page!=null)
				return StringUtils.isNotBlank(page.getNavigationTitle()) ? page.getNavigationTitle() : page.getTitle();
    	}
    	return StringUtils.EMPTY;
    }

    /**
     * Gets the CTA link path
     * @return the CTA link path
     */
    public String getCtaLinkPath(){ 
    	if (resourceResolver == null) {
            return ctaLinkPath;
            }
        
        return CommonUtil.dotHtmlLink(ctaLinkPath,resourceResolver);
    }

    /**
     * Gets the new window link
     * @return the new window link
     */
    public String getNewWindow(){
        return StringUtils.equals(CommonConstants.TRUE, newWindow) ? CommonConstants.TARGET_BLANK : CommonConstants.TARGET_SELF;
    }

    /**
     * Gets the source tracking check
     * @return- the boolean source tracking check
     */
    public String getEnableSourceTracking(){ return enableSourceTracking; }

    /**
     * Gets the apply no follow tag check
     * @return the now follow tag check
     */
    public String getApplyNoFollowTag(){ return applyNoFollowTag; }

    /**
     * Gets the CTA button target attribute
     * @return the target attribute
     */
    public String getTarget(){
        return getNewWindow();
    }

    /**
     * Gets the rel attribute value
     * @return the rel attribute
     */
    public String getRel(){
        return applyNoFollowTag.equalsIgnoreCase("true") ? NOFOLLOWNOINDEX : StringUtils.EMPTY;
    }

    /**
     * Gets the check if the link is external
     * @return the check determining whether the link is external
     */
    public Boolean getIsExternal(){
        boolean isExternal = false;
        if (null != ctaLinkPath && (StringUtils.startsWith(ctaLinkPath, CommonConstants.HTTP)
                || StringUtils.startsWith(ctaLinkPath,
                CommonConstants.HTTPS)
                || StringUtils.startsWith(ctaLinkPath,
                CommonConstants.WWW))) {
            isExternal = true;
        }
        return isExternal;
    }
    
}
