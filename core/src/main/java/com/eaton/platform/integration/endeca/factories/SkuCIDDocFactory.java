package com.eaton.platform.integration.endeca.factories;

import com.eaton.platform.core.models.ITM;
import com.eaton.platform.integration.endeca.enums.CIDDocType;
import com.eaton.platform.integration.endeca.pojo.sku.SkuCIDDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class SkuCIDDocFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkuCIDDocFactory.class);
    public SkuCIDDoc createFrom(ITM.ITMATRBT.CONTENT.DOC doc) {
        SkuCIDDoc skuCIDDoc = new SkuCIDDoc();
        skuCIDDoc.setId(doc.getId());
        skuCIDDoc.setType(getType(doc));
        skuCIDDoc.setLanguage(doc.getLanguage());
        skuCIDDoc.setCountry(doc.getCountry());
        skuCIDDoc.setValue(doc.getValue());
        if(doc.getPriority() != null) {
            skuCIDDoc.setPriority(doc.getPriority().toString());
        }
        return skuCIDDoc;
    }

    private static CIDDocType getType(ITM.ITMATRBT.CONTENT.DOC doc) {
        try {
            return CIDDocType.valueOf(doc.getType().toUpperCase(Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            LOGGER.debug("the attribute type is not know, returning other");
            return CIDDocType.OTHER;
        }
    }
}
