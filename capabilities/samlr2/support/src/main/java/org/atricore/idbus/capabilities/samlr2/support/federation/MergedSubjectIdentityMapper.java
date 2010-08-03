package org.atricore.idbus.capabilities.samlr2.support.federation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.IdentityMapper;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * The mapped subject contains local and remote subject information
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MergedSubjectIdentityMapper implements IdentityMapper {

    private static final Log logger = LogFactory.getLog(MergedSubjectIdentityMapper.class);

    private boolean useLocalId = true;

    public boolean isUseLocalId() {
        return useLocalId;
    }

    public void setUseLocalId(boolean useLocalId) {
        this.useLocalId = useLocalId;
    }


    public Subject map(Subject remoteSubject, Subject localSubject) {

        Subject federatedSubject = null;

        Set<Principal> merged = new HashSet<Principal>();

        if (useLocalId) {
            Set<SubjectNameID> subjectNameID = localSubject.getPrincipals(SubjectNameID.class);
            // federated subject is identified using local account name identifier
            for (SubjectNameID sc : subjectNameID) {
                merged.add(sc);
            }

        } else {
            Set<SubjectNameID> subjectNameID = remoteSubject.getPrincipals(SubjectNameID.class);
            // federated subject is identified using local account name identifier
            for (SubjectNameID sc : subjectNameID) {
                merged.add(sc);
            }
        }


        for (Principal p : remoteSubject.getPrincipals()) {
            if (p instanceof SubjectNameID)
                continue;

            if (logger.isTraceEnabled())
                logger.trace("Merging IDP principal " + p);

            merged.add(p);
        }

        for (Principal p : localSubject.getPrincipals()) {
            if (p instanceof SubjectNameID)
                continue;

            if (logger.isTraceEnabled())
                logger.trace("Merging Local principal " + p);

            merged.add(p);
        }

        federatedSubject = new Subject(true, merged,
                localSubject.getPublicCredentials(),
                localSubject.getPrivateCredentials());

        if (logger.isTraceEnabled())
            logger.trace("Merged subject " + federatedSubject);

        return federatedSubject;
    }
}
