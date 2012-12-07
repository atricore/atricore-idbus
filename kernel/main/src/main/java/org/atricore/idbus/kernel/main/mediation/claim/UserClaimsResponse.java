package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.mediation.Channel;

/**
 *
 */
public interface UserClaimsResponse {

    String getId();

    String getRelayState();

    Channel getIssuer();

    String getInResponseTo();

    UserClaimSet getAttributeSet();
}
