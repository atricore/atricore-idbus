package org.atricore.idbus.capabilities.oauth2.rserver;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SimpleAccessTokenResolverFactory extends AccessTokenResolverFactory {

    @Override
    protected AccessTokenResolver doMakeResolver() {
        return new AccessTokenResolverImpl();
    }
}
