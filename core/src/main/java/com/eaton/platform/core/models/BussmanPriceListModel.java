package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.eaton.platform.core.bean.secure.AgentBean;
import com.eaton.platform.core.bean.secure.BusmannPriceFileName;
import com.eaton.platform.core.services.BussmannPriceFileService;
import com.eaton.platform.core.services.secure.AgentReportsServiceConfiguration;
import com.eaton.platform.core.util.CommonUtil;
import com.eaton.platform.integration.auth.models.AuthenticationToken;
import com.eaton.platform.integration.auth.models.UserProfile;
import com.eaton.platform.integration.auth.services.AuthenticationService;
import com.eaton.platform.integration.auth.services.AuthenticationServiceConfiguration;
import com.eaton.platform.integration.auth.util.AgentReportsUtil;
import com.eaton.platform.integration.auth.util.AuthCookieUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Model(adaptables = { Resource.class,
        SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BussmanPriceListModel {

    private static final Logger LOG = LoggerFactory.getLogger(BussmanPriceListModel.class);
    private static final String BUSS_US_ACCOUNT_GROUP = "Z001";
    private static final String BUSS_US_PRICE_LIST = "1231_VE_USD";
    private static final String BUSS_US_SALES_DISTRICT = "BU001";
    private static final String BUSS_US_FILE_NAME = "Buss US Ele Dist - Form_BP-CPDS.xlsx";
    private static final String BUSS_US_SALES_ORG = "1231";
    private static final String EDI_US_ACCOUNT_GROUP = "Z001";
    private static final String EDI_US_PRICE_LIST = "1233_VK_USD";
    private static final String EDI_US_SALES_DISTRICT = "BU005";
    private static final String EDI_US_SALES_ORG = "1233";
    private static final String EDI_US_FILE_NAME = "Form_EDPL.xlsx";
    private static final String BUSS_CAN_ACCOUNT_GROUP = "Z001";
    private static final String BUSS_CAN_SALES_ORG = "2031";
    private static final String BUSS_CAN_SALES_DISTRICT = "BU008";
    private static final String BUSS_CAN_PRICE_LIST = "2031_VH_CAD";
    private static final String BUSS_CAN_FILE_NAME = "Form_CBP-DS.xlsx";
    private static final String EDI_CAN_ACCOUNT_GROUP = "Z001";
    private static final String EDI_CAN_SALES_DISTRICT = "BU008";
    private static final String EDI_CAN_SALES_ORG = "2033";
    private static final String EDI_CAN_PRICE_LIST = "2033_VN_CAD";
    private static final String EDI_CAN_FILE_NAME = "Edison_Canadian_Price_Sheet.xlsx";

    private static final String HAS_BUSSMANN_PRICE_FILE = "hasbussmannpricefile";
    private static final String SALES_DISTRICT = "salesDistrict";
    private static final String ORG_UNIT1 = "orgUnit1";
    private static final String PRICE_LIST_ID = "priceListId";
    private static final String ORGANIZATION="organization";
    private static final String EATON_CUS_SITE_DN_STRING = "o=eaton.com";
    private Map<String, String> arAccessTokenMap;
    private ArrayList<String> bussmanAccessList;
    private Boolean isBussmanUser = false;
    private AuthenticationToken authenticationToken;
    private PoolingHttpClientConnectionManager conMgr;

    private ArrayList<BusmannPriceFileName> bussmanPriceList;

    /** The AgentReportsServiceConfiguration service. */
    @OSGiService
    private AgentReportsServiceConfiguration agentReportsServiceConfiguration;
     @OSGiService
    BussmannPriceFileService bussmannPriceFileService;
    /** The httpFactory service. */
    @OSGiService
    private HttpClientBuilderFactory httpFactory;

    /** The AuthenticationService config service. */
    @OSGiService
    private AuthenticationServiceConfiguration authenticationServiceConfig;

    /** The AuthenticationService service. */
    @OSGiService
    private AuthenticationService authenticationService;

    /** The AgentReports config service. */
    @OSGiService
    protected AgentReportsServiceConfiguration serviceConfiguration;

    @OSGiService
    private Externalizer externalizer;

    @Self
    private SlingHttpServletRequest request;

    @PostConstruct
    protected void init() {
        String rawJWT = AuthCookieUtil.getJWTFromAuthCookie(request, authenticationServiceConfig);
        if (StringUtils.isNotEmpty(rawJWT)) {
            authenticationToken = authenticationService.parseToken(rawJWT);
            UserProfile userProfile = authenticationToken.getUserProfile();
            LOG.debug("userProfile Value for logged in user{}", userProfile);
            if (userProfile != null) {
                Iterable<String> nsRolesList = userProfile.getApplicationAccessTags();
                for (String nsRole : nsRolesList) {
                    LOG.debug("Nsrole Value for logged in user{}", nsRole);
                    if (HAS_BUSSMANN_PRICE_FILE.equalsIgnoreCase(nsRole)
                            && userProfile.getEatonCustSite().getDn().contains(EATON_CUS_SITE_DN_STRING)) {
                        isBussmanUser = true;
                        break;
                    }
                }
            }

        }

       if (Boolean.TRUE.equals(isBussmanUser)) {
            LOG.debug("BussmanFlow :: Started");
            bussmanAccessList = new ArrayList<>();
            String userEmail = AgentReportsUtil.getUserEmail(authenticationToken);
            LOG.debug("userEmail found for {}.", userEmail);
            if (!userEmail.isEmpty() && bussmanAccessList.isEmpty()) {
                LOG.debug("bussmanAccessList and userEmail.");
                arAccessTokenMap = AgentReportsUtil.getAccessTokenAPI(conMgr, httpFactory,
                        agentReportsServiceConfiguration);

                JsonArray jsonArray = AgentReportsUtil.getUserInfo(conMgr, httpFactory,
                        agentReportsServiceConfiguration, userEmail, arAccessTokenMap);
                getBussmanOrgInfoJson(jsonArray);

            }
        }
    }

    private void getBussmanOrgInfoJson(JsonArray jsonArray) {
        if (jsonArray != null) {
            for (JsonElement jsonElement : jsonArray) {
                JsonObject accessJsonObj = jsonElement.getAsJsonObject();
                AgentBean agentBean = new Gson().fromJson(accessJsonObj, AgentBean.class);
                bussmanAccessList.add(agentBean.getId());
            }
        } else {
            LOG.debug("Agent IDs not found in for {}", bussmanAccessList);
        }

        if (!bussmanAccessList.isEmpty()) {
            bussmanPriceList = new ArrayList<>();
            for (String agentId : bussmanAccessList) {
                LOG.debug("Agent IDs is found in for {}", agentId);
                JsonObject orgInfoJson = AgentReportsUtil.getOrgInfo(agentId, conMgr, httpFactory,
                        serviceConfiguration, arAccessTokenMap);
                LOG.debug("orgInfoJson found in for {}", orgInfoJson);
                getBussmanInfoFilesData(orgInfoJson);
            }
        }

    }

    private void getBussmanInfoFilesData(JsonObject bussInfoJson) {

        // set 1
        JsonObject orgnatizataion = bussInfoJson.getAsJsonObject(ORGANIZATION);

        String salesOrg = null != orgnatizataion.get(ORG_UNIT1)
                ? orgnatizataion.get(ORG_UNIT1).toString().replace("\"", "")
                : "";
        LOG.debug("salesOrg {}", salesOrg);
        String salesDistrict = null != orgnatizataion.get(SALES_DISTRICT)
                ? orgnatizataion.get(SALES_DISTRICT).toString().replace("\"", "")
                : "";
        LOG.debug("salesDistrict {}", salesDistrict);
        String priceList = null != orgnatizataion.get(PRICE_LIST_ID)
                ? orgnatizataion.get(PRICE_LIST_ID).toString().replace("\"", "")
                : "";
        LOG.debug("priceList {}", priceList);
        createUSPriceList(bussmanPriceList, salesOrg, salesDistrict, priceList);
        createCAPriceList(bussmanPriceList, salesOrg, salesDistrict, priceList);

        LOG.debug("getBussmanInfoFilesData");
    }

    private void createUSPriceList(ArrayList<BusmannPriceFileName> bussmanPriceList, String salesOrg,
            String salesDistrict, String priceList) {
        BusmannPriceFileName busmannPriceEDIUS = new BusmannPriceFileName();
        BusmannPriceFileName busmannPriceFileUS = new BusmannPriceFileName();
        String accountGroup = "Z001";
        if (BUSS_US_SALES_ORG.equalsIgnoreCase(salesOrg) && priceList.contains(BUSS_US_PRICE_LIST)
                && BUSS_US_SALES_DISTRICT.equalsIgnoreCase(salesDistrict)
                && accountGroup.equalsIgnoreCase(BUSS_US_ACCOUNT_GROUP)) {
            busmannPriceFileUS.setNameOfFile(BUSS_US_FILE_NAME);
            busmannPriceFileUS.setUrlOfFile(CommonUtil.getMappedUrl(bussmannPriceFileService.getBussmannUSfileURL(), externalizer, request));

            bussmanPriceList.add(busmannPriceFileUS);
            LOG.debug("In us view");

        } else if (EDI_US_SALES_ORG.equalsIgnoreCase(salesOrg) && priceList.contains(EDI_US_PRICE_LIST)
                && (EDI_US_SALES_DISTRICT).equalsIgnoreCase(salesDistrict)
                && accountGroup.equalsIgnoreCase(EDI_US_ACCOUNT_GROUP)) {
            busmannPriceEDIUS.setNameOfFile(EDI_US_FILE_NAME);
            busmannPriceEDIUS.setUrlOfFile(CommonUtil.getMappedUrl(bussmannPriceFileService.getBussmannEDIUSfileURL(), externalizer, request));

            bussmanPriceList.add(busmannPriceEDIUS);
            LOG.debug("In us eid");

        } else {
            LOG.info("no files forund for US");
        }
    }

    private void createCAPriceList(ArrayList<BusmannPriceFileName> bussmanPriceLists, String salesOrg,
            String salesDistrict, String priceLists) {
        BusmannPriceFileName busmannPriceEDICAN = new BusmannPriceFileName();
        BusmannPriceFileName busmannPriceFileNameCAN = new BusmannPriceFileName();
        String accountGroup = "Z001";
        if (BUSS_CAN_SALES_ORG.equalsIgnoreCase(salesOrg) && priceLists.contains(BUSS_CAN_PRICE_LIST)
                && (BUSS_CAN_SALES_DISTRICT).equalsIgnoreCase(salesDistrict)
                && accountGroup.equalsIgnoreCase(BUSS_CAN_ACCOUNT_GROUP)) {
            busmannPriceFileNameCAN.setNameOfFile(BUSS_CAN_FILE_NAME);
            busmannPriceFileNameCAN.setUrlOfFile(CommonUtil.getMappedUrl(bussmannPriceFileService.getBussmannCANfileURL(), externalizer, request));
            LOG.debug("In ca view");
            bussmanPriceLists.add(busmannPriceFileNameCAN);
        } else if (EDI_CAN_SALES_ORG.equalsIgnoreCase(salesOrg) && priceLists.contains(EDI_CAN_PRICE_LIST)
                && (EDI_CAN_SALES_DISTRICT).equalsIgnoreCase(salesDistrict)
                && accountGroup.equalsIgnoreCase(EDI_CAN_ACCOUNT_GROUP)) {
            busmannPriceEDICAN.setNameOfFile(EDI_CAN_FILE_NAME);
            busmannPriceEDICAN.setUrlOfFile(CommonUtil.getMappedUrl(bussmannPriceFileService.getBussmannEDICANfileURL(), externalizer, request)
);
            LOG.debug("In ca edit");
            bussmanPriceLists.add(busmannPriceEDICAN);
        } else {
            LOG.info("no files forund for canada");
        }

    }

    public List<BusmannPriceFileName> getBussmanPriceList() {
        return bussmanPriceList;
    }

}
