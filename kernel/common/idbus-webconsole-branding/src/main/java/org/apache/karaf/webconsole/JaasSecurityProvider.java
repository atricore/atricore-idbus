package org.apache.karaf.webconsole;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;

import org.apache.felix.webconsole.WebConsoleSecurityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaasSecurityProvider implements WebConsoleSecurityProvider {

	private static final Logger LOG = LoggerFactory.getLogger(WebConsoleSecurityProvider.class);

    private String realm;

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public Object authenticate(final String username, final String password) {
        try {
            Subject subject = new Subject();
            LoginContext loginContext = new LoginContext(realm, subject, new CallbackHandler() {
                public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                    for (int i = 0; i < callbacks.length; i++) {
                        if (callbacks[i] instanceof NameCallback) {
                            ((NameCallback) callbacks[i]).setName(username);
                        } else if (callbacks[i] instanceof PasswordCallback) {
                            ((PasswordCallback) callbacks[i]).setPassword(password.toCharArray());
                        } else {
                            throw new UnsupportedCallbackException(callbacks[i]);
                        }
                    }
                }
            });
            loginContext.login();
            return subject;
        } catch (FailedLoginException e) {
            LOG.debug("Login failed", e);
            return null;
        } catch (AccountException e) {
            LOG.warn("Account failure", e);
            return null;
        } catch (GeneralSecurityException e) {
            LOG.error("General Security Exception", e);
            return null;
        }
    }

    public boolean authorize(Object o, String s) {
        return true;
    }
}
