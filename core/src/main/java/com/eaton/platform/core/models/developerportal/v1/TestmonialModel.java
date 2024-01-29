package com.eaton.platform.core.models.developerportal.v1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
		resourceType = "eaton/components/developer-portal/core/testimonial/v1/testimonial")
public class TestmonialModel {
	
	@Inject
    private Resource testimonialsList;
	
	
	public List<TesimonialListModel> testimonials;
	
	
	@PostConstruct
	protected void init() {
		testimonials = new ArrayList<TesimonialListModel>();
		if (testimonialsList != null) {
			populateModel(testimonials, testimonialsList);
		}
	}
	
	public  List<TesimonialListModel> populateModel(List<TesimonialListModel> testimonials, Resource resource) {
		if (resource != null) {
			Iterator<Resource> linkResources = resource.listChildren();
			while (linkResources.hasNext()) {
				TesimonialListModel testimonial = linkResources.next().adaptTo(TesimonialListModel.class);
				if (testimonial != null){
					testimonials.add(testimonial);
				}
			}
		}
		return testimonials;
	}

	public List<TesimonialListModel> getTestimonials() {
		return testimonials;
	}
}