package de.rockware.aem.token.api.resource;

import de.rockware.aem.token.impl.service.DefaultTokenService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.lenient;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class ResourceHelperTest {

    private final AemContext ctx = new AemContext();

    @Mock
    ResourceResolverFactory factory;

    @Mock
    ResourceResolver nonLiveResolver;

    @BeforeEach
    void setUp() throws LoginException {
        lenient().when(factory.getServiceResourceResolver(anyMap())).thenReturn(ctx.resourceResolver());
        lenient().when(nonLiveResolver.isLive()).thenReturn(false);
    }

    @Test
    void getResolver() {
        ResourceResolver resolver = ResourceHelper.getResolver(factory, DefaultTokenService.class);
        assertNotNull(resolver);
    }

    @Test
    void closeResolver() {
        ResourceResolver resolver = ResourceHelper.getResolver(factory, DefaultTokenService.class);
        ResourceHelper.closeResolver(resolver);
        ResourceHelper.closeResolver(nonLiveResolver);
    }

    @Test
    void getAuthInfoMap() {
        Map<String, Object> authInfo = ResourceHelper.getAuthInfoMap(DefaultTokenService.class);
        assertEquals(ResourceResolverFactory.SUBSERVICE, "sling.service.subservice");
    }
}