package com.eaton.platform.core.util;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.i18n.I18n;
import com.day.cq.replication.ReplicationLog;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.webservicesupport.Configuration;
import com.day.cq.wcm.webservicesupport.ConfigurationManager;
import com.day.cq.wcm.webservicesupport.ConfigurationManagerFactory;
import com.eaton.platform.core.bean.CountryLanguageCodeBean;
import com.eaton.platform.core.bean.HowToBuyBean;
import com.eaton.platform.core.bean.ProductFamilyPDHDetails;
import com.eaton.platform.core.bean.SecondaryLinksBean;
import com.eaton.platform.core.bean.SkuCidDocBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.core.models.ITM;
import com.eaton.platform.core.models.ITM.ITMATRBT;
import com.eaton.platform.core.models.ITM.ITMATRBT.CONTENT;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.informatica.util.InformaticaUtil;
import com.eaton.platform.integration.qr.constants.QRConstants;
import com.eaton.platform.integration.varnish.constants.VarnishConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.xss.XSSAPI;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.activation.MimetypesFileTypeMap;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;

import static com.eaton.platform.core.constants.AEMConstants.CQ_CLOUD_SERVICE_CONFIGS;
import static com.eaton.platform.core.constants.CommonConstants.SITE_CLOUD_CONFIG_NODE_NAME;

/**
 * The Class CommonUtil.
 */
