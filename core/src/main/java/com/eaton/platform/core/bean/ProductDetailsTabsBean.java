package com.eaton.platform.core.bean;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.integration.endeca.bean.skudetails.SKUDetailsBean;

import java.util.List;

public class ProductDetailsTabsBean {

    List<Tabsbean> tabslist;
    List<HowToBuyBean> howToBuyOptions;

    private String howToBuyLabel;
    private String howToBuyLink;
    private String howToBuytarget;
    private boolean isSuffixEnabled;
    private boolean isModalEnabled;
    private boolean isSourceTrackingEnabled;

    private String productName;
    private String eyebrowtitle;
    private String eyebrowLink;
    private String eyebrowtarget;

    //intraTab
    private String intraTabTitle;
    private String intraTabHref;
    private String intraTabTarget;

    //temperory
    private boolean graphicToggle;

    private boolean useDarktheme;

    private boolean showOverviewTab;
    private boolean showMiddleTab;
    private boolean showResourcesTab;
    private boolean noMatchFound = false;
    private boolean isQrCodeDisplay = Boolean.FALSE;
    private boolean qrErrorCode = Boolean.FALSE;
    private String qrSerialNumber;
    private String checkIconPath;
    private String serialAuthFlagPresent = CommonConstants.FALSE;
    private String repEmailFlag;
    private String qrValidationFlag = CommonConstants.FALSE;
    private String qrSerialNumberLink;

    private String externalMappedCurrentPagePath;

    private String modelCode;

    public static ProductDetailsTabsBean of(SKUDetailsBean skuDetailsBean) {
        ProductDetailsTabsBean productDetailsTabsBean = new ProductDetailsTabsBean();
        if (skuDetailsBean == null) {
            return productDetailsTabsBean;
        }
        productDetailsTabsBean.modelCode = skuDetailsBean.getModelCode();
        return productDetailsTabsBean;
    }

    /**
     * @return the useDarktheme
     */
    public boolean isUseDarktheme() {
        return useDarktheme;
    }

    /**
     * @param useDarktheme the useDarktheme to set
     */
    public void setUseDarktheme(boolean useDarktheme) {
        this.useDarktheme = useDarktheme;
    }

    /**
     * @return the intraTabTitle
     */
    public String getIntraTabTitle() {
        return intraTabTitle;
    }

    /**
     * @param intraTabTitle the intraTabTitle to set
     */
    public void setIntraTabTitle(String intraTabTitle) {
        this.intraTabTitle = intraTabTitle;
    }

    /**
     * @return the intraTabHref
     */
    public String getIntraTabHref() {
        return intraTabHref;
    }

    /**
     * @param intraTabHref the intraTabHref to set
     */
    public void setIntraTabHref(String intraTabHref) {
        this.intraTabHref = intraTabHref;
    }

    /**
     * @return the intraTabTarget
     */
    public String getIntraTabTarget() {
        return intraTabTarget;
    }

    /**
     * @param intraTabTarget the intraTabTarget to set
     */
    public void setIntraTabTarget(String intraTabTarget) {
        this.intraTabTarget = intraTabTarget;
    }

    /**
     * @return the graphicToggle
     */
    public boolean isGraphicToggle() {
        return graphicToggle;
    }

    /**
     * @param graphicToggle the graphicToggle to set
     */
    public void setGraphicToggle(boolean graphicToggle) {
        this.graphicToggle = graphicToggle;
    }

    public List<Tabsbean> getTabslist() {
        return tabslist;
    }

    public void setTabslist(List<Tabsbean> tabslist) {
        this.tabslist = tabslist;
    }

    public List<HowToBuyBean> getHowToBuyOptions() {
        return howToBuyOptions;
    }

