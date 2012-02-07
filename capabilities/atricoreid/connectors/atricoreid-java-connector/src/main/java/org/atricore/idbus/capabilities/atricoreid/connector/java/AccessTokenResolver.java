package org.atricore.idbus.capabilities.atricoreid.connector.java;

import org.atricore.idbus.capabilities.atricoreid.common.AtricoreIDAccessToken;

/**
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface AccessTokenResolver {

    AtricoreIDAccessToken resolve(String token) throws AtricoreIDRServerException ;

}
