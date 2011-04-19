package com.atricore.idbus.console.twofactor.wikid.authscheme;

import com.wikidsystems.client.wClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AbstractAuthenticationScheme;

import java.security.Principal;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WiKIDAuthenticationScheme extends AbstractAuthenticationScheme {

    private static final Log logger = LogFactory.getLog(WiKIDAuthenticationScheme.class);

    private String serverHost;

    private int serverPort;

    private String serverCode;

    private String caCertStorePath;

    private String caCertStorePass;

    private String wcStorePath;

    private String wcStorePass;

    public WiKIDAuthenticationScheme() {
        this.setName("2factor-authentication");
    }

    @Override
    protected CredentialProvider doMakeCredentialProvider() {
        return new WiKIDCredentialProvider();
    }

    public boolean authenticate() throws SSOAuthenticationException {
        try {

            if (logger.isTraceEnabled())
                logger.trace("connecting client to " + serverHost + ":" + serverPort);

            wClient wc = new wClient(serverHost, serverPort,
                    System.getProperty("karaf.home") + wcStorePath, wcStorePass,
                    System.getProperty("karaf.home") + caCertStorePath, caCertStorePass);

            String username = getUsername(_inputCredentials);
            String passcode = getPassCode(_inputCredentials);

            if (logger.isTraceEnabled())
                logger.trace("check credentials : " + username + "/" + passcode + " for server " + serverCode);

            setAuthenticated(wc.CheckCredentials(username, passcode, serverCode));

            return isAuthenticated();

        } catch (Exception e) {
            throw new SSOAuthenticationException(e);
        }
    }

    public Principal getPrincipal() {
        return new SimplePrincipal(getUsername(_inputCredentials));
    }

    public Principal getPrincipal(Credential[] credentials) {
        return new SimplePrincipal(getUsername(credentials));
    }

    /**
     * Only one password credential supported.
     */
    public Credential[] getPrivateCredentials() {

        Credential c = getPassCodeCredential(_inputCredentials);
        if (c == null)
            return new Credential[0];

        Credential[] r = {c};
        return r;

    }

    /**
     * Only one username credential supported.
     */
    public Credential[] getPublicCredentials() {
        Credential c = getUsernameCredential(_inputCredentials);
        if (c == null)
            return new Credential[0];

        Credential[] r = {c};
        return r;
    }

    protected String getUsername(Credential[] credentials) {
        WiKIDUsernameCredential c = getUsernameCredential(credentials);
        if (c == null)
            return null;

        return (String) c.getValue();
    }

    protected String getPassCode(Credential[] credentials) {
        WiKIDPassCodeCredential p = getPassCodeCredential(credentials);
        if (p == null)
            return null;
        return (String) p.getValue();
    }


    /**
     * Gets the credential that represents a Username.
     */
    protected WiKIDUsernameCredential getUsernameCredential(Credential[] credentials) {

        for (Credential credential : credentials) {
            if (credential instanceof WiKIDUsernameCredential) {
                return (WiKIDUsernameCredential) credential;
            }
        }
        return null;
    }

    protected WiKIDPassCodeCredential getPassCodeCredential(Credential[] credentials) {
        for (int i = 0; i < credentials.length; i++) {
            if (credentials[i] instanceof WiKIDPassCodeCredential) {
                return (WiKIDPassCodeCredential) credentials[i];
            }
        }
        return null;
    }


    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getCaCertStorePath() {
        return caCertStorePath;
    }

    public void setCaCertStorePath(String caCertStorePath) {
        this.caCertStorePath = caCertStorePath;
    }

    public String getCaCertStorePass() {
        return caCertStorePass;
    }

    public void setCaCertStorePass(String caCertStorePass) {
        this.caCertStorePass = caCertStorePass;
    }

    public String getWcStorePath() {
        return wcStorePath;
    }

    public void setWcStorePath(String wcStorePath) {
        this.wcStorePath = wcStorePath;
    }

    public String getWcStorePass() {
        return wcStorePass;
    }

    public void setWcStorePass(String wcStorePass) {
        this.wcStorePass = wcStorePass;
    }
}
