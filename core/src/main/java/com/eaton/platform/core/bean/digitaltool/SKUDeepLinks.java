package com.eaton.platform.core.bean.digitaltool;

import java.io.Serializable;
import java.util.List;

public class SKUDeepLinks implements Serializable {
    private static final long serialVersionUID = 1L;
    private String locale;
    private String skuLink;
    private String downloadPDF;
    private String downloadXls;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getSkuLink() {
        return skuLink;
    }

    public void setSkuLink(String skuLink) {
        this.skuLink = skuLink;
    }

    public String getDownloadPDF() {
        return downloadPDF;
    }

    public void setDownloadPDF(String downloadPDF) {
        this.downloadPDF = downloadPDF;
    }

    public String getDownloadXls() {
        return downloadXls;
    }

    public void setDownloadXls(String downloadXls) {
        this.downloadXls = downloadXls;
    }

}