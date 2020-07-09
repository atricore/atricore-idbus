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
 * The mapped subject contains local and remote subject information
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MergedSubjectIdentityMapper implements IdentityMapper {

    private static final Log logger = LogFactory.getLog(MergedSubjectIdentityMapper.class);

    private boolean useLocalId = true;

    private Set<String> roleAttributeNames = new HashSet<String>();

    public MergedSubjectIdentityMapper() {
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
        return true;
    } // TODO : FIX

    public void setUseLocalId(boolean useLocalId) {
        this.useLocalId = useLocalId;
    }

    public Subject map(Subject remoteSubject, Subject localSubject) {
        return map(remoteSubject, localSubject, null);
    }

    public Subject map(Subject remoteSubject, Subject localSubject, Set<Principal> additionalPrincipals) {

        Subject federatedSubject = null;

        Set<Principal> merged = new HashSet<Principal>();

        if (isUseLocalId()) {
            Set<SubjectNameID> subjectNameID = localSubject.getPrincipals(SubjectNameID.class);
            // federated subject is identified using local account name identifier
            for (SubjectNameID sc : subjectNameID) {

                if (logger.isTraceEnabled())
                    logger.trace("Using local subject ID : " + sc.getName());

                merged.add(sc);
            }

        } else {
            Set<SubjectNameID> subjectNameID = remoteSubject.getPrincipals(SubjectNameID.class);
            // federated subject is identified using local account name identifier
            for (SubjectNameID sc : subjectNameID) {

                if (logger.isTraceEnabled())
                    logger.trace("Using remote subject ID : " + sc.getName());

                merged.add(sc);
            }
        }


        for (Principal p : remoteSubject.getPrincipals()) {
            if (p instanceof SubjectNameID)
                continue;

            if (logger.isTraceEnabled())
                logger.trace("Merging IDP principal " + p);

            // If Subject attribute is configured as role name attribute, also add a SubjectRole
            if (p instanceof SubjectAttribute) {
                SubjectAttribute sa = (SubjectAttribute) p;
                if (roleAttributeNames.contains(sa.getName())) {
                    merged.add(new SubjectRole(sa.getValue()));
                }
            }

            merged.add(p);
        }

        for (Principal p : localSubject.getPrincipals()) {
            if (p instanceof SubjectNameID)
                continue;

            if (logger.isTraceEnabled())
                logger.trace("Merging Local principal " + p);

            if (!merged.contains (p))
                merged.add(p);
        }

        if (additionalPrincipals != null) {
            for (Principal p : additionalPrincipals) {
                if (p instanceof SubjectNameID)
                    continue;

                if (logger.isTraceEnabled())
                    logger.trace("Merging Additional principal " + p);

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

        }

        /*
        // federated subject entitlements are the ones conveyed in the idp subject with
        // an extra tag
        for (SubjectAttribute sa : idpSubject.getPrincipals(SubjectAttribute.class)) {
            // Map SAML 2.0 Groups as roles
            if (sa.getName().equals("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:groups")) {
                merged.add(new SubjectRole(sa.getValue()));
            }
        }

         */

        federatedSubject = new Subject(true, merged,
                localSubject.getPublicCredentials(),
                localSubject.getPrivateCredentials());

        if (logger.isTraceEnabled())
            logger.trace("Merged subject " + federatedSubject);

        return federatedSubject;
    }
}
