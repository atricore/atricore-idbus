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

package org.atricore.idbus.capabilities.sso.main.emitter;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.session.SSOSession;

import javax.security.auth.Subject;
import java.io.Serializable;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2SecurityTokenEmissionContext implements Serializable {

    private CircleOfTrustMemberDescriptor member;

    private MetadataEntry roleMetadata ;

    private Subject subject;

    private AssertionType assertion;

    private AuthenticationState authnState;

    private String sessionIndex;

    private SSOSession ssoSession;

    private MetadataEntry issuerMetadata;

    private String identityPlanName;

    public SamlR2SecurityTokenEmissionContext() {
    }

    public SamlR2SecurityTokenEmissionContext(AuthenticationState authnState,
                                              CircleOfTrustMemberDescriptor member,
                                              MetadataEntry roleMetadata) {

        this.member = member;
        this.roleMetadata = roleMetadata;
        this.authnState = authnState;
    }

    public RequestAbstractType getRequest() {
        return authnState.getAuthnRequest();
    }

    public CircleOfTrustMemberDescriptor getMember() {
        return member;
    }

    public void setMember(CircleOfTrustMemberDescriptor member) {
        this.member = member;
    }

    public MetadataEntry getRoleMetadata() {
        return roleMetadata;
    }

    public void setRoleMetadata(MetadataEntry roleMetadata) {
        this.roleMetadata = roleMetadata;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public AssertionType getAssertion() {
        return assertion;
    }

    public void setAssertion(AssertionType assertion) {
        this.assertion = assertion;
    }

    public void setAuthnState(AuthenticationState authnState) {
        this.authnState= authnState;
    }

    public AuthenticationState getAuthnState() {
        return authnState;
    }

    public void setSessionIndex(String sessionIndex) {
        this.sessionIndex = sessionIndex;
    }

    public String getSessionIndex() {
        return this.sessionIndex;
    }

    public SSOSession getSsoSession() {
        return ssoSession;
    }

    public void setSsoSession(SSOSession ssoSession) {
        this.ssoSession = ssoSession;
    }

    public MetadataEntry getIssuerMetadata() {
        return issuerMetadata;
    }

    public void setIssuerMetadata(MetadataEntry issuerMetadata) {
        this.issuerMetadata = issuerMetadata;
    }

    public void setIdentityPlanName(String identityPlanName) {
        this.identityPlanName = identityPlanName;
    }

    public String getIdentityPlanName() {
        return identityPlanName;
    }
}
