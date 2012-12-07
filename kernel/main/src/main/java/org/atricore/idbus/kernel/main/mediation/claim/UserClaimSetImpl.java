package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.mediation.claim.UserClaim;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaimSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 */
public class UserClaimSetImpl implements UserClaimSet {

    private List<UserClaim> attrs = new ArrayList<UserClaim>();

    public void addAttribute(UserClaim attr) {
        attrs.add(attr);
    }

    public Collection<UserClaim> getAttributes() {
        return Collections.unmodifiableCollection(attrs);
    }

    @Override
    public String toString() {
        return super.toString() + "["+ attrs +"]";
    }
}
