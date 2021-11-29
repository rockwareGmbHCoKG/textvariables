package de.rockware.aem.token.impl.servlet.filter;

import de.rockware.aem.token.api.service.TokenService;
import de.rockware.aem.token.impl.service.DefaultTokenService;
import de.rockware.aem.token.impl.servlet.TokenResponseWrapper;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.util.PrivateAccessor;

import org.apache.sling.engine.impl.filter.FilterHandle;
import org.apache.sling.engine.impl.filter.RequestSlingFilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

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
    void doFilterNull() throws IOException, ServletException {
        TokenServletFilter filter = new TokenServletFilter();
        filter.doFilter(ctx.request(), ctx.response(), null);
    }

    @Test
    void init() {
        TokenServletFilter filter = new TokenServletFilter();
        filter.init(null);
        filter.destroy();
    }

    @Test
    void process() throws IOException, NoSuchFieldException {
        DefaultTokenService dtService = new DefaultTokenService();
        TokenServletFilter filter = new TokenServletFilter();
        PrivateAccessor.setField(filter, "tokenService", dtService);
        filter.process(ctx.request(), ctx.response(), new TokenResponseWrapper((HttpServletResponse) ctx.response()));
        ctx.response().setContentType("application/json");
        filter.process(ctx.request(), ctx.response(), new TokenResponseWrapper((HttpServletResponse) ctx.response()));
    }

}