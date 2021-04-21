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
package org.atricore.idbus.capabilities.sso.ui.internal;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;
import org.atricore.idbus.capabilities.sso.ui.agent.SecurityContext;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.pwdreset.PwdResetState;
import org.atricore.idbus.capabilities.sso.ui.page.selfsvcs.registration.RegistrationState;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.common.sso._1_0.protocol.SelectEntityRequestType;
import org.atricore.idbus.kernel.main.mediation.claim.CredentialClaimsRequest;
import org.atricore.idbus.kernel.main.mediation.claim.UserClaimsRequest;
import org.atricore.idbus.kernel.main.mediation.policy.PolicyEnforcementRequest;
import org.atricore.idbus.kernel.main.provisioning.domain.UserSecurityQuestion;


/**
 * SSO-specific implementation of the wicket web session. Used to persist the claim request.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class SSOWebSession extends WebSession {

    private CredentialClaimsRequest credentialClaimsRequest;

    private UserClaimsRequest userClaimsRequest;

    private PolicyEnforcementRequest policyEnforcementRequest;

    private String lastAppErrorId;

    private SecurityContext securityContext;

    private UserSecurityQuestion[] securityQuestions;

    private RegistrationState  registrationState;
    private int retries;
    private String lastUsername;
    private PwdResetState pwdResetState;

    public SSOWebSession(Request request) {
        super(request);
    }

    /**
     * @return Current authenticated web session
     */
    public static SSOWebSession get() {
        return (SSOWebSession) Session.get();
    }

    public void setCredentialClaimsRequest(SSOCredentialClaimsRequest credentialClaimsRequest) {
        this.credentialClaimsRequest = credentialClaimsRequest;
    }

    public CredentialClaimsRequest getCredentialClaimsRequest() {
        return credentialClaimsRequest;
    }

    public void setLastAppErrorId(String lastAppErrorId) {
        this.lastAppErrorId = lastAppErrorId;
    }

    public String getLastAppErrorId() {
        return lastAppErrorId;
    }

    public boolean isAuthenticated() {
        return securityContext != null && securityContext.isSessionValid();
    }

    public String getPrincipal() {
        return securityContext != null ? securityContext.getPrincipal() : null;
    }

    public void setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public UserSecurityQuestion[] getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(UserSecurityQuestion[] securityQuestions) {
        this.securityQuestions = securityQuestions;
    }

    public RegistrationState  getRegistrationState() {
        return registrationState;
    }

    public void setRegistrationState(RegistrationState registrationState) {
        this.registrationState = registrationState;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public UserClaimsRequest getUserClaimsRequest() {
        return userClaimsRequest;
    }

    public void setUserClaimsRequest(UserClaimsRequest userClaimsRequest) {
        this.userClaimsRequest = userClaimsRequest;
    }

    public void setLastUsername(String lastUsername) {
        this.lastUsername = lastUsername;
    }

    public String getLastUsername() {
        return lastUsername;
    }

    public PolicyEnforcementRequest getPolicyEnforcementRequest() {
        return policyEnforcementRequest;
    }

    public void setPolicyEnforcementRequest(PolicyEnforcementRequest policyEnforcementRequest) {
        this.policyEnforcementRequest = policyEnforcementRequest;
    }

    public void setPwdResetState(PwdResetState state) {
        this.pwdResetState = state;
    }

    public PwdResetState getPwdResetState() {
        return pwdResetState;
    }
}
