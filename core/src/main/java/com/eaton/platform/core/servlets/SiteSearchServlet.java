package com.eaton.platform.core.servlets;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.FacetBean;
import com.eaton.platform.core.bean.FacetURLBean;
import com.eaton.platform.core.bean.FacetURLBeanServiceResponse;
import com.eaton.platform.core.bean.ProductGridSelectors;
import com.eaton.platform.core.bean.loadmore.Image;
import com.eaton.platform.core.bean.loadmore.Link;
import com.eaton.platform.core.bean.sitesearch.SecondaryLink;
import com.eaton.platform.core.bean.sitesearch.SiteSearch;
import com.eaton.platform.core.bean.sitesearch.SiteSearchContentItem;
import com.eaton.platform.core.bean.sitesearch.SiteSearchResultsList;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.enums.secure.SecureModule;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EatonConfigService;
import com.eaton.platform.core.services.FacetURLBeanService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.FilterUtil;
import com.eaton.platform.integration.auth.services.AuthorizationService;
import com.eaton.platform.integration.endeca.bean.EndecaConfigServiceBean;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.FilterBean;
import com.eaton.platform.integration.endeca.bean.factories.SecureFilterBeanFactory;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchPageResponse;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResponse;
import com.eaton.platform.integration.endeca.bean.sitesearch.SiteSearchResultsBean;
import com.eaton.platform.integration.endeca.constants.EndecaConstants;
import com.eaton.platform.integration.endeca.services.EndecaConfig;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component(service = Servlet.class,
		immediate = true,
		property = {
				ServletConstants.SLING_SERVLET_METHODS_GET,
				ServletConstants.SLING_SERVLET_PATHS + "/eaton/content/search/loadmore",
		})
public class SiteSearchServlet extends SlingSafeMethodsServlet{
	private static final Logger LOG = LoggerFactory.getLogger(SiteSearchServlet.class);

	private String Mobile_SKU_Image = "/content/dam/eaton/resources/default-sku-image-220.jpg";
	private static final String SEARCH = "search";
	private static final String COUNTRY_CONSTANT = "Country";

	private static String SPECIFICATION_HASH_URL=".html#Specifications";
	private static String RESOURCE_HASH_URL=".html#Resources";

	private String language;
	private String country;
	private String pageType;
	private String primarySubCategoryTag;
	private String pageSize;
	private String unitedStatesDateFormat;
	private String defaultSortOrder;
	private String activeFacets;
	private String currentLoadMore;
	private String currentSortBy;
	private String currentResourcePath;
	private String pimPrimaryImage;
	private String extensionId;
	private String selectors;
	private String requestUri;
	private String skuCardAttr;
	private String skuPageUrl;
	private String skuFallBackImage;
	private String returnFacetsFor;
	private int totalCount;
	private String countString;
	private String jsonResponse;
    private ProductGridSelectors productGridSelectors;
	private FacetURLBean facetURLBean;
	private List<SiteSearchResultsBean> siteSearchEndecaResults;
	private EndecaConfigServiceBean endecaConfigBean = null;
	public static final String DOCUMENT_STRING = "docx";
	public static final String SPREADSHEET_STRING = "xlsx";
	public static final String MS_EXCEL_STRING = "xlsm";
	public static final String PRESENTATION_STRING = "pptx";
	public static final String MP4_STRING = "mp4";
	public static final String APPLICATION_MP4_STRING = "application/mp4";
	public static final String APPLICATION_SPREADSHEET = "application/xlsx";
	public static final String APPLICATION_DOCUMENT = "application/docx";
	public static final String APPLICATION_PRESENTATION = "application/pptx";
	public static final String APPLICATION_MS_EXCEL = "application/xlsm";

	@Reference
	private transient EndecaService endecaService;

	@Reference
	private transient EndecaConfig endecaConfigService;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private AdminService adminService;

	@Reference
	private EatonConfigService configService;

	@Reference
	private AuthorizationService authorizationService;

    @Reference
    private FacetURLBeanService facetURLBeanService;

    @Reference
	private Externalizer externalizer;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
		LOG.info("******** LoadMore Servlet execution started ***********");

