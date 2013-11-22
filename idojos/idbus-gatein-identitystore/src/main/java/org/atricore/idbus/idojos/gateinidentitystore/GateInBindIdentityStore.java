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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialKey;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AuthenticationScheme;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.BindContext;
import org.atricore.idbus.kernel.main.store.identity.BindableCredentialStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;



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

            /*
            String authnPath = URIUtil.encodePath(
                    (!gateInContext.isEmpty() ? "/" + gateInContext : "") +
                            "/rest/sso/authcallback/auth/" + new String(Base64.encodeBase64(username.getBytes())) +
                            "/" + new String(Base64.encodeBase64(password.getBytes()))
            );
            */
            String authnPath =
                    (!gateInContext.isEmpty() ? "/" + gateInContext : "") +
                            "/rest/sso/authcallback/auth/" + new String(Base64.encodeBase64(username.getBytes())) +
                            "/" + new String(Base64.encodeBase64(password.getBytes()));
            StringBuilder urlBuffer = new StringBuilder();

            urlBuffer.append("http://" + gateInHost + ":" + gateInPort + authnPath);

            boolean success = this.executeRemoteCall(urlBuffer.toString());

            return success;
        } catch (Exception e) {
            throw new SSOAuthenticationException(e);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    private boolean executeRemoteCall(String authUrl) throws Exception {
        if (log.isDebugEnabled())
            log.debug("Starting remote call using HTTP client\n["+authUrl+"]");

        InputStream instream = null;
        HttpRequestBase httpReq = null;

        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();

            if (log.isTraceEnabled())
                log.trace("Client OK");

            httpReq = new HttpGet(authUrl);

            if (log.isTraceEnabled())
                log.trace("Method OK");

            HttpResponse httpRes = httpClient.execute(httpReq);
            int status = httpRes.getStatusLine().getStatusCode();

            if (log.isTraceEnabled())
                log.trace("Client execution OK");

            if (log.isTraceEnabled())
                log.trace("Response OK");

            // Get hold of the response entity
            HttpEntity entity = httpRes.getEntity();


            if (entity != null) {

                instream = entity.getContent();

                // If the response does not enclose an entity, there is no need
                byte[] buff = new byte[1024];
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                String response = null;


                // Just ignore the content ...
                // should we do something with this ?!
                int r = instream.read(buff);
                int total = r;
                while (r > 0) {
                    baos.write(buff, 0, r);
                    r = instream.read(buff);
                    total += r;
                }

                response = baos.toString();

                if (total > 0)
                    log.debug("Read response content size : " + total + " [" + response + "] ");

                switch (status) {
                    case 200:
                        if (response != null && Boolean.parseBoolean(response)) {
                            return true;
                        }
                        break;
                    default:
                        log.warn("Received invalid HTTP status " + status + " for " + authUrl);
                }

                return false;

            } else {

                // Not authenticated
                log.warn("No response body received for " + authUrl);
                return false;
            }


        } catch (IOException ex) {
            // In case of an IOException the connection will be released
            // back to the connection manager automatically
            throw ex;
        } catch (RuntimeException ex) {
            // In case of an unexpected exception you may want to abort
            // the HTTP request in order to shut down the underlying
            // connection immediately.
            if (httpReq != null) {
                try {
                    httpReq.abort();
                } catch(Exception e) {
                    log.error(e.getMessage(), e);

                }
            }
        } finally {
            // Closing the input stream will trigger connection release
            try {
                instream.close();
            } catch (Exception ignore) {
                // Ignore this ...
            }
        }

        return false;
    }

    public Credential[] loadCredentials (CredentialKey key, CredentialProvider cp)throws SSOIdentityException {
        throw new UnsupportedOperationException("GateIn credentials cannot be accessed through this mechanism");
    }

}