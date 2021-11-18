package de.rockware.aem.token.api.resource;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Helper class.
 */
@Slf4j
public class ResourceHelper {
    /**
     * Get a new resource resolver - don't forget to close it or refresh it if needed.
     *
     * @return resolver or null
     */
    public static ResourceResolver getResolver(ResourceResolverFactory resourceResolverFactory, Class clazz) {
        ResourceResolver resolver = null; //NOPMD
        try {
            resolver = resourceResolverFactory.getServiceResourceResolver(getAuthInfoMap(clazz));
        } catch (LoginException ex) {
            log.debug("Cannot get resourceresolver for class {}: {}.", clazz.getName(), ex.getMessage());
        }
        return resolver;
    }

    /**
     * Savely close a resource resolver.
     */
    public static void closeResolver(ResourceResolver resolver) {
        if (resolver.isLive()) {
            resolver.close();
        } else {
            log.info("Cannot close resolver. Resolver is not live.");
        }
    }

    public static Map<String, Object> getAuthInfoMap(Class<?> serviceClass) {
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, serviceClass.getName());
        return authInfo;
    }

}
