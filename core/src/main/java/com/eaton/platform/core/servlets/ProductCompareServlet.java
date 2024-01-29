package com.eaton.platform.core.servlets;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.AttrNmBean;
import com.eaton.platform.core.bean.Attribute;
import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.AttributeValue;
import com.eaton.platform.core.bean.GlobalAttrGroupBean;
import com.eaton.platform.core.bean.ImageGroupBean;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.SkuCidDocBean;
import com.eaton.platform.core.bean.TaxynomyAttributeGroupBean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.services.EatonSiteConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SkuImageParser;
import com.eaton.platform.core.vgselector.constants.VGCommonConstants;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.google.gson.Gson;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


@Component(service = Servlet.class,
        immediate = true,
        property = {
                ServletConstants.SLING_SERVLET_METHODS_POST,
                ServletConstants.SLING_SERVLET_PATHS + "/eaton/productcompare",
        })
public class ProductCompareServlet extends SlingAllMethodsServlet{

private static final long serialVersionUID = 2598426539166789515L;

private static final Logger LOGGER = LoggerFactory.getLogger(ProductCompareServlet.class);

@Reference
public transient AdminService adminService;

@Reference
public transient EndecaRequestService endecaRequestService;

@Reference
public transient EndecaService endecaService;

@Reference
public transient ProductFamilyDetailService productFamilyDetailService;

@Reference
private transient EatonSiteConfigService eatonSiteConfigService;

@Reference
protected EatonConfigService configService;

@EatonSiteConfigInjector
public transient Optional<SiteResourceSlingModel> siteResourceSlingModel;

private transient List<AttributeListDetail> attributeGroupList;

private static final String OTHERS = "others";
private static final String MULTIPLE ="multiple";
private static final String TYPE_DOC_LINKS ="DOC_LINKS";
private static final String LINK ="link";
private static final String TYPE_IMAGE = "Image";
private static final String IMAGE = "image";
private static final String TYPE_DOCUMENT = "Dcoument";
private static final String DOCUMENT = "document";
private static final Integer TYPE_LIST_SIZE = 2;
private static final String IMAGE_DIFF = "Image_different";
private static final String BACKSLASH = "\n";
private static final String COMMA = ",";
private static final String LINE_BREAK = "<br/>";
private static final String PRODUCT_URL = "ProductSku Url";
/** The Constant ZOOM. */
private static final String ZOOM = "220x220";

private transient List<String> catalogNo;

@Override
 protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
		 throws ServletException, IOException {
	LOGGER.debug("ProductCompareServlet :: goPost() :: Started");
	List<SKUDetailsBean> skuDetailsBeanCompareList = new ArrayList<SKUDetailsBean>();
	String selectedSku = request.getParameter(CommonConstants.PRODUCT_PARAM_AJAX);
	String facetList = StringUtils.EMPTY;
	catalogNo = new ArrayList<String>();
	final Gson gson = new Gson();
	String resourcePath = null;
	Page currentPage=null;
	if(null!=selectedSku && selectedSku.contains(BACKSLASH)){
		 int lineSeperator= selectedSku.indexOf(BACKSLASH);
		 selectedSku=selectedSku.substring(lineSeperator+1);
	 }
	 String selectedstr[]=selectedSku.split(COMMA);
	 for(int catIndex=0;catIndex<selectedstr.length;catIndex++)
	 {
		 catalogNo.add(selectedstr[catIndex]);
	 }
	 List<Map<String, HashMap>> masterMapList = new ArrayList<Map<String, HashMap>>();
	 final PrintWriter responseWriter = response.getWriter();
	 response.setContentType(VGCommonConstants.APPLICATION_JSON);
	 JSONArray imageArray = new JSONArray();
	 JSONObject jsonImage=null;
	 String baseSKUPath =null;
     if(adminService!=null) {
			try (ResourceResolver adminResourceResolver = adminService.getReadService()) {
				// get the URL of the page on which this servlet is referred
				String refererURL = CommonUtil.getRefererURL(request);
				if(refererURL!=null) {
					// get content path
					resourcePath = CommonUtil.getContentPath(adminResourceResolver, refererURL);
					Resource currentPageRes = adminResourceResolver.resolve(resourcePath);
					currentPage = currentPageRes.adaptTo(Page.class);
					String skuPageName = configService.getConfigServiceBean().getSkupagename();
					baseSKUPath = CommonUtil.getSKUPagePath(currentPage, skuPageName);
					siteResourceSlingModel = eatonSiteConfigService.getSiteConfig(currentPage);
					final SiteResourceSlingModel resourceSlingModel = this.siteResourceSlingModel.get();
					final EndecaServiceRequestBean endecaRequestBean = endecaRequestService
							.getProductCompareEndecaRequestBean(currentPage, catalogNo);
					final SKUDetailsResponseBean skuDetailsResponse = endecaService.getProductCompareSKUList(endecaRequestBean);
					skuDetailsBeanCompareList = skuDetailsResponse.getSkuResponse().getSkuDetailsList();
					attributeGroupList = new ArrayList<AttributeListDetail>();
					if(skuDetailsBeanCompareList.size()>0){
					for (int prodIndex = 0; prodIndex < skuDetailsBeanCompareList.size(); prodIndex++) {
						SKUDetailsBean skuDetailsBean = skuDetailsResponse.getSkuResponse().getSkuDetailsList().get(prodIndex);
						URL imageURL = retriveImage(skuDetailsBean);
						jsonImage = new JSONObject();
						jsonImage.put(TYPE_IMAGE, imageURL);
						imageArray.add(jsonImage);
						final ProductFamilyPIMDetails productFamilyPIMDetailsBean = productFamilyDetailService
								.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);
						if ((null != resourceSlingModel && null != skuDetailsBean) && null != productFamilyPIMDetailsBean) {
							// Used LinkedListMultimap to maintain the sequence
							Map<String, HashMap> masterMap = new LinkedHashMap<String, HashMap>();
							JSONObject productURLJson = new JSONObject();
							productURLJson.put(PRODUCT_URL, baseSKUPath);
							masterMap.put(TYPE_IMAGE, jsonImage);
							masterMap.put(PRODUCT_URL, productURLJson);
							setComponentValues(request, resourceSlingModel, skuDetailsBean, productFamilyPIMDetailsBean, currentPage, masterMap, prodIndex);
							masterMapList.add(masterMap);
						}
					}
				}
				}
			}catch (Exception e){
				LOGGER.error("ProductCompareServlet :: doPost() ::", e.getMessage());
			}
 		}
		Gson productJson = new Gson();
		// convert your list to json
		String jsonCartList = productJson.toJson(masterMapList);
		responseWriter.write(jsonCartList);
		responseWriter.flush();
		LOGGER.debug("ProductCompareServlet :: doPost() :: Exit");
}

 public void addToPos(int pos, JSONObject jsonObj, JSONArray jsonArr){
	   for (int i = jsonArr.size(); i > pos; i--){
	      jsonArr.add(i, jsonArr.get(i-1));
	   }
	   jsonArr.add(pos, jsonObj);
	}
 public void addToPosArra(int pos, JSONArray jsonObj, JSONArray jsonArr){
	   for (int i = jsonArr.size(); i > pos; i--){
	      jsonArr.add(i, jsonArr.get(i-1));
	   }
	   jsonArr.add(pos, jsonObj);
	}

 /*This method returns the image URL for selected comparable products */
 private URL retriveImage(SKUDetailsBean skuDetailsBean) {
	URL imageUrl=null;
	if(skuDetailsBean.getSkuImgs() != null && !(skuDetailsBean.getSkuImgs().isEmpty()) ){
     String xmlString = skuDetailsBean.getSkuImgs();

     SkuImageParser skuImageParser = new SkuImageParser();
     Map<String, ImageGroupBean> attrGrpList = skuImageParser.getPDHImageList(xmlString,adminService);
     if(attrGrpList != null){
         for (Map.Entry<String, ImageGroupBean> entry1 : attrGrpList.entrySet()) {
             if(entry1 != null && entry1.getKey()!=null && !(entry1.getKey().isEmpty()) && entry1.getValue()!= null){
                 String title = entry1.getKey();
                 ImageGroupBean imageGroupBean = entry1.getValue();
                 if(imageGroupBean.getImageRenditionMap() !=null && !(imageGroupBean.getImageRenditionMap().isEmpty())){
                     Map<String, AttrNmBean> renditionMap = imageGroupBean.getImageRenditionMap();
                     for(Map.Entry<String, AttrNmBean> entry2 : renditionMap.entrySet()){
                         String temp = entry2.getKey();
                         AttrNmBean attrNmBean = entry2.getValue();

                         if(StringUtils.containsIgnoreCase(temp, ZOOM)){
                        	 LOGGER.info("Get the Primary image value");
                             if(attrNmBean.getCdata()!=null && !(attrNmBean.getCdata().isEmpty())){
                            	 LOGGER.info("attrNmBean cData is not null");
                             	if("IMAGE_PRIMARY".equals(title)){
                             		try {
										imageUrl=new URL(attrNmBean.getCdata());
									} catch (MalformedURLException e) {
										e.printStackTrace();
									}
                             		break;
                                 }
                             }
                         }
                     }
                 }
             }
         }
     }
 }
	return imageUrl;

}

