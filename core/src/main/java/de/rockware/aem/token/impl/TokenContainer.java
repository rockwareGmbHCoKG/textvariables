package de.rockware.aem.token.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Tokens and values in a map.
 */
public class TokenContainer {

    private Map<String, String> map = new HashMap<>();

    private String contentPath;

    /**
     * Constructor.
     * @param contentPath path of the page resource that holds the token data.
     */
    public TokenContainer(String contentPath) {
        this.contentPath = contentPath;
    }

    /**
     * Set the map with tokens and their values.
     * @param map   new map
     */
    public void setTokens(Map<String, String> map) {
        this.map = map;
    }

    /**
     * Get the content path where the tokens have been read from.
     * @return  content path
     */
    public String getContentPath() {
        return contentPath;
    }

    /**
     * Get the complete token map.
     * @return  map with tokens and values.
     */
    public Map<String, String> getTokenMap() {
        return Collections.unmodifiableMap(map);
    }

    /**
     * Check if a token is available.
     * @param token token to check
     * @return  true if available, false else.
     */
    public boolean hasToken(String token) {
        return map.containsKey(token);
    }

    /**
     * Get token value or empty string if token is not available or null or empty.
     * @param token token to check
     * @return  value from map, or empty string if token is not availa
     */
    public String getTokenValue(String token) {
        return map.getOrDefault(token, "");
    }
}