		try {
			String loadMoreJsonString = request.getParameter("url");
			countString = request.getParameter("count");
			JSONParser parser = new JSONParser();
			JSONObject paramJson = new JSONObject();
			JSONArray valuesJSONarray = new JSONArray();
			paramJson = (JSONObject) parser.parse(loadMoreJsonString);

			if(paramJson.containsKey("dataAttribute")){
                valuesJSONarray = (JSONArray) paramJson.get("dataAttribute");

                for(int i=0;i<valuesJSONarray.size();i++){
                    JSONObject  innerJson = (JSONObject) valuesJSONarray.get(i);


                    if(innerJson.containsKey("currentLanguage")  && innerJson.get("currentLanguage") != null){
                        language = innerJson.get("currentLanguage").toString();
                    }else if(innerJson.containsKey("currentCountry") && innerJson.get("currentCountry") != null){
                        country = innerJson.get("currentCountry").toString();
                    }else if(innerJson.containsKey("pageType") && innerJson.get("pageType") != null){
                        pageType = innerJson.get("pageType").toString();
                    }else if(innerJson.containsKey("primarySubCategoryTag") && innerJson.get("primarySubCategoryTag") != null){
                        primarySubCategoryTag = innerJson.get("primarySubCategoryTag").toString();
                    }else if(innerJson.containsKey("pageSize") && innerJson.get("pageSize") != null){
                        pageSize = innerJson.get("pageSize").toString();
                    }else if(innerJson.containsKey("unitedStatesDateFormat") && innerJson.get("unitedStatesDateFormat") != null){
                    	unitedStatesDateFormat = innerJson.get("unitedStatesDateFormat").toString();
                    }else if(innerJson.containsKey("defaultSortOrder") && innerJson.get("defaultSortOrder") != null){
                        defaultSortOrder = innerJson.get("defaultSortOrder").toString();
                    }else if(innerJson.containsKey("currentLoadMore") && innerJson.get("currentLoadMore") != null){
                        currentLoadMore = innerJson.get("currentLoadMore").toString();
                    }else if(innerJson.containsKey("currentSortByOption") && innerJson.get("currentSortByOption") != null){
                        currentSortBy = innerJson.get("currentSortByOption").toString();
                    }else if(innerJson.containsKey("currentResourcePath") && innerJson.get("currentResourcePath") != null){
                        currentResourcePath = innerJson.get("currentResourcePath").toString();
                    }else if(innerJson.containsKey("pimPrimaryImage") && innerJson.get("pimPrimaryImage") != null){
                        pimPrimaryImage = innerJson.get("pimPrimaryImage").toString();
                    }else if(innerJson.containsKey("extensionId") && innerJson.get("extensionId") != null){
                        extensionId = innerJson.get("extensionId").toString();
                    }else if(innerJson.containsKey("selectors") && innerJson.get("selectors") != null){
                        selectors = innerJson.get("selectors").toString();
                    }else if(innerJson.containsKey("requestUri") && innerJson.get("requestUri") != null){
                        requestUri = innerJson.get("requestUri").toString();
                    }else if(innerJson.containsKey("skuCardAttr") && innerJson.get("skuCardAttr") != null){
                        skuCardAttr = innerJson.get("skuCardAttr").toString();
                    }else if(innerJson.containsKey("skuPageUrl") && innerJson.get("skuPageUrl") != null){
                        skuPageUrl = innerJson.get("skuPageUrl").toString();
                    }else if(innerJson.containsKey("skuFallBackImage") && innerJson.get("skuFallBackImage") != null){
                        skuFallBackImage = innerJson.get("skuFallBackImage").toString();
                    }else if(innerJson.containsKey("returnFacetsFor") && innerJson.get("returnFacetsFor") != null){
                        returnFacetsFor = innerJson.get("returnFacetsFor").toString();
                    }
                }
            }

			String pagePath = "";
			if (currentResourcePath != null) {
				final PageManager pageManager = request.getResource().getResourceResolver().adaptTo(PageManager.class);
				if (pageManager != null) {
					final Page page = pageManager.getContainingPage(currentResourcePath);

					if (page != null) {
						pagePath = page.getPath();
					} else {
						LOG.error("Current resource path of site search servlet had no containing page: " + request.getRequestURI());
					}
				} else {
					LOG.error("pageManager is null in site search servlet.");
				}
			} else {
				LOG.error("currentResourcePath is null in site search servlet.");
			}

			parseURL(request);
			EndecaServiceRequestBean endecaServiceRequestBean = populateEndecaServiceRequestBean(pagePath, request);
			// Populate Site Search Results
			if (pageType != null && (pageType.equals(CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE))) {
				boolean isUnitedStatesDateFormat = Boolean.parseBoolean(unitedStatesDateFormat);
                SiteSearchResponse sitesearchResponse = endecaService.getSearchResults(endecaServiceRequestBean, request, isUnitedStatesDateFormat);
                totalCount = sitesearchResponse.getPageResponse().getTotalCount();
                SiteSearch sitesearch = setSiteSearchResults(sitesearchResponse.getPageResponse(),skuPageUrl,skuFallBackImage,request.getResourceResolver());
                jsonResponse = convertSiteSearchBeanTOJSON(sitesearch);
            }

			int countInt = Integer.parseInt(countString);
			int pageSizeInt = Integer.parseInt(pageSize);
			countInt = countInt + pageSizeInt;
			int indexLast = jsonResponse.lastIndexOf("}");
			String subJson = jsonResponse.substring(0, indexLast);
			String buttonStatus = "active";
			if(countInt+pageSizeInt >= totalCount){
                buttonStatus = "inActive";
            }
			jsonResponse = subJson+",\"loadmoreButtonCount\":\""+countInt+"\",\"buttonStatus\":\""+buttonStatus+"\"}";
			response.getWriter().println(jsonResponse);
		} catch (ParseException e) {
			LOG.error("parse exception in site search servlet");
		}

	}

	public SiteSearch setSiteSearchResults(SiteSearchPageResponse siteSearchResponse, String skuPageUrl, String skuFallBackImage, ResourceResolver resourceResolver) {
		SiteSearch siteSearchBean = new SiteSearch();

		try {
			List<SiteSearchResultsList> siteSearchResultsLists = new ArrayList < >();
			siteSearchEndecaResults = siteSearchResponse.getSiteSearchResults();
			populateContentType(skuPageUrl, skuFallBackImage, resourceResolver);
			for (SiteSearchResultsBean siteSearchEndecaBean: siteSearchEndecaResults) {
				SiteSearchResultsList siteSearchResults = new SiteSearchResultsList();
				siteSearchResults.setContentType(siteSearchEndecaBean.getContentType());

				SiteSearchContentItem contentItem = new SiteSearchContentItem();

				contentItem.setArticleType(siteSearchEndecaBean.getContentType());
				contentItem.setDate(siteSearchEndecaBean.getPublishDate());
				contentItem.setDescription(siteSearchEndecaBean.getDescription());
				contentItem.setName(siteSearchEndecaBean.getTitle());
				contentItem.setDocumentSize(siteSearchEndecaBean.getFileSize());
				contentItem.setDocumentType(siteSearchEndecaBean.getFileType());
				contentItem.setStatus(siteSearchEndecaBean.getStatus());
				contentItem.setEatonECCN(siteSearchEndecaBean.getEatonECCN());
				contentItem.setEatonSHA(siteSearchEndecaBean.getEatonSHA());
				contentItem.setSecure(siteSearchEndecaBean.isSecure());

				Link link = new Link();
				if (siteSearchEndecaBean.getUrl() != null) {
					link.setUrl(siteSearchEndecaBean.getUrl());
				} else if (siteSearchEndecaBean.getContentType() == "sku") {
					link.setUrl("/content/eaton/language-masters/en-us/skuPage." + siteSearchEndecaBean.getTitle() + ".html");
				} else {
					link.setUrl("");
				}

				link.setText(siteSearchEndecaBean.getTitle());
				link.setLinkType(siteSearchEndecaBean.getLinkType());
				link.setCompleteUrl(siteSearchEndecaBean.getCompleteUrl());

				if (siteSearchEndecaBean.getUrlTarget() != null) {
					link.setTarget(siteSearchEndecaBean.getUrlTarget());
				}
				contentItem.setLink(link);

				contentItem.setSecondaryLinks(siteSearchEndecaBean.getSecondaryLinkList());

				Image image = new Image();
				if (siteSearchEndecaBean.getDsktopRendition() != null && !siteSearchEndecaBean.getDsktopRendition().equals("")) {
					image.setMobile(siteSearchEndecaBean.getMobileRendition());
					image.setTablet(siteSearchEndecaBean.getDsktopRendition());
					image.setDesktop(siteSearchEndecaBean.getDsktopRendition());
				} else {
					image.setMobile("");
					image.setTablet("");
					image.setDesktop("");
				}
				contentItem.setImage(image);

				siteSearchResults.setContentItem(contentItem);
				siteSearchResultsLists.add(siteSearchResults);

			}
			siteSearchBean.setResultsList(siteSearchResultsLists);

		} catch(Exception e) {
			LOG.error("Exception occured in setskuListLoadMoreValues()" + e);
		}

		return siteSearchBean;
	}

	private void populateContentType(String skuPageUrl, String skuFallBackImage, ResourceResolver resourceResolver) {
		for (int i = 0; i < siteSearchEndecaResults.size(); i++) {
			SiteSearchResultsBean siteSearchResultsBean = siteSearchEndecaResults.get(i);

			if ((siteSearchResultsBean.getContentType() != null) && ((siteSearchResultsBean.getContentType().equals("product")) || (siteSearchResultsBean.getContentType().equals("products")))) {
				if ((siteSearchResultsBean.getUrl() != null) && (!siteSearchResultsBean.getUrl().equals("")) && (siteSearchResultsBean.getUrl().indexOf("/catalog") != -1)) {
					siteSearchResultsBean.setContentType("family");

					if (siteSearchResultsBean.getUrl() != null) {
						siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
					}

					List<SecondaryLink> secondaryLinkList = new ArrayList <> ();

					int index = siteSearchResultsBean.getUrl().indexOf(".html");
					String contextURL = siteSearchResultsBean.getUrl().substring(0, index);

					SecondaryLink specificationsLink = new SecondaryLink();

					if ((siteSearchResultsBean.getMiddleTabName() != null) && (!siteSearchResultsBean.getMiddleTabName().equals(""))) {
						specificationsLink.setText(siteSearchResultsBean.getMiddleTabName());
						LOG.debug("SSH::specificationsLink Middle Tab Value" + specificationsLink.getText());
					} else {
						specificationsLink.setText("Models");
					}

					specificationsLink.setUrl(contextURL + "." + (specificationsLink.getText()).toLowerCase() + CommonConstants.HTML_EXTN);

					SecondaryLink resourceLink = new SecondaryLink();
					resourceLink.setText("Resources");
					resourceLink.setUrl(contextURL + ".resources.html");
					secondaryLinkList.add(specificationsLink);
					secondaryLinkList.add(resourceLink);

					siteSearchResultsBean.setSecondaryLinkList(secondaryLinkList);

					if (siteSearchResultsBean.getUrl() != null) {
						siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
						siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
					}

					siteSearchResultsBean.setDsktopRendition(siteSearchResultsBean.getImage());
					siteSearchResultsBean.setMobileRendition(siteSearchResultsBean.getImage());

					siteSearchEndecaResults.set(i, siteSearchResultsBean);

				} else if ((siteSearchResultsBean.getUrl() != null) && (!siteSearchResultsBean.getUrl().equals(""))) {
					siteSearchResultsBean.setContentType("others");
					if ((siteSearchResultsBean.getUrl() != null) && (!siteSearchResultsBean.getUrl().equals(""))) {
						if ((siteSearchResultsBean.getUrl().contains("http")) || (siteSearchResultsBean.getUrl().contains("https")) || (siteSearchResultsBean.getUrl().contains("www"))) {
							siteSearchResultsBean.setLinkType("external");
							siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());
						} else {
							siteSearchResultsBean.setLinkType("internal");

							if (siteSearchResultsBean.getUrl() != null) {
								siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
								siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
							}
						}
						siteSearchEndecaResults.set(i, siteSearchResultsBean);
					}
				} else {
					siteSearchResultsBean.setContentType("sku");
					List<SecondaryLink> secondaryLinkList = new ArrayList<> ();

					if (skuPageUrl == null) {
						skuPageUrl = "";
					}

					skuPageUrl = CommonUtil.dotHtmlLinkSKU(skuPageUrl, resourceResolver);
					String contextURL = skuPageUrl + "." + CommonUtil.encodeURLString(siteSearchResultsBean.getTitle());

					if (contextURL != null) {
						contextURL = CommonUtil.dotHtmlLink(contextURL, resourceResolver);
					}

					if (contextURL != null) {
						siteSearchResultsBean.setUrl(contextURL + ".html");
						siteSearchResultsBean.setCompleteUrl(contextURL + ".html");
					}

					SecondaryLink specificationsLink = new SecondaryLink();
					specificationsLink.setText("Specifications");
					specificationsLink.setUrl(contextURL + SPECIFICATION_HASH_URL);

					SecondaryLink resourceLink = new SecondaryLink();
					resourceLink.setText("Resources");
					resourceLink.setUrl(contextURL + RESOURCE_HASH_URL);
					secondaryLinkList.add(specificationsLink);
					secondaryLinkList.add(resourceLink);
					siteSearchResultsBean.setSecondaryLinkList(secondaryLinkList);

					if ((siteSearchResultsBean.getDsktopRendition() == null) || (siteSearchResultsBean.getDsktopRendition().equals(""))) {
						siteSearchResultsBean.setDsktopRendition(Mobile_SKU_Image);
					}

					if ((siteSearchResultsBean.getMobileRendition() == null) || (siteSearchResultsBean.getMobileRendition().equals(""))) {
						if ((skuFallBackImage != null) && (!skuFallBackImage.equals(""))) {
							siteSearchResultsBean.setMobileRendition(skuFallBackImage);
						}
					}

					siteSearchEndecaResults.set(i, siteSearchResultsBean);
				}
			} else if ((siteSearchResultsBean.getContentType() != null) && ((siteSearchResultsBean.getContentType().equals("article")) || ((siteSearchResultsBean.getContentType().equals("news-and-insights"))))) {
				if ((siteSearchResultsBean.getUrl() != null) && (!siteSearchResultsBean.getUrl().equals(""))) {
					if ((siteSearchResultsBean.getUrl().contains("http")) || (siteSearchResultsBean.getUrl().contains("https")) || (siteSearchResultsBean.getUrl().contains("www"))) {
						siteSearchResultsBean.setLinkType("external");
						siteSearchResultsBean.setUrlTarget("_blank");
						siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());

					} else {
						siteSearchResultsBean.setLinkType("internal");
						siteSearchResultsBean.setUrlTarget("");
						if (siteSearchResultsBean.getUrl() != null) {
							siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
							siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
						}
					}
					siteSearchEndecaResults.set(i, siteSearchResultsBean);
				}
			} else if ((siteSearchResultsBean.getContentType() != null) && ((siteSearchResultsBean.getContentType().equals("resources")))) {
				if ((siteSearchResultsBean.getUrl() != null) && (!siteSearchResultsBean.getUrl().equals(""))) {
					if ((siteSearchResultsBean.getUrl().contains("http")) || (siteSearchResultsBean.getUrl().contains("https")) || (siteSearchResultsBean.getUrl().contains("www"))) {
						siteSearchResultsBean.setLinkType("external");
						siteSearchResultsBean.setUrlTarget("_blank");
						siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());

					} else {
						siteSearchResultsBean.setLinkType("internal");
						siteSearchResultsBean.setUrlTarget("");
						if (siteSearchResultsBean.getUrl() != null) {
							siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
							siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
						}
					}

					String assetSize = StringUtils.EMPTY;
					String assetType = StringUtils.EMPTY;

					if ((siteSearchResultsBean.getFileSize() != null) && (!siteSearchResultsBean.getFileSize().equals(""))) {
						long fileSize = Long.parseLong(siteSearchResultsBean.getFileSize());
						long sizeOfAsset = (long)(fileSize / Math.pow(10, 6));
						String spaceUnit = "";
						if (sizeOfAsset < 1) {
							sizeOfAsset = (long)(fileSize / Math.pow(10, 3));
							spaceUnit = CommonConstants.KB;
							if (sizeOfAsset < 1) {
								spaceUnit = CommonConstants.B;
							}
						} else {
							spaceUnit = CommonConstants.MB;
						}

						assetSize = sizeOfAsset + spaceUnit;
					}

					if ((siteSearchResultsBean.getFileType() != null) && (!siteSearchResultsBean.getFileType().equals(""))) {
						if (siteSearchResultsBean.getFileType().equals("image/jpeg")) {
							siteSearchResultsBean.setFileType("jpg");
						} else if (siteSearchResultsBean.getFileType().equals("application/javascript")) {
							siteSearchResultsBean.setFileType("javascript");
						} else if (siteSearchResultsBean.getFileType().equals("text/html")) {
							siteSearchResultsBean.setFileType("html");
						} else if (siteSearchResultsBean.getFileType().equals("image/gif")) {
							siteSearchResultsBean.setFileType("gif");
						} else if (siteSearchResultsBean.getFileType().equals("application/xml")) {
							siteSearchResultsBean.setFileType("xml");
						} else if (siteSearchResultsBean.getFileType().equals("application/vnd.ms-powerpoint")) {
							siteSearchResultsBean.setFileType("ppt");
						} else if (siteSearchResultsBean.getFileType().equals("ppt")) {
							siteSearchResultsBean.setFileType("ppt");
						} else if (siteSearchResultsBean.getFileType().equals("application/vnd.ms-excel")) {
							siteSearchResultsBean.setFileType("xls");
						} else if (siteSearchResultsBean.getFileType().equals("application/pdf")) {
							siteSearchResultsBean.setFileType("pdf");
						} else if (siteSearchResultsBean.getFileType().equals(APPLICATION_DOCUMENT)) {
							siteSearchResultsBean.setFileType(DOCUMENT_STRING);
						} else if (siteSearchResultsBean.getFileType().equals(APPLICATION_PRESENTATION)) {
							siteSearchResultsBean.setFileType(PRESENTATION_STRING);
						} else if (siteSearchResultsBean.getFileType().equals(APPLICATION_SPREADSHEET)) {
							siteSearchResultsBean.setFileType(SPREADSHEET_STRING);
						} else if (siteSearchResultsBean.getFileType().equals(APPLICATION_MS_EXCEL)) {
							siteSearchResultsBean.setFileType(MS_EXCEL_STRING);
						} else if (siteSearchResultsBean.getFileType().equals(APPLICATION_MP4_STRING)) {
							siteSearchResultsBean.setFileType(MP4_STRING);
						}

						if (!assetSize.equals("")) {
							assetType = siteSearchResultsBean.getFileType() + CommonConstants.BLANK_SPACE;
						} else {
							assetType = siteSearchResultsBean.getFileType();
						}
					}

					siteSearchResultsBean.setFileSize(assetSize);
					siteSearchResultsBean.setFileType(assetType);

					siteSearchEndecaResults.set(i, siteSearchResultsBean);
				}
			} else if ((siteSearchResultsBean.getContentType() != null) && ((siteSearchResultsBean.getContentType().equals("services")))) {
				siteSearchResultsBean.setContentType("others");
				if ((siteSearchResultsBean.getUrl() != null) && (!siteSearchResultsBean.getUrl().equals(""))) {
					if ((siteSearchResultsBean.getUrl().contains("http")) || (siteSearchResultsBean.getUrl().contains("https")) || (siteSearchResultsBean.getUrl().contains("www"))) {
						siteSearchResultsBean.setLinkType("external");
						siteSearchResultsBean.setUrlTarget("_blank");
						siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());

					} else {
						siteSearchResultsBean.setLinkType("internal");
						siteSearchResultsBean.setUrlTarget("");
						if (siteSearchResultsBean.getUrl() != null) {
							siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
							siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
						}
					}
					siteSearchEndecaResults.set(i, siteSearchResultsBean);
				}
			} else {
				siteSearchResultsBean.setContentType("others");
				if ((siteSearchResultsBean.getUrl() != null) && (!siteSearchResultsBean.getUrl().equals(""))) {
					if ((siteSearchResultsBean.getUrl().contains("http")) || (siteSearchResultsBean.getUrl().contains("https")) || (siteSearchResultsBean.getUrl().contains("www"))) {
						siteSearchResultsBean.setLinkType("external");
						siteSearchResultsBean.setUrlTarget("_blank");
						siteSearchResultsBean.setCompleteUrl(siteSearchResultsBean.getUrl());

					} else {
						siteSearchResultsBean.setLinkType("internal");
						siteSearchResultsBean.setUrlTarget("");
						if (siteSearchResultsBean.getUrl() != null) {
							siteSearchResultsBean.setCompleteUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
							siteSearchResultsBean.setUrl(CommonUtil.dotHtmlLink(siteSearchResultsBean.getUrl(), resourceResolver));
						}
					}
					siteSearchEndecaResults.set(i, siteSearchResultsBean);
				}
			}
		}
	}

	public String convertSiteSearchBeanTOJSON(SiteSearch sitesearch) throws IOException {
		LOG.info("Entry into convertBeanTOJSON method");
		ObjectMapper mapper = new ObjectMapper();
		String jsonRequest;

		try {
			jsonRequest = mapper.writeValueAsString(sitesearch);

		} catch(JsonGenerationException e) {
			throw new JsonGenerationException(EndecaConstants.INVALID_ENTRY_MSG, e);
		} catch(JsonMappingException e) {
			throw new JsonMappingException(EndecaConstants.INVALID_ENTRY_MSG, e);
		}

		if (LOG.isDebugEnabled()) LOG.debug("JSON request :" + jsonRequest);

		LOG.info("Exit from convertBeanTOJSON method");

		return jsonRequest;
	}

	public EndecaServiceRequestBean populateEndecaServiceRequestBean(final String pagePath, SlingHttpServletRequest request) {
		EndecaServiceRequestBean endecaServiceRequestBean = new EndecaServiceRequestBean();
		if (endecaConfigService != null) {
			endecaConfigBean = endecaConfigService.getConfigServiceBean();
		}
		// Setting common attributes of Endeca Request Bean
		if (endecaConfigBean != null) {
			endecaServiceRequestBean.setSearchApplicationKey(endecaConfigBean.getEspAppKey());
		}

		endecaServiceRequestBean.setFunction(SEARCH);
		endecaServiceRequestBean.setLanguage(language);
		// Populated the EndecaRequestBean for sub-category page
		 if ((pageType != null) && pageType.equals(CommonConstants.PAGE_TYPE_SITE_SEARCH_PAGE)) {
			populateSiteSearchPageEndecaRequestBean(endecaServiceRequestBean, pagePath, request);
		}

		return endecaServiceRequestBean;
	}

	public void populateSiteSearchPageEndecaRequestBean(EndecaServiceRequestBean endecaServiceRequestBean,
														final String pagePath, SlingHttpServletRequest request) {

		String sortByOption = "";

		if(null != endecaConfigBean){
			endecaServiceRequestBean.setSearchApplication(endecaConfigBean.getSitesearchAppName());
		}

		if (pageSize != null) {

			endecaServiceRequestBean.setNumberOfRecordsToReturn(pageSize);
		}
		if (defaultSortOrder != null && !defaultSortOrder.equals("")){
			sortByOption = defaultSortOrder;
		}

		if((null != productGridSelectors) && (null != productGridSelectors.getSearchTerm())){
			endecaServiceRequestBean.setSearchTerms(productGridSelectors.getSearchTerm());
		}

		//TBU
		//endecaServiceRequestBean.setSearchTerms("eaton");

		List<FilterBean> filters = new ArrayList<>();
		FilterBean filterBean = new FilterBean();
		List<String> filterValues = new ArrayList<>();
		filters = new ArrayList<>();

		//Setting Country
		filterBean = new FilterBean();
		filterBean.setFilterName(COUNTRY_CONSTANT);
		filterValues = new ArrayList<>();
		if ((country != null) && (!country.equals(""))) {
			filterValues.add(country);
		}
		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);

		// EAT-4643 Secure Search
		endecaServiceRequestBean.setSearchApplication(SecureModule.SECURESEARCH.getAppId());
		filters.addAll(new SecureFilterBeanFactory()
				.createFilterBeans(authorizationService.getTokenFromSlingRequest(request), SecureModule.SECURESEARCH));

		//Setting Facets
		filterBean = new FilterBean();
		filterBean.setFilterName("Facets");
		filterValues = new ArrayList<>();

		if(productGridSelectors!=null){
			if((productGridSelectors.getFacets()!=null) && (productGridSelectors.getFacets().size()>0)){
				List<FacetBean> FacetList = productGridSelectors.getFacets();
				for (int i = 0; i < FacetList.size(); i++){
					FacetBean facetBean = FacetList.get(i);
					filterValues.add(facetBean.getFacetID());
				}
			}
		}

		String currentTab = "";
		if((productGridSelectors!=null) && (productGridSelectors.getSelectedTab()!=null)){
			currentTab = productGridSelectors.getSelectedTab();
			filterValues.add(currentTab);
		}


		if(filterValues.size()==0){
			filterValues.add("");
		}

		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);

		// Setting startingRecordNumber
		if(countString!=null && pageSize != null) {
		int countInt = Integer.parseInt(countString);
		int pageSizeInt = Integer.parseInt(pageSize);
		int startNum = countInt + pageSizeInt;

		String startNumString = Integer.toString(startNum);
			endecaServiceRequestBean.setStartingRecordNumber(startNumString);
		} else {
			endecaServiceRequestBean.setStartingRecordNumber("0");
		}

		//Setting ReturnFacets
		filterBean = new FilterBean();
		filterBean.setFilterName("ReturnFacetsFor");
		filterValues = new ArrayList<>();
		String[] returnFacetsForArray = null;
		if(returnFacetsFor != null){
			returnFacetsForArray = returnFacetsFor.split("\\{\\[\\(\\)\\]\\}");
		}

		if ((returnFacetsForArray != null) && (returnFacetsForArray.length != 0)) {
			for(int i = 0 ; i < returnFacetsForArray.length; i++){
				String decodedFilterValue = returnFacetsForArray[i].replaceAll("\\[\\]", "&amp;");
				if (StringUtils.isNotBlank(decodedFilterValue)) {
					filterValues.add(decodedFilterValue);
				}
			}
		}

		if(filterValues.size()==0){
			filterValues.add("");
		}

		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);

		//Setting SortBy
		filterBean = new FilterBean();
		filterBean.setFilterName("SortBy");
		filterValues = new ArrayList<>();
		if((productGridSelectors!=null)  && (productGridSelectors.getSortyByOption()!=null) && (!productGridSelectors.getSortyByOption().equals(""))) {
			filterValues.add(productGridSelectors.getSortyByOption());
		} else {
			filterValues.add(sortByOption);
		}
		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);

		// End of Loop

		//Setting Auto Correct
		filterBean = new FilterBean();
		filterBean.setFilterName("autoCorrect");
		filterValues = new ArrayList<>();
		//TBU
		filterValues.add("true");

		filterBean.setFilterValues(filterValues);
		filters.add(filterBean);
		final String applicationId = CommonUtil.getApplicationId(pagePath);
		if (applicationId.equals(CommonConstants.APPLICATION_ID_ECJV)) {
			filters.add(FilterUtil.getFilterBean(EndecaConstants.APPLICATION_ID, applicationId));
		}

		// Setting Facet List
		endecaServiceRequestBean.setFilters(filters);

	}

    public void parseURL(SlingHttpServletRequest request) {

        try {
            String[] selectorsArray = null;
            if (selectors != null) {
                selectorsArray = selectors.toString().split("\\|");
            }

            String contentPath = requestUri;
            String[] contentPathList = contentPath.split("\\.");
            productGridSelectors = new ProductGridSelectors();
            facetURLBean = new FacetURLBean();
            String basecontentPath = "";

            if ((contentPathList != null) && (contentPathList.length > 0)) {
                basecontentPath = contentPathList[0];
                facetURLBean.setContentPath(basecontentPath);

                if (selectorsArray != null && selectorsArray.length > 0) {
                    if ((selectorsArray[0] != null) && ((selectorsArray[0].startsWith("specifications")))) {
                        facetURLBean.setContentPath(contentPathList[0] + "." + selectorsArray[0]);
                        facetURLBean.setExtensionIdStartURL(
                            contentPathList[0] + "." + selectorsArray[0] + ".extensionId");
                    }
                }
            }

            if (null != externalizer) {
				currentResourcePath = CommonUtil.getMappedUrl(currentResourcePath,externalizer ,request);
			}

            FacetURLBeanServiceResponse facetURLBeanServiceResponse = facetURLBeanService.getFacetURLBeanResponse(
                selectorsArray, Integer.parseInt(pageSize), pageType, currentResourcePath);
            if (facetURLBeanServiceResponse != null) {
                facetURLBean = facetURLBeanServiceResponse.getFacetURLBean();
                productGridSelectors = facetURLBeanServiceResponse.getProductGridSelectors();
            }

        } catch (Exception e) {
            LOG.error("exception occured on parseUrl method of Load More Servlet" + e);
        }
    }


}
