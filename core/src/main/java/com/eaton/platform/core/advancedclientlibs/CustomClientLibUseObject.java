package com.eaton.platform.core.advancedclientlibs;

import javax.script.Bindings;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.sightly.pojo.Use;
import org.slf4j.Logger;

import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;

public class CustomClientLibUseObject implements Use {

    private static final String BINDINGS_CATEGORIES = "categories";
    private static final String BINDINGS_MODE = "mode";
    
    private static final String BINDINGS_OPTION = "option";
    private static final String DEFER = "defer";
    private static final String ASYNC = "async";
    private String option;

    private HtmlLibraryManager htmlLibraryManager = null;
    private String[] categories;
    private String mode;
    private SlingHttpServletRequest request;
    private PrintWriter out;
    private Logger log;

    public void init(Bindings bindings) {
        Object categoriesObject = bindings.get(BINDINGS_CATEGORIES);
        option = (String) bindings.get(BINDINGS_OPTION);
        log = (Logger) bindings.get(SlingBindings.LOG);
        if (categoriesObject != null) {
            if (categoriesObject instanceof Object[]) {
                Object[] categoriesArray = (Object[]) categoriesObject;
                categories = new String[categoriesArray.length];
                int i = 0;
                for (Object o : categoriesArray) {
                    if (o instanceof String) {
                        categories[i++] = ((String) o).trim();
                    }
                }
            } else if (categoriesObject instanceof String) {
                categories = ((String) categoriesObject).split(",");
                int i = 0;
                for (String c : categories) {
                    categories[i++] = c.trim();
                }
            }
            if (categories != null && categories.length > 0) {
                mode = (String) bindings.get(BINDINGS_MODE);
                request = (SlingHttpServletRequest) bindings.get(SlingBindings.REQUEST);
                SlingScriptHelper sling = (SlingScriptHelper) bindings.get(SlingBindings.SLING);
                htmlLibraryManager = sling.getService(HtmlLibraryManager.class);
            }
        }
    }

    public String include() {
        StringWriter sw = new StringWriter();
        try {
            if (categories == null || categories.length == 0)  {
                log.error("'categories' option might be missing from the invocation of the /apps/granite/sightly/templates/clientlib.html" +
                        "client libraries template library. Please provide a CSV list or an array of categories to include.");
            } else {
                PrintWriter out = new PrintWriter(sw);
                if ("js".equalsIgnoreCase(mode)) {
                    htmlLibraryManager.writeJsInclude(request, out, categories);
                } else if ("css".equalsIgnoreCase(mode)) {
                    htmlLibraryManager.writeCssInclude(request, out, categories);
                } else {
                    htmlLibraryManager.writeIncludes(request, out, categories);
                }
            }
        } catch (IOException e) {
            log.error("Failed to include client libraries {}", categories);
        }
        
        String output = sw.toString();

        if (StringUtils.isNotEmpty(output) && StringUtils.isNotBlank(option)) {
            if (option.equals(DEFER)) {
                output = output.replaceAll(".js\"></script>", ".js\" defer></script>");
            } else if (option.equals(ASYNC)) {
                output = output.replaceAll(".js\"></script>", ".js\" async></script>");
            }
        }
        
        return output.trim();
    }
}

