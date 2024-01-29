package com.eaton.platform.integration.endeca.pojo.sku;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ITM;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.factories.SkuCIDContentFactory;
import com.eaton.platform.integration.endeca.pojo.base.AbstractEndecaXMLField;
import com.eaton.platform.integration.endeca.pojo.base.EndecaField;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EndecaXMLCidField extends AbstractEndecaXMLField<ITM> {
    private final AdminService adminService;

    private final SkuCIDContentFactory skuCIDDocFactory;

    public EndecaXMLCidField(AdminService adminService, EndecaField endecaField, Locale locale, String country) {
        super(endecaField);
        this.adminService = adminService;
        this.skuCIDDocFactory = new SkuCIDContentFactory(locale, country);
    }

    @Override
    protected Class<ITM> getClazz() {
        return ITM.class;
    }

    public Map<Long, SkuCIDContent> getSkuCidDocContentMap() {
        ITM skuItem = unmarshalledValue();
        Map<Long, SkuCIDContent> skuCidDocMap = new HashMap<>();
        if (skuItem == null) {
            return skuCidDocMap;
        }
        skuItem.getITMATRBT().getCONTENT().forEach(xmlContent -> {
            SkuCIDContent skuCIDContent = skuCIDDocFactory.createFrom(xmlContent);
            if(skuCIDContent.containsDocs()) {
                skuCidDocMap.put(skuCIDContent.getId(), skuCIDContent);
            }
        });
        return skuCidDocMap;
    }

    @Override
    protected InputStream getXsdInputStream() {
        return CommonUtil.getFileFromDAM(adminService, CommonConstants.DAM_XSD_FILEPATH, CommonConstants.CID_XSD_FILENAME);
    }
}
