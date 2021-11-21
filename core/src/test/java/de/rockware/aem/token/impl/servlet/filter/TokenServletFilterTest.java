package de.rockware.aem.token.impl.servlet.filter;

import de.rockware.aem.token.api.service.TokenService;
import de.rockware.aem.token.impl.service.DefaultTokenService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.engine.impl.filter.FilterHandle;
import org.apache.sling.engine.impl.filter.RequestSlingFilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class TokenServletFilterTest {

    private final AemContext ctx = new AemContext();

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/token/api/resource/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        DefaultTokenService dtService = new DefaultTokenService();
        ctx.registerService(TokenService.class, dtService);
    }

    @Test
    void doFilter() throws IOException, ServletException {
        TokenServletFilter filter = new TokenServletFilter();
        FilterChain chain = new RequestSlingFilterChain(null, new FilterHandle[] {});
        assertThrows(IllegalArgumentException.class, () -> {
            filter.doFilter(ctx.request(), ctx.response(), chain);
        });
    }

    @Test
    void init() {
    }

    @Test
    void destroy() {
    }
}