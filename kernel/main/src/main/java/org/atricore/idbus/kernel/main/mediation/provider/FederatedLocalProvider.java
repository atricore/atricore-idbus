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

import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustManager;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitContainer;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.state.ProviderStateManager;
import org.osgi.framework.BundleContext;

import java.util.Set;

/**
 * Represents a provider that is locally deployed in the IDBus.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public interface FederatedLocalProvider extends FederatedProvider, StatefulProvider {

    /**
     * The channel that this provider uses to communicate with other providers
     * @return
     */
    FederationChannel getChannel();

    /**
     * The channel that this provider uses to communicate with other providers
     * @return
     */
    FederationChannel getChannel(String configurationKey);

    /**
     * Specific set of channels that this provider uses to communicate with other providers.
     *
     * @return
     */
    Set<FederationChannel> getChannels();

    /**
     * Specific set of channels that this provider uses to communicate with other providers.
     *
     * @return
     */
    Set<FederationChannel> getChannels(String configurationKey);


    /**
     * The channel this provider uses to communicate with binding providers or bindings
     * @return
     */
    BindingChannel getBindingChannel();

    IdentityMediationUnitContainer getUnitContainer();

    CircleOfTrustManager getCotManager();

    BundleContext getBundleContext();
}
