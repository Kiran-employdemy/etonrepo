package com.eaton.platform.core.bean.secure;

public class SecureAssetsBean {

    private boolean isSecureAsset;

    private String assetTitle;

    private String assetPublicationDate;

    private String assetDescription;

    private String assetLastModifiedDate;

    private String assetSize;

    private String assetPath;

    public boolean isSecureAsset() {
        return isSecureAsset;
    }

    public void setSecureAsset(final boolean secureAsset) {
        isSecureAsset = secureAsset;
    }

    public String getAssetTitle() {
        return assetTitle;
    }

    public String getAssetPublicationDate() {
        return assetPublicationDate;
    }

    public String getAssetDescription() {
        return assetDescription;
    }

    public String getAssetLastModifiedDate() {
        return assetLastModifiedDate;
    }

    public String getAssetSize() {
        return getAssetSizeAndType(this.assetSize);
    }

    public String getAssetPath() {
        return assetPath;
    }

    private String getAssetSizeAndType(String assetSize){
        return assetSize;
    }


    public void setAssetTitle(String assetTitle) {
        this.assetTitle = assetTitle;
    }

    public void setAssetPublicationDate(String assetPublicationDate) {
        this.assetPublicationDate = assetPublicationDate;
    }

    public void setAssetDescription(String assetDescription) {
        this.assetDescription = assetDescription;
    }

    public void setAssetLastModifiedDate(String assetLastModifiedDate) {
        this.assetLastModifiedDate = assetLastModifiedDate;
    }

    public void setAssetSize(String assetSize) {
        this.assetSize = assetSize;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
}
