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

package org.atricore.idbus.kernel.main.mediation.channel;

import org.atricore.idbus.kernel.main.federation.AccountLinkEmitter;
import org.atricore.idbus.kernel.main.federation.AccountLinkLifecycle;
import org.atricore.idbus.kernel.main.federation.IdentityMapper;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.MetadataEntry;
import org.atricore.idbus.kernel.main.mediation.AbstractChannel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.Provider;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: AbstractFederationChannel.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public abstract class AbstractFederationChannel extends AbstractChannel implements FederationChannel {

    private CircleOfTrust cot;
    private CircleOfTrustMemberDescriptor member;
    private MetadataEntry metadata;
    private transient FederatedLocalProvider provider;
    private transient FederatedProvider targetProvider;

    private transient AccountLinkLifecycle accountLinkLifecycle;
    private transient AccountLinkEmitter accountLinkEmitter;
    private transient IdentityMapper identityMapper;


    public CircleOfTrust getCircleOfTrust() {
        return cot;
    }

    public void setCircleOfTrust(CircleOfTrust cot) {
        this.cot = cot;
    }

    public CircleOfTrustMemberDescriptor getMember() {
        return member;
    }

    public void setMember(CircleOfTrustMemberDescriptor member) {
        this.member = member;
    }

    public MetadataEntry getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataEntry metadata) {
        this.metadata = metadata;
    }

    public String getRole() {
        return provider.getRole();
    }

    public FederatedProvider getTargetProvider() {
        return targetProvider;
    }

    public void setTargetProvider(FederatedProvider targetProvider) {
        this.targetProvider = targetProvider;
    }

    public FederatedLocalProvider getProvider() {
        return provider;
    }

    public void setProvider(FederatedLocalProvider provider) {
        this.provider = provider;
    }

    public AccountLinkLifecycle getAccountLinkLifecycle() {
        return accountLinkLifecycle;
    }

    public void setAccountLinkLifecycle(AccountLinkLifecycle accountLinkLifecycle) {
        this.accountLinkLifecycle = accountLinkLifecycle;
    }

    public AccountLinkEmitter getAccountLinkEmitter() {
        return accountLinkEmitter;
    }

    public void setAccountLinkEmitter(AccountLinkEmitter accountLinkEmitter) {
        this.accountLinkEmitter = accountLinkEmitter;
    }

    public IdentityMapper getIdentityMapper() {
        return identityMapper;
    }

    public void setIdentityMapper(IdentityMapper identityMapper) {
        this.identityMapper = identityMapper;
    }

    @Override
    public IdentityMediationUnitContainer getUnitContainer() {
        return provider.getUnitContainer();
    }

    @Override
    public void setUnitContainer(IdentityMediationUnitContainer identityMediationUnitContainer) {
        // Fedeeration channesl use mediation engine from parent providers
        throw new UnsupportedOperationException("Set mediation engine in provider!");
    }

    @Override
    public String toString() {
        return super.toString() +
                "[cot=" + (cot != null ? cot.getName() : null) +
                ",alias=" + (member !=null ? member.getAlias() : null )+ "," +
                ",provider=" + (provider !=null ? provider.getName() : null) +
                ",targetProvider="+ (targetProvider != null ? targetProvider.getName():null)
                +"]";
    }
}



