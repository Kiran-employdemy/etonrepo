package com.eaton.platform.core.servlets.assets;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO for using in parsing of json using Gson
 */
public class AssetZipDownloadInput {
    private List<String> documentLinks = new ArrayList<>();

    public List<String> getDocumentLinks() {
        return documentLinks;
    }
}
