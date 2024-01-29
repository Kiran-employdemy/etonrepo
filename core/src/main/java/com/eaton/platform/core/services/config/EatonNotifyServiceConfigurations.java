package com.eaton.platform.core.services.config;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@ObjectClassDefinition(name = "Eaton auto rollout/publish to country configurations")
public @interface EatonNotifyServiceConfigurations {

    @AttributeDefinition(
            name = "Choose any language",
            description = "if you choose Default then it will notify to default group",
            options = {
                    @Option(label = "default", value = "Default"),
                    @Option(label = "/cn/zh-cn", value = "Chinese(China)"),
                    @Option(label = "/cs/cz-cs", value = "Czech(China)"),
                    @Option(label = "/dk/da-dk", value = "Danish(Denmark)"),
                    @Option(label = "/nl/nl-nl", value = "Dutch(Netherlands)"),
                    @Option(label = "/be/nl-nl", value = "Dutch(Belgium)"),
                    @Option(label = "/lb/en-gb", value = "English(Lebanon)"),
                    @Option(label = "/tn/en-gb", value = "English(Tunisia)"),
                    @Option(label = "/fr/en-gb", value = "English(France)"),
                    @Option(label = "/hr/en-gb", value = "English(Croatia)"),
                    @Option(label = "/tz/en-gb", value = "English(Tanzania)"),
                    @Option(label = "/ch/en-gb", value = "English(Switzerland)"),
                    @Option(label = "/bg/en-gb", value = "English(Bulgaria)"),
                    @Option(label = "/ie/en-gb", value = "English(Ireland)"),
                    @Option(label = "/pt/en-gb", value = "English(Portugal)"),
                    @Option(label = "/ke/en-gb", value = "English(Kenya)"),
                    @Option(label = "/at/en-gb", value = "English(Austria)"),
                    @Option(label = "/cd/en-gb", value = "English(Congo - Kinshasa)"),
                    @Option(label = "/bw/en-gb", value = "English(Botswana)"),
                    @Option(label = "/sk/en-gb", value = "English(Slovakia)"),
                    @Option(label = "/lt/en-gb", value = "English(Lithuania)"),
                    @Option(label = "/se/en-gb", value = "English(Sweden)"),
                    @Option(label = "/et/en-gb", value = "English(Ethiopia)"),
                    @Option(label = "/it/en-gb", value = "English(Italy)"),
                    @Option(label = "/nl/en-gb", value = "English(Netherlands)"),
                    @Option(label = "/dk/en-gb", value = "English(Denmark)"),
                    @Option(label = "/ug/en-gb", value = "English(Uganda)"),
                    @Option(label = "/mz/en-gb", value = "English(Mozambique)"),
                    @Option(label = "/sa/en-gb", value = "English(Saudi Arabia)"),
                    @Option(label = "/es/en-gb", value = "English(Spain)"),
                    @Option(label = "/tr/en-gb", value = "English(Turkey)"),
                    @Option(label = "/ee/en-gb", value = "English(Estonia)"),
                    @Option(label = "/na/en-gb", value = "English(Namibia)"),
                    @Option(label = "/pl/en-gb", value = "English(Poland)"),
                    @Option(label = "/ma/en-gb", value = "English(Morocco)"),
                    @Option(label = "/by/en-gb", value = "English(Belarus)"),
                    @Option(label = "/cm/en-gb", value = "English(Cameroon)"),
                    @Option(label = "/de/en-gb", value = "English(Germany)"),
                    @Option(label = "/qa/en-gb", value = "English(Qatar)"),
                    @Option(label = "/ae/en-gb", value = "English(United Arab Emirates)"),
                    @Option(label = "/ng/en-gb", value = "English(Nigeria)"),
                    @Option(label = "/cs/en-gb", value = "English(Czech Republic)"),
                    @Option(label = "/be/en-gb", value = "English(Belgium)"),
                    @Option(label = "/ua/en-gb", value = "English(Ukraine)"),
                    @Option(label = "/rs/en-gb", value = "English(Serbia)"),
                    @Option(label = "/il/en-gb", value = "English(Israel)"),
                    @Option(label = "/om/en-gb", value = "English(Oman)"),
                    @Option(label = "/cf/en-gb", value = "English(Central African Republic)"),
                    @Option(label = "/zm/en-gb", value = "English(Zambia)"),
                    @Option(label = "/ne/en-gb", value = "English(Niger)"),
                    @Option(label = "/za/en-gb", value = "English(South Africa)"),
                    @Option(label = "/hu/en-gb", value = "English(Hungary)"),
                    @Option(label = "/ga/en-gb", value = "English(Gabon)"),
                    @Option(label = "/gr/en-gb", value = "English(Greece)"),
                    @Option(label = "/sc/en-gb", value = "English(Seychelles)"),
                    @Option(label = "/eg/en-gb", value = "English(Egypt)"),
                    @Option(label = "/sz/en-gb", value = "English(Swaziland)"),
                    @Option(label = "/zw/en-gb", value = "English(Zimbabwe)"),
                    @Option(label = "/mg/en-gb", value = "English(Madagascar)"),
                    @Option(label = "/gb/en-gb", value = "English(United Kingdom)"),
                    @Option(label = "/dj/en-gb", value = "English(Djibouti)"),
                    @Option(label = "/rw/en-gb", value = "English(Rwanda)"),
                    @Option(label = "/cg/en-gb", value = "English(Congo - Brazzaville)"),
                    @Option(label = "/re/en-gb", value = "English(Reunion)"),
                    @Option(label = "/kw/en-gb", value = "English(Kuwait)"),
                    @Option(label = "/no/en-gb", value = "English(Norway)"),
                    @Option(label = "/ru/en-gb", value = "English(Russia)"),
                    @Option(label = "/mw/en-gb", value = "English(Malawi)"),
                    @Option(label = "/ro/en-gb", value = "English(Romania)"),
                    @Option(label = "/gh/en-gb", value = "English(Ghana)"),
                    @Option(label = "/bh/en-gb", value = "English(Bahrain)"),
                    @Option(label = "/sd/en-gb", value = "English(Sudan)"),
                    @Option(label = "/mu/en-gb", value = "English(Mauritius)"),
                    @Option(label = "/lu/en-gb", value = "English(Luxembourg)"),
                    @Option(label = "/jo/en-gb", value = "English(Jordan)"),
                    @Option(label = "/ao/en-gb", value = "English(Angola)"),
                    @Option(label = "/lv/en-gb", value = "English(Latvia)"),
                    @Option(label = "/fi/en-gb", value = "English(Finland)"),
                    @Option(label = "/ee/ee-et", value = "Estonia(Estonian)"),
                    @Option(label = "/fi/fi-fi", value = "Finnish(Finland)"),
                    @Option(label = "/ca/fr-ca", value = "French(Canada)"),
                    @Option(label = "/fr/fr-fr", value = "French(France)"),
                    @Option(label = "/ma/fr-fr", value = "French(Morocco)"),
                    @Option(label = "/be/fr-fr", value = "French(Belgium)"),
                    @Option(label = "/at/de-de", value = "German(Austria)"),
                    @Option(label = "/ch/de-de", value = "German(Switzerland)"),
                    @Option(label = "/de/de-de", value = "German(Germany)"),
                    @Option(label = "/il/he-il", value = "Hebrew(Israel)"),
                    @Option(label = "/hu/hu-hu", value = "Hungarian(Hungary)"),
                    @Option(label = "/it/it-it", value = "Italian(Italy)"),
                    @Option(label = "/lv/lv-lv", value = "Latvian(Latvia)"),
                    @Option(label = "/lt/lt-lt", value = "Lithuanian(Lithuania)"),
                    @Option(label = "/no/no-no", value = "Norwegian(Norway)"),
                    @Option(label = "/pl/pl-pl", value = "Polish(Poland)"),
                    @Option(label = "/br/pt-br", value = "Portuguese(Brazil)"),
                    @Option(label = "/pt/pt-pt", value = "Portuguese(Portugal)"),
                    @Option(label = "/ro/ro-ro", value = "Romanian(Romania)"),
                    @Option(label = "/ru/ru-ru", value = "Russian(Russia)"),
                    @Option(label = "/es/es-es", value = "Spanish(Spain)"),
                    @Option(label = "/se/sv-se", value = "Swedish(Sweden)"),
                    @Option(label = "/tr/tr-tr", value = "Turkish(Turkey)"),
                    @Option(label = "/ua/ua-uk", value = "Ukrainian(Ukraine)"),
            }
    )
    String languageWithCountry() default StringUtils.EMPTY;

    @AttributeDefinition( name = "Rollout Email Notification Group",description = "Sending notification for each country site rollout notification group" )
    String rolloutEmailNotificationGroup() default StringUtils.EMPTY;

    @AttributeDefinition(name = "Publish Email Notification Group",description = "Sending notification for each country site publish notification group")
    String publishEmailNotificationGroup() default StringUtils.EMPTY;

    @AttributeDefinition( name = "Configure Email Template for Rollout" )
    String emailTemplateRollout() default "/apps/eaton/settings/wcm/designs/email_template/en/autoContentRolloutEmailNotificationTemplate.html";

    @AttributeDefinition(name = "Configure Email Template for Publish")
    String emailTemplatePublish() default "/apps/eaton/settings/wcm/designs/email_template/en/autoContentPublishEmailNotificationTemplate.html";

}
