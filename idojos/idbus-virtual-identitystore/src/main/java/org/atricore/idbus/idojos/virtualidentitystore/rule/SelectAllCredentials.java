package org.atricore.idbus.idojos.virtualidentitystore.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.idojos.virtualidentitystore.CredentialMappingRule;
import org.atricore.idbus.idojos.virtualidentitystore.BaseCredentialMappingRule;
import org.atricore.idbus.kernel.main.authn.Credential;

import java.util.Collection;

/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: SelectAllCredentials.java 1644 2010-07-27 19:31:39Z sgonzalez $
 * @org.apache.xbean.XBean element="select-all-credentials"
 * <p/>
 * Selects all the whole set of credential records supplied by the configured sources.
 */
public class SelectAllCredentials extends BaseCredentialMappingRule implements CredentialMappingRule {

    private static final Log logger = LogFactory.getLog(SelectAllCredentials.class);

    public Collection<Credential> select(Collection<Credential> sourceCredentials) {
        return sourceCredentials;
    }

}