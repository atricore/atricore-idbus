package org.atricore.idbus.kernel.main.mediation.claim;

/**
 */
public interface UserClaimsRequest extends ClaimsRequest {

    Object getAttribute(String key);

    void setAttribute(String key, Object value);

}
