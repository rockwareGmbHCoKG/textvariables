package de.rockware.aem.core.impl.token.service;

import com.day.crx.JcrConstants;
import de.rockware.aem.core.api.caconfigs.TokenConfig;
import de.rockware.aem.core.api.token.service.TokenService;
import de.rockware.aem.core.impl.token.TokenContainer;
import de.rockware.aem.core.impl.token.TokenServiceConfiguration;
import de.rockware.aem.core.impl.token.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default Token Service implementation.
 */
@Component(service = TokenService.class, name = "DefaultTokenService", immediate = true)
@Designate(ocd = TokenServiceConfiguration.class)
@Slf4j
public class DefaultTokenService implements TokenService {

    private TokenServiceConfiguration configuration;

    // key is content path, value is TokenContainer object with tokens and their values.
    private Map<String, TokenContainer> tokenMaps = new HashMap<>();

    private static final Pattern PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    @Activate
    public void activate(TokenServiceConfiguration serviceConfiguration) {
        log.debug("Starting default token service.");
        this.configuration = serviceConfiguration;
    }

    @Override
    public String replaceTokens(String originalText, TokenConfig tConfig, Resource resource) {
        String returnText = originalText;
        if (tConfig != null && StringUtils.isNotEmpty(tConfig.variablePath())) {
            String relevantTokenMap = getRelevantTokenPath(tConfig.variablePath(), resource.getResourceResolver());
            // now find and replace tokens in original text
            log.trace("Original text: {}.", originalText);
            returnText = computeReplacedText(originalText, relevantTokenMap);
            log.trace("Processed text: {}.", returnText);
            // TODO: remove OSGi config(?)
            // TODO: what should be configurable?
        }
        return returnText;
    }

    @Override
    public Map<String, String> getTokenMap(String contentPath) {
        return tokenMaps.get(contentPath).getTokenMap();
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

    @Override
    public boolean getFilterActive() {
        return configuration.getFilterActive();
    }

    /**
     * Update tokenMaps Map if needed and return a list with the index of the relevant tokenMap entries for the given path.
     * @param contentPath   content path to process
     * @param resolver      resource resolver
     * @return              index
     */
    private String getRelevantTokenPath(String contentPath, ResourceResolver resolver) {
        String newPath = TokenUtils.cleanPath(contentPath);
        Resource tokenResource;
        boolean tokenResourceFound = false;
        String relevantTokenPath = "";
        tokenResource = resolver.getResource(newPath);
        if (null == tokenResource) {
            log.trace("No token data found on {}.", newPath + "/" + JcrConstants.JCR_CONTENT + "/" + configuration.getTokenPagePropertyName());
        } else {
            tokenResourceFound = true;
            // we check if the page that contains the token definitions has already been inspected. Because it might be referenced in the page properties of several pages.
            if (tokenMaps.containsKey(newPath)) {
                log.trace("No need to check those tokens. They are already in our map.");
            } else {
                // build a TokenContainer object with all tokens and values found beneath tmpResource.
                tokenMaps.put(newPath, TokenUtils.buildTokenContainer(tokenResource, configuration.getTokenKeyName(), configuration.getTokenValueName(),
                        new ArrayList<>(Arrays.asList(configuration.getAllowedResourceTypes()))));
            }
            relevantTokenPath = newPath;
        }
        if (!tokenResourceFound) {
            log.debug("Token Resource could not be found in path {}.", contentPath);
        }
        return relevantTokenPath;
    }

    /**
     * Replcae all tokens with values.
     * @param originalText          text with tokens
     * @param relevantTokenPath      relevant key for map - search in this map entry for token values.
     * @return                      text with replaced tokens
     */
    private String computeReplacedText(String originalText, String relevantTokenPath) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = PATTERN.matcher(originalText);
        while (matcher.find()) {
            String replacement = "";
            String tokenKey = matcher.group(1);
            log.trace("Found key {}.", tokenKey);
            boolean valueFound = false;
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
        return new ArrayList<>(Arrays.asList(configuration.getAllowedContentTypes()));
    }

}
