package com.eaton.platform.integration.eloqua.servlets;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.EloquaCloudConfigModel;
import com.eaton.platform.core.models.EloquaFormModel;
import com.eaton.platform.core.models.ResourceDecorator;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.services.ReCaptchaService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.akamai.services.AkamaiNetStorageService;
import com.eaton.platform.integration.eloqua.condition.Redirectable;
import com.eaton.platform.integration.eloqua.condition.impl.SoftwareDeliveryRedirectable;
import com.eaton.platform.integration.eloqua.constant.EloquaConstants;
import com.eaton.platform.integration.eloqua.services.EloquaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.ServiceException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_POST,
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/structure/eaton-edit-template-page",
				ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/content/eloqua-form",
				ServletConstants.SLING_SERVLET_SELECTORS + EloquaSubmitProxyServlet.SERVLET_SELECTOR
		})
public class EloquaSubmitProxyServlet extends SlingAllMethodsServlet {

	/*** The Constant serialVersionUID.	 */
	private static final long serialVersionUID = -4883285716152125473L;

	private static final Logger LOG = LoggerFactory.getLogger(EloquaSubmitProxyServlet.class);

	public static final String SELECTOR_AEM_FORM_IDENTIFIER = "aemformeloqua";
	public static final String SERVLET_SELECTOR = "eloquaproxy";
	public static final String PROP_REDIRECT_URL = "redirectUrl";
	public static final String PARAM_REDIRECT_URL = "redirectUrl";
	public static final String FORM_PARAM_SEPERATOR = "&";
	public static final String HEADER_EQUAL = "=";
	public static final String HEADER_COOKIE_SEPERATOR = "; ";

	@Reference
	private AdminService adminService;

	@Reference
	private HttpClientBuilderFactory httpFactory;

	@Reference
	private CloudConfigService configService;

	@Reference
	private ReCaptchaService reCaptchaService;

	@Reference
	private AkamaiNetStorageService akamaiNetStorageService;

	@Reference
	private EloquaService eloquaService;

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException, InternalError {
		LOG.info("Eloqua proxy servlet :: Started");

		ResourceResolver resourceResolver = request.getResourceResolver();
		final String postCookies = Arrays.asList(request.getCookies() != null ? request.getCookies() : new Cookie[0]).stream()
				.map(cookie -> cookie.getName() + HEADER_EQUAL + cookie.getValue())
				.collect(Collectors.joining(HEADER_COOKIE_SEPERATOR));
		final List<String> selectors = Arrays.asList(request.getRequestPathInfo().getSelectors());
		final String aemRedirectUrl = request.getResource().getValueMap().get(PROP_REDIRECT_URL, StringUtils.EMPTY);
		final boolean isFromAemForm = selectors.contains(SELECTOR_AEM_FORM_IDENTIFIER);
		final boolean isRecaptchaEnabled = shouldValidateReCaptcha(request.getResource());
		final String eloquaSubmitUrl = getEloquaSubmitUrl(request.getResource());

		String eloquaResponse = null;

		if (isRecaptchaEnabled) {
			LOG.debug("Eloqua proxy servlet :: ReCaptcha needed validated and passed validation.");

			try {
				final String reCaptchaResponse = request.getRequestParameter(ReCaptchaService.PARAM_RECAPTCHA_CLIENT_RESPONSE).toString();
				final String reCaptchaSecret = getReCaptchaSecret(request.getResource());
				if (reCaptchaResponse == null || StringUtils.isEmpty(reCaptchaResponse)){
					throw new NullPointerException("ReCaptcha token (reCaptchaResponse) is blank.");
				}

				boolean isReCaptchaValid = reCaptchaService.validate(reCaptchaResponse, reCaptchaSecret);
				if (isReCaptchaValid){
					eloquaResponse = eloquaService.proxyToEloqua(request, postCookies, eloquaSubmitUrl);
				}
			} catch (NullPointerException e){
				LOG.error("Eloqua proxy servlet :: ", e);
			}

		} else {
			LOG.debug("Recaptcha not enabled in component configuration");
			eloquaResponse = eloquaService.proxyToEloqua(request, postCookies, eloquaSubmitUrl);
		}
		Redirectable swRedirectable = new SoftwareDeliveryRedirectable(request,akamaiNetStorageService);
		if(swRedirectable.shouldRedirect()){
			String redirectUrl = swRedirectable.redirectUrl();
			LOG.debug("Software Delivery Redirectable :: attempting to redirect :: {}",redirectUrl);
			if(StringUtils.isNotBlank(redirectUrl)){
				response.sendRedirect(redirectUrl);
			}
		}

		if (!isFromAemForm) {
			String redirectUrl = getRedirect(eloquaResponse, resourceResolver, aemRedirectUrl);
			response.sendRedirect(redirectUrl);
		}

		LOG.info("Eloqua proxy servlet :: Finished");
	}

