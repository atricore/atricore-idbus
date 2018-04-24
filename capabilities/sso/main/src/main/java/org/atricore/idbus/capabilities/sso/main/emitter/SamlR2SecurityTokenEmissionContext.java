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
import oasis.names.tc.saml._2_0.assertion.EncryptedElementType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import org.atricore.idbus.capabilities.sso.main.idp.SPChannelConfiguration;
import org.atricore.idbus.capabilities.sso.main.idp.producers.AuthenticationState;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenEmissionContext;
import org.atricore.idbus.common.sso._1_0.protocol.AbstractPrincipalType;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2SecurityTokenEmissionContext extends AbstractSecurityTokenEmissionContext {

    private List<AbstractPrincipalType> proxyPrincipals = new ArrayList<AbstractPrincipalType>();

    private CircleOfTrustMemberDescriptor member;

    private EndpointDescriptor spAcs;

    private String identityPlanName;

    // SAML 2.0 Specific information

    private MetadataEntry roleMetadata ;

    private AssertionType assertion;

    private EncryptedElementType encryptedAssertion;

    private AuthenticationState authnState;

    private MetadataEntry issuerMetadata;

    private String attributeProfile;

    private SPChannelConfiguration spChannelConfig;

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

    public List<AbstractPrincipalType> getProxyPrincipals() {
        return proxyPrincipals;
    }

    public void setProxyResponse(List<AbstractPrincipalType> proxyPrincipals) {
        this.proxyPrincipals = proxyPrincipals;
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

    public void setSpAcs(EndpointDescriptor spAcs) {
        this.spAcs = spAcs;
    }

    public EndpointDescriptor getSpAcs() {
        return this.spAcs;
    }

    public String getAttributeProfile() {
        return attributeProfile;
    }

    public void setAttributeProfile(String attributeProfile) {
        this.attributeProfile = attributeProfile;
    }

    public SPChannelConfiguration getSpChannelConfig() {
        return spChannelConfig;
    }

    public void setSpChannelConfig(SPChannelConfiguration spChannelConfig) {
        this.spChannelConfig = spChannelConfig;
    }

    public EncryptedElementType getEncryptedAssertion() {
        return encryptedAssertion;
    }

    public void setEncryptedAssertion(EncryptedElementType encryptedAssertion) {
        this.encryptedAssertion = encryptedAssertion;
    }
}
