package com.eaton.platform.core.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.EloquaCloudConfigModel;
import com.eaton.platform.core.services.EloquaFormConfigService;
import com.eaton.platform.core.services.config.EloquaSandBoxPathConfig;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The Class ConfigServiceImpl.
 */
@Designate(ocd = EloquaSandBoxPathConfig.class)
@Component( service = EloquaFormConfigService.class,
        immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "EloquaFormConfigImpl",
                AEMConstants.PROCESS_LABEL + "EloquaFormConfigImpl"
        })
public class EloquaFormConfigImpl implements EloquaFormConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EatonConfigServiceImpl.class);
    private static final String ELOQUA_CLOUD_CONFIG_NODE_NAME = "eloquaconfig";
    private String eloquaCloudServicePath;

    @Activate
    @Modified
    protected void activate(final EloquaSandBoxPathConfig config) {
        this.eloquaCloudServicePath = config.eloqua_sandbox_cloud_service_path();
    }

    @Override
    public EloquaCloudConfigModel setUpEloquaServiceParameters(final ConfigurationManagerFactory configurationManagerFactory,
                                                               final String contentPath,
                                                               final ResourceResolver resourceResolver) {
        LOGGER.debug("Start setUpEloquaServiceParameters method");
        EloquaCloudConfigModel eloquaCloudConfigModel = null;
        Optional<Configuration> cloudConfigObjOptional = null;
        if (StringUtils.isNotEmpty(contentPath)) {
            final Resource pageResource = resourceResolver.getResource(contentPath);
            cloudConfigObjOptional = Optional.ofNullable(CommonUtil
                    .getCloudConfigObj(configurationManagerFactory,
                            resourceResolver, pageResource, CommonConstants.ELOQUA_CLOUD_CONFIG_NODE_NAME));
        }else {
            if(null != eloquaCloudServicePath) {
                String[] services = {eloquaCloudServicePath};
                ConfigurationManager configMgr = configurationManagerFactory.getConfigurationManager(resourceResolver);
                cloudConfigObjOptional = Optional.ofNullable(configMgr.getConfiguration(CommonConstants.ELOQUA_CLOUD_CONFIG_NODE_NAME, services));
            }
        }
            if (null != cloudConfigObjOptional  && cloudConfigObjOptional.isPresent()) {
                final Configuration configuration = cloudConfigObjOptional.get();
                final String configContentNodePath = configuration.getContentResource().getPath();
                final Optional<Resource> configContenResourceOptional = Optional.ofNullable(resourceResolver
                        .getResource(configContentNodePath));
                if (configContenResourceOptional.isPresent()) {
                    eloquaCloudConfigModel = configContenResourceOptional.get()
                            .adaptTo(EloquaCloudConfigModel.class);
                }
            }

        LOGGER.debug("End setUpEloquaServiceParameters method");
        return eloquaCloudConfigModel;
    }

    @Override
    public String[] getLOVFromConfig(final SlingHttpServletRequest request, final ResourceResolver adminResourceResolver,
                                     final ConfigurationManagerFactory configManagerFctry, final String propertyName) {
        //local variables
        Page eloquaConfigPage = null;
        String[] propValArr = CommonConstants.EMPTY_ARRAY;

        // get refererURL from request since current page is not available in fixed path servlet
        String refererURL = CommonUtil.getRefererURL(request);
        //get content path
        String resourcePath = CommonUtil.getContentPath(adminResourceResolver, refererURL);
        Resource currentPageRes = adminResourceResolver.resolve(resourcePath);
        Configuration configObj = CommonUtil.getCloudConfigObj(configManagerFctry, adminResourceResolver,
                currentPageRes, ELOQUA_CLOUD_CONFIG_NODE_NAME);
        // if cloud config object is not null, get the details
        if(null != configObj){
            eloquaConfigPage = adminResourceResolver.resolve(configObj.getPath()).adaptTo(Page.class);
            // get Country LOV values configured in cloud config page
            if(eloquaConfigPage!=null)
                propValArr = eloquaConfigPage.getProperties().get(propertyName, String[].class);
        }
        return propValArr;
    }

    @Override
    public EloquaCloudConfigModel getEloquaConfigDetails(final Resource eloquaConfigRes) {
        EloquaCloudConfigModel eloquaCloudConfigModel = null;
        if(null != eloquaConfigRes){
            eloquaCloudConfigModel = eloquaConfigRes.adaptTo(EloquaCloudConfigModel.class);
        }
        return eloquaCloudConfigModel;
    }
}
