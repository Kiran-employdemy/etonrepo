package com.eaton.platform.core.models.resourcelist;

import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

@Model(adaptables = {
        Resource.class,
}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SecureAssetModel {
    @ValueMapValue
    private List<String> productCategories;
    @ValueMapValue
    private List<String> accountType;
    @ValueMapValue
    private List<String> applicationAccess;
    @ValueMapValue(name = "xmp:eaton-partner-program-and-tier-level")
    private List<String> partnerProgramAndTierLevel;
    @ValueMapValue
    private String secureAsset;


    public boolean validateAccountTypeTags(List<String> listOfTags) {
        return validateListOfTags(accountType, listOfTags);
    }

    public boolean validateApplicationAccessTags(List<String> listOfTags) {
        return validateListOfTags(applicationAccess, listOfTags);
    }

    private boolean validateListOfTags(List<String> applicationAccess, List<String> listOfTags) {
        if (CollectionUtils.isNotEmpty(applicationAccess) && CollectionUtils.isEmpty(listOfTags)) {
            return false;
        }
        if (CollectionUtils.isEmpty(applicationAccess)) {
            return true;
        }
        return applicationAccess.stream().anyMatch(listOfTags::contains);
    }

    public boolean validateProductCategoriesTags(List<String> listOfTags) {
        return validateListOfTags(productCategories, listOfTags);
    }

    public boolean validatePartnerProgramAndTierLevelTags(List<String> listOfTags) {
        return validateListOfTags(partnerProgramAndTierLevel, listOfTags);
    }

    public boolean isSecured() {
        return CommonConstants.YES.equalsIgnoreCase(secureAsset);
    }
}
