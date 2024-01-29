package com.eaton.platform.core.servlets;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import com.eaton.platform.core.services.PageAssetReferenceService;


@ExtendWith({MockitoExtension.class})
class ReferencedAssetsServletTest {

	@InjectMocks
    ReferencedAssetsServlet referencedAssetsServlet = new ReferencedAssetsServlet();

    @Mock
    ResourceResolver mockResourceResolver;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @Mock
    Resource mockResource;

    @Mock
    Node mockNode;

    @Mock
    PrintWriter writer;

    @Mock
    PageAssetReferenceService pageAssetReferenceService;
    private static final String DOWNLOAD_LIST_TYPE = "eaton/components/general/download-link-list";
    private static final String RESOURCE_LIST_TYPE = "eaton/components/product/resource-list";

    @Test
    public void testDoGet() throws Exception {
        List<AssetDetails> pageAssetList = new LinkedList<>();
        pageAssetList.add(new AssetDetails("asset1", "/content/dam/products", "image/jpg", "component1", "123", "us", "en", "tab1"));
        pageAssetList.add(new AssetDetails("asset2", "/content/dam/products", "image/png", "component2", "456", "uk", "fr", "tab2"));
        List<AssetDetails> tagAssetList = new LinkedList<>();
        tagAssetList.add(new AssetDetails("asset3", "/content/dam/tags", "image/gif", "component3", "789", "ca", "es", "tab3"));
        tagAssetList.add(new AssetDetails("asset4", "/content/dam/tags", "image/jpeg", "component4", "101", "au", "de", "tab4"));
        List<AssetDetails> resourceListTagAssetList = new LinkedList<>();
        resourceListTagAssetList.add(new AssetDetails("asset5", "/content/dam/tags", "image/gif", "component5", "788", "in", "hn", "tab5"));
        resourceListTagAssetList.add(new AssetDetails("asset6", "/content/dam/tags", "image/jpeg", "component6", "102", "hu", "es", "tab6"));
        when(slingHttpServletRequest.getResourceResolver()).thenReturn(mockResourceResolver);
        when(slingHttpServletRequest.getResource()).thenReturn(mockResource);
        when(mockResource.adaptTo(Node.class)).thenReturn(mockNode);
        when(pageAssetReferenceService.fetchAllPageAsset(mockNode, slingHttpServletRequest)).thenReturn(pageAssetList);
        when(pageAssetReferenceService.fetchAllTagedAssets(slingHttpServletRequest, mockResourceResolver,DOWNLOAD_LIST_TYPE)).thenReturn(tagAssetList);
        when(pageAssetReferenceService.fetchAllTagedAssets(slingHttpServletRequest, mockResourceResolver,RESOURCE_LIST_TYPE)).thenReturn(resourceListTagAssetList);
        StringWriter stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(slingHttpServletResponse.getWriter()).thenReturn(writer);
        referencedAssetsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
        Assertions.assertEquals(2, pageAssetList.size(), "should be 2");
        Assertions.assertEquals(2, tagAssetList.size(), "should be 2");
        Assertions.assertEquals(2, resourceListTagAssetList.size(), "should be 2");
    }

    @Test
    public void testIfCurrentNodeIsNull() throws Exception {
        String EMPTY_JSON = "{}";
        when(slingHttpServletRequest.getResource()).thenReturn(mockResource);
        when(mockResource.adaptTo(Node.class)).thenReturn(null);
        when(slingHttpServletResponse.getWriter()).thenReturn(writer);
        referencedAssetsServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
        verify(writer).write(EMPTY_JSON);
        verify(slingHttpServletResponse).setContentType("application/json");
        verify(slingHttpServletResponse).setCharacterEncoding("UTF-8");
        assertNull(mockResource.adaptTo(Node.class));
        Assertions.assertEquals(slingHttpServletResponse.getWriter(), writer);
    }

}
