package de.rockware.aem.token.impl.service;

import de.rockware.aem.token.api.caconfigs.TokenConfig;
import de.rockware.aem.token.api.caconfigs.TokenConfigImpl;
import de.rockware.aem.token.api.service.TokenService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class DefaultTokenServiceTest {

    private final AemContext ctx = new AemContext();

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/token/api/resource/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        DefaultTokenService dtService = new DefaultTokenService();
        ctx.registerService(TokenService.class, dtService);
    }

    @Test
    void activate() {
        DefaultTokenService defaultService = (DefaultTokenService) ctx.getService(TokenService.class);
        defaultService.activate();
    }

    @Test
    void replaceTokens() {
        TokenService service = ctx.getService(TokenService.class);
        ctx.currentResource("/content/experiencepage");
        service.replaceTokens("abc ${testtoken1} def", null, ctx.currentResource());
        TokenConfig tConfig = new TokenConfigImpl();
        service.replaceTokens("abc ${tokenKey} def", tConfig, ctx.currentResource());
    }

    @Test
    void getTokenMap() {
        TokenService service = ctx.getService(TokenService.class);
        ctx.currentResource("/content/experiencepage");
        assertNotNull(service.getTokenMap("/"));
        assertNotNull(service.getTokenMap(""));
        assertNotNull(service.getTokenMap(null));
    }

    @Test
    void getTokenList() {
        TokenService service = ctx.getService(TokenService.class);
        ctx.currentResource("/content/experiencepage");
        assertNotNull(service.getTokenList("/"));
        assertNotNull(service.getTokenList(""));
        assertNotNull(service.getTokenList(null));
    }

    @Test
    void flushMap() {
        TokenService service = ctx.getService(TokenService.class);
        ctx.currentResource("/content/experiencepage");
        service.flushMap("/");
    }

    @Test
    void getContentTypes() {
        TokenService service = ctx.getService(TokenService.class);
        service.getContentTypes();
    }
}