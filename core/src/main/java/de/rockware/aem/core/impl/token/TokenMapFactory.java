package de.rockware.aem.core.impl.token;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates token maps with values from the repository.
 */
public class TokenMapFactory {

    /*Private constructor, which is not accessible from other class*/
    private TokenMapFactory(){

    }
		
    /**
     * Create a token map with the available tokens and values.
     * @param contentPath               content page path where the token data can be found
     * @param tokenRootResourcePath     absolute path to the resource that has the tokendata.
     * @param resourceResolver          resource resolver
     * @return                          map with tokens and values.
     */
    public static TokenContainer getTokenMap(String contentPath, String tokenRootResourcePath, ResourceResolver resourceResolver) {
        Map<String, String> data = new HashMap<>();
        Resource tokenRootResource = resourceResolver.getResource(tokenRootResourcePath);
        if (null != tokenRootResource) {
            ValueMap valueMap = tokenRootResource.getValueMap();
            for (String key : valueMap.keySet()) {
                data.put(key, valueMap.get(key, String.class));
            }
        }
        TokenContainer tokenContainer = new TokenContainer(contentPath);
        tokenContainer.setTokens(data);
        return tokenContainer;
    }
}
