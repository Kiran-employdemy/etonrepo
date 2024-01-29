package com.eaton.platform.integration.endeca.services.impl;

import com.eaton.platform.integration.endeca.bean.EndecaServiceRequestBean;
import com.eaton.platform.integration.endeca.pojo.pdh.EndecaPdhResponse;
import com.google.gson.Gson;

public class EndecaServiceResponseBeanFixtures {

    public static final String SAMPLE_RESULT = "{\n" +
            "  \"response\" : {\n" +
            "    \"status\" : null,\n" +
            "    \"statusDetails\" : {\n" +
            "      \"messages\" : null\n" +
            "    },\n" +
            "    \"didYouMean\" : null,\n" +
            "    \"binning\" : null,\n" +
            "    \"searchResults\" : {\n" +
            "      \"totalCount\" : 1,\n" +
            "      \"document\" : [ {\n" +
            "        \"content\" : [ {\n" +
            "          \"name\" : \"Document_Language\",\n" +
            "          \"type\" : \"text\",\n" +
            "          \"value\" : \"en_US,Global,en_GB\"\n" +
            "        }, {\n" +
            "          \"name\" : \"Country\",\n" +
            "          \"type\" : \"text\",\n" +
            "          \"value\" : \"CA,MX,US\"\n" +
            "        }, {\n" +
            "          \"name\" : \"Language\",\n" +
            "          \"type\" : \"text\",\n" +
            "          \"value\" : \"de_DE,en_GB,en_US,fr_CA,fr_FR\"\n" +
            "        }, {\n" +
            "          \"name\" : \"INVENTORY_ITEM_ID\",\n" +
            "          \"type\" : \"text\",\n" +
            "          \"value\" : \"6380105\"\n" +
            "        } ]\n" +
            "      } ]\n" +
            "    },\n" +
            "    \"request\" : null\n" +
            "  }\n" +
            "}";

    public static final String RESULT_FROM_EMPTY_REQ_STRING = "{\n" +
            "  \"response\" : {\n" +
            "    \"status\" : \"fail\",\n" +
            "    \"statusDetails\" : {\n" +
            "      \"messages\" : [ \"Search Application is missing\" ]\n" +
            "    },\n" +
            "    \"didYouMean\" : null,\n" +
            "    \"binning\" : null,\n" +
            "    \"searchResults\" : null,\n" +
            "    \"request\" : null\n" +
            "  }";

    public static final String PDH1_PDH2_URL = "http://searchv1-dev.tcc.etn.com:8080/EatonSearchApp/EatonSearchWS";

    public static EndecaPdhResponse createEndecaPdhResponse(){
        return new Gson().fromJson(SAMPLE_RESULT, EndecaPdhResponse.class);
    }

    public static EndecaServiceRequestBean createEndecaServiceRequestBean() {
        EndecaServiceRequestBean endecaRequestBean = new EndecaServiceRequestBean();
        endecaRequestBean.setSearchApplication("eatonpdhlstcountries");
        endecaRequestBean.setSearchApplicationKey("abc123");
        endecaRequestBean.setFunction("search");
        endecaRequestBean.setSearchTerms("BR120");
        endecaRequestBean.setLanguage("##ignore##");
        endecaRequestBean.setStartingRecordNumber("0");
        endecaRequestBean.setNumberOfRecordsToReturn("10");
        return endecaRequestBean;
    }

    public static EndecaServiceRequestBean createEmptyEndecaServiceRequestBean() {
        return new EndecaServiceRequestBean();
    }
}
