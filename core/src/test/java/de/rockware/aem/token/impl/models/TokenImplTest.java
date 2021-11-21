package de.rockware.aem.token.impl.models;

import de.rockware.aem.token.api.models.Token;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class TokenImplTest {

    private final AemContext ctx = new AemContext();

    @BeforeEach
    void setUp() {
        ctx.addModelsForClasses(TokenImpl.class);
        ctx.load().json("/de/rockware/aem/token/impl/models/TokenImplTest.json", "/content");
    }

    @Test
    void postInit() {
        ctx.currentResource("/content/token");
        Token token = ctx.request().adaptTo(Token.class);
        assertNotNull(token);
    }

    @Test
    void getTokenKey() {
        ctx.currentResource("/content/token");
        Token token = ctx.request().adaptTo(Token.class);
        assertEquals(token.getTokenKey(), "testtoken1");
    }

    @Test
    void getTokenValue() {
        ctx.currentResource("/content/token");
        Token token = ctx.request().adaptTo(Token.class);
        assertEquals(token.getTokenValue(), "This is MY value, yeah!");
    }
}