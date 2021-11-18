package de.rockware.aem.token.impl.servlet.filter;

import de.rockware.aem.token.api.caconfigs.TokenConfig;
import de.rockware.aem.token.api.service.TokenService;
import de.rockware.aem.token.impl.servlet.TokenResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(
        configurationPid = "de.rockware.aem.core.impl.token.servlet.filter.TokenServletFilter",
        service = { Filter.class },
        property = {
                "sling.filter.scope=request",
                "sling.filter.pattern=/content/(.*)",
                "service.ranking:Integer=" + Integer.MAX_VALUE,
        }
)
@Slf4j
public class TokenServletFilter implements Filter {

    @Reference
    TokenService tokenService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
        TokenResponseWrapper wrapper = new TokenResponseWrapper((HttpServletResponse) response);
        filterChain.doFilter(request, wrapper);
        boolean touched = false;
        if (request instanceof SlingHttpServletRequest && isValidContentType(response)) {
            long startTime = System.currentTimeMillis();
            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
            Resource resource = slingRequest.getResource();
            TokenConfig tConfig = getConfig(resource);
            if (tConfig != null && tConfig.tokenReplacerActive()) {
                String content = null;
                if(wrapper.getResponseAsString() != null) {
                    content	= wrapper.getResponseAsString();
                }
                String replacedContent = tokenService.replaceTokens(content, tConfig, resource);
                log.trace("Replaced content. New response: {}.", replacedContent);
                response.getWriter().write(replacedContent);
                response.getWriter().close();
                touched = true;
                log.debug("Processing time: {} ms.", System.currentTimeMillis() - startTime);
            }
        } else {
            log.debug("Request is not a SlingHttpServletRequest or content type {} is not valid.", response.getContentType());
        }
        if (!touched) {
            response.getOutputStream().write(wrapper.getResponseAsBytes());
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    /**
     * Check if response has the right content type.
     * @param response  response object
     * @return          true if correct - we do not process digital files
     */
    private boolean isValidContentType(ServletResponse response) {
        String contentType = response.getContentType();
        boolean returnValue = false;
        if (StringUtils.isNotEmpty(contentType)) {
            for (String configEntry : tokenService.getContentTypes()) {
                if (StringUtils.startsWith(contentType, configEntry)) {
                    returnValue = true;
                    log.trace("Content type {} is valid because of config entry {}.", contentType, configEntry);
                    break;
                }
            }
        }
        if (!returnValue) {
            log.trace("Content type {} is not valid. Return false.", contentType);
        }
        return returnValue;
    }

    /**
     * Get the token configuration.
     * @param resource  current resource
     * @return  token config
     */
    private TokenConfig getConfig(Resource resource) {
        TokenConfig tConfig = null;
        if (resource != null) {
            ConfigurationBuilder cBuilder = resource.adaptTo(ConfigurationBuilder.class);
            if (cBuilder != null) {
                tConfig = cBuilder.as(TokenConfig.class);
            }
        }
        return tConfig;
    }
}
