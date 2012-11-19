package org.atricore.idbus.kernel.common.support.pki;

import java.security.KeyStore;
import java.util.Collection;

/**
 *
 */
public interface KeyStoreManager {

    void init() throws PKIException;

    Collection<KeyStoreDefinition> listTrustStores();

    KeyStoreDefinition registerStore(String id, String description, String location, String passphrase, String type) throws PKIException;

    KeyStore loadStore(KeyStoreDefinition def) throws PKIException;


}
