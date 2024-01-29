package com.eaton.platform.core.bean;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


class SecondaryLinksBeanTest {

    @Test
    public void shouldExtractFileNameWithExtension() {
        String url = "/content/dam/eaton/products/backup-power-ups-surge-it-power-distribution/backup-power-ups/eaton-5p-ups/eaton-5prackmount-ups-brochure-br153009en.pdf";
        String expectedFileName = "eaton-5prackmount-ups-brochure-br153009en.pdf";
        SecondaryLinksBean secondaryLinksBean = new SecondaryLinksBean();
        secondaryLinksBean.setPath(url);
        String actualFileName = secondaryLinksBean.getFileName();
        assertEquals(expectedFileName, actualFileName, "Extracted filename should match");
    }

    @Test
    public void shouldExtractFileNameWithoutExtension() {
        String url = "/content/dam/eaton/products/backup-power-ups-surge-it-power-distribution/backup-power-ups/eaton-5p-ups/eaton-5prackmount-ups-brochure-br153009en";
        String expectedFileName = "eaton-5prackmount-ups-brochure-br153009en";
        SecondaryLinksBean secondaryLinksBean = new SecondaryLinksBean();
        secondaryLinksBean.setPath(url);
        String actualFileName = secondaryLinksBean.getFileName();

        assertEquals(expectedFileName, actualFileName, "Extracted filename should match");
    }

    @Test
    public void shouldHandleEmptyUrl() {
        String url = "";
        String expectedFileName = null;
        SecondaryLinksBean secondaryLinksBean = new SecondaryLinksBean();
        secondaryLinksBean.setPath(url);
        String actualFileName = secondaryLinksBean.getFileName();
        assertEquals(expectedFileName, actualFileName, "Empty url should return null");
    }

    @Test
    public void shouldHandleUrlWithQueryParameters() {
        String url = "/content/dam/eaton/products/abc.pdf?param1=value1&param2=value2";
        String expectedFileName = "abc.pdf";
        SecondaryLinksBean secondaryLinksBean = new SecondaryLinksBean();
        secondaryLinksBean.setPath(url);
        String actualFileName = secondaryLinksBean.getFileName();
        assertEquals(expectedFileName, actualFileName, "Filename should be extracted ignoring query parameters");
    }

    @Test
    public void shouldHandleUrlWithEncodedCharacters() {
        String url = "/content/dam/eaton/products/%E2%82%AC%20euro.pdf";
        String expectedFileName = "€ euro.pdf";
        SecondaryLinksBean secondaryLinksBean = new SecondaryLinksBean();
        secondaryLinksBean.setPath(url);
        String actualFileName = secondaryLinksBean.getFileName();
        assertEquals(expectedFileName, actualFileName, "Filename should be extracted with decoded characters");
    }
}