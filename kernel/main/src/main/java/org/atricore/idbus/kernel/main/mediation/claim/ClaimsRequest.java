package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.io.Serializable;

/**
 */
public interface ClaimsRequest extends Serializable {

    String getId();

    String getRelayState();

    String getLastErrorId();

    String getLastErrorMsg();

    Channel getIssuerChannel();

    IdentityMediationEndpoint getIssuerEndpoint();


}
