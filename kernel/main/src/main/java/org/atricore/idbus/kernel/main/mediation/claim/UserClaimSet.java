package org.atricore.idbus.kernel.main.mediation.claim;

import java.io.Serializable;
import java.util.Collection;

/**
 */
public interface UserClaimSet extends Serializable {

    void addAttribute(UserClaim attr);

    Collection<UserClaim> getAttributes();

}
