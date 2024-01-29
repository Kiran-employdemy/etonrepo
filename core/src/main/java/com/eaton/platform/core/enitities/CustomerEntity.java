package com.eaton.platform.core.enitities;

import org.apache.poi.ss.formula.functions.T;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Value;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerEntity {
    private static final String FILE_NAME = "downloadFileName";
    private static final String EMAIL_RECIPIENTS = "emailToRecipients";
    private static final String DOWNLOAD_ASSET_PATHS = "downloadAssetPaths";

    public String downloadFileName;
    public String[] emailToRecipients;
    public String[] emailAssetPaths;

    public Map<String, Object> create() {
        final Map<String, Object> valueMap = new HashMap<>();
        update(valueMap);
        return valueMap;
    }

    public void update(final Map<String, Object> valueMap) {
        if (downloadFileName != valueMap.get(FILE_NAME)) valueMap.put(FILE_NAME, downloadFileName);
        if (emailToRecipients != valueMap.get(EMAIL_RECIPIENTS)) valueMap.put(EMAIL_RECIPIENTS, emailToRecipients);
        if (emailAssetPaths != valueMap.get(DOWNLOAD_ASSET_PATHS)) valueMap.put(DOWNLOAD_ASSET_PATHS, emailAssetPaths);
    }

    public void populate(final ValueMap valueMap) {
        downloadFileName = valueMap.get(FILE_NAME).toString();
        emailToRecipients = valueMap.get(EMAIL_RECIPIENTS, new String[]{});
        emailAssetPaths = valueMap.get(DOWNLOAD_ASSET_PATHS, new String[]{});
    }
}
