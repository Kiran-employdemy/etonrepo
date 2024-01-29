package com.eaton.platform.integration.cad.impl;
import com.eaton.platform.core.constants.AEMConstants;
import com.eaton.platform.core.constants.CommonConstants;
import com.eaton.platform.core.exception.EatonApplicationException;
import com.eaton.platform.core.exception.EatonSystemException;
import com.eaton.platform.integration.cad.services.CadenasUrlService;
import com.eaton.platform.integration.cad.services.config.CadenasUrlServiceConfig;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * This is a Javadoc comment
 * CadenasUrlServiceImpl class
 */
@Designate(ocd = CadenasUrlServiceConfig.class)
@Component(service = CadenasUrlService.class, immediate = true,
        property = {
                AEMConstants.SERVICE_VENDOR_EATON,
                AEMConstants.SERVICE_DESCRIPTION + "CadenasUrlServiceImpl",
                AEMConstants.PROCESS_LABEL + "CadenasUrlServiceImpl"
        })
public class CadenasUrlServiceImpl implements CadenasUrlService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CadenasUrlServiceImpl.class);
    private String cadQualifierUrl;
    private String partCommunityUrl;

    protected CadenasUrlServiceConfig config;

    protected PoolingHttpClientConnectionManager conMgr;
    /**
     * This is a Javadoc comment
     * HttpClientBuilderFactory referenced
     */
    @Reference
    public HttpClientBuilderFactory httpFactory;
    @Activate
    @Modified
    protected final void activate(final CadenasUrlServiceConfig config) {
        if (config != null) {
            cadQualifierUrl = config.cadQualifierUrl();
            partCommunityUrl = config.partCommunityUrl();
        }
    }
    @Override
    public String getCadQualifierUrl() {
        return this.cadQualifierUrl;
    }
    @Override
    public String getPartCommunityUrl() {
        return partCommunityUrl;
    }
    /**
     * This is a Javadoc comment
     * returns xmlResponse
     */
    @Override
    public String getResponseFromCadenas(String requestURL) {
        LOGGER.error("getResponseFromCadenas::Start");
        StringBuilder builder = new StringBuilder();
        String xmlResponse;
        try {
            final HttpClient client = httpFactory.newBuilder().setConnectionTimeToLive(90, TimeUnit.SECONDS).build();
            if(requestURL != null) {
                final URIBuilder uriBuilder = new URIBuilder(requestURL);
                final URI build = uriBuilder.build();
                final HttpGet httpGet = new HttpGet(build);
                final HttpResponse execute = client.execute(httpGet);
                final int statusCode = execute.getStatusLine().getStatusCode();
                LOGGER.error("getResponseFromCadenas::statusCode::{}", statusCode);
                if (statusCode == 200) {
                    final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(execute.getEntity().getContent()));
                    String line;
                    while (null != (line = bufferedReader.readLine())) {
                        builder.append(line);
                    }
                    bufferedReader.close();
                } else {
                    builder.append(CommonConstants.EMPTY_STR);
                }
            }
        } catch (ClientProtocolException e) {
            LOGGER.error("ClientProtocolException while calling the api", e);
        } catch (IOException e) {
            LOGGER.error("IOException while calling the api", e);
        } catch (URISyntaxException e) {
            LOGGER.error("URISyntaxException while calling the api", e);
        }
        xmlResponse = builder.toString();
        LOGGER.error("getResponseFromCadenas::Ended");
        return xmlResponse;
    }
    /**
     * This method is to read CSV files from DAM
     *
     * @return file
     * @throws EatonApplicationException error
     * @throws EatonSystemException error
     */
    public File getCSVfilefromDAM(
            ResourceResolver adminResourceResolver, String filePath,
            String csvFileName) throws EatonApplicationException,
            EatonSystemException {
        LOGGER.debug("Entered into getCSVfilefromDAM method");
        File csvFile = new File(csvFileName);
        Resource res = adminResourceResolver.getResource(filePath.concat(CommonConstants.ASSET_ORIGINAL_RENDITION_PATH));
        StringWriter strWriter = new StringWriter();
        if (res != null) {
            ValueMap resvm = res.getValueMap();
            if (resvm.containsKey(CommonConstants.JCR_DATA)) {
                InputStream stream = (InputStream) resvm.get(CommonConstants.JCR_DATA);
                try (PrintWriter writer = new PrintWriter(csvFile, CommonConstants.UTF_8)) {
                    IOUtils.copy(stream, strWriter, StandardCharsets.UTF_8);
                    writer.println(strWriter.toString());
                    writer.close();
                    LOGGER.debug("CSV file successfully Found in DAM @ {}", filePath);
                } catch (IOException e) {
                    LOGGER.error("Exception Occurred while reading CSV file - {} from DAM", filePath);
                    throw new EatonApplicationException("Exception Occurred while reading XSD file from DAM", e.getMessage(), e);
                } catch (Exception e) {
                    LOGGER.error("Exception Occurred while reading CSV file - {} from DAM", filePath);
                    throw new EatonSystemException("Exception Occurred while reading CSV file from DAM ", e.getMessage(), e);
                }
            }
        } else {
            LOGGER.info("Schema File does not Exists in DAM @ {}", filePath);
            throw new EatonSystemException("Schema File does not Exists in DAM @", filePath);
        }
        LOGGER.debug("Exited from getCSVfilefromDAM method");
        return csvFile;
    }
}
