package de.rockware.aem.token.impl;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class TokenUtilsTest {

    private final AemContext ctx = new AemContext();

    @BeforeEach
    void setUp() {
    }

    @Test
    void cleanPath() {
        String path1 = "/content/wow/jcr:content";
        String path2 = "/content/wow/jcr:frozenNode";
        String path3 = "/content/wow/subpage";
        String path4 = "";
        String path5 = null;
        assertEquals(TokenUtils.cleanPath(path1), "/content/wow");
        assertEquals(TokenUtils.cleanPath(path2), "/content/wow");
        assertEquals(TokenUtils.cleanPath(path3), path3);
        assertEquals(TokenUtils.cleanPath(path4), path4);
        assertEquals(TokenUtils.cleanPath(path5), "");
    }

    @Test
    void buildTokenContainer() {
        ctx.load().json("/de/rockware/aem/token/api/resource/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        Resource resource = ctx.request().getResource();
        List<String> allowedResourceTypes = new ArrayList<>();
        allowedResourceTypes.add("textvariables/components/token");
        allowedResourceTypes.add("textvariables/components/richtoken");
        TokenContainer container = TokenUtils.buildTokenContainer(resource, "tokenKey", "tokenValue", allowedResourceTypes);
    }
}