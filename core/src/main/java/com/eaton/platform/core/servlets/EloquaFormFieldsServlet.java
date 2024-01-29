package com.eaton.platform.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.search.QueryBuilder;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.models.EloquaCloudConfigModel;
import com.eaton.platform.core.models.EloquaFormModel;
import com.eaton.platform.core.services.EloquaFormConfigService;
import com.eaton.platform.core.services.EloquaFormOverlayService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.eloqua.services.EloquaService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.apache.sling.query.SlingQuery.$;


/***
 * This servlet is used for populating the hidden fields to a dialog drop down.
 */


@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/eloqua/formfield",
		})
public class EloquaFormFieldsServlet extends SlingSafeMethodsServlet{

	private static final Logger LOGGER = LoggerFactory.getLogger(EloquaFormFieldsServlet.class);
	private static final String SELECTOR = "selector";
	private static final String HIDDEN = "hidden";
	private static final String ALL = "all";


	@Reference
	private EloquaService eloquaService;

	@Reference
	private ConfigurationManagerFactory configurationManagerFactory;

	@Reference
	private QueryBuilder queryBuilder;

	@Reference
	private EloquaFormConfigService eloquaFormConfigService;

	@Reference
	private EloquaFormOverlayService eloquaFormOverlayService;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws IOException {
		final ResourceResolver resourceResolver = request.getResourceResolver();
		final String refererURL = CommonUtil.getRefererURL(request);
		final String contentPath = CommonUtil.getContentPath(resourceResolver, refererURL);
		final String resourcePath = request.getRequestPathInfo().getResourcePath();
		final Resource dialogResource = resourceResolver.resolve(resourcePath);


		if ((null != configurationManagerFactory && null != eloquaFormConfigService) && null != dialogResource) {
			final String selector = dialogResource.getValueMap().get(SELECTOR, StringUtils.EMPTY);
			final EloquaCloudConfigModel eloquaCloudConfigModel = eloquaFormConfigService
					.setUpEloquaServiceParameters(configurationManagerFactory, contentPath,
							resourceResolver);
			final String eloquaFormID = getEloquaFormID(contentPath, resourceResolver);

			if (StringUtils.isNotEmpty(eloquaFormID)) {
				final String eloquaFormResponse = eloquaService.getEloquaForm(eloquaCloudConfigModel, eloquaFormID);

				if (StringUtils.isNotEmpty(eloquaFormResponse)) {
					final List<Resource> resourceList = eloquaFormOverlayService.extractFormFields(eloquaFormResponse,
							resourceResolver, selector);

					if (CollectionUtils.isNotEmpty(resourceList)) {
						DataSource dataSource = new SimpleDataSource(resourceList.iterator());
						request.setAttribute(DataSource.class.getName(), dataSource);
					}
				}
			}
		}
	}



	private String getEloquaFormID(final String contentPath, final ResourceResolver resourceResolver) {
		String formID =  StringUtils.EMPTY;
		final Page page = resourceResolver.resolve(contentPath).adaptTo(Page.class);
		final Resource contentResource = page.getContentResource();
		final List<Resource> eloquaResource = $(contentResource).find("eaton/components/content/eloqua-form").asList();
		final Optional<Resource> resourceOptional = eloquaResource.stream().findFirst();
		if (resourceOptional.isPresent()) {
			final Optional<EloquaFormModel> eloquaFormModel = Optional.ofNullable(resourceOptional.get()
					.adaptTo(EloquaFormModel.class));
			if (eloquaFormModel.isPresent()) {
				formID = eloquaFormModel.get().getFormId();
			}
		}
		return formID;
	}

}
