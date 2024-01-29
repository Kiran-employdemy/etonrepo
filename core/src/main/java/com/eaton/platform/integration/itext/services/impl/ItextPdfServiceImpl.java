package com.eaton.platform.integration.itext.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.jcr.Node;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.Attribute;
import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.ProductDetailsCardBean;
import com.eaton.platform.core.bean.ResourceListDetail;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ExternalResourceModel;
import com.eaton.platform.core.models.ProductDetailsCardModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.models.productspecifications.ProductSpecificationsModel;
import com.eaton.platform.core.models.resourcelistpdh.ResourceListPDHModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.itext.services.ItextPdfService;
import com.eaton.platform.integration.itext.services.config.ItextPdfServiceConfig;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;

/**
 * This class is used to generate the PDF using iTextPdf API.
 *
 */
@Designate(ocd = ItextPdfServiceConfig.class)
@Component(service = ItextPdfService.class, immediate = true, property = {
		AEMConstants.SERVICE_DESCRIPTION + "ItextPdf Service", AEMConstants.SERVICE_VENDOR_EATON,
		AEMConstants.PROCESS_LABEL + "ItextPdfServiceImpl" })
public class ItextPdfServiceImpl implements ItextPdfService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ItextPdfServiceImpl.class);
	
	private static final int ONE_NBSP = 1;
	private static final int TWO_NBSP = 2;
	
	private static final String MICRO_CHAR = "µ";
	private static final String NBSP = "&nbsp;";
	private static final Pattern PATTERN = Pattern.compile(MICRO_CHAR);

	public static final String EATON_LOGO = "https://www.eaton.com/content/dam/eaton/global/logos/eaton-logo-small.png";
	public static final String TEMPLATE_LINK = "/apps/eaton/settings/wcm/designs/iTextPdf_template/iTextPdfSKU.html";
	public static final String JCR_CONTENT = "/jcr:content";
	public static final String JCR_DATA = "jcr:data";
	public static final String CATALOG_NUMBER = "Catalog Number";
	public static final String UPC = "UPC";
	public static final String EATON_LOGO_STR = "eatonLogo";
	public static final String IMAGE_PATH = "imagePath";
	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String PRICE = "price";
	public static final String PRICE_DISC = "priceDisclaimer";
	public static final String CTA_LABEL = "primaryCTALabel";
	public static final String HEADER_CAT_NUM = "headerCatlogNumber";
	public static final String HEADER_UPC = "headerUPC";
	public static final String ATT_GRP_LIST = "attributeGroupList";
	public static final String RESOURCE_DETAILS = "resourceListDetails";
	public static final String EXTRENAL_RCS_LIST = "externalResourceList";
	public static final String LOG_STR = "LOG";
	
	// These keys represent the i18n keys and the html variable names for static text within the pdf
	// If an i18n key name is modified, the html variable name must also be modified in iTextPdfSKU.html
	private static final String CATALOG_NUMBER_KEY = "catalogNumber";
	private static final String RESOURCES_KEY = "Resources";
	private static final String COPYRIGHT_KEY = "copyright";
	private static final String EATON_CORP_KEY = "eatonCorp";
	private static final String EATON_HOUSE_KEY = "eatonHouse";
	private static final String PEMBROKE_ROAD_KEY = "pembrokeRoad";
	private static final String DUBLIN_IRELAND_KEY = "dublinIreland";
	private static final String TRADEMARK_LINE_1_KEY = "trademarkLine1";
	private static final String TRADEMARK_LINE_2_KEY = "trademarkLine2";
	private static final String DEFAULT_TAXONOMY_ATTRIBUTE_LABEL_KEY = CommonConstants.DEFAULT_TAXONOMY_ATTRIBUTE_LABEL;
	
	// These keys represent html variable names for various dynamic elements within the pdf
	private static final String LOGO_LINK_KEY = "logoLink";
	private static final String EATON_SITE_CONFIG_CURRENT_LANGUAGE_KEY = "eatonSiteConfigCurrentLanguage";
	private static final String YEAR_KEY = "year";
	
	@Reference
	public transient AdminService adminService;
	
	private boolean enablePdfGeneration;
	private boolean debugLog;
	private String fallbackLogoLink;
	
	/**
	 * @param config
	 * 
	 *               Set the OSGI configuration values.
	 */
	@Activate
	public void activate(ItextPdfServiceConfig config) {
		long activateST = System.currentTimeMillis();
		enablePdfGeneration = config.enablePdfGeneration();
		debugLog = config.debugLog();
		fallbackLogoLink = config.fallbackLogoLink();
		long activateET = System.currentTimeMillis();
		LOG.info("activate method took " + (activateET - activateST) + " milliseconds");
	}

	@Override
	public byte[] getPDFByHtmlSource(SlingHttpServletRequest request) throws IOException {
		long pDFByHtmlSourceST = System.currentTimeMillis();
		LOG.debug("ItextPdfServiceImpl : Start from getPDFByHtmlSource() method");
		String headerCatlogNumber = null;
		byte[] buffer = null;
		
		try (ResourceResolver resourceResolver = adminService.getReadService()) {
			final Resource resource = resourceResolver.getResource(TEMPLATE_LINK + JCR_CONTENT);
			if (resource != null) {
			Node node = resource.adaptTo(Node.class);
			InputStream	is = node.getProperty(JCR_DATA).getBinary().getStream();
			InputStreamReader reader = new InputStreamReader(is);
			ProductDetailsCardBean productDetailsCardBean = getProductDetailsCardBean(request);
			if(productDetailsCardBean!=null){
			List<AttributeListDetail> attributeGroupList = getAttributeGroupList(request);
			ResourceListPDHModel resourceListPDHModel = getResourceListPDHModel(request);
			List<ResourceListDetail> resourceListDetails = resourceListPDHModel.getResourceList();
			List<ExternalResourceModel> externalResourceList = resourceListPDHModel.getExternalResourceList();
			for(AttributeListDetail attributeListDetail : attributeGroupList) {
				List<Attribute> listOfAttribute = attributeListDetail.getAttributeList();
				if(listOfAttribute != null){
				for (Attribute attribute : listOfAttribute) {
					if (attribute.getAttributeLabel().equals(CATALOG_NUMBER) && null != attribute.getAttributeValue()) {
						headerCatlogNumber = attribute.getAttributeValue().getCdata();
					}
				}
			}
			}
			VelocityEngine engine = new VelocityEngine();
			engine.init();
			VelocityContext context = new VelocityContext();
			context.put(EATON_LOGO_STR, EATON_LOGO);
			if(productDetailsCardBean !=null) {
				if(productDetailsCardBean.getImageDesktop() !=null) {
					context.put(IMAGE_PATH, productDetailsCardBean.getImageDesktop());
				}
				context.put(TITLE, addSpaceForMicroChar(productDetailsCardBean.getTitle(), TWO_NBSP));
				context.put(DESCRIPTION, addSpaceForMicroChar(productDetailsCardBean.getDescription(), TWO_NBSP));
				context.put(PRICE, productDetailsCardBean.getPrice());
				context.put(PRICE_DISC, productDetailsCardBean.getPriceDisclaimer());
				context.put(CTA_LABEL, productDetailsCardBean.getPrimaryCTALabel());
			}
			if(headerCatlogNumber !=null) {
                context.put(HEADER_CAT_NUM, headerCatlogNumber);
			} else {
				context.put(HEADER_CAT_NUM, productDetailsCardBean.getTitle());
			}
			context.put(ATT_GRP_LIST, attributeGroupList);
			context.put(RESOURCE_DETAILS, resourceListDetails);
			context.put(EXTRENAL_RCS_LIST, externalResourceList);

			int year = LocalDate.now().getYear();
			context.put(YEAR_KEY, year);

			context = setCurrentLanguage(request, context);

			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			String currentPageUri = org.apache.commons.lang3.StringUtils.replaceAll(request.getRequestURI(), "\\..*", org.apache.commons.lang3.StringUtils.EMPTY);
			Page currentPage = pageManager.getPage(currentPageUri);
			if (null == currentPage) {
				LOG.error("Unable to get current page resource for uri of {}. i18n values not set. Eaton logo links not set.", currentPageUri);
			} else {
				context = setI18nValue(request, currentPage, context, DEFAULT_TAXONOMY_ATTRIBUTE_LABEL_KEY);
				context = setI18nValue(request, currentPage, context, CATALOG_NUMBER_KEY);
				context = setI18nValue(request, currentPage, context, RESOURCES_KEY);
				context = setI18nValue(request, currentPage, context, EATON_CORP_KEY);
				context = setI18nValue(request, currentPage, context, EATON_HOUSE_KEY);
				context = setI18nValue(request, currentPage, context, PEMBROKE_ROAD_KEY);
				context = setI18nValue(request, currentPage, context, DUBLIN_IRELAND_KEY);
				context = setI18nValue(request, currentPage, context, COPYRIGHT_KEY);
				context = setI18nValue(request, currentPage, context, TRADEMARK_LINE_1_KEY);
				context = setI18nValue(request, currentPage, context, TRADEMARK_LINE_2_KEY);

				context = setLogoLink(currentPage, context, resourceResolver);

			}

			StringWriter swOut = new StringWriter();
			Velocity.evaluate(context, swOut, LOG_STR, reader);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ConverterProperties converterProperties = new ConverterProperties();
			long startTimeCP = System.currentTimeMillis();
			HtmlConverter.convertToPdf(swOut.toString(), outputStream, converterProperties);
			buffer = outputStream.toByteArray();
			long endTimeCP = System.currentTimeMillis();
			LOG.info("PDF generated in " + (endTimeCP - startTimeCP) + " milliseconds");
			if (LOG.isInfoEnabled()) {
				LOG.info("PDF generated Successfully.");
			}
			LOG.debug("ItextPdfServiceImpl : Exit from getPDFByHtmlSource() method");
			swOut.close();
			}
		}
		} catch (Exception e) {
			LOG.error("Exception while generating PDF from html : {}", e.getMessage());
		}
		long pDFByHtmlSourceET = System.currentTimeMillis();
		LOG.info("getPDFByHtmlSource took " + (pDFByHtmlSourceET - pDFByHtmlSourceST) + " milliseconds");
		return buffer;
	}

	private ProductDetailsCardBean getProductDetailsCardBean(SlingHttpServletRequest request) {
		long productDetailsCardBeanST = System.currentTimeMillis();
		ProductDetailsCardBean productDetailsCardBean = null;
		ProductDetailsCardModel productDetailsCardModel = request.adaptTo(ProductDetailsCardModel.class);
		if (productDetailsCardModel != null) {
			productDetailsCardBean = productDetailsCardModel.getProductDetailsCardBean();
		}
		long productDetailsCardBeanET = System.currentTimeMillis();
		LOG.info("getProductDetailsCardBean took " + (productDetailsCardBeanET - productDetailsCardBeanST) + " milliseconds");
		return productDetailsCardBean;
	}

	private List<AttributeListDetail> getAttributeGroupList(SlingHttpServletRequest request) {
		long attributeGroupListST = System.currentTimeMillis();
		List<AttributeListDetail> attributeGroupList = new ArrayList<AttributeListDetail>();
		ProductSpecificationsModel productSpecificationsModel = request.adaptTo(ProductSpecificationsModel.class);
		if (productSpecificationsModel != null) {
			attributeGroupList = productSpecificationsModel.getAttributeGroupList();
		}
		long attributeGroupListET = System.currentTimeMillis();
		LOG.info("getAttributeGroupList took " + (attributeGroupListET - attributeGroupListST) + " milliseconds");
		for (AttributeListDetail attributeList : attributeGroupList) {
			for (Attribute attribute : attributeList.getAttributeList()) {
				attribute.getAttributeValue().setCdata(addSpaceForMicroChar(attribute.getAttributeValue().getCdata(), ONE_NBSP));
			}
		}
		return attributeGroupList;
	}

	private ResourceListPDHModel getResourceListPDHModel(SlingHttpServletRequest request) {
		long resourceListPDHModelST = System.currentTimeMillis();
		ResourceListPDHModel resourceListPDHModel = request.adaptTo(ResourceListPDHModel.class);
		long resourceListPDHModelET = System.currentTimeMillis();
		LOG.info("getResourceListPDHModel took " + (resourceListPDHModelET - resourceListPDHModelST) + " milliseconds");
		return resourceListPDHModel;
	}
	
	private VelocityContext setI18nValue(SlingHttpServletRequest request, Page currentPage, VelocityContext context, String key) {
		LOG.debug("setI18nValue :: START");
		String i18nValue = CommonUtil.getI18NFromResourceBundle(request, currentPage, key);
		if (StringUtils.isNotEmpty(i18nValue)) {
			context.put(key, i18nValue);
			LOG.debug("i18n key ({}) set to value {}", key, i18nValue);
		} else {
			LOG.error("Unable to find i18n value for {}", key);
		}
		LOG.debug("setI18nValue :: END");
		return context;
	}
	
	private VelocityContext setLogoLink(Page currentPage, VelocityContext context, ResourceResolver resourceResolver) {
		LOG.debug("setLogoLink : START");
		
		String currentHomepagePath = CommonUtil.getHomePagePath(currentPage);
		LOG.debug("currentHomepagePath: {}", currentHomepagePath);
		String logoLinkDotHtml = CommonUtil.dotHtmlLink(currentHomepagePath, resourceResolver);
		if (StringUtils.isNotEmpty(logoLinkDotHtml)) {
			context.put(LOGO_LINK_KEY, logoLinkDotHtml);
			LOG.debug("Logo link set to {}", logoLinkDotHtml);
		} else {
			context.put(LOGO_LINK_KEY, fallbackLogoLink);
			LOG.warn("Unable to set current page's homepage dot html link for page path {}. Fallback logo link used ({}).", currentHomepagePath, fallbackLogoLink);
		}

		LOG.debug("setLogoLink : END");
		return context;
	}
	
	private static VelocityContext setCurrentLanguage(SlingHttpServletRequest request, VelocityContext context){
		LOG.debug("setCurrentLanguage : START");
		final EatonSiteConfigModel eatonSiteConfigModel = request.adaptTo(EatonSiteConfigModel.class);
		if (null != eatonSiteConfigModel) {
			String eatonSiteConfigCurrentLanguage = eatonSiteConfigModel.getCurrentLanguage();
			if (StringUtils.isNotEmpty(eatonSiteConfigCurrentLanguage)){
				context.put(EATON_SITE_CONFIG_CURRENT_LANGUAGE_KEY, eatonSiteConfigCurrentLanguage);
				LOG.debug("Current language set to {}", eatonSiteConfigCurrentLanguage);
			} else {
				LOG.error("Unable to get current language from site configuration");
			}
		} else {
			LOG.error("Unable to adapt sling request to EatonSiteConfigModel class");
		}
		LOG.debug("setCurrentLanguage : END");
		return context;
	}

	@Override
	public boolean isEnablePdfGeneration() {
		return enablePdfGeneration;
	}

	private static String addSpaceForMicroChar(String text, int numberOfSpaces) {
		if (text == null) {
			return null;
		}	
		return PATTERN.matcher(text).replaceAll(MICRO_CHAR + NBSP.repeat(numberOfSpaces));
	}
}