	/**
	 * Get redirect url
	 * Precedence:
	 * 1. AEM Component Config
	 * 2. Eloqua redirect
	 * 3. AEM OSGi fallback url
	 * @param eloquaResponse
	 * @param resourceResolver
	 * @param aemRedirectUrl
	 * @return
	 */
	private String getRedirect(String eloquaResponse, ResourceResolver resourceResolver, String aemRedirectUrl) {

		if (StringUtils.isNotBlank(aemRedirectUrl)) {
			return CommonUtil.dotHtmlLink(aemRedirectUrl, resourceResolver);
		} else {
			Pattern pattern = Pattern.compile(EloquaConstants.SUBMISSION_RESPONSE_REDIRECT_REGEX);
			Matcher matcher = pattern.matcher(eloquaResponse);
			return matcher.find() ? matcher.group(1) : eloquaService.defaultRedirect();
		}
	}

	/**
	 * @param resource Either an Eloqua Form Component, in which case the reCaptcha checkbox will be inspected, or another resource.
	 *                 In this latter case it assumes that there is an AEM Form under this resource. The presence of the 'Captcha' component will be determined.
	 * @return Whether or not reCaptcha has been configured for this form submission.
	 */
	private boolean shouldValidateReCaptcha(final Resource resource) {
		boolean validate = false;

		if (CommonConstants.ELOQUA_FROM_RESOURCE_TYPE.equals(resource.getResourceType())) {
			validate = CommonConstants.TRUE.equals(resource.getValueMap().get(EloquaFormModel.PROP_ADD_CAPTCHA, CommonConstants.FALSE));
		} else {
			final Optional<Resource> aemFormComponent = resource.adaptTo(ResourceDecorator.class).findByResourceType(CommonConstants.PRODUCT_SELECTOR_FORM_RESOURCE_TYPE);

			if (aemFormComponent.isPresent()) {
				final String formPath = aemFormComponent.get().getValueMap().get(CommonConstants.PROP_FORM_REF, String.class);

				if (formPath != null) {
					try(ResourceResolver resourceResolver =  adminService.getReadService()) {
						final String formContentPath = formPath.replace(CommonConstants.AEM_FORMS_MIRROR_BASE_PATH, CommonConstants.AEM_FORMS_ACTUAL_BASE_PATH);
						final Optional<Resource> form = resourceResolver
								.resolve(formContentPath).adaptTo(ResourceDecorator.class)
								.findByResourceType(CommonConstants.RESOURCE_TYPE_AEM_FORMS_CAPTCHA);

						if (form.isPresent() && CommonConstants.VAL_CAPTCHA_SERVICE_RECAPTCHA.equals(form.get().getValueMap().get(CommonConstants.PROP_CAPTCHA_SERVICE, String.class))) {
							validate = true;
						}
					}
				}

			}
		}

		return validate;
	}

	/**
	 * This method treats the situation of there not being a configured secret as an error. Therefore it will throw an
	 * exception if the reCaptcha secret cannot be found. So only call this method if you expect there to be a secret configured.
	 * @param resource Any resource.
	 * @return The configured reCaptcha secret for the given resource.
	 */
	private String getReCaptchaSecret(final Resource resource) {
		final String secret;
		final Optional<EloquaCloudConfigModel> config = configService.getEloquaCloudConfig(resource);

		if (config.isPresent()) {
			secret = config.get().getRecaptchasecret();

			if (StringUtils.isBlank(secret)) {
				throw new ServiceException("EloquaSubmitProxyServlet: Recaptcha secret not found for: " + resource.getPath());
			}
		} else {
			throw new ServiceException("EloquaSubmitProxyServlet: Eloqua cloud config not found for: " + resource.getPath());
		}

		return secret;
	}

	private String getEloquaSubmitUrl(final Resource resource) {
		final String submitUrl;
		final Optional<EloquaCloudConfigModel> config = configService.getEloquaCloudConfig(resource);

		if (config.isPresent()) {
			submitUrl = config.get().getEloquaSubmitUrl();

			if (StringUtils.isBlank(submitUrl)) {
				throw new ServiceException("EloquaSubmitProxyServlet: Eloqua Submit URL not found for: " + resource.getPath());
			}
		} else {
			throw new ServiceException("EloquaSubmitProxyServlet: Eloqua Submit URL not found for: " + resource.getPath());
		}

		return submitUrl;
	}
}
