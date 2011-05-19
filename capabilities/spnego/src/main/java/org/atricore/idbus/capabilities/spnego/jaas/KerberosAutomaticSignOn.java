package org.atricore.idbus.capabilities.spnego.jaas;


import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;

public class KerberosAutomaticSignOn {
    private String principal;

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }
    public void signon() throws Exception {
        try {
            authenticate(new String[] { principal } );
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Subject authenticate(Object credentials) throws SecurityException {
        if (!(credentials instanceof String[])) {
            throw new IllegalArgumentException("Expected String[1], got "
                    + (credentials != null ? credentials.getClass().getName() : null));
        }
        final String[] params = (String[]) credentials;
        if (params.length != 1) {
            throw new IllegalArgumentException("Expected String[1] but length was " + params.length);
        }
        try {
            LoginContext loginContext = new LoginContext("spnego", new CallbackHandler() {
                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    for (int i = 0; i < callbacks.length; i++) {
                        if (callbacks[i] instanceof NameCallback) {
                            ((NameCallback) callbacks[i]).setName(params[0]);
                        } else {
                            throw new UnsupportedCallbackException(callbacks[i]);
                        }
                    }
                }
            });
            loginContext.login();
            return loginContext.getSubject();
        } catch (LoginException e) {
            throw new SecurityException("Authentication failed", e);
        }
    }

}
