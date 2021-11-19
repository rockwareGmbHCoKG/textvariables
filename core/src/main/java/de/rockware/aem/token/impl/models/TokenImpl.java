package de.rockware.aem.token.impl.models;

import de.rockware.aem.token.api.models.Token;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {
        Token.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class TokenImpl implements Token {

    @ValueMapValue(name = "tokenKey")
    @Default(values = "")
    @Getter
    private String tokenKey;

    @ValueMapValue(name = "tokenValue")
    @Default(values = "")
    @Getter
    private String tokenValue;

    @PostConstruct
    protected void postInit() {
        log.trace("Initializing model TokenImpl.");
    }

}
