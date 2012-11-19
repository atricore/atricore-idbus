package org.atricore.idbus.kernel.common.support.pki;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * File based keystore manager
 */
public class KeyStoreManagerImpl implements KeyStoreManager {

    private List<KeyStoreDefinition> keyStores = new ArrayList<KeyStoreDefinition>();

    private boolean initWithErrors = false;

    private static final Log logger = LogFactory.getLog(KeyStoreManagerImpl.class);

    public void init() throws PKIException {

        boolean err = false;
        for (KeyStoreDefinition ts: keyStores) {
            try {
                validateStoreDefinition(ts);
            } catch (PKIException e) {
                logger.error(e.getMessage(), e);
                err = true;
            }
        }

        // Do we have errors ?
        if (err && !initWithErrors)
            throw new PKIException("Errors while initializing , check log file for details.");
    }

    public Collection<KeyStoreDefinition> listTrustStores() {
        return keyStores;
    }

    public KeyStoreDefinition registerStore(String id, String description, String location, String passphrase, String type) throws PKIException {
        KeyStoreDefinition newTs = new KeyStoreDefinition(id, description, location, passphrase, type);
        validateStoreDefinition(newTs);
        keyStores.add(newTs);
        return newTs;
    }

    public KeyStore loadStore(KeyStoreDefinition def) throws PKIException {

        // For now, all stores are files
        InputStream in = null;
        try {
            logger.debug("Loading KeyStore " + def.getLocation() + "...");

            in = new FileInputStream(def.getLocation());
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, def.getPassword().toCharArray());

            return ks;

        } catch (FileNotFoundException e) {
            throw new PKIException(e.getMessage(), e);
        } catch (CertificateException e) {
            throw new PKIException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            throw new PKIException(e.getMessage(), e);
        } catch (KeyStoreException e) {
            throw new PKIException(e.getMessage(), e);
        } catch (IOException e) {
            throw new PKIException(e.getMessage(), e);
        } finally {
            if (in != null) try { in.close() ; } catch (IOException ignored) {/**/}
        }
    }

    // -------------------------- Properties (spring friendly)

    public List<KeyStoreDefinition> getKeyStores() {
        return keyStores;
    }

    public void setKeyStores(List<KeyStoreDefinition> keyStores) {
        this.keyStores = keyStores;
    }

    public boolean isInitWithErrors() {
        return initWithErrors;
    }

    public void setInitWithErrors(boolean initWithErrors) {
        this.initWithErrors = initWithErrors;
    }

    // --------------------------- Utilities

    protected void validateStoreDefinition(KeyStoreDefinition def) throws PKIException {

        if (def.getId() == null)
            throw new PKIException("Key Store id cannot be null");

        if (def.getDescription() == null)
            throw new PKIException("Key Store description cannot be null ["+def.getId()+"]");

        if (def.getLocation() == null)
            throw new PKIException("Key Store location cannot be null ["+def.getId()+"]");

        if (def.getPassword() == null)
            throw new PKIException("Key Store passphrase cannot be null ["+def.getId()+"]");

        File f = new File(def.getLocation());
        if (!f.exists() || !f.canRead() || f.isDirectory()) {
            logger.error("Key Store location is invalid [exists:"+  f.exists() +
                    ", can-read:"+ f.canRead() + ", is-folder:"+ f.isDirectory() + "] ["+def.getId()+"]");
            throw new InvalidTrustStoreLocation(def.getLocation());
        }
    }


}
