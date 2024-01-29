package com.eaton.platform.integration.cad.impl;

import com.eaton.platform.integration.cad.Formats;
import com.eaton.platform.integration.cad.services.CadFormatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

@Component(service = CadFormatService.class, immediate = true)
public class CadFormatServiceImpl implements CadFormatService {
    private static final Logger LOG = LoggerFactory.getLogger(CadFormatServiceImpl.class);
    private static final String NEW_LINE = "\n";
    @Reference
    private HttpClientBuilderFactory httpFactory;
    @Override
    public String readXMLFromURL(String responseURL) {
        final StringBuilder content = new StringBuilder();
        Formats tempFormats;
        String jsonOutput = null;
        try {
            final HttpClient client = httpFactory.newBuilder().setConnectionTimeToLive(90, TimeUnit.SECONDS).build();
            final URIBuilder uriBuilder = new URIBuilder(responseURL);
            final URI build = uriBuilder.build();
            final HttpGet httpGet = new HttpGet(build);
            final HttpResponse execute = client.execute(httpGet);
            final String reasonPhrase = execute.getStatusLine().getReasonPhrase();
            LOG.error("reasonPhrase::{}", reasonPhrase);
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(execute.getEntity().getContent()));
                String line;
                while (null != (line = bufferedReader.readLine()) ) {
                    content.append(line);
                }
                bufferedReader.close();
            }
        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException while calling the api", e);
        } catch (IOException e) {
            LOG.error("IOException while calling the api", e);
        } catch (URISyntaxException e) {
            LOG.error("URISyntaxException while calling the api", e);
        }
        String xmlResponse = content.toString();
        xmlResponse = xmlResponse.substring(xmlResponse.indexOf(NEW_LINE) + 1);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Formats.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            tempFormats = (Formats) unmarshaller.unmarshal(new StringReader(xmlResponse));
            if(tempFormats!= null){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                jsonOutput = gson.toJson(tempFormats);
            }
        } catch (JAXBException e) {
            LOG.info("CadFormatServiceImpl JAXB Exception::{} ", e.getMessage());
        }
        return jsonOutput;
    }
}