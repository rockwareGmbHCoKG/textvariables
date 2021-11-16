package de.rockware.aem.core.impl.token;

import org.apache.sling.api.resource.AbstractResourceVisitor;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class TokenResourceVisitor extends AbstractResourceVisitor {

    private static final Logger logger = getLogger(TokenResourceVisitor.class);

    private List<String> resourceTypes;

    private List<Resource> resources = new ArrayList<>();

    /**
     * Constructor.
     * @param resourceTypes list of valid resourcetypes.
     */
    public TokenResourceVisitor(List<String> resourceTypes) {
        super();
        this.resourceTypes = resourceTypes;
    }

    @Override
    protected void visit(Resource resource) {
        logger.trace("Processing resource {}", resource.getPath());
        for (String resourceType : resourceTypes) {
            if (resource.isResourceType(resourceType)) {
                logger.trace("Resource has resource type {}.", resourceType);
                resources.add(resource);
                break;
            }
        }
    }

    public List<Resource> getTokenRootResources() {
        return resources;
    }
}
