package com.eaton.platform.core.replication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;

import com.day.cq.replication.ContentBuilder;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationContent;
import com.day.cq.replication.ReplicationContentFactory;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationLog;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.eaton.platform.core.constants.CommonConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Vanish content builder to create replication content containing a JSON array
 * of URLs for Vanish to purge through the Vanish Transport Handler. This class
 * takes the internal resource path and converts it to external URLs as well as
 * adding vanity URLs and pages that may Sling include the activated resource.
 */
@Component(service = ContentBuilder.class,immediate = true,
property = {"name=varnish"})
public class VarnishContentBuilder implements ContentBuilder {

    @Reference
    private ResourceResolverFactory resolverFactory;

    /** The name of the replication agent */
    public static final String NAME = "varnish";

    /**
     * The serialization type as it will display in the replication
     * agent edit dialog selection field.
     */
    public static final String TITLE = "Varnish Purge Agent";

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
     * Vanish to purge.
     */
    @Override
    public ReplicationContent create(Session session, ReplicationAction action,
                                     ReplicationContentFactory factory, Map<String, Object> parameters)
            throws ReplicationException {

        final String path = action.getPath();
        final ReplicationLog log = action.getLog();

        ResourceResolver resolver = null;
        PageManager pageManager = null;
        JSONArray jsonArray = new JSONArray();

        if (StringUtils.isNotBlank(path)) {
            try {
                HashMap<String, Object> sessionMap = new HashMap<>();
                sessionMap.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session);
                resolver = resolverFactory.getResourceResolver(sessionMap);

                if (resolver != null) {
                    pageManager = resolver.adaptTo(PageManager.class);
                }
            } catch (LoginException e) {
                log.error("Could not retrieve Page Manager", e);
            }

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
                    //Externalizer externalizer = resolver.adaptTo(Externalizer.class);
                    //final String link = externalizer.externalLink(resolver,CommonConstants.EXTERNALIZER_DOMAIN_EATON, path);
                    final String link = path;
                    jsonArray.put(link);
                    log.info("Page link added: " + link);

                    if(link.contains(CommonConstants.SHORT_URL_REMOVAL_PART)) {
                        /* Also cover for short URL form as well */
                        final String shortLink = link.replace(CommonConstants.SHORT_URL_REMOVAL_PART, "/");
                        jsonArray.put(shortLink);
                        log.info("Short form Content URL link added: " + shortLink);
                    }
                    /*
                     * Add page's vanity URL if it exists.
                     */
                    final String vanityUrl = purgedPage.getVanityUrl();

                    if (StringUtils.isNotBlank(vanityUrl)) {
                        jsonArray.put(vanityUrl);
                        log.info("Vanity URL added: " + vanityUrl);
                    }

                    /*
                     * Get containing pages that includes the resource.
                     */
                    // Run project specific query

                } else {
						jsonArray.put(path);
						log.info("Resource path added: " + path);
                }

                return createContent(factory, jsonArray);
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
            tempFile = Files.createTempFile("varnish_purge_agent", ".tmp");
        } catch (IOException e) {
            throw new ReplicationException("Could not create temporary file", e);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(tempFile, Charset.forName("UTF-8"))) {
            writer.write(jsonArray.toString());
            writer.flush();

            return factory.create("text/plain", tempFile.toFile(), true);
        } catch (IOException e) {
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
