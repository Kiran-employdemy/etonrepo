package com.eaton.platform.core.servlets.productdatasheet;

import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static com.eaton.platform.core.constants.CommonConstants.HTML_EXTN;
import static com.eaton.platform.core.constants.CommonConstants.PDF_EXTN;

public class DataSheetDeepLinkList {
    private String locale;
    private String skuLink;
    private String downloadPDF;
    private String downloadXls;

    public static DataSheetDeepLinkList of(Locale localeObject, String baseUrl, String skuId) {
        DataSheetDeepLinkList dataSheetDeepLinkList = new DataSheetDeepLinkList();
        dataSheetDeepLinkList.locale = localeObject.getDisplayLanguage();
        String localizedSkuPageLink = baseUrl + String.format("/%s/%s/skuPage.%s", localeObject.getCountry().toLowerCase(Locale.getDefault())
                , localeObject.toLanguageTag().toLowerCase(Locale.getDefault()), URLEncoder.encode(skuId, StandardCharsets.UTF_8).replace("+", "%20").replace(".", "%2E").replace("%", "%25"));
        dataSheetDeepLinkList.skuLink = localizedSkuPageLink.concat(HTML_EXTN);
        dataSheetDeepLinkList.downloadPDF = localizedSkuPageLink.concat(PDF_EXTN);
        dataSheetDeepLinkList.downloadXls = localizedSkuPageLink.concat(HTML_EXTN);
        return dataSheetDeepLinkList;
    }
    public static DataSheetDeepLinkList empty(Locale localeObject) {
        DataSheetDeepLinkList dataSheetDeepLinkList = new DataSheetDeepLinkList();
        dataSheetDeepLinkList.locale = localeObject.getDisplayLanguage();
        dataSheetDeepLinkList.skuLink = StringUtils.EMPTY;
        dataSheetDeepLinkList.downloadPDF = StringUtils.EMPTY;
        dataSheetDeepLinkList.downloadXls = StringUtils.EMPTY;
        return dataSheetDeepLinkList;
    }
}
