package com.eaton.platform.integration.cad.models;

import com.eaton.platform.integration.cad.services.CadFormatService;
import com.eaton.platform.integration.cad.services.CadenasUrlService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a Javadoc comment
 * DownloadCadDataModel class
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DownloadCadDataModel {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadCadDataModel.class);

    @Inject @Via("resource")
    private String articleNumbers;

    @Inject @Via("resource")
    private String buttonTitle;

    @OSGiService
    private CadFormatService cadFormatService;

    @OSGiService
    private CadenasUrlService cadUrlService;

    protected List<String> formatList = new ArrayList<String>();

    @PostConstruct
    protected void init() {
        LOG.debug("DownloadCadDataModel :: setComponentValues() :: Started");
        String xmlUrl = cadUrlService.getCadQualifierUrl();
        String formats = cadFormatService.readXMLFromURL(xmlUrl);

        if(!StringUtils.isEmpty(formats)) {
            formatList.add(formats);
        }
        LOG.debug("DownloadCadDataModel :: setComponentValues() :: Ended");
    }

    public String getButtonTitle() {
        return buttonTitle;
    }
    public String getArticleNumbers(){
        return articleNumbers;
    }

    public List<String> getFormatList() {
        return formatList;
    }
}
