package org.atricore.idbus.capabilities.spnego.jaas;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;


/**
 * @author gbrigandi
 */
@Command(scope = "spnego", name = "init", description = "Init Kerberos Service.")
public class KerberosServerInitCommand extends OsgiCommandSupport {

    @Argument(index = 0, name = "realm", description = "Realm", required = true, multiValued = false)
    private String realm;

    @Argument(index = 1, name = "principal", description = "Principal", required = true, multiValued = false)
    private String principal;

    @Override
    protected Object doExecute() throws Exception {
        return authenticate(new String[] { principal } );
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
            LoginContext loginContext = new LoginContext(realm, new CallbackHandler() {
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
