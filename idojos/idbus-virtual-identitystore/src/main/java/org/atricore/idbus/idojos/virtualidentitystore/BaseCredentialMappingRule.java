package org.atricore.idbus.idojos.virtualidentitystore;


import org.atricore.idbus.kernel.main.authn.Credential;

import java.util.Collection;

/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: BaseCredentialMappingRule.java 1644 2010-07-27 19:31:39Z sgonzalez $
 */
public class BaseCredentialMappingRule {

    public Collection<Credential> select(Collection<Credential> sourceCredentials) {
        return null;
    }

    public Collection<Credential> join(Collection<Credential> selectedCredentials) {
        return null;
    }

    public Collection<Credential> transform(Collection<Credential> jointCredentials) {
        return null;
    }

    public void validate(Collection<Credential> transformedCredentials) {

    }


}
