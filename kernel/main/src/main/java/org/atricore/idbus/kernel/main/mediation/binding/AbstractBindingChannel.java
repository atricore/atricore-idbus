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

package org.atricore.idbus.kernel.main.mediation.binding;

import org.atricore.idbus.kernel.main.mediation.AbstractChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.StatefulProvider;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: AbstractBindingChannel.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public abstract class AbstractBindingChannel extends AbstractChannel implements BindingChannel {

    private FederatedLocalProvider provider;

    private String defaultServiceURL;

    public FederatedLocalProvider getFederatedProvider() {
        return provider;
    }

    public void setFederatedProvider(FederatedLocalProvider provider) {
        this.provider = provider;
    }

    public StatefulProvider getProvider() {
        return provider;
    }

    @Override
    public String getDefaultServiceURL() {
        return defaultServiceURL;
    }

    public void setDefaultServiceURL(String defaultServiceURL) {
        this.defaultServiceURL = defaultServiceURL;
    }
}
