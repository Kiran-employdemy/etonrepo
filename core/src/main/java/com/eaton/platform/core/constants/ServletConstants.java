package com.eaton.platform.core.constants;

import org.apache.http.HttpStatus;
import org.apache.sling.api.servlets.HttpConstants;

public final class ServletConstants extends HttpConstants implements HttpStatus {

  public static final String SLING_SERVLET_PATHS = "sling.servlet.paths=";
  public static final String SLING_SERVLET_SELECTORS ="sling.servlet.selectors=";
  public static final String SLING_SERVLET_EXTENSIONS ="sling.servlet.extensions=";
  public static final String SLING_SERVLET_EXTENSION_JSON = SLING_SERVLET_EXTENSIONS + "json";
  public static final String SLING_SERVLET_EXTENSION_ZIP = SLING_SERVLET_EXTENSIONS + "zip";
  public static final String SLING_SERVLET_EXTENSION_CSV = SLING_SERVLET_EXTENSIONS + "csv";
  public static final String SLING_SERVLET_EXTENSION_HTML = SLING_SERVLET_EXTENSIONS + "html";
  public static final String SLING_SERVLET_EXTENSION_XML = SLING_SERVLET_EXTENSIONS + "xml";
  public static final String SLING_SERVLET_EXTENSION_XLS = SLING_SERVLET_EXTENSIONS + "xls";
  public static final String SLING_SERVLET_RESOURCE_TYPES = "sling.servlet.resourceTypes=";
  public static final String SLING_SERVLET_DEFAULT_RESOURCE_TYPE = "sling/servlet/default";
  public static final String SLING_SERVLET_METHODS = "sling.servlet.methods=";
  public static final String SLING_SERVLET_METHODS_GET = SLING_SERVLET_METHODS + HttpConstants.METHOD_GET;
  public static final String SLING_SERVLET_METHODS_POST = SLING_SERVLET_METHODS + HttpConstants.METHOD_POST;
  public static final String SLING_SERVLET_METHODS_HEAD = SLING_SERVLET_METHODS + HttpConstants.METHOD_HEAD;

  public static final String SLING_SERVLET_METATYPE ="sling.servlet.metatype=";

  private ServletConstants(){
    super();
  }
}
