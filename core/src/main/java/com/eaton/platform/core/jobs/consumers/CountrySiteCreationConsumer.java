/**
 *
 * @author ICF
 * On 19th Jan 2021
 * EAT- 4495
 *
 * */
package com.eaton.platform.core.jobs.consumers;

import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.services.AdminService;
import com.eaton.platform.core.services.CountryLangCodeConfigService;
import com.eaton.platform.core.util.CommonUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 *
 * Job consumer for Country site creation handler
 *
 * */
@Component(service = JobConsumer.class,
        immediate = true,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=" + CommonConstants.COUNTRY_SITE_CREATION_JOB_TOPIC,
        })
public class CountrySiteCreationConsumer implements JobConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountrySiteCreationConsumer.class);
    private Map<String, List<String>> allDomainCountryLangCodeMap = new HashMap<>();
    private LoadingCache<String, Map<String, List<String>>> countryLangCodeJobCache;

    @Reference
    private AdminService adminService;

    @Reference
    private CountryLangCodeConfigService countryLangCodeConfigService;

    @Override
    public JobResult process(Job job) {

        LOGGER.debug("CountrySiteCreationConsumer: Enters the process() method");
        final String currentPage = (String) job.getProperty(CommonConstants.CURRENT_PATH);
        final String[] currentPageArr = currentPage.split(CommonConstants.SLASH_STRING);
        final String domainName = currentPageArr[2];
        final String countryCode = currentPageArr[3];
        final String languageCode = currentPageArr[4];

        if(countryLangCodeConfigService != null) {
            List<String> domainList = countryLangCodeConfigService.getCountryLangCodesList(domainName);
            LOGGER.debug("CountrySiteCreationConsumer: Enters the try block");
            if (!domainList.isEmpty()) {
                domainList.add(countryCode.concat(CommonConstants.SLASH_STRING).concat(languageCode));
                final List<String> finalDomainList = domainList.stream()
                        .distinct()
                        .collect(Collectors.toList());
                allDomainCountryLangCodeMap = countryLangCodeConfigService.getCountryLangCodesMap(CommonConstants.EXTERNALIZER_DOMAIN_EATON);
                allDomainCountryLangCodeMap.put(domainName, finalDomainList);
                if (countryLangCodeConfigService.getCountryLangCodeCache() != null) {
                    countryLangCodeConfigService.getCountryLangCodeCache().invalidate(CommonConstants.EXTERNALIZER_DOMAIN_EATON);
                    countryLangCodeJobCache = Caffeine.newBuilder()
                            .maximumSize(countryLangCodeConfigService.getMaxCacheSize())
                            .expireAfterWrite(countryLangCodeConfigService.getMaxCacheDuration(), TimeUnit.MINUTES)
                            .build(countryLangCodes -> allDomainCountryLangCodeMap);
                    countryLangCodeConfigService.setCountryLangCodeCache(countryLangCodeJobCache);
                    return JobResult.OK;
                }
            } else {
                LOGGER.debug("CountrySiteCreationConsumer: Enters the else condition");
                try (ResourceResolver resourceResolver = adminService.getReadService()) {
                    allDomainCountryLangCodeMap.put(CommonConstants.EXTERNALIZER_DOMAIN_EATON,
                            CommonUtil.getLangCodeListFromRepo(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON));
                    allDomainCountryLangCodeMap.put(CommonConstants.EXTERNALIZER_DOMAIN_EATON_CUMMINS,
                            CommonUtil.getLangCodeListFromRepo(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_EATON_CUMMINS));
                    allDomainCountryLangCodeMap.put(CommonConstants.EXTERNALIZER_DOMAIN_GREENSWITCHING,
                            CommonUtil.getLangCodeListFromRepo(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_GREENSWITCHING));
                    allDomainCountryLangCodeMap.put(CommonConstants.EXTERNALIZER_DOMAIN_PHOENIXTEC,
                            CommonUtil.getLangCodeListFromRepo(resourceResolver, CommonConstants.EXTERNALIZER_DOMAIN_PHOENIXTEC));
                    if (countryLangCodeConfigService != null) {
                        countryLangCodeJobCache = Caffeine.newBuilder()
                                .maximumSize(countryLangCodeConfigService.getMaxCacheSize())
                                .expireAfterWrite(countryLangCodeConfigService.getMaxCacheDuration(), TimeUnit.MINUTES)
                                .build(countryLangCodes -> allDomainCountryLangCodeMap);
                        countryLangCodeConfigService.setCountryLangCodeCache(countryLangCodeJobCache);
                        return JobResult.OK;
                    }
                }
            }
        }
        return JobResult.FAILED;

    }
}
