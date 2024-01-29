package com.eaton.platform.core.util;

import com.eaton.platform.core.constants.CommonConstants;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

import static com.eaton.platform.core.constants.CommonConstants.*;

public class ResourceBundleFixtures {
    public static final String BULK_DOWNLOAD_TOOL_TIP = "Bulk Download ToolTip";
    public static final String DOWNLOAD_VALUE = "Download";
    public static final String BULK_DOWNLOAD_VALUE = "Bulk Download";

    public static final String DOWNLOAD_EXCEEDED_TEXT = "You exceeded the download limit";

    public static final String RESOURCES_VALUE = "Resources";
    public static final String FROM_VALUE = "from";
    public static final String NOREPLY_KEY = "bdeNoReply";
    public static final String NOREPLY_VALUE = "This is an automatic generated message. Please do not reply.";

    public static final String RESOURCES_DE_VALUE = "Ressourcen";
    public static final String FROM_DE_VALUE = "von";
    public static final String NOREPLY_DE_VALUE = "Dies ist eine automatisch generierte Nachricht. Bitte nicht antworten.";
    public static final String OTHER_KEY = "other key";
    public static final String OTHER_VALUE = "other value";
    public static final String HOW_TO_BUY_VALUE = "How to buy";
    public static final String MULTILINGUAL_KEY = "multilingual";
    public static final String MULTILINGUAL_VALUE = "Multilingual";
    public static final String GLOBAL_KEY = "global";
    public static final String GLOBAL_VALUE = "Global";

    public static ResourceBundle resourceBundle() { return new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[][]{
                    {BULK_DOWNLOAD_TOOL_TIP_KEY, BULK_DOWNLOAD_TOOL_TIP},
                    {DOWNLOAD_KEY, DOWNLOAD_VALUE},
                    {BULK_DOWNLOAD_KEY, BULK_DOWNLOAD_VALUE},
                    {DOWNLOAD_LIMIT_EXCEEDED_KEY, DOWNLOAD_EXCEEDED_TEXT},
                    {RESOURCES_TAB, RESOURCES_VALUE},
                    {FROM, FROM_VALUE},
                    {NOREPLY_KEY, NOREPLY_VALUE},
                    {MULTILINGUAL_KEY, MULTILINGUAL_VALUE},
                    {GLOBAL_KEY, GLOBAL_VALUE},
                    {"EndecaLabel.eaton-secure_attributes", "My Eaton"},
                    {"secure-only", "Secure only"},
                    {"statusNew", "New"},
                    {"statusUpdated", "Updated"},
                    {CommonConstants.HOW_TO_BUY_LABEL, HOW_TO_BUY_VALUE},
                    {OTHER_KEY, OTHER_VALUE}
            };
        }
    };
    }

    public static ResourceBundle resourceBundleDeDe() { return new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[][]{
                    {RESOURCES_TAB, RESOURCES_DE_VALUE},
                    {FROM, FROM_DE_VALUE},
                    {NOREPLY_KEY, NOREPLY_DE_VALUE},
                    {OTHER_KEY, OTHER_VALUE}
            };
        }
    };
    }

    public static ResourceBundle resourceBundlePartnerProgramEnUs() { return new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[][]{
                    {"progConsultantProgram", "Consultant program"},
                    {"progCtrPanelBuilderProgram", "Control panel builder program"},
                    {"progElectricalInstaller", "Electrical installer program"},
                    {"Registered", "Registered"},
                    {"Authorized", "Authorized"}
            };
        }
    };
    }

    public static ResourceBundle resourceBundlePartnerProgramDeDe() { return new ListResourceBundle() {
        @Override
        protected Object[][] getContents() {
            return new Object[][]{
                    {"progConsultantProgram", "Beraterprogramm"},
                    {"progCtrPanelBuilderProgram", "Programm zum Erstellen von Bedienfeldern"},
                    {"progElectricalInstaller", "Elektroinstallateurprogramm"},
                    {"Registered", "Eingetragen"},
                    {"Authorized", "Autorisiert"}
            };
        }
    };
    }
}
