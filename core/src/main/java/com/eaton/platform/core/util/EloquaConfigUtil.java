package com.eaton.platform.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.EloquaCloudConfigModel;

import org.jsoup.nodes.Element;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Class EloquaConfigUtil.
 */
public final class EloquaConfigUtil {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EloquaConfigUtil.class);

    /** The Constant ELOQUA_CLOUD_CONFIG_NODE_NAME. */
    private static final String ELOQUA_CLOUD_CONFIG_NODE_NAME = "eloquaconfig";
    private static final String TYPE = "type";


    private EloquaConfigUtil() {
        LOGGER.info("Inside EloquaConfigUtil constructor");
    }

    /**
     * Gets the eloqua config details.
     *
     * @param eloquaConfigRes the eloqua config res
     * @return the eloqua config details
     */
    public static EloquaCloudConfigModel getEloquaConfigDetails(Resource eloquaConfigRes){
        LOGGER.info("Entered into EloquaConfigUtil getEloquaConfigDetails() method :::");
        EloquaCloudConfigModel eloquaCloudConfigModel = null;
        if(null != eloquaConfigRes){
            eloquaCloudConfigModel = eloquaConfigRes.adaptTo(EloquaCloudConfigModel.class);
        }
        LOGGER.info("Exited from EloquaConfigUtil getEloquaConfigDetails() method :::");
        return eloquaCloudConfigModel;

    }



    public static EloquaCloudConfigModel setUpEloquaServiceParameters(final ConfigurationManagerFactory configurationManagerFactory,
                                                                      final String contentPath,
                                                                      final ResourceResolver resourceResolver) {
        EloquaCloudConfigModel eloquaCloudConfigModel = null;
        if (StringUtils.isNotEmpty(contentPath)) {
            final Resource pageReesource = resourceResolver.getResource(contentPath);
            final Optional<Configuration> cloudConfigObjOptional = Optional.ofNullable(CommonUtil
                    .getCloudConfigObj(configurationManagerFactory,
                            resourceResolver, pageReesource, CommonConstants.ELOQUA_CLOUD_CONFIG_NODE_NAME));
            if (cloudConfigObjOptional.isPresent()) {
                final Configuration configuration = cloudConfigObjOptional.get();
                final String configContentNodePath = configuration.getContentResource().getPath();
                final Optional<Resource> configContenResourceOtional = Optional.ofNullable(resourceResolver
                        .getResource(configContentNodePath));
                if (configContenResourceOtional.isPresent()) {
                    eloquaCloudConfigModel = configContenResourceOtional.get()
                            .adaptTo(EloquaCloudConfigModel.class);
                }
            }
        }
        return eloquaCloudConfigModel;
    }


    /**
     * Gets the LOV from config.
     *
     * @param request the request
     * @param adminResourceResolver the admin resource resolver
     * @param configManagerFctry the config manager fctry
     * @param propertyName the property name
     * @return the LOV from config
     */
    public static String[] getLOVFromConfig(SlingHttpServletRequest request, ResourceResolver adminResourceResolver,
                                            ConfigurationManagerFactory configManagerFctry, String propertyName){
        LOGGER.info("Entered into EloquaConfigUtil getLOVValues() method :::");
        //local variables
        Page eloquaConfigPage = null;
        String[] propValArr = CommonConstants.EMPTY_ARRAY;

        // get refererURL from request since current page is not available in fixed path servlet
        String refererURL = CommonUtil.getRefererURL(request);
        //get content path
        String resourcePath = CommonUtil.getContentPath(adminResourceResolver, refererURL);
        Resource currentPageRes = adminResourceResolver.resolve(resourcePath);
        Configuration configObj = CommonUtil.getCloudConfigObj(configManagerFctry, adminResourceResolver, currentPageRes, ELOQUA_CLOUD_CONFIG_NODE_NAME);
        // if cloud config object is not null, get the details
        if(null != configObj){
            eloquaConfigPage = adminResourceResolver.resolve(configObj.getPath()).adaptTo(Page.class);
            // get Country LOV values configured in cloud config page
            if(eloquaConfigPage!=null)
                propValArr = eloquaConfigPage.getProperties().get(propertyName, String[].class);
        }
        LOGGER.info("Exited from EloquaConfigUtil getLOVValues() method :::");
        return propValArr;
    }


    public static List<Element> getFilteredElement(final String fieldType, Document htmlDoc) {
        final List<Element> collect = htmlDoc.getAllElements().stream()
                .filter(element -> element.attr(TYPE).equals(fieldType) ? true : false)
                .collect(Collectors.toList());

        return collect;
    }


}