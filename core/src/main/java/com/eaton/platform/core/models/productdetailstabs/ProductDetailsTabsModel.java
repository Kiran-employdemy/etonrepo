package com.eaton.platform.core.models.productdetailstabs;


import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.bean.HowToBuyBean;
import com.eaton.platform.core.bean.ProductDetailsTabsBean;
import com.eaton.platform.core.bean.ProductFamilyPIMDetails;
import com.eaton.platform.core.bean.Tabsbean;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.SiteConfigModel;
import com.eaton.platform.core.models.eatonsiteconfig.EatonSiteConfigModel;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.EndecaRequestService;
import com.eaton.platform.core.services.ProductFamilyDetailService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsResponseBean;
import com.eaton.platform.integration.endeca.services.EndecaService;
import com.eaton.platform.integration.qr.constants.QRConstants;
import com.eaton.platform.integration.qr.services.AESService;
import com.eaton.platform.integration.qr.services.QRService;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Model(adaptables = {Resource.class, SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductDetailsTabsModel {
    public static final String CONTACT_ME_VALUE = "CONTACT ME";
    public static final String CONTACT_ME_KEY = "contactMe";
    private static final String LINKS_NAV = "links-navigation";
    private static final String WCMMODE = "wcmmode=";
    private static final String RUNMODE_AUTHOR = "author";
    private ProductDetailsTabsBean productDetailsTabsBean;
    private final List<Tabsbean> tabList = new ArrayList<>();
    Logger LOGGER = LoggerFactory.getLogger(ProductDetailsTabsModel.class);
    @OSGiService
    EndecaRequestService endecaRequestService;
    @OSGiService
    ProductFamilyDetailService productFamilyDetailService;
    @OSGiService
    AdminService adminService;
    @OSGiService
    AESService aESService;
    @OSGiService
    QRService qrService;
    private ProductFamilyPIMDetails productFamilyPIMDetails;
    private String catalogNumber = StringUtils.EMPTY;
    private String[] selectors;
    @Inject
    private SlingHttpServletRequest slingRequest;
    @Inject
    private SlingHttpServletResponse slingResponse;
    @Inject
    private Resource resource;
    @OSGiService
    private EndecaService endecaService;
    @Inject
    private Page currentPage;
    @Inject
    private PageManager pageManager;
    @Inject
    @Via("resource")
    @Named("view")
    private String pageType;
    @Inject
    @Via("resource")
    @Named("checkIconQrCodePath")
    private String checkIconPath;
    private String isSkuPage;
    @Inject
    private static SlingSettingsService slingSettingsService;
    public static final String MIDDLE_TAB_HASH = "#tab-2";

    @PostConstruct
    public void init() {
        boolean serialAuthFlag = Boolean.FALSE;
        boolean serialFlag = Boolean.FALSE;
        String repEmail = null;
        List<HowToBuyBean> howtobuyAuthenticate = new ArrayList<>();
        selectors = slingRequest.getRequestPathInfo().getSelectors();
        final EndecaServiceRequestBean endecaRequestBean = endecaRequestService.getEndecaRequestBean(currentPage, selectors, StringUtils.EMPTY);
        final EatonSiteConfigModel eatonSiteConfigModel = slingRequest.adaptTo(EatonSiteConfigModel.class);
        final SiteConfigModel siteConfiguration = eatonSiteConfigModel.getSiteConfig();
        final SKUDetailsResponseBean skuDetails = endecaService.getSKUDetails(endecaRequestBean);
        final SKUDetailsBean skuDetailsBean = skuDetails.getSkuResponse().getSkuDetails();
        productDetailsTabsBean = ProductDetailsTabsBean.of(skuDetailsBean);
        if (null != skuDetailsBean) {
            serialAuthFlag = skuDetailsBean.getSerialAuthFlag();
            serialFlag = skuDetailsBean.getSerialFlag();
            repEmail = skuDetailsBean.getRepEmail();
        }
        if (serialAuthFlag) {
            productDetailsTabsBean.setSerialAuthFlagPresent(CommonConstants.TRUE);
        }
        LOGGER.debug("serialAuthFlag for the sku id {} is {}", endecaRequestBean.getSearchTerms(), serialAuthFlag);
        LOGGER.debug("serialFlag for the sku id {} is {}", endecaRequestBean.getSearchTerms(), serialFlag);
        if((skuDetailsBean == null ||
                StringUtils.isEmpty(skuDetailsBean.getInventoryId()) ||
                StringUtils.isEmpty(skuDetailsBean.getExtensionId())) && !isAuthor()){

            LOGGER.debug("Missing Endeca Record, Inventory ID or ExtensionID for sku {} redirecting to 404" , endecaRequestBean.getSearchTerms());

            try {
                String redirectUrl = CommonUtil.getHomePagePath(currentPage).concat("/404.html");
                slingResponse.sendRedirect(redirectUrl);
            } catch (IOException e) {
                LOGGER.error("exception in redirect {}", e.getMessage());
            }
        }
        productFamilyPIMDetails = productFamilyDetailService.getProductFamilyPIMDetailsBean(skuDetailsBean, currentPage);

        if (selectors.length > 0) {

            catalogNumber = URLDecoder.decode(selectors[0], StandardCharsets.UTF_8);
            if (!catalogNumber.equals(CommonConstants.SPECIFICATIONS_TAB_SELECTOR) && !catalogNumber.equals(CommonConstants.RESOURCES_TAB_SELECTOR)) {
                productDetailsTabsBean.setProductName(catalogNumber);

            }
        }

        if ((slingRequest.getRequestPathInfo().getSelectors()).length != 0) {
            selectors = slingRequest.getRequestPathInfo().getSelectors();
        }
        if (null != currentPage) {
            ValueMap pageProperties = currentPage.getProperties();
            isSkuPage = pageProperties.get(CommonConstants.PAGE_TYPE, StringUtils.EMPTY);
        }

        if (StringUtils.equalsIgnoreCase(pageType, LINKS_NAV)) {
            productDetailsTabsBean.setGraphicToggle(true);
            productDetailsTabsBean.setUseDarktheme(true);
        } else {
            productDetailsTabsBean.setGraphicToggle(true);
            productDetailsTabsBean.setUseDarktheme(false);
        }

        // Setting Product Family Details
        setProductFamilyDetails(skuDetailsBean);

        // Setting How To Buy Details
        setHowToBuyDetails(skuDetailsBean, siteConfiguration, serialAuthFlag, howtobuyAuthenticate);

        // Setting Tab Details
        setTabDetails();

        //Call getQRSerialNumValidation for serial number validation from Url.
        final boolean qrSerialNumValidation = getQRSerialNumValidation(serialFlag, serialAuthFlag);

        // Setting QR Code Display
        setQrCodeDisplay(qrSerialNumValidation, serialFlag, serialAuthFlag, repEmail);

    }

    private boolean getQRSerialNumValidation(boolean serialFlag, boolean serialAuthFlag) {
        boolean qRSerialNumValidationFlag = Boolean.FALSE;
        //QR code: Decrypt serial num and eventId.
        final String serialNumber = slingRequest.getParameter(QRConstants.SERIAL_ID_CHARATER);
        final String eventId = slingRequest.getParameter(QRConstants.EVENT_ID_CHARACTER);
        String decryptSerialNumber = null;
        if (null != serialNumber && null != eventId) {
            productDetailsTabsBean.setQrValidationFlag(CommonConstants.TRUE);
            slingRequest.getSession().setAttribute(QRConstants.SERIAL_ID_CHARATER, serialNumber);
            slingRequest.getSession().setAttribute(QRConstants.EVENT_ID_CHARACTER, eventId);
        }
        if (null != serialNumber) {
            decryptSerialNumber = aESService.decrypt(serialNumber);
        }
        String decryptEventId = null;
        if (null != eventId) {
            decryptEventId = aESService.decrypt(eventId);
        }
        //QR code: Validate Serial num using SOA URL.
        qRSerialNumValidationFlag = validateQRSerialNumber(decryptSerialNumber, decryptEventId, serialNumber, eventId, serialFlag, serialAuthFlag);

        return qRSerialNumValidationFlag;
    }

    private boolean validateQRSerialNumber(String decryptSerialNumber, String decryptEventId, String serialNumber, String eventId, boolean serialFlag, boolean serialAuthFlag) {
        //QR code: Validate Serial num using SOA URL.
        boolean isValid = Boolean.FALSE;
        if (null != qrService && null != decryptSerialNumber && null != decryptEventId) {
            final Map<String, String> headerMap = CommonUtil.getRequestHeadersMap(slingRequest);
            final JsonObject qrCodeJsonObject = qrService.validateSerialNumber(decryptSerialNumber, decryptEventId, StringUtils.EMPTY, headerMap);
            if (null != qrCodeJsonObject && qrCodeJsonObject.has(QRConstants.ERROR_CODE)) {
                final String responseCode = qrCodeJsonObject.get(QRConstants.ERROR_CODE).getAsString();
                isValid = Boolean.TRUE;
                if (responseCode.equals(QRConstants.ERRCD00)) {
                    productDetailsTabsBean.setQrErrorCode(Boolean.TRUE);
                    productDetailsTabsBean.setQrSerialNumber(decryptSerialNumber);
                    productDetailsTabsBean.setCheckIconPath(checkIconPath);
                    String qrSerialNumberLink = currentPage.getPath().concat(".").concat(catalogNumber).concat("." + CommonConstants.RESOURCES_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN).concat("?").concat(QRConstants.SERIAL_ID_CHARATER).concat("=").concat(serialNumber).concat("&").concat(QRConstants.EVENT_ID_CHARACTER).concat("=").concat(eventId);
                    productDetailsTabsBean.setQrSerialNumberLink(qrSerialNumberLink);
                    if (null != productDetailsTabsBean.getHowToBuyOptions()) {
                        productDetailsTabsBean.getHowToBuyOptions().get(1).setLink(qrSerialNumberLink);
                    }
                } else {
                    productDetailsTabsBean.setNoMatchFound(Boolean.TRUE);
                }

                //Setting QR code display for resources tab
                setQrCodeDisplayForResourceTab(serialFlag, serialAuthFlag);
            }
        }
        return isValid;
    }

    private void setQrCodeDisplayForResourceTab(boolean serialFlag, boolean serialAuthFlag) {
        if (null != selectors && Arrays.asList(selectors).contains(CommonConstants.RESOURCES_TAB_SELECTOR)) {
            if (serialFlag) {
                if (serialAuthFlag) {
                    productDetailsTabsBean.setQrCodeDisplay(Boolean.TRUE);
                } else {
                    productDetailsTabsBean.setQrCodeDisplay(Boolean.FALSE);
                }
            } else {
                productDetailsTabsBean.setQrCodeDisplay(Boolean.FALSE);
            }
        }
    }

    private boolean validlink(String productFamilyAEMPath) {
        Page aemFamilyPage = pageManager.getPage(productFamilyAEMPath);
        return aemFamilyPage != null;
    }

    private void setTabsValue(String overviewTabURL, String specificationsTabURL, String resourcesTabURL, String productsFamilyPageURL) {

        Tabsbean overviewTab = new Tabsbean();
        overviewTab.setIcons(CommonConstants.OVERVIEW_TAB_ICON);
        overviewTab.setTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.OVERVIEW_TAB, CommonConstants.OVERVIEW_TAB));
        overviewTab.setHref(overviewTabURL);
        overviewTab.setSelected(false);


        Tabsbean sepcificationsTab = new Tabsbean();
        sepcificationsTab.setIcons(CommonConstants.SPECIFICATIONS_TAB_ICON);
        sepcificationsTab.setTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.SPECIFICATIONS_TAB, CommonConstants.SPECIFICATIONS_TAB));
        sepcificationsTab.setHref(specificationsTabURL);
        sepcificationsTab.setSelected(false);


        Tabsbean resourcesTab = new Tabsbean();
        resourcesTab.setIcons(CommonConstants.RESOURCES_TAB_ICON);
        resourcesTab.setTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.RESOURCES_TAB, CommonConstants.RESOURCES_TAB));
        resourcesTab.setHref(resourcesTabURL);
        resourcesTab.setSelected(false);

        if ((selectors != null) && (selectors.length != 0)) {

            String selectorItem = selectors[selectors.length - 1];
            switch (selectorItem) {

                case "specifications":
                    sepcificationsTab.setSelected(true);
                    productDetailsTabsBean.setIntraTabTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.RESOURCES_TAB, CommonConstants.RESOURCES_TAB));
                    productDetailsTabsBean.setIntraTabHref(resourcesTabURL);
                    productDetailsTabsBean.setIntraTabTarget(CommonConstants.TARGET_SELF);
                    productDetailsTabsBean.setShowMiddleTab(true);

                    break;
                case "resources":
                    resourcesTab.setSelected(true);
                    productDetailsTabsBean.setIntraTabTitle((CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.VIEW_ADDITIONAL_RESOURCES, CommonConstants.VIEW_ADDITIONAL_RESOURCES_DEFAULT)));
                    productDetailsTabsBean.setIntraTabHref(productsFamilyPageURL);
                    productDetailsTabsBean.setIntraTabTarget(CommonConstants.TARGET_SELF);
                    productDetailsTabsBean.setShowResourcesTab(true);
                    break;
                default:
                    overviewTab.setSelected(true);
                    productDetailsTabsBean.setIntraTabTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.SPECIFICATIONS_TAB, CommonConstants.SPECIFICATIONS_TAB));
                    productDetailsTabsBean.setIntraTabHref(specificationsTabURL);
                    productDetailsTabsBean.setIntraTabTarget(CommonConstants.TARGET_SELF);
                    productDetailsTabsBean.setShowOverviewTab(true);
                    break;
            }

        } else {
            overviewTab.setSelected(true);
            productDetailsTabsBean.setIntraTabTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.SPECIFICATIONS_TAB, CommonConstants.SPECIFICATIONS_TAB));
            productDetailsTabsBean.setIntraTabHref(specificationsTabURL);
            productDetailsTabsBean.setIntraTabTarget(CommonConstants.TARGET_SELF);
            productDetailsTabsBean.setShowOverviewTab(true);
        }
        tabList.add(overviewTab);
        tabList.add(sepcificationsTab);
        tabList.add(resourcesTab);

        productDetailsTabsBean.setTabslist(tabList);
    }

    /**
     * @return the productDetailsTabsBean
     */
    public ProductDetailsTabsBean getProductDetailsTabsBean() {
        return productDetailsTabsBean;
    }

    private void setProductFamilyDetails(SKUDetailsBean skuDetailsBean) {
        // Setting Product Details
        if (skuDetailsBean != null && productFamilyPIMDetails != null) {
            if (productFamilyPIMDetails.getProductName() != null && !(productFamilyPIMDetails.getProductName().equals(""))) {
                productDetailsTabsBean.setEyebrowtitle(productFamilyPIMDetails.getProductName());
            }
            setEyeBrowLinks();
            //Setting how to buy title and links
            setHowToBuyTitleAndLinks(skuDetailsBean);
        }
    }

    private void setEyeBrowLinks() {
        if ((productFamilyPIMDetails.getProductFamilyAEMPath() != null) && (!productFamilyPIMDetails.getProductFamilyAEMPath().equals(""))) {
            if (validlink(productFamilyPIMDetails.getProductFamilyAEMPath())) {
                String eyebrowLinkPath = productFamilyPIMDetails.getProductFamilyAEMPath();
                productDetailsTabsBean.setEyebrowLink(CommonUtil.dotHtmlLink(eyebrowLinkPath).concat(MIDDLE_TAB_HASH));
            }
        } else {
            productDetailsTabsBean.setEyebrowLink(StringUtils.EMPTY);
        }
    }

    private void setHowToBuyTitleAndLinks(SKUDetailsBean skuDetailsBean) {
        if ((productFamilyPIMDetails.getHowToBuyList() != null) && (!productFamilyPIMDetails.getHowToBuyList().isEmpty())) {
            List<HowToBuyBean> howToBuylist = productFamilyPIMDetails.getHowToBuyList();
            if (howToBuylist.size() == 1 && skuDetailsBean.getStatus() != null && !skuDetailsBean.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE)) {
                HowToBuyBean howToBuyBean = howToBuylist.get(0);
                setHowToBuyBeanProperties(howToBuyBean);
            } else {
                String howtoBuyLabel;
                if ((CommonConstants.INACTIVE.equalsIgnoreCase(skuDetailsBean.getStatus())) || CommonConstants.DISCONTINUED.equalsIgnoreCase(skuDetailsBean.getStatus())) {
                    howtoBuyLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.SUPPORT_LABEL, CommonConstants.SUPPORT_LABEL);
                } else {
                    howtoBuyLabel = CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.HOW_TO_BUY_LABEL, CommonConstants.HOW_TO_BUY_LABEL_DEFAULT);
                }
                productDetailsTabsBean.setHowToBuyLink(null);
                productDetailsTabsBean.setHowToBuyLabel(howtoBuyLabel);
                productDetailsTabsBean.setHowToBuytarget(null);
                productDetailsTabsBean.setHowToBuyOptions(CommonUtil.replaceTokenForHowToBuyOptions(productFamilyPIMDetails.getHowToBuyList(), isSkuPage, selectors[0]));
            }
            if ((productFamilyPIMDetails.getProductFamilyAEMPath() != null)) {
                productDetailsTabsBean.setExternalMappedCurrentPagePath(CommonUtil.removeSiteContentRootPathPrefix(productFamilyPIMDetails.getProductFamilyAEMPath()));
            }
        }
    }

    private void setHowToBuyBeanProperties(HowToBuyBean howToBuyBean) {
        if (howToBuyBean != null) {
            if (howToBuyBean.getTitle() != null) {
                productDetailsTabsBean.setHowToBuyLabel(howToBuyBean.getTitle());
            }
            if (howToBuyBean.getLink() != null) {
                productDetailsTabsBean.setHowToBuyLink(howToBuyBean.getLink());
            }
            if (howToBuyBean.getOpenInNewWindow() != null) {
                productDetailsTabsBean.setHowToBuytarget(howToBuyBean.getOpenInNewWindow());
            }

            productDetailsTabsBean.setModalEnabled(howToBuyBean.isModalEnabled());
            productDetailsTabsBean.setSuffixEnabled(howToBuyBean.isSuffixEnabled());
            productDetailsTabsBean.setSourceTrackingEnabled(howToBuyBean.isSourceTrackingEnabled());
            productDetailsTabsBean.setHowToBuyOptions(null);
        }
    }

    private void setHowToBuyDetails(SKUDetailsBean skuDetailsBean, SiteConfigModel siteConfiguration, boolean serialAuthFlag, List<HowToBuyBean> howtobuyAuthenticate) {
        if (skuDetailsBean != null) {
            if ((productDetailsTabsBean.getEyebrowtitle() == null) && (skuDetailsBean.getFamilyName() != null)) {
                String productName = skuDetailsBean.getFamilyName();
                productDetailsTabsBean.setEyebrowtitle(productName);
                /*Set how to buy as Support for discontinued SKU page else set contact me for orphan SKU page*/
                setHowToBuySupportDiscontinuedSku(siteConfiguration, skuDetailsBean, serialAuthFlag, howtobuyAuthenticate);
            }
        } else {
            productDetailsTabsBean.setNoMatchFound(true);
        }
        if ((siteConfiguration != null) && (productDetailsTabsBean.getHowToBuyLabel() == null)) {
            if (skuDetailsBean != null && skuDetailsBean.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE)) {
                productDetailsTabsBean.setHowToBuyLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.SUPPORT_LABEL, CommonConstants.SUPPORT_LABEL));
            } else {
                productDetailsTabsBean.setHowToBuyLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.HOW_TO_BUY_LABEL, CommonConstants.HOW_TO_BUY_LABEL_DEFAULT));
            }
            if (siteConfiguration.getDefaultLinkHTB() != null) {
                productDetailsTabsBean.setHowToBuyLink(siteConfiguration.getDefaultLinkHTB());
            }
            productDetailsTabsBean.setHowToBuytarget(CommonConstants.TARGET_SELF);
            productDetailsTabsBean.setHowToBuyOptions(null);
        }
    }

    private void setHowToBuySupportDiscontinuedSku(SiteConfigModel siteConfiguration, SKUDetailsBean skuDetailsBean, boolean serialAuthFlag, List<HowToBuyBean> howtobuyAuthenticate) {
        /*Set how to buy as Support for discontinued SKU page else set contact me for orphan SKU page*/
        if ((skuDetailsBean.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE)) || (skuDetailsBean.getStatus().equalsIgnoreCase(CommonConstants.DISCONTINUED))) {
            productDetailsTabsBean.setHowToBuyLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CommonConstants.SUPPORT_LABEL, CommonConstants.SUPPORT_LABEL));
            if (null != siteConfiguration && siteConfiguration.getDefaultLinkHTB() != null) {
                productDetailsTabsBean.setHowToBuyLink(siteConfiguration.getDefaultLinkHTB());
            }
        } else if (null == skuDetailsBean.getExtensionId() || skuDetailsBean.getExtensionId().isEmpty() || null == skuDetailsBean.getInventoryId() || skuDetailsBean.getInventoryId().isEmpty()) {
            productDetailsTabsBean.setHowToBuyLabel(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CONTACT_ME_KEY, CONTACT_ME_VALUE));
            if (null != siteConfiguration && siteConfiguration.getDefaultLinkHTB() != null) {
                productDetailsTabsBean.setHowToBuyLink(siteConfiguration.getDefaultLinkHTB());
            }
            if (serialAuthFlag) {
                //Setting how to buy Auth
                setHowToBuyAuth(howtobuyAuthenticate, siteConfiguration);
            }
        }
    }

    private void setHowToBuyAuth(List<HowToBuyBean> howtobuyAuthenticate, SiteConfigModel siteConfiguration) {
        HowToBuyBean howToBuyBeanAuth = new HowToBuyBean();
        if (null != siteConfiguration && siteConfiguration.getDefaultLinkHTB() != null) {
            howToBuyBeanAuth.setLink(siteConfiguration.getDefaultLinkHTB());
            howToBuyBeanAuth.setTitle(CommonUtil.getI18NFromResourceBundle(slingRequest, currentPage, CONTACT_ME_KEY, CONTACT_ME_VALUE));
            howtobuyAuthenticate.add(howToBuyBeanAuth);
        }
        HowToBuyBean howToBuyBeanAuth1 = new HowToBuyBean();
        howToBuyBeanAuth1.setTitle(QRConstants.AUTHENTICATE_LABEL_DEFAULT);
        howToBuyBeanAuth1.setDropdownIcon(QRConstants.AUTHENTICATE_PRODUCT_ICON);
        howtobuyAuthenticate.add(howToBuyBeanAuth1);
        productDetailsTabsBean.setHowToBuyOptions(howtobuyAuthenticate);

    }

    private void setTabDetails() {
        //Setting Tab Details
        String currentPagePath = currentPage.getPath();
        String overviewTabURL = currentPagePath.concat(CommonConstants.HTML_EXTN);
        String specificationsTabURL = currentPagePath.concat("." + CommonConstants.SPECIFICATIONS_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN);
        String resourcesTabURL = currentPagePath.concat("." + CommonConstants.RESOURCES_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN);

        String productsFamilyAEMUrl = null;
        if (null != productFamilyPIMDetails) {
            productsFamilyAEMUrl = productFamilyPIMDetails.getProductFamilyAEMPath();
        }

        String productsFamilyPageURL = StringUtils.EMPTY;
        String parameter = slingRequest.getParameter("wcmmode");

        if (catalogNumber != null && !(catalogNumber.isEmpty())) {
            catalogNumber = CommonUtil.encodeURLString(catalogNumber);
            if (CommonUtil.isBlankOrNull(parameter)) {
                overviewTabURL = currentPagePath.concat(".").concat(catalogNumber).concat(CommonConstants.HTML_EXTN);
                specificationsTabURL = currentPagePath.concat(".").concat(catalogNumber).concat("." + CommonConstants.SPECIFICATIONS_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN);
                resourcesTabURL = currentPagePath.concat(".").concat(catalogNumber).concat("." + CommonConstants.RESOURCES_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN);
            } else if (parameter != null) {
                overviewTabURL = currentPagePath.concat(".").concat(catalogNumber).concat(CommonConstants.HTML_EXTN).concat("?").concat(WCMMODE + parameter);
                specificationsTabURL = currentPagePath.concat(".").concat(catalogNumber).concat("." + CommonConstants.SPECIFICATIONS_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN).concat("?").concat(WCMMODE + parameter);
                resourcesTabURL = currentPagePath.concat(".").concat(catalogNumber).concat("." + CommonConstants.RESOURCES_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN).concat("?").concat(WCMMODE + parameter);
            }
        } else if (CommonUtil.isBlankOrNull(catalogNumber) && parameter != null) {
            overviewTabURL = currentPagePath.concat(CommonConstants.HTML_EXTN).concat("?").concat(WCMMODE + parameter);
            specificationsTabURL = currentPagePath.concat("." + CommonConstants.SPECIFICATIONS_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN).concat("?").concat(WCMMODE + parameter);
            resourcesTabURL = currentPagePath.concat("." + CommonConstants.RESOURCES_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN).concat("?").concat(WCMMODE + parameter);

        }

        if (null != productsFamilyAEMUrl) {
            productsFamilyPageURL = productsFamilyAEMUrl.concat("." + CommonConstants.RESOURCES_TAB_SELECTOR).concat(CommonConstants.HTML_EXTN);
        }

        if (null != slingRequest.getParameter(QRConstants.SERIAL_ID_CHARATER) && null != slingRequest.getParameter(QRConstants.EVENT_ID_CHARACTER)) {
            final String serialNumber = slingRequest.getParameter(QRConstants.SERIAL_ID_CHARATER);
            final String eventId = slingRequest.getParameter(QRConstants.EVENT_ID_CHARACTER);
            if (null != serialNumber && null != eventId) {
                overviewTabURL = overviewTabURL.concat("?").concat(QRConstants.SERIAL_ID_CHARATER).concat("=").concat(serialNumber).concat("&").concat(QRConstants.EVENT_ID_CHARACTER).concat("=").concat(eventId);
                resourcesTabURL = resourcesTabURL.concat("?").concat(QRConstants.SERIAL_ID_CHARATER).concat("=").concat(serialNumber).concat("&").concat(QRConstants.EVENT_ID_CHARACTER).concat("=").concat(eventId);

            }
        }
        setTabsValue(overviewTabURL, specificationsTabURL, resourcesTabURL, productsFamilyPageURL);
    }

    private void setQrCodeDisplay(boolean qrSerialNumValidation, boolean serialFlag, boolean serialAuthFlag, String repEmail) {
        if (!qrSerialNumValidation && productDetailsTabsBean.getQrValidationFlag().equalsIgnoreCase(CommonConstants.TRUE)) {
            try {

                String redirectUrl = CommonUtil.getHomePagePath(currentPage).concat("/404.html");
                slingResponse.sendRedirect(redirectUrl);

            } catch (IOException e) {
                LOGGER.error("exception in redirect {}", e.getMessage());
            }
        }
        //To ensure qr manual flow displays only on resources tab
        if (null != selectors && !qrSerialNumValidation) {
            //Check serialAuthFlag, serialFlag getting from endeca to display QR code.
            if (serialFlag) {
                if (serialAuthFlag) productDetailsTabsBean.setQrCodeDisplay(Boolean.TRUE);
                else productDetailsTabsBean.setQrCodeDisplay(Boolean.FALSE);
            } else productDetailsTabsBean.setQrCodeDisplay(Boolean.FALSE);
        }
          /* EAT-7371 - In the event that QR code validation fails, display the representative's
        email for reporting purposes. */
        productDetailsTabsBean.setRepEmailFlag(repEmail);
    }

    private boolean isAuthor() {
        return slingSettingsService.getRunModes().contains(RUNMODE_AUTHOR);
    }

}