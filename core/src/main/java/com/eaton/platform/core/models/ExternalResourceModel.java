package com.eaton.platform.core.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExternalResourceModel {

    @Inject
    String externalLinkText;

    @Inject
    String resourceLink;

    @Inject
    String accordionName;


    boolean isExternalLink;

    public String getExternalLinkText() {
        return externalLinkText;
    }

    public String getResourceLink() {
        return resourceLink;
    }

    public boolean isExternalLink() {
        final Pattern urlPattern = Pattern.compile("(http|https)");
        if (StringUtils.isNotEmpty(resourceLink)) {
            final Matcher matcher = urlPattern.matcher(resourceLink);
            isExternalLink = matcher.lookingAt();
        }
        return isExternalLink;
    }

    public String getAccordionName() {
        return accordionName;
    }
}
