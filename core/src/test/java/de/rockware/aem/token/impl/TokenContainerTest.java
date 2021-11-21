package de.rockware.aem.token.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TokenContainerTest {

    private TokenContainer container;

    @BeforeEach
    void setUp() {
        container = new TokenContainer("/");
    }

    @Test
    void getContentPath() {
        assertEquals(container.getContentPath(), "/");
    }

    @Test
    void getTokenMap() {
    }

    @Test
    void hasToken() {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("one", "oneValue");
        tokenMap.put("two", "noneValue");
        container.setTokens(tokenMap);
        assertTrue(container.hasToken("one"));
        assertEquals(container.getTokenValue("one"), "oneValue");
        assertNotNull(container.getTokenMap());
    }

    @Test
    void getTokenValueNull() {
        Map<String, String> tokenMap = null;
        container.setTokens(tokenMap);
        assertFalse(container.hasToken("one"));
    }
}