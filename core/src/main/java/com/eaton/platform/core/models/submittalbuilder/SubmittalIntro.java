package com.eaton.platform.core.models.submittalbuilder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SubmittalIntro {

    @Inject
    private String title;

    @Inject
    private String description;

    @Inject
    private String instructionsTitle;

    @Inject
    private String instructions;

    @Inject
    private String filtersButtonText;

    @Inject
    private String packageButtonText;

    @Inject
    private String downloadButtonText;

    @Inject
    private String finishEditsButtonText;

    @Inject
    private String viewSubmittalBuilderButtonText;

    @Inject
    private String editSubmittalBuilderButtonText;
    
    @Inject
    private String submittalBuilderHelpLinkText;
    
    @Inject
    private String submittalBuilderHelpLink;

    @PostConstruct
    protected void init() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getInstructionsTitle() {
        return instructionsTitle;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getFiltersButtonText() {
        return filtersButtonText;
    }

    public String getPackageButtonText() {
        return packageButtonText;
    }

    public String getDownloadButtonText() {
        return downloadButtonText;
    }

    public String getFinishEditsButtonText() {
        return finishEditsButtonText;
    }

    public String getViewSubmittalBuilderButtonText() {
        return viewSubmittalBuilderButtonText;
    }

    public String getEditSubmittalBuilderButtonText() {
        return editSubmittalBuilderButtonText;
    }

	public String getSubmittalBuilderHelpLinkText() {
		return submittalBuilderHelpLinkText;
	}

	public String getSubmittalBuilderHelpLink() {
		return submittalBuilderHelpLink;
	}
    
}
