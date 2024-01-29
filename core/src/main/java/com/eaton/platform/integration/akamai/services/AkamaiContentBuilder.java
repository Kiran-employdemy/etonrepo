package com.eaton.platform.integration.akamai.services;

import com.day.cq.replication.*;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


/**
 * Akamai content builder to create replication content containing a JSON array
 * of URLs for Akamai to purge through the Akamai Transport Handler. This class
 * takes the internal resource path and converts it to external URLs as well as
 * adding vanity URLs and pages that may Sling include the activated resource.
 */
@Component(service = ContentBuilder.class,
        property = {"name=akamai", "value=akamai"},
        immediate = true)
public class AkamaiContentBuilder implements ContentBuilder {
    @Reference
    private ResourceResolverFactory resolverFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(AkamaiContentBuilder.class);

    /** The name of the replication agent */
    public static final String NAME = "akamai";

    /**
     * The serialization type as it will display in the replication
     * agent edit dialog selection field.
     */
    public static final String TITLE = "Akamai Purge Agent";

    /**
     * {@inheritDoc}
     */
    @Override
    public ReplicationContent create(Session session, ReplicationAction action,
                                     ReplicationContentFactory factory) throws ReplicationException {
        return create(session, action, factory, null);
    }

    /**
     * Create the replication content containing the public facing URLs for
     * Akamai to purge.
     */
    @Override
    public ReplicationContent create(Session session, ReplicationAction action,
                                     ReplicationContentFactory factory, Map<String, Object> parameters)
            throws ReplicationException {

        final String path = action.getPath();
        final ReplicationLog log = action.getLog();

        PageManager pageManager;
        JSONArray jsonArray = new JSONArray();
        ResourceResolver resolver = null;

        if (StringUtils.isNotBlank(path) && StringUtils.isAsciiPrintable(path)) {
            try {
                HashMap<String, Object> sessionMap = new HashMap<>();
                sessionMap.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session);
                resolver = resolverFactory.getResourceResolver(sessionMap);
                pageManager = resolver.adaptTo(PageManager.class);
                try {
                    if (pageManager != null) {
                        Page purgedPage = pageManager.getPage(path);

                        /*
                         * Get the external URL if the resource is a page. Otherwise, use the
                         * provided resource path.
                         */
                        if (purgedPage != null) {
                            /*
                             * Use the Externalizer, Sling mappings, Resource Resolver mapping and/or
                             * string manipulation to transform "/content/my-site/foo/bar" into
                             * "https://www.my-site.com/foo/bar.html". This example assumes a custom
                             * "production" externalizer setting.
                             */
                            final String link = path;
                            jsonArray.put(link);
                            log.info("Page link added: " + link);

                            /*
                             * Add page's vanity URL if it exists.
                             */
                            final String vanityUrl = purgedPage.getVanityUrl();

                            if (StringUtils.isNotBlank(vanityUrl)) {
                                jsonArray.put(vanityUrl);
                                log.info("Vanity URL added: " + vanityUrl);
                            }
                        } else {
                            jsonArray.put(path);
                            log.info("Resource path added: " + path);
                        }

                        return createContent(factory, jsonArray);
                    }
                }catch(ReplicationException e){
                    LOGGER.error("Replication error: ", e);
                }
            } catch (LoginException e) {
                log.error("Could not retrieve Page Manager", e.getMessage());
            } finally {
                if(null != resolver && resolver.isLive()){
                    resolver.close();
                }
            }
        }

        return ReplicationContent.VOID;
    }

    /**
     * Create the replication content containing
     *
     * @param factory Factory to create replication content
     * @param jsonArray JSON array of URLS to include in replication content
     * @return replication content
     *
     * @throws ReplicationException if an error occurs
     */
    private ReplicationContent createContent(final ReplicationContentFactory factory,
                                             final JSONArray jsonArray) throws ReplicationException {

        Path tempFile;

        try {
            tempFile = Files.createTempFile("akamai_purge_agent", ".tmp");
        } catch (IOException e) {
            LOGGER.error("Could not create temporary file :: createContent Method in AkamaiContentBuilder class");
            throw new ReplicationException("Could not create temporary file", e);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
            writer.write(jsonArray.toString());
            writer.flush();

            return factory.create("text/plain", tempFile.toFile(), true);
        } catch (IOException e) {
            LOGGER.error("Could not write to temporary file in createContent method and throwing IOException");
            throw new ReplicationException("Could not write to temporary file", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@value #NAME}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@value #TITLE}
     */
    @Override
    public String getTitle() {
        return TITLE;
    }
}