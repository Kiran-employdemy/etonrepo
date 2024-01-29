package com.eaton.platform.core.models;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RawHTMLModel {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(RawHTMLModel.class);
    
    /** The html body. */
    private String htmlBody;
    
    /** The component CSS source list. */
    private List<String> componentCSSSourceList;
    
    /** The component JS source list. */
    private List<String> componentJSSourceList;

    /** The Constant PROPERTY_HTML_SOURCE. */
    private static final String PROPERTY_HTML_SOURCE = "htmlsource";
    
    /** The Constant HTML_ENCODING. */
    private static final String HTML_ENCODING = "html";
    
    /** The Constant URL_ENCODING. */
    private static final String URL_ENCODING = "url";
    
    /** The Constant JAVASCRIPT_ENCODING. */
    private static final String JAVASCRIPT_ENCODING = "script";
    
    /** The Constant PROPERTY_HTML_FRAGMENT_BODY. */
    private static final String PROPERTY_HTML_FRAGMENT_BODY = "body";
    
    /** The Constant MULTIFIELD_PROPERTY_NAME. */
    private static final String MULTIFIELD_PROPERTY_NAME = "propKey";
    
    /** The Constant MULTIFIELD_PROPERTY_VALUE. */
    private static final String MULTIFIELD_PROPERTY_VALUE = "propValue";
    
    /** The Constant MULTIFIELD_PROPERTY_ENCODING. */
    private static final String MULTIFIELD_PROPERTY_ENCODING = "encodingType";
    
    /** The Constant CHARSET_UTF. */
    private static final String CHARSET_UTF = "UTF-8";
    
    /** The Constant MULTIFIELD_PROP_LIST_NODE_NAME. */
    private static final String MULTIFIELD_PROP_LIST_NODE_NAME = "dynamichtmlproperties";
    
    @Inject
	private AdminService adminService;
    
    /** The toggle inner grid. */
    @Inject
    @Via("resource")
    private String toggleInnerGrid;

    @Inject
	private SlingHttpServletRequest slingRequest;
    
    @Self
	@Via("resource")
	private Resource resource;
	
    @PostConstruct
	protected void init() {
		LOGGER.debug("Raw HTML Model Init Started");
		if(adminService != null){
			try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
                // get the HTML Source path
                String htmlSource = CommonUtil.getStringProperty(resource.getValueMap(), PROPERTY_HTML_SOURCE);
                Resource htmlResource = null;
                if (null != htmlSource) {
                    htmlResource = adminResourceResolver.getResource(htmlSource);
                }
                // Page object to access properties of the HTML source
                Page rawHTMLPage = null;
                if (htmlResource != null) {
                    rawHTMLPage = htmlResource.adaptTo(Page.class);
                    if (rawHTMLPage != null) {
                        /*
                         * method to check if the CSS sources are already loaded for the page/request. If not already loaded,
                         * then load them as part of the component else not required.
                         */
                        populateStyleList(rawHTMLPage, slingRequest);
                        /*
                         * method to check if the JS scripts are already loaded for the page/request. If not already loaded,
                         * then load them as part of the component else not required.
                         */
                        populateScriptList(rawHTMLPage, slingRequest);
                    }
                }
                // populate html body
                if (htmlResource != null) {
                    htmlBody = processHTMLContent(htmlResource, resource);
                }
            }
		}
     	LOGGER.debug("Raw HTML Model Init Ended");
    }
    
	/**
     * This method checks if the styles of the component are already loaded on the page if multiple HTML Fragment/Promo
     * components are used on same page. If already loaded, there is no need to load them again. This is achieved by
     * maintaining a list of already loaded styles, set as an attribute(pageCSSSourceList) in SlingHttpServletRequest
     * @param htmlTemplatePage the html template page
     * @param request the request
     */
    @SuppressWarnings("unchecked")
    private void populateStyleList(Page htmlTemplatePage, SlingHttpServletRequest request) {
        LOGGER.debug("Entry into RawHTMLModel populateStyleList method ");
        // constants
        final String PROPERTY_FRAGMENT_CSS_LIST = "css";
        final String REQUEST_CSS_LIST = "pageCSSSourceList";
        // local variables
        String[] styles = new String[] {};
        // list variable to hold unique styles for a page
        List<String> pageCSSSourceList = new ArrayList<>();
        // list variable to hold styles for a component
        List<String> compCSSSourceList = new ArrayList<>();
        // get the styles from the multi-field in HTML Fragment
        if (htmlTemplatePage.getProperties().containsKey(PROPERTY_FRAGMENT_CSS_LIST)) {
            styles = htmlTemplatePage.getProperties().get(PROPERTY_FRAGMENT_CSS_LIST, String[].class);
        }
        // check if the attribute pageCSSSourceList exists in the request. If available, get the attribute value.
        if (null != request.getAttribute(REQUEST_CSS_LIST)) {
            pageCSSSourceList = (ArrayList<String>) request.getAttribute(REQUEST_CSS_LIST);
        }
        /*
         * iterate through the author configured styles to check if the style is already available in the request. If
         * not available, add it to page level styles list and component level styles list
         */
        if (null != styles && styles.length > 0) {
            for (String cssSource : styles) {
                if (!pageCSSSourceList.contains(cssSource)) {
                    compCSSSourceList.add(cssSource);
                    pageCSSSourceList.add(cssSource);
                }
            }
        }
        // populate instance variable for component specific styles list
        if (!compCSSSourceList.isEmpty()) {
            componentCSSSourceList = compCSSSourceList;
        }
        // set the page level styles in the request
        request.setAttribute(REQUEST_CSS_LIST, pageCSSSourceList);
        LOGGER.debug("Exit from RawHTMLModel populateStyleList method ");
    }

    /**
     * This method checks if the scripts of the component are already loaded on the page if multiple HTML Fragment/Promo
     * components are used on same page. If already loaded, there is no need to load them again. This is achieved by
     * maintaining a list of already loaded scripts, set as an attribute(attributes pageJSSourceList) in
     * SlingHttpServletRequest
     * @param htmlTemplatePage the html template page
     * @param request the request
     */
    @SuppressWarnings("unchecked")
    private void populateScriptList(Page htmlTemplatePage, SlingHttpServletRequest request) {
        LOGGER.debug("Entry into RawHTMLModel populateScriptList method ");
        // constants
        final String PROPERTY_FRAGMENT_JS_LIST = "scripts";
        final String REQUEST_JS_LIST = "pageJSSourceList";
        // local variables
        String[] scripts = new String[] {};
        // list variable to hold uniue scripts for a page
        List<String> pageJSSourceList = new ArrayList<>();
        // list variable to hold scripts for a component
        List<String> compJSSourceList = new ArrayList<>();
        // get the styles and scripts from the multi-field in HTML Fragment
        if (htmlTemplatePage.getProperties().containsKey(PROPERTY_FRAGMENT_JS_LIST)) {
            scripts = htmlTemplatePage.getProperties().get(PROPERTY_FRAGMENT_JS_LIST, String[].class);
        }
        // check if the attribute pageJSSourceList exists in the request. If available, get the attribute value.
        if (null != request.getAttribute(REQUEST_JS_LIST)) {
            pageJSSourceList = (ArrayList<String>) request.getAttribute(REQUEST_JS_LIST);
        }
        /*
         * iterate through the author configured scripts to check if the script is already available in the request. If
         * not available, add it to page level scripts list and component level scripts list
         */
        if (null != scripts && scripts.length > 0) {
            for (String jsSource : scripts) {
                if (!pageJSSourceList.contains(jsSource)) {
                    compJSSourceList.add(jsSource);
                    pageJSSourceList.add(jsSource);
                }
            }
        }
        // populate instance variable for component specific scripts list
        if (!compJSSourceList.isEmpty()) {
            componentJSSourceList = compJSSourceList;
        }
        // set the page level scripts in the request
        request.setAttribute(REQUEST_JS_LIST, pageJSSourceList);
        LOGGER.debug("Exit from RawHTMLModel populateScriptList method ");
    }

    /**
     * This method get the HTML Fragment content and replace the variables with their corresponding author configured
     * values.
     * @param htmlTemplatePage the html template page
     * @param rawHTMLSource the res
     * @return the string
     */
    private String processHTMLContent(Resource rawHTMLSource, Resource currentRes) {
        LOGGER.debug("Entry into RawHTMLModel processHTMLContent method ");
        // local variables
        String body = null;
        String propKey ;
        String propValue ;
        String propEncoding ;
        String tokenSearch ;
        Resource dynamicHTMLPropertiesRes =  null;
        
        if(null != rawHTMLSource && rawHTMLSource.hasChildren()){
        	// get the HTML Fragment content
            body = CommonUtil.getStringProperty(rawHTMLSource.getChild(CommonConstants.JCR_CONTENT_STR).getValueMap(), 
            		PROPERTY_HTML_FRAGMENT_BODY);
            // get the properties multi-field value from dialog
        	dynamicHTMLPropertiesRes = currentRes.getChild(MULTIFIELD_PROP_LIST_NODE_NAME);
        }
        
        try {
	        if(null != dynamicHTMLPropertiesRes && dynamicHTMLPropertiesRes.hasChildren()){
	        	Iterator<Resource> propListIt = dynamicHTMLPropertiesRes.listChildren();
	        	
	        	while (propListIt.hasNext()) {
	        		Resource itemRes = propListIt.next();
	        		
	        		propKey = CommonUtil.getStringProperty(itemRes.getValueMap(), MULTIFIELD_PROPERTY_NAME);
	        		propValue = CommonUtil.getStringProperty(itemRes.getValueMap(), MULTIFIELD_PROPERTY_VALUE);
	        		propEncoding = CommonUtil.getStringProperty(itemRes.getValueMap(), MULTIFIELD_PROPERTY_ENCODING);
	        		
	        		// create variable strings that are to be replaced with their respective values in the HTML Fragment
	                // content
	                tokenSearch = "${" + propKey + "}";
	                // escape token values based on encoding type
	                if (HTML_ENCODING.equalsIgnoreCase(propEncoding)) {
	                	propValue = StringEscapeUtils.escapeHtml(propValue);
	                } else if (URL_ENCODING.equalsIgnoreCase(propEncoding)) {
	                	propValue = URLEncoder.encode(propValue, CHARSET_UTF);
	                } else if (JAVASCRIPT_ENCODING.equalsIgnoreCase(propEncoding)) {
	                	propValue = StringEscapeUtils.escapeJavaScript(propValue);
	                }
	                // replace the variables in the HTML Fragment content with their respective values
	                body = StringUtils.replace(body, tokenSearch, propValue);
	        	}
	        }
            
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException while encoding HTML varaibles ", e);
        }
        if (LOGGER.isInfoEnabled()) {
        	LOGGER.info(String.format("Exit from RawHTMLModel processHTMLContent method: %s", body));
        }
        return body;
    }

	/**
	 * @return the htmlBody
	 */
	public String getHtmlBody() {
		return htmlBody;
	}
	
	/**
	 * Gets the toggle inner grid.
	 *
	 * @return the toggle inner grid
	 */
    public String getToggleInnerGrid() {
		return toggleInnerGrid;
	}
	
	/**
	 * @return the componentCSSSourceList
	 */
	public List<String> getComponentCSSSourceList() {
		return componentCSSSourceList;
	}

	/**
	 * @return the componentJSSourceList
	 */
	public List<String> getComponentJSSourceList() {
		return componentJSSourceList;
	}
}
