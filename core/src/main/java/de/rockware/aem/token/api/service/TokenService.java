package de.rockware.aem.token.api.service;

import de.rockware.aem.token.api.caconfigs.TokenConfig;
import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.Map;

/**
 * This service provides methods to read key value pairs from configurations and replace keys in strings with the matching values.
 * As it is working with config factories, different content paths could work with different key value pair locations.
 */
public interface TokenService {

    /**
     * Replace all tokens in originalText that are defined for the given contentPath and return the result String.
     * @param originalText  text with tokens
     * @param tConfig   token config - This configuration holds the path to the variables and their values.
     * @param resource  to get a valid resource resolver
     * @return  if original text is empty, null, does not contain any (replacable) tokens, original text is returned,
     *          otherwise the replaced string is returned.
     */
    String replaceTokens(String originalText, TokenConfig tConfig, Resource resource);

    /**
     * Get all the content types that the filter should process and scan for token keys.
     * @return      list with content types.
     */
    List<String> getContentTypes();
}
