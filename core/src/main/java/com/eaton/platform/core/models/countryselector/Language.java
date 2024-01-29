package com.eaton.platform.core.models.countryselector;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.Locale;

/**
 * The Class Language.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Language{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1409656172706414693L;

    /** The languagename. */
    @Inject
    private String languageName;

    /** The countryname. */
    @Inject
    private String countryName;
    
    /** The languagepath. */
    @Inject
    private String languagePath;
    
    @Inject
    private String languageCountryCode;
    
    @Inject
    private String countryCode;
    
    @Inject
    private String languageCode;
    
    @OSGiService
    protected AdminService adminService;
    
    private static final Logger LOG = LoggerFactory.getLogger(Language.class);
 
    @PostConstruct
    protected void init() {
    	  if(null != adminService) {
              try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
            	  if(adminResourceResolver !=null) {
            	  Page languageHomePage;	
            	  String configuredLanguagePath=getLanguagePath();
	            	  if(configuredLanguagePath != null ) {
		            	  if(configuredLanguagePath.contains(CommonConstants.HTML_EXTN)) {
		            		  configuredLanguagePath = configuredLanguagePath.replaceAll(CommonConstants.HTML_EXTN, ""); 
		            	  }
		            	  final Resource pageResource = adminResourceResolver.getResource(configuredLanguagePath);
		            	  if(pageResource !=null) {
		            	  languageHomePage = pageResource.adaptTo(Page.class); 
		            		  final Locale locale = CommonUtil.getLocaleFromPagePath(languageHomePage);	
		            		  if(locale !=null) {
			            		  setLanguageCountryCode(locale.toString());
			            		  if(locale.getCountry() !=null) {
			            			  setCountryCode(locale.getCountry());
			            		  }
			            		  if(locale.getLanguage() !=null) {
			            			  setLanguageCode(locale.getLanguage());
			            		  }
		            		  }
		            	 	}
	            	  }
            	 }
              }catch (Exception exception) {
      			LOG.error("Exception occured while getting the reader service", exception);
      		}
    	  }
    }
    
    
    /**
     * Sets the languageName.
     *
     * @param languageName the new languagename
     */
    public void setLanguageName(String languageName) {
        this.languageName = languageName;
     }
     
     /**
      * Gets the languageName.
      *
      * @return the languageName
      */
    public String getLanguageName() {
        return languageName;
     }

    /**
     * Sets the languagePath.
     *
     * @param languagePath the new languagePath
     */
    public void setLanguagePath(String languagePath) {
        this.languagePath = languagePath;
     }
     
     /**
      * Gets the languagePath.
      *
      * @return the languagePath
      */
    public String getLanguagePath() {
        return CommonUtil.dotHtmlLink(languagePath);
     }

    /**
     * Sets the countryName.
     *
     * @param countryName the new countryName
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * Gets the countryName.
     *
     * @return the countryName
     */
    public String getCountryName() {
        return countryName;
    }

	public String getLanguageCountryCode() {
		return languageCountryCode;
	}

	public void setLanguageCountryCode(String languageCountryCode) {
		this.languageCountryCode = languageCountryCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

}
