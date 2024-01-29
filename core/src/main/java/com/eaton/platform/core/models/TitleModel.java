package com.eaton.platform.core.models;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TitleModel {

    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(TitleModel.class);

    @Inject @Via("resource") @Default(values = "default")
    private String view;

    @Inject @Via("resource")
    private String catLabel;

    @Inject @Via("resource")
    private String type;

    @Inject @Via("resource")
    private String catLink;

    @Inject @Via("resource")
    private String byLineText;

    @Inject @Via("resource")
    private Calendar byLineDate;

    @Inject @Via("resource")
    private String toggleInnerGrid;

    @Inject @Via("resource") @Named("jcr:title")
    private String title;

    @Inject @Source("sling-object")
    private ResourceResolver resourceResolver;

    @Inject @Via("resource")
    private String eyebrowLabel;

    @Inject @Via("resource")
    private String eyebrowLink;

    @Inject
    private Page currentPage;
    
    @EatonSiteConfigInjector
	private Optional<SiteResourceSlingModel> siteResourceSlingModel;
    
    private boolean isUnitedStatesDateFormat = false;

    @PostConstruct
    protected void init() {
        LOG.debug("TitleModel :: setComponentValues() :: Started");
    }

    public String getByLineDate() {
        String publicationDate = StringUtils.EMPTY;
        SimpleDateFormat publicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
		if (siteResourceSlingModel.isPresent()) {
			isUnitedStatesDateFormat = Boolean.parseBoolean(siteResourceSlingModel.get().getUnitedStatesDateFormat());
			if(isUnitedStatesDateFormat) {
				publicationDateFormat = new SimpleDateFormat(CommonConstants.UNITED_STATES_DATE_FORMAT_PUBLISH);
			}
		}
        if(null != byLineDate) {
        	publicationDateFormat.setCalendar(byLineDate);
            publicationDate = publicationDateFormat.format(byLineDate.getTime());
            return ", "+publicationDate;
        }else{
            return publicationDate;
        }

    }

    public String getTitle() {
        return title;
    }

    public String getByLineText() {
        return byLineText;
    }

    public String getToggleInnerGrid() {
        return toggleInnerGrid;
    }

    public String getType() {
        return type;
    }

    public String getView() {
        return view;
    }

    public String getCatLabel() {
        return CommonUtil.getLinkTitle(catLabel, catLink, resourceResolver);
    }

    public String getCatLink() {
        return catLink;
    }

    public String getEyebrowLabel() {
        return eyebrowLabel;
    }

    public String getEyebrowLink() {
        if (StringUtils.isNotBlank(eyebrowLink)) {
            return CommonUtil.dotHtmlLink(eyebrowLink);
        } else {
            return CommonUtil.dotHtmlLink(currentPage.getParent().getPath());
        }
    }

    public String getTitleLink() {
        String link = this.getCatLink() ;

        if (StringUtils.isNotBlank(link)) {
            link = this.getCatLink();
            return CommonUtil.dotHtmlLink(link);
        } else {
            link = currentPage.getParent().getPath();
            return CommonUtil.dotHtmlLink(link);
        }
    }

    public String getCurrentPageTitle() {
        if (currentPage.getPageTitle() != null) {
            return currentPage.getPageTitle();
        } else if(currentPage.getTitle() != null) {
            return currentPage.getTitle();
        } else {
            return null;
        }
    }
}