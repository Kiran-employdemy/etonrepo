package com.eaton.platform.core.vgselector.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.loadmore.Image;
import com.eaton.platform.core.bean.loadmore.Link;
import com.eaton.platform.core.bean.loadmore.ProductAttribute;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.vgselector.bean.loadmore.ClutchListContentItem;
import com.eaton.platform.core.vgselector.bean.loadmore.ClutchListLoadMore;
import com.eaton.platform.core.vgselector.bean.loadmore.ClutchListResultsList;
import com.eaton.platform.core.vgselector.bean.loadmore.TorqueListContentItem;
import com.eaton.platform.core.vgselector.bean.loadmore.TorqueListLoadMore;
import com.eaton.platform.core.vgselector.bean.loadmore.TorqueListResultsList;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.integration.endeca.bean.familymodule.SKUCardParameterBean;
import com.eaton.platform.integration.endeca.bean.vgproductselector.clutchselector.ClutchTool;
import com.eaton.platform.integration.endeca.bean.vgproductselector.torqueselector.TorqueTool;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;

/**
 * The Class VGSelectorLoadMoreHelper.
 */
public class VGSelectorLoadMoreHelper {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(VGSelectorLoadMoreHelper.class);
	
	/**
	 * Sets the clutch list load more values.
	 *
	 * @param clutchToolList the clutch tool list
	 * @param skuPageName the sku page name
	 * @param currentPage the current page
	 * @param fallBackImage the fall back image
	 * @param longDescCheck the long desc check
	 * @param resourceResolver the resource resolver
	 * @return the clutch list load more
	 */
	public ClutchListLoadMore setClutchListLoadMoreValues(List<ClutchTool> clutchToolList, String skuPageName, Page currentPage, String fallBackImage,String longDescCheck, ResourceResolver resourceResolver){
		
		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		homePagePath = CommonUtil.dotHtmlLinkSKU(homePagePath,resourceResolver);
		
		ClutchListLoadMore clutchListLoadMore = new ClutchListLoadMore();
		try{
		List<ClutchListResultsList>  cluctchListResultsLists= new  ArrayList<>();
		for (ClutchTool clutchTool : clutchToolList) {
			
			ClutchListResultsList clutchListResults = new ClutchListResultsList();
			ClutchListContentItem contentItem = new ClutchListContentItem();
			
			contentItem.setName(clutchTool.getCatalogNumber());
			
			if(longDescCheck.equalsIgnoreCase(VGCommonConstants.TRUE)){
				contentItem.setDescription(clutchTool.getLongDesc());
			}else{
				contentItem.setDescription(StringUtils.EMPTY);
			}
			
			Image image = new Image();
			if(clutchTool.getDsktopRendition() != null && !clutchTool.getDsktopRendition().equals(StringUtils.EMPTY)){
			image.setMobile(clutchTool.getMobileRendition());
			image.setTablet(clutchTool.getDsktopRendition());
			image.setDesktop(clutchTool.getDsktopRendition());
			}else{
				image.setMobile(fallBackImage);
				image.setTablet(fallBackImage);
				image.setDesktop(fallBackImage);
			}
			contentItem.setImage(image);
			
			Link link = new Link();
			link.setUrl(homePagePath + skuPageName.concat(VGCommonConstants.DOT + CommonUtil.encodeURLString(clutchTool.getCatalogNumber())).concat(CommonConstants.HTML_EXTN));
			contentItem.setLink(link);
			
			List<ProductAttribute> productAttributesList = new ArrayList<>();
			for (SKUCardParameterBean skuCardParameter:clutchTool.getSkuCardParameters()) {
				ProductAttribute productAttribute = new ProductAttribute();
				productAttribute.setProductAttributeLabel(skuCardParameter.getLabel());
				productAttribute.setProductAttributeValue(skuCardParameter.getSkuCardValues());
				productAttributesList.add(productAttribute);
			}
			
			contentItem.setProductAttributes(productAttributesList);
			
			clutchListResults.setContentItem(contentItem);
			cluctchListResultsLists.add(clutchListResults);
		}
		
		clutchListLoadMore.setResultsList(cluctchListResultsLists);
		}catch(Exception e){
			LOGGER.error("Exception occured in setClutchListLoadMoreValues()"+e);
		}
		
		return clutchListLoadMore;
	}

