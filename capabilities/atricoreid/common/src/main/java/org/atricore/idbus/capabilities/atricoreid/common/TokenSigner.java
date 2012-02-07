package org.atricore.idbus.capabilities.atricoreid.common;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface TokenSigner {

    String signToken(String tokenValue) throws AtricoreIDSignatureException;

    boolean isValid(String tokenValue, String tokenSignature) throws AtricoreIDSignatureException;

    String getSignAlg();
}
