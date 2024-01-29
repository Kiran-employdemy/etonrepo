package com.eaton.platform.core.models.resourcelistpdh;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.bean.AttrNmBean;
import com.eaton.platform.core.bean.Document;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.ResourceListDetail;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.injectors.annotations.EatonSiteConfigInjector;
import com.eaton.platform.core.models.ExternalResourceModel;
import com.eaton.platform.core.models.ModelViewerCloudConfig;
import com.eaton.platform.core.models.PageDecorator;
import com.eaton.platform.core.models.SiteResourceSlingModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CloudConfigService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.services.resourcelistpdh.ResourceLanguageListService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.core.util.SkuImageParser;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Model to provide the PDH List Resource component with data
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ResourceListPDHModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceListPDHModel.class);
    private static final String CATALOG = "&catalog=";
    private static final String HIDE_PORTLETS_NAVIGATION = "&hidePortlets=navigation";
    private static final String PART = "?part=";


    @OSGiService
    protected AdminService adminService;

    @OSGiService
    protected EndecaRequestService endecaRequestService;

    @OSGiService
    protected EndecaService endecaService;

    @EatonSiteConfigInjector
    protected Optional<SiteResourceSlingModel> siteResourceSlingModel;

    @Inject
    protected SlingHttpServletRequest slingRequest;

    @Inject
    protected Page currentPage;

    @Inject
    protected Resource resource;

    @OSGiService
    protected ProductFamilyDetailService productFamilyDetailService;

    @OSGiService
    protected CloudConfigService configService;
    @OSGiService
    private ResourceLanguageListService resourceLanguageListService;

    private List<ResourceListDetail> resourceList;

    private List<ExternalResourceModel> externalResourceList = new ArrayList<ExternalResourceModel>();

    private String productName;

    private String disableDatasheet;

    @PostConstruct
    public void init() {
        if (siteResourceSlingModel.isPresent() && null != adminService) {
            final String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
            final EndecaServiceRequestBean endecaRequestBean = endecaRequestService.getEndecaRequestBean(currentPage,
                    selectors, StringUtils.EMPTY);
            final SKUDetailsResponseBean skuDetailsResponseBean = endecaService.getSKUDetails(endecaRequestBean);
            final SKUDetailsBean skuDetails = skuDetailsResponseBean.getSkuResponse().getSkuDetails();
            disableDatasheet = siteResourceSlingModel.get().getDisableDatasheet();
            setComponentValues(skuDetails, selectors);
        }
    }

    public java.util.Optional<ModelViewerCloudConfig.ModelViewer> getModelViewer(Optional<PageDecorator> pageDecorator, java.util.Optional<ModelViewerCloudConfig> threeDCloudConfig) {
        return threeDCloudConfig.isPresent() && pageDecorator.isPresent()
                ? threeDCloudConfig.get().getModelViewers().stream().filter(modelViewer -> modelViewer.getName().equals(pageDecorator.get().getModelViewer())).findFirst()
                : java.util.Optional.empty();
    }

    public void setComponentValues(SKUDetailsBean skuData, String[] selectors) {

        Optional<PageDecorator> pageDecorator = Optional.empty();
        java.util.Optional<ModelViewerCloudConfig.ModelViewer> modelViewerOptional = java.util.Optional.empty();

        //Changes for the 3D and 2D model.
        final ProductFamilyPIMDetails productFamilyPIMDetailsBean = productFamilyDetailService
                .getProductFamilyPIMDetailsBean(skuData, currentPage);
        final String productFamilyAEMPath = productFamilyPIMDetailsBean.getProductFamilyAEMPath();
        if ((null != configService) && (StringUtils.isNotEmpty(productFamilyAEMPath))) {
            final Resource familyResource = slingRequest.getResourceResolver().resolve(productFamilyAEMPath);
            final java.util.Optional<ModelViewerCloudConfig> threeDCloudConfig = configService
                    .getModelViewerCloudConfig(familyResource);
            pageDecorator = Optional
                    .ofNullable(familyResource.adaptTo(PageDecorator.class));
            modelViewerOptional = getModelViewer(pageDecorator,
                    threeDCloudConfig);
        }

        Map<String, List<AttrNmBean>> documentMap = new HashMap<>();
        if (skuData != null) {
            productName = skuData.getFamilyName();
            resourceList = new ArrayList<>();
            List<Document> documentList;


            //Resource List Population Code to be added post changes in site configuration.
            Map<String, List<String>> resourceCategoryList = resourceLanguageListService.getResourceLanguageList(currentPage.getLanguage());

            if ((skuData.getDocuments() != null) && (!skuData.getDocuments().equals(""))) {
                SkuImageParser documentXmlParser = new SkuImageParser();
                documentMap = documentXmlParser.getDocumentMap(skuData.getDocuments(), adminService);
            }

            if ((resourceCategoryList != null) && (!resourceCategoryList.isEmpty())) {

                for (Map.Entry<String, List<String>> entry : resourceCategoryList.entrySet()) {
                    // Get it from I18n
                    String resourceCategoryTitle = entry.getKey();
                    documentList = new ArrayList<>();
                    int counter = 0;
                    final List<String> documentLinks = new ArrayList<>();
                    ResourceListDetail resourceObject = new ResourceListDetail();
                    List<String> pdhGroupIDs = entry.getValue();
                    if (((pdhGroupIDs != null) && (!pdhGroupIDs.isEmpty())) && ((documentMap != null) && (documentMap.size() > 0))) {
                        for (int k = 0; k < pdhGroupIDs.size(); k++) {
                            String pdhGroupId = pdhGroupIDs.get(k);
                            List<AttrNmBean> pdhDocumentList = documentMap.get(pdhGroupId);
                            if (pdhDocumentList != null) {
                                for (int i = 0; i < pdhDocumentList.size(); i++) {
                                    AttrNmBean pdhDocument = pdhDocumentList.get(i);
                                    if (((pdhDocument != null) && (isDocumentEligible(pdhDocument))) && ((pdhDocument.getLabel() != null) && (!pdhDocument.getLabel().isEmpty()) &&
                                            ((pdhDocument.getCdata() != null) && (!pdhDocument.getCdata().isEmpty())))) {
                                        Document document = new Document();
                                        document.setDocumentId(pdhDocument.getId());
                                        document.setDocumentName(pdhDocument.getLabel());
                                        document.setDocumentLink(pdhDocument.getCdata());
                                        documentLinks.add(pdhDocument.getCdata());
                                        documentList.add(document);
                                        if (pdhDocument.getCdata().toLowerCase(Locale.getDefault()).contains(CommonConstants.CONTENT_DAM_NO_EXTRA_SLASH)) {
                                            counter++;
                                        }

                                    }
                                }
                            }
                        }

                        if (!documentList.isEmpty()) {
                            resourceObject.setDocumentList(documentList);
                            resourceObject.setDocumentLinks(documentLinks);
                            resourceObject.setCounter(counter);
                        }
                    }

                    if (!documentList.isEmpty()) {
                        if (modelViewerOptional.isPresent()) {
                            final String resourceGroup = modelViewerOptional.get().getResourceGroup();
                            boolean isGroupExist = pdhGroupIDs.stream().anyMatch(pdhID -> pdhID.equals(resourceGroup));
                            if ((pageDecorator.isPresent() && pageDecorator.get().isShow3dModel()) && isGroupExist) {
                                final String partSolutionURL = modelViewerOptional.get().getPartSolutionsUrl()
                                        .concat(PART)
                                        .concat(selectors[0])
                                        .concat(CATALOG)
                                        .concat(modelViewerOptional.get().getPartSolutionsCatalog())
                                        .concat(HIDE_PORTLETS_NAVIGATION);
                                resourceObject.setPartSolutionURL(partSolutionURL);
                            }
                        }
                        resourceObject.setResourceGroupName(resourceCategoryTitle);
                        resourceList.add(resourceObject);
                    }
                }
            }
        }
    }


    boolean isDocumentEligible(AttrNmBean pdhDocument) {
        if (currentPage == null) {
            LOGGER.debug("Current Page is null, returning false to skip the document.");
            return false;
        }
        if (!pdhDocument.toBeDisplayed()) {
            return false;
        }
        String pdhDocumentLanguage = pdhDocument.getLanguage();
        String pdhDocumentCountry = pdhDocument.getCountry();
        if (isLanguageOrCountryNull(pdhDocument, pdhDocumentLanguage, pdhDocumentCountry)) {
            return false;
        }
        List<String> documentLanguages = Arrays.asList(pdhDocumentLanguage.split(","));
        List<String> documentCountries = Arrays.asList(pdhDocumentCountry.split(","));
        String currentCountry = CommonUtil.getCountryFromPagePath(currentPage);
        Locale languageValue = currentPage.getLanguage(false);
        return shouldNotBeSkipped(documentLanguages, documentCountries, currentCountry, languageValue);
    }

    private static boolean isLanguageOrCountryNull(AttrNmBean pdhDocument, String pdhDocumentLanguage, String pdhDocumentCountry) {
        if (pdhDocumentLanguage == null) {
            LOGGER.debug("Language list of document {} is null, returning false to skip the document.", pdhDocument.getId());
            return true;
        }
        if (pdhDocumentCountry == null) {
            LOGGER.debug("Country list of document {} is null, returning false to skip the document.", pdhDocument.getId());
            return true;
        }
        return false;
    }

    private static boolean shouldNotBeSkipped(List<String> documentLanguages, List<String> documentCountries, String currentCountry, Locale languageValue) {
        if (documentLanguages.contains("Global") && (documentCountries.contains("Global") || documentCountries.contains(currentCountry))) {
            return true;
        }
        String currentLanguage = languageValue.toString();
        if (documentCountries.contains("Global") && (documentLanguages.contains("Global") || documentLanguages.contains(currentLanguage))) {
            return true;
        }
        return documentCountries.contains(currentCountry) && documentLanguages.contains(currentLanguage);
    }


    public List<ResourceListDetail> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<ResourceListDetail> resourceList) {
        this.resourceList = resourceList;
    }

    public List<ExternalResourceModel> getExternalResourceList() {
        return externalResourceList;
    }

    public String getProductName() {
        return productName;
    }

    public String getDisableDatasheet() {
        return disableDatasheet;
    }

    public void setDisableDatasheet(String disableDatasheet) {
        this.disableDatasheet = disableDatasheet;
    }
}