package de.rockware.aem.token.impl.service;

import com.drew.lang.annotations.NotNull;

import de.rockware.aem.token.api.caconfigs.TokenConfig;
import de.rockware.aem.token.api.service.TokenService;
import de.rockware.aem.token.impl.TokenContainer;
import de.rockware.aem.token.impl.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default Token Service implementation.
 */
@Component(service = TokenService.class, name = "DefaultTokenService", immediate = true)
@Slf4j
public class DefaultTokenService implements TokenService {

    private static final List<String> CONTENT_TYPES = new ArrayList<>();
    static {
        CONTENT_TYPES.add("text/");
        CONTENT_TYPES.add("application/json");
        CONTENT_TYPES.add("application/xml");
        CONTENT_TYPES.add("application/xhtml+xml");
    }

    // key is content path, value is TokenContainer object with tokens and their values.
    private Map<String, TokenContainer> tokenMaps = new HashMap<>();

    private static final Pattern PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    @Activate
    public void activate() {
        log.debug("Starting default token service.");
    }

    @Override
    public String replaceTokens(String originalText, TokenConfig tConfig, Resource resource) {
        String returnText = originalText;
        if (tConfig != null && tConfig.tokenReplacerActive() && StringUtils.isNotEmpty(tConfig.variablePath())) {
            String relevantTokenMap = getRelevantTokenPath(tConfig, resource.getResourceResolver());
            // now find and replace tokens in original text
            log.trace("Original text: {}.", originalText);
            returnText = replaceVariables(originalText, relevantTokenMap);
            log.trace("Processed text: {}.", returnText);
        } else {
            log.debug("No token replacement due to configuration settings.");
        }
        return returnText;
    }

    @Override
    public Map<String, String> getTokenMap(String contentPath) {
        if (tokenMaps.containsKey(contentPath)) {
            return tokenMaps.get(contentPath).getTokenMap();
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public List<String> getTokenList(String contentPath) {
        List<String> newList = new ArrayList<>();
        if (tokenMaps.get(contentPath) != null) {
            newList.addAll(tokenMaps.get(contentPath).getTokenMap().keySet());
        }
        return newList;
    }

    @Override
    public void flushMap(String path) {
        if (tokenMaps.containsKey(path)) {
            tokenMaps.remove(path);
            log.debug("Flushed path {}.", path);
        } else {
            log.trace("Could not flush path {}. Map did not contain matching key.", path);
        }
    }

    /**
     * Update tokenMaps Map if needed and return a list with the index of the relevant tokenMap entries for the given path.
     * @param tConfig   TokenConfiguration
     * @param resolver      resource resolver
     * @return              index
     */
    private String getRelevantTokenPath(@NotNull TokenConfig tConfig, ResourceResolver resolver) {
        String newPath = TokenUtils.cleanPath(tConfig.variablePath());
        Resource tokenResource;
        boolean tokenResourceFound = false;
        String relevantTokenPath = "";
        tokenResource = resolver.getResource(newPath);
        if (null == tokenResource) {
            log.trace("No token data found on {}.", newPath);
        } else {
            tokenResourceFound = true;
            // we check if the page that contains the token definitions has already been inspected. Because it might be referenced in the page properties of several pages.
            if (tokenMaps.containsKey(newPath)) {
                log.trace("No need to check those tokens. They are already in our map.");
            } else {
                // build a TokenContainer object with all tokens and values found beneath tmpResource.
                tokenMaps.put(newPath, TokenUtils.buildTokenContainer(tokenResource, tConfig.tokenKeyPropertyName(), tConfig.tokenValuePropertyName(),
                        new ArrayList<>(Arrays.asList(tConfig.tokenComponentResourceTypes()))));
            }
            relevantTokenPath = newPath;
        }
        if (!tokenResourceFound) {
            log.debug("Token Resource could not be found in path {}.", tConfig.variablePath());
        }
        return relevantTokenPath;
    }

    /**
     * Replace all tokens with values.
     * @param originalText          text with tokens
     * @param relevantTokenPath      relevant key for map - search in this map entry for token values.
     * @return                      text with replaced tokens
     */
    private String replaceVariables(String originalText, String relevantTokenPath) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = PATTERN.matcher(originalText);
        while (matcher.find()) {
            String replacement = "";
            String tokenKey = matcher.group(1);
            log.trace("Found key {}.", tokenKey);
            if (tokenMaps.containsKey(relevantTokenPath) && tokenMaps.get(relevantTokenPath).getTokenMap().containsKey(tokenKey)) {
                replacement = tokenMaps.get(relevantTokenPath).getTokenMap().get(tokenKey);
                log.trace("Found value {} for key {} in map {}.", replacement, tokenKey, relevantTokenPath);
                matcher.appendReplacement(buffer, replacement);
            } else {
                log.info("Key {} could not be replaced.", tokenKey);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    @Override
    public List<String> getContentTypes() {
        return CONTENT_TYPES;
    }

}
