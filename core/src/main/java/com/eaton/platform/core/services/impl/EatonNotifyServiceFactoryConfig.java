package com.eaton.platform.core.services.impl;

import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.services.config.EatonNotifyServiceConfigurations;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Runnable.class,immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "EatonNotifyServiceFactoryConfig",
                AEMConstants.PROCESS_LABEL + "EatonNotifyServiceFactoryConfig"
        })
@Designate(ocd = EatonNotifyServiceConfigurations.class,factory = true)
public class EatonNotifyServiceFactoryConfig implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(EatonNotifyServiceFactoryConfig.class);
    public static final String ROLLOUT_NOTIFICATION_GROUP = "rolloutEmailNotificationGroup";
    public static final String LANGUAGE_WITH_COUNTRY = "languageWithCountry";
    public static final String PUBLISH_NOTIFICATION_GROUP = "publishEmailNotificationGroup";
    public static final String EMAIL_TEMPLATE_ROLL_OUT = "emailTemplateRollout";
    public static final String EMAIL_TEMPLATE_PUBLISH = "emailTemplatePublish";
    String languageWithCountry;
    String rolloutEmailNotificationGroup;
    String publishEmailNotificationGroup;
    String emailTemplateRollout;
    String emailTemplatePublish;

    @Activate
    protected void activate(final EatonNotifyServiceConfigurations config) {
        try {
            this.languageWithCountry = config.languageWithCountry();
            this.emailTemplatePublish = config.emailTemplatePublish();
            this.emailTemplateRollout = config.emailTemplateRollout();
            this.publishEmailNotificationGroup = config.publishEmailNotificationGroup();
            this.rolloutEmailNotificationGroup = config.rolloutEmailNotificationGroup();
        } catch (Exception e) {
            LOGGER.error("APIConfig Exception : Error while activating service :: "+e.getMessage(),e);
        }
    }
    public void run()
    {
    }

    public String getLanguageWithCountry() {
        return languageWithCountry;
    }

    public String getRolloutEmailNotificationGroup() {
        return rolloutEmailNotificationGroup;
    }

    public String getPublishEmailNotificationGroup() {
        return publishEmailNotificationGroup;
    }

    public String getEmailTemplateRollout() {
        return emailTemplateRollout;
    }

    public String getEmailTemplatePublish() {
        return emailTemplatePublish;
    }
}