public void setComponentValues(SlingHttpServletRequest request,SiteResourceSlingModel siteConfiguration,
			SKUDetailsBean skuData, ProductFamilyPIMDetails productFamilyPIMDetails, Page currentPage,Map masterMap, int catalogIndex ) {
		SkuImageParser attributeXmlParser = new SkuImageParser();
		Map<String,List<SkuCidDocBean>> cidDataMap = new HashMap<String,List<SkuCidDocBean>>();

		Locale languageValue = currentPage.getLanguage(false);
		String currentLanguage = CommonConstants.EN_US;
		if ((languageValue != null) && (languageValue.getLanguage() != null)
				&& (!languageValue.getLanguage().isEmpty()) && (languageValue.getCountry() != null)
				&& (!languageValue.getCountry().isEmpty())) {
			currentLanguage = languageValue.getLanguage() + CommonConstants.HYPHEN
					+ languageValue.getCountry().toLowerCase();
		}
		if(skuData!=null){
			cidDataMap = CommonUtil.getSkuCidData(skuData, adminService);
		}
		if((siteConfiguration!=null) && (siteConfiguration.getGlobalAttributeList()!=null) && (!siteConfiguration.getGlobalAttributeList().isEmpty())){

			List<GlobalAttrGroupBean> globalAttributeGroupList = siteConfiguration.getGlobalAttributeList();
			if(skuData!=null){

				 Map<String, AttrNmBean> globalAttributeMap ;
				 globalAttributeMap = attributeXmlParser.getAttributeMapWithMultiRowMultiValue(skuData.getGlobalAttrs(), adminService, true);

				 List<Attribute> globalAttributeBeanList = new ArrayList<Attribute>();
				 AttributeListDetail attributeListDetailbean = new AttributeListDetail();
				 if(globalAttributeMap!=null) {
					 for (int i = 0; i < globalAttributeGroupList.size(); i++) {

						 String attributeId = globalAttributeGroupList.get(i).getAttrValue();
						 if ((attributeId != null) && (!attributeId.isEmpty())) {
							 AttrNmBean attributeBean = globalAttributeMap.get(attributeId);
							 if(attributeBean!=null) {
							 AttributeValue attributeValue = new AttributeValue();
							 Attribute attribute = new Attribute();

							 attributeValue.setAttributeType(OTHERS);
							 attributeValue.setCdata(attributeBean.getCdata().replace("\\|", LINE_BREAK));
							 if ((cidDataMap != null) && (cidDataMap.size() > 0)) {
								 String cid = attributeBean.getCid();
								 if ((cid != null) && (!cid.isEmpty())) {
									 List<SkuCidDocBean> cidDataBeanList = cidDataMap.get(cid);
									 if(cidDataBeanList!=null) {
									 if (cidDataBeanList.size() > 1) {
										 attributeValue.setAttributeType(MULTIPLE);
									 }
									 attributeValue = updateAttributeValue(currentPage, cidDataBeanList,attributeValue);
								 }
								 }
							 }
							 attribute.setAttributeValue(attributeValue);
							 attribute.setAttributeLabel(attributeBean.getLabel());
							 attribute.setAttributeId(attributeBean.getId());
							 globalAttributeBeanList.add(attribute);
								String attributeKey;
								if (attributeBean.getId() != null) {
									attributeKey = attributeBean.getLabel() + "," + attributeBean.getId();
								} else {
									attributeKey = attributeBean.getLabel() + "," + attributeBean.getLabel();
								}
							}
						}
					}
				}

				 if(!globalAttributeBeanList.isEmpty()){
					 attributeListDetailbean.setAttributeList(globalAttributeBeanList);
					 String groupName = siteConfiguration.getGlobalAttributeGroupName();
					 if((groupName!=null) && (!groupName.isEmpty())){
						 attributeListDetailbean.setAttributeGroupName(groupName);
						 attributeGroupList.add(attributeListDetailbean);
						 Map<String,String> attributeMap = new LinkedHashMap<String, String>();
						 List<Attribute> attributeList = attributeListDetailbean.getAttributeList();
						 if(null!=attributeList) {
							 for (int j = 0; j < attributeList.size(); j++) {
								 attributeMap.put(attributeList.get(j).getAttributeId() + CommonConstants.PROD_COMP_KEY + attributeList.get(j).getAttributeLabel(), attributeList.get(j).getAttributeValue().getCdata());
								 if (attributeList.get(j).getAttributeId().equalsIgnoreCase(CommonConstants.CATALOG_ID)) {
									 masterMap.put(CommonConstants.CATALOG_NUMBER + CommonConstants.PROD_COMP_KEY + attributeList.get(j).getAttributeLabel(), attributeList.get(j).getAttributeValue().getCdata());
								 }
								 if (attributeList.get(j).getAttributeId().equalsIgnoreCase(CommonConstants.PRODUCT_ID)) {
									 masterMap.put(CommonConstants.PRODUCT_NAME + CommonConstants.PROD_COMP_KEY + attributeList.get(j).getAttributeLabel(), attributeList.get(j).getAttributeValue().getCdata());
								 }
							 }
						 }
						 masterMap.put(CommonConstants.CATEGORIES_+CommonConstants.CATEGORIES_GENERAL_SPEC+groupName, attributeMap);
					 }
				 }
			}
		} else {
			if(skuData!=null){

				 Map<String, AttrNmBean> globalAttributeMap ;
				 globalAttributeMap = attributeXmlParser.getAttributeMapWithMultiRowMultiValue(skuData.getGlobalAttrs(), adminService, true);

				 List<Attribute> globalAttributeBeanList = new ArrayList<Attribute>();
				 AttributeListDetail attributeListDetailbean = new AttributeListDetail();
				 if(globalAttributeMap !=null) {
					 for (Map.Entry<String, AttrNmBean> entry : globalAttributeMap.entrySet()) {

						 AttrNmBean attributeBean = entry.getValue();
						 Attribute attribute = new Attribute();
						 AttributeValue attributeValue = new AttributeValue();

						 attributeValue.setAttributeType(OTHERS);
						 attributeValue.setCdata(attributeBean.getCdata().replace("\\|", LINE_BREAK));

						 if ((cidDataMap != null) && (cidDataMap.size() > 0)) {
							 String cid = attributeBean.getCid();
							 if ((cid != null) && (!cid.isEmpty())) {
								 List<SkuCidDocBean> cidDataBeanList = cidDataMap.get(cid);
								 if(cidDataBeanList!=null) {
								 if (cidDataBeanList.size() > 1) {
									 attributeValue.setAttributeType(MULTIPLE);
								 }
								 attributeValue = updateAttributeValue(currentPage,cidDataBeanList,attributeValue);
							 }
							 }
						 }

						 attribute.setAttributeValue(attributeValue);
						 attribute.setAttributeLabel(attributeBean.getLabel());
						 globalAttributeBeanList.add(attribute);
					 }
				 }

				 if(!globalAttributeBeanList.isEmpty()){
					 attributeListDetailbean.setAttributeList(globalAttributeBeanList);
					 String groupName = siteConfiguration.getGlobalAttributeGroupName();
					 if((groupName!=null) && (!groupName.isEmpty())){
						 attributeListDetailbean.setAttributeGroupName(groupName);
						 attributeGroupList.add(attributeListDetailbean);
						 Map<String,String> attributeMap = new LinkedHashMap<String, String>();
						 List<Attribute> attributeList = attributeListDetailbean.getAttributeList();
						 if(null!=attributeList) {
							 for (int j = 0; j < attributeList.size(); j++) {
								 attributeMap.put(attributeList.get(j).getAttributeId() + CommonConstants.PROD_COMP_KEY + attributeList.get(j).getAttributeLabel(), attributeList.get(j).getAttributeValue().getCdata());
								 if (attributeList.get(j).getAttributeId().equalsIgnoreCase(CommonConstants.CATALOG_ID)) {
									 masterMap.put(CommonConstants.CATALOG_NUMBER + CommonConstants.PROD_COMP_KEY + attributeList.get(j).getAttributeLabel(), attributeList.get(j).getAttributeValue().getCdata());
								 }
								 if (attributeList.get(j).getAttributeId().equalsIgnoreCase(CommonConstants.PRODUCT_ID)) {
									 masterMap.put(CommonConstants.PRODUCT_NAME + CommonConstants.PROD_COMP_KEY + attributeList.get(j).getAttributeLabel(), attributeList.get(j).getAttributeValue().getCdata());
								 }
							 }
						 }
						 masterMap.put(CommonConstants.CATEGORIES_+CommonConstants.CATEGORIES_GENERAL_SPEC+groupName, attributeMap);
					 }
				 }
			}
		}

		if((productFamilyPIMDetails!=null) && (productFamilyPIMDetails.getTaxonomyAttributeGroupList()!=null) && !(productFamilyPIMDetails.getTaxonomyAttributeGroupList().isEmpty())){

			List<TaxynomyAttributeGroupBean> taxynomyAttributeGroupList = productFamilyPIMDetails.getTaxonomyAttributeGroupList();
			if(skuData!=null){
				 Map<String, AttrNmBean> taxonomyAttributeMap = new HashMap<String,AttrNmBean>() ;

				 if((skuData.getTaxonomyAttrs()!=null) && (!skuData.getTaxonomyAttrs().isEmpty())){
					 taxonomyAttributeMap = attributeXmlParser.getAttributeMapWithMultiRowMultiValue(skuData.getTaxonomyAttrs(), adminService, true);
				 }

				 if((taxonomyAttributeMap!=null) && (taxonomyAttributeMap.size()>0)){
					 for(int i = 0; i < taxynomyAttributeGroupList.size(); i++){
						 List<Attribute> taxonomyAttributeBeanList = new ArrayList<Attribute>();

						 AttributeListDetail attributeListDetailbean = new AttributeListDetail();

						 List<String> taxonomyAttributeList = new ArrayList<String>();
						 if(taxynomyAttributeGroupList.get(i).getAttributeList()!=null){
							taxonomyAttributeList = taxynomyAttributeGroupList.get(i).getAttributeList();
						 }

						 boolean isAttributePresent = false;
						 for(int j = 0; j < taxonomyAttributeList.size(); j++){
							 String attributeId = taxonomyAttributeList.get(j);
							 if((attributeId!=null) && (!attributeId.isEmpty())){
									 AttrNmBean attributeBean = taxonomyAttributeMap.get(attributeId);
									 if(attributeBean!=null) {
									 Attribute attribute = new Attribute();
								     AttributeValue attributeValue = new AttributeValue();
								     isAttributePresent = true;
									 attributeValue.setAttributeType(OTHERS);
									 attributeValue.setCdata(attributeBean.getCdata().replace("\\|", LINE_BREAK));
									 if((cidDataMap!=null) && (cidDataMap.size()>0)) {
											String cid = attributeBean.getCid();
											if((cid!=null) && (!cid.isEmpty()) && (cidDataMap.containsKey(cid))){
													List<SkuCidDocBean> cidDataBeanList = cidDataMap.get(cid);
													if(cidDataBeanList!=null) {
													if(cidDataBeanList.size()>1){
														attributeValue.setAttributeType(MULTIPLE);
													}
												attributeValue = updateAttributeValue(currentPage,cidDataBeanList,attributeValue);
											}
											}
										}

									 attribute.setAttributeValue(attributeValue);
									 attribute.setAttributeLabel(attributeBean.getLabel());
									 attribute.setAttributeId(attributeBean.getId());
									 taxonomyAttributeBeanList.add(attribute);
							 }
							 }
						 }
						 if(!taxonomyAttributeBeanList.isEmpty()){

							 attributeListDetailbean.setAttributeList(taxonomyAttributeBeanList);
							 String groupName = taxynomyAttributeGroupList.get(i).getGroupName();
							 String groupId = taxynomyAttributeGroupList.get(i).getGroupId();
							 if((groupName!=null) && (!groupName.isEmpty())){
								 attributeListDetailbean.setAttributeGroupName(groupName);
								 attributeGroupList.add(attributeListDetailbean);
								 Map<String,String> attributeMap = new LinkedHashMap<String, String>();
								 List<Attribute> attributeList = attributeListDetailbean.getAttributeList();
								 if(null!=attributeList) {
									 for (int j = 0; j < attributeList.size(); j++) {
										 attributeMap.put(attributeList.get(j).getAttributeId() + CommonConstants.PROD_COMP_KEY + attributeList.get(j).getAttributeLabel(), attributeList.get(j).getAttributeValue().getCdata());
										 if (attributeList.get(j).getAttributeId().equalsIgnoreCase(EndecaConstants.RUNTIME_GRAPH_ID)) {
											 String runTimeGraphURL = EndecaConstants.RUNTIME_GRAPH_URL
													 + currentLanguage + CommonConstants.SLASH_STRING + catalogNo.get(catalogIndex);
											 attributeMap.put(EndecaConstants.RUNTIME_GRAPH_URL_LABEL, runTimeGraphURL);
										 }

									 }
								 }
								 masterMap.put(CommonConstants.CATEGORIES_+groupId+CommonConstants.PROD_COMP_KEY+groupName, attributeMap);
							 }
						 }
					 }
				 }
			}
		} else {
			if(skuData!=null){
				 Map<String, AttrNmBean> taxonomyAttributeMap = new HashMap<String,AttrNmBean>() ;

				 if((skuData.getTaxonomyAttrs()!=null) && (!skuData.getTaxonomyAttrs().isEmpty())){
					 taxonomyAttributeMap = attributeXmlParser.getAttributeMapWithMultiRowMultiValue(skuData.getTaxonomyAttrs(), adminService, true);
				 }

				 if((taxonomyAttributeMap!=null) && (taxonomyAttributeMap.size()>0)){
					 List<Attribute> taxonomyAttributeBeanList = new ArrayList<Attribute>();
					 AttributeListDetail attributeListDetailbean = new AttributeListDetail();
					 for (Map.Entry<String, AttrNmBean> entry : taxonomyAttributeMap.entrySet()){
						 Attribute attribute = new Attribute();
						 AttrNmBean attributeBean = entry.getValue();
						 AttributeValue attributeValue = new AttributeValue();
						 attributeValue.setAttributeType(OTHERS);

						 attributeValue.setCdata(attributeBean.getCdata().replace("\\|", LINE_BREAK));
						 if((cidDataMap!=null) && (cidDataMap.size()>0)){
								String cid = attributeBean.getCid();
								if((cid!=null) && (!cid.isEmpty()) && (cidDataMap.containsKey(cid))){
										List<SkuCidDocBean> cidDataBeanList = cidDataMap.get(cid);
										if(cidDataBeanList.size()>1){
											attributeValue.setAttributeType(MULTIPLE);
										}
									attributeValue = updateAttributeValue(currentPage,cidDataBeanList,attributeValue);

								}
							}

						 attribute.setAttributeValue(attributeValue);
						 attribute.setAttributeLabel(attributeBean.getLabel());
						 attribute.setAttributeId(attributeBean.getId());
						 taxonomyAttributeBeanList.add(attribute);
					 }

					 if(!taxonomyAttributeBeanList.isEmpty()){

						 attributeListDetailbean.setAttributeList(taxonomyAttributeBeanList);
						 String groupName = CommonUtil.getI18NFromResourceBundle(request,currentPage,CommonConstants.DEFAULT_TAXONOMY_ATTRIBUTE_LABEL,CommonConstants.DEFAULT_TAXONOMY_ATTRIBUTE_LABEL_DEFAULT);
						 if((groupName!=null) && (!groupName.isEmpty())){
							 attributeListDetailbean.setAttributeGroupName(groupName);
							 attributeGroupList.add(attributeListDetailbean);
							 Map<String,String> attributeMap = new LinkedHashMap<String, String>();
							 List<Attribute> attributeList = attributeListDetailbean.getAttributeList();
							 if(null!=attributeList) {
								 for (int j = 0; j < attributeList.size(); j++) {
									 attributeMap.put(attributeList.get(j).getAttributeId() + CommonConstants.PROD_COMP_KEY + attributeList.get(j).getAttributeLabel(), attributeList.get(j).getAttributeValue().getCdata());
									 if (attributeList.get(j).getAttributeId().equalsIgnoreCase(EndecaConstants.RUNTIME_GRAPH_ID)) {
										 String runTimeGraphURL = EndecaConstants.RUNTIME_GRAPH_URL
												 + currentLanguage + CommonConstants.SLASH_STRING + catalogNo.get(catalogIndex);
										 attributeMap.put(EndecaConstants.RUNTIME_GRAPH_URL_LABEL, runTimeGraphURL);
									 }
								 }
							 }
							 masterMap.put(CommonConstants.CATEGORIES_+CommonConstants.PRODUCT_SPEC+CommonConstants.PROD_COMP_KEY+groupName, attributeMap);
						 }
					 }
				 }
			}
		}
	}

	private AttributeValue  updateAttributeValue(Page currentPage,List<SkuCidDocBean> cidDataBeanList,AttributeValue attributeValue) {
     if(cidDataBeanList != null) {
			for (int t = 0; t < cidDataBeanList.size(); t++) {
				SkuCidDocBean cidDataBean = cidDataBeanList.get(t);
				if ((cidDataBean != null) && (isCidEligible(currentPage,cidDataBean))) {
					attributeValue.setPriority(cidDataBean.getDocPriority());
					attributeValue.setLanguage(cidDataBean.getLanguage());
					attributeValue.setCountry(cidDataBean.getCountry());
					attributeValue.setCidCdata(cidDataBean.getDocCdataValue());
					if ((cidDataBean.getType() != null) && (!cidDataBean.getType().isEmpty())) {
					   if (cidDataBean.getType().equals(TYPE_DOC_LINKS)) {
						  if(cidDataBeanList.size() < TYPE_LIST_SIZE) {
								attributeValue.setAttributeType(LINK); }
							attributeValue.setLinkURL(cidDataBean.getDocCdataValue());
						} else if (cidDataBean.getType().equals(TYPE_IMAGE)){
							if (cidDataBeanList.size() < TYPE_LIST_SIZE) {
								attributeValue.setAttributeType(IMAGE); }
							attributeValue.setImageURL(cidDataBean.getDocCdataValue());
						} else if (cidDataBean.getType().equals(TYPE_DOCUMENT)){
							if(cidDataBeanList.size() < TYPE_LIST_SIZE) {
								attributeValue.setAttributeType(DOCUMENT); }
							attributeValue.setDocumentURL(cidDataBean.getDocCdataValue());
						} else {
							LOGGER.debug("Error just setting the link only");
							attributeValue.setDocumentURL(cidDataBean.getDocCdataValue());
						}
					}

				}
			}
		}
     return attributeValue;
	}

