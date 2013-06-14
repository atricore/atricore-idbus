/*
 * Atricore IDBus
 *
 * Copyright (c) 2009-2012, Atricore Inc.
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

import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.confirmation.IdentityConfirmationChannel;
import org.atricore.idbus.kernel.main.mediation.select.SelectorChannel;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateManager;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 * Default implementation of an identity confirmation provider.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class IdentityConfirmationProviderImpl implements IdentityConfirmationProvider, BundleContextAware {

    private String name;

    private String description;

    private String displayName;

    private String role;

    private transient CircleOfTrustManager cotManager;

    private transient IdentityMediationUnitContainer unitContainer;

    private transient BundleContext bundleContext;

    private IdentityConfirmationChannel channel;

    private ProviderStateManager stateManager;

    public IdentityConfirmationChannel getChannel() {
        return channel;
    }

    public void setChannel(IdentityConfirmationChannel channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public CircleOfTrustManager getCotManager() {
        return cotManager;
    }

    public void setCotManager(CircleOfTrustManager cotManager) {
        this.cotManager = cotManager;
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


}
