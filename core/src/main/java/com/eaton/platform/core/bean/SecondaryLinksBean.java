package com.eaton.platform.core.bean;

import com.eaton.platform.core.constants.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class SecondaryLinksBean {
	private static final Logger LOG = LoggerFactory
			.getLogger(SecondaryLinksBean.class);
    private String text;
    private String path;
    private String newWindow;
    private Boolean isExternal;
    private Boolean secLinkSkuOnly;
    private String fileName;


    public Boolean getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNewWindow() {
        return newWindow;
    }

    public void setNewWindow(String newWindow) {
        this.newWindow = newWindow;
    }

    public Boolean isSecLinkSkuOnly() {
        return secLinkSkuOnly;
    }

    public void setSecLinkSkuOnly(Boolean secLinkSkuOnly) {
        this.secLinkSkuOnly = secLinkSkuOnly;
    }

    public String getFileName() {
        LOG.info("SecondaryLinksBean :: getFileName :: START");
        try {
            if (null != path && !CommonConstants.ARTICLE_PAGE_TEMPLATE_PATH.equals(path)) {
                fileName = Paths.get(new URI(path).getPath()).getFileName().toString();
            }
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
        }
        return fileName;
    }
}
