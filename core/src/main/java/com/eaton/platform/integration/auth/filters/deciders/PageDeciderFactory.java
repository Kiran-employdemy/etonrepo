package com.eaton.platform.integration.auth.filters.deciders;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;

import java.util.TreeSet;

public class PageDeciderFactory {
    private final AuthenticationServiceConfiguration authenticationServiceConfig;
    private final Page page;

    public PageDeciderFactory(Page page, AuthenticationServiceConfiguration configuration) {
        this.authenticationServiceConfig = configuration;
        this.page = page;
    }
    public TreeSet<AbstractPageLoginUrlDecider> getAllDeciders(){
        TreeSet<AbstractPageLoginUrlDecider> deciders = new TreeSet<>();
        deciders.add(new DeveloperPortalDecider(page.getContentResource(),authenticationServiceConfig.devPortalOktaLoginURI()));
        deciders.add(new BehPageDecider(page,authenticationServiceConfig.getOktaWidgetLoginURI()));
        return deciders;
    }
}
