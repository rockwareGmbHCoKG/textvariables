package de.rockware.aem.token.api.models;

/**
 * A token has a key and a value.
 */
public interface Token {
    /**
     * Returns the token key {@link String} element.
     *
     * @return {@link String} token key
     */
    default String getTokenKey() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the token value {@link String} element.
     *
     * @return {@link String} token value
     */
    default String getTokenValue() {
        throw new UnsupportedOperationException();
    }
}
