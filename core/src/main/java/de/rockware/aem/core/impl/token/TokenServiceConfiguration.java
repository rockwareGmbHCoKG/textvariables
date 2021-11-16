package de.rockware.aem.core.impl.token;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Token Service Configuration", description = "Service Configuration")
public @interface TokenServiceConfiguration {
    @AttributeDefinition(name = "Token Page Property Name", description = "Name of the resource property that holds the page to the token page. This property must be a child of jcr:content.")
    String getTokenPagePropertyName() default "tokenPagePath";

    @AttributeDefinition(name = "Token Key Property Name", description = "Name of the property that holds a token name.")
    String getTokenKeyName() default "tokenKey";

    @AttributeDefinition(name = "Token Value Property Name", description = "Name of the property that holds a token value.")
    String getTokenValueName() default "tokenValue";

    @AttributeDefinition(name = "Token Parent Resource Types", description = "Those resource types will be searched. Token resources need to be children of a resource with one of the resource types defined here.", cardinality = Integer.MAX_VALUE)
    String[] getAllowedResourceTypes();

    @AttributeDefinition(name = "Filter Active", description = "Check to have a servlet filter working on all text based resources and trying to replace tokens. Components do not need to call the token service manually then.")
    boolean getFilterActive() default false;

    @AttributeDefinition(name = "Content Types", description = "The servlet filter with scan data with the given content types for token keys. To have all kinds of text types scanned, enter 'text/'", cardinality = Integer.MAX_VALUE)
    String[] getAllowedContentTypes();

}