public class CommonUtil {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);
	/** The Constant LOCATION. */
	private static final String LOCATION = "location";
	/** The Constant CAMPAIGN_PATH. */
	private static final String CAMPAIGN_PATH = "/content/campaigns/";
	/** The Constant JCR_CONTENT. */
	public static final String JCR_CONTENT = "jcr:content";
	/** The Constant STAR_CHAR. */
	private static final char STAR_CHAR = '*';
	/** The Constant STAR_ESCAPE_CHAR. */
	private static final String STAR_ESCAPE_CHAR = "%2A";
	/** The Constant TWO. */
	private static final int TWO = 2;
	/** The Constant SPACE_ESCAPE_CHAR1. */
	private static final String SPACE_ESCAPE_CHAR1 = "%20";
	/** The Constant PLUS_CHAR. */
	private static final char PLUS_CHAR = '+';
	/** The Constant PERCENTAGE_CHAR. */
	private static final char PERCENTAGE_CHAR = '%';
	/** The Constant SEVEN_CHAR. */
	private static final char SEVEN_CHAR = '7';
	/** The Constant E_CHAR. */
	private static final char E_CHAR = 'E';
	/** The Constant TILD_CHAR. */
	private static final char TILD_CHAR = '~';
	/** The Constant SPACE_STRING. */
	public static final String SPACE_STRING = " ";
	/** The Constant BACKSLASH_SINGLE_QUOTE. */
	public static final String BACKSLASH_SINGLE_QUOTE = "\\'";
	/** The Constant SINGLE_QUOTE. */
	public static final String SINGLE_QUOTE = "'";
	/** The Constant REG_EXPR. */
	private static final String REG_EXPR = "(?s)<[^>]*>(\\s*<[^>]*>)*";
	/** The Constant EMPTY_ARRAY. */
	private static final String[] EMPTY_ARRAY = new String[0];
	private static final String STR_CONSTANT = "java.lang.String";

	/**
	 * Instantiates a new common util.
	 */
	private CommonUtil() {
		LOGGER.debug("Inside CommonUtil constructor");
	}

	/**
	 * Get date format
	 * this method can be used for getting the date format
	 * return parsed date
	 */
	public static Date getDateFormat(final String dateFormat,final String date) {
		Date parseDate = null;
		try {
			final DateFormat simpleDateFormat = new SimpleDateFormat(
					dateFormat, Locale.ENGLISH);
			parseDate =simpleDateFormat.parse(date);

		} catch (ParseException e) {
			LOGGER.error("Unable to parse the date {}", e.getMessage());
		}
		return parseDate;
	}
	/**
	 * Get compare dates
	 * this method can be used for getting the compare date
	 * @return date difference
	 */
	public static int compareDate(final Date date1,final Date date2){

		return  -1 * (date1.compareTo(date2));
	}
	/**
	 * Gets the string property.
	 *
	 * @param valueMap
	 *            the value map
	 * @param key
	 *            the key
	 * @param xssAPI
	 *            the xss api
	 * @return the string property
	 */
	public static String getStringProperty(ValueMap valueMap, String key, XSSAPI xssAPI) {
		LOGGER.debug("CommonUtil :: getStringProperty() :: Start");
		String returnVal = StringUtils.EMPTY;
		if (null != valueMap && null != key){
			if (valueMap.containsKey(key)) {
				returnVal = (String) valueMap.get(key);
				// Remove XSS issues
				returnVal = xssAPI.getValidHref(returnVal);
			}
		}
		LOGGER.debug("CommonUtil :: getStringProperty() :: Exit");
		return returnVal;
	}

	/**
	 * Gets the string property.
	 *
	 * @param valueMap
	 *            the value map
	 * @param propKey
	 *            the prop key
	 * @return the string property
	 */
	public static String getStringProperty(final ValueMap valueMap, final String propKey) {
		// LOGGER.debug("CommonUtil :: getStringProperty() ::
		// Start");CommentedCode
		String retVal = StringUtils.EMPTY;
		if (null != valueMap && null != propKey) {
			if (valueMap.containsKey(propKey)) {
				retVal = valueMap.get(propKey).toString();
			}
		}
		// LOGGER.debug("CommonUtil :: getStringProperty() ::
		// Exit");CommentedCode
		return retVal;
	}

	/**
	 * Gets the string property.
	 *
	 * @param map
	 *            the map
	 * @param propKey
	 *            the prop key
	 * @return the string property
	 */
	public static String getStringProperty(final Map<String, Object> map, final String propKey) {
		LOGGER.debug("CommonUtil :: getStringProperty() :: Start");
		String retVal = StringUtils.EMPTY;
		if (map.containsKey(propKey)) {
			retVal = (String) map.get(propKey);
		}
		LOGGER.debug("CommonUtil :: getStringProperty() :: Exit");
		return retVal;
	}

	/**
	 * Gets the date property in a string format.
	 *
	 * @param valueMap
	 *            the value map
	 * @param propKey
	 *            the prop key
	 * @return the date property in string format This method can be re-factored
	 *         after the utility is run on all the environments.
	 */
	public static String getDateStringProperty(final ValueMap valueMap, final String propKey) {
		LOGGER.debug("CommonUtil :: getDateStringProperty() :: Start");
		Object datepublished = "";
		String strDate = StringUtils.EMPTY;
		if (null != valueMap && null != propKey){
			if (valueMap.containsKey(propKey)) {
				datepublished = valueMap.get(propKey);
				/*
				 * if the utility fails to update any record, below code would
				 * handle the valid date format scenario
				 */
				if (datepublished instanceof Calendar) {
					SimpleDateFormat frmt = new SimpleDateFormat("mm/dd/yy");
					strDate = format((Calendar) datepublished, frmt);
				} else {
					strDate = datepublished.toString();
				}
			}
		}
		LOGGER.debug("CommonUtil :: getDateStringProperty() :: Exit");
		return strDate;
	}

	/**
	 * Gets the date property.
	 *
	 * @param valueMap
	 *            the value map
	 * @param propKey
	 *            the prop key
	 * @return the date property
	 */
	public static Calendar getDateProperty(final ValueMap valueMap, final String propKey) {
		LOGGER.debug("CommonUtil :: getDateProperty() :: Start");
		Calendar dateVal = null;
		if (null != valueMap && null != propKey){
			if (valueMap.containsKey(propKey)) {
				dateVal = (Calendar) valueMap.get(propKey);
			}
		}
		LOGGER.debug("CommonUtil :: getDateProperty() :: Exit");
		return dateVal;
	}

	/**
	 * Gets the date property.
	 *
	 * @param valueMap
	 *            the value map
	 * @param propKey
	 *            the prop key
	 * @param simpleDateFormat
	 *            the simple date format
	 * @return the date property
	 */
	public static String getDateProperty(final ValueMap valueMap, final String propKey,
										 final SimpleDateFormat simpleDateFormat) {
		LOGGER.debug("CommonUtil :: getDateProperty() :: Start");
		String formattedDateVal = StringUtils.EMPTY;
		if (null != valueMap && null != propKey) {
			if (valueMap.containsKey(propKey) && valueMap.get(propKey)!=null) {
					try{
						if(valueMap.get(propKey) instanceof String){
							String publicationDate = (String)valueMap.get(propKey);
							Date date = simpleDateFormat.parse(publicationDate);
							Calendar dateVal = Calendar. getInstance();
							dateVal.setTime(date);
							formattedDateVal = format(dateVal, simpleDateFormat);
						}
						else {
							Calendar dateVal = (Calendar) valueMap.get(propKey);
							formattedDateVal = format(dateVal, simpleDateFormat);
						}
					} catch(Exception e){
						LOGGER.error(" Exception in formatting Date from Date object "+e.getMessage());
					}
					if(formattedDateVal.equals(StringUtils.EMPTY)){
						String publicationDate = (String)valueMap.get(propKey);
						Date date;
						try {
							if(publicationDate!=null){
								date = simpleDateFormat.parse(publicationDate);
								Calendar dateVal = Calendar. getInstance();
								dateVal.setTime(date);
								formattedDateVal = format(dateVal, simpleDateFormat);
							}
						} catch(Exception e){
							LOGGER.error(" Exception in formatting Date from String object "+e.getMessage());
						}
					}
			}
		}
		LOGGER.debug("CommonUtil :: getDateProperty() :: Exit");
		return formattedDateVal;
	}
	/**
	 * Format.
	 *
	 * @param date
	 *            the date
	 * @param simpleDateFormat
	 *            the simple date format
	 * @return the string
	 */
	public static String format(Calendar date, SimpleDateFormat simpleDateFormat) {
		LOGGER.debug("CommonUtil :: format() :: Start");
		simpleDateFormat.setCalendar(date);
		String dateFormatted = simpleDateFormat.format(date.getTime());
		LOGGER.debug("CommonUtil :: format() :: Exit");
		return dateFormatted;
	}
	/**
	 * Format Date.
	 *
	 * @param date
	 *            the date
	 * @param simpleDateFormat
	 *            the simple date format
	 * @return the string
	 */
	public static String formatDate(Date date, String simpleDateFormat) {
		LOGGER.debug("CommonUtil :: formatDate() :: Start");
		SimpleDateFormat sdf = new SimpleDateFormat(simpleDateFormat);
		String dateFormatted = sdf.format(date);
		LOGGER.debug("CommonUtil :: formatDate() :: Exit");
		return dateFormatted;
	}


	/**
	 * getDatefromString.
	 *
	 * @param dateInString
	 *            the String
	 * @return the Date
	 */
	public static Date getDatefromString(String dateInString){
		LOGGER.debug("CommonUtil :: getDatefromString() :: Start");
		SimpleDateFormat formatter = new SimpleDateFormat();
		Date date = null ;
		try {
			date = formatter.parse(dateInString);
		} catch (ParseException e) {
			LOGGER.debug("ParseException occoured", e.getMessage());
		}
		LOGGER.debug("CommonUtil :: getDatefromString() :: Exit");
		return date;
	}

	/**
	 * Gets the string array property.
	 *
	 * @param valueMap
	 *            the value map
	 * @param key
	 *            the key
	 * @return the string array property
	 */
	public static String[] getStringArrayProperty(ValueMap valueMap, String key) {
		LOGGER.debug("CommonUtil :: getStringArrayProperty() :: Start");
		String[] returnVal = EMPTY_ARRAY;
		if (null != valueMap && null != key){
			if (valueMap.containsKey(key)) {
				returnVal = (String[]) valueMap.get(key);
			}
		}
		LOGGER.debug("CommonUtil :: getStringArrayProperty() :: Exit");
		return returnVal;
	}

	/**
	 * Gets the string inherited property.
	 *
	 * @param inheritanceValueMap
	 *            the inheritance value map
	 * @param propKey
	 *            the prop key
	 * @return the string inherited property
	 */
	public static String getStringInheritedProperty(final InheritanceValueMap inheritanceValueMap,
													final String propKey) {
		LOGGER.debug("CommonUtil :: getStringInheritedProperty() :: Start");
		String retVal = inheritanceValueMap.getInherited(propKey, StringUtils.EMPTY);
		LOGGER.debug("CommonUtil :: getStringInheritedProperty() :: Exit");
		return retVal;
	}

	/**
	 * Gets the string array inherited property.
	 *
	 * @param inheritanceValueMap
	 *            the inheritance value map
	 * @param propKey
	 *            the prop key
	 * @return the string array inherited property
	 */
	public static String[] getStringArrayInheritedProperty(final InheritanceValueMap inheritanceValueMap,
														   final String propKey) {
		LOGGER.debug("CommonUtil :: getStringArrayInheritedProperty() :: Start");
		String[] returnVal = inheritanceValueMap.getInherited(propKey, EMPTY_ARRAY);
		LOGGER.debug("CommonUtil :: getStringArrayInheritedProperty() :: Exit");
		return returnVal;
	}

	/**
	 * Escape html.
	 *
	 * @param htmlString
	 *            the html string
	 * @return the string
	 */
	public static String escapeHTML(final String htmlString) {
		LOGGER.debug("CommonUtil :: escapeHTML() :: Start");
		String output = htmlString;
		if (output != null) {
			output = output.replaceAll(REG_EXPR, SPACE_STRING).replaceAll(BACKSLASH_SINGLE_QUOTE, SINGLE_QUOTE)
					.replaceAll(".html", "").trim();
		}
		LOGGER.debug("CommonUtil :: escapeHTML() :: Exit");
		return output;
	}

	/**
	 * Gets the i18 n from user source.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param key
	 *            the key
	 * @return the i18 n from user source
	 */
	public static String getI18NFromUserSource(SlingHttpServletRequest slingRequest, String key) {
		LOGGER.debug("CommonUtil :: getI18NFromUserSource() :: Start");
		I18n i18n = new I18n(slingRequest);
		String retVal = i18n.get(key);
		LOGGER.debug("CommonUtil :: getI18NFromUserSource() :: Exit");
		return retVal;
	}

	public static String getCartUrlFromPage(Page currentPage,ResourceResolver adminResourceResolver){
		SiteConfigModel siteConfig = getSiteConfigFromPage(currentPage,adminResourceResolver);
		if(null != siteConfig){
			return siteConfig.getCartUrl();
		}
		return StringUtils.EMPTY;
	}

	public static SiteConfigModel getSiteConfigFromPage(Page currentPage,ResourceResolver adminResourceResolver){
		if (null != adminResourceResolver) {
			final Resource contentResource = currentPage.getContentResource();
			final HierarchyNodeInheritanceValueMap valueMap = new HierarchyNodeInheritanceValueMap(contentResource);
			final String[] services = valueMap.getInherited(CQ_CLOUD_SERVICE_CONFIGS, new String[]{});
			final Optional<ConfigurationManager> configurationManagerOptional = Optional
					.ofNullable(adminResourceResolver.adaptTo(ConfigurationManager.class));
			if (configurationManagerOptional.isPresent()) {
				final ConfigurationManager configurationManager = configurationManagerOptional.get();
				final Optional<Configuration> configurationOptional = Optional.ofNullable(configurationManager
						.getConfiguration(SITE_CLOUD_CONFIG_NODE_NAME, services));
				if (configurationOptional.isPresent()) {
					final Resource siteContentResource = configurationOptional.get().getContentResource();
					SiteConfigModel siteConfig = siteContentResource.adaptTo(SiteConfigModel.class);
					if(null != siteConfig){
						return siteConfig;
					}
				}
			} else {
				LOGGER.error("Resource resolver does not exist");
			}
		}
		return null;
	}

	public static Page getCurrentPagefromRequestReferer(SlingHttpServletRequest request){
		final String refererURL = CommonUtil.getRefererURL(request);
		final ResourceResolver resourceResolver = request.getResourceResolver();
		final String pagePath = CommonUtil.getContentPath(resourceResolver, refererURL);
		final Page currentPage = resourceResolver.resolve(pagePath).adaptTo(Page.class);
		return currentPage;
	}

	public static String getCartUrl(SlingHttpServletRequest slingRequest) {
     LOGGER.debug("CommonUtil :: getCartUrl() :: Start");
		final EatonSiteConfigModel eatonSiteConfigModel = slingRequest.adaptTo(EatonSiteConfigModel.class);
		if (null != eatonSiteConfigModel) {
			final SiteConfigModel siteConfiguration = eatonSiteConfigModel.getSiteConfig();
			if(null != siteConfiguration){
				return siteConfiguration.getCartUrl();
			}
		}
		LOGGER.debug("CommonUtil :: getCartUrl() :: Exit");
		return StringUtils.EMPTY;
	}

	/**
	 * Gets the i18 n from resource bundle.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param currentPage
	 *            the current page
	 * @param key
	 *            the key
	 * @return the i18 n from resource bundle
	 */
	public static String getI18NFromResourceBundle(SlingHttpServletRequest slingRequest, Page currentPage, String key) {
		LOGGER.debug("CommonUtil :: getI18NFromResourceBundle() :: Start");
		Locale pageLang = currentPage.getLanguage(true);
		String retVal = getI18NFromLocale(slingRequest, key, pageLang);
		if(retVal == null || StringUtils.equals(key, retVal)){
			Locale fallbackLang = new Locale(CommonConstants.FALLBACK_LOCALE);
			retVal = getI18NFromLocale(slingRequest, key, fallbackLang);
		}
		LOGGER.debug("CommonUtil :: getI18NFromResourceBundle() :: Exit");
		return retVal;
	}

	/**
	 * Gets the i18 n from locale.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param currentPage
	 *            the current page
	 * @param key
	 *            the key
	 * @param args
	 *            the args
	 * @return the i18 n from locale
	 */
	public static String getI18NFromResourceBundle(SlingHttpServletRequest slingRequest, Page currentPage, String key,
												   Object... args) {
		LOGGER.debug("CommonUtil :: getI18NFromResourceBundle() Bundle :: Start");
		Locale pageLang = currentPage.getLanguage(false);
		String retVal = getI18NFromLocale(slingRequest, key, pageLang, args);
		LOGGER.debug("CommonUtil :: getI18NFromResourceBundle() Bundle :: Exit");
		return retVal;
	}

	/**
	 * Gets the i18 n from locale.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param key
	 *            the key
	 * @param pageLang
	 *            the page lang
	 * @param args
	 *            the args
	 * @return the i18 n from locale
	 */
	public static String getI18NFromLocale(SlingHttpServletRequest slingRequest, String key, Locale pageLang,
										   Object... args) {
		LOGGER.debug("CommonUtil :: getI18NFromLocale() :: Start");
		ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLang);
		String retVal = I18n.get(resourceBundle, key, null, args);
		LOGGER.debug("CommonUtil :: getI18NFromLocale() :: Exit");
		return retVal;
	}

	/**
	 * Gets the i18 n from locale.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param key
	 *            the key
	 * @param pageLang
	 *            the page lang
	 * @return the i18 n from locale
	 */
	public static String getI18NFromLocale(SlingHttpServletRequest slingRequest, String key, Locale pageLang) {
		LOGGER.debug("CommonUtil :: getI18NFromLocale() ARGS :: Start");
		ResourceBundle resourceBundle = slingRequest.getResourceBundle(pageLang);
		I18n i18n = new I18n(resourceBundle);
		String retVal = i18n.get(key);
		LOGGER.debug("CommonUtil :: getI18NFromLocale() ARGS :: Exit");
		return retVal;
	}

	/**
	 * Gets the i18 n from user source.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param key
	 *            the key
	 * @param defaultValue
	 *            the default value
	 * @return the i18 n from user source
	 */
	public static String getI18NFromUserSource(SlingHttpServletRequest slingRequest, String key, String defaultValue) {
		LOGGER.debug("CommonUtil :: getI18NFromUserSource() :: Start");
		String retVal = getI18NFromUserSource(slingRequest, key);
		if (StringUtils.isBlank(retVal) || StringUtils.equals(key, retVal)) {
			retVal = defaultValue;
		}
		LOGGER.debug("CommonUtil :: getI18NFromUserSource() :: Exit");
		return retVal;
	}

	/**
	 * Gets the i18 n from resource bundle.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param currentPage
	 *            the current page
	 * @param key
	 *            the key
	 * @param defaultValue
	 *            the default value
	 * @return the i18 n from resource bundle
	 */
	public static String getI18NFromResourceBundle(SlingHttpServletRequest slingRequest, Page currentPage, String key,
												   String defaultValue) {
		LOGGER.debug("CommonUtil :: getI18NFromResourceBundle() :: Start");
		String retVal = getI18NFromResourceBundle(slingRequest, currentPage, key);
		if (StringUtils.isBlank(retVal) || StringUtils.equals(key, retVal)) {
			retVal = defaultValue;
		}
		LOGGER.debug("CommonUtil :: getI18NFromResourceBundle() :: Exit");
		return retVal;
	}

	/**
	 * Generate hash value.
	 *
	 * @param pagePath
	 *            the page path
	 * @return the string
	 */
	public static String generateHashValue(final String pagePath) {
		LOGGER.debug("CommonUtil :: generateHashValue() :: Start");
		CRC32 crc32Obj = new CRC32();
		crc32Obj.update(pagePath.getBytes());
		String retVal = Long.toHexString(crc32Obj.getValue());
		if (StringUtils.isBlank(retVal)) {
			retVal = StringUtils.EMPTY;
		}
		LOGGER.debug("CommonUtil :: generateHashValue() :: Exit");
		return retVal;
	}

	/**
	 * Encode.
	 *
	 * @param value
	 *            the value
	 * @param encType
	 *            the enc type
	 * @return the string
	 * @throws EatonApplicationException
	 *             the eaton application exception
	 */
	public static String encode(final String value, final String encType) throws EatonApplicationException {
		LOGGER.debug("CommonUtil :: encode() :: Start");
		String encoded = null;
		StringBuilder buf = new StringBuilder();
		try {
			encoded = URLEncoder.encode(value, encType);
			if (encoded == null) {
				LOGGER.error("Unable to get encoded value. encoded var is null. Hence returning empty buf");
				return buf.toString();
			}
			final int length = encoded.length();
			buf = new StringBuilder(length);
			for (int i = 0; i < length; i++) {
				char focus = encoded.charAt(i);
				if (focus == STAR_CHAR) {
					buf.append(STAR_ESCAPE_CHAR);
				} else if (focus == PLUS_CHAR) {
					buf.append(SPACE_ESCAPE_CHAR1);
				} else if (focus == PERCENTAGE_CHAR && (i + 1) < length && encoded.charAt(i + 1) == SEVEN_CHAR
						&& encoded.charAt(i + 2) == E_CHAR) {
					buf.append(TILD_CHAR);
					i += TWO;
				} else {
					buf.append(focus);
				}
			}
		} catch (UnsupportedEncodingException exception) {
			// ideally never supposed to occur.
			LOGGER.error("Encoding exception for " + value + " and coding type" + encType, exception.getMessage());
			throw new EatonApplicationException("", exception);
		} catch (Exception exception) {
			LOGGER.error("Unknown exception for " + value + " and coding type" + encType, exception.getMessage());
			throw new EatonApplicationException("", exception);
		}
		LOGGER.debug("CommonUtil :: encode() :: Exit");
		return buf.toString();
	}

	/**
	 * Days between.
	 *
	 * @param d1
	 *            the d1
	 * @param d2
	 *            the d2
	 * @return the int
	 */
	public static int daysBetween(Date d1, Date d2) {
		return (int) ((d1.getTime() - d2.getTime()) / (1000 * 60 * 60 * 24));
	}

	/**
	 * Gets the original page.
	 *
	 * @param currentPage
	 *            the current page
	 * @param resolver
	 *            the resolver
	 * @return the original page
	 */
	public static Page getOriginalPage(final Page currentPage, final ResourceResolver resolver) {
		LOGGER.debug("CommonUtil :: getOriginalPage() :: Start");
		LOGGER.debug("currentPage Path - " + currentPage.getPath());
		Page originalPage = currentPage;
		if (currentPage != null && currentPage.getPath().startsWith(CAMPAIGN_PATH)) {
			ValueMap pageProp = currentPage.getProperties();
			if (pageProp.containsKey(LOCATION)) {
				String locationVal = (String) pageProp.get(LOCATION);
				String orginalPagePath = locationVal.substring(0, locationVal.indexOf(JCR_CONTENT));
				PageManager pageManager = null;
				if (resolver != null) {
					pageManager = resolver.adaptTo(PageManager.class);
				}
				if (pageManager != null)
					originalPage = pageManager.getPage(orginalPagePath);
			}
		}
		LOGGER.debug("result Page Path - " + originalPage.getPath());
		LOGGER.debug("CommonUtil :: getOriginalPage() :: Exit");
		return originalPage;
	}

	/**
	 * Gets the list from string array.
	 *
	 * @param inputStringArray
	 *            the input string array
	 * @return the list from string array
	 */
	public static List<String> getListFromStringArray(String[] inputStringArray) {
		LOGGER.debug("CommonUtil :: getListFromStringArray() :: Start");
		List<String> outputStringList = null;
		if (inputStringArray != null && inputStringArray.length > 0) {
			outputStringList = Arrays.<String>asList(inputStringArray);
		}
		LOGGER.debug("CommonUtil :: getListFromStringArray() :: Exit");
		return outputStringList;
	}

	/**
	 * Gets the list from string array.
	 *
	 * @param inputStringArray
	 *            the input string array
	 * @return the list from string array
	 */
	public static Map<String, String> getListFromStringMap(String[] inputStringArray) {
		LOGGER.debug("CommonUtil :: getListFromStringMap() :: Start");
		Map<String, String> outputStringMap = new HashMap<>();
		if (inputStringArray != null && inputStringArray.length > 0) {
			for (String outputString : inputStringArray) {
				String[] outputStringArray = StringUtils.split(outputString, "~");
				if (outputStringArray != null && outputStringArray.length > 0) {
					outputStringMap.put(outputStringArray[0], outputStringArray[1]);
				}
			}
		}
		LOGGER.debug("CommonUtil :: getListFromStringMap() :: Exit");
		return outputStringMap;
	}

	/**
	 * Gets the sling request string attribute.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param attrKey
	 *            the attr key
	 * @return the sling request string attribute
	 */
	public static String getSlingRequestStringAttribute(final SlingHttpServletRequest slingRequest,
														final String attrKey) {
		LOGGER.debug("CommonUtil :: getSlingRequestStringAttribute() :: Start");
		String attrValue = StringUtils.EMPTY;
		if (slingRequest.getAttribute(attrKey) != null) {
			attrValue = (String) slingRequest.getAttribute(attrKey);
		}
		LOGGER.debug("CommonUtil :: getSlingRequestStringAttribute() :: Exit");
		return attrValue;
	}

	/**
	 * Gets the sling request parameter.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param attrKey
	 *            the attr key
	 * @return the sling request parameter
	 */
	public static String getSlingRequestParameter(final SlingHttpServletRequest slingRequest, final String attrKey) {
		LOGGER.debug("CommonUtil :: getSlingRequestParameter() :: Start");
		String attrValue = StringUtils.EMPTY;
		if (slingRequest.getParameter(attrKey) != null) {
			attrValue = slingRequest.getParameter(attrKey);
		}
		LOGGER.debug("CommonUtil :: getSlingRequestParameter() :: Exit");
		return attrValue;
	}

	/**
	 * Gets the sling request integer attribute.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param attrKey
	 *            the attr key
	 * @return the sling request integer attribute
	 */
	public static Integer getSlingRequestIntegerAttribute(final SlingHttpServletRequest slingRequest,
														  final String attrKey) {
		LOGGER.debug("CommonUtil :: getSlingRequestIntegerAttribute() :: Start");
		Integer attrValue = 0;
		if (slingRequest.getAttribute(attrKey) != null) {
			String attrStringValue = (String) slingRequest.getAttribute(attrKey);
			if (StringUtils.isNotBlank(attrStringValue)) {
				attrValue = Integer.parseInt(attrStringValue);
			}
		}
		LOGGER.debug("CommonUtil :: getSlingRequestIntegerAttribute() :: Exit");
		return attrValue;
	}

	/**
	 * Gets the sling request integer parameter.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param attrKey
	 *            the attr key
	 * @return the sling request integer parameter
	 */
	public static Integer getSlingRequestIntegerParameter(final SlingHttpServletRequest slingRequest,
														  final String attrKey) {
		LOGGER.debug("CommonUtil :: getSlingRequestIntegerParameter() :: Start");
		Integer attrValue = 0;
		if (slingRequest.getParameter(attrKey) != null) {
			String attrStringValue = slingRequest.getParameter(attrKey);
			if (StringUtils.isNotBlank(attrStringValue)) {
				attrValue = Integer.parseInt(attrStringValue);
			}
		}
		LOGGER.debug("CommonUtil :: getSlingRequestIntegerParameter() :: Exit");
		return attrValue;
	}

	/**
	 * Gets the sling request double attribute.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param attrKey
	 *            the attr key
	 * @return the sling request double attribute
	 */
	public static Double getSlingRequestDoubleAttribute(final SlingHttpServletRequest slingRequest,
														final String attrKey) {
		LOGGER.debug("CommonUtil :: getSlingRequestDoubleAttribute() :: Start");
		Double attrValue = 0.0;
		if (slingRequest.getAttribute(attrKey) != null) {
			String attrStringValue = (String) slingRequest.getAttribute(attrKey);
			if (StringUtils.isNotBlank(attrStringValue)) {
				attrValue = Double.parseDouble(attrStringValue);
			}
		}
		LOGGER.debug("CommonUtil :: getSlingRequestDoubleAttribute() :: Exit");
		return attrValue;
	}

	/**
	 * Gets the sling request double parameter.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param attrKey
	 *            the attr key
	 * @return the sling request double parameter
	 */
	public static Double getSlingRequestDoubleParameter(final SlingHttpServletRequest slingRequest,
														final String attrKey) {
		LOGGER.debug("CommonUtil :: getSlingRequestDoubleParameter() :: Start");
		Double attrValue = 0.0;
		if (slingRequest.getParameter(attrKey) != null) {
			String attrStringValue = slingRequest.getParameter(attrKey);
			if (StringUtils.isNotBlank(attrStringValue)) {
				attrValue = Double.parseDouble(attrStringValue);
			}
		}
		LOGGER.debug("CommonUtil :: getSlingRequestDoubleParameter() :: Exit");
		return attrValue;
	}

	/**
	 * Gets the sling request boolean attribute.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param attrKey
	 *            the attr key
	 * @return the sling request boolean attribute
	 */
	public static Boolean getSlingRequestBooleanAttribute(final SlingHttpServletRequest slingRequest,
														  final String attrKey) {
		LOGGER.debug("CommonUtil :: getSlingRequestBooleanAttribute() :: Start");
		Boolean attrValue = false;
		if (slingRequest.getAttribute(attrKey) != null) {
			String attrStringValue = (String) slingRequest.getAttribute(attrKey);
			if (StringUtils.isNotBlank(attrStringValue)) {
				attrValue = Boolean.parseBoolean(attrStringValue);
			}
		}
		LOGGER.debug("CommonUtil :: getSlingRequestBooleanAttribute() :: Exit");
		return attrValue;
	}

	/**
	 * Gets the sling request boolean parameter.
	 *
	 * @param slingRequest
	 *            the sling request
	 * @param attrKey
	 *            the attr key
	 * @return the sling request boolean parameter
	 */
	public static Boolean getSlingRequestBooleanParameter(final SlingHttpServletRequest slingRequest,
														  final String attrKey) {
		LOGGER.debug("CommonUtil :: getSlingRequestBooleanParameter() :: Start");
		Boolean attrValue = false;
		if (slingRequest.getParameter(attrKey) != null) {
			String attrStringValue = slingRequest.getParameter(attrKey);
			if (StringUtils.isNotBlank(attrStringValue)) {
				attrValue = Boolean.parseBoolean(attrStringValue);
			}
		}
		LOGGER.debug("CommonUtil :: getSlingRequestBooleanParameter() :: Exit");
		return attrValue;
	}

	/**
	 * Gets the link title.
	 *
	 * @param linkTitle
	 *            the link title
	 * @param link
	 *            the link
	 * @param resourceResolver
	 *            the resource resolver
	 * @return title
	 */
	public static String getLinkTitle(String linkTitle, String link, ResourceResolver resourceResolver) {
		LOGGER.debug("CommonUtil :: getLinkTitle() :: Start");
		String titleValue = "";
		titleValue = linkTitle;
		if (null == linkTitle) {
			if (null != link && startsWithAnySiteContentRootPath(link)) {
				if ((resourceResolver != null) && (resourceResolver.getResource(link) != null)) {
					Resource res = resourceResolver.getResource(link);
					Page linkPage = null;
					if (res != null) {
						linkPage = res.adaptTo(Page.class);
					}
					if (linkPage != null)
						titleValue = null != linkPage.getNavigationTitle() ? linkPage.getNavigationTitle()
								: null != linkPage.getPageTitle() ? linkPage.getPageTitle() :(linkPage.getTitle() != null ? linkPage.getTitle() : linkPage.getName());
				}
			} else {
				titleValue = StringUtils.EMPTY;
			}
		}
		LOGGER.debug("CommonUtil :: getLinkTitle() :: Exit");
		return titleValue;
	}


	public static String getLinkPagePath(String link) {
		LOGGER.debug("CommonUtil :: getLinkedPage() :: Start");
		String pagePathLink =StringUtils.EMPTY;
		if(link.startsWith(CommonConstants.CONTENT_ROOT_FOLDER)) {
			pagePathLink = link.replace(CommonConstants.HTML_EXTN, StringUtils.EMPTY);
		} else {
				try {
					URL pageUrl=new URL(link);
					if(null != pageUrl) {
						if (pageUrl.getPath().startsWith(CommonConstants.CONTENT_ROOT_FOLDER)) {
							pagePathLink = pageUrl.getPath().replace(CommonConstants.HTML_EXTN, StringUtils.EMPTY);
						} else {
							pagePathLink = CommonConstants.CONTENT_ROOT_FOLDER.concat(pageUrl.getPath()).replace(CommonConstants.HTML_EXTN, StringUtils.EMPTY);
						}
					}
				} catch (MalformedURLException e) {
					LOGGER.error("CommonUtil :: getLinkedPage() :: MalformedURLException",e);
				}
		}
		LOGGER.debug("CommonUtil :: getLinkedPage() :: Exit");
		return pagePathLink;
	}


	/**
	 * This method returns the cloud configuration page object.
	 *
	 * @param configManagerFctry
	 *            the config manager fctry
	 * @param resolver
	 *            the resolver
	 * @param pageResource
	 *            the page resource
	 * @param configName
	 *            the config name
	 * @return the cloud config obj
	 */
	public static Configuration getCloudConfigObj(ConfigurationManagerFactory configManagerFctry,
												  ResourceResolver resolver, Resource pageResource, String configName) {
		LOGGER.debug("CommonUtil :: getCloudConfigObj() :: Start");
		String[] services = null;
		Configuration configObj = null;
		Page currentPage = pageResource.adaptTo(Page.class);
		ConfigurationManager configMgr = configManagerFctry.getConfigurationManager(resolver);
		// get cq:cloudservices property value from current page properties
		if (currentPage != null)
			services = getStringArrayProperty(currentPage.getProperties(), CommonConstants.CLOUD_SERVICES);
		// if cloud services are not found at current page, then get inherited
		// page properties of the current page.
		if (services != null) {
			if (services.length == 0) {
				InheritanceValueMap currentPageInheritedProp = new HierarchyNodeInheritanceValueMap(pageResource);
				services = getStringArrayInheritedProperty(currentPageInheritedProp, CommonConstants.CLOUD_SERVICES);
			}
		}
		// if cloud services do not exist in inherited page properties, get
		// cloud services from home page
		if (services != null) {
			if (services.length == 0) {
				Page homePage = getHomePage(currentPage);
				if (homePage != null)
					services = getStringArrayProperty(homePage.getProperties(), CommonConstants.CLOUD_SERVICES);
			}
			if(StringUtils.isNotEmpty(configName) && services.length >0){
				configObj = configMgr.getConfiguration(configName, services);
			}
		}
		LOGGER.debug("CommonUtil :: getCloudConfigObj() :: Exit");
		return configObj;
	}

	public static String getSKUPagePath(final Page page, final String skuPageName) {
		String homePagePath = CommonUtil.getHomePagePath(page);
		if(null != page.getContentResource()) {
			homePagePath = CommonUtil.dotHtmlLinkSKU(homePagePath, page.getContentResource().getResourceResolver());
		}
		return homePagePath + skuPageName;
	}

	/**
	 * Gets the home page path.
	 *
	 * @param page
	 *            the page
	 * @return the home page path
	 */
	public static String getHomePagePath(Page page) {
		LOGGER.debug("CommonUtil :: getHomePagePath() :: Start");
		String homepagePath = StringUtils.EMPTY;
		if(null != page) {
			LOGGER.debug("Depth -- " + page.getDepth());

			if (startsWithAnySiteContentRootPath(page.getPath()) && page.getDepth() > CommonConstants.COUNTRY_PAGE_DEPTH) {
				homepagePath = page.getAbsoluteParent(CommonConstants.LANGUAGE_PAGE_DEPTH - 1).getPath();
			}
		}
		LOGGER.debug("CommonUtil :: getHomePagePath() :: Exit");
		return homepagePath;
	}

	/**
	 * This method gets the home page.
	 *
	 * @param page
	 *            the page
	 * @return the home page
	 */
	public static Page getHomePage(Page page) {
		LOGGER.debug("CommonUtil :: getHomePage() :: Start");
		Page homePage = null;
		if ((null != page && startsWithAnySiteContentRootPath(page.getPath()))
				&& page.getDepth() > CommonConstants.COUNTRY_PAGE_DEPTH) {
			LOGGER.debug("Depth - " + page.getDepth());
			homePage = page.getAbsoluteParent(CommonConstants.LANGUAGE_PAGE_DEPTH - 1);
		}
		LOGGER.debug("CommonUtil :: getHomePage() :: Exit");
		return homePage;
	}

	/**
	 * This method checks if it is a home page.
	 *
	 * @param page
	 *            the page
	 * @return isHomepage
	 */
	public static boolean isHomePagePath(Page page) {
		LOGGER.debug("CommonUtil :: isHomePagePath() :: Start");
		LOGGER.debug("Depth --- " + page.getDepth());
		boolean isHomepage = false;
		if (startsWithAnySiteContentRootPath(page.getPath())
				&& page.getDepth() == CommonConstants.LANGUAGE_PAGE_DEPTH) {
			isHomepage = true;
		}
		LOGGER.debug("CommonUtil :: isHomePagePath() :: Exit");
		return isHomepage;
	}

	/**
	 * This method reads the jcr:data of file and returns the response.
	 *
	 * @param adminResourceResolver
	 *            the admin resource resolver
	 * @param filePath
	 *            the file path
	 * @return response from file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public static String getResponseStringFromFile(ResourceResolver adminResourceResolver, String filePath)
			throws IOException, RepositoryException {
		LOGGER.debug("CommonUtil :: getResponseStringFromFile() :: Start");
		StringWriter writer = new StringWriter();
		Resource res = adminResourceResolver
				.getResource(filePath.concat("/jcr:content/renditions/original/jcr:content"));
		if (res != null) {
			ValueMap resvm = res.getValueMap();
			if (resvm.containsKey("jcr:data")) {
				InputStream stream = (InputStream) resvm.get("jcr:data");
				IOUtils.copy(stream, writer, "UTF-8");
			}
		}
		LOGGER.debug("CommonUtil :: getResponseStringFromFile() :: Exit");
		return writer.toString();
	}


	/**
	 * This method appends the .html if link is internal
	 *
	 * @param link
	 *            the link
	 * @return the link
	 */
	public static String dotHtmlLink(String link) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("CommonUtil :: dotHtmlLink() :: Start " + link);
		}
		String linkPath = link;
		if (null != linkPath) {
			if (startsWithAnySiteContentRootPath(linkPath) && !StringUtils.endsWith(linkPath, CommonConstants.HTML_EXTN)) {
				linkPath = linkPath + CommonConstants.HTML_EXTN;
			} else if (StringUtils.startsWith(linkPath, CommonConstants.HTTP) || StringUtils.startsWith(linkPath, CommonConstants.HTTPS)) {
				linkPath = link;
			} else if (StringUtils.startsWith(linkPath, CommonConstants.WWW)) {
				linkPath = CommonConstants.HTTP_SLASH + linkPath;
			}
		} else {
			linkPath = StringUtils.EMPTY;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("CommonUtil :: dotHtmlLink() :: Exit " + linkPath);
		}
		return linkPath;
	}
	/**
	 * This method externalize the link if link is internal
	 *
	 * @param link
	 *            the link
	 * @param resourceResolver
	 *            the resource resolver
	 * @return the link
	 */
	public static String dotHtmlLink(String link,ResourceResolver resourceResolver) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("CommonUtil :: dotHtmlLink() with externalizer :: Start " + link);
		}
		String linkPath = link;
		if (resourceResolver!=null) {
			Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
			if (null != linkPath) {
				String domainName=getExternalizerDomainNameBySiteRootPath(linkPath);
				if (startsWithAnySiteContentRootPath(linkPath) && null != externalizer && !StringUtils.contains(linkPath, ".html")) {
					linkPath = externalizer.externalLink(resourceResolver,domainName,linkPath) + CommonConstants.HTML_EXTN;
				} else if (null != externalizer && !StringUtils.startsWith(linkPath, CommonConstants.HTTP) && !StringUtils.startsWith(linkPath, CommonConstants.HTTPS) && !StringUtils.startsWith(linkPath, CommonConstants.FILE_TYPE_APPLICATION_JAVASCRIPTS_VALUE)) {
					linkPath = externalizer.externalLink(resourceResolver,domainName,linkPath);
				} else if (StringUtils.startsWith(linkPath, CommonConstants.HTTP) || StringUtils.startsWith(linkPath, CommonConstants.HTTPS) || StringUtils.startsWith(linkPath, CommonConstants.FILE_TYPE_APPLICATION_JAVASCRIPTS_VALUE)) {
					linkPath = link;
				} else if (StringUtils.startsWith(linkPath, CommonConstants.WWW)) {
					linkPath = CommonConstants.HTTP_SLASH + linkPath;
				}
			} else {
				linkPath = StringUtils.EMPTY;
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("CommonUtil :: dotHtmlLink() with externalizer :: Exit " + linkPath);
		}
		return linkPath;
	}

	/**
	 * This method externalize the link if link is internal
	 *
	 * @param link
	 *            the link
	 * @param resourceResolver
	 *            the resource resolver
	 * @param domainKey
	 * @return the link
	 */
	public static String dotHtmlLink(String link, ResourceResolver resourceResolver, String domainKey) {

		LOGGER.debug("CommonUtil :: dotHtmlLink() with externalizer :: Start");
		LOGGER.debug("CommonUtil :: dotHtmlLink() with externalizer :: link :: {}", link);
		LOGGER.debug("CommonUtil :: dotHtmlLink() with externalizer :: domainKey :: {}", domainKey);

		String linkPath = link;
		Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
		if (null != linkPath) {
			if (startsWithAnySiteContentRootPath(linkPath) && null != externalizer && !StringUtils.contains(linkPath, CommonConstants.HTML_EXTN)) {
				linkPath = externalizer.externalLink(resourceResolver, domainKey, linkPath) + CommonConstants.HTML_EXTN;
			} else if (null != externalizer && !StringUtils.startsWith(linkPath, CommonConstants.HTTP) && !StringUtils.startsWith(linkPath, CommonConstants.HTTPS) && !StringUtils.startsWith(linkPath, CommonConstants.FILE_TYPE_APPLICATION_JAVASCRIPTS_VALUE)) {
				linkPath = externalizer.externalLink(resourceResolver, domainKey, linkPath);
			} else if (StringUtils.startsWith(linkPath, CommonConstants.WWW)) {
				linkPath = CommonConstants.HTTP_SLASH + linkPath;
			}
		} else {
			linkPath = StringUtils.EMPTY;
		}
		LOGGER.debug("CommonUtil :: dotHtmlLink() with externalizer :: linkPath :: {}", linkPath);
		LOGGER.debug("CommonUtil :: dotHtmlLink() with externalizer :: Exit ");

		return linkPath;
	}

	/**
	 * This method externalize the link if link is SKU page
	 *
	 * @param link
	 *            the link
	 * @param resourceResolver
	 *            the resource resolver
	 * @return the link
	 */
	public static String dotHtmlLinkSKU(String link,ResourceResolver resourceResolver) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("CommonUtil :: dotHtmlLinkSKU() with externalizer :: Start " + link);
		}
		String linkPath = link;
		Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
		if (null != linkPath) {
			String domainName=getExternalizerDomainNameBySiteRootPath(linkPath);
			if (startsWithAnySiteContentRootPath(linkPath) && null != externalizer) {
				linkPath = externalizer.externalLink(resourceResolver,domainName,linkPath);
			} else if (null != externalizer && !StringUtils.startsWith(linkPath, CommonConstants.HTTP) && !StringUtils.startsWith(linkPath, CommonConstants.HTTPS) && !StringUtils.startsWith(linkPath, CommonConstants.FILE_TYPE_APPLICATION_JAVASCRIPTS_VALUE)) {
				linkPath = externalizer.externalLink(resourceResolver,domainName,linkPath);
			} else if (StringUtils.startsWith(linkPath, CommonConstants.HTTP) || StringUtils.startsWith(linkPath, CommonConstants.HTTPS) || StringUtils.startsWith(linkPath, CommonConstants.FILE_TYPE_APPLICATION_JAVASCRIPTS_VALUE)) {
				linkPath = link;
			} else if (StringUtils.startsWith(linkPath, CommonConstants.WWW)) {
				linkPath = CommonConstants.HTTP_SLASH + linkPath;
			}
		} else {
			linkPath = StringUtils.EMPTY;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("CommonUtil :: dotHtmlLinkSKU() with externalizer :: Exit " + linkPath);
		}
		return linkPath;
	}

	/**
	 * Gets the checks if is external.
	 *
	 * @return the checks if is external
	 */
	public static boolean getIsExternal(String linkDestination) {
		boolean isExternal = false;
		if (null != linkDestination) {
			if (StringUtils.startsWith(linkDestination, CommonConstants.HTTP)
					|| StringUtils.startsWith(linkDestination, CommonConstants.HTTPS)) {
				isExternal = true;
			}
		}
		return isExternal;
	}

	/**
	 * This method returns the alternate txt from DAM asset.
	 *
	 * @param resourceResolver
	 *            the resource resolver
	 * @param imagePath
	 *            the image path
	 * @return asset alternate text
	 */
	public static String getAssetAltText(ResourceResolver resourceResolver, String imagePath) {
		LOGGER.debug("CommonUtil :: getAssetAltText() :: Start");
		String assetAltText = StringUtils.EMPTY;
		Resource assetResource = null;
		try {
			if ((resourceResolver != null) && (imagePath != null)) {
				assetResource = resourceResolver.getResource(URLDecoder.decode(imagePath, CommonConstants.UTF_8));
				if (null != assetResource) {
					Resource jcrResource = assetResource.getChild(JcrConstants.JCR_CONTENT);
					if (null != jcrResource) {
						Resource metaDataResource = jcrResource.getChild(DamConstants.METADATA_FOLDER);
						ValueMap properties = null;
						if (metaDataResource != null)
							properties = metaDataResource.getValueMap();
						assetAltText = !StringUtils.equals(StringUtils.EMPTY,
								CommonUtil.getStringProperty(properties, DamConstants.DC_TITLE))
								? CommonUtil.getStringProperty(properties, DamConstants.DC_TITLE)
								: assetResource.getName();
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Exception occured due to improper image name - ", e);
		}
		LOGGER.debug("CommonUtil :: getAssetAltText() :: Exit");
		return assetAltText;
	}

	/**
	 * Gets the asset size.
	 *
	 * @param asset
	 *            the asset
	 * @return the asset size
	 */
	public static String getAssetSize(Asset asset) {
		String assetSize = StringUtils.EMPTY;
		if (null != asset) {
			String spaceUnit = "";
			// get the asset size in Bytes unit
			long sizeOfAsset = (long) (asset.getOriginal().getSize() / Math.pow(10, 6));
			// set the asset size unit
			if (sizeOfAsset < 1) {
				sizeOfAsset = (long) (asset.getOriginal().getSize() / Math.pow(10, 3));
				spaceUnit = CommonConstants.KB;
				if (sizeOfAsset < 1) {
					sizeOfAsset = (long) (asset.getOriginal().getSize());
					spaceUnit = CommonConstants.B;
				}
			} else {
				spaceUnit = CommonConstants.MB;
			}
			assetSize = getType(asset) + CommonConstants.BLANK_SPACE + sizeOfAsset + CommonConstants.BLANK_SPACE
					+ spaceUnit;
		}
		return assetSize;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public static String getType(Asset asset) {
		String assetType = StringUtils.EMPTY;
		if (null != asset) {
			String assetName = asset.getName();
			if (StringUtils.isNotBlank(assetName)) {
				assetType = StringUtils.upperCase(StringUtils.substringAfterLast(assetName, CommonConstants.PERIOD));
			}
		}
		return assetType;
	}

	/**
	 * Gets the asset.
	 *
	 * @param assetResource
	 *            the asset resource
	 * @return the asset
	 */
	public static Asset getAsset(Resource assetResource) {
		Asset asset = null;
		if (null != assetResource && (StringUtils.equals(
				CommonUtil.getStringProperty(assetResource.getValueMap(), CommonConstants.JCR_PRIMARY_TYPE),
				DamConstants.NT_DAM_ASSET))) {
			asset = assetResource.adaptTo(Asset.class);
		}
		return asset;
	}

	/**
	 * Gets the asset title.
	 *
	 * @param assetResource the asset resource
	 * @return the asset title
	 */
	public static String getAssetTitle(String assetPath, Resource assetResource) {
		String assetTitle = StringUtils.EMPTY;
		if(StringUtils.startsWith(assetPath, CommonConstants.CONTENT_DAM) && null != getAsset(assetResource)) {
			// get the dc:title property from metadata of dam asset
			assetTitle = getAssetMetadataValue(DamConstants.DC_TITLE, assetResource);
			// if dc:title is blank then get the asset node name
			if (StringUtils.isBlank(assetTitle) && null != assetResource) {
				assetTitle = assetResource.getName();
			}
		}
		return assetTitle;
	}
	/**
	 * Gets the checks if is investor page.
	 *
	 * @return the checks if is investor page
	 */
	public static boolean getIsInvestorPage(Page resourcePage) {
		if(null != resourcePage) {
			Tag[] tags = resourcePage.getTags();
			if(null != tags) {
				for(Tag tag : tags){
					if(StringUtils.equals("investor-relations", tag.getName())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Gets the asset metadata value.
	 *
	 * @param metadataPropertyName the metadata property name
	 * @return the asset metadata value
	 */
	public static String getAssetMetadataValue(String metadataPropertyName, Resource assetResource) {
		String metadataPropertyValue = StringUtils.EMPTY;
		if(null!=assetResource){
			Asset asset = getAsset(assetResource);
			if(null != asset) {
				metadataPropertyValue = asset.getMetadataValue(metadataPropertyName);
			}
		}
		return metadataPropertyValue;
	}

	/**
	 * This method returns the referer url from request header.
	 *
	 * @param request
	 *            the request
	 * @return the referer URL
	 */
	public static String getRefererURL(final SlingHttpServletRequest request) {
		LOGGER.debug("CommonUtil :: getRefererURL() :: Start");
		String refererURL;
		// get current page url from referer of the request header
		refererURL = request.getHeader(CommonConstants.REFERER_URL);
		LOGGER.debug("CommonUtil :: getRefererURL() :: Exit");
		return refererURL;
	}

	/**
	 * This method gets the referer url from request header and returns the page
	 * path.
	 *
	 * @param resolver
	 *            the resolver
	 * @param refererURL
	 *            the referer url
	 * @return path
	 */
	public static String getContentPath(ResourceResolver resolver, String refererURL) {
		LOGGER.debug("CommonUtil :: getContentPath() :: Start");
		String path = null;
		try {
			URL pageURL = new URL(refererURL);
			path = StringUtils.remove(pageURL.getPath(), "/editor.html");
			path = StringUtils.remove(path, ".html");
			path = StringUtils.remove(path, ".htm");
			Resource pageResource = resolver.resolve(path);
			path = pageResource.getPath();
		} catch (MalformedURLException ex) {
			LOGGER.error("Error in parsing the Referrer URL :: " + refererURL + " :: Exception :: " + ex.getMessage());
		}
		LOGGER.debug("CommonUtil :: getContentPath() :: Exit");
		return path;
	}

	/**
	 * @param xmlSourceString
	 * @param clazz
	 * @return
	 * @throws ClassNotFoundException
	 *
	 *             Generic method to get JAXB unmarshal class for SKU element.
	 *             Validation code is commented yet due data mismatch.
	 */
	public static <T> T getUnmarshaledClass(String xmlSourceString, Class<T> clazz, InputStream xsdInputStream) {
		T generatedPojoClass = null;
		Schema schema = null;
		try {
			StreamSource source = new StreamSource(new StringReader(xmlSourceString));
			JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
			if (null != xsdInputStream) {
				SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				schema = sf.newSchema(new StreamSource(xsdInputStream));
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				jaxbUnmarshaller.setSchema(schema);
				jaxbUnmarshaller.setEventHandler(new AemXmlValidator());
				if (!xmlSourceString.isEmpty()) {
					generatedPojoClass = jaxbUnmarshaller.unmarshal(source, clazz).getValue();
					return generatedPojoClass;
				}
			}
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage());
		} catch (SAXException ex) {
			LOGGER.debug("Exception:------------------ " + ex.getMessage());
		}
		return null;
	}

	/**
	 * Method to write file into DAM
	 * @param resourceResolver
	 * @param jcrSession
	 * @param file
	 * @param is
	 * @param damFolderPath
	 */
	public static void writeFileIntoDAM(ResourceResolver resourceResolver,Session jcrSession,File file,InputStream is,String damFolderPath){
		String contentType = StringUtils.EMPTY;
		String fileName = StringUtils.EMPTY;
		try{
			if(null!=file && null!=is){
				contentType = new MimetypesFileTypeMap().getContentType(file) ;
				fileName = file.getName();

				AssetManager asstMgr = resourceResolver.adaptTo(AssetManager.class);
				if(asstMgr!=null){
					/* Create folder path if not exist */
					Node damXmlFolderNode = JcrUtils.getOrCreateByPath(damFolderPath,JcrResourceConstants.NT_SLING_FOLDER,JcrResourceConstants.NT_SLING_ORDERED_FOLDER, jcrSession, true);
					String newFilePath = damXmlFolderNode.getPath()+StringUtils.join("/")+fileName;
					asstMgr.createAsset(newFilePath, is, contentType, true);
				}else{
					throw new IllegalArgumentException("Can not adapt to Day AssetManager");
				}
			}

		} catch (RepositoryException e) {
			LOGGER.error(e.getMessage());
		}
	}
	/**
	 * @param adminService
	 * @param fileDirPath
	 * @param fileName
	 * @return return file from DAM Method to read file uploaded in AEM DAM
	 */
	public static InputStream getFileFromDAM(AdminService adminService, String fileDirPath, String fileName) {
		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			String filePath= fileDirPath.concat(fileName);
			return InformaticaUtil.getXSDInputStreamfromDAM(adminResourceResolver, filePath);
		} catch (EatonSystemException e) {
			LOGGER.debug("Error while reading file from DAM " + e.getMessage());
		}
        return null;
	}

	/**
	 * @param session
	 * @param assetManager
	 * @param fileUploadPath
	 * @param fileArchivePath
	 * @param fileName
	 * @return
	 *
	 * Common method for existing excel and json file to get archive.
	 */
	public static String archiveDAMAsset(Session session,com.adobe.granite.asset.api.AssetManager assetManager, String fileUploadPath,String fileArchivePath, String fileName) {
		try { Node archiveFilePathNode = JcrUtils.getOrCreateByPath(fileArchivePath,JcrResourceConstants.NT_SLING_FOLDER,JcrResourceConstants.NT_SLING_ORDERED_FOLDER, session, true);
			String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
			assetManager.getAsset(fileUploadPath+fileName);
			String fileExtension = FilenameUtils.getExtension(fileName);
			assetManager.copyAsset(fileUploadPath+fileName, FilenameUtils.removeExtension(archiveFilePathNode.getPath() + StringUtils.join("/")+fileName)+"_"+timeStamp+"."+fileExtension);
			LOGGER.debug("Moved the ASSET! ");
			return String.format("The existing %s file archived to %s", fileExtension, fileArchivePath);
		}
		catch(Exception e)
		{
			LOGGER.debug("ASSET ERROR: "+e.getMessage());
		}
		return null;
	}

	/**
	 *
	 * @param adminService
	 * @param filePath
	 * @return Json String from DAM
	 * @throws IOException
	 */
	public static String getResponseStringFromDAM(AdminService adminService, String filePath) throws IOException {
		String jsonResponse = null;
		LOGGER.debug("File Path :", filePath);
		try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
			jsonResponse = getResponseStringFromFile(adminResourceResolver, filePath);
		} catch (RepositoryException e) {
			LOGGER.error(EndecaConstants.FILE_NOT_FOUND_ERROR_MSG, e.getMessage() + e);
		}
		return jsonResponse;
	}

	/**
	 * To return decoded searchTerm String
	 *
	 * @param searchTerm
	 * @return
	 */
	public static String decodeSearchTermString(String searchTerm) {
		String decodedSearchTerm = searchTerm ;
		//decodedSearchTerm = URLDecoder.decode(searchTerm, "UTF-8");
		if(decodedSearchTerm.indexOf("{}")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\{\\}", "/");
		}
		if(decodedSearchTerm.indexOf("[]")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\[\\]", "=");
		}
		if(decodedSearchTerm.indexOf("::")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\:\\:", ".");
		}
		if(decodedSearchTerm.indexOf("<>")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\<\\>", "\\$");
		}
		if((decodedSearchTerm.indexOf("&") >= 0) && (decodedSearchTerm.indexOf("&amp") == -1)){
			decodedSearchTerm = decodedSearchTerm.replaceAll("&", "&amp;");
		}
		if(decodedSearchTerm.indexOf("%%")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\%\\%", "&amp;");
		}
		if(decodedSearchTerm.indexOf("%20")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("%20", SPACE_STRING);
		}
		return decodedSearchTerm;
	}

	/**
	 * To return decoded CatalogNumber String
	 *
	 * @param catalogNumber
	 * @return
	 */
	public static String decodeCatalogNumber(String catalogNumber) {
		String decodedSearchTerm = catalogNumber ;
		if(decodedSearchTerm.indexOf("{}")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\{\\}", "/");
		}
		if(decodedSearchTerm.indexOf("[]")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\[\\]", "=");
		}
		if(decodedSearchTerm.indexOf("::")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\:\\:", ".");
		}
		if(decodedSearchTerm.indexOf("<>")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\<\\>", "\\$");
		}
		if((decodedSearchTerm.indexOf("&") >= 0) && (decodedSearchTerm.indexOf("&amp") == -1)){
			decodedSearchTerm = decodedSearchTerm.replaceAll("&", "&amp;");
		}
		if(decodedSearchTerm.indexOf("%%")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("\\%\\%", "&amp;");
		}
		if(decodedSearchTerm.indexOf("+")>=0){
			decodedSearchTerm= decodedSearchTerm.replaceAll("\\+", "%2b");
		}
		if(decodedSearchTerm.indexOf("%20")>=0){
			decodedSearchTerm = decodedSearchTerm.replaceAll("%20", SPACE_STRING);
		}
		try {
			decodedSearchTerm = URLDecoder.decode(decodeSearchTermString(decodedSearchTerm),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
		}
		return decodedSearchTerm;
	}

	/**
	 * To return encoded searchTerm String
	 *
	 * @param searchTerm
	 * @return
	 */
	public static String encodeSearchTermString(String searchTerm) {
		String encodedSearchTerm = searchTerm;
		encodedSearchTerm = searchTerm.replaceAll("/", "{}");
		encodedSearchTerm = encodedSearchTerm.replaceAll("=", "[]");
		encodedSearchTerm = encodedSearchTerm.replaceAll("\\.", "::");
		encodedSearchTerm = encodedSearchTerm.replaceAll("\\$", "<>");
		encodedSearchTerm = encodedSearchTerm.replaceAll("&", "&amp;");
		encodedSearchTerm = encodedSearchTerm.replaceAll(StringUtils.SPACE, SPACE_ESCAPE_CHAR1);
		//encodedSearchTerm = URLEncoder.encode(encodedSearchTerm,"UTF-8");
		return encodedSearchTerm;
	}
	/**
	 * To check the null value of a String
	 *
	 * @param str
	 * @return
	 */
	public static boolean isBlankOrNull(String str) {
		return (str == null || "".equals(str.trim()));
	}

	/**
	 * To check the null value of a list
	 *
	 * @param list
	 * @return
	 */
	public static <T> boolean isNullOrEmpty(Collection<T> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * To perform a null check for integer
	 *
	 * @param i
	 * @return
	 */
	public static boolean isNullorZero(Integer i) {
		return 0 == (i == null ? 0 : i);
	}

	/**
	 * @param filePath
	 * @return
	 *
	 * 		common method to read file data
	 */
	public static String readFileContent(String filePath) {
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return content;
	}

	/**
	 * To unescape the XML
	 */
	public static String convertXMLString(String xmlValue) {
		String xmlString = null;
		if (!isBlankOrNull(xmlValue)) {
			xmlString = StringEscapeUtils.unescapeXml(xmlValue);
			xmlString = StringUtils.replace(StringUtils.trim(xmlString), "\\r", " ");
		}
		return xmlString;
	}

	public static ProductFamilyPDHDetails readPDHNodeData(ResourceResolver adminResourceResolver,
														  Resource extnIdResource) {
		ProductFamilyPDHDetails productFamilyPDHDetails = new ProductFamilyPDHDetails();
		String sequencedCoreFeatures = "";
		String supportInfo = "";
		String marDesc = "";
		// get product name from PDH
		Resource prodNameRes = extnIdResource.getChild(CommonConstants.NODE_PRODUCT_NAME);
		if (null != prodNameRes) {
			productFamilyPDHDetails.setProductName(CommonUtil.getStringProperty(prodNameRes.getValueMap(),
					CommonConstants.PROPERTY_PDH_VALUE_CQ_DATA));
		}
		// get inventory item id resource
		Resource inventoryItemIdRes = extnIdResource.getParent();
		// get taxonomy attribute resource
		Resource txnmyAttrRes = inventoryItemIdRes.getChild(CommonConstants.NODE_TXNMY_ATTRS);
		// get core features resource
		Resource coreFeaturesRes = txnmyAttrRes.getChild(CommonConstants.NODE_CORE_FEATURES);
		if (null != coreFeaturesRes) {
			Iterator<Resource> coreFeaturesResList = coreFeaturesRes.listChildren();
			Map<String,String> coreFeatureMap = new TreeMap<>();
			while (coreFeaturesResList.hasNext()) {
				Resource resourceItem = coreFeaturesResList.next();
				if(resourceItem != null){
					String resSequenceNew = CommonUtil.getStringProperty(resourceItem.getValueMap(), CommonConstants.SEQUENCE);
					Resource prodFeatureRes = resourceItem.getChild(CommonConstants.NODE_PROD_FEATURE);
					if(prodFeatureRes != null){
						String coreFeature = CommonUtil.getStringProperty(prodFeatureRes.getValueMap(), CommonConstants.PROPERTY_PDH_VALUE_CQ_DATA);
						coreFeatureMap.put(resSequenceNew, coreFeature);
					}
				}
			}
			for (String sequence : coreFeatureMap.keySet()){
				sequencedCoreFeatures += coreFeatureMap.get(sequence).concat("<br>");
			}
			productFamilyPDHDetails.setCoreFeatures(sequencedCoreFeatures);
		}
		// get support info resource
		Resource supportInfoRes = txnmyAttrRes.getChild(CommonConstants.NODE_TECH_SUPPORT);
		if (null != supportInfoRes) {
			Iterator<Resource> supportInfoResList = supportInfoRes.listChildren();
			while (supportInfoResList.hasNext()) {
				Resource resourceItem = supportInfoResList.next();
				Iterator<Resource> supportInfoItemResList = resourceItem.listChildren();
				while (supportInfoItemResList.hasNext()) {
					Resource supportInfoItemRes = supportInfoItemResList.next();
					if (null != supportInfoItemRes) {
						supportInfo += CommonUtil.getStringProperty(supportInfoItemRes.getValueMap(),
								CommonConstants.PROPERTY_PDH_VALUE_CQ_DATA).concat("|");
					}
				}
				productFamilyPDHDetails.setSupportInfo(supportInfo);
			}
		}
		// get primary image resource
		Resource imgPrimaryRes = adminResourceResolver
				.resolve(inventoryItemIdRes.getPath().concat(CommonConstants.IMG_RENDITION));
		if (null != imgPrimaryRes) {
			productFamilyPDHDetails.setPrimaryImgName(CommonUtil.getStringProperty(imgPrimaryRes.getValueMap(),
					CommonConstants.PROPERTY_PDH_VALUE_CQ_DATA));
		}

		Resource spinImgRes = adminResourceResolver
				.resolve(inventoryItemIdRes.getPath().concat(CommonConstants.SPIN_IMG));
		if (null != spinImgRes) {
			productFamilyPDHDetails.setPdhSpinImage(CommonUtil.getStringProperty(spinImgRes.getValueMap(),
					CommonConstants.PROPERTY_PDH_VALUE_CQ_DATA));
		}

		// populate inventory item id used to create node
		productFamilyPDHDetails.setExtensionId(extnIdResource.getName());
		// get PDH marketing description
		Resource pdhMktDescRes = txnmyAttrRes.getChild(CommonConstants.NODE_PDH_MKTG_DESC);
		if (null != pdhMktDescRes) {
			Iterator<Resource> resourceList = pdhMktDescRes.listChildren();
			while (resourceList.hasNext()) {
				Resource resourceItem = resourceList.next();
				Resource mktDescRes = resourceItem.getChild(CommonConstants.NODE_MKTG_DESC);
				if (null != mktDescRes) {
					marDesc += CommonUtil
							.getStringProperty(mktDescRes.getValueMap(), CommonConstants.PROPERTY_PDH_VALUE_CQ_DATA)
							.concat("<br>");
				}
			}
			productFamilyPDHDetails.setMarketingDescription(marDesc);
		}
		return productFamilyPDHDetails;
	}

	public static String priceItem(String PRICE_LIST_PATH, AdminService adminService, String catalogNumber,String country, String rootElementName, String filename) {
		String Price = StringUtils.EMPTY;
		if(StringUtils.isNotBlank(PRICE_LIST_PATH)) {
			PRICE_LIST_PATH = PRICE_LIST_PATH.concat(filename).concat(".json");
			String reqJsonString = null;
			try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
				reqJsonString = CommonUtil.getResponseStringFromFile(adminResourceResolver, PRICE_LIST_PATH);
				if(StringUtils.isNotBlank(reqJsonString)) {
					JsonObject catalogPriceJsonObject = new JsonParser().parse(reqJsonString).getAsJsonObject();
					JsonObject countryJSONObj = new JsonObject();
					if(catalogPriceJsonObject.has(rootElementName))
						countryJSONObj  = catalogPriceJsonObject.get(rootElementName).getAsJsonObject();
					for (int i=0; i < countryJSONObj.size(); i++) {
						if(countryJSONObj.has(country.toLowerCase())){
							JsonArray catalogNumberArray = countryJSONObj.get(country.toLowerCase()).getAsJsonArray();
							for (int j=0; j < catalogNumberArray.size(); j++) {
								JsonObject catalogNumberObject  = catalogNumberArray.get(j).getAsJsonObject();
								//need the catalog number here
								if(catalogNumberObject.has(catalogNumber)){
									JsonObject catalogObject = catalogNumberObject.get(catalogNumber).getAsJsonObject();
									if(catalogObject!=null){
										Price = catalogObject.get("Currency Symbol").getAsString().concat(catalogObject.get("Price").getAsString());
									}
								}
							}
						}
					}
				}
			} catch (Exception  e) {
				LOGGER.error(e.getMessage());
			}
		}
		return Price;
	}

	public static String decodeURLString(String urlString) {
		String decodedUrlString = urlString;
		try {
			decodedUrlString = URLDecoder.decode(decodeSearchTermString(urlString),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
		}
		return decodedUrlString;
	}

	public static String encodeURLString(String urlString) {
		String skuName = urlString;
		try {
			skuName = URLEncoder.encode(encodeSearchTermString(urlString),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
		}
		return skuName;
	}

	public static List<Tag> getCurrentPageCQtags(Page currentPage, AdminService adminService){
		LOGGER.info("Entering getCurrentPageCQtags method.");
		TagManager tagManager = null;
		if(null != adminService){
			try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
				if (adminResourceResolver != null) {
					tagManager = adminResourceResolver.adaptTo(TagManager.class);
				}
			}
		}
		String[] tagsArray = (String[])currentPage.getProperties().get("cq:tags");
		List<Tag> tagsList = new ArrayList<Tag>();
		if((tagsArray!=null) && (tagsArray.length>0)){
			LOGGER.debug("CQ Tags Length "+tagsArray.length);
			for(String tagItem : tagsArray){
				if (tagManager != null) {
					Tag cqTag = tagManager.resolve(tagItem);
					if(cqTag!=null){
						tagsList.add(cqTag);
					}
				}
			}
		}
		LOGGER.debug("TagList Length "+tagsList.size());
		LOGGER.info("Exiting getCurrentPageCQtags method.");
		return tagsList;
	}

	/**
	 * Determines whether the provided authored country exactly matches the current country.
	 * Values of "All" and null are considered to NOT match any country. Case is ignored
	 * during the comparison. The values should be provided as two character country codes.
	 * @param currentCountry The country to be compared against.
	 * @param authoredCountry The authored country.
	 * @return Whether or not the countries exactly match.
	 */
	public static boolean exactCountryMatch(String currentCountry, String authoredCountry) {
		return authoredCountry.equalsIgnoreCase(currentCountry);
	}

	/**
	 * Determines whether the provided authored country matches the current country.
	 * Values of "All" and null are considered to match any country. Case is ignored
	 * during the comparison. The values should be provided as two character country codes.
	 * @param currentCountry The country to be compared against.
	 * @param authoredCountry The authored country.
	 * @return Whether or not the countries match.
	 */
	public static boolean countryMatches(String currentCountry, String authoredCountry) {
		return authoredCountry == null ||
				authoredCountry.equals("") ||
				authoredCountry.equalsIgnoreCase(currentCountry) ||
				authoredCountry.equalsIgnoreCase("All");
	}

	/**
	 * Determines whether the provided current country matches one of the authored countries.
	 * Values of "All" and null are considered to match any country. Case is ignored
	 * during the comparison. The values should be provided as two character country codes.
	 * @param currentCountry The country to be compared against.
	 * @param authoredCountries The authored country.
	 * @return Whether or not the countries match.
	 */
	public static boolean countryMatches(String currentCountry, String[] authoredCountries) {
		for (String authoredCountry : authoredCountries) {
			if (countryMatches(currentCountry, authoredCountry)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Construct an array  of the authored countries from the properties.
	 * @param properties from The value map.
	 * @return an array of all the authored countries.
	 */

	public static String[] getAuthoredCountries(ValueMap properties){
		String[] authoredCountries;
		if(null != properties.get(CommonConstants.COUNTRIES_FIELD)) {
			if(STR_CONSTANT.equals(properties.get(CommonConstants.COUNTRIES_FIELD).getClass().getName())) {
				String countryName = properties.get(CommonConstants.COUNTRIES_FIELD, StringUtils.EMPTY);
				authoredCountries = new String[] {countryName};
			} else {
				authoredCountries = (String[]) properties.get(CommonConstants.COUNTRIES_FIELD);
			}
		} else {
			authoredCountries = new String[] {StringUtils.EMPTY};
		}
		return authoredCountries;
	}
	/**
	 * Construct an array  of the authored countries from the properties.
	 * @param properties from The value map.
	 * @return an array of all the authored countries.
	 */

	public static String[] getAuthoredCountriesSupportInfo(ValueMap properties){
		String[] authoredCountries=null;
		if(null != properties.get(CommonConstants.SUPPORT_INFO_COUNTRY)) {
			if(STR_CONSTANT.equals(properties.get(CommonConstants.SUPPORT_INFO_COUNTRY).getClass().getName())) {
				String countryName = properties.get(CommonConstants.SUPPORT_INFO_COUNTRY, StringUtils.EMPTY);
				authoredCountries = new String[] {countryName};
			}else{
				authoredCountries = (String[]) properties.get(CommonConstants.SUPPORT_INFO_COUNTRY);
			}
		} else {
			authoredCountries = new String[] {StringUtils.EMPTY};
		}
		return authoredCountries;
	}

	/**
	 * Determines whether the provided current country eactly matches one of the authored countries.
	 * Values of "All" and null are considered to NOT match any country. Case is ignored
	 * during the comparison. The values should be provided as two character country codes.
	 * @param currentCountry The country to be compared against.
	 * @param authoredCountries The authored country.
	 * @return Whether or not the countries exactly match.
	 */
	public static boolean exactCountryMatch(String currentCountry, String[] authoredCountries) {
		for (String authoredCountry : authoredCountries) {
			if (exactCountryMatch(currentCountry, authoredCountry)) {
				return true;
			}
		}
		return false;
	}

	public static List <HowToBuyBean> filterSkuHowToBuyOptions(List<HowToBuyBean> howToBuyList, boolean isSku){
		if (CollectionUtils.isNotEmpty(howToBuyList)) {
			return howToBuyList.stream().filter(howToBuyBean -> howToBuyBean.isSkuOnly() == isSku).collect(Collectors.toList());
		}
		return null;
	}

	public static List <HowToBuyBean> replaceTokenForHowToBuyOptions(final List<HowToBuyBean> howToBuyList, String pageType, String selector) {
		if ( CollectionUtils.isNotEmpty(howToBuyList) ) {
			final List <HowToBuyBean> newHowToBuyBeans = new ArrayList < > ();
			if(StringUtils.equalsIgnoreCase(pageType,CommonConstants.PAGE_TYPE_SKU_PAGE) && StringUtils.isNotEmpty(selector) ){
				howToBuyList.forEach(howToBuyBean -> {
					final String link = howToBuyBean.getLink();
					if(StringUtils.contains(link, CommonConstants.SKUID_URL_TOKEN)){
						howToBuyBean.setLink(link.replace(CommonConstants.SKUID_URL_TOKEN, selector));
					}
					newHowToBuyBeans.add(howToBuyBean);
				});
				return  newHowToBuyBeans;
			}else {
				return  filterSkuHowToBuyOptions(howToBuyList,false);
			}
		}
		return null;
	}

	public static JsonObject getJsonRequestFromPOST(final SlingHttpServletRequest request)
			throws Exception {
		final StringBuilder buffer = new StringBuilder();
		final BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		final String data = buffer.toString();
		final JsonObject dataJson = new JsonParser().parse(data).getAsJsonObject();
		return 	dataJson;
	}

	public static List<Asset> getAssetsFromPath(final ResourceResolver resolver, final List<String> assetPaths) {
		return assetPaths.stream()
		.map(assetPath -> resolver.resolve(assetPath))
		.filter(Objects::nonNull)
		.map(resource -> resource.adaptTo(Asset.class))
		.filter(Objects::nonNull)
		.collect(Collectors.toList());
	}

	public static boolean startsWithAnySiteContentRootPath(final String contentPath) {
		return getSiteRootPath(contentPath).isPresent();
	}

	public static String removeSiteContentRootPathPrefix(final String contentPath) {
		return getSiteRootPath(contentPath).map(root -> { return StringUtils.removeStart(contentPath, root);}).orElse(contentPath);
	}

    public static String getExternalizerDomainNameBySiteRootPath(final String contentPath) {
        String domainName=getMatchedTagPathBySiteRootPathPrefix(contentPath, CommonConstants.siteRootPathConfigForExternalizer);
        return StringUtils.isNotBlank(domainName) ? domainName : CommonConstants.EXTERNALIZER_DOMAIN_EATON;
    }

	public static String getMatchedTagPathBySiteRootPathPrefix(String contentPath,
															   final Map<String, String> tagPathConfigs) {
		String tagPath = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(contentPath)) {
			for (Map.Entry<String, String> marketTagEntry : tagPathConfigs.entrySet()) {
				if (contentPath.startsWith(marketTagEntry.getKey())) {
					tagPath = marketTagEntry.getValue();
					break;
				}
			}
		}
		return tagPath;
	}

	public static String getProductBasePathByProductPath(String productPath) {
		if (StringUtils.isNotBlank(productPath)) {
			for (String productRootPath : CommonConstants.productRootPathConfigList) {
				if (productPath.startsWith(productRootPath)) {
					productPath = productRootPath;
				}
			}
		}
		return productPath;
	}

	public static String getProductBasePathByCurrentPagePath(String pagePath) {
		return getMatchedTagPathBySiteRootPathPrefix(pagePath, CommonConstants.productRootPathConfigMap);
	}

	public static boolean isTagNameMatchesWithAnySiteTagNameSpace(String tagName) {
		if (StringUtils.isNotBlank(tagName)) {
			for (String tagNameSpace : CommonConstants.tagNameSpaceConfig) {
				if (tagNameSpace.equals(tagName)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getSiteRootPrefixByPagePath(String pagePath) {
		return getSiteRootPath(pagePath).orElse(StringUtils.EMPTY);
	}

	public static String[] getExcludedPaths(final ValueMap properties) {
		String[] excludePaths = null;
		if (properties.containsKey(VarnishConstants.EXCLUDE_PATHS)) {
			excludePaths = properties.get(VarnishConstants.EXCLUDE_PATHS, new String[0]);
		}
		return excludePaths;
	}

	public static String getVarnishGroupName(final String[] domain) {
		String groupName = null;
		if (null != domain && domain.length > 1) {
			if(domain[1].contains(VarnishConstants.US_DOMAIN)){
				groupName = domain[1].replace(VarnishConstants.US_DOMAIN,StringUtils.EMPTY).concat(VarnishConstants.GROUP_NAME_US);
			} else if (domain[1].contains(VarnishConstants.SG_DOMAIN)){
				groupName = domain[1].replace(VarnishConstants.SG_DOMAIN,StringUtils.EMPTY).concat(VarnishConstants.GROUP_NAME_SG);
			} else if(domain[1].contains(VarnishConstants.NL_DOMAIN)){
				groupName = domain[1].replace(VarnishConstants.NL_DOMAIN,StringUtils.EMPTY).concat(VarnishConstants.GROUP_NAME_NL);
			} else {
				groupName = domain[1].concat(VarnishConstants.GROUP_NAME_US);
			}
		}
		return groupName;
	}

	public static JsonArray getFilteredPurgePath(final JsonArray varnishFlushPaths,
																			   final String[] excludedPaths, final ReplicationLog log) throws Exception {
		JsonArray purgeObjects = null;
		if (null != varnishFlushPaths) {
			if (varnishFlushPaths.size() > 0){
				purgeObjects = new JsonArray();
				for (int pathIndex = 0; pathIndex < varnishFlushPaths.size(); pathIndex ++) {
					final String path = varnishFlushPaths.get(pathIndex).getAsString();
					boolean isNotExcluded = true;
					if (null != excludedPaths) {
						for(String excludePath : excludedPaths) {
							if (path.contains(excludePath)) {
								isNotExcluded = false;
								log.info(VarnishConstants.END_LINE);
								log.info(path.concat(VarnishConstants.EXCLUDE_PATH_LOG_MESSAGE));
								log.info(VarnishConstants.END_LINE);
								break;
							}
						}
					}
					if (isNotExcluded) {
						purgeObjects.add(path);
					}
				}
				LOGGER.debug("Length of Content Array to clear dispatcher" + purgeObjects.toString());
			}
		}
		return purgeObjects;
	}

	public static final ArrayList clearVarnishCache(final JsonArray purgeObjects,
													final HttpPost request, final HttpClientContext context,
													final ReplicationLog log, final String user,
													final String groupName, final String domain) throws Exception, IOException {
		final JsonObject varnishRequestJson = new JsonObject();
		final HttpClient client = HttpClientBuilder.create().build();
		final DateFormat dateFormat = new SimpleDateFormat(VarnishConstants.DATE_FORMAT);
		final Date currentDate = new Date();
		final ArrayList varnishResponse = new ArrayList();
		HttpResponse response;
		if (purgeObjects != null && purgeObjects.size() > 0) {
			for(int purgeIndex=0; purgeIndex<purgeObjects.size(); purgeIndex++ ) {
				final String purgePath = purgeObjects.get(purgeIndex).toString();
				final StringBuilder expressionContent = new StringBuilder();
				expressionContent.append(VarnishConstants.REQUEST_URLCONSTANT);
				expressionContent.append(purgePath);
				varnishRequestJson.add(VarnishConstants.EXPRESSION, new Gson().toJsonTree(expressionContent.toString()));
				varnishRequestJson.add(VarnishConstants.TARGET, new Gson().toJsonTree(domain));
				varnishRequestJson.add(VarnishConstants.GROUP_NAME, new Gson().toJsonTree(groupName));
				varnishRequestJson.add(VarnishConstants.USERNAME_CONSTANT, new Gson().toJsonTree(user));
				varnishRequestJson.add(VarnishConstants.TIMESTAMP, new Gson().toJsonTree(dateFormat.format(currentDate)));
				varnishRequestJson.add(VarnishConstants.CREATED, new Gson().toJsonTree(dateFormat.format(currentDate)));
				varnishRequestJson.add(VarnishConstants.ID , new Gson().toJsonTree("null"));
				log.info(VarnishConstants.VARNISH_JSON_DESC.concat(varnishRequestJson.toString()));
				final StringEntity entity = new StringEntity(varnishRequestJson.toString(), ContentType.APPLICATION_JSON);
				request.setEntity(entity);
				if (null != client) {
					response = client.execute(request,context);
					if (null != response) {
						log.info(response.toString());
						log.info(VarnishConstants.END_LINE);
						String inputLine ;
						BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						while ((inputLine = br.readLine()) != null) {
							log.info(VarnishConstants.VARNISH_RESPONSE_DESC.concat(inputLine));
						}
						br.close();
						log.info(VarnishConstants.END_LINE);
						final int statusCode = response.getStatusLine().getStatusCode();
						varnishResponse.add(String.valueOf(statusCode));
					}
				}
			}
		} else {
			log.info(VarnishConstants.PURGE_DESCRIPTION);
			LOGGER.debug(VarnishConstants.PURGE_DESCRIPTION);
			varnishResponse.add(VarnishConstants.PURGE_DESCRIPTION);
		}
		return varnishResponse;
	}
	/**
	 * Get the Date in proper format (Ex: Fri Apr 06 2018 17:53:39 GMT+0530) from Gregorian.
	 *
	 * @param object is Gregorian Calendar objects.
	 * @return the date with this (Ex: Fri Apr 06 2018 17:53:39 GMT+0530)format as string.
	 */
	public static String getStringDateFromGregorianForAssets(Object object) {
		String convertedDateFormat = null;
		final String dateFormatInStr = ((GregorianCalendar) object).getTime().toString();
		if (dateFormatInStr.contains(CommonConstants.SPACE_STRING)) {
			final String[] splitDate = dateFormatInStr.split(CommonConstants.SPACE_STRING);
			String displayNameTimeZone = ((GregorianCalendar) object).getTimeZone().getDisplayName();
			if (displayNameTimeZone.contains(CommonConstants.COLON)) {
				displayNameTimeZone = displayNameTimeZone.replace(CommonConstants.COLON, StringUtils.EMPTY);
			}
			if (splitDate.length >= 6) {
				convertedDateFormat = splitDate[0].concat(CommonConstants.SPACE_STRING).concat(splitDate[1]).concat(CommonConstants.SPACE_STRING + splitDate[2]).concat(CommonConstants.SPACE_STRING)
						.concat(splitDate[5]).concat(CommonConstants.SPACE_STRING).concat(splitDate[3]).concat(CommonConstants.SPACE_STRING).concat(displayNameTimeZone);
			}
		}
		return convertedDateFormat;
	}

	/**
	 * Get the Epoc Time in proper format (Ex: 878683434333) from Gregorian.
	 *
	 * @param object is Gregorian Calendar objects.
	 * @return the epoc time with this (Ex: 97878374343433)format as string.
	 */
	public static String getEpocTimeFromGregorianForAssets(Object object) {
		Long date = ((GregorianCalendar) object).getTime().getTime();
		return date.toString();
	}

	/**
	 *
	 * @param currentPagePath is the the request URI.
	 * @return the products root folder path.
	 */
	public static String getProductsFolderPath(final String currentPagePath)
	{
		String productsFolderPath = null;
		if(StringUtils.contains(currentPagePath, CommonConstants.ITEM_PARAM)){
			productsFolderPath = StringUtils.substringAfter(currentPagePath, CommonConstants.ITEM_PARAM_NAME_EQUALS);
		} else {
			productsFolderPath = StringUtils.substringAfter(currentPagePath, CommonConstants.HTML_EXTN);
		}
		return productsFolderPath;
	}

	public static String getApplicationId(String resourcePath) {
    	if (StringUtils.isNotBlank(resourcePath)) {
			Optional<String> rootPath = Stream.of(getSiteRootPath(resourcePath), getDamRootPath(resourcePath)).filter(Optional::isPresent).map(Optional::get).findFirst();
			return rootPath.map(rootPathString -> CommonConstants.rootPathApplicationIdConfig.get(rootPathString))
					.orElseGet(() -> {
		LOGGER.error("INVALID SITE ROOT. APPLICATION ID MISSING");
						return CommonConstants.APPLICATION_ID_MISSING;
					});
		} else {
			return CommonConstants.APPLICATION_ID_MISSING;
		}
	}

	private static Optional<String> getSiteRootPath(String contentPath) {
		return CommonConstants.siteRootPathConfig.stream().filter(contentPath::startsWith).findFirst();
	}

	private static Optional<String> getDamRootPath(String damAssetPath) {
		return CommonConstants.damRootPathConfig.stream().filter(damAssetPath::startsWith).findFirst();
	}

	public static boolean isSkuDiscontinued(String status) {
		return StringUtils.equalsIgnoreCase(CommonConstants.INACTIVE,status) || StringUtils.equalsIgnoreCase(CommonConstants.DISCONTINUED,status) || StringUtils.equalsIgnoreCase(CommonConstants.SKU_STATUS_Retired,status);
	}

    /**
     *
     * @param adminService - admin service
     * @param currentPage - currentPage to get page tags
     * @param baseTagPath - baseTagPath to get sub tags under the base tag
     * @return -  return all the sub tag names uniquely as collection set
     */
    public static Set<String> getPageTagNamesByBaseTagPath(final AdminService adminService, final Page currentPage, final String baseTagPath) {
        if (null != adminService && null != currentPage && StringUtils.isNotBlank(baseTagPath)) {
        	try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
				final String[] pageTags = (String[]) currentPage.getProperties().get(CommonConstants.CQ_TAGS);
				return getTagCollectionSet( adminResourceResolver, pageTags, baseTagPath );
			}
		}
        return null;
    }

	/**
	 *
	 * @param adminService - admin service
	 * @param pimPagePath - pimPagePath of currentPage to get page tags
	 * @param baseTagPath - baseTagPath to get sub tags under the base tag
	 * @return -  return all the sub tag names uniquely as collection set
	 */
	public static Set<String> getPageTagNamesByPimTagPath(final AdminService adminService, final String pimPagePath, final String baseTagPath) {
		if (null != adminService && null != pimPagePath && StringUtils.isNotBlank(baseTagPath)) {
			String[] pimTags = new String[0];
			try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
				Resource pimResource = adminResourceResolver.getResource(pimPagePath);
				if(pimResource != null) {
					ValueMap pimValueMap = pimResource.adaptTo(ValueMap.class);
					if( null != pimValueMap) {
						pimTags = (String[]) pimValueMap.get(CommonConstants.CQ_TAGS);
					}
				}
				return getTagCollectionSet( adminResourceResolver, pimTags, baseTagPath );
			}
		}
		return null;
	}

	/**
	 *
	 * @param adminResourceResolver - adminResourceResolver service
	 * @param tags - resourcePath to get cq tags
	 * @param baseTagPath - baseTagPath to get sub tags under the base tag
	 * @return -  return all the tag names uniquely as collection set
	 */
	public static Set<String> getTagCollectionSet(ResourceResolver adminResourceResolver,final String[] tags , final String baseTagPath) {
		Set<String> tagCollectionSet = new HashSet<>();
		final TagManager tagManager = adminResourceResolver != null ? adminResourceResolver.adaptTo(TagManager.class) : null;
		if (tagManager != null) {
			tagCollectionSet = Arrays.stream(Optional.ofNullable(tags).orElse(new String[0]))
					.map(tagManager::resolve)
					.filter(Objects::nonNull)
					.filter(tag -> !tag.getPath().equals(baseTagPath) && tag.getPath().startsWith(baseTagPath))
					.map(Tag::getName)
					.collect(Collectors.toSet());
		}
		return tagCollectionSet;
	}

	/**
	 * @param currentPageTemplatePath - current page template path
	 * @param microSiteHomePageTemplatePathConfig - microsites home page templates config object
	 * @return - the boolean value based on current page template path matches with any of the microsite template configs.
	 */
	public static boolean isTemplatePathMatchesWithAnyMicroSiteTemplates(final String currentPageTemplatePath, final Map<String, String> microSiteHomePageTemplatePathConfig) {
		boolean isTemplatePathMatches = false;
		if (StringUtils.isNotBlank(currentPageTemplatePath)) {
			isTemplatePathMatches = microSiteHomePageTemplatePathConfig.entrySet().stream().anyMatch(template -> template.getValue().equals(currentPageTemplatePath));
		}
		return isTemplatePathMatches;
	}

	public static String getMappedUrl(String pagePath, Externalizer externalizer, SlingHttpServletRequest slingRequest) {
		final String contentResourcePath = (StringUtils.isNotEmpty(pagePath) && null != externalizer
				.relativeLink(slingRequest, pagePath))
				? externalizer.relativeLink(slingRequest, pagePath) : pagePath;
		return contentResourcePath;
	}

	/**
	 * This method is to get Locale from page path and not from jcr:language
	 * @param page
	 * @return
	 */
	public static Locale getLocaleFromPagePath(final Page page){
		return page.getLanguage(Boolean.TRUE);
	}

	/**
	 * This method is to modify Locale unsupported by java from page path and not from jcr:language
	 * @param page
	 * @return
	 */
	public static String getUpdatedLocaleFromPagePath(final Page page){
		Locale languageLocale=page.getLanguage(Boolean.TRUE);
		String languageLocaleString=languageLocale.toString();
		if((languageLocale.getCountry()).equals(CommonConstants.ISRAEL_LOCALE)) {
			languageLocaleString=languageLocaleString.replace(CommonConstants.ISRAEL_OLD_LOCALE, CommonConstants.ISRAEL_NEW_LOCALE);
		}
		//EAT-8299 - Indonesia Looping Issue - Fix
		if((languageLocale.getCountry()).equals(CommonConstants.INDONESIA_NEW_LOCALE)) {
			languageLocaleString=languageLocaleString.replace(CommonConstants.INDONESIA_OLD_LOCALE, CommonConstants.INDONESIA_NEW_LOCALE);
		}
		return languageLocaleString;
	}

	/**
	 * This method will take country from page path like for fr/en-gb path country is FR
	 * but for language masters GB will be considered as country.
	 * @param page
	 * @return
	 */
	public static String getCountryFromPagePath(final Page page){
		Page secondRootPage = page.getAbsoluteParent(2);

		if (null != secondRootPage) {
			if(null != secondRootPage.getName() &&
				!secondRootPage.getName()
					.equalsIgnoreCase(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)){
				return secondRootPage.getName().toUpperCase();
			}
			if (null != secondRootPage.getName() &&
				secondRootPage.getName()
					.equalsIgnoreCase(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)) {
				return getLocaleFromPagePath(page).getCountry();
			}
		}
		return null;
	}

	/**
	 * This method will return client public ip address from rest api which is open source.
	 * @return IP Adress
	 */
	public static String getClientPublicIpAddress(HttpClientBuilderFactory httpFactory){
		LOGGER.debug("Start with getClientPublicIpAddress method");
		String ipAddress = StringUtils.EMPTY;
		BufferedReader bufferedReader = null;
		try {
			HttpClient client = httpFactory.newBuilder().build();
			HttpGet request = new HttpGet(QRConstants.IP_ADDRESS_URL);
			HttpResponse response = client.execute(request);
			final HttpEntity entity = response.getEntity();
			final int responseCode = response.getStatusLine().getStatusCode();
			String ipAddressJson;
			if (responseCode == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
				while ((ipAddressJson = bufferedReader.readLine()) != null) {
					if(ipAddressJson != null && ipAddressJson.contains(QRConstants.IP_ADDRESS_PARAMETER)){
						JsonParser jsonParser = new JsonParser();
						JsonObject json = (JsonObject)jsonParser.parse(ipAddressJson);
						ipAddress = json.get(QRConstants.IP_ADDRESS_PARAMETER).getAsString();
					}
				}
			}
			LOGGER.debug("ipAddress from getClientPublicIpAddress() ::: "+ipAddress);
		} catch (MalformedURLException e) {
			LOGGER.error("MalformedURLException in getIpAddress method "+e.getMessage());
		} catch (IOException e) {
			LOGGER.error("IOException in getIpAddress method "+e.getMessage());
		}finally {
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					LOGGER.error("While closing bufferedReader "+e.getMessage());
				}
			}
		}
		LOGGER.debug("End with getClientPublicIpAddress method");
		return ipAddress;
	}

	/**
	 * This method will return the Http Post method connection.
	 *  @param strUrl
	 *  @param contentType
	 *  @param accept
	 * @return HttpPost
	 */
	public static HttpPost  getHttpPostMethod(final String strUrl, final String contentType, final String accept){
		LOGGER.debug("Start HttpPost getHttpPostMethod and URL is "+strUrl);
		HttpPost postRequest = null;
		try {
			postRequest = new HttpPost(strUrl);
			postRequest.setHeader(CommonConstants.CONTENT_TYPE,contentType);
			postRequest.setHeader(CommonConstants.ACCEPT,accept);

		} catch (Exception e) {
			LOGGER.error("Exception in getHttpPostMethod method "+e.getMessage());
		}
		LOGGER.debug("End HttpPost getHttpPostMethod. ");
		return postRequest;
	}

	/**
	 * This method will return the Http Get method connection.
	 *  @param strUrl ,contentType , accept
	 * @return HttpRequest
	 */
	public static HttpGet  getHttpGetMethod(final String strUrl){
		LOGGER.debug("Start HttpGet getHttpMethod and URL is "+strUrl);
		HttpGet getRequest = null;
		try {
			getRequest = new HttpGet(strUrl);
			getRequest.setHeader(CommonConstants.CONTENT_TYPE,CommonConstants.APPLICATION_JSON);
			getRequest.setHeader(CommonConstants.ACCEPT,CommonConstants.APPLICATION_JSON);
		} catch (Exception e) {
			LOGGER.error("Exception in getHttpGetMethod method "+e.getMessage());
		}
		LOGGER.debug("End HttpGet getHttpGetMethod. ");
		return getRequest;
	}
	/**
	 * This method will return the runmode instance from SlingSettingService that is author or publish.
	 * @return set of runmodes
	 */
	public static Set<String> getRunModes() {
		Bundle bundle = FrameworkUtil.getBundle(CommonUtil.class);
		if (bundle != null) {
			BundleContext bundleContext = bundle.getBundleContext();
			ServiceReference serviceReference = bundleContext.getServiceReference(SlingSettingsService.class.getName());
			SlingSettingsService slingSettingsService = (SlingSettingsService) bundleContext.getService(serviceReference);
			return slingSettingsService.getRunModes();
		}
		return Set.of();
	}

	/**
	 * @param skuDetailBean
	 * @param adminService
	 * @return parsed sku cid data
	 */
	public static Map<String, List<SkuCidDocBean>> getSkuCidData(SKUDetailsBean skuDetailBean, AdminService adminService) {
		InputStream xsdInputStream = null;
		String xmlString = skuDetailBean.getCid();
		if (null != xmlString && !xmlString.isEmpty()) {
			xmlString = StringEscapeUtils.unescapeXml(xmlString);
			xmlString = StringUtils.replace(StringUtils.trim(xmlString), "\\r", " ");
			xsdInputStream = CommonUtil.getFileFromDAM(adminService, CommonConstants.DAM_XSD_FILEPATH, CommonConstants.CID_XSD_FILENAME);
			if(xsdInputStream != null) {
				ITM itm = CommonUtil.getUnmarshaledClass(xmlString, ITM.class, xsdInputStream);
				if (null != itm) {
					ITMATRBT itmAtr = itm.getITMATRBT();
					List < CONTENT > contentList = itmAtr.getCONTENT();
					return getContentItems(contentList);
				}
			}
		} else {
			LOGGER.debug("Unable to get xml data from Endeca for cid");
		}
		return null;
	}

	/**
	 * @param contentList
	 * @return iterate content element,generate corresponding cid bean list.
	 */
	private static Map<String, List <SkuCidDocBean>> getContentItems(List <CONTENT> contentList) {
		final Map<String, List <SkuCidDocBean>> cidDataMap = new HashMap<> ();
		final Map<Long, CONTENT> collectCidDataMap = contentList.stream().collect(Collectors.toMap(CONTENT::getCID, Function.identity()));
		collectCidDataMap.forEach((key, content) -> {
		List <SkuCidDocBean> cidDocList = new ArrayList <> ();
			content.getDOC().stream().forEach(contentItem -> {
		final SkuCidDocBean docBean = new SkuCidDocBean();
		if (StringUtils.isNotEmpty(contentItem.getId())) {
		  docBean.setDocId(contentItem.getId());
		}
		if (StringUtils.isNotEmpty(contentItem.getPriority().toString())) {
		  docBean.setDocPriority(contentItem.getPriority().toString());
		}
		if (StringUtils.isNotEmpty(contentItem.getSUBTYPE())) {
		  docBean.setDocSubtype(contentItem.getSUBTYPE());
		}
		if (StringUtils.isNotEmpty(contentItem.getValue())) {
		  docBean.setDocCdataValue(contentItem.getValue());
		}
		if (StringUtils.isNotEmpty(contentItem.getCountry())) {
		  docBean.setCountry(contentItem.getCountry().split("'"));
		}
		if (StringUtils.isNotEmpty(contentItem.getLanguage())) {
		  docBean.setLanguage(contentItem.getLanguage());
		}
		if (StringUtils.isNotEmpty(contentItem.getType())) {
		  docBean.setType(contentItem.getType());
		}
		cidDocList.add(docBean);
		});
		cidDataMap.put(key.toString(), cidDocList);
		});
		return cidDataMap;
	}
	/**
	 * @param request
	 * @param externalizer
	 * @return domain name.
	 */
	public static  String getDomainName(SlingHttpServletRequest request, Externalizer externalizer){
		LOGGER.debug("CommonUtil getDomainName() :: Started");
		final ResourceResolver resourceResolver = request.getResourceResolver();
		for (String domain : CommonConstants.vanityDomain) {
			final String loc = Objects.nonNull(externalizer) ? externalizer.externalLink(resourceResolver, domain, request.getPathInfo()): StringUtils.EMPTY;
			if(loc.contains(request.getServerName())){
				final String domainName = (CommonConstants.EXTERNALIZER_DOMAIN_PHOENIXTEC).equals(domain) ?
						CommonConstants.PHOENIX_TEC_POWER_DOMAIN : domain.concat(".com");
				LOGGER.debug("CommonUtil getDomainName():: Exit");
				return domainName;
			}
		}
		LOGGER.debug("CommonUtil getDomainName");
		return CommonConstants.EATON_DOMAIN;
	}

	/**
	 * Get the externalized domain of any page
	 * @param externalizer - externalizer
	 * @param resourceResolver - resource resolver
	 * @param currentPagePath - current page path
	 * @return externalizedDomain - the externalized domain of the page passed as currentPagePath
	 */
	public static String getExternalizedDomain(Externalizer externalizer, ResourceResolver resourceResolver, String currentPagePath) {
		LOGGER.debug("getExternalizedDomain : START");

		// externalize page
		String externalizedDomainName = getExternalizerDomainNameBySiteRootPath(currentPagePath);
		LOGGER.debug("externalized domain name: {}", externalizedDomainName);
		String externalizedUrl = externalizer.externalLink(resourceResolver, externalizedDomainName, currentPagePath);
		LOGGER.debug("externalized url: {}", externalizedUrl);

		// remove protocol from externalized url
		String externalizedUrlWithoutProtocol;
		if (StringUtils.contains(externalizedUrl, CommonConstants.HTTP_SLASH)) {
			externalizedUrlWithoutProtocol = StringUtils.remove(externalizedUrl, CommonConstants.HTTP_SLASH);
		} else if (StringUtils.contains(externalizedUrl, CommonConstants.HTTPS_SLASH)) {
			externalizedUrlWithoutProtocol = StringUtils.remove(externalizedUrl, CommonConstants.HTTPS_SLASH);
		} else {
			externalizedUrlWithoutProtocol = externalizedUrl;
		}
		LOGGER.debug("externalized url without protocol: {}", externalizedUrlWithoutProtocol);

		// remove page path (if exists) from externalized url
		String externalizedUrlWithoutProtocolAndPagePath;
		int slashIndex = externalizedUrlWithoutProtocol.indexOf(CommonConstants.SLASH_STRING);
		if (slashIndex != -1) {
			externalizedUrlWithoutProtocolAndPagePath = externalizedUrlWithoutProtocol.substring(0, slashIndex);
		} else {
			externalizedUrlWithoutProtocolAndPagePath = externalizedUrlWithoutProtocol;
		}
		LOGGER.debug("externalized url without protocol and page path: {}", externalizedUrlWithoutProtocolAndPagePath);

		// remove port number (if exists) from externalized url
		String externalizedDomain;
		int colonIndex = externalizedUrlWithoutProtocolAndPagePath.indexOf(CommonConstants.COLON);
		if (colonIndex != -1) {
			externalizedDomain = externalizedUrlWithoutProtocolAndPagePath.substring(0, colonIndex);
		} else {
			externalizedDomain = externalizedUrlWithoutProtocolAndPagePath;
		}
		LOGGER.debug("externalized url without page path, protocol and port number: {}", externalizedDomain);
		LOGGER.debug("externalized domain: {}", externalizedDomain);

		LOGGER.debug("getExternalizedDomain : END");
		return externalizedDomain;
	}

	/**
	 * @return current timestamp.
	 */
	public static Timestamp getCurrentTime() {
		final Date currentDate = new Date();
		final Timestamp currentTime = new Timestamp(currentDate.getTime());
		return currentTime;
	}

	public static Map<String,String> getRequestHeadersMap(SlingHttpServletRequest request){
		Map<String,String> headerMap = new HashMap<>();
		Enumeration<String> headerNames = request.getHeaderNames();
		if(headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				String headerValue = request.getHeader(headerName);
				headerMap.put(headerName, headerValue);
			}
		}
		return headerMap;
	}

	public static List <SecondaryLinksBean> replaceTokenForSecondaryLinkOptions(final List<SecondaryLinksBean> secondaryLinkList, String pageType, String selector) {
		if ( CollectionUtils.isNotEmpty(secondaryLinkList) ) {
			final List <SecondaryLinksBean> newSecondaryLinkList = new ArrayList < > ();
			if(StringUtils.equalsIgnoreCase(pageType,CommonConstants.PAGE_TYPE_SKU_PAGE) || StringUtils.equalsIgnoreCase(pageType,CommonConstants.PAGE_TYPE_GLOBAL_PRODUCT_SKU_PAGE) && StringUtils.isNotEmpty(selector) ){
				secondaryLinkList.forEach(secondaryLinksBean -> {
					final String link = secondaryLinksBean.getPath();
					if(StringUtils.contains(link, CommonConstants.SKUID_URL_TOKEN)){
						secondaryLinksBean.setPath(link.replace(CommonConstants.SKUID_URL_TOKEN, selector));
					}
					newSecondaryLinkList.add(secondaryLinksBean);
				});
				return  newSecondaryLinkList;
			}else {
				return  filterSkuSecondaryLinkOptions(secondaryLinkList,false);
			}
		}
		return null;
	}

	public static List <SecondaryLinksBean> filterSkuSecondaryLinkOptions(List<SecondaryLinksBean> secondaryLinkList, boolean isSku){
		if (CollectionUtils.isNotEmpty(secondaryLinkList)) {
			return secondaryLinkList.stream().filter(secondaryLinksBean -> secondaryLinksBean.isSecLinkSkuOnly() == isSku).collect(Collectors.toList());
		}
		return null;
	}

	/**
	 * @param link
	 * @return boolean to validate the external URL
	 */
	public static boolean isExternalURL(final String link) {
		boolean isExternal = StringUtils.startsWith(link, CommonConstants.HTTP)
				|| StringUtils.startsWith(link, CommonConstants.HTTPS)
				|| StringUtils.startsWith(link, CommonConstants.WWW);
		LOGGER.debug("primaryCTAURL- {}  isExternalURL :: ", link);
		return isExternal;
	}

	/**
	 * @param resourceResolver from adminService
	 * @param domain from current location
	 * @return country-language code list
	 */
	public static List<String> getLangCodeListFromRepo(ResourceResolver resourceResolver, String domain){
		List<String> langCodeList = new ArrayList<>();
		String path = "/content/".concat(domain);
		Resource mainPageResource = resourceResolver.getResource(path);
		if(mainPageResource != null && mainPageResource.hasChildren()) {
			Iterable<Resource> countryItr = mainPageResource.getChildren();
			countryItr.forEach(country -> {
				if (!country.getName().equals(CommonConstants.LANGUAGE_MASTERS_NODE_NAME) && !country.getName().equals(CommonConstants.JCR_CONTENT_STR)) {
					String listItem = country.getName().concat(CommonConstants.SLASH_STRING);
					Iterable<Resource> languageItr = country.getChildren();
					languageItr.forEach(language -> {
					  if (!language.getName().equals(CommonConstants.JCR_CONTENT_STR)) {
						String listItemStr= listItem.concat(language.getName());
						langCodeList.add(listItemStr);
					  }
					});
				}
			});
		}
		langCodeList.add("x-default/x-default");
		final List<String> finalLangCodeList = langCodeList.stream()
				.distinct()
				.collect(Collectors.toList());
		return finalLangCodeList;
	}

	/**
	 * @param countryLanguageCodeArrayElement
	 * @return country-language code bean
	 */
	public static CountryLanguageCodeBean getCountryLangCodesMap(String countryLanguageCodeArrayElement, String xDefaultLangCode) {
		CountryLanguageCodeBean countryLanguageCodeBean = new CountryLanguageCodeBean();
		if(countryLanguageCodeArrayElement!=null){
			String[] codes = countryLanguageCodeArrayElement.split(CommonConstants.SLASH_STRING);
			String countryCode = null, languageCode = null;
			if((codes.length>0) ){
				countryCode = codes[0];
				languageCode = codes[1];
			}
			if(countryCode!=null) {
				if(countryCode.equals(CommonConstants.X_DEFAULT)){
					countryLanguageCodeBean.setCountryCode(xDefaultLangCode.split(CommonConstants.SLASH_STRING)[0]);
					countryLanguageCodeBean.setGoogleCode(CommonConstants.X_DEFAULT);
				}else {
					countryLanguageCodeBean.setCountryCode(countryCode);
				}
			}
			if(languageCode!=null) {
				if(languageCode.equals(CommonConstants.X_DEFAULT)){
					countryLanguageCodeBean.setLanguageCode(xDefaultLangCode.split(CommonConstants.SLASH_STRING)[1]);
				} else {
					countryLanguageCodeBean.setLanguageCode(languageCode);
				}
			}
		}
		return countryLanguageCodeBean;
	}
	public static String updateCTAURLString(String currentLink, Page currentPage,
			ResourceResolver adminServiceReadService) {

		if (currentLink!=null && CommonUtil.startsWithAnySiteContentRootPath(currentLink))
		{
		    String link=currentLink;
        	String pageCountry = CommonUtil.getCountryFromPagePath(currentPage);
        	if(pageCountry != null) {
        		pageCountry = pageCountry.toLowerCase();
        	}
            Locale languageLocale=currentPage.getLanguage(Boolean.TRUE);
            String languageString=languageLocale.toString().toLowerCase().replace("_", "-");
            String linkLanguageLM=null;
            if(currentPage != null){
		        if (link.contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME) &&
				currentPage.getPath().contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)) {
		        	  linkLanguageLM=link.substring(32,37);
			          link = link.replaceFirst(linkLanguageLM, languageString);
				  } else if (link.contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME) &&
				   (!(currentPage.getPath().contains(CommonConstants.LANGUAGE_MASTERS_NODE_NAME)))){
		              linkLanguageLM=link.substring(32,37);
		    	      link = link.replaceFirst(CommonConstants.LANGUAGE_MASTERS_NODE_NAME, pageCountry);
				      link = link.replaceFirst(linkLanguageLM, languageString);
				 } else {
					 String linkCountry= link.substring(15, 17);
					 String linkLanguage= link.substring(18, 23);
					 link = link.replaceFirst(linkLanguage, languageString);
                     link = link.replaceFirst(linkCountry, pageCountry);
				}
		        Resource linkResource = null;
		        String withouthtmllink=link.replace(".html", "");
		        linkResource = adminServiceReadService.getResource(withouthtmllink.trim());
		        if(linkResource!=null){
		        	currentLink=withouthtmllink;
		        }
		    }
	   }
	   return currentLink;
	}
	/**
	 * Method to get resource object from the link path
	 *
	 * @param path - path
	 * @param resourceResolver - resourceResolver
	 * @return - resource - if link path starts with site root-path or dam
	 */
	public static Resource getResourceFromLinkPath(String path, ResourceResolver resourceResolver) {
		if (null != path){
			if (CommonUtil.startsWithAnySiteContentRootPath(path) || StringUtils.startsWith(path, CommonConstants.CONTENT_DAM)) {
				path = path.contains(CommonConstants.HTML_EXTN) ? FilenameUtils.removeExtension(path): path;
				final Resource resource = resourceResolver.resolve(path);
				if (null != resource) {
					return resource;
				}
			}
		}
		return  null;
	}
	/**
	 * Method to construct locale specific page based on the redirectLocale,siteRootPath and defaultURI passed to the method
	 * Returns defaultURI passed to the method if locale specific resource doesn't exist
	 * Returns defaultURI passed to the method if redirectLocale passed to the method is empty or null
	 * @param redirectLocale - can be fetched from eaton-redirect-url cookie ex(us/en-us)
	 * @param defaultURI - default page path with domain ex: https://login-dev.tcc.etn.com/us/en-us/login.html
	 * @param resourceResolver - resourceResolver
	 * @param siteRootPath - To check if resource exist ex:/content/eaton
	 * @return locale specific page url
	 */
	public static  String constructLocaleSpecificUrl(String redirectLocale,  String defaultURI, ResourceResolver resourceResolver, String siteRootPath){
		String localeSpecificURL = defaultURI;
		final String pattern = CommonConstants.SLASH_STRING.concat(CommonConstants.US_EN_US);
		if (StringUtils.isNotBlank(redirectLocale) && StringUtils.isNotBlank(localeSpecificURL) && null != resourceResolver ){
			final String[] urlParts = localeSpecificURL.split(pattern);
			String pageName = urlParts.length >= TWO ? urlParts[1]: StringUtils.EMPTY;
			Resource redirectPathResource = resourceResolver.getResource(siteRootPath
					.concat(redirectLocale).concat(pageName.replace(CommonConstants.HTML_EXTN, "")));
			if ( null != redirectPathResource ){
				localeSpecificURL =  localeSpecificURL.replaceFirst(pattern, CommonConstants.SLASH_STRING.concat(redirectLocale));
			}
		}
		return localeSpecificURL;
	}

	/**
	 * Method to construct locale specific page based on the redirectCookie,siteRootPath and defaultURI passed to the method
	 * Returns defaultURI passed to the method if redirect cookie doesn't exist
	 * Returns defaultURI passed to the method if redirectLocale passed to the method is empty or null
	 * @param redirectCookie - redirect from the browser cookie
	 * @param defaultURI - default page path with domain ex: https://login-dev.tcc.etn.com/us/en-us/secure/dashboard.html
	 * @param resourceResolver - resourceResolver
	 * @return locale specific page url
	 */
	public static  String constructLocaleSpecificUrlByRedirectCookie(String redirectCookie,  String defaultURI, ResourceResolver resourceResolver){
		String localeSpecificURL = defaultURI;
		if (StringUtils.isNotBlank(redirectCookie) && StringUtils.isNotBlank(localeSpecificURL) && null != resourceResolver ){
			try {
				LOGGER.debug("****** constructLocaleSpecificUrlByRedirectCookie() :  Redirect Cookie ********* {}",redirectCookie);
				String decodedPathSegment = URLDecoder.decode(redirectCookie, "UTF-8");
				Resource redirectPathURL = resourceResolver.getResource(CommonConstants.CONTENT_ROOT_FOLDER_NO_EXTRA_SLASH + decodedPathSegment + CommonConstants.SECURE_DASHBOARD);
				if (redirectPathURL != null) {
					LOGGER.debug("****** constructLocaleSpecificUrlByRedirectCookie() :  Decoded Path ********* {}",decodedPathSegment);
					LOGGER.debug("****** constructLocaleSpecificUrlByRedirectCookie() :  Redirected Path URL ********* {}",redirectPathURL);
					return decodedPathSegment.concat(CommonConstants.SECURE_DASHBOARD).concat(CommonConstants.HTML_EXTN);
				}
				LOGGER.debug("****** constructLocaleSpecificUrlByRedirectCookie() :  Processed Redirect URL ********* {}",localeSpecificURL);
			} catch (Exception e) {
				LOGGER.error(" *******  URL Exception *********",e);
			}
		}
		return localeSpecificURL;
	}

	/**
	 * This method responsible to search using 'QueryBuilder' by passing Dam Path(/content/dam/eaton) as a root folder
	 *  and looks for assets of primary type is 'DAM:Asset' and also checks if the found asset mime type is 'application/pdf' only.
	 *  Returns found assets as List<Hit>
	 * @param resourceResolver Resource Resolver
	 * @param damRootPath damRootPath
	 * @param range  -1d, -1m, 20h
	 * @param localeTag locale tag from current page
	 * @param queryBuilder query builder object
	 * @return List of assets found
	 */
	public static List<Hit> findAssetsByLocale(ResourceResolver resourceResolver, String damRootPath, String localeTag, String range, QueryBuilder queryBuilder){
		Map<String, String> map = new HashMap<>();
		map.put(CommonConstants.QUERY_PATH, damRootPath);
		map.put(CommonConstants.QUERY_TYPE, DamConstants.NT_DAM_ASSET);
		map.put(CommonConstants.QUERY_LIMIT, "-1");
		// Check if 'range' parameter exist. Filter results based on the range Ex :  Last 1hour,1day,1Month
		if (StringUtils.isNotBlank(range)) {
			map.put(CommonConstants.QUERY_PROPERTY_3_KEY, CommonConstants.QUERY_PROPERTY_JCR_LASTMODIFED);
			map.put(CommonConstants.QUERY_DATE_RAGE_UPPERBOUND, "0");
			map.put(CommonConstants.QUERY_DATE_RAGE_LOWERBOUND, range);
		}
		Query query = queryBuilder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
		SearchResult result = query.getResult();
		LOGGER.debug("SiteMap JCR Xpath Query ::::: {}",result.getQueryStatement());
		return result.getHits();
	}

	/**
	 *  This method is used to find the tag matched by using country id extracted from the current page locale.
	 * @param resourceResolver ResourceResolver Object
	 * @param countryId country id extracted from current page
	 * @return matched TagId
	 */
	public static String findTagByName(ResourceResolver resourceResolver,String countryId){
		final TagManager tagMgr = resourceResolver.adaptTo(TagManager.class);
		if(tagMgr != null && tagMgr.resolve(CommonConstants.COUNTRY_TAG_PATH) != null) {
			Iterator<Tag> countryParentNameSpace = tagMgr.resolve(CommonConstants.COUNTRY_TAG_PATH).listChildren();
			while (countryParentNameSpace.hasNext()) {
				Tag regionTag = countryParentNameSpace.next();
				Iterator<Tag> countryTags = regionTag.listChildren();
				while (countryTags.hasNext()) {
					Tag countryTag = countryTags.next();
					// Check if the current tag name is matched with current page country id
					if (countryTag.getName().equalsIgnoreCase(countryId)) {
						return countryTag.getTagID();
					}
				}
			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Method to check the current page is with in lifespan period.
	 * @param resource Current page Resource
	 * @param lifeSpanPeriod LifeSpan Period.
	 * @return boolean flag to hide and show the page.
	 */
	public static boolean checkProductLifeSpanIsWithinThePeriod(Resource resource, int lifeSpanPeriod){
		// If lifeSpan period is '0' return true - Allow to Display
		if(lifeSpanPeriod == 0) {
			return true;
		}
		try {
			ValueMap pageProperties = resource.getValueMap();
			Calendar publicationDate = pageProperties.get(CommonConstants.PUBLICATION_DATE, Calendar.class);
			if (publicationDate != null) {
				publicationDate.add(Calendar.DATE, lifeSpanPeriod);
				if (publicationDate.after(Calendar.getInstance())) {
					return true;
				}
			}
		}catch (IllegalArgumentException iae){
			LOGGER.error(" *** Exception while reading Date property for JCR Node **** {}", iae.getMessage());
		}
		return false;
	}
	public static long getDaysDifference(String publicationDate) {
		SimpleDateFormat publicationDateFormat  = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT_PUBLISH);
		Date parsedPublicationDate;
		long differenceInDays = -99;
		try {
			parsedPublicationDate = publicationDateFormat.parse(publicationDate);
			Date currentDate = Calendar.getInstance().getTime();
			 String strDate = publicationDateFormat.format(currentDate);
	       Date parsedCurrentDate = publicationDateFormat.parse(strDate);
				long differenceInTime = parsedCurrentDate.getTime() - parsedPublicationDate.getTime();
				differenceInDays  = differenceInTime/ (1000 * 60 * 60 * 24);
		} catch (ParseException e) {
			LOGGER.error("getDaysDifference: Error in parsing the Date :: :: Exception :: {}" ,e.getMessage());
		}
		return differenceInDays;
	}
	public static List<Resource> getChildrenResourcesByType(Resource resource, String resourceType) {
		List<Resource> result = new ArrayList<>();
		if(!resource.hasChildren()){
			return result;
		}
		Iterator<Resource> iterator = resource.listChildren();
		while(iterator.hasNext()) {
			Resource child = iterator.next();
			if(child.isResourceType(resourceType)) {
				result.add(child);
			} else {
				result.addAll(getChildrenResourcesByType(child,resourceType));
			}
		}
		return result;
	}
	public static ArrayList clearVarnishSixCache(final JsonArray purgeObjects,
													final HttpPost request, final HttpClientContext context,
													final ReplicationLog log, final int vclGroupId,
													 final String varnishDomain, String accessToken) throws IOException {
		LOGGER.debug("CommonUtil :: clearVarnishSixCache() :: Start");

		final JsonObject varnishRequestJson = new JsonObject();
		final HttpClient client = HttpClientBuilder.create().build();
		final JsonObject vclGroup = new JsonObject();
		JsonArray jsonPathArray = new JsonArray();
		JsonArray jsonDomainArray;

		final ArrayList varnishResponse = new ArrayList();
		HttpResponse response;

		if (purgeObjects != null && purgeObjects.size() > 0) {
			for(int purgeIndex=0; purgeIndex<purgeObjects.size(); purgeIndex++) {
				final String purgePath = purgeObjects.get(purgeIndex).toString();

				request.setHeader(CommonConstants.AUTHORIZATION, StringUtils.join(VarnishConstants.BEARER_SPACE, accessToken));
				varnishRequestJson.add(VarnishConstants.METHOD, new Gson().toJsonTree(VarnishConstants.BAN));
				jsonPathArray.add(purgePath);
				varnishRequestJson.add(VarnishConstants.PATHS, jsonPathArray);
				vclGroup.add(VarnishConstants.ID, new Gson().toJsonTree(vclGroupId));
				varnishRequestJson.add(VarnishConstants.VCLGROUP, new Gson().toJsonTree(vclGroup));
				jsonDomainArray = getDomainJsonArray(varnishDomain);
				varnishRequestJson.add(VarnishConstants.DOMAINS,jsonDomainArray);

				log.info(VarnishConstants.VARNISH_JSON_DESC.concat(varnishRequestJson.toString()));

				final StringEntity entity = new StringEntity(varnishRequestJson.toString(), ContentType.APPLICATION_JSON);
				request.setEntity(entity);
				if (null != client) {
					response = client.execute(request,context);

					if (null != response) {
						log.info(response.toString());
						log.info(VarnishConstants.END_LINE);
						String inputLine ;
						BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						while ((inputLine = br.readLine()) != null) {
							log.info(VarnishConstants.VARNISH_RESPONSE_DESC.concat(inputLine));
						}
						br.close();
						log.info(VarnishConstants.END_LINE);
						final int statusCode = response.getStatusLine().getStatusCode();
						varnishResponse.add(String.valueOf(statusCode));
					}
				}
			}
		} else {
			log.info(VarnishConstants.PURGE_DESCRIPTION);
			LOGGER.debug(VarnishConstants.PURGE_DESCRIPTION);
			varnishResponse.add(VarnishConstants.PURGE_DESCRIPTION);
		}
		LOGGER.debug("CommonUtil :: clearVarnishSixCache() :: End");
		return varnishResponse;
	}
	public static JsonArray getDomainJsonArray(String varnishDomain) {
		JsonArray domainArray = new JsonArray();
		String[] domains = varnishDomain.split(VarnishConstants.COMMA);
		for (int domain = 0;  domain < domains.length; domain++) {
			JsonObject domainObject	= new JsonObject();
			domainObject.add(VarnishConstants.FQDN, new Gson().toJsonTree(domains[domain]));
			domainArray.add(domainObject);
		}
		return domainArray;
	}

	/**
	 * This method checks passed tagid is valid (Exists in AEM) then return true, else false.
	 */
	public static boolean isValidAEMTag(TagManager tagManager, String tagId){
		if(null != tagManager && null != tagId ){
			final Tag tagObj = tagManager.resolve(tagId);
			if (tagObj != null) {
				return true;
			}
		}
		return false;
	}

	public static String getLastLevel(String tagId){
		if(tagId.contains(CommonConstants.UNDERSCORE)){
			int n = tagId.lastIndexOf(CommonConstants.UNDERSCORE);
			return tagId.substring(n+1);
		}else{
			return tagId;
		}
	}

	public static String getFirstLevel(String tagId){
		if(tagId.contains(CommonConstants.UNDERSCORE)){
			int n = tagId.indexOf(CommonConstants.UNDERSCORE);
			return tagId.substring(0,n);
		}else{
			return tagId;
		}
	}

	/**
	 * Adapts the adaptable to the given type.
	 *
	 * @param <T>
	 *            the generic type
	 * @param adaptable
	 *            the adaptable
	 * @param type
	 *            the type
	 * @return the the adapted object
	 * @throws EatonSystemException
	 *             the Eaton system exception if Adaptable.adaptTo(type) returned null
	 */
	public static <T> T adapt(final Adaptable adaptable, final Class<T> type) throws EatonSystemException {
		if (adaptable == null) {
			throw new IllegalArgumentException("Adaptable is not defined");
		}
		if (type == null) {
			throw new IllegalArgumentException("Type is not defined");
		}
		final T adapter = adaptable.adaptTo(type);

		if (adapter == null) {
			throw new EatonSystemException(StringUtils.EMPTY, String.format("Can not adapt %s to %s.", adaptable.getClass().getName(), type.getName()));
		}
		return adapter;
	}
}
