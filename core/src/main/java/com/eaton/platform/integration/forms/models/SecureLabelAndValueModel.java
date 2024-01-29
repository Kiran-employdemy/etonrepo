package com.eaton.platform.integration.forms.models;

import com.day.cq.commons.jcr.JcrConstants;
import com.eaton.platform.core.util.SecureUtil;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.stream.Stream;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecureLabelAndValueModel {


    /** Field Name. */
    @Inject
    @Via("resource")
    private String name;

    /** Field Title. */
    @Inject
    @Via("resource") @Named(JcrConstants.JCR_TITLE)
    private String title;

    private String value;

    @Inject
    private SlingHttpServletRequest slingHttpServletRequest;

    /**
     *  Authorization Service
     */
    @Inject
    private AuthorizationService authorizationService;

    /**
     * Get's Field Value from Profile Object.
     */
    @PostConstruct
    protected void init() {
        String afDataJSON = authorizationService.getProfileJSONFromSlingRequest(slingHttpServletRequest);
        if(null != afDataJSON && StringUtils.isNoneEmpty(afDataJSON)){
            value = SecureUtil.getProfileValueByKey(name, afDataJSON);
            value = formatValue(value);
        }
    }

    private String formatValue(String value){
        if(StringUtils.isBlank(value)){
            return value;
        }
        String[] csv = StringUtils.split(value.trim(),",");
        // this should sort everything
        csv = Stream.of(csv).sorted().toArray(String[]::new);
        if(csv.length>1) {
            csv = StringUtils.stripAll(csv);
            return StringUtils.joinWith(", ",csv);
        }
        return value;
    }


    public String getValue() {
        return value;
    }
}
