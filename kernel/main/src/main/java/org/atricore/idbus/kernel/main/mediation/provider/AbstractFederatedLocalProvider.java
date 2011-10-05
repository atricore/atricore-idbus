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
public abstract class AbstractFederatedLocalProvider extends AbstractFederatedProvider
        implements FederatedLocalProvider, BundleContextAware  {

    private static final Log logger = LogFactory.getLog(AbstractFederatedLocalProvider.class);

    private BindingChannel bindingChannel;

    private transient ProviderStateManager stateManager;

    private transient CircleOfTrustManager cotManager;

    private transient IdentityMediationUnitContainer unitContainer;

    private transient BundleContext bundleContext;

    public BindingChannel getBindingChannel() {
        return bindingChannel;
    }

    public void setBindingChannel(BindingChannel bindingChannel) {
        this.bindingChannel = bindingChannel;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
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
