package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.mediation.Channel;

/**
 *
 */
public interface UserClaimsResponse extends ClaimsResponse {

    Channel getIssuer();

}
