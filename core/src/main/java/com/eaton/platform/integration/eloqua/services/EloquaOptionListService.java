package com.eaton.platform.integration.eloqua.services;

import com.eaton.platform.core.models.EloquaCloudConfigModel;

public interface EloquaOptionListService {
    public String getOptionList(EloquaCloudConfigModel eloquaCloudConfigModel, final String optionId);
    public String getCountryFromDAMOptionList();
}
