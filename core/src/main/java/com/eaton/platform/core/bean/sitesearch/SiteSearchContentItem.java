
package com.eaton.platform.core.bean.sitesearch;

import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.eaton.platform.core.bean.loadmore.Image;
import com.eaton.platform.core.bean.loadmore.Link;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({
    "name",
    "description",
    "image",
    "link",
    "secondaryLinks",
    "articleType",
    "date",
    "documentType",
    "documentSize",
    "documentName",
    "status",
    "eatonSHA",
    "eatonECCN",
    "secure"

})
public class SiteSearchContentItem {

    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("image")
    private Image image;
    @JsonProperty("link")
    private Link link;
    @JsonProperty("secondaryLinks")
    private List<SecondaryLink> secondaryLinks = null;
    @JsonProperty("articleType")
    private String articleType;
    @JsonProperty("date")
    private String date;
    @JsonProperty("documentType")
    private String documentType;
    @JsonProperty("documentSize")
    private String documentSize;
    @JsonProperty("documentName")
    private String documentName;
    @JsonProperty("status")
    private String status;
    @JsonProperty("eatonSHA")
    private String eatonSHA;
    @JsonProperty("eatonECCN")
    private String eatonECCN;
    @JsonProperty("secure")
    private boolean secure;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public List<SecondaryLink> getSecondaryLinks() {
        return secondaryLinks;
    }

    public void setSecondaryLinks(List<SecondaryLink> secondaryLinks) {
        this.secondaryLinks = secondaryLinks;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentSize() {
        return documentSize;
    }

    public void setDocumentSize(String documentSize) {
        this.documentSize = documentSize;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
    	this.status = status;
    }
    public String getEatonSHA() {
        return eatonSHA;
    }
    public void setEatonSHA(String eatonSHA) {
        this.eatonSHA = eatonSHA;
    }
    public String getEatonECCN() {
        return eatonECCN;
    }
    public void setEatonECCN(String eatonECCN) {
        this.eatonECCN = eatonECCN;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }
}
