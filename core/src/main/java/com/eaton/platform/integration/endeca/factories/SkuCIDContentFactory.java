package com.eaton.platform.integration.endeca.factories;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ITM;
import com.eaton.platform.integration.endeca.pojo.sku.SkuCIDContent;
import com.eaton.platform.integration.endeca.pojo.sku.SkuCIDDoc;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SkuCIDContentFactory {

    private final SkuCIDDocFactory skuCIDDocFactory = new SkuCIDDocFactory();
    private final Locale locale;
    private final String country;

    public SkuCIDContentFactory(Locale locale, String country) {
        this.locale = locale;
        this.country = country;
    }

    public SkuCIDContent createFrom(ITM.ITMATRBT.CONTENT xmlContent) {
        List<SkuCIDDoc> skuCIDDocs = new LinkedList<>();
        xmlContent.getDOC().forEach((ITM.ITMATRBT.CONTENT.DOC xmlDoc) -> {
            if (isDocEligible(xmlDoc)) {
                skuCIDDocs.add(skuCIDDocFactory.createFrom(xmlDoc));
            }
        });
        return new SkuCIDContent(xmlContent.getCID(), skuCIDDocs);
    }

    private boolean isDocEligible(ITM.ITMATRBT.CONTENT.DOC xmlDoc) {
        if (locale != null) {
            final String currentLanguage = locale.toString();
            if (xmlDoc.getLanguage() != null) {
                List<String> languageList = Stream.of(xmlDoc.getLanguage().split(CommonConstants.COMMA)).collect(Collectors.toList());
                if (languageList.stream().noneMatch(language -> language.equalsIgnoreCase(CommonConstants.GLOBAL) || language.equalsIgnoreCase(currentLanguage))) {
                    return false;
                }
            }
            if (country != null) {
                List<String> countryList = Stream.of(xmlDoc.getCountry().split(CommonConstants.COMMA)).collect(Collectors.toList());
                return countryList.stream().anyMatch(countryFromList -> countryFromList.equalsIgnoreCase(CommonConstants.GLOBAL) || countryFromList.equalsIgnoreCase(country));
            }
        }
        return true;
    }
}