	/**
	 * Convert clutch tool bean TOJSON.
	 *
	 * @param clutchListLoadMore the clutch list load more
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String convertClutchToolBeanTOJSON(ClutchListLoadMore clutchListLoadMore) throws IOException {
		LOGGER.debug("Entry into convertClutchToolBeanTOJSON method");
		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest;
	
		try {
			// Object to JSON in String using Jackson mapper
			jsonRequest = mapper.writeValueAsString(clutchListLoadMore);
	
		} catch (JsonGenerationException e) {
			throw new JsonGenerationException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch (JsonMappingException e) {
			throw new JsonMappingException(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("JSON request :"+jsonRequest);
		LOGGER.debug("Exit from convertClutchToolBeanTOJSON method");
	
		return jsonRequest;
	}
	
	/**
	 * Convert torque tool bean TOJSON.
	 *
	 * @param torqueListLoadMore the torque list load more
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String convertTorqueToolBeanTOJSON(TorqueListLoadMore torqueListLoadMore) throws IOException {
		LOGGER.debug("Entry into convertTorqueToolBeanTOJSON method");
		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest;
	
		try {
	
			// Object to JSON in String using Jackson mapper
			jsonRequest = mapper.writeValueAsString(torqueListLoadMore);
	
		} catch (JsonGenerationException e) {
			throw new JsonGenerationException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch (JsonMappingException e) {
			throw new JsonMappingException(EndecaConstants.INVALID_ENTRY_MSG, e);
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("JSON request :"+jsonRequest);
		LOGGER.debug("Exit from convertTorqueToolBeanTOJSON method");
	
		return jsonRequest;
	}

	/**
	 * Sets the torque list load more values.
	 *
	 * @param torqueToolList the torque tool list
	 * @param skuPageName the sku page name
	 * @param currentPage the current page
	 * @param fallBackImage the fall back image
	 * @param longDescCheck the long desc check
	 * @param resourceResolver the resource resolver
	 * @return the torque list load more
	 */
	public TorqueListLoadMore setTorqueListLoadMoreValues(List<TorqueTool> torqueToolList, String skuPageName, Page currentPage, String fallBackImage,String longDescCheck, ResourceResolver resourceResolver){
		
		String homePagePath = CommonUtil.getHomePagePath(currentPage);
		homePagePath = CommonUtil.dotHtmlLinkSKU(homePagePath,resourceResolver);
		
		TorqueListLoadMore torqueListLoadMore = new TorqueListLoadMore();
		try{
		List<TorqueListResultsList>  torqueListResultsLists= new  ArrayList<>();
		for (TorqueTool torqueTool : torqueToolList) {
			
			TorqueListResultsList torqueListResults = new TorqueListResultsList();
			TorqueListContentItem contentItem = new TorqueListContentItem();
			
			contentItem.setName(torqueTool.getCatalogNumber());
			
			if(longDescCheck.equalsIgnoreCase(VGCommonConstants.TRUE)){
				contentItem.setDescription(torqueTool.getLongDesc());
			}else{
				contentItem.setDescription(StringUtils.EMPTY);
			}
			
			Image image = new Image();
			if(torqueTool.getDsktopRendition() != null && !torqueTool.getDsktopRendition().equals(StringUtils.EMPTY)){
			image.setMobile(torqueTool.getMobileRendition());
			image.setTablet(torqueTool.getDsktopRendition());
			image.setDesktop(torqueTool.getDsktopRendition());
			}else{
				image.setMobile(fallBackImage);
				image.setTablet(fallBackImage);
				image.setDesktop(fallBackImage);
			}
			contentItem.setImage(image);
			
			Link link = new Link();
			link.setUrl(homePagePath + skuPageName.concat("."+CommonUtil.encodeURLString(torqueTool.getCatalogNumber())).concat(CommonConstants.HTML_EXTN));
			contentItem.setLink(link);
			
			List<ProductAttribute> productAttributesList = new ArrayList<>();
			for (SKUCardParameterBean skuCardParameter:torqueTool.getSkuCardParameters()) {
				ProductAttribute productAttribute = new ProductAttribute();
				productAttribute.setProductAttributeLabel(skuCardParameter.getLabel());
				productAttribute.setProductAttributeValue(skuCardParameter.getSkuCardValues());
				productAttributesList.add(productAttribute);
			}
			
			contentItem.setProductAttributes(productAttributesList);
			
			torqueListResults.setContentItem(contentItem);
			torqueListResultsLists.add(torqueListResults);
		}
		
		torqueListLoadMore.setResultsList(torqueListResultsLists);
		}catch(Exception e){
			LOGGER.error("Exception occured in setTorqueListLoadMoreValues()"+e);
		}
		
		return torqueListLoadMore;
	}

}
