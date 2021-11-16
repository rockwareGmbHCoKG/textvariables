package de.rockware.aem.core.impl.token;

import com.day.crx.JcrConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Utility methods to deal with tokens and their values.
 */
public final class TokenUtils {

    private static final Logger logger = getLogger(TokenUtils.class);

    /**
     * Default constructor.
     */
    private TokenUtils() {
        // nothing to do
    }

    /**
     * Iterate subresources to find resources that match one of the resourceTypes in the list.
     * @param contentResource   Should represent a page or a jcr:content resource
     * @param resourceTypes     list with resource types
     * @return                  a list of resource found that match any of the given resource types
     */
    public static List<Resource> getTokenRootResources(Resource contentResource, List<String> resourceTypes) {
        List<Resource> resources = new ArrayList<>();
        Resource currentResource = contentResource;
        if (null != currentResource && !currentResource.getPath().contains(JcrConstants.JCR_CONTENT)) {
            currentResource = currentResource.getChild(JcrConstants.JCR_CONTENT);
        }
        if (null != currentResource) {
            TokenResourceVisitor visitor = new TokenResourceVisitor(resourceTypes);
            visitor.accept(currentResource);
            resources =  visitor.getTokenRootResources();
        } else {
            logger.info("Could not find a resource or content resource.");
        }
        return resources;
    }

    /**
     * Get page resource of current resource if possible. Should be either the resource itself or an ancestor.
     * @param oldPath   the old path
     * @return new path (remove jcr:content and more
     */
    public static String cleanPath(String oldPath) {
        String path = oldPath;
        int idx = path.lastIndexOf("/jcr:content");
        if (idx >= 0) {
            path = path.substring(0, idx);
        }
        else {
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
        List<Resource> tokenRootResourceList = getTokenRootResources(contentResource,  new ArrayList<>(allowedResourceTypes));
        for (Resource tokenRootResource : tokenRootResourceList) {
            logger.debug("Checking resource {}.", tokenRootResource.getPath());
            Resource currentResource = tokenRootResource.getChild("tokendata");
            currentResource = currentResource == null ? tokenRootResource : currentResource;
            for (Resource tokenSubResource : currentResource.getChildren()) {
                ValueMap valueMap = tokenSubResource.getValueMap();
                String tokenName = valueMap.get(tokenKeyName, String.class);
                String tokenValue = valueMap.get(tokenValueName, String.class);
                // token name must always be set
                // token value can be empty if the editor clears the field (because he really wants an EMPTY replacement.
                if (StringUtils.isNotEmpty(tokenName)) {
                    logger.trace("Writing tokenName {} to map.", tokenName);
                    tokenData.put(tokenName, tokenValue);
                }
            }
        }

        TokenContainer tokenContainer = new TokenContainer(resource.getPath());
        tokenContainer.setTokens(tokenData);
        return tokenContainer;
    }


}
