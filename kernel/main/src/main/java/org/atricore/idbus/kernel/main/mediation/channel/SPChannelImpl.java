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
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.SSOSessionManagerFactory;
import org.atricore.idbus.kernel.main.store.SSOIdentityManager;
import org.xmlsoap.schemas.ws._2005._02.trust.wsdl.SecurityTokenService;

/**
 * Service Provider channel default implementation.  Allows a Provider (IDP, etc) to
 * communitcate with a Service Provider (SP)
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: IDPChannelImpl.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class SPChannelImpl extends AbstractFederationChannel implements SPChannel {

    // Specifies the STS Service to be used when generating Security Tokens for this SP.
    private transient SecurityTokenService securityTokenService;

    // SSO Session manager service used to handle sessions for this SP.
    private transient SSOSessionManager sessionManager;

    // SSO Session manager factory service used to create a session manager if needed;
    private transient SSOSessionManagerFactory sessionManagerFactory;


    // SSO Identity manager service used to retrieve identity for this SP.
    private transient SSOIdentityManager identityManager;

    private boolean isProxyModeEnabled;

    private Channel proxy;

    private String attributeProfile;



    public SecurityTokenService getSecurityTokenService() {
        return securityTokenService;
    }

    public void setSecurityTokenService(SecurityTokenService securityTokenService) {
        this.securityTokenService = securityTokenService;
    }

    public SSOSessionManager getSessionManager() {
        if (this.sessionManager == null && this.getSessionManagerFactory() != null)
            this.sessionManager = this.getSessionManagerFactory().getInstance();

        return this.sessionManager;
    }

    public void setSessionManager(SSOSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public SSOSessionManagerFactory getSessionManagerFactory() {
        return sessionManagerFactory;
    }

    public void setSessionManagerFactory(SSOSessionManagerFactory sessionManagerFactory) {
        this.sessionManagerFactory = sessionManagerFactory;
    }

    public SSOIdentityManager getIdentityManager() {
        return this.identityManager;
    }

    public void setIdentityManager (SSOIdentityManager identityManager) {
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

    @Override
    public String getAttributeProfile() {
        return attributeProfile;
    }

    public void setAttributeProfile(String attributeProfile) {
        this.attributeProfile = attributeProfile;
    }
}
