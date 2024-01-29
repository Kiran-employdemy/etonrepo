package com.eaton.platform.integration.itext.servlets;

import java.io.IOException;
import com.eaton.platform.core.services.AdminService;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.itext.services.ItextPdfService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;

/**
 * The Class ItextPdf Servlet.
 *
 */
@Component(service = Servlet.class, immediate = true, property = { ServletConstants.SLING_SERVLET_METHODS_GET,
		ServletConstants.SLING_SERVLET_RESOURCE_TYPES + "eaton/components/structure/eaton-edit-template-page",
		ServletConstants.SLING_SERVLET_EXTENSIONS + "pdf" })
public final class ItextPdfServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ItextPdfServlet.class);
	private static final String APP_TYPE = "application/pdf;charset=UTF-8";
	private static final String PDF_EXT = ".pdf";
	private static final String CONTENT_DISP = "Content-disposition";
	private static final String INLINE_FILENAME = "inline; filename=";

	@Reference
	protected ItextPdfService iTextPdfService;

	@Reference
	private SlingSettingsService settings;

	public static final String SRC = "src/main/resources/templates/SkuPage.vm";
	@Reference
	private transient AdminService adminService;
	transient PageManager pagemanager;
	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOG.debug("ItextPdfServlet : Start from doGet() method");
		if (iTextPdfService.isEnablePdfGeneration()) {
			String[] selectors = request.getRequestPathInfo().getSelectors();
			if (ArrayUtils.isNotEmpty(selectors)) {
				ServletOutputStream outputStream = response.getOutputStream();
				byte[] buffer = iTextPdfService.getPDFByHtmlSource(request);
				if(buffer !=null){
				response.setContentType(APP_TYPE);
				response.setHeader(CONTENT_DISP, INLINE_FILENAME + selectors[0] + PDF_EXT);
				outputStream.write(buffer);
				outputStream.flush();
				outputStream.close();
				}else{
						Page currentPage = null;
						pagemanager = request.getResourceResolver().adaptTo(PageManager.class);
						String refererURL = request.getRequestPathInfo().getResourcePath();
						if(refererURL!=null) {
							refererURL = refererURL.replace(CommonConstants.TAG_HIERARCHY_SEPARATOR + CommonConstants.JCR_CONTENT_STR, CommonConstants.ARTICLE_PAGE_TEMPLATE_PATH);
							currentPage = pagemanager.getPage(refererURL);
							String redirectUrl = CommonUtil.getHomePagePath(currentPage).concat(CommonConstants.ERROR_PAGE_404);
							response.sendRedirect(redirectUrl);
						}
				}
			}
		}
		LOG.debug("ItextPdfServlet : Exit from doGet() method");
	}
}