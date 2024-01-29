package com.eaton.platform.core.models;

import java.util.ArrayList;
import java.util.List;
import com.eaton.platform.core.bean.TopicLinkWithIconBean;
import com.eaton.platform.core.util.CommonUtil;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;

import org.apache.sling.models.annotations.Model;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static com.eaton.platform.core.constants.CommonConstants.TOPIC_LIST_WITH_ICON_TITLE;
import static com.eaton.platform.core.constants.CommonConstants.TOPIC_LIST_WITH_ICON_DESCRIPTION;
import static com.eaton.platform.core.constants.CommonConstants.TOPIC_LIST_WITH_ICON_ICON;
import static com.eaton.platform.core.constants.CommonConstants.TOPIC_LIST_WITH_ICON_LINK;
import static com.eaton.platform.core.constants.CommonConstants.TOPIC_LIST_WITH_ICON_NEW_WINDOW;
import static com.eaton.platform.core.constants.CommonConstants.TARGET_BLANK;
import static com.eaton.platform.core.constants.CommonConstants.TARGET_SELF;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TopicLinksModel {
    private Resource resource;

    public TopicLinksModel(Resource resource) {
        this.resource = resource;
    }

	/**
     * Provides the list of topic links for the given country. A topic link is a grouping of
     * information that comes either from PDH or from the authored values on the PIM node. A PIM
     * node is one of the nodes underneath "/var/commerce/products/eaton"
     *
     * @param country A string representation of the country to create the list for. Different
     *                countries will have different lists of topic links based on how the PIM
     *                node was authored.
     * @return The list of topic links relevant to the given country based on the given resource.
     */
    public List<TopicLinkWithIconBean> forCountry(String country) {
        List<TopicLinkWithIconBean> links = new ArrayList<>();

        resource.getChildren().forEach(linkResource -> {
            ValueMap properties = linkResource.getValueMap();
            String[] authoredCountries = CommonUtil.getAuthoredCountries(properties);

            if (CommonUtil.countryMatches(country, authoredCountries)) {
                TopicLinkWithIconBean topicLinkWithIcon = new TopicLinkWithIconBean();

                topicLinkWithIcon.setTitle(properties.get(TOPIC_LIST_WITH_ICON_TITLE, EMPTY));
                topicLinkWithIcon.setDescription(properties.get(TOPIC_LIST_WITH_ICON_DESCRIPTION, EMPTY));
                topicLinkWithIcon.setIcon(properties.get(TOPIC_LIST_WITH_ICON_ICON, EMPTY));
                topicLinkWithIcon.setLink(properties.get(TOPIC_LIST_WITH_ICON_LINK, EMPTY));
                topicLinkWithIcon.setNewWindow(!properties.get(TOPIC_LIST_WITH_ICON_NEW_WINDOW, EMPTY).equals(EMPTY)
                        ? TARGET_BLANK : TARGET_SELF);

                links.add(topicLinkWithIcon);
            }
        });

        return links;
    }
}
