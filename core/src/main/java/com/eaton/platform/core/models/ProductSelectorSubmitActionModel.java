package com.eaton.platform.core.models;

import com.adobe.aemds.guide.utils.GuideSubmitUtils;
import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.eaton.platform.core.constants.CommonConstants.FACET_SEPERATOR;
import static com.eaton.platform.core.constants.CommonConstants.PRODUCT_SELECTOR_FORM_DROPDOWN_RESOURCE_TYPE;
import static com.eaton.platform.core.constants.CommonConstants.PRODUCT_TABS_RESOURCE_TYPE;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductSelectorSubmitActionModel {
	private static final String MODELS_FACETS = ".models.facets";
	private static final String FACETS = ".facets";
	private static final String STATUS = "status";
	private static final String OWNER = "owner";
	private static final Logger LOG = LoggerFactory.getLogger(ProductSelectorSubmitActionModel.class);

	@Inject
	@Via("resource")
	private String redirect;

	@Inject
	@Source("sling-object")
	private ResourceResolver resourceResolver;

	@Inject
	private Resource resource;

	@OSGiService
	private AdminService adminService;

	@OSGiService
	private Externalizer externalizer;

	@Inject
	private SlingHttpServletRequest slingRequest;

	public void doSubmitAction(final HttpServletRequest request, final SlingHttpServletRequest slingRequest) {
		GuideSubmitUtils.setRedirectParameters(slingRequest, redirectParameters(slingRequest));
		GuideSubmitUtils.setRedirectUrl(slingRequest, redirectionUrl(request));
	}

	/**
	 * This removes the status and owner redirect parameters from the ones provided in the request.
	 * @param slingRequest The request to base the redirect parameters on.
	 * @return The map of redirect parameters to add to the GuideSubmitUtils.
	 */
	private Map<String, String> redirectParameters(SlingHttpServletRequest  slingRequest) {
		Map<String, String> redirectParameters = GuideSubmitUtils.getRedirectParameters(slingRequest);

		if (redirectParameters == null) {
			redirectParameters = new HashMap<>();
		} else {
			redirectParameters.remove(STATUS);
			redirectParameters.remove(OWNER);

			//Temporary fix to blank out values
			redirectParameters.put(STATUS, "");
			redirectParameters.put(OWNER, "");
		}

		return redirectParameters;
	}

	public String getExternalizedRedirect() {
		return externalizer != null && slingRequest != null
				? externalizer.relativeLink(slingRequest, redirect)
				: redirect;
	}

	/**
	 * The redirection url for this submit action. This is created based upon the thank you page that is configured and
	 * the facets specified in the request. If the thank you page has a product tabs component the models selector
	 * will be added to the url.
	 * @param request The request to base the facets on.
	 * @return The redirection url to use for this request.
	 */
	private String redirectionUrl(HttpServletRequest request) {
		Optional<Resource> pageResource;
		String redirectionUrlString = StringUtils.EMPTY;
		try(ResourceResolver adminServiceReadService = adminService.getReadService()) {
			LOG.info("redirect>>>>>>>>>>"+redirect);
			pageResource = Optional.ofNullable(adminServiceReadService.resolve(redirect));
			redirectionUrlString =  CommonUtil.dotHtmlLink(new StringBuilder()
					.append(getExternalizedRedirect())
					.append(pageResource.get().adaptTo(ResourceDecorator.class).findByResourceType(PRODUCT_TABS_RESOURCE_TYPE).isPresent() ? MODELS_FACETS : FACETS)
					.append(facetsSelector(request)).toString(), resourceResolver);
			if(!redirectionUrlString.endsWith(CommonConstants.HTML_EXTN)) {
				redirectionUrlString = redirectionUrlString.concat(CommonConstants.HTML_EXTN);
			}

		}
		LOG.info("Redirection URL>>>>>>>>>>>>"+redirectionUrlString);
		return redirectionUrlString;
	}

	/**
	 * The facets selector as created based upon the parameters that exist in the request and the product selector dropdowns
	 * that exist in the form.
	 * @param request The request to base the list of facets on.
	 * @return The url selector to use for the given request.
	 */
	private String facetsSelector(HttpServletRequest request) {
		String selector = StringUtils.EMPTY;
		String selectedFacets = slingRequest.getParameter("_selectedFacets");
		selector = resource.adaptTo(ResourceDecorator.class)
				.findAllByResourceType(PRODUCT_SELECTOR_FORM_DROPDOWN_RESOURCE_TYPE).stream()
				.map(result -> request.getParameter(result.getValueMap().get(CommonConstants.PROPERTY_NAME, String.class)))
				.filter(StringUtils::isNotBlank)
				.collect(Collectors.joining(FACET_SEPERATOR));
		if(StringUtils.isNotBlank(selectedFacets)) {
			List<String> facets = Arrays.asList(selectedFacets.split(","));
			selector = StringUtils.isNotBlank(selector) ?
					selector.concat(FACET_SEPERATOR).concat(facets.stream().collect(Collectors.joining(FACET_SEPERATOR))) :
					facets.stream().collect(Collectors.joining(FACET_SEPERATOR));
		}
		return StringUtils.isNotEmpty(selector) ? (FACET_SEPERATOR+selector) : StringUtils.EMPTY;
	}
}
