package org.atricore.idbus.capabilities.oauth2.rserver;

import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;

/**
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface AccessTokenResolver {

    OAuth2AccessToken resolve(String token) throws OAuth2RServerException ;

}
