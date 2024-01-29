package com.eaton.platform.core.services;

import com.day.cq.wcm.api.Page;
import com.eaton.platform.core.models.EndecaConfigModel;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.HashMap;
import java.util.Optional;

public interface TxnmyAttributeDropDownService {
    public HashMap<String,String> constructDropdownOptions(final ResourceResolver resourceResolver, final Page currentPage);
    public HashMap<String,String> getAttributesFromPimResource(final ResourceResolver resourceResolver, final String pimPath, Optional<EndecaConfigModel> endecaConfigModel);
}
