/******************************************************************************
 * JBoss, a division of Red Hat                                               *
 * Copyright 2006, Red Hat Middleware, LLC, and individual                    *
 * contributors as indicated by the @authors tag. See the                     *
 * copyright.txt in the distribution for a full listing of                    *
 * individual contributors.                                                   *
 *                                                                            *
 * This is free software; you can redistribute it and/or modify it            *
 * under the terms of the GNU Lesser General Public License as                *
 * published by the Free Software Foundation; either version 2.1 of           *
 * the License, or (at your option) any later version.                        *
 *                                                                            *
 * This software is distributed in the hope that it will be useful,           *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU           *
 * Lesser General Public License for more details.                            *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this software; if not, write to the Free                *
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA         *
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.                   *
 ******************************************************************************/
package org.atricore.idbus.idojos.gateinidentitystore;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialKey;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AuthenticationScheme;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.BindContext;
import org.atricore.idbus.kernel.main.store.identity.BindableCredentialStore;


/**
 * @author <a href="mailto:sshah@redhat.com">Sohil Shah</a>
 * @org.apache.xbean.XBean element="gatein-store"
 */
public class GateInBindIdentityStore implements BindableCredentialStore {
    private static final Log log = LogFactory.getLog(GateInBindIdentityStore.class);

    private AuthenticationScheme authenticationScheme = null;

    private String gateInHost;
    private String gateInPort;
    private String gateInContext;

    public GateInBindIdentityStore() {
    }

    public void setAuthenticationScheme(AuthenticationScheme authenticationScheme) {
        this.authenticationScheme = authenticationScheme;
    }

    public String getGateInHost() {
        return gateInHost;
    }

    public void setGateInHost(String gateInHost) {
        this.gateInHost = gateInHost;
    }

    public String getGateInPort() {
        return gateInPort;
    }

    public void setGateInPort(String gateInPort) {
        this.gateInPort = gateInPort;
    }

    public String getGateInContext() {
        return gateInContext;
    }

    public void setGateInContext(String gateInContext) {
        this.gateInContext = gateInContext;
    }


    public boolean bind(String username, String password, BindContext bindContext)
            throws SSOAuthenticationException {
        try {
            // return this.portalIdentityService.authenticate(username, password);
            log.debug("Performing Authentication........................");
            log.debug("Username: " + username);
            log.debug("Password: " + password);

            StringBuilder urlBuffer = new StringBuilder();
            urlBuffer.append("http://" + gateInHost + ":" + gateInPort +
                    (!gateInContext.isEmpty() ? "/" + gateInContext : "") +
                    "/rest/sso/authcallback/auth/" + username + "/"  + password);

            boolean success = this.executeRemoteCall(urlBuffer.toString());

            return success;
        } catch (Exception e) {
            throw new SSOAuthenticationException(e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    private boolean executeRemoteCall(String authUrl) throws Exception {
        HttpClient client = new HttpClient();
        GetMethod method = null;
        try {
            method = new GetMethod(authUrl);

            int status = client.executeMethod(method);
            String response = method.getResponseBodyAsString();

            switch (status) {
                case 200:
                    if (response.equals(Boolean.TRUE.toString())) {
                        return true;
                    }
                    break;
            }

            return false;
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
    }

    public Credential[] loadCredentials(CredentialKey key, CredentialProvider cp) throws SSOIdentityException {
        throw new UnsupportedOperationException("GateIn credentials cannot be accessed through this mechanism");
    }
}