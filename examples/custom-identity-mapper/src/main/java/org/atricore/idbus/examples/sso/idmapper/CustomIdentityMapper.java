package org.atricore.idbus.examples.sso.idmapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.IdentityMapper;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.SubjectRole;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sgonzalez on 3/12/15.
 */
public class CustomIdentityMapper implements IdentityMapper {

    private static final Log logger = LogFactory.getLog(CustomIdentityMapper.class);

    private String customCfg;

    @Override
    public Subject map(Subject remoteSubject, Subject localSubject) {
        return map(remoteSubject, localSubject, null);
    }

    /**
     * Create a new federated subject based on the remote Subject, and the local subject (if any).
     *
     * @param remoteSubject the subject information received from the IdP.
     * @param localSubject the local subject retrieved from the Identity Source
     * @param additionalPrincipals a set of principals that must be added to the final subject
     *
     * @return the federated subject
     */
    @Override
    public Subject map(Subject remoteSubject, Subject localSubject, Set<Principal> additionalPrincipals) {

        Subject federatedSubject = null;

        SubjectNameID id = null;
        Set<SubjectNameID> localIds = localSubject.getPrincipals(SubjectNameID.class);
        if (localIds.size() == 1) {
            id = localIds.iterator().next();
        }

        if (id == null) {
            Set<SubjectNameID> idpIds = remoteSubject.getPrincipals(SubjectNameID.class);
            if (idpIds.size() == 1) {
                id = idpIds.iterator().next();
            }
        }

        Set<Principal> merged = new HashSet<Principal>();
        merged.add(id);

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

        // Automatically assigned roles
        List<SubjectRole> roles = new ArrayList<SubjectRole>();
        roles.add(new SubjectRole("role1"));
        merged.addAll(roles);

        // Create federated subject
        federatedSubject = new Subject(true, merged,
                localSubject.getPublicCredentials(),
                localSubject.getPrivateCredentials());

        if (logger.isDebugEnabled())
            logger.debug("Merged subject " + federatedSubject);

        return federatedSubject;
    }

    public String getCustomCfg() {
        return customCfg;
    }

    public void setCustomCfg(String customCfg) {
        this.customCfg = customCfg;
    }
}
