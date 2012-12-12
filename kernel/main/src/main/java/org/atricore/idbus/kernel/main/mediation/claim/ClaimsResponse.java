package org.atricore.idbus.kernel.main.mediation.claim;

import java.io.Serializable;

/**
 */
public interface ClaimsResponse extends Serializable {

    String getId();

    String getRelayState();

    String getInResponseTo();

    ClaimSet getClaimSet();

}
