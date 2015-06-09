package org.atricore.idbus.capabilities.sso.support.federation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.support.profiles.DCEPACAttributeDefinition;
import org.atricore.idbus.kernel.main.federation.IdentityMapper;
import org.atricore.idbus.kernel.main.federation.SubjectAttribute;
import org.atricore.idbus.kernel.main.federation.SubjectNameID;
import org.atricore.idbus.kernel.main.federation.SubjectRole;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 *  The mapped subject contains remote sujbect information 
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class RemoteSubjectIdentityMapper implements IdentityMapper {

    private static final Log logger = LogFactory.getLog(RemoteSubjectIdentityMapper.class);

    private boolean useLocalId = true;

    private Set<String> roleAttributeNames = new HashSet<String>();

    public RemoteSubjectIdentityMapper() {
        // TODO : This depends on the attribute profile, make it dynamic!
        roleAttributeNames.add(DCEPACAttributeDefinition.GROUPS.getValue());
        roleAttributeNames.add(DCEPACAttributeDefinition.GROUP.getValue());
        roleAttributeNames.add("groups");
    }

    public Set<String> getRoleAttributeNames() {
        return roleAttributeNames;
    }

    public void setRoleAttributeNames(Set<String> roleAttributeNames) {
        this.roleAttributeNames = roleAttributeNames;
    }

    public boolean isUseLocalId() {
        return useLocalId;
    }

    public void setUseLocalId(boolean useLocalId) {
        this.useLocalId = useLocalId;
    }

    public Subject map(Subject remoteSubject, Subject localSubject) {
        return map(remoteSubject, localSubject, null);
    }

    public Subject map(Subject remoteSubject, Subject localSubject, Set<Principal> additionalPrincipals) {

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

        Set<Principal> principals = remoteSubject.getPrincipals();
        for (Principal p : principals) {
            if (p instanceof SubjectNameID)
                continue;

            // If Subject attribute is configured as role name attribute, also add a SubjectRole
            if (p instanceof SubjectAttribute) {
                SubjectAttribute sa = (SubjectAttribute) p;
                if (roleAttributeNames.contains(sa.getName())) {
                    SubjectRole r = new SubjectRole(sa.getValue());
                    if (!merged.contains(r))
                        merged.add(r);
                }
            }

            if (!merged.contains(p))
                merged.add(p);
        }

        if (additionalPrincipals != null) {
            for (Principal p : additionalPrincipals) {
                if (p instanceof SubjectNameID)
                    continue;

                // If Subject attribute is configured as role name attribute, also add a SubjectRole
                if (p instanceof SubjectAttribute) {
                    SubjectAttribute sa = (SubjectAttribute) p;
                    if (roleAttributeNames.contains(sa.getName())) {
                        merged.add(new SubjectRole(sa.getValue()));
                    }
                }

                merged.add(p);
            }
        }

        federatedSubject = new Subject(true, merged,
                localSubject.getPublicCredentials(),
                localSubject.getPrivateCredentials());

        return federatedSubject;
    }
}
