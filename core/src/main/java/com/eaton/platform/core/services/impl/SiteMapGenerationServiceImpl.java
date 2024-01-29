package com.eaton.platform.core.services.impl;


import com.eaton.platform.core.services.SiteMapGenerationService;
import com.eaton.platform.core.services.config.SiteMapGenerationServiceConfig;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

import java.util.Arrays;
import java.util.List;

/**
 * The Class SiteMapGenerationConfigServiceImpl.
 */
@Designate(ocd = SiteMapGenerationServiceConfig.class)
@Component(service = SiteMapGenerationService.class, immediate = true,
        configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class SiteMapGenerationServiceImpl implements SiteMapGenerationService {

    private String[] resourseTypes;

    private boolean includeInheritValue;

    private boolean includeLastModified;

    private String[] changefreqProperties;

    private String[] priorityProperties;

    private String damAssetProperty;

    private String[] damAssetTypes;

    private String externalExcludeProperty;

    private String internalExcludeProperty;

    private String characterEncoding;

    private boolean extensionlessUrls;

    private boolean removeTrailingSlash;


    @Activate
    @Modified
    protected void activate(final SiteMapGenerationServiceConfig config) {
        this.resourseTypes = config.slingServletResourceTypes();
        this.includeInheritValue = config.includeInherit();
        this.includeLastModified = config.includeLastmod();
        this.changefreqProperties = config.changefreqProperties();
        this.priorityProperties = config.priorityProperties();
        this.damAssetProperty = config.damasssetsProperty();
        this.damAssetTypes = config.damassetTypes();
        this.externalExcludeProperty = config.externalExcludeProperty();
        this.internalExcludeProperty = config.internalExcludeProperty();
        this.characterEncoding = config.characterEncoding();
        this.extensionlessUrls = config.extensionlessUrls();
        this.removeTrailingSlash = config.removeSlash();
    }

    @Override
    public String[] getResourceTypes(){
        return resourseTypes;
    }

    @Override
    public boolean getIncludeInheritValue(){
        return includeInheritValue;
    }

    @Override
    public boolean getIncludeLastModified(){
        return includeLastModified;
    }

    @Override
    public String[] getChangefreqProperties(){
        return changefreqProperties;
    }

    @Override
    public String[] getPriorityProperties(){
        return priorityProperties;
    }

    @Override
    public String getDamAssetProperty(){
        return damAssetProperty;
    }

    @Override
    public List<String> getDamAssetTypes(){
        return Arrays.asList(damAssetTypes);
    }

    @Override
    public String getExternalExcludeProperty(){
        return externalExcludeProperty;
    }

    @Override
    public String getInternalExcludeProperty(){
        return internalExcludeProperty;
    }

    @Override
    public String getCharacterEncoding(){
        return characterEncoding;
    }

    @Override
    public boolean getExtensionlessUrls(){
        return extensionlessUrls;
    }

    @Override
    public boolean getRemoveTrailingSlash(){
        return removeTrailingSlash;
    }

}
