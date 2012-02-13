package org.atricore.idbus.capabilities.spnego;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.HexDump;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.authn.exceptions.AuthenticationFailureException;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AbstractAuthenticationScheme;
import org.ietf.jgss.*;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.security.PrivilegedAction;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class SpnegoAuthenticationScheme extends AbstractAuthenticationScheme {

    private static final Log logger = LogFactory.getLog(SpnegoAuthenticationScheme.class);

    private String realm;
    private String principalName;
    private GSSName clientName;

    public SpnegoAuthenticationScheme() {
        this.setName("spnego-authentication");
    }

    @Override
    protected CredentialProvider doMakeCredentialProvider() {
        return new SpnegoCredentialProvider();
    }

    public boolean authenticate() throws SSOAuthenticationException {

        try {

            String spnegoToken = getSpnegoToken(_inputCredentials);
            byte[] binarySpnegoToken = Base64.decodeBase64(spnegoToken.getBytes());

            if (logger.isTraceEnabled())
                logger.trace("Authenticate Spnego Token : " + spnegoToken);

            // NTLM tokens start with 'NTLM'string base64 encoded ... => TlRMT
            if (spnegoToken.startsWith("TlRMT")) {
                // This is an NTLM Token, looks like browser is not trusting the server ...
                logger.error("NTLM Token received, cannot perform Spnego Authentication");
                throw new AuthenticationFailureException("Authentication failed, NTLM token recieved " + spnegoToken);
            }

            LoginContext loginContext = new LoginContext(realm, new CallbackHandler() {

                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    for (int i = 0; i < callbacks.length; i++) {
                        if (callbacks[i] instanceof NameCallback) {
                            ((NameCallback) callbacks[i]).setName(principalName);
                        } else {
                            throw new UnsupportedCallbackException(callbacks[i]);
                        }
                    }
                }
            });
            loginContext.login();
            Subject kerberosSubject = loginContext.getSubject();
            clientName = new SecurityContextEstablisher().acceptKerberosServiceTicket(
                    binarySpnegoToken,
                    kerberosSubject,
                    principalName
            );

            if (clientName == null) {
                logger.debug("Client name is NULL");
                return false;
            }

            logger.debug("Client name is " + clientName.toString());

            setAuthenticated(true);

            return isAuthenticated();

        } catch (LoginException e) {
            if (logger.isDebugEnabled())
                logger.debug("Spnego Authenticatio Failure : " + e.getMessage(), e);

            throw new AuthenticationFailureException("Authentication failed : " + e.getMessage());

        } catch (Exception e) {
            throw new SSOAuthenticationException(e);
        }
    }

    public Principal getPrincipal() {
        // TODO : Look for GSSNAME oid format
        String pName = clientName.toString();
        pName = pName.substring(0, pName.indexOf("@"));
        return new SimplePrincipal(pName);
    }

    public Principal getPrincipal(Credential[] credentials) {
        return new SimplePrincipal(getSpnegoToken(credentials));
    }

    /**
     * Only one password credential supported.
     */
    public Credential[] getPrivateCredentials() {
        Credential c = getSpnegoTokenCredential(_inputCredentials);
        if (c == null)
            return new Credential[0];

        Credential[] r = {c};
        return r;
    }

    public Credential[] getPublicCredentials() {
        return new Credential[0];
    }

    protected String getSpnegoToken(Credential[] credentials) {
        SpnegoTokenCredential c = getSpnegoTokenCredential(credentials);
        if (c == null)
            return null;

        return (String) c.getValue();
    }

    /**
     * Gets the credential that represents a Username.
     */
    protected SpnegoTokenCredential getSpnegoTokenCredential(Credential[] credentials) {

        for (Credential credential : credentials) {
            if (credential instanceof SpnegoTokenCredential) {
                return (SpnegoTokenCredential) credential;
            }
        }
        return null;
    }

    class SecurityContextEstablisher implements PrivilegedAction {

        private byte[] kerberosServiceTicket;
        private String principal;
        private boolean loginOk = false;

        /**
         * Authenticates the kerberos service token .
         *
         * @param kerberosSubject the kerberos subject under which the authentication logic is ran
         */
        GSSName acceptKerberosServiceTicket(byte[] kerberosServiceTicket, Subject kerberosSubject, String principal) {
            this.kerberosServiceTicket = kerberosServiceTicket;
            this.principal = principal;
            return (GSSName) Subject.doAs(kerberosSubject, this);
        }

        /**
         * <p>
         * This is the only method in PrivilegedAction interface.
         * </p>
         * <p>
         * Created a GSS security context by verifying the kerberos service ticket supplied
         * by the client
         * </p>
         */
        public Object run() {
            //The context for secure communication with client.
            GSSContext serverGSSContext = null;

            try {
                GSSManager manager = GSSManager.getInstance();
                Oid spnego = new Oid("1.3.6.1.5.5.2");

                GSSName serverGSSName = manager.createName(principal, null);
                GSSCredential serverGSSCreds = manager.createCredential(serverGSSName,
                        GSSCredential.INDEFINITE_LIFETIME,
                        spnego,
                        GSSCredential.ACCEPT_ONLY);

                serverGSSContext = manager.createContext(serverGSSCreds);

                byte[] outputToken;
                outputToken = serverGSSContext.acceptSecContext(kerberosServiceTicket, 0,
                        kerberosServiceTicket.length);

                loginOk = true;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                HexDump.dump(outputToken, 0, baos, 0);
                logger.debug("Kerberos Service Ticket Successfully relayed (hex) : " + baos.toString());
                return serverGSSContext.getSrcName();
            } catch (GSSException e) {
                if (logger.isDebugEnabled())
                    logger.error("Fatal Error creating GSS security context : " + e.getMessage(), e);
            } catch (IOException e) {
                logger.error("I/O Error creating GSS security context : " + e.getMessage(), e);
            } catch (Exception e) {
                logger.error("Fatal Error creating GSS security context : " + e.getMessage(), e);
            }

            return null;
        }

    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }


}
