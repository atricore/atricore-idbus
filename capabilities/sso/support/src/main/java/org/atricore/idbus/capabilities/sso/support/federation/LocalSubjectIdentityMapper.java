package org.atricore.idbus.capabilities.sso.support.federation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.IdentityMapper;
import org.atricore.idbus.kernel.main.federation.SubjectAttribute;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.SubjectRole;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Set;

/**
 *  The mapped subject contains local subject information
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LocalSubjectIdentityMapper implements IdentityMapper {

    private static final Log logger = LogFactory.getLog(LocalSubjectIdentityMapper.class);

    public Subject map(Subject remoteSubject, Subject localSubject, Set<Principal> additionalPrincipals) {
        if (additionalPrincipals != null) {
            localSubject.getPrincipals().addAll(additionalPrincipals);
        }

        // Get some "special" attributes from remote subject:
        SubjectNameID sid = null;
        for (Principal p : localSubject.getPrincipals()) {
            if (p instanceof SubjectNameID) {
                sid = (SubjectNameID) p;
            }
        }

        for (Principal p : remoteSubject.getPrincipals()) {
            if (p instanceof SubjectNameID) {
                if (sid != null)
                    continue;
                localSubject.getPrincipals().add(p);
            }

            if (logger.isTraceEnabled())
                logger.trace("Merging IDP principal " + p);

            // If Subject attribute is configured as role name attribute, also add a SubjectRole
            if (p instanceof SubjectAttribute) {
                SubjectAttribute sa = (SubjectAttribute) p;
                // Add all SP special attributes
                if (sa.getName().startsWith("urn:org:atricore:idbus:sso:sp:")) {
                    localSubject.getPrincipals().add(sa);
                }
            }

        }

        return localSubject;
    }

    public Subject map(Subject remoteSubject, Subject localSubject) {
        return map(remoteSubject, localSubject, null);
    }
}
