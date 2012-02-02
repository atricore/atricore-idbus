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

import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;

/**
 * Identity Provdier channel implementation.
 * Allows a Service Provider (SP) to communitcate with an Identity Provider (IDP)
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class IdPChannelImpl extends AbstractFederationChannel implements IdPChannel {

    // SSO Session manager used when communicating with this SP
    private transient SSOSessionManager sessionManager;

    @Deprecated
    private transient SSOIdentityManager identityManager;

    private boolean isProxyModeEnabled;

    private Channel proxy;

    /**
     * @org.apache.xbean.Property alias="session-manager"
     */
    public SSOSessionManager getSessionManager() {
        return this.sessionManager;
    }

    public void setSessionManager(SSOSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Deprecated
    public SSOIdentityManager getIdentityManager() {
        return identityManager;
    }

    @Deprecated
    public void setIdentityManager(SSOIdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    public boolean isProxyModeEnabled() {
        return isProxyModeEnabled;
    }

    public void setProxyModeEnabled(boolean proxyModeEnabled) {
        isProxyModeEnabled = proxyModeEnabled;
    }

    public Channel getProxy() {
        return proxy;
    }

    public void setProxy(Channel proxy) {
        this.proxy = proxy;
    }
}