package com.eaton.platform.integration.endeca.bean.crossreference;

import com.eaton.platform.integration.endeca.bean.FacetGroupBean;
import com.eaton.platform.integration.endeca.bean.FacetValueBean;
import com.eaton.platform.integration.endeca.bean.FacetsBean;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CrossReferenceResponseBean implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(CrossReferenceResponseBean.class);
    public static final String VALUE = "value";
    private static final String TITLE = "title";
    private static final String NAME = "name";
    private static final String VALUES = "values";
    private static final String ID = "id";
    private static final String COUNT = "count";


    @JsonProperty("response")
    private CrossReferenceBean response;

    public CrossReferenceBean getResponse() {
        return response;
    }


    public JsonArray getFacetGroupListJson() {
        final JsonArray facetGroupListJson = new JsonArray();
        if (null != response) {
            FacetsBean binning = response.getBinning();
            List<FacetGroupBean> facetGroupList = binning.getFacetGroupList();
            if (facetGroupList != null) {
                facetGroupList.forEach(facetGroupBean -> {
                    facetGroupListJson.add(getFiltersConfig(facetGroupBean.getFacetGroupLabel(),
                            facetGroupBean.getFacetGroupId(), facetGroupBean.getFacetValueList()));
                });
            }
        }
        return facetGroupListJson;
    }

    private JsonObject getFiltersConfig(final String title, final String name, final List<FacetValueBean> values) {
        final JsonObject filterConfig = new JsonObject();
        final JsonArray valueJson = new JsonArray();

        try {
            values.forEach(value -> valueJson.add(getFilterItem(value)));

            filterConfig.add(VALUES, valueJson);
            filterConfig.add(NAME, new Gson().toJsonTree(name));
            filterConfig.add(VALUE, new Gson().toJsonTree(name));
            filterConfig.add(ID, new Gson().toJsonTree(name));
            filterConfig.add(TITLE, new Gson().toJsonTree(title));
        } catch (Exception e) {
            LOG.error("JSON Exception while forming service JSON object", e);
        }

        return filterConfig;
    }

    private JsonObject getFilterItem(final FacetValueBean value) {
        final JsonObject serviceObject = new JsonObject();
        try {
            serviceObject.add(TITLE, new Gson().toJsonTree(value.getFacetValueLabel()));
            serviceObject.add(NAME, new Gson().toJsonTree(value.getFacetValueId()));
            serviceObject.add(ID, new Gson().toJsonTree(value.getFacetValueId()));
            serviceObject.add(COUNT, new Gson().toJsonTree(value.getFacetValueDocs()));
            serviceObject.add(VALUE, new Gson().toJsonTree(value.getFacetValueLabel()));
            serviceObject.add("active", new Gson().toJsonTree(value.getActiveRadioButton() != null && "true".equals(value.getActiveRadioButton())));
        } catch (Exception e) {
            LOG.error("JSON Exception while forming service JSON object", e);
        }
        return serviceObject;
    }

}
