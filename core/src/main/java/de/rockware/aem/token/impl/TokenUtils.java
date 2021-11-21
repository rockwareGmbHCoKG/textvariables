package de.rockware.aem.token.impl;

import com.day.crx.JcrConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.query.Query;
import java.util.*;

/**
 * Utility methods to deal with tokens and their values.
 */
@Slf4j
public final class TokenUtils {

    /**
     * Default constructor.
     */
    private TokenUtils() {
        // nothing to do
    }

    /**
     * Get page resource of current resource if possible. Should be either the resource itself or an ancestor.
     * @param oldPath   the old path
     * @return new path (remove jcr:content and more
     */
    public static String cleanPath(String oldPath) {
        String path = oldPath == null ? "" : oldPath;
        int idx = path.lastIndexOf("/jcr:content");
        if (idx >= 0) {
            path = path.substring(0, idx);
        } else {
            idx = path.indexOf("/jcr:frozenNode");
            if (idx >= 0) {
                path = path.substring(0, idx);
            }
        }
        return path;
    }

    /**
     * Build a TokenContainer object with all the tokens found under the given resource.
     * The resource needs to be a jcr:content resource or needs to have a child with name jcr:content.
     * Then the whole subtree is scanned for nodes with one of the allowed resource types. All tokens found in any of those nodes will be added to the token data.
     * @param resource              Page resource. One of the descendant resources needs to be checked for tokens
     * @param tokenKeyName          Key name
     * @param tokenValueName        value name
     * @param allowedResourceTypes  list with allowed resource types
     * @return          TokenContainer.
     */
    public static TokenContainer buildTokenContainer(Resource resource, String tokenKeyName, String tokenValueName, List<String> allowedResourceTypes) {
        Map<String, String> tokenData = new HashMap<>();
        Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);

        String queryString = "SELECT * FROM [nt:unstructured] AS token WHERE (ISDESCENDANTNODE(token, '" + contentResource.getPath() + "')";
        boolean firstEntry = true;
        for (String resourceType : allowedResourceTypes) {
            queryString += (firstEntry ? " AND (" : " OR ") + "token.[sling:resourceType] = '" + resourceType + "'";
            firstEntry = false;
        }
        if (!firstEntry) {
            queryString += ")";
        }
        queryString += ")";
        log.debug("Query String: {}", queryString);
        Iterator<Resource> resourceIterator = resource.getResourceResolver().findResources(queryString, Query.JCR_SQL2);
        while (resourceIterator.hasNext()) {
            Resource result = resourceIterator.next();
            ValueMap valueMap = result.getValueMap();
            String tokenName = valueMap.get(tokenKeyName, String.class);
            String tokenValue = valueMap.get(tokenValueName, String.class);
            // token name must always be set
            // token value can be empty if the editor clears the field (because he really wants an EMPTY replacement.
            if (StringUtils.isNotEmpty(tokenName)) {
                log.trace("Writing tokenName {} to map.", tokenName);
                tokenData.put(tokenName, tokenValue);
            }
        }

        TokenContainer tokenContainer = new TokenContainer(resource.getPath());
        tokenContainer.setTokens(tokenData);
        return tokenContainer;
    }


}
