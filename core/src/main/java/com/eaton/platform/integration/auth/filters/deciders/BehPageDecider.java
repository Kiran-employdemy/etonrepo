package com.eaton.platform.integration.auth.filters.deciders;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang.StringUtils;

import static com.eaton.platform.integration.auth.constants.SecureConstants.SECURE_DIGITAL_CONTENT_PATH_SUFFIX;
import static com.eaton.platform.integration.auth.constants.SecureConstants.SOFTWARE_DELIVERY_TEMPLATE_PATH;

public class BehPageDecider extends AbstractPageLoginUrlDecider{

    private String redirectTo;
    private Page page;


    public BehPageDecider(Page page,String redirectTo){
        super.orderRanking = 2;
        this.page = page;
        this.redirectTo = redirectTo;

    }
    @Override
    public boolean conditionMatched() {
        String pageTemplate = page.getProperties().get(NameConstants.NN_TEMPLATE, StringUtils.EMPTY);
        return page.getPath().contains(SECURE_DIGITAL_CONTENT_PATH_SUFFIX) || SOFTWARE_DELIVERY_TEMPLATE_PATH.equals(pageTemplate);
    }

    @Override
    public String redirectTo() {
        return redirectTo;
    }
}
