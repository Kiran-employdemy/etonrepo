package com.eaton.platform.integration.auth.filters;

import static com.eaton.platform.core.constants.CommonConstants.RUNMODE_AUTHOR;
import java.time.LocalDate;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import com.day.cq.commons.jcr.JcrConstants;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.engine.EngineConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.format.DateTimeFormatter;
import com.eaton.platform.core.constants.AEMConstants;
/**
 * Filter used to rename the downloaded report.
 */

@Component(service = Filter.class,
		property = {
				AEMConstants.SERVICE_DESCRIPTION + "ACS Commons - Download CSV Filter",
				EngineConstants.SLING_FILTER_METHODS + "=GET",
				EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
				Constants.SERVICE_RANKING + ":Integer=10000",
				EngineConstants.SLING_FILTER_SELECTORS + "=report",
				EngineConstants.SLING_FILTER_RESOURCETYPES + "="+ACSCommonsReportsFilter.RESOURCE_TYPE,
				EngineConstants.SLING_FILTER_EXTENSIONS + "=csv"
		})

public class ACSCommonsReportsFilter implements Filter {

	private static final Logger LOG = LoggerFactory.getLogger(ACSCommonsReportsFilter.class);
	@Reference
	private SlingSettingsService slingSettingsService;
	private static final String KEYWORD = "keyword";
	private static final String TYPE = "type";
	private static final String REFERENCE_REPORT = "-Reference Report";
	public static final String RESOURCE_TYPE = "acs-commons/components/utilities/report-builder/report-page";
	private static final String NT_BASE = "nt:base";
	private static final String CQ_PAGE = "cq:Page";
	private static final String DAM_ASSET = "dam:Asset";
	private static final String ALL = "All";
	private static final String PAGE = "Pages";
	private static final String ASSET = "Assets";
	private static final String RESOURCE_PATH = "/var/acs-commons/reports/reference-report-by-keyword-or-path/jcr:content";

	@Activate
	public void activate(final ComponentContext context){
		LOG.debug("ACSCommonsReportsFilter :: activate() :: Start, set");
		Set<String> runModes = slingSettingsService.getRunModes();
		if (runModes.contains(RUNMODE_AUTHOR) && context != null) {
			if(null != context.getProperties()) {
				final String componentName = (String) context.getProperties()
						.get(ComponentConstants.COMPONENT_NAME);
				context.disableComponent(componentName);
			}
		}
		LOG.debug("ACSCommonsReportsFilter :: activate() :: End");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Empty Body
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String keyword = null;
		String type = null;
		if (LOG.isDebugEnabled()) {
			LOG.debug("ACSCommonsReportsFilter :: doFilter() :: Start");
		}
		final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
		final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
		if(null !=  request.getParameter(KEYWORD)){
			    keyword = request.getParameter(KEYWORD);
				if(keyword.equals(CommonConstants.ARTICLE_PAGE_TEMPLATE_PATH)){
				keyword = KEYWORD;
			}
		}
		if(null != request.getParameter(TYPE)){
		 	type = request.getParameter(TYPE);
			type = resultAssetType(type);
		}else{
		 	type = type;
		}
		Resource resource = slingRequest.getResource();
		if(resource.getPath().equals(RESOURCE_PATH)) {
			ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
			LocalDate date = LocalDate.now();
			date = currentDate(date);
			map.put(JcrConstants.JCR_TITLE, keyword + "-" + type + "-" + date + REFERENCE_REPORT);
		}
		chain.doFilter(request, response);
	}
	private LocalDate currentDate(LocalDate date){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
		String text = date.format(formatter);
		LocalDate parsedDate = LocalDate.parse(text, formatter);
		return parsedDate;
	}
	private String resultAssetType(String type){
		String assetType = null;
		LOG.debug("ACSCommonsReportsFilter :: getAssetType() :: Start, set");
		if(type.equals(NT_BASE)){
			assetType = ALL;
		}else if(type.equals(CQ_PAGE)){
			assetType = PAGE;
		}else if(type.equals(DAM_ASSET)){
			assetType = ASSET;
		}
		LOG.debug("ACSCommonsReportsFilter :: getAssetType() :: End");
		return assetType;
	}
	@Override
	public void destroy() {
		// No-Op
	}

}