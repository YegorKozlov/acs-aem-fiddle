package com.adobe.acs.tools.fiddle.impl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.http.client.methods.HttpPost;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.spi.resource.provider.ObservationReporter;
import org.apache.sling.spi.resource.provider.ProviderContext;
import org.apache.sling.spi.resource.provider.ResolveContext;
import org.apache.sling.spi.resource.provider.ResourceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FiddleResourceProviderImplTest {

    private FiddleResourceProviderImpl resourceProvider;

    @Mock
    private ResolveContext resolveContext;

    @Mock
    private ResourceContext resourceContext;

    @Mock
    private ProviderContext providerContext;

    @Mock
    private ObservationReporter observationReporter;

    private Resource scriptResource;

    @BeforeEach
    void setUp(AemContext context) {

        resourceProvider = context.registerInjectActivateService(new FiddleResourceProviderImpl());
        resourceProvider.start(providerContext);
        scriptResource = InMemoryScript.set("jsp", "out.println('Hello')")
                .toResource(context.resourceResolver());
    }

    @AfterEach
    void tearDown() {
        InMemoryScript.clear();
    }

    @Test
    void getResource() {
        Resource res1 = resourceProvider.getResource(resolveContext, scriptResource.getPath(), resourceContext, null);
        assertNotNull(res1);
        assertTrue(scriptResource.getPath().startsWith(Constants.SCRIPT_PATH));

        Resource res2 = resourceProvider.getResource(resolveContext, "/apps/some-path", resourceContext, null);
        assertNull(res2);
    }

    @Test
    void listChildren(AemContext context) {
        Iterator<Resource> it1 = resourceProvider.listChildren(resolveContext, scriptResource.getParent());
        assertTrue(it1.hasNext());
        assertTrue(it1.next().getPath().startsWith(Constants.SCRIPT_PATH));

        context.build().resource("/content/test", "jcr:title", "resource title").commit();
        Iterator<Resource> it2 = resourceProvider.listChildren(resolveContext, context.resourceResolver().getResource("/content/test"));
        assertNull(it2);
    }

    @Test
    void refresh() {
        when(providerContext.getObservationReporter()).thenReturn(observationReporter);
        resourceProvider.refresh(scriptResource.getPath());
        verify(observationReporter, times(1)).reportChanges(any(List.class), eq(false));
    }
}
