package de.rockware.aem.core.api.caconfigs;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration(label = "Text Variable Token Config", description = "This is a configuration for text variables and tokens.")
public @interface TokenConfig {
    /**
     * @return path to the current homepage.
     */
    @Property(label = "Variable Path", description = "This is the page with your variables.", property = {
            "widgetType=pathbrowser",
            "pathbrowserRootPath=/content"})
    String variablePath() default "";

}
