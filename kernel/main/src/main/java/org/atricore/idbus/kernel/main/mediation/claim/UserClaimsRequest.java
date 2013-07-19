package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;

import java.util.Set;

/**
 */
public interface UserClaimsRequest extends ClaimsRequest {

    Object getAttribute(String key);

    void setAttribute(String key, Object value);

}
