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

package org.atricore.idbus.kernel.main.mediation.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrust;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateManager;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractFederatedProvider implements FederatedLocalProvider, BundleContextAware {

    private static final Log logger = LogFactory.getLog(AbstractFederatedProvider.class);

    private String name;

    private String description;

    private String role;

    private FederationChannel channel;

    private Set<FederationChannel> channels = new HashSet<FederationChannel>();

    private BindingChannel bindingChannel;

    private CircleOfTrust circleOfTrust;

    private transient ProviderStateManager stateManager;

    private transient CircleOfTrustManager cotManager;

    private transient IdentityMediationUnitContainer unitContainer;

    private transient BundleContext bundleContext;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public FederationChannel getChannel() {
        return channel;
    }

    public void setChannel(FederationChannel channel) {
        this.channel = channel;
    }

    public Set<FederationChannel> getChannels() {
        return channels;
    }

    public void setChannels(Set<FederationChannel> channels) {
        this.channels = channels;
    }

    public BindingChannel getBindingChannel() {
        return bindingChannel;
    }

    public void setBindingChannel(BindingChannel bindingChannel) {
        this.bindingChannel = bindingChannel;
    }

    public CircleOfTrust getCircleOfTrust() {
        return circleOfTrust;
    }

    public void setCircleOfTrust(CircleOfTrust circleOfTrust) {
        this.circleOfTrust = circleOfTrust;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public List<CircleOfTrustMemberDescriptor> getMembers() {

        List<CircleOfTrustMemberDescriptor> members = new ArrayList<CircleOfTrustMemberDescriptor>();
        for (FederationChannel channel : channels) {
            members.add(channel.getMember());
        }
        
        // Add also the default channel's member
        if (channel != null)
            members.add(channel.getMember());

        return members;
    }

    public IdentityMediationUnitContainer getUnitContainer() {
        return unitContainer;
    }

    public void setUnitContainer(IdentityMediationUnitContainer unitContainer) {
        this.unitContainer = unitContainer;
    }

    public ProviderStateManager getStateManager() {
        return stateManager;
    }

    public void setStateManager(ProviderStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public CircleOfTrustManager getCotManager() {
        return cotManager;
    }

    public void setCotManager(CircleOfTrustManager cotManager) {
        this.cotManager = cotManager;
    }

}
