package org.atricore.idbus.capabilities.oauth2.common;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface TokenSigner {

    String signToken(String tokenValue) throws OAuth2SignatureException;

    boolean isValid(String tokenValue, String tokenSignature) throws OAuth2SignatureException;

    String getSignAlg();
}
