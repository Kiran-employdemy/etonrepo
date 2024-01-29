package com.eaton.platform.integration.endeca.bean.crossreference;

import com.eaton.platform.core.util.CommonUtil;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class XRefResult implements Serializable {
    private static final String PROP_CROSSED_PART_BRAND = "Crossed_Part_Brand";
    private static final String PROP_CROSSED_PART_SUB_BRAND = "Crossed_Part_Sub_Brand";
    private static final String PROP_CROSSED_PART_NUMBER = "Crossed_Part_Number";
    private static final String PROP_SUB_BRAND = "Sub_Brand";
    private static final String PROP_PART_NUMBER = "Part_Number";
    private static final String PROP_PART_TYPE = "Part_Type";
    private static final String PROP_DESCRIPTION = "Description";
    private static final String PROP_IMAGE = "Image";
    private static final String PROP_REPL_PART_NUMBER = "Repl_Part_Number";
    private static final String PROP_UPSELL_SKUS = "UpSell_SKUs";
    private static final String PROP_STATUS = "Status";
    private static final String PROP_MODEL_CODE = "Model_Code";
    private static final String PROP_COMMENTS = "Comments";
    private static final String PROP_DEFAULT_VALUE = "";
    private static final String IDENTIFIER_NAME = "name";
    private static final String IDENTIFIER_VALUE = "value";
    private static final String PRIORITIZED_PART_SEPARATOR = ",";
    private static final String PRIORITIZED_PART_DIVIDER = ":";

    private final HashMap<String, String> props;

    public XRefResult(final List<LinkedHashMap<String, String>> endecaResult) {
         props = endecaResult.stream().collect(Collector.of(
                HashMap::new,
                (props1, prop) -> props1.put(prop.get(IDENTIFIER_NAME), prop.get(IDENTIFIER_VALUE)),
                (props1, props2) -> {
                    props1.putAll(props2);
                    return props1;
                }
        ));
    }

    /**
     * Given a well structured string contain a comma separated list of part numbers and priority pairs, this will return a list
     * of PrioritizedPartNumber objects.
     * @param prop A string of the format: "ABC:123,XYZ:456" where "ABC" and "XYZ" are part numbers and "123" and "456" are priorities.
     *             The string may contain any number of pairs. The priority is optional but the ":" will always be present.
     * @return A list of prioritized part numbers.
     */
    private List<PrioritizedPartNumber> getPrioritizedPartNumbers(String prop) {
        return Arrays.stream(props.getOrDefault(prop, PROP_DEFAULT_VALUE).split(PRIORITIZED_PART_SEPARATOR))
                .filter(str -> ! str.isEmpty())
                .map(str -> {
                    String[] parts = str.split(PRIORITIZED_PART_DIVIDER);
                    return new PrioritizedPartNumber(parts[0], parts.length > 1 ? parts[1] : "");
                })
                .collect(Collectors.toList());
    }

    public String getCrossedPartBrand() {
        return props.getOrDefault(PROP_CROSSED_PART_BRAND, PROP_DEFAULT_VALUE);
    }

    public String getCrossedPartSubBrand() {
        return props.getOrDefault(PROP_CROSSED_PART_SUB_BRAND, PROP_DEFAULT_VALUE);
    }

    public String getCrossedPartNumber() {
        return props.getOrDefault(PROP_CROSSED_PART_NUMBER, PROP_DEFAULT_VALUE);
    }

    public String getSubBrand() {
        return props.getOrDefault(PROP_SUB_BRAND, PROP_DEFAULT_VALUE);
    }

    public String getPartNumber() {
        return props.getOrDefault(PROP_PART_NUMBER, PROP_DEFAULT_VALUE);
    }

    public String getEncodedPartNumber() {
        return CommonUtil.encodeURLString(props.getOrDefault(PROP_PART_NUMBER, PROP_DEFAULT_VALUE));
    }

    public String getPartType() {
        return props.getOrDefault(PROP_PART_TYPE, PROP_DEFAULT_VALUE);
    }

    public String getDescription() {
        return props.getOrDefault(PROP_DESCRIPTION, PROP_DEFAULT_VALUE);
    }

    public String getImage() {
        return props.getOrDefault(PROP_IMAGE, PROP_DEFAULT_VALUE);
    }

    public List<PrioritizedPartNumber> getReplPartNumbers() {
        return getPrioritizedPartNumbers(PROP_REPL_PART_NUMBER);
    }

    public List<PrioritizedPartNumber> getUpsellSkus() {
        return getPrioritizedPartNumbers(PROP_UPSELL_SKUS);
    }

    public String getStatus() {
        return props.getOrDefault(PROP_STATUS, PROP_DEFAULT_VALUE);
    }

    public String getModelCode() {
        return props.getOrDefault(PROP_MODEL_CODE, PROP_DEFAULT_VALUE);
    }

    public String getComments() {
        return props.getOrDefault(PROP_COMMENTS, PROP_DEFAULT_VALUE);
    }

    public String getResultID() {
        return props.getOrDefault(PROP_PART_NUMBER, PROP_DEFAULT_VALUE)
                .replaceAll("[^a-zA-Z0-9_]|^\\s", "")
                .concat(props.getOrDefault(PROP_CROSSED_PART_SUB_BRAND, PROP_DEFAULT_VALUE))
                .replaceAll("[^a-zA-Z0-9_]|^\\s", "");
    }

    public class PrioritizedPartNumber {
        private final String partNumber;
        private final String priority;

        PrioritizedPartNumber(final String partNumber, final String priority) {
            this.partNumber = partNumber;
            this.priority = priority;
        }

        public String getPartNumber() {
            return partNumber;
        }

        public String getPriority() {
            return priority;
        }
    }
}
