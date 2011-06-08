package org.atricore.idbus.capabilities.spnego.jaas;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.BundleContext;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundleClassLoader;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.*;

public class KerberosServiceInit {

    private static final Log logger = LogFactory.getLog(KerberosServiceInit.class);

    public static final String KEYTAB_RESOURCE_BASE = "META-INF/krb5";

    private String realm;

    private String principal;

    private String keyTabName;

    private boolean overwriteSetup;

    private String keyTabRepository;

    private String krb5Config;

    private BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getKeyTabName() {
        return keyTabName;
    }

    public void setKeyTabName(String keyTabName) {
        this.keyTabName = keyTabName;
    }

    public String getKeyTabRepository() {
        return keyTabRepository;
    }

    public void setKeyTabRepository(String keyTabRepository) {
        this.keyTabRepository = keyTabRepository;
    }

    public boolean isOverwriteSetup() {
        return overwriteSetup;
    }

    public void setOverwriteSetup(boolean overwriteSetup) {
        this.overwriteSetup = overwriteSetup;
    }

    public String getKrb5Config() {
        return krb5Config;
    }

    public void setKrb5Config(String krb5Config) {
        this.krb5Config = krb5Config;
    }

    /**
     * Initialize Kerberos
     * @throws Exception
     */
    public void init() throws Exception {
        // Check if Kerberos setup must be updated
        if (overwriteSetup)
            overwriteKerberosSetup();

        // Perform an automatic signon, to make sure that everything is working.
        try {
            authenticate(new String[] { principal } );
        } catch (Exception e) {
            logger.error("Cannot perform Kerberos Sign-On:" + e.getMessage(), e);
            // TODO : Should we stop JOSSO Start=up ? what if the DC get's back on=line later.
            throw e;
        }
    }



    public Subject authenticate(Object credentials) throws SecurityException {

        //sun.security.krb5.Config.getInstance();
        if (logger.isDebugEnabled())
            logger.debug("Performing automatic authentication using credentials " + credentials);

        if (!(credentials instanceof String[])) {
            throw new IllegalArgumentException("Expected String[1], got "
                    + (credentials != null ? credentials.getClass().getName() : null));
        }
        final String[] params = (String[]) credentials;
        if (params.length != 1) {
            throw new IllegalArgumentException("Expected String[1] but length was " + params.length);
        }
        try {

            if (logger.isDebugEnabled())
                logger.debug("Performing automatic authentication using principal " + params[0]);

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
            logger.error("Login failure : " + e.getMessage());
            throw new SecurityException("Authentication failed", e);
        }
    }

    public void overwriteKerberosSetup() throws Exception {

        OutputStream out = null;
        InputStream in = null;
        try {
            // Install KeyTab
            in = loadKeyTabResource(keyTabName);

            File file = new File(keyTabRepository + "/" + keyTabName);
            out = new FileOutputStream(file, false);

            if (logger.isDebugEnabled())
                logger.debug("Installing keytab file to : " + file.getAbsolutePath());

            IOUtils.copy(in, out);

            // Update Kerberos setup file:

        } catch (Exception e) {
            logger.error("Cannot configure Kerberos :" + e.getMessage(), e);
            throw e;
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }

    }

    public InputStream loadKeyTabResource(String keyTabName) throws Exception {

        String resourcePath = KEYTAB_RESOURCE_BASE  + "/" + keyTabName;

        if (logger.isTraceEnabled())
            logger.trace("Loading resource : " + resourcePath);

        ClassLoader cl = new OsgiBundleClassLoader(bundleContext.getBundle());
        InputStream in = cl.getResourceAsStream(resourcePath);
        if (in == null) {
            logger.error("Resource load faile for : " + resourcePath);
            throw new Exception("No keytab found as resource " + resourcePath);
        }

        return in;
    }

}
