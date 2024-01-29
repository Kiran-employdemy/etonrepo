package com.eaton.platform.core.servlets.assets;

import com.day.cq.dam.api.jobs.AssetDownloadService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.eaton.platform.core.servlets.assets.ServletInputStreamFixtures.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetZipDownloadServletTest {

    @InjectMocks
    AssetZipDownloadServlet assetZipDownloadServlet = new AssetZipDownloadServlet();
    @Mock
    AssetDownloadService assetDownloadService;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    SlingHttpServletResponse slingHttpServletResponse;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    Resource resource1;
    @Mock
    Resource resource2;
    @Mock
    Resource resource3;

    @Mock
    ServletOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        when(slingHttpServletResponse.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    @DisplayName("When calling doPost with a list of documents is empty, empty property, nothing happens")
    void testDoPostDocumentLinksIsEmpty() throws IOException, ServletException {
        when(slingHttpServletRequest.getInputStream()).thenReturn(documentListEmptyProperty());

        assetZipDownloadServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(outputStream, never()).write(Base64.getEncoder().encodeToString("test".getBytes()).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("When calling doPost with a list of documents is null, empty property, nothing happens")
    void testDoPostDocumentLinksIsNull() throws IOException, ServletException {
        when(slingHttpServletRequest.getInputStream()).thenReturn(documentListNullProperty());

        assetZipDownloadServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(outputStream, never()).write(Base64.getEncoder().encodeToString("test".getBytes()).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("When calling doPost with a list of documents that have no special characters, the correct assets are downloaded")
    void testDoPostNoSpecialCharactersInPaths() throws IOException, ServletException {
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(slingHttpServletRequest.getInputStream()).thenReturn(documentListWithoutSpecialCharacter());
        when(resourceResolver.getResource("/path/to/asset/without/special-characters1/asset1.jpg")).thenReturn(resource1);
        when(resourceResolver.getResource("/path/to/asset/without/special-characters2/asset2.jpg")).thenReturn(resource2);
        when(resourceResolver.getResource("/path/to/asset/without/special-characters3/asset3.jpg")).thenReturn(resource3);

        when(assetDownloadService.assetDownload(any(AssetDownloadService.AssetDownloadParams.class))).thenReturn("test");

        assetZipDownloadServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(outputStream).write(Base64.getEncoder().encodeToString("test".getBytes()).getBytes(StandardCharsets.UTF_8));
    }



    @DisplayName("When calling doPost with a list of documents that have special characters, the correct assets are downloaded")
    void testDoPostSpecialCharactersInPaths() throws IOException, ServletException {
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(resourceResolver);
        when(slingHttpServletRequest.getInputStream()).thenReturn(documentListWithSpecialCharacter());
        when(resourceResolver.getResource("/path/to/asset/without/special-charácters1/ässet1.jpg")).thenReturn(resource1);
        when(resourceResolver.getResource("/path/to/asset/without/special-charactèrs2/asset2.jpg")).thenReturn(resource2);
        when(resourceResolver.getResource("/path/to/asset/without/speçial-characters3/asset3.jpg")).thenReturn(resource3);

        when(assetDownloadService.assetDownload(any(AssetDownloadService.AssetDownloadParams.class))).thenReturn("test");

        assetZipDownloadServlet.doPost(slingHttpServletRequest, slingHttpServletResponse);

        verify(outputStream).write(Base64.getEncoder().encodeToString("test".getBytes()).getBytes(StandardCharsets.UTF_8));
    }

    private void verifyClosureOfOutputStream() throws IOException {
        verify(outputStream).flush();
        verify(outputStream).close();
    }


}