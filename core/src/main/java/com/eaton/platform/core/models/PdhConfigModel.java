package com.eaton.platform.core.models;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PdhConfigModel {
    private static final Logger LOG = LoggerFactory.getLogger(PdhConfigModel.class);
	public static final String CONFIG_NAME = "pdh-configurations";

    @Inject
    private Resource resource;

	@Inject
	private String[] comboAttributes;

	public List<ComboAttribute> getComboAttributes() {
         return comboAttributes != null ? Arrays.asList(comboAttributes).stream()
                 .map(comboAttribute -> new ComboAttribute(comboAttribute))
                 .collect(Collectors.toList())
                 : new ArrayList<>();
	}

	/**
	 * @param splitFieldName Either of the split field names that may or may not be present in the list of combo attributes.
	 * @return The matching combo field name or an empty optional if none is found.
	 */
	public Optional<String> getI18nKey(String splitFieldName) {
		Optional<ComboAttribute> firstMatchedAttr = getComboAttributes().stream()
				.filter(comboAttribute -> comboAttribute.matchesField(splitFieldName))
				.findFirst();

		if (firstMatchedAttr.isPresent()) {
			return firstMatchedAttr.get().getI18nKey(splitFieldName);
		} else {
			return Optional.empty();
		}
	}

	public class ComboAttribute {
        private String comboFieldName;
        private String field1Name;
        private String field1I18nKey;
        private String field2Name;
        private String field2I18nKey;

        public ComboAttribute(final String json) {
            try {
            	JsonObject attributeJson = new JsonParser().parse(json).getAsJsonObject();
                comboFieldName = attributeJson.get("comboFieldName").getAsString();
                field1Name = attributeJson.get("field1Name").getAsString();
                field1I18nKey = attributeJson.get("field1I18nKey").getAsString();
                field2Name = attributeJson.get("field2Name").getAsString();
                field2I18nKey = attributeJson.get("field2I18nKey").getAsString();
            } catch (Exception e) {
                LOG.error("Error parsing combo attribute json at: " + resource.getPath(), e);
            }
        }

        public String getComboFieldName() {
            return comboFieldName;
        }

        public String getField1Name() {
            return field1Name;
        }

        public String getField1I18nKey() {
            return field1I18nKey;
        }

        public String getField2Name() {
            return field2Name;
        }

        public String getField2I18nKey() {
            return field2I18nKey;
        }

        /**
         * @param splitFieldName A field name that may match either of the split fields of this combo attribute.
         * @return True if the provided split field name matches either of the field names in this combo attribute.
         */
        public boolean matchesField(String splitFieldName) {
            return StringUtils.equalsIgnoreCase(splitFieldName, field1Name) || StringUtils.equalsIgnoreCase(splitFieldName, field2Name);
        }

        /**
         * @param splitFieldName A field name that may match either of the split fields of this combo attribute.
         * @return The i18n key that corresponds to the provided split field name.
         */
        public Optional<String> getI18nKey(String splitFieldName) {
            if (StringUtils.equalsIgnoreCase(splitFieldName, field1Name)) {
                return Optional.ofNullable(field1I18nKey);
            } else if (StringUtils.equalsIgnoreCase(splitFieldName, field2Name)) {
                return Optional.ofNullable(field2I18nKey);
            } else {
                return Optional.empty();
            }
        }
    }
}