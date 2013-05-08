package org.atricore.idbus.capabilities.sso.support.federation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.IdentityMapper;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Set;

/**
 *  The mapped subject contains local sujbect information
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LocalSubjectIdentityMapper implements IdentityMapper {

    private static final Log logger = LogFactory.getLog(LocalSubjectIdentityMapper.class);

    public Subject map(Subject remoteSubject, Subject localSubject, Set<Principal> additionalPrincipals) {
        if (additionalPrincipals != null) {
            // TODO : Subject may be read-only
            localSubject.getPrincipals().addAll(additionalPrincipals);
        }
        return localSubject;
    }

    public Subject map(Subject remoteSubject, Subject localSubject) {
        return map(remoteSubject, localSubject, null);
    }
}
