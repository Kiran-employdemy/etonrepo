package com.eaton.platform.core.services.vanity.impl;

import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.vanity.EatonVanityConfigService;
import com.eaton.platform.core.services.vanity.EatonVanityURLService;
import com.eaton.platform.core.services.vanity.LocaleMappingService;
import com.eaton.platform.core.services.vanity.VanityDataStoreService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

/**
 * <html> Description: This class is used to get redirection url associated with requested vanity url. </html>
 * @author ICF
 * @version 1.0
 * @since 2020
 *
 */
@Component(service = EatonVanityURLService.class,immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "EatonVanityURLServiceImpl",
                AEMConstants.PROCESS_LABEL + "EatonVanityURLServiceImpl"
        })
public class EatonVanityURLServiceImpl implements EatonVanityURLService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EatonVanityURLServiceImpl.class);
    private static final String VANITY_DISPATCH_CHECK_ATTR = "acs-aem-commons__vanity-check-loop-detection";

    @Reference
    private VanityDataStoreService vanityDataStoreService;

    @Reference
    private LocaleMappingService localeMappingService;

    @Reference
    private EatonVanityConfigService eatonVanityConfigService;

    @Reference
    private Externalizer externalizer;

    @Activate
    protected void activate() {

        LOGGER.debug("Inside activate method for EatonVanityURLServiceImpl");
    }

    @Override
    public boolean dispatchMethod(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        try {
            if (request.getAttribute(VANITY_DISPATCH_CHECK_ATTR) != null) {
                LOGGER.trace("Processing a previously vanity dispatched request. Skipping...");
                return false;
            }
            request.setAttribute(VANITY_DISPATCH_CHECK_ATTR, true);
            LOGGER.debug("EatonVanityURLServiceImpl :: dispatchMethod() :: Started");
             final JSONObject vanityObject = isVanityRequest(request) ? vanityDataStoreService.lookupAndFetchVanityEntry(request): null;
             if (null != vanityObject) {
                 /* Forwarding request to vanity resource - country site*/
                 final String countrySitePage = Objects.nonNull(localeMappingService) ? localeMappingService.getLanguageSiteRootPagePath(request) : StringUtils.EMPTY;
                 final String redirectPagePath = vanityMappingToAdditionalPages(vanityObject, countrySitePage);
                 if (StringUtils.isNotBlank(redirectPagePath)) {
                     final String redirectionLink = CommonUtil.dotHtmlLink(redirectPagePath, request.getResourceResolver());
                     response.sendRedirect(redirectionLink.endsWith(CommonConstants.HTML_EXTN) ? redirectionLink : redirectionLink.concat(CommonConstants.HTML_EXTN));
                     return true;
                 } else {
                     LOGGER.debug("Forwarding request to vanity resource");
                     final String defaultPagePath = CommonUtil.dotHtmlLink(vanityObject.getString(CommonConstants.DEFAULT_PAGE_PATH), request.getResourceResolver());
                     response.sendRedirect(defaultPagePath.endsWith(CommonConstants.HTML_EXTN) ? defaultPagePath : defaultPagePath.concat(CommonConstants.HTML_EXTN));
                     return true;
                 }
             }
        } catch (JSONException jsonException){
            LOGGER.error("JSONException in dispatchMethod() - EatonVanityURLServiceImpl",jsonException);
        } catch (IOException io){
            LOGGER.error("IOException in dispatchMethod() method - EatonVanityURLServiceImpl",io);
        }
        LOGGER.debug("EatonVanityURLServiceImpl :: dispatchMethod() :: Exit");
        return false;
    }


    private  String  vanityMappingToAdditionalPages(JSONObject vanityObject,String countrySitePage) throws JSONException {
        LOGGER.debug("EatonVanityURLServiceImpl :: vanityMappingToAdditionalPages() :: Started");
        final JSONArray additionalPageArr = ! vanityObject.isNull(CommonConstants.ADDITIONAL_PAGE_PATH) ? (JSONArray) vanityObject.get(CommonConstants.ADDITIONAL_PAGE_PATH) : new JSONArray() ;
        String targetCountrySitePage = "";
        String redirectPath = "";
        if(additionalPageArr.length() > 0 &&  StringUtils.isNotBlank(countrySitePage)) {
            for (int index = 0; index < additionalPageArr.length(); index++) {
                if (additionalPageArr.getString(index).contains(countrySitePage)) {
                    targetCountrySitePage = additionalPageArr.getString(index).concat(CommonConstants.HTML_EXTN);
                }
            }
        }
        if (StringUtils.isNotBlank(targetCountrySitePage)) {
            redirectPath = targetCountrySitePage;
        } else {
            if (eatonVanityConfigService != null) {
                final StringBuilder intermediatePagePathWURL = new StringBuilder();
                if (Objects.nonNull(localeMappingService) && StringUtils.isNotBlank(countrySitePage)){
                    final String countrySitePageShortURL = CommonUtil.removeSiteContentRootPathPrefix(countrySitePage) ;
                    final String defaultPageShortURL = CommonUtil.removeSiteContentRootPathPrefix(vanityObject.get(CommonConstants.DEFAULT_PAGE_PATH).toString());
                    intermediatePagePathWURL.append(CommonConstants.SLASH_STRING)
                            .append(countrySitePageShortURL)
                            .append(eatonVanityConfigService.getVanityConfig().getIntermediatePageName())
                            .append(CommonConstants.HTML_EXTN)
                            .append(CommonConstants.DEFAULT_PAGE_URL_PARAM)
                            .append(CommonConstants.SLASH_STRING)
                            .append(defaultPageShortURL)
                            .append(CommonConstants.HTML_EXTN)
                            .append(CommonConstants.HOME_PAGE_URL_PARAM)
                            .append(CommonConstants.SLASH_STRING)
                            .append(countrySitePageShortURL).append(CommonConstants.HTML_EXTN);

                    redirectPath = intermediatePagePathWURL.toString();
                }
            }
        }

        LOGGER.debug("EatonVanityURLServiceImpl :: vanityMappingToAdditionalPages() :: Exit");
        return redirectPath;

    }
    private boolean isVanityRequest(SlingHttpServletRequest request){
        final String[] skipPaths = eatonVanityConfigService.getVanityConfig().getLookupSkipPath();
        final String requestPath = request.getRequestPathInfo().getResourcePath();
        return Arrays.stream(skipPaths).noneMatch(requestPath::startsWith);
    }

}