    public void setHowToBuyOptions(List<HowToBuyBean> howToBuyOptions) {
        this.howToBuyOptions = howToBuyOptions;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getEyebrowtitle() {
        return eyebrowtitle;
    }

    public void setEyebrowtitle(String eyebrowtitle) {
        this.eyebrowtitle = eyebrowtitle;
    }

    public String getEyebrowLink() {
        return eyebrowLink;
    }

    public void setEyebrowLink(String eyebrowLink) {
        this.eyebrowLink = eyebrowLink;
    }

    public String getEyebrowtarget() {
        return eyebrowtarget;
    }

    public void setEyebrowtarget(String eyebrowtarget) {
        this.eyebrowtarget = eyebrowtarget;
    }

    public String getHowToBuyLabel() {
        return howToBuyLabel;
    }

    public void setHowToBuyLabel(String howToBuyLabel) {
        this.howToBuyLabel = howToBuyLabel;
    }

    public String getHowToBuyLink() {
        return howToBuyLink;
    }

    public void setHowToBuyLink(String howToBuyLink) {
        this.howToBuyLink = howToBuyLink;
    }

    public String getHowToBuytarget() {
        return howToBuytarget;
    }

    public void setHowToBuytarget(String howToBuytarget) {
        this.howToBuytarget = howToBuytarget;
    }

    public boolean isSuffixEnabled() {
        return isSuffixEnabled;
    }

    public void setSuffixEnabled(boolean suffixEnabled) {
        isSuffixEnabled = suffixEnabled;
    }

    public boolean isModalEnabled() {
        return isModalEnabled;
    }

    public void setModalEnabled(boolean modalEnabled) {
        isModalEnabled = modalEnabled;
    }

    public boolean isSourceTrackingEnabled() {
        return isSourceTrackingEnabled;
    }

    public void setSourceTrackingEnabled(boolean sourceTrackingEnabled) {
        isSourceTrackingEnabled = sourceTrackingEnabled;
    }

    public boolean isQrCodeDisplay() {
        return isQrCodeDisplay;
    }

    public void setQrCodeDisplay(boolean qrCodeDisplay) {
        isQrCodeDisplay = qrCodeDisplay;
    }

    public String getQrValidationFlag() {
        return qrValidationFlag;
    }

    public void setQrValidationFlag(String qrValidationFlag) {
        this.qrValidationFlag = qrValidationFlag;
    }

    /**
     * @return the showOverviewTab
     */
    public boolean isShowOverviewTab() {
        return showOverviewTab;
    }

    /**
     * @param showOverviewTab the showOverviewTab to set
     */
    public void setShowOverviewTab(boolean showOverviewTab) {
        this.showOverviewTab = showOverviewTab;
    }

    /**
     * @return the showMiddleTab
     */
    public boolean isShowMiddleTab() {
        return showMiddleTab;
    }

    /**
     * @param showMiddleTab the showMiddleTab to set
     */
    public void setShowMiddleTab(boolean showMiddleTab) {
        this.showMiddleTab = showMiddleTab;
    }

    /**
     * @return the showResourcesTab
     */
    public boolean isShowResourcesTab() {
        return showResourcesTab;
    }

    /**
     * @param showResourcesTab the showResourcesTab to set
     */
    public void setShowResourcesTab(boolean showResourcesTab) {
        this.showResourcesTab = showResourcesTab;
    }

    public boolean isNoMatchFound() {
        return noMatchFound;
    }

    public void setNoMatchFound(boolean noMatchFound) {
        this.noMatchFound = noMatchFound;
    }

    public boolean isQrErrorCode() {
        return qrErrorCode;
    }

    public void setQrErrorCode(boolean qrErrorCode) {
        this.qrErrorCode = qrErrorCode;
    }

    public String getQrSerialNumber() {
        return qrSerialNumber;
    }

    public void setQrSerialNumber(String qrSerialNumber) {
        this.qrSerialNumber = qrSerialNumber;
    }

    public String getQrSerialNumberLink() {
        return qrSerialNumberLink;
    }

    public void setQrSerialNumberLink(String qrSerialNumberLink) {
        this.qrSerialNumberLink = qrSerialNumberLink;
    }

    public String getCheckIconPath() {
        return checkIconPath;
    }

    public void setCheckIconPath(String checkIconPath) {
        this.checkIconPath = checkIconPath;
    }

    public String isSerialAuthFlagPresent() {
        return serialAuthFlagPresent;
    }

    public void setSerialAuthFlagPresent(String serialAuthFlagPresent) {
        this.serialAuthFlagPresent = serialAuthFlagPresent;
    }

    public String getRepEmailFlag() {
        return repEmailFlag;
    }

    public void setRepEmailFlag(String repEmailFlag) {
        this.repEmailFlag = repEmailFlag;
    }

    public String getExternalMappedCurrentPagePath() {
        return externalMappedCurrentPagePath;
    }

    public void setExternalMappedCurrentPagePath(String externalMappedCurrentPagePath) {
        this.externalMappedCurrentPagePath = externalMappedCurrentPagePath;
    }

    public String getModelCode() {
        return modelCode;
    }

}
