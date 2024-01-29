package com.eaton.platform.integration.endeca.servlets;

import com.adobe.xfa.ut.StringUtils;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.integration.auth.constants.AuthConstants;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.helpers.EndecaServiceHelper;
import com.eaton.platform.integration.endeca.pojo.pdh.EndecaPdhResponse;
import com.eaton.platform.integration.endeca.services.EndecaQRService;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;



/***
 * This servlet is used with QR codes.
 * https://qr.eaton.com/?c=PDG33F0600E3XN redirects to /eaton/EndecaQRProcess.PDG33F0600E3XN.json?S=serialnum&E=...
 * Then,
 *   If the current locale is stored in a browser cookie & the product is available in that country redirects to a locale-specific SKU page.
 *   If the locale cookie is absent or the product is not sold in the cookie's locale, redirect to a global SKU page as configured in OSGi.
 */

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/EndecaQRProcess",
		})
public class EndecaQRProcessingServlet extends SlingSafeMethodsServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8826409808306493899L;
	private static final Logger LOGGER = LoggerFactory.getLogger(EndecaQRProcessingServlet.class);
	private static final String CONTENT_ROOT_PATH = EndecaServiceHelper.CONTENT_ROOT_FOLDER;

	private transient EndecaPdhResponse endecaPdhResponse;

	/** The endeca QR processing service. */
	@Reference
	private transient EndecaQRService endecaQRService;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.debug("Start of QRProcessingServlet - doGet().");
		response.setContentType("text/html");
		String countryValueFromCookie = "";

		RequestParameterMap params = request.getRequestParameterMap();
		String localePrefix = CONTENT_ROOT_PATH + "/us/en-us";
		String localeCookieValue = "";
		ResourceResolver resourceResolver = request.getResourceResolver();
		String[] selectors = request.getRequestPathInfo().getSelectors();
		if(request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE) != null) {
			localeCookieValue = request.getCookie(AuthConstants.ETN_REDIRECT_COOKIE).getValue().replace("%2F", "/");
			String[] splitLocaleCookieValue = localeCookieValue.split("/");
			countryValueFromCookie = splitLocaleCookieValue[1];
			localePrefix = CONTENT_ROOT_PATH + localeCookieValue;
		}

		//If catalogNumber selector is not provided or is null, redirect to 404
		if(selectors.length == 0) {
			redirectTo404(resourceResolver, response, localePrefix);
			return;
		}
		String catalogNumber = selectors[0];
		EndecaServiceRequestBean endecaRequestBean = endecaQRService.createPdhEndecaRequestBean(catalogNumber);
		if (endecaQRService.getEatonpdhlstcountries(endecaRequestBean) == null) {
			LOGGER.error("Null response from endecaQRService.getEatonpdhlstcountries(endecaRequestBean);");
			redirectTo404(resourceResolver, response, localePrefix);
			return;
		} else {
			endecaPdhResponse = endecaQRService.getEatonpdhlstcountries(endecaRequestBean);
			List<String> endecaCountries = endecaPdhResponse.fetchCountries();
			if (!endecaCountries.isEmpty() && !StringUtils.isEmpty(localeCookieValue)) {
				if (endecaCountries.contains(countryValueFromCookie.toUpperCase())) {
					//If endeca countries includes cookie country, redirect to Locale specific SKU page
					redirectToLocaleSku(resourceResolver, response, localePrefix, catalogNumber, params);
				} else {
					//  If Endeca language does not match current locale (cookie), redirect to Global SKU page
					redirectToGlobalSku(resourceResolver, response, catalogNumber, params);
				}
			} else {
				//  If current locale not present, redirect to Global SKU page
				redirectToGlobalSku(resourceResolver, response, catalogNumber, params);
			}
		}
		response.getOutputStream().println(new Gson().toJson(endecaPdhResponse));
	}

	private void redirectToLocaleSku(ResourceResolver resourceResolver, SlingHttpServletResponse response,
									 String localePrefix, String catalogNumber, RequestParameterMap params) throws  IOException {
		if(resourceResolver.getResource(localePrefix) != null) {
			response.sendRedirect(
					resourceResolver.map(localePrefix + "/skuPage." + catalogNumber + ".html" + getParamStr(params))
			);
		} else {
			redirectToGlobalSku(resourceResolver, response, catalogNumber, params);
		}
	}

	private void redirectToGlobalSku(ResourceResolver resourceResolver, SlingHttpServletResponse response,
									 String catalogNumber, RequestParameterMap params) throws IOException {
		String globalPrefix = CONTENT_ROOT_PATH + endecaQRService.getGlobalSkuPagePath();
		if(resourceResolver.getResource(globalPrefix) != null) {
			response.sendRedirect(
					resourceResolver.map(globalPrefix + "." + catalogNumber + ".html" + getParamStr(params))
			);
		}
	}

	private static void redirectTo404(ResourceResolver resourceResolver, SlingHttpServletResponse response, String localePrefix) throws IOException {
		if(resourceResolver.getResource(localePrefix) != null) {
			response.sendRedirect(resourceResolver.map(localePrefix.concat(CommonConstants.ERROR_PAGE_404)));
		}
	}

	private static String getParamStr(RequestParameterMap params) {
		String paramStr = "";
		RequestParameter serialNumber = params.getValue("S");
		RequestParameter eventId = params.getValue("E");
		if(serialNumber != null && eventId != null) {
			paramStr = "?S=" + serialNumber.getString() + "&E=" + eventId.getString();
		}
		if(serialNumber != null && eventId == null) {
			paramStr = "?S=" + serialNumber.getString();
		}
		if(serialNumber == null && eventId != null) {
			paramStr = "?E=" + eventId.getString();
		}
		return paramStr;
	}

}
