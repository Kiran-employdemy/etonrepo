package com.eaton.platform.core.servlets;

public class AssetEmailAttachmentServletFixtures {
    public static final String emailBodyExpected = "<img src=\"https://www.eaton.com/content/dam/eaton/global/logos/eaton-logo-small.png\" /><br/>" +
            "<img src=\"https://www.eaton.com/content/dam/eaton/eaton-own-or-royalty-free-purchased-images/resources-bar.jpg\" /><br/>" +
            "<h1>Resources from Eaton.com</h1>" +
            "<li><a href=\"example.com/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/af-gf-receptacles/obc-afgf-dual-purpose-recep-sell-sheet.pdf\">AF/GF dual-purpose receptacles sell sheet</a></li>";

    public static final String emailBodyExpectedNoTitle = "<img src=\"https://www.eaton.com/content/dam/eaton/global/logos/eaton-logo-small.png\" /><br/>" +
            "<img src=\"https://www.eaton.com/content/dam/eaton/eaton-own-or-royalty-free-purchased-images/resources-bar.jpg\" /><br/>" +
            "<h1>Resources from Eaton.com</h1>" +
            "<li><a href=\"example.com/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/home-automation-hub/home-automation-hub-faq.pdf\">home-automation-hub-faq.pdf</a></li>";

    public static final String emailBodyExpectedI18nDeDe = "<img src=\"https://www.eaton.com/content/dam/eaton/global/logos/eaton-logo-small.png\" /><br/>" +
            "<img src=\"https://www.eaton.com/content/dam/eaton/eaton-own-or-royalty-free-purchased-images/resources-bar.jpg\" /><br/>" +
            "<h1>Ressourcen von Eaton.com</h1>" +
            "<li><a href=\"example.com/content/dam/eaton/products/wiring-devices-and-connectivity/wiring-devices/af-gf-receptacles/obc-afgf-dual-purpose-recep-sell-sheet.pdf\">AF/GF dual-purpose receptacles sell sheet</a></li>";

    public static final String subjectExpectedI18nEnUs = "Resources from Eaton.com";

    public static final String subjectExpectedI18nDeDe = "Ressourcen von Eaton.com";

    public static final String noReplyExpectedI18nEnUs = "This is an automatic generated message. Please do not reply.";

    public static final String noReplyExpectedI18nDeDe = "Dies ist eine automatisch generierte Nachricht. Bitte nicht antworten.";

}
