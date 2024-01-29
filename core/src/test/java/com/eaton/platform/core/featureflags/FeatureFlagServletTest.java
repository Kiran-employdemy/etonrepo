package com.eaton.platform.core.featureflags;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeatureFlagServletTest {

    @InjectMocks
    FeatureFlagServlet featureFlagServlet = new FeatureFlagServlet();
    @Mock
    FeatureFlagRegistryService featureFlagRegistryService;
    @Mock
    SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    SlingHttpServletResponse slingHttpServletResponse;
    @Mock
    Resource featureFlagDialogResource;
    @Mock
    Resource featureFlagParentResource;
    @Mock
    Resource feature1;
    @Mock
    Resource feature2;

    @Test
    void testDoGetNoParent() throws ServletException, IOException {
        when(slingHttpServletRequest.getResource()).thenReturn(featureFlagDialogResource);
        when(featureFlagDialogResource.getParent()).thenReturn(null);
        featureFlagServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
        verify(slingHttpServletRequest).setAttribute(ArgumentMatchers.eq(DataSource.class.getName()), ArgumentMatchers.argThat(dataSource -> !((SimpleDataSource)dataSource).iterator().hasNext()));
    }

    @Test
    void testDoGetParent() throws ServletException, IOException {
        when(slingHttpServletRequest.getResource()).thenReturn(featureFlagDialogResource);
        when(featureFlagDialogResource.getParent()).thenReturn(featureFlagParentResource);
        when(featureFlagParentResource.getPath()).thenReturn("/mnt/override/apps/eaton/components/secure/advanced-search/cq:dialog/content/items/columns/items/column2/items/tabs/items/config/items/experimental-features/items/column/items");
        when(featureFlagRegistryService.getExperimentalFeaturesFor("eaton/components/secure/advanced-search", slingHttpServletRequest)).thenReturn(Arrays.asList(feature1, feature2));
        featureFlagServlet.doGet(slingHttpServletRequest, slingHttpServletResponse);
        verify(slingHttpServletRequest).setAttribute(ArgumentMatchers.eq(DataSource.class.getName()), ArgumentMatchers.argThat(dataSource -> {
            Iterator<Resource> iterator = ((SimpleDataSource) dataSource).iterator();
            return iterator.next().equals(feature1) && iterator.next().equals(feature2);
        }));
    }
}