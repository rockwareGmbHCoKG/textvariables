package de.rockware.aem.token.api.caconfigs;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "Text Variable Token Config", description = "This is a configuration for text variables and tokens.")
public @interface TokenConfig {
    /**
     * @return path to the page that has keys and values.
     */
    @Property(label = "Variable Path", description = "This is the page with your variables.", property = {
            "widgetType=pathbrowser",
            "pathbrowserRootPath=/content"})
    String variablePath() default "";

    @Property(label = "Resource Type for Component", description = "Do only change if you created your own component.")
    String[] tokenComponentResourceTypes() default {"textvariables/components/token", "textvariables/components/richToken"};

    @Property(label = "Property Name for Token Key", description = "Optional. Only needed if you created your own component.")
    String tokenKeyPropertyName() default "key";

    @Property(label = "Property Name for Token Value", description = "Optional. Only needed if you created your own component.")
    String tokenValuePropertyName() default "value";

    @Property(label = "Replace Tokens?", description = "If set to false, variables will not be replaced.")
    boolean tokenReplacerActive() default false;

}
