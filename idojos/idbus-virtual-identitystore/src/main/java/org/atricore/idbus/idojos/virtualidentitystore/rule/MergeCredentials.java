package org.atricore.idbus.idojos.virtualidentitystore.rule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.idojos.virtualidentitystore.CredentialMappingRule;
import org.atricore.idbus.idojos.virtualidentitystore.BaseCredentialMappingRule;
import org.atricore.idbus.kernel.main.authn.Credential;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: MergeCredentials.java 1644 2010-07-27 19:31:39Z sgonzalez $
 * @org.apache.xbean.XBean element="merge-credentials"
 * <p/>
 * Create a set of virtual credentials by leaving out duplicate from source credentials.
 */
public class MergeCredentials extends BaseCredentialMappingRule implements CredentialMappingRule {

    private static final Log logger = LogFactory.getLog(MergeCredentials.class);

    public Collection<Credential> join(Collection<Credential> selectedCredentials) {
        HashSet<Credential> jointCredentialSet = new HashSet<Credential>();

        logger.debug("Joining credentials " + selectedCredentials + " with duplicate removal");

        for (Iterator<Credential> selectedCredentialsIterator = selectedCredentials.iterator(); selectedCredentialsIterator.hasNext();) {
            Credential selectedCredential = selectedCredentialsIterator.next();

            jointCredentialSet.add(selectedCredential);
        }

        return jointCredentialSet;
    }


}