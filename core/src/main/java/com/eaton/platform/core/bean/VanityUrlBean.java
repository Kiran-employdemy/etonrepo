package com.eaton.platform.core.bean;

import java.sql.Timestamp;

/**
 * <html> Description: This bean class used in VanityDataStoreService class to store vanity objects </html> .
 *
 * @author ICF
 * @version 1.0
 * @since 2020
 */
public class VanityUrlBean {

    private String groupId;

    private String title;

    private String defaultPage;

    private String[] additionalPage;

    private String notes;

    private String status;

    private Timestamp createdDate;

    private Timestamp lastModifiedDate;

    private boolean primaryVanity;

    private boolean disabled;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getDefaultPage () { return defaultPage; }

    public void setDefaultPage (String defaultPage) { this.defaultPage = defaultPage; }

    public String[] getAdditionalPage() { return additionalPage; }

    public void setAdditionalPage (String[] additionalPage) {
        this.additionalPage = additionalPage;
    }

    @Override
    public String toString() {
        return "ClassPojo [defaultPage = "+defaultPage+", additionalPage = "+additionalPage+"]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public Timestamp getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Timestamp lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isPrimaryVanity() {
        return primaryVanity;
    }

    public void setPrimaryVanity(boolean primaryVanity) {
        this.primaryVanity = primaryVanity;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


}
