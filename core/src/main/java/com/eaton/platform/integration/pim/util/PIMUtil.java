package com.eaton.platform.integration.pim.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.constants.CommonConstants;

/**
 * The Class PIMUtil.
 */
public final class PIMUtil {
	/** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(PIMUtil.class);
    
    /** The Constant INDEX_START_STRING. */
	private static final String INDEX_START_STRING = "_cq_dialog.html";
	
	/** The Constant INDEX_END_STRING. */
	private static final String INDEX_END_STRING = "/jcr:content";
	
	/** The Constant PIM_PAGE_PATH. */
	private static final String PIM_PAGE_PATH = "pimPagePath";
	
    
    /**
     * Instantiates a new pim util.
     */
    private PIMUtil() {
        LOGGER.debug("Inside PIMUtil constructor");
    }
    
    /**
     * Gets the page path from request.
     *
     * @param request the request
     * @return the page path from request
     */
    public static String getPagePathFromRequest(SlingHttpServletRequest request){
    	LOGGER.info("PIMUtil :: getPagePathFromRequest() :: Start");
		String pagePath = null;
		// item param exists when page properties is accessed from sites.html console
		pagePath = request.getParameter("item");
		// else get the page path from request.getPathInfo()
		if(null == pagePath){
			String requestString = request.getPathInfo();
			int indexStart = requestString.indexOf(INDEX_START_STRING);
			indexStart = indexStart+15;
			int indexEnd = requestString.indexOf(INDEX_END_STRING);
			if(indexStart != -1 && indexEnd != -1){
				pagePath = requestString.substring(indexStart, indexEnd);
			}
		}
		
		
		LOGGER.info("PIMUtil :: getPagePathFromRequest() :: End");
		return pagePath;
	}

	/**
	 * Gets the Corresponding English PIM Path.
	 *
	 * @param pimPagePath the path to the pim
	 * @return the path to the English PIM
	 */
	public static String getEnglishPIMPagePath(String pimPagePath)	{
		final String pagePathLanguage = StringUtils.substringBetween(pimPagePath, CommonConstants.PIM_PATH,
				CommonConstants.SLASH_STRING);
		if (StringUtils.isNotBlank(pagePathLanguage)) {
			pimPagePath = pimPagePath.replace(pagePathLanguage, CommonConstants.ENGLISH_LANGUAGE);
		}
		return pimPagePath;
	}


	/**
     * Gets the PIM page path.
     *
     * @param page the page
     * @return the PIM page path
     */
    public static String getPIMPagePath(Page page)	{
    	LOGGER.info("PIMUtil :: getPIMPagePath() :: Start");
    	String pimPath = null;
		if(page.getProperties().get(PIM_PAGE_PATH) != null){
			pimPath = page.getProperties().get(PIM_PAGE_PATH).toString();		
		}
		LOGGER.info("PIMUtil :: getPIMPagePath() :: Start");
		return pimPath;
	}

	/**
	 * Sort map by values.
	 *
	 * @param unsortedMap the unsorted map
	 * @return the map
	 */
	public static Map<String, String> sortMapByValues(Map<String, String> unsortedMap) {
		LOGGER.debug("******** sortMapByValues execution started ***********");
		
        List<String> mapKeys = new ArrayList<>(unsortedMap.keySet());
	    List<String> mapValues = new ArrayList<>(unsortedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);

	    LinkedHashMap<String, String> sortedMap = new LinkedHashMap<>();

        Iterator<String> keyIt = mapKeys.iterator();

        while (keyIt.hasNext()) {
        	String sortedKey = keyIt.next();
			if (StringUtils.isNotEmpty(sortedKey)) {
				sortedMap.put(sortedKey, unsortedMap.get(sortedKey));
			}
        }
	    LOGGER.debug("******** sortMapByValues execution ended ***********");
	    return sortedMap;
	}
	
	/**
	 * Returns a list of value map resources based upon the unsorted map.
	 * @param adminResourceResolver A resource resolver capable of being used to create a value map resource.
	 * @param unsortedMap An unsorted map of values where the keys are the text seen in the drop down and the value is the value stored by the dropdown.
	 * @return A sorted list of value map resources based upon the provided unsortedMap along with a none option added if specified with the label provided.
	 */
	public static List<Resource> prepareDropDownList(final ResourceResolver adminResourceResolver, final HashMap<String, String> unsortedMap, final boolean addNoneOption, final String noneLabel) {
		final List<Resource> dropdownList = new ArrayList<>();

		if (addNoneOption) {
            final ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
            valueMap.put(CommonConstants.VALUE, StringUtils.EMPTY);
            valueMap.put(CommonConstants.TEXT, noneLabel);
            dropdownList.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(),
                    JcrConstants.NT_UNSTRUCTURED, valueMap));
		}

		final Map<String, String> sortedMap = sortMapByValues(unsortedMap);

		for (Entry<String, String> entry : sortedMap.entrySet()) {
			final ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
			valueMap.put(CommonConstants.VALUE, entry.getValue());
			valueMap.put(CommonConstants.TEXT, entry.getKey());
			dropdownList.add(new ValueMapResource(adminResourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, valueMap));
		}

		return dropdownList;
	}

	/**
	 * @param adminResourceResolver A resource resolver capable of being used to create a value map resource.
	 * @param unsortedMap An unsorted map of values where the keys are the text seen in the drop down and the value is the value stored by the dropdown.
     * @return A sorted list of value map resources based upon the provided unsortedMap along with a none option.
     */
	public static List<Resource> prepareDropDownList(final ResourceResolver adminResourceResolver, final HashMap<String, String> unsortedMap) {
		return prepareDropDownList(adminResourceResolver, unsortedMap, true, CommonConstants.DROPDOWN_DEFAULT_OPTION);
	}
}