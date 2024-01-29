package com.eaton.platform.core.servlets;

import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.constants.ServletConstants;
import com.eaton.platform.core.constants.VanityStatus;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.vanity.VanityDataStoreService;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

/**
 * This class will update the vanity json file with new vanity information
 */
@Component(service = Servlet.class, immediate = true, property = {
        ServletConstants.SLING_SERVLET_METHODS_GET,
        ServletConstants.SLING_SERVLET_METHODS_POST,
        ServletConstants.SLING_SERVLET_PATHS + "/eaton/vanity/authoring"
})
public class VanityDataManagementServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = -7418546517794570232L;
    private static final Logger LOG = LoggerFactory.getLogger(VanityDataManagementServlet.class);
    private static final String VANITY_PATH = "/content/dam/eaton/resources/vanity/";

    private enum vanityOperation {
        CREATE,
        UPDATE,
        DELETE,
        DISABLED,
        PUBLISH;
    }

    @Reference
    private AdminService adminService;


    @Reference
    private Replicator replicator;


    @Reference
    private VanityDataStoreService vanityDataStoreService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.debug(" VanityDataManagementServlet :: doGet() :: Started");
        final PrintWriter responseWriter = response.getWriter();
        final String domainName = request.getParameter(CommonConstants.DOMAIN_NAME);
        final JSONObject resJsonObject = new JSONObject();
        final JSONArray resJsonArr = new JSONArray();
        try {
            final JSONObject domainJsonObject = vanityDataStoreService.getDomainSpecificVanities(domainName);
            if(domainJsonObject != null && domainJsonObject.getJSONObject(domainName) != null) {
                final JSONObject parentJson = domainJsonObject.getJSONObject(domainName);
                JSONObject resVanityObject = new JSONObject();
                final Iterator<String> keys = parentJson.keys();
                while (keys.hasNext()) {
                   final String key = keys.next();
                    if (parentJson.get(key) instanceof JSONObject) {
                        resVanityObject = parentJson.getJSONObject(key);
                        resVanityObject.put(CommonConstants.VANITY_URL, key);
                        resVanityObject.put(CommonConstants.DOMAIN, domainName);
                        final Timestamp publishDate = getPublishDateDisplay(request.getResourceResolver(), domainName);
                        if (publishDate != null) {
                            resVanityObject.put(CommonConstants.PUBLISHING_DATE, publishDate);
                        }
                    }
                    resJsonArr.put(resVanityObject);
                }
                resJsonObject.put(CommonConstants.DATA, resJsonArr);
            }

        } catch (JSONException e) {
            LOG.error("VanityDataManagementServlet :: doGet() :: Error while writing json", e);
        }
        LOG.debug(" VanityDataManagementServlet :: doGet() :: Exit");
        responseWriter.print(resJsonObject);
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        LOG.debug(" VanityDataManagementServlet :: doPost() :: Started");
        try(final ResourceResolver writeService = adminService.getWriteService()){
            final String domainName = request.getParameter(CommonConstants.DOMAIN_NAME);
            final String operation = request.getParameter(CommonConstants.OPERATION);
            final boolean domainCheck = CommonConstants.EATON_DOMAIN.equals(domainName) || CommonConstants.LOGIN_DOMAIN.equals(domainName) || CommonConstants.EATON_CUMMINS_DOMAIN.equals(domainName) ||
                    CommonConstants.GREEN_SWITCHING_DOMAIN.equals(domainName) || CommonConstants.PHOENIX_TEC_POWER_DOMAIN.equals(domainName);

            /* Checking the domain and invoking operation*/
            if (domainCheck && CommonUtil.getRunModes().contains(Externalizer.AUTHOR)) {
                final vanityOperation operationName = vanityOperation.valueOf(operation.toUpperCase(Locale.ENGLISH));
                switch (operationName) {
                    case CREATE:
                        createNewVanity(domainName, request, writeService, response, operation);
                        break;
                    case UPDATE:
                        updateVanityData(domainName, request, writeService, response, operation);
                        break;
                    case DISABLED:
                        disableVanityUrls(domainName, request, writeService, response, operation);
                        break;
                    case PUBLISH:
                        publishJson(domainName, request, writeService, response);
                        break;
                    case DELETE:
                        deleteVanity(domainName, request, writeService, response, operation);
                        break;
                    default:
                        LOG.debug(" VanityDataManagementServlet :: doPost() :: Exit");
                }
            } else {
                response.setStatus(403);
            }
        }
        LOG.debug(" VanityDataManagementServlet :: doPost() :: Exit");

    }


    /**
     * This method is used to create new vanity entry
     *
     */
    protected void createNewVanity(String jsonFileName, SlingHttpServletRequest request, ResourceResolver writeService, SlingHttpServletResponse response , String operation) {
        LOG.debug(" VanityDataManagementServlet :: updateVanityData() :: Started");
        final JSONObject vanityJsonObject = vanityDataStoreService.getDomainSpecificVanities(jsonFileName);
        if(vanityJsonObject!= null) {
            try {
                response.setContentType("text/html;charset=UTF-8");
                final PrintWriter printWriter = response.getWriter();
                final JSONObject createVanityJson = vanityDataStoreService.createNewVanity(jsonFileName, request, null, vanityJsonObject,"");
                if(createVanityJson != null) {
                    writeJsonToFile(jsonFileName, writeService, createVanityJson);
                    printWriter.println(vanityDataStoreService.getUnPublishChangesCount(jsonFileName, operation));
                } else{
                    printWriter.println(CommonConstants.TRUE);
                }
                printWriter.close();
            } catch (IOException e) {
                LOG.error("VanityDataManagementServlet :: updateVanityData() :: Error while getting writer", e);
            }
        }
        LOG.debug(" VanityDataManagementServlet :: updateVanityData() :: Exit");
    }


    /**
     * This method is used to update existing vanity entry
     *
     */
    protected void updateVanityData(String jsonFileName, SlingHttpServletRequest request, ResourceResolver writeService, SlingHttpServletResponse response, String operation) {
        LOG.debug(" VanityDataManagementServlet :: updateVanityData() :: Started");
        final JSONObject vanityJsonObject = vanityDataStoreService.getDomainSpecificVanities(jsonFileName);
        if(vanityJsonObject!= null) {
            try {
                final PrintWriter printWriter = response.getWriter();
                final JSONObject updateVanityJson = vanityDataStoreService.updateRequestedVanity(request, jsonFileName, CommonUtil.getCurrentTime(), vanityJsonObject);
                writeJsonToFile(jsonFileName, writeService, updateVanityJson);
                printWriter.println(vanityDataStoreService.getUnPublishChangesCount(jsonFileName,operation));
                printWriter.close();
            } catch (IOException e) {
                LOG.error("VanityDataManagementServlet :: updateVanityData() :: Error while getting writer", e);
            }
        }
        LOG.debug(" VanityDataManagementServlet :: updateVanityData() :: Exit");

    }

    /**
     * This method is used to disable vanity entries. It dos not delete the entry.
     *
     */
    protected void disableVanityUrls(String jsonFileName, SlingHttpServletRequest request, ResourceResolver writeService, SlingHttpServletResponse response, String operation)  {
        LOG.debug(" VanityDataManagementServlet :: disableVanityUrls() :: Started");
        final String[] vanityUrlList = request.getParameterValues("vanityUrlList");
        response.setContentType("text/html;charset=UTF-8");
        try {
            final PrintWriter printWriter = response.getWriter();
            final JSONObject jsonObject = vanityDataStoreService.disableRequestedVanities(vanityUrlList,jsonFileName,CommonUtil.getCurrentTime());
            writeJsonToFile(jsonFileName, writeService , jsonObject);
            printWriter.println(vanityDataStoreService.getUnPublishChangesCount(jsonFileName,operation));
            printWriter.close();
        } catch (IOException e) {
            LOG.error("VanityDataManagementServlet :: Error while getting writer", e);
        }
        LOG.debug(" VanityDataManagementServlet :: disableVanityUrls() :: Exit");

    }

    /**
     * This method is used to publish vanity json file from DAM resource
     *
     */
    protected void publishJson(String jsonFileName, SlingHttpServletRequest request, ResourceResolver writeService, SlingHttpServletResponse response) {
        LOG.debug(" VanityDataManagementServlet :: publishJson() :: Started");
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final Session session = resourceResolver.adaptTo(Session.class);
        try {
            final PrintWriter printWriter = response.getWriter();
            final JSONObject jsonObject = vanityDataStoreService.getDomainSpecificVanities(jsonFileName);
            if(jsonObject != null && jsonObject.getJSONObject(jsonFileName) != null) {
                JSONObject vanityJson = jsonObject.getJSONObject(jsonFileName);
                if(vanityJson != null) {
                    final Iterator<String> keys = vanityJson.keys();
                    while (keys.hasNext()) {
                        final String key = keys.next();
                        vanityJson = setPublishStatus(vanityJson, key);
                    }
                }
                writeJsonToFile(jsonFileName, writeService , vanityJson);
            }
            replicator.replicate(session, ReplicationActionType.ACTIVATE, VANITY_PATH.concat(jsonFileName).concat(CommonConstants.JSON_EXTN));
            printWriter.close();
        } catch (ReplicationException | JSONException e) {
            LOG.error("VanityDataManagementServlet :: publishJson() :: Error occurred", e);
        } catch (IOException e) {
            LOG.error("VanityDataManagementServlet :: publishJson() :: Error occurred while writing json", e);
        }
        LOG.debug(" VanityDataManagementServlet :: publishJson() :: Exit");
    }

    private static JSONObject setPublishStatus(JSONObject vanityJson, String key) {
        try {
            if (vanityJson.get(key) instanceof JSONObject) {
                final String status = vanityJson.getJSONObject(key).getString(CommonConstants.STATUS) != null &&
                    (VanityStatus.DISABLED_UNPUBLISHED_STATUS.getVanityStatus().equals(vanityJson.getJSONObject(key).getString(CommonConstants.STATUS))
                            || VanityStatus.DISABLED_STATUS.getVanityStatus().equals(vanityJson.getJSONObject(key).getString(CommonConstants.STATUS))) ?
                    VanityStatus.DISABLED_STATUS.getVanityStatus() : VanityStatus.ACTIVE.getVanityStatus();
                vanityJson.getJSONObject(key).put(CommonConstants.STATUS, status);
            }
        } catch (JSONException e) {
            LOG.error("VanityDataManagementServlet :: publishJson() :: Error occurred", e);
        }
        return vanityJson;
    }

    /**
     * This method is used to delete vanity entries
     *
     */
    protected void deleteVanity(String jsonFileName, SlingHttpServletRequest request, ResourceResolver writeService, SlingHttpServletResponse response, String operation){
        LOG.debug(" VanityDataManagementServlet :: deleteVanity() :: Started");
        final String[] vanityUrlList = request.getParameterValues(CommonConstants.VANITY_URL_LIST);
        response.setContentType("text/html;charset=UTF-8");
        try {
            final PrintWriter printWriter = response.getWriter();
            final JSONObject jsonObject = vanityDataStoreService.deleteRequestedVanities(vanityUrlList,jsonFileName,CommonUtil.getCurrentTime());
            writeJsonToFile(jsonFileName, writeService , jsonObject);
            printWriter.println(vanityDataStoreService.getUnPublishChangesCount(jsonFileName,operation));
            printWriter.close();
        } catch (IOException e) {
            LOG.error("VanityDataManagementServlet :: deleteVanity() : Error while getting writer", e);
        }
        LOG.debug(" VanityDataManagementServlet :: deleteVanity() :: Exit");
    }

    protected void writeJsonToFile(String jsonFileName, ResourceResolver writeService, JSONObject jsonObject){
        LOG.debug(" VanityDataManagementServlet :: writeJsonToFile() :: Started");
        final String jsonFile = VANITY_PATH + jsonFileName + CommonConstants.JSON_EXTN;
        try {
            final JSONObject newJsonObject = new JSONObject();
            newJsonObject.put(jsonFileName, jsonObject);

            final AssetManager assetManager = writeService.adaptTo(AssetManager.class);
            final InputStream inputStream = new ByteArrayInputStream(newJsonObject.toString().getBytes());

            assert assetManager != null;
            assetManager.createAsset(jsonFile, inputStream, CommonConstants.EXTENSION_JSON, true);
        } catch (JSONException e) {
            LOG.error("VanityDataManagementServlet :: writeJsonToFile() :: Error while writing json", e);
        }
        LOG.debug(" VanityDataManagementServlet :: writeJsonToFile() :: Exit");
    }

    /**
     * This method is used to convert lastReplicated date to Timestamp
     *
     * @return publishedDate
     *
     */
    private static Timestamp getPublishDateDisplay(ResourceResolver resourceResolver,String domainName) {
        LOG.debug(" VanityDataManagementServlet :: getPublishDateDisplay() :: Started");
        Timestamp dateTimeStamp = null;
        final Resource linkPathResource = resourceResolver.getResource(VANITY_PATH.concat(domainName).concat(CommonConstants.JSON_EXTN));
        try {
            if (null != linkPathResource) {
                final Resource jcrResource = linkPathResource.getChild(CommonConstants.JCR_CONTENT_STR);
                if (null != jcrResource && jcrResource.getValueMap() != null) {
                    final ValueMap valueMap = jcrResource.getValueMap();
                    final SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.PUBLISHING_DATE_FORMAT);
                    if(valueMap.get(CommonConstants.REPLICATION_DATE)!=null) {
                        final String publicationDate = CommonUtil.format((Calendar) valueMap.get(CommonConstants.REPLICATION_DATE), dateFormat);
                        final Date date = dateFormat.parse(publicationDate);
                        dateTimeStamp = new Timestamp(date.getTime());
                    }
                }
            }
        } catch (ParseException e) {
            LOG.error("VanityDataManagementServlet :: getPublishDateDisplay() :: Error while parsing", e);
        }
        LOG.debug(" VanityDataManagementServlet :: getPublishDateDisplay() :: Exit");
        return dateTimeStamp;
    }
}
