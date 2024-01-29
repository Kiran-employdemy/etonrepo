package com.eaton.platform.core.replication;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Designate(ocd = FlushServicesConfigurationImpl.FlushServiceConfig.class)
@Component(service = FlushServiceConfiguration.class)
public class FlushServicesConfigurationImpl implements FlushServiceConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlushServicesConfigurationImpl.class);
    private List<Pattern> skipPaths;

    @Activate
    @Modified
    public void activate(FlushServiceConfig config) {
        String[] strings = config.skipPathsRegexp();
        skipPaths = new ArrayList<>();
        if (strings != null && strings.length > 0) {
            for (String regex : strings) {
                try {
                    Pattern pattern = Pattern.compile(regex);
                    skipPaths.add(pattern);
                } catch (PatternSyntaxException e) {
                    LOGGER.error("Invalid regexp will be skipped: {}", regex, e);
                }
            }
        }
    }

    @Override
    public boolean skipContentFlushing(String path) {
       return skipPaths.stream().anyMatch(pattern -> pattern.matcher(path).find());
    }

    @ObjectClassDefinition(description = "Flush replication agents configuration")
    public @interface FlushServiceConfig {
        String[] skipPathsRegexp() default {".*/secure($|/.*)"};
    }
}
