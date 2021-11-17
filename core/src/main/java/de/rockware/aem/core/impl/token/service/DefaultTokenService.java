package de.rockware.aem.core.impl.token.service;

import com.day.crx.JcrConstants;
import de.rockware.aem.core.api.caconfigs.TokenConfig;
import de.rockware.aem.core.api.resource.ResourceHelper;
import de.rockware.aem.core.api.token.service.TokenService;
import de.rockware.aem.core.impl.token.TokenContainer;
import de.rockware.aem.core.impl.token.TokenServiceConfiguration;
import de.rockware.aem.core.impl.token.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
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

    @Reference
    ResourceResolverFactory resolverFactory;

    @Activate
    public void activate(TokenServiceConfiguration serviceConfiguration) {
        log.debug("Starting default token service.");
        this.configuration = serviceConfiguration;
    }

    @Override
    public String replaceTokens(String originalText, String contentPath) {
        String returnText = originalText;

        // TODO: tokenpageproperty name no longer from OSGi.
        // TODO: instead -> read path to token config page from CaConfig
        if (StringUtils.isNotEmpty(configuration.getTokenPagePropertyName())) {
            ResourceResolver resolver = ResourceHelper.getResolver(resolverFactory, this.getClass());
            String newPath = TokenUtils.cleanPath(contentPath);
            Resource tmpResource;
            boolean tokenResourceFound = false;
            // get all tokenMaps keys that reference TokenContainer objects with possible data.
            List<String> relevantTokenMaps = getRelevantTokenMapsList(contentPath, resolver);
            // now find and replace tokens in original text
            log.trace("Original text: {}.", originalText);
            returnText = computeReplacedText(originalText, relevantTokenMaps);
            log.trace("Processed text: {}.", returnText);
            ResourceHelper.closeResolver(resolver);
        } else {
            log.info("Cannot replace tokens. OSGi configuration for token service has no value.");
        }
        return returnText;
    }

    @Override
    public String replaceTokens(String originalText, TokenConfig tConfig, Resource resource) {
        String returnText = originalText;
        if (tConfig != null && StringUtils.isNotEmpty(tConfig.variablePath())) {
            // TODO: get token map
            // TODO: replace tokens
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
     * Extract path to tokenResource from page properties and return the token resource.
     * @param currentResourcePath   path of resource that needs to be checked.
     * @param resolver              resource resolver
     * @return                      the resource with the token definitions or null.
     */
    private Resource getTokenResource(String currentResourcePath, ResourceResolver resolver) {
        Resource tokenResource = null;
        Resource tmpResource = resolver.getResource(currentResourcePath + "/" + JcrConstants.JCR_CONTENT);
        if (null != tmpResource) {
            ValueMap tmpMap = tmpResource.getValueMap();
            String path = tmpMap.get(configuration.getTokenPagePropertyName(), String.class);
            tokenResource = StringUtils.isNotEmpty(path) ? resolver.getResource(path) : null;
        }
        return tokenResource;
    }

    /**
     * Update tokenMaps Map if needed and return a list with all indexes of all relevant tokenMaps entries for the given path.
     * First entry has highest priority.
     * @param contentPath   content path to process
     * @param resolver      resource resolver
     * @return              list with indexes
     */
    private List<String> getRelevantTokenMapsList(String contentPath, ResourceResolver resolver) {
        String newPath = TokenUtils.cleanPath(contentPath);
        Resource tokenResource;
        boolean tokenResourceFound = false;
        List<String> relevantTokenMaps = new ArrayList<>();
        // iterate ancestors until we are on level 2 to get all available token definitions.
        while (StringUtils.isNotEmpty(newPath) && newPath.startsWith("/content/")) {
            tokenResource = getTokenResource(newPath, resolver);
            if (null == tokenResource) {
                log.trace("No token data found on {}. Checking parent.", newPath + "/" + JcrConstants.JCR_CONTENT + "/" + configuration.getTokenPagePropertyName());
            } else {
                tokenResourceFound = true;
                // we check if the page that contains the token definitions has already been inspected. Because it might be referenced in the page properties of several pages.
                if (tokenMaps.containsKey(tokenResource.getPath())) {
                    log.trace("No need to check those tokens. They are already in our map.");
                } else {
                    // build a TokenContainer object with all tokens and values found beneath tmpResource.
                    tokenMaps.put(tokenResource.getPath(), TokenUtils.buildTokenContainer(tokenResource, configuration.getTokenKeyName(), configuration.getTokenValueName(),
                            new ArrayList<>(Arrays.asList(configuration.getAllowedResourceTypes()))));
                }
                relevantTokenMaps.add(tokenResource.getPath());
            }
            // see if there is more on parent side
            int lastSlashIndex = StringUtils.lastIndexOf(newPath, '/');
            newPath = StringUtils.substring(newPath, 0, lastSlashIndex);
        }
        if (!tokenResourceFound) {
            log.debug("Token Resource could not be found in path {}.", contentPath);
        }
        return relevantTokenMaps;
    }

    /**
     * Replcae all tokens with values.
     * @param originalText          text with tokens
     * @param relevantTokenMaps     list of relevant keys for map - search in those map entries for token values.
     * @return                      text with replaced tokens
     */
    private String computeReplacedText(String originalText, List<String> relevantTokenMaps) {
        StringBuffer buffer = new StringBuffer();
        Matcher matcher = PATTERN.matcher(originalText);
        while (matcher.find()) {
            String replacement = "";
            String tokenKey = matcher.group(1);
            log.trace("Found key {}.", tokenKey);
            boolean valueFound = false;
            for (String path : relevantTokenMaps) {
                if (tokenMaps.containsKey(path) && tokenMaps.get(path).getTokenMap().containsKey(tokenKey)) {
                    replacement = tokenMaps.get(path).getTokenMap().get(tokenKey);
                    log.trace("Found value {} for key {} in map {}.", replacement, tokenKey, path);
                    valueFound = true;
                    break;
                }
            }
            if (!valueFound) {
                log.info("Key {} could not be replaced.", tokenKey);
            } else {
                matcher.appendReplacement(buffer, replacement);
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
