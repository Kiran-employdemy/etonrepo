package com.eaton.platform.core.models.secure;


import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Locale;


/**
 * What's New Model to inject basic properties
 *
 * @author ICF
 * @version 1.0s
 * @since 2021
 *
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WhatsNewModel {
    @Inject
    protected Page currentPage;

    /** The country. */
    private String country;

    /** The country. */
    private String language;

    @PostConstruct
    private void init(){
        country = CommonUtil.getCountryFromPagePath(currentPage);
        Locale languageValue =  CommonUtil.getLocaleFromPagePath(currentPage);
        if (languageValue != null && ((languageValue.getLanguage() != null) && (!languageValue.getLanguage().equals(StringUtils.EMPTY)))) {
            language = languageValue.getLanguage();
        }
    }

    /**
     *
     * @return Page
     */
    public Page getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Page currentPage) {
        this.currentPage = currentPage;
    }

    /**
     *
     * @return Country
     */
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return Language
     */
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