public boolean isCidEligible(Page currentPage,SkuCidDocBean cidBean){

		Locale languageValue = currentPage.getLanguage(false);
	    String cuurentLanguage = StringUtils.EMPTY;
	    String currentCountry = StringUtils.EMPTY ;
	    String documentLanguage;
	    String[] documentCountry = null ;
	    boolean documentEligible = false ;
	    boolean languageEligible = false ;
	    boolean countryEligible = false;
		final String GLOBAL = "Global";

	    if ((languageValue != null) && (languageValue.getLanguage() != null) && (!languageValue.getLanguage().isEmpty()) && (languageValue.getCountry() != null) && (!languageValue.getCountry().isEmpty()) ) {
					currentCountry = languageValue.getCountry();
					cuurentLanguage = languageValue.getLanguage()+"_"+languageValue.getCountry();

		}

	    documentLanguage = cidBean.getLanguage();
	    documentCountry = cidBean.getCountry();
	   if((documentLanguage != null) &&((documentLanguage.equals(GLOBAL)) || (documentLanguage.equals(cuurentLanguage)))) {
			   languageEligible = true;
	   }

	   if((documentCountry[0]!=null)&&(documentCountry[0].equals(GLOBAL)) || (ArrayUtils.contains(documentCountry, currentCountry))) {
			   countryEligible = true;
	   }

	    if((languageEligible) && (countryEligible)){
	    	documentEligible = true;
	    }

	    return documentEligible;
	}

	public List<AttributeListDetail> getAttributeGroupList() {
		return attributeGroupList;
	}

	public void setAttributeGroupList(List<AttributeListDetail> attributeGroupList) {
		this.attributeGroupList = attributeGroupList;
	}

	/*This method matches all product values for all comparable attributes and return true if matches else return false */
	public boolean verifyAllProductValuesSame(List<String> list) {
	    return IterableUtils.matchesAll(list, new org.apache.commons.collections4.Predicate<String>() {
	        public boolean evaluate(String s) {
	            return s.equals(list.get(0));
	        }
	    });
	}

}