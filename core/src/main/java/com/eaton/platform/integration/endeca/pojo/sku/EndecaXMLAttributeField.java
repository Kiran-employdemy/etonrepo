package com.eaton.platform.integration.endeca.pojo.sku;

import com.eaton.platform.core.bean.AttributeListDetail;
import com.eaton.platform.core.bean.attributetable.AttributeTable;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.models.ATRGRPTYPE;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.endeca.factories.AttributeListDetailFactory;
import com.eaton.platform.integration.endeca.pojo.base.AbstractEndecaXMLField;
import com.eaton.platform.integration.endeca.pojo.base.EndecaField;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EndecaXMLAttributeField extends AbstractEndecaXMLField<ATRGRPTYPE> {
    private final boolean withTableAttributes;
    private final AdminService adminService;
    private final Set<AttributeTable> attributeTables = new LinkedHashSet<>();

    private final AttributeListDetailFactory attributeListDetailFactory;

    public EndecaXMLAttributeField(AdminService adminService, EndecaField endecaField, String country, boolean withTableAttributes, Map<Long, SkuCIDContent> cidContentMap) {
        super(endecaField);
        this.adminService = adminService;
        this.withTableAttributes = withTableAttributes;
        this.attributeListDetailFactory = new AttributeListDetailFactory(withTableAttributes, cidContentMap, country);
    }

    @Override
    protected Class<ATRGRPTYPE> getClazz() {
        return ATRGRPTYPE.class;
    }

    public List<AttributeListDetail> withGroupName(String groupName) {
        ATRGRPTYPE attributeGroups = unmarshalledValue();
        AttributeListDetail attributeListDetail = attributeListDetailFactory.createFrom(attributeGroups);
        return createAttributeListDetailList(groupName, attributeListDetail);
    }

    public List<AttributeListDetail> withGroupNameAndOnlyFor(String groupName, List<String> groupNamesToRetain) {
        ATRGRPTYPE attributeGroups = unmarshalledValue();
        AttributeListDetail attributeListDetail = attributeListDetailFactory.createInOrderAndFiltered(groupNamesToRetain, attributeGroups);
        return createAttributeListDetailList(groupName, attributeListDetail);
    }

    private List<AttributeListDetail> createAttributeListDetailList(String groupName, AttributeListDetail attributeListDetail) {
        List<AttributeListDetail> attributeListDetails = new LinkedList<>();
        attributeListDetail.setAttributeGroupName(groupName);
        if (withTableAttributes) {
            attributeTables.addAll(attributeListDetail.filterOutAttributeTables());
        }
        if (attributeListDetail.getAttributeList().isEmpty()) {
            return attributeListDetails;
        }
        attributeListDetails.add(attributeListDetail);
        return attributeListDetails;
    }

    @Override
    protected InputStream getXsdInputStream() {
        return CommonUtil.getFileFromDAM(adminService, CommonConstants.DAM_XSD_FILEPATH, CommonConstants.IMG_XSD_FILENAME);
    }

    public Set<AttributeTable> getAttributeTables() {
        return attributeTables;
    }
}
