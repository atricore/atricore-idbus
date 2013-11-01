package org.atricore.idbus.idojos.virtualidentitystore;


import org.atricore.idbus.kernel.main.authn.BaseRole;

import java.util.Collection;

/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: BaseRoleMappingRule.java 1644 2010-07-27 19:31:39Z sgonzalez $
 */
public class BaseRoleMappingRule {

    public Collection<BaseRole> select(Collection<BaseRole> sourceRoles) {
        return null;
    }

    public Collection<BaseRole> join(Collection<BaseRole> selectedRoles) {
        return null;
    }

    public Collection<BaseRole> transform(Collection<BaseRole> jointRoles) {
        return null;
    }

    public void validate(Collection<BaseRole> transformedRoles) {
    }


}
