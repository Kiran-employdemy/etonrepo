package com.eaton.platform.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

/**
 * Disclaimer Model Class
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DisclaimerModel {

	@Inject
	private String  disclaimersText;

	@Inject
	private String selectedField;

	public final String getDisclaimersText() {
		return disclaimersText;
	}

	public final void setDisclaimersText(final String disclaimersText) {
		this.disclaimersText = disclaimersText;
	}

	public final String getSelectedField() {
		return selectedField;
	}

	public final void setSelectedField(final String selectedField) {
		this.selectedField = selectedField;
	}
}
