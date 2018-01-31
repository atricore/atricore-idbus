package org.atricore.idbus.capabilities.csca.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 10/24/12
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractX509CertificateValidator implements X509CertificateValidator {

    private static final Log log = LogFactory
            .getLog(AbstractX509CertificateValidator.class);

    protected String _url;
    protected String _trustStore;
    protected String _trustPassword;
    protected List<String> _caCertAliases;
    protected List<String> _trustAnchorCertAliases;

    private KeyStore _keystore;
    private Set<TrustAnchor> _trustAnchors;
    private List<X509Certificate> _caCerts;
    private boolean _initialized = false;

    /**
     * Initialize the keystore and trusted certificates.
     */
    public synchronized void initialize() {
        try {
            if (_initialized) {
                return;
            }
            if (_trustStore == null) {
                log.error("TrustStore is not set!");
                throw new RuntimeException("Can't initialize keystore!");
            }
            if (_trustAnchorCertAliases == null || _trustAnchorCertAliases.size() == 0) {
                log.error("Trust anchor certificate aliases are not set!");
                throw new RuntimeException("Trust anchor certificate aliases are not set!");
            }

            // load keystore
            _keystore = KeyStore.getInstance("JKS");
            char[] trustPass = null;
            if (_trustPassword != null) {
                trustPass = _trustPassword.toCharArray();
            }
            _keystore.load(getClass().getResourceAsStream(_trustStore), trustPass);

            // load trust anchor certificates
            _trustAnchors = new HashSet<TrustAnchor>();
            for (String trustAnchorCertAlias : _trustAnchorCertAliases) {
                Certificate certificate = _keystore.getCertificate(trustAnchorCertAlias);
                if (certificate != null && certificate instanceof X509Certificate) {
                    TrustAnchor ta = new TrustAnchor((X509Certificate)certificate, null);
                    _trustAnchors.add(ta);
                }
            }

            // load intermediate CA certificates
            _caCerts = new ArrayList<X509Certificate>();
            if (_caCertAliases != null && _caCertAliases.size() > 0) {
                for (String caCertAlias : _caCertAliases) {
                    Certificate certificate = _keystore.getCertificate(caCertAlias);
                    if (certificate != null && certificate instanceof X509Certificate) {
                        _caCerts.add((X509Certificate)certificate);
                    }
                }
            }

            _initialized = true;

        } catch (Exception e) {
            log.error(e, e);
            throw new RuntimeException("Can't initialize keystore : " + e.getMessage(), e);
        }
    }

    /**
     * Generates certificate path from supplied client certificate
     * and CA certificates.
     *
     * @param clientCertificate client certificate
     * @return certificate path
     * @throws java.security.cert.CertificateException
     */
    protected CertPath generateCertificatePath(X509Certificate clientCertificate)
            throws CertificateException {
        if (!_initialized) {
            initialize();
        }
        List<X509Certificate> certs = new ArrayList<X509Certificate>();
        certs.add(clientCertificate);
        certs.addAll(_caCerts);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        return cf.generateCertPath(certs);
    }

    /**
     * Generates trust anchors.
     *
     * @return trust anchors
     * @throws CertificateException
     */
    protected Set<TrustAnchor> generateTrustAnchors() throws CertificateException {
        if (!_initialized) {
            initialize();
        }
        return _trustAnchors;
    }

    /**
     * Gets certificate from keystore.
     *
     * @param alias alias
     * @return certificate or null
     * @throws CertificateException
     */
    protected X509Certificate getCertificate(String alias) throws CertificateException {
        if (alias == null) {
            return null;
        }
        if (!_initialized) {
            initialize();
        }
        try {
            return (X509Certificate) _keystore.getCertificate(alias);
        } catch (KeyStoreException e) {
            log.error(e, e);
            throw new RuntimeException("Error getting certificate from keystore : " + e.getMessage(), e);
        }
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return _url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        _url = url;
    }

    /**
     * @return the trustStore
     */
    public String getTrustStore() {
        return _trustStore;
    }

    /**
     * @param trustStore the trustStore to set
     */
    public void setTrustStore(String trustStore) {
        _trustStore = trustStore;
    }

    /**
     * @return the trustPassword
     */
    public String getTrustPassword() {
        return _trustPassword;
    }

    /**
     * @param trustPassword the trustPassword to set
     */
    public void setTrustPassword(String trustPassword) {
        _trustPassword = trustPassword;
    }

    /**
     * @return the trustAnchorCertAliases
     */
    public List<String> getTrustAnchorCertAliases() {
        return _trustAnchorCertAliases;
    }

    /**
     * @param trustAnchorCertAliases the trustAnchorCertAliases to set
     */
    public void setTrustAnchorCertAliases(List<String> trustAnchorCertAliases) {
        _trustAnchorCertAliases = trustAnchorCertAliases;
    }

    /**
     * @return the caCertAliases
     */
    public List<String> getCaCertAliases() {
        return _caCertAliases;
    }

    /**
     * @param caCertAliases the caCertAliases to set
     */
    public void setCaCertAliases(List<String> caCertAliases) {
        _caCertAliases = caCertAliases;
    }

    public boolean isInitialized() {
        return this._initialized;
    }
}
