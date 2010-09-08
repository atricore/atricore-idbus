/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.samlr2.main.emitter.plans;

import oasis.names.tc.saml._2_0.assertion.AuthnStatementType;
import org.atricore.idbus.capabilities.sts.main.WSTConstants;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;
import org.atricore.idbus.kernel.planning.IdentityPlanningException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This plan will transform an input token (UsernameToken, etc) into a SAMLR2 Authentication Assertion.
 *
 * @org.apache.xbean.XBean element="sectoken-to-authassertion-plan"
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2SecurityTokenToAuthnAssertionPlan.java 1335 2009-06-24 16:34:38Z sgonzalez $
 */
public class SamlR2SecurityTokenToAuthnAssertionPlan extends AbstractSAMLR2AssertionPlan {

    public IdentityPlanExecutionExchange prepare(IdentityPlanExecutionExchange ex) throws IdentityPlanningException {
        
        IdentityArtifact artifact = ex.getIn();

        try {
            Object requestToken = artifact.getContent();


            if (logger.isTraceEnabled())
                logger.trace("Emitting toke for " + requestToken.getClass().getSimpleName());

            String username = null;
            if (requestToken instanceof UsernameTokenType ) {
                UsernameTokenType usernameToken = (UsernameTokenType) requestToken;
                username = usernameToken.getUsername().getValue();
            } else if (isRememberMeToken( artifact.getContent() ) ) {
                Subject s = (Subject) ex.getProperty( WSTConstants.SUBJECT_PROP );
                SimplePrincipal principal = s.getPrincipals( SimplePrincipal.class ).iterator().next();
                username = principal.getName();
            } else if (requestToken instanceof AuthnStatementType) {
                Subject s = (Subject) ex.getProperty( WSTConstants.SUBJECT_PROP );
                SSOUser ssoUser = s.getPrincipals(SSOUser.class).iterator().next();
                username = ssoUser.getName(); 
            } else {
                // TODO : Support other token types!
                throw new IdentityPlanningException("Unsupported token " + requestToken.getClass().getName());
            }

            if (logger.isDebugEnabled())
                logger.debug("Emitting token for " + username);

            // All principals that will be added to the subject
            Set<Principal> principals = new HashSet<Principal>();
            SSOIdentityManager idMgr = getIdentityManager();
            if (idMgr == null)
                throw new IllegalStateException("SSOIdentityManager not configured for plan " + getClass().getSimpleName());

            // Find SSOUser principal
            SSOUser ssoUser = idMgr.findUser(username);
            principals.add(ssoUser);

            // Find SSORole principals
            SSORole[] ssoRoles = getIdentityManager().findRolesByUsername(username);
            principals.addAll(Arrays.asList(ssoRoles));

            if (logger.isTraceEnabled()) {

            }

            // Build subject and publish as execution context variable.
            Subject s = null;
            // If we had a subject already, use the private / public credentials!
            // This subject came probably from a JOSSO1 Auth Scheme.
            if (ex.getProperty(WSTConstants.SUBJECT_PROP) != null) {
                Subject auths = (Subject) ex.getProperty(WSTConstants.SUBJECT_PROP);
                s = new Subject(true, principals, auths.getPrivateCredentials(), auths.getPublicCredentials());
            } else {
                s = new Subject(true, principals, new HashSet(), new HashSet());
            }

            ex.setProperty(WSTConstants.SUBJECT_PROP, s);

        } catch (NoSuchUserException e) {
            throw new IdentityPlanningException(e.getMessage(), e);
        } catch (SSOIdentityException e) {
            throw new IdentityPlanningException(e.getMessage(), e);
        }

        return ex;
    }

    /**
     * Process fragment registry form SAMLR2 Authn Assertion generation from Subject.
     * @return
     */

    protected String getProcessDescriptorName() {
        return "samlr2-assertion-from-token-process";
    }

    private Boolean isRememberMeToken(Object requestToken){

        // TODO : User SAMLR2 AuthnContextClass Previous Session
        if (requestToken instanceof BinarySecurityTokenType ){
            return ((BinarySecurityTokenType)requestToken).getOtherAttributes().containsKey( new QName( Constants.REMEMBERME_NS) );
        }
        return false;
    }
}
