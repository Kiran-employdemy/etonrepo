package com.eaton.platform.core.servlets;

public class AssetDetails {

    private String name;
    private String path;
    private String mimeType;
    private String componentType;
    private String assetId;
    private String country;
    private String language;
    private String searchTab;

    /**
     * @param name
     * @param path
     * @param mimeType
     */
    public AssetDetails(String name, String path, String mimeType,String componentType, String assetId,String country, String language, String searchTab) {
        this.name = name;
        this.path = path;
        this.mimeType = mimeType;
        this.componentType = componentType;
        this.assetId = assetId;
        this.country = country;
        this.language = language;
        this.searchTab = searchTab;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSearchTab() {
        return searchTab;
    }

    public void setSearchTab(String searchTab) {
        this.searchTab = searchTab;
    }
}