package de.rockware.aem.token.api.caconfigs;

import java.lang.annotation.Annotation;

public class TokenConfigImpl implements TokenConfig {
    @Override
    public String variablePath() {
        return "/content/experiencepage";
    }

    @Override
    public String[] tokenComponentResourceTypes() {
        return new String[] {"textvariables/components/token", "textvariables/components/richToken"};
    }

    @Override
    public String tokenKeyPropertyName() {
        return "tokenKey";
    }

    @Override
    public String tokenValuePropertyName() {
        return "tokenValue";
    }

    @Override
    public boolean tokenReplacerActive() {
        return true;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
