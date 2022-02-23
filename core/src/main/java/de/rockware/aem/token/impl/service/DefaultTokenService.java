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

    private static final Pattern PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    @Activate
    public void activate() {
        log.debug("Starting default token service.");
    }

    @Override
    public String replaceTokens(String originalText, TokenConfig tConfig, Resource resource) {
        String returnText = originalText;
        if (tConfig != null && tConfig.tokenReplacerActive() && StringUtils.isNotEmpty(tConfig.variablePath())) {
            TokenContainer tokenContainer = getTokenContainer(tConfig, resource.getResourceResolver());
            // now find and replace tokens in original text
            log.trace("Original text: {}.", originalText);
            if (tokenContainer != null) {
                returnText = replaceVariables(originalText, tokenContainer);
                log.trace("Processed text: {}.", returnText);
            } else {
                log.info("TokenContainer is null. No processing.");
            }
        } else {
            log.debug("No token replacement due to configuration settings.");
        }
        return returnText;
    }

    /**
     * Return a container with the the relevant tokens for the given path.
     * @param tConfig   TokenConfiguration
     * @param resolver      resource resolver
     * @return              token container
     */
    private TokenContainer getTokenContainer(@NotNull TokenConfig tConfig, ResourceResolver resolver) {
        TokenContainer container = null;
        String newPath = TokenUtils.cleanPath(tConfig.variablePath());
        Resource tokenResource;
        boolean tokenResourceFound = false;
        tokenResource = resolver.getResource(newPath);
        if (null == tokenResource) {
            log.trace("No token data found on {}.", newPath);
        } else {
            tokenResourceFound = true;
            // build a TokenContainer object with all tokens and values found beneath tmpResource.
            container = TokenUtils.buildTokenContainer(tokenResource, tConfig.tokenKeyPropertyName(), tConfig.tokenValuePropertyName(),
                        new ArrayList<>(Arrays.asList(tConfig.tokenComponentResourceTypes())));
        }
        if (!tokenResourceFound) {
            log.debug("Token Resource could not be found in path {}.", tConfig.variablePath());
        }
        return container;
    }

    /**
     * Replace all tokens with values.
     * @param originalText          text with tokens
     * @param tokenContainer        the container with all tokens
     * @return                      text with replaced tokens
     */
    private String replaceVariables(String originalText, TokenContainer tokenContainer) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = PATTERN.matcher(originalText);
        while (matcher.find()) {
            String replacement = "";
            String tokenKey = matcher.group(1);
            log.trace("Found key {}.", tokenKey);
            if (tokenContainer.getTokenMap().containsKey(tokenKey)) {
                replacement = tokenContainer.getTokenMap().get(tokenKey);
                log.trace("Found value {} for key {}.", replacement, tokenKey);
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
