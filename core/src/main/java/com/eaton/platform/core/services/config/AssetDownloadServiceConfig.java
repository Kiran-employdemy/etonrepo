package com.eaton.platform.core.services.config;

import com.eaton.platform.core.constants.CommonConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Eaton AssetDownloadService ")
public @interface AssetDownloadServiceConfig {

    @AttributeDefinition(
            name = "Max Allowed Download Package Size before compression",
            description = "Max Allowed Download Package Size before compression."
                    + "Please use caution as you are changing the value for this field.",
            type = AttributeType.LONG
    )
    long getMaxAllowedDownloadPackageSize() default 104857600L;

    @AttributeDefinition(
            name = "Prefix to files in zip package",
            description = "Add prefixes to files in zip.Eg: _,-"
    )
    String getPrefix() default CommonConstants.UNDER_SCORE;

    @AttributeDefinition(
            name = "Max Allowed Redirects",
            description = "Max allowed redirects when generating a zip from a url. Default is a 1.",
            type = AttributeType.INTEGER
    )
    int getMaxAllowedRedirects() default 1;

    @AttributeDefinition(
            name = "Download File Timeout",
            description = "Max time in seconds before the file download will timeout.",
            type = AttributeType.INTEGER
    )
    int getDownloadFileTimeout() default 60;

